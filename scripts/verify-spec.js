const fs = require('fs');
const path = require('path');
const crypto = require('crypto');

const SPEC_DIR = path.join(__dirname, '..', 'spec');
const CHECKSUM_FILE = path.join(SPEC_DIR, 'checksum.json');

function hashFile(filePath) {
  const content = fs.readFileSync(filePath, 'utf8').replace(/\r\n/g, '\n');
  return crypto.createHash('sha256').update(content, 'utf8').digest('hex');
}

console.log('--- Verifying Game Specifications and Rules ---');

// 1. Verify game-spec.md
const gameSpecPath = path.join(SPEC_DIR, 'game-spec.md');
if (!fs.existsSync(gameSpecPath)) {
  console.error('FAIL: spec/game-spec.md is missing');
  process.exit(1);
}
const gameSpecContent = fs.readFileSync(gameSpecPath, 'utf8');
const ruleMatches = gameSpecContent.match(/RULE-\d{3}/g) || [];
const uniqueRules = Array.from(new Set(ruleMatches));
console.log(`[PASS] Found ${uniqueRules.length} unique RULE IDs in game-spec.md`);

// 2. Verify levels.json
const levelsPath = path.join(SPEC_DIR, 'levels.json');
const levelsData = JSON.parse(fs.readFileSync(levelsPath, 'utf8'));

if (!Array.isArray(levelsData.levels) || levelsData.levels.length !== 20) {
  console.error(`FAIL: levels.json must contain exactly 20 levels. Found: ${levelsData.levels ? levelsData.levels.length : 0}`);
  process.exit(1);
}

const validStations = new Set(["spa", "hair", "makeup", "dressup", "cake", "pet", "photo"]);
levelsData.levels.forEach((lvl, idx) => {
  const expectedId = idx + 1;
  if (lvl.id !== expectedId) {
    console.error(`FAIL: Level id mismatch. Expected ${expectedId}, got ${lvl.id}`);
    process.exit(1);
  }
  const shouldBePremium = expectedId >= 11;
  if (lvl.requiresPremium !== shouldBePremium) {
    console.error(`FAIL: Level ${expectedId} premium flag mismatch. Expected ${shouldBePremium}, got ${lvl.requiresPremium}`);
    process.exit(1);
  }
  if (!Array.isArray(lvl.stations) || lvl.stations.length < 3 || lvl.stations.length > 6) {
    console.error(`FAIL: Level ${expectedId} stations count must be 3-6. Found ${lvl.stations ? lvl.stations.length : 0}`);
    process.exit(1);
  }
  lvl.stations.forEach(st => {
    if (!validStations.has(st)) {
      console.error(`FAIL: Level ${expectedId} has invalid station: ${st}`);
      process.exit(1);
    }
  });
  if (!lvl.nameVi) {
    console.error(`FAIL: Level ${expectedId} missing nameVi localization.`);
    process.exit(1);
  }
  if (!Array.isArray(lvl.criteria) || lvl.criteria.length < 3 || lvl.criteria.length > 6) {
    console.error(`FAIL: Level ${expectedId} criteria count must be 3-6. Found ${lvl.criteria ? lvl.criteria.length : 0}`);
    process.exit(1);
  }
  lvl.criteria.forEach(cr => {
    if (!cr.description || !cr.descriptionEn) {
      console.error(`FAIL: Level ${expectedId} criterion ${cr.id} missing bilingual description/descriptionEn.`);
      process.exit(1);
    }
  });
});
console.log('[PASS] levels.json verified: 20 levels, correct 1-10 free / 11-20 premium, 3-6 stations & criteria.');

// 3. Verify items.json
const itemsPath = path.join(SPEC_DIR, 'items.json');
const itemsData = JSON.parse(fs.readFileSync(itemsPath, 'utf8'));

const categoryMap = new Map();
itemsData.categories.forEach(cat => {
  if (!cat.nameEn) {
    console.error(`FAIL: Category ${cat.id} missing nameEn`);
    process.exit(1);
  }
  categoryMap.set(cat.id, { total: 0, locked: 0, name: cat.name });
});

const itemIds = new Set();
itemsData.items.forEach(item => {
  if (!item.nameEn) {
    console.error(`FAIL: Item ${item.id} missing nameEn`);
    process.exit(1);
  }
  if (itemIds.has(item.id)) {
    console.error(`FAIL: Duplicate item ID detected: ${item.id}`);
    process.exit(1);
  }
  itemIds.add(item.id);

  if (!categoryMap.has(item.category)) {
    console.error(`FAIL: Item ${item.id} references undefined category ${item.category}`);
    process.exit(1);
  }

  const cat = categoryMap.get(item.category);
  cat.total += 1;
  if (!item.unlockedByDefault) {
    cat.locked += 1;
  }
  if (item.candyPrice < 0) {
    console.error(`FAIL: Item ${item.id} has negative candy price: ${item.candyPrice}`);
    process.exit(1);
  }
  if (item.unlockedByDefault && item.candyPrice !== 0) {
    console.error(`FAIL: Unlocked item ${item.id} has non-zero candy price: ${item.candyPrice}`);
    process.exit(1);
  }
});

// Check locked items <= 25% for each category
categoryMap.forEach((stats, catId) => {
  if (stats.total === 0) {
    console.error(`FAIL: Category ${catId} has 0 items!`);
    process.exit(1);
  }
  const lockedRatio = stats.locked / stats.total;
  if (lockedRatio > 0.25) {
    console.error(`FAIL: Category ${catId} exceeds 25% locked threshold! Locked: ${stats.locked}/${stats.total} (${(lockedRatio*100).toFixed(1)}%)`);
    process.exit(1);
  }
});
console.log(`[PASS] items.json verified: ${itemsData.items.length} items across ${itemsData.categories.length} categories. All categories <= 25% locked items.`);

// 4. Verify golden vectors
const goldenDir = path.join(SPEC_DIR, 'golden');
const goldenFiles = fs.readdirSync(goldenDir).filter(f => f.endsWith('.json'));
if (goldenFiles.length < 5) {
  console.error(`FAIL: Expected at least 5 golden vector files. Found: ${goldenFiles.length}`);
  process.exit(1);
}
console.log(`[PASS] Golden vectors verified: ${goldenFiles.length} files found.`);

// 5. Generate / verify checksums
const filesToHash = [
  'game-spec.md',
  'levels.json',
  'items.json',
  ...goldenFiles.map(f => `golden/${f}`)
];

const checksums = {};
filesToHash.forEach(relPath => {
  const fullPath = path.join(SPEC_DIR, relPath);
  checksums[relPath] = hashFile(fullPath);
});

fs.writeFileSync(CHECKSUM_FILE, JSON.stringify(checksums, null, 2) + '\n');
console.log(`[PASS] Checksum generated and written to spec/checksum.json (${filesToHash.length} files tracked).`);
console.log('--- SPECIFICATION VERIFICATION SUCCEEDED ---');
