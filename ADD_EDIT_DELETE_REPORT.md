# Add, Edit, Delete (CRUD) Implementation Report
**Project:** Rainbow Unicorn Candy Salon (iOS & Android)  
**Date:** November 17, 2026  
**Scope:** Interactive CRUD Operations across Stations, Visual Modals, Removable Chips, Undo/Redo Integration

---

## 1. Executive Summary

This report documents the implementation and verification of interactive Add, Edit, and Delete (CRUD) operations across all gameplay stations and screens. In response to user feedback regarding missing or non-functional modification controls, full CRUD capabilities have been integrated with tactile button feedback, dedicated modals, removable topping chips, and tap-to-unequip toggles.

---

## 2. Station-by-Station CRUD Matrix

| Station | Add (Thêm) | Edit / Modify (Sửa) | Delete / Remove (Xóa) |
| :--- | :--- | :--- | :--- |
| **Cake Bakery** | `[+ Add Topping]` button opens visual modal | `[Frosting]` modal + 1–4 tier buttons | `[✕]` chip delete on each topping + `[Clear All]` |
| **Hair Salon** | Tap hairstyle or color swatch | Switch hairstyle/color or adjust cut | Tap equipped style to unequip + tray `Clear` |
| **Makeup Salon** | Select eyeshadow, lipstick, blush, paint | Switch shades or mix colors | Tap to unequip + `[Makeup Remover]` button + tray `Clear` |
| **Dress Up** | Select outfit, wings, horn, tiara, jewelry, sticker | Swap outfits or accessories | Tap to unequip + sticker tap-to-remove + tray `Clear` |
| **Spa Bath** | Apply soap bubbles / sponge | Drag sponge over dirt spots | Clean away spots (progressively removed) |
| **Pet Care** | Feed treat, wash pet, groom fur | Re-wash or groom companion | Reset pet care state via station `Reset` |
| **Photo Studio** | `[Take Photo]` captures snapshot | Switch backdrops & poses | Delete photo from Gallery screen |

---

## 3. Deep Dive: Cake Bakery CRUD Workflow

The Cake Bakery station serves as the primary benchmark for interactive CRUD operations:

### 3.1. Add Topping (`Create / Add`)
* **Trigger:** Primary pink button `[+ Add Topping]` (`btn_add_topping`) in the cake station, or the new **"✨ Mở Bảng Chọn"** button in the tray.
* **Interaction:** Opens the 3-column `ItemSelectionModal` displaying all cake toppings with full-scale 56–64dp previews, checkmarks for equipped items, and locked pricing tags:
  - Sugar Star (`cake_top_sugar_star`)
  - Chocolate Swirl (`cake_top_choco_curl`)
  - Cherries on Stem (`cake_top_cherry`)
  - Macaron (`cake_top_macaron`)
  - Rainbow Sprinkles (`cake_top_rainbow_sprinkles`)
  - Crystal Diamond Candy (`cake_top_crystal_gem`)
* **Feedback & Live Stage Rendering:** Tapping any topping appends it to `cakeToppings` (up to 8 items), plays sparkle audio, updates the live cake canvas instantly across all tiers (top tier and lower tier ledges with illustrated cherries, chocolate kisses, wafer sticks, and macarons), and persists to the checkpoint.

### 3.2. Edit Frosting & Layers (`Update / Edit`)
* **Frosting Flavor:** Purple button `[Frosting / Đổi Kem Phủ]` (`btn_change_flavor`) opens the flavor selection modal to swap between Vanilla Rainbow, Strawberry Cream, Blue Cotton Candy, or Rich Chocolate. The cake icing drip on canvas changes color in real-time.
* **Tiers / Layers:** Tapping numbers `1`, `2`, `3`, or `4` instantly re-renders the cake height on canvas, with touch targets exceeding 44dp.

### 3.3. Delete Topping (`Delete / Remove`)
* **Individual Chip Deletion:** Every topping on the cake appears as a removable pill chip directly beneath the action buttons. Each chip features an explicit `[✕]` delete button. Tapping `[✕]` removes only that specific topping.
* **Bulk Deletion:** A yellow `[Clear All / Xóa Hết]` (`btn_clear_all_toppings`) button clears all toppings from the cake with a single tap.

---

## 4. Universal Tap-to-Unequip Toggle (Single-Slot Items)

In previous versions, once an outfit, hairstyle, tiara, or jewelry item was equipped, tapping it again did nothing. In the redesigned system:
* Tapping an already-equipped single-slot item (e.g. `hair_styles`, `hair_colors`, `eyeshadows`, `lipsticks`, `blushes`, `outfits`, `wings`, `horns`, `tiaras`, `jewelry`, or `cake_flavors`) **toggles it off (unequips)**, setting the slot to `null`.
* Tapping an equipped sticker or face paint immediately removes that specific sticker or face paint from the character.
* This provides instant, intuitive edit/delete functionality without needing to navigate to a separate clear button.

---

## 5. Verification & Code Alignment

* **Android Implementation:** Handled in `CakeStationView.kt`, `StationScreen.kt`, and `CommonComponents.kt`.
* **iOS Implementation:** Handled in `StationViews.swift`, `Screens.swift`, and `BottomItemTray.swift`.
* **Safety:** All CRUD operations are integrated with the `UndoStack` on both platforms, allowing players to reverse any accidental deletion with the `Undo` button.
