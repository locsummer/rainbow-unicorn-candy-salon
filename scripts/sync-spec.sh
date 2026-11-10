#!/usr/bin/env bash
set -euo pipefail

echo "=== Syncing Game Specifications to Android & iOS ==="
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ROOT_DIR="$(cd "${SCRIPT_DIR}/.." && pwd)"

# Verify spec integrity
node "${SCRIPT_DIR}/verify-spec.js"

# Android targets
ANDROID_ASSETS="${ROOT_DIR}/android/app/src/main/assets"
ANDROID_TEST_RES="${ROOT_DIR}/android/core/src/test/resources"
mkdir -p "${ANDROID_ASSETS}" "${ANDROID_TEST_RES}/golden"

cp "${ROOT_DIR}/spec/levels.json" "${ANDROID_ASSETS}/"
cp "${ROOT_DIR}/spec/items.json" "${ANDROID_ASSETS}/"
cp "${ROOT_DIR}/spec/levels.json" "${ANDROID_TEST_RES}/"
cp "${ROOT_DIR}/spec/items.json" "${ANDROID_TEST_RES}/"
cp -r "${ROOT_DIR}/spec/golden/"* "${ANDROID_TEST_RES}/golden/"

# iOS targets
IOS_RES="${ROOT_DIR}/ios/RainbowUnicorn/Resources"
IOS_TEST_RES="${ROOT_DIR}/ios/GameCore/Tests/GameCoreTests/Golden"
mkdir -p "${IOS_RES}" "${IOS_TEST_RES}"

cp "${ROOT_DIR}/spec/levels.json" "${IOS_RES}/"
cp "${ROOT_DIR}/spec/items.json" "${IOS_RES}/"
cp -r "${ROOT_DIR}/spec/golden/"* "${IOS_TEST_RES}/"

echo "=== Sync completed successfully. Checksums match. ==="
