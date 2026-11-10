const fs = require('fs');

function parseCSV(text) {
  const lines = text.trim().split('\n');
  const rows = [];
  for (const line of lines) {
    if (!line.trim()) continue;
    let row = [''], inQuotes = false;
    for (let i = 0; i < line.length; i++) {
      let c = line[i], next = line[i+1];
      if (c === '"') {
        if (inQuotes && next === '"') { row[row.length - 1] += '"'; i++; }
        else { inQuotes = !inQuotes; }
      } else if (c === ',' && !inQuotes) {
        row.push('');
      } else {
        row[row.length - 1] += c;
      }
    }
    rows.push(row);
  }
  return rows;
}

const rows = parseCSV(fs.readFileSync('docs/TEST_CASES.csv', 'utf8'));
console.log('Total rows:', rows.length);
console.log('Header:', rows[0]);

const groups = {};
const statuses = {};
const platforms = {};
const priorities = {};

for (let i = 1; i < rows.length; i++) {
  const r = rows[i];
  const grp = r[1];
  const plat = r[2];
  const prio = r[7];
  const st = r[10];

  groups[grp] = (groups[grp] || 0) + 1;
  statuses[st] = (statuses[st] || 0) + 1;
  platforms[plat] = (platforms[plat] || 0) + 1;
  priorities[prio] = (priorities[prio] || 0) + 1;
}

console.log('Group counts (18 groups):');
for (const [k, v] of Object.entries(groups)) {
  console.log(`  ${k}: ${v}`);
}

console.log('\nStatuses:');
for (const [k, v] of Object.entries(statuses)) {
  console.log(`  ${k}: ${v}`);
}

console.log('\nPlatforms:');
for (const [k, v] of Object.entries(platforms)) {
  console.log(`  ${k}: ${v}`);
}

console.log('\nPriorities:');
for (const [k, v] of Object.entries(priorities)) {
  console.log(`  ${k}: ${v}`);
}
