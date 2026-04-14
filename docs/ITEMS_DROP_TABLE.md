# ITEMS AND DROP TABLE

## Overview

This project now separates:

- Item stats: `core/src/main/resources/scripts/inventory_items.json`
- Drop rules: `core/src/main/resources/scripts/drop_tables.json`
- Enemy mapping to drop table: `core/src/main/resources/scripts/enemies.json` (`dropTableID`)

This keeps balancing centralized and prevents per-enemy stat drift.

## Item Stat Columns

Each item entry should define:

- `itemTypeID`: unique item id
- `itemAttributes`: bit flags (consumable/equippable/stackable)
- `itemUseType`: functional category (weapon, armor, potion, quest item)
- `itemUseTypeValue`: power value used in battle/UI
- `itemRarity`: rarity tier
- `itemValue`: store buy/sell base value

## Drop Table Columns

Each drop table entry defines:

- `dropTableID`: unique table id
- `drops[]`: list of drops
: each drop has
- `itemTypeID`: item id from item table
- `probability`: independent chance in range `[0.0, 1.0]`

## Enemy Mapping

Enemy config should use:

- `dropTableID`: id of the drop table used by this enemy

`BattleState` will first resolve drops from `dropTableID`. If absent, it falls back to legacy inline `drops` for backward compatibility.
