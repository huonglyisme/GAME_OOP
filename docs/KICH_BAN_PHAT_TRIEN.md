# KỊCH BẢN PHÁT TRIỂN & FIX BUGS - ECHOES OF MANAFALL

## Context
**Tên đề tài:** "Xây dựng game nhập vai 2D theo lượt Echoes of Manafall và mở rộng chế độ co-op qua mạng LAN"

**Mô tả:** Đề tài xây dựng một game nhập vai 2D theo lượt trên nền tảng LibGDX với các chức năng chính như di chuyển nhân vật, tương tác NPC, nhận nhiệm vụ, chiến đấu theo lượt, quản lý vật phẩm và lưu/tải dữ liệu. Đồng thời, đề tài định hướng mở rộng chế độ co-op qua mạng LAN để nhiều người chơi có thể cùng tham gia và phối hợp trong game.

**Hiện trạng dự án:**
- 95 Java source files, 41 test files, kiến trúc Component-Based + Observer Pattern
- 5 bugs đã xác định (inventory stacking, FadeIn, drag offset, HUD spacing, exit position)
- Chưa có bất kỳ networking code nào (0% LAN co-op)
- Battle system chỉ hỗ trợ 1v1, chưa có multi-player

---

## CẤU TRÚC NHÓM (5 NGƯỜI)

| STT | Vai trò | Tên gọi | Phụ trách chính |
|-----|---------|---------|-----------------|
| 1 | **Leader / Giám sát** | Leader | Review code, quản lý tiến độ, kiến trúc hệ thống, integration test |
| 2 | **Dev 1** | Dev-Gameplay | Phát triển tính năng gameplay mới + LAN co-op (server/client) |
| 3 | **Dev 2** | Dev-UI/Audio | Phát triển UI, Audio, Map, Transition effects |
| 4 | **Bug Fixer 1** | Fixer-Inventory | Fix bugs liên quan Inventory & Status UI |
| 5 | **Bug Fixer 2** | Fixer-System | Fix bugs liên quan Transition, Physics, Save/Load |

---

## GIAI ĐOẠN 1: FIX BUGS & ỔN ĐỊNH HỆ THỐNG (Sprint 1-2, ~2 tuần)

### Fixer-Inventory: 3 bugs

#### Bug 1: Items stacking sai loại (Critical)
- **File:** `core/src/main/java/com/gdx/game/inventory/slot/InventorySlot.java` (line 54-77, 104-116)
- **Nguyên nhân:** Method `add()` gọi `incrementItemCount()` mà KHÔNG kiểm tra `isStackable()` hoặc `isSameItemType()`
- **Cách fix:**
  1. Trong `InventorySlot.add(Actor actor)` (line 54): thêm kiểm tra type trước khi increment
  2. Trong `InventorySlot.add(Array<Actor> array)` (line 104): thêm validation tương tự
  3. So sánh với logic đúng đã có ở `swapSlots()` (line 219-232) - nơi đã check `sourceActor.isSameItemType(targetActor) && sourceActor.isStackable()`
- **Test:** Cập nhật `core/src/test/java/com/gdx/game/inventory/slot/InventorySlotTest.java`
  - Thêm test case: add 2 items khác ItemTypeID vào cùng slot -> verify không stack
  - Thêm test case: add 2 items cùng type nhưng `isStackable() = false` -> verify không stack

#### Bug 2: Drag & drop offset (High)
- **File:** `core/src/main/java/com/gdx/game/inventory/slot/InventorySlotSource.java` (line 43)
- **Nguyên nhân:** `dragAndDrop.setDragActorPosition(-x, -y + getActor().getHeight())` không tính đúng tọa độ
- **Cách fix:**
  1. Sửa offset thành: `dragAndDrop.setDragActorPosition(getActor().getWidth()/2, -getActor().getHeight()/2)` hoặc tính từ center của actor
  2. Test thủ công bằng cách kéo thả item trong inventory
- **Test:** Cập nhật `core/src/test/java/com/gdx/game/inventory/InventoryUITest.java`

#### Bug 3: Status HUD spacing (Medium)
- **File:** `core/src/main/java/com/gdx/game/status/StatusUI.java` (line 83-105 `handleHpBar()`, line 107-129 `handleMpBar()`)
- **Nguyên nhân:** `hpValLabel` và `mpValLabel` không có fixed width -> khi giá trị thay đổi số chữ số (100->99), label co lại gây blank space
- **Cách fix:**
  1. Thêm `.width(30).align(Align.right)` cho `hpValLabel` (line 101) và `mpValLabel`
  2. Thêm `.padRight(2)` trước separator "/"
- **Test:** Cập nhật `core/src/test/java/com/gdx/game/status/StatusUITest.java`

---

### Fixer-System: 2 bugs + cải thiện

#### Bug 4: FadeIn effect broken (High)
- **Files:**
  - `core/src/main/java/com/gdx/game/screen/transition/effects/FadeInTransitionEffect.java` (line 18-27)
  - `core/src/main/java/com/gdx/game/screen/MenuNewGameScreen.java` (line 99, 135)
  - `core/src/main/java/com/gdx/game/screen/MenuLoadGameScreen.java` (line 95)
- **Nguyên nhân:**
  1. `next.show()` bị comment out ở line 19 -> screen tiếp theo không được init
  2. Alpha calc `1f - getAlpha()` có thể bị ngược
- **Cách fix:**
  1. Uncomment `next.show()` ở FadeInTransitionEffect.java line 19
  2. So sánh logic alpha với `FadeOutTransitionEffect.java` để đảm bảo nhất quán
  3. Bỏ comment ở MenuNewGameScreen.java (line 99, 135) và MenuLoadGameScreen.java (line 95) để enable lại FadeIn
  4. Test chuyển cảnh menu -> game nhiều lần
- **Test:** Cập nhật `core/src/test/java/com/gdx/game/screen/transition/TimeTransitionTest.java`

#### Bug 5: Exit position reset khi đóng bằng nút X (Medium)
- **File:** `core/src/main/java/com/gdx/game/map/MapManager.java` (line 59-68)
- **Nguyên nhân:** Line 66 check `Gdx.input.isKeyPressed(Input.Keys.ESCAPE)` - chỉ hoạt động khi user nhấn ESC, nút X không trigger điều kiện này
- **Cách fix:**
  1. Thay đổi logic: không dùng key check, thay bằng flag từ GdxGame hoặc bỏ điều kiện ESCAPE
  2. Option A: Trong `GdxGame.java`, override `dispose()` -> set một flag "isExiting" trước khi save profile
  3. Option B (đơn giản hơn): Luôn reset position khi save, bất kể cách thoát. Loại bỏ điều kiện `if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE))` ở line 66
- **Test:** Cập nhật `core/src/test/java/com/gdx/game/profile/ProfileManagerTest.java`

#### Cải thiện thêm: Z-Sorting (nếu còn thời gian)
- **Files:** `PlayerGraphicsComponent.java`, `NPCGraphicsComponent.java`
- Implement sprite layering dựa trên Y position

---

### Dev-Gameplay (Sprint 1-2): Nghiên cứu & chuẩn bị LAN

Trong khi Fixers sửa bugs, Dev-Gameplay:
1. **Nghiên cứu KryoNet** (thư viện networking phổ biến cho libGDX)
2. **Thiết kế kiến trúc mạng:**
   - Server/Client model
   - Game state serialization protocol
   - Turn sync mechanism
3. **Prototype NetworkManager** singleton (tương tự pattern của `ProfileManager.java`)
4. **Thêm dependency KryoNet** vào `gradle/libs.versions.toml` và `build.gradle.kts`

### Dev-UI/Audio (Sprint 1-2): Sound system + Content

1. **Implement Sound Effects** trong `AudioManager.java` (hiện chỉ có music)
2. **Thêm sound settings** vào `OptionScreen.java`
3. **Mở rộng AudioObserver** để hỗ trợ sound events (battle hit, item pickup, quest complete)

### Leader (Sprint 1-2):
1. Setup branch strategy: `main` <- `develop` <- feature/bugfix branches
2. Review tất cả bug fix PRs
3. Viết integration tests verify bugs đã fix
4. Chuẩn bị CI/CD (GitHub Actions đã có sẵn)

---

## GIAI ĐOẠN 2: PHÁT TRIỂN TÍNH NĂNG MỚI (Sprint 3-5, ~3 tuần)

### Dev-Gameplay: LAN Co-op Core

#### Sprint 3: Network Foundation
- **Tạo package mới:** `core/src/main/java/com/gdx/game/network/`
- **Files cần tạo:**
  1. `NetworkManager.java` - Singleton quản lý kết nối
  2. `GameServer.java` - Host game, lắng nghe connections
  3. `GameClient.java` - Join game, kết nối đến host
  4. `NetworkProtocol.java` - Định nghĩa message types (PLAYER_MOVE, PLAYER_ATTACK, SYNC_STATE, etc.)
  5. `NetworkObserver.java` + `NetworkSubject.java` - Observer pattern (theo codebase pattern)
- **Tích hợp vào MenuScreen:**
  - Thêm button "Host Game" và "Join Game" vào `MenuScreen.java`
  - Tạo `LobbyScreen.java` - màn hình chờ kết nối

#### Sprint 4: Player Sync
- **Tạo `RemotePlayerInputComponent.java`** trong `entities/player/`:
  - Nhận input từ network thay vì keyboard
  - Reuse `PlayerGraphicsComponent` và `PlayerPhysicsComponent` (component architecture cho phép)
- **Đồng bộ vị trí player:**
  - Host gửi position updates cho client
  - Client gửi input cho host
  - Host là authoritative (chống cheat)
- **Sửa `GameScreen.java`:**
  - Render cả player 2 trên map
  - Cả 2 player dùng chung `MapManager`
- **Sửa `Entity.java` / `EntityFactory.java`:**
  - Thêm `EntityType.REMOTE_PLAYER`
  - Factory tạo entity với `RemotePlayerInputComponent`

#### Sprint 5: Co-op Battle
- **Sửa `BattleState.java`:**
  - Hỗ trợ 2 players trong battle (2v1 hoặc 2v2)
  - Turn order: Player1 -> Player2 -> Enemy (dựa trên speed)
  - Sync attack calculations qua network
- **Sửa `BattleHUD.java` / `BattleUI.java`:**
  - Hiển thị stats cả 2 players
  - Cho phép player 2 chọn attack/item
- **Sửa `BattleScreen.java`:**
  - Nhận battle commands từ remote player

---

### Dev-UI/Audio: UI mở rộng + Map content

#### Sprint 3: Transition Effects + Credits
- Thêm transition effects mới vào `screen/transition/effects/`:
  - `SlideTransitionEffect.java`
  - `WipeTransitionEffect.java`
- Tạo `CreditsScreen.java`

#### Sprint 4: Map & Quest Content
- Thêm map mới (ít nhất 1 map) bằng Tiled editor
  - Tạo file `.tmx` mới trong `core/src/main/resources/asset/map/`
  - Tạo class Map tương ứng trong `map/worldMap/`
  - Đăng ký trong `MapFactory.java`
- Thêm NPC mới với conversations
- Thêm ít nhất 1 quest mới (quest004.json)

#### Sprint 5: Co-op UI
- Tạo `LobbyScreen.java` - UI cho host/join game
- Thêm chat UI đơn giản cho co-op mode
- Hiển thị player 2 info trên HUD

---

### Fixer-Inventory (sau Sprint 2): Chuyển sang hỗ trợ Dev

#### Sprint 3-4: Inventory cho Co-op
- Mỗi player có inventory riêng biệt
- Thêm tính năng trade items giữa 2 players
- Tạo `TradeUI.java` (dựa trên pattern `StoreInventoryUI.java`)

#### Sprint 5: Testing & Polish
- Viết unit tests cho tất cả tính năng mới liên quan inventory
- Test edge cases: trade khi inventory đầy, trade quest items, etc.

---

### Fixer-System (sau Sprint 2): Chuyển sang hỗ trợ Dev

#### Sprint 3-4: Save/Load cho Co-op
- Mở rộng `ProfileManager.java` để lưu co-op session
- Thêm properties: player2 position, player2 inventory, player2 stats
- Handle disconnect/reconnect: save state khi player disconnect

#### Sprint 5: Testing & Polish
- Viết integration tests cho network save/load
- Test edge cases: save khi đang battle, reconnect mid-game

---

### Leader (Sprint 3-5):
- Review tất cả feature PRs
- Viết `BonusCalculationIntegrationTest.java` mở rộng cho co-op
- Tạo integration test cho network: connect/disconnect/reconnect
- Performance testing: đảm bảo game smooth với 2 players
- Quản lý merge conflicts giữa các branches

---

## GIAI ĐOẠN 3: TÍCH HỢP & KIỂM THỬ (Sprint 6-7, ~2 tuần)

### Toàn nhóm:

#### Sprint 6: Integration
- Merge tất cả branches vào develop
- Fix conflicts và regression bugs
- End-to-end testing co-op mode trên LAN thực tế
- Leader viết comprehensive integration tests

#### Sprint 7: Polish & Demo
- Fix remaining bugs từ integration testing
- Optimize performance (network latency, rendering)
- Chuẩn bị demo: quay video gameplay co-op
- Viết documentation: README cập nhật, hướng dẫn chơi co-op
- Build release version

---

## BẢNG TỔNG HỢP PHÂN CÔNG THEO SPRINT

| Sprint | Leader | Dev-Gameplay | Dev-UI/Audio | Fixer-Inventory | Fixer-System |
|--------|--------|-------------|-------------|-----------------|--------------|
| **1** | Setup branches, review | Nghiên cứu KryoNet | Sound effects impl | Bug 1: Item stacking | Bug 4: FadeIn effect |
| **2** | Review bug fixes, integration test | Thiết kế network arch | Sound settings UI | Bug 2: Drag offset | Bug 5: Exit position |
| **3** | Review features | Network foundation | Transition effects mới | Bug 3: HUD spacing + Co-op inventory | Z-sorting + Co-op save |
| **4** | Review, perf test | Player sync trên LAN | Map & quest mới | Trade UI giữa players | Disconnect handling |
| **5** | Integration test co-op | Co-op battle system | Co-op UI (lobby, chat) | Testing inventory co-op | Testing save/load co-op |
| **6** | Merge & integration | Fix network bugs | Fix UI bugs | Fix inventory bugs | Fix system bugs |
| **7** | Final review & release | Demo prep | Documentation | Testing | Testing |

---

## FILES QUAN TRỌNG CẦN TẠO MỚI (cho LAN Co-op)

```
core/src/main/java/com/gdx/game/network/
├── NetworkManager.java          (Dev-Gameplay)
├── GameServer.java              (Dev-Gameplay)
├── GameClient.java              (Dev-Gameplay)
├── NetworkProtocol.java         (Dev-Gameplay)
├── NetworkObserver.java         (Dev-Gameplay)
├── NetworkSubject.java          (Dev-Gameplay)
└── packets/
    ├── PlayerMovePacket.java    (Dev-Gameplay)
    ├── BattleActionPacket.java  (Dev-Gameplay)
    ├── SyncStatePacket.java     (Dev-Gameplay)
    └── TradePacket.java         (Fixer-Inventory)

core/src/main/java/com/gdx/game/entities/player/
└── RemotePlayerInputComponent.java  (Dev-Gameplay)

core/src/main/java/com/gdx/game/screen/
├── LobbyScreen.java            (Dev-UI/Audio)
└── CreditsScreen.java           (Dev-UI/Audio)

core/src/main/java/com/gdx/game/inventory/
└── TradeUI.java                 (Fixer-Inventory)

core/src/test/java/com/gdx/game/network/
├── NetworkManagerTest.java      (Leader)
├── GameServerTest.java          (Leader)
└── GameClientTest.java          (Leader)
```

---

## FILES QUAN TRỌNG CẦN SỬA

| File | Ai sửa | Mục đích |
|------|--------|----------|
| `build.gradle.kts` | Leader | Thêm KryoNet dependency |
| `gradle/libs.versions.toml` | Leader | Thêm KryoNet version |
| `InventorySlot.java` | Fixer-Inventory | Fix bug stacking |
| `InventorySlotSource.java` | Fixer-Inventory | Fix bug drag offset |
| `StatusUI.java` | Fixer-Inventory | Fix bug HUD spacing |
| `FadeInTransitionEffect.java` | Fixer-System | Fix bug FadeIn |
| `MenuNewGameScreen.java` | Fixer-System | Enable FadeIn |
| `MenuLoadGameScreen.java` | Fixer-System | Enable FadeIn |
| `MapManager.java` | Fixer-System | Fix bug exit position |
| `GdxGame.java` | Fixer-System | Exit handling |
| `GameScreen.java` | Dev-Gameplay | Render player 2, network integration |
| `BattleState.java` | Dev-Gameplay | Multi-player battle |
| `BattleHUD.java` | Dev-Gameplay | 2-player battle UI |
| `BattleScreen.java` | Dev-Gameplay | Network battle commands |
| `Entity.java` | Dev-Gameplay | Remote player support |
| `EntityFactory.java` | Dev-Gameplay | REMOTE_PLAYER type |
| `MenuScreen.java` | Dev-UI/Audio | Host/Join buttons |
| `AudioManager.java` | Dev-UI/Audio | Sound effects |
| `OptionScreen.java` | Dev-UI/Audio | Sound settings |
| `MapFactory.java` | Dev-UI/Audio | New maps |
| `ProfileManager.java` | Fixer-System | Co-op save/load |

---

## VERIFICATION / KIỂM THỬ

### Sau mỗi Sprint:
1. **Chạy toàn bộ unit tests:** `./gradlew test`
2. **Chạy integration tests:** `./gradlew integrationTest`
3. **Chạy game thủ công:** `./gradlew desktop:run` (macOS cần `-XstartOnFirstThread` đã config)

### Test Plan cho LAN Co-op:
1. Host tạo game -> verify server listening
2. Client join bằng IP LAN -> verify connection established
3. Cả 2 player di chuyển trên cùng map -> verify position sync
4. Cả 2 player vào battle -> verify turn order correct
5. Player disconnect -> verify game handles gracefully
6. Reconnect -> verify state restored
7. Save/Load co-op session -> verify cả 2 players' state persisted

### Test Plan cho Bug Fixes:
1. Bug 1: Kéo 2 items khác loại vào cùng slot -> verify không stack
2. Bug 2: Kéo item -> verify item hiển thị sát cursor
3. Bug 3: HP/MP thay đổi từ 100->99 -> verify không có blank space
4. Bug 4: Chuyển menu -> game -> verify FadeIn smooth
5. Bug 5: Thoát game bằng nút X -> reopen -> verify vị trí reset đúng
