# KIẾN TRÚC HỆ THỐNG - ECHOES OF MANAFALL

## Tổng quan

Game RPG 2D theo lượt, xây dựng trên **LibGDX 1.14.0**, Java 21, Gradle 9.4.1.

```
GdxGame (Game)
├── MenuScreen
├── CharacterSelectionScreen
├── GameScreen (gameplay chính)
│   ├── MapManager (quản lý map Tiled)
│   ├── Entity (player, NPC, enemy)
│   └── PlayerHUD (inventory, status, quest)
├── BattleScreen (chiến đấu theo lượt)
├── OptionScreen (cài đặt)
└── GameOverScreen
```

---

## Cấu trúc Module

```
ECHOES_OF_MANAFALL/
├── core/          ← Toàn bộ game logic
│   ├── src/main/java/com/gdx/game/
│   ├── src/test/java/com/gdx/game/
│   └── src/main/resources/        ← Assets, maps, configs
├── desktop/       ← Desktop launcher (LWJGL3)
├── docs/          ← Tài liệu dự án
└── gradle/        ← Gradle wrapper + version catalog
```

---

## Design Patterns sử dụng

### 1. Component-Based Architecture (Entity System)
Mỗi Entity gồm 3 component có thể hoán đổi:

```
Entity
├── InputComponent      (xử lý input: keyboard hoặc AI hoặc network)
├── PhysicsComponent    (va chạm, di chuyển)
└── GraphicsComponent   (render sprite, animation)
```

**Files:**
- `component/Component.java` - Interface gốc
- `entities/Entity.java` - Entity chứa 3 components
- `entities/player/Player*Component.java` - Components cho player
- `entities/npc/NPC*Component.java` - Components cho NPC

### 2. Observer Pattern (Event System)
Dùng xuyên suốt để giao tiếp giữa các hệ thống:

| Observer | Subject | Mục đích |
|----------|---------|----------|
| BattleObserver | BattleSubject | Events trong battle (damage, turn) |
| InventoryObserver | InventorySubject | Thay đổi inventory (equip, use) |
| StatusObserver | StatusSubject | Cập nhật HP/MP/XP |
| ProfileObserver | ProfileSubject | Save/Load game |
| AudioObserver | AudioSubject | Play music/sound |
| ComponentObserver | ComponentSubject | Entity state changes |
| ConversationGraphObserver | ConversationGraphSubject | Dialog events |
| ClassObserver | ClassSubject | Class upgrade events |
| InventorySlotObserver | InventorySlotSubject | Slot-level item changes |
| StoreInventoryObserver | StoreInventorySubject | NPC shop events |

### 3. Factory Pattern
- `EntityFactory` - Tạo player, NPC, enemy từ JSON config
- `InventoryItemFactory` - Tạo items từ JSON config
- `MapFactory` - Tạo maps từ TMX files
- `EquipmentSetFactory` - Tạo equipment sets

### 4. Singleton Pattern
- `ProfileManager` - Quản lý save/load
- `ResourceManager` - Quản lý assets
- `AnimationManager` - Quản lý animations
- `EntityFactory` / `InventoryItemFactory` - Factories

### 5. State Machine
- `GameScreen.GameState`: SAVING, LOADING, RUNNING, PAUSED, GAME_OVER
- `Entity.State`: IDLE, WALKING, IMMOBILE
- `Entity.Direction`: UP, DOWN, LEFT, RIGHT

---

## Hệ thống chính

### Battle System (Turn-based)
```
BattleScreen → BattleState (logic) + BattleHUD (UI)
                  ├── Tính turn order theo speed ratio
                  ├── Player attack / magic / item
                  ├── Enemy attack (AI đơn giản)
                  └── Drop items khi thắng
```
**Files:** `battle/BattleState.java`, `battle/BattleHUD.java`, `battle/BattleUI.java`

### Inventory System (Drag & Drop)
```
InventoryUI
├── InventorySlot[] (inventory grid)
├── EquipSlot[] (helmet, armor, shield, boots, weapon, wand)
├── Drag & Drop (InventorySlotSource → InventorySlotTarget)
└── EquipmentSet (set bonus khi đủ bộ)
```
**Files:** `inventory/InventoryUI.java`, `inventory/slot/InventorySlot.java`

### Quest System
```
QuestGraph
├── QuestTask[] (Fetch, Kill, Delivery, Guard, Escort, Return, Discover)
├── QuestTaskDependency (task phụ thuộc nhau)
└── Rewards (XP, GP)
```
**Files:** `quest/QuestGraph.java`, `quest/QuestTask.java`

### Character Class Tree (Binary Tree)
```
Warrior → Knight / Gladiator
            ├── Paladin / Warlord
            └── Duelist / WeaponMaster
```
**Files:** `entities/player/characterclass/tree/Tree.java`, `entities/player/characterclass/tree/Node.java`

### Save/Load System
```
ProfileManager (Singleton)
├── ObjectMap<String, Object> properties (key-value store)
├── Serialize → JSON → Base64 encode → .sav file
└── Notify ProfileObservers on save/load events
```
**File:** `profile/ProfileManager.java`

---

## Assets & Resources

```
core/src/main/resources/
├── asset/
│   ├── map/          ← Tiled maps (.tmx) + tilesets (.tsx, .png)
│   ├── background/   ← Battle backgrounds
│   ├── data/         ← UI skin (uiskin.json)
│   └── tool/         ← Cursor
├── sprites/
│   ├── characters/   ← Character sprite sheets
│   └── items/        ← Item icons
├── skins/            ← UI themes (statusui, items)
├── classes/          ← Class tree JSON configs
├── conversations/    ← Dialog JSON files
├── entities/hero/    ← Player entity configs
├── quests/           ← Quest JSON definitions
├── scripts/          ← Quest task scripts
├── music/            ← MP3 background music
└── fonts/            ← Font files
```

---

## Test Infrastructure

- **Framework:** JUnit 5 + Mockito + AssertJ
- **GdxRunner:** Custom JUnit extension chạy tests trong LibGDX headless context
- **Coverage:** 42 test files bao phủ hầu hết packages
- **Test types:** Unit tests + Integration tests (tách bằng Gradle tasks)

```bash
./gradlew unitTest          # Chỉ unit tests
./gradlew integrationTest   # Chỉ integration tests
./gradlew test              # Tất cả
./gradlew desktop:run       # Chạy game
```
