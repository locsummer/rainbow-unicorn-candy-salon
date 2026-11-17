# Gameplay Fix Report – Rainbow Unicorn Candy Salon
**Project:** Rainbow Unicorn Candy Salon (iOS & Android)  
**Date:** November 17, 2026  
**Scope:** Gameplay Flow, Station Logic, State Persistence, Undo/Redo Engine, Scoring & Criteria Evaluation

---

## 1. Executive Summary

This report documents the verification, bug fixes, and logic upgrades implemented across all 7 gameplay stations in Rainbow Unicorn Candy Salon on iOS and Android. Every interactive station now possesses robust state management, responsive touch actions, full undo/redo capabilities, non-destructive reset, and accurate customer criteria scoring.

---

## 2. Gameplay Flow by Station

### 2.1. Spa Bath Station (`StationType.SPA`)
* **Objective:** Clean the unicorn pony using the interactive sponge tool until the cleanliness score reaches at least 80%.
* **Interaction:** Drag the sponge over 8 dirt spots on the pony's face, neck, and body.
* **Logic & Guardrails:**
  - Real-time progress bar with percentage readout (`spa_clean_percent`).
  - Haptic and bubble audio effects (`SoundManager.playBubble`) on every spot cleaned.
  - Completion gate: If the user attempts to tap `Done` with cleanliness $< 80\%$, the game prevents progression and surfaces a helpful toast message (`spa_need_more`).
  - Cleanliness score correctly feeds into the level customer evaluation engine.

### 2.2. Hair Salon Station (`StationType.HAIR`)
* **Objective:** Style and color the pony's mane and tail to match the customer's request.
* **Features:**
  - **Hair Styles:** Twin Buns, High Ponytail, Silky Straight, Pop Waves, and French Braid.
  - **Color Dyes:** Rainbow multi-tone, pastel pink, mint green, galaxy purple, lavender, neon lime, plum, and magenta.
  - **Selective Coloring:** Dyes affect only hair locks and braid strands; color never bleeds into pony skin, eyes, or accessories.
  - **Undo/Redo:** Every hairstyle and dye step registers on the `LevelEngine` undo stack.
  - **Tap-to-Toggle:** Tapping an already-equipped hairstyle or hair dye safely unequips it back to the natural style.

### 2.3. Makeup Salon Station (`StationType.MAKEUP`)
* **Objective:** Apply eyeshadow, blush, lipstick, and face paint designs.
* **Features:**
  - **Eyeshadow:** Precise eyelid mask clipping ensuring colors stay neatly within orbital boundaries.
  - **Blush:** Dual-cheek soft circular airbrush pads.
  - **Lipstick:** Glossy colored lips with shine highlight.
  - **Face Paints:** Up to 3 simultaneous face paint motifs with FIFO replacement or tap-to-remove toggle.
  - **Makeup Remover:** Prominent button in Tier 3 Top HUD allows instant reset of all makeup without affecting hair or dress.

### 2.4. Dress Up & Accessories Station (`StationType.DRESSUP`)
* **Objective:** Equip dresses, wings, tiaras, horns, jewelry, and decorative stickers.
* **Logic & Rules:**
  - **Outfits:** Full dresses replacing basic garments cleanly.
  - **Accessories:** Wings, horns, tiaras, and necklaces each occupy dedicated non-conflicting slots.
  - **Sticker Management:** Enforces a maximum of 10 stickers (`LayerComposer.MAX_STICKERS`).
  - **Anchoring:** Stickers snap cleanly to 10 predefined anatomical body slots rather than floating randomly in the air.
  - **Tap-to-Unequip:** Tapping any equipped single-slot item or sticker immediately unequips it.

### 2.5. Cake Bakery Station (`StationType.CAKE`)
* **Objective:** Bake and decorate a custom candy salon cake with layers, frosting, and toppings.
* **Logic Upgrades:**
  - **Interactive Tier Selection:** 1 to 4 tiers with live canvas rendering.
  - **Frosting Flavor Selection:** Vanilla Rainbow, Strawberry Cream, Blue Cotton Candy, or Rich Chocolate with matching icing drip colors.
  - **Topping CRUD Controls:**
    - `[+ Add Topping]`: Opens the visual `ItemSelectionModal`.
    - `[Frosting / Đổi Kem Phủ]`: Opens the frosting flavor modal.
    - Removable topping chips with individual `[✕]` delete buttons.
    - `[Clear All / Xóa Hết]`: Clears all toppings with one tap.

### 2.6. Pet Care Station (`StationType.PET`)
* **Objective:** Feed, wash, and groom the pony's pet companion.
* **Interaction:** Three tactile buttons with status indicators and sparkles for feeding delicious treats, washing with soapy bubbles, and brushing fluffy fur until 100% satisfied.

### 2.7. Photo Studio Station (`StationType.PHOTO`)
* **Objective:** Capture the completed character in festive poses and backdrops.
* **Features:**
  - Backdrops: Pastel Studio, Disco Dancefloor, and Rainbow Forest.
  - Poses: Happy Smile, Star Jump, and Friendly Wave.
  - Shutter Effect: Realistic camera flash animation and shutter sound.
  - Output: Saves photo to device storage with platform-specific secure permissions (MediaStore API on Android, Add-Only Photo Library on iOS).

---

## 3. Core Engine, Undo/Redo & Save System

* **`LevelEngine` State Management:** Tracks `CharacterState`, `currentStationIndex`, `canUndo`, `canRedo`, and per-station output metrics (`StationOutput`).
* **Non-Destructive Reset:** Resetting a station restores default values for *only that station*, preserving progress in previously completed stations.
* **Auto-Save Checkpoints:** Every item equip, layer change, and station progression serializes to JSON via `buildCheckpoint()` and saves safely to local storage (`FileManager` / `DataStore`), enabling seamless resume upon relaunch.
