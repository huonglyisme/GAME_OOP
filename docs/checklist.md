# Checklist 2 ngày — Gameplay điểm cao + Co-op realtime

## Triết lý chọn scope
Co-op "2 player chung battle screen" mất 5-8 ngày → **không vừa**. Thay vào đó dùng model **shared world + async battle**:
- 2 máy thấy nhau **di chuyển realtime** trên map
- Quái HP/alive/dead **sync qua mạng**
- Battle vẫn 1v1 turn-based (không cần đụng `BattleState`)
- A đánh quái còn 30/100 HP rồi escape → packet sync → B mở battle với quái đó **HP=30**
- Đáp ứng yêu cầu "**Player 2 đánh tiếp quái với HP cũ**" mà không phải viết lại battle engine

→ Build trên transport sẵn có (`NetworkManager`/`GameServer`/`GameClient`), chỉ thêm **2 packet mới** + render remote player.

---

## Tính năng mới đề xuất (điểm cao, fix nhẹ)

| # | Tính năng | Lý do ăn điểm |
|---|---|---|
| **A1** | Boss phase 2 — khi HP<50% boss đổi stats (ATK +50%, SPD +30%) | "AI animation" / "boss mechanic" |
| **A2** | Boss drop item duy nhất (MANA_CRYSTAL) | "loot system" |
| **A3** | Auto-save khi qua portal | "save system" |
| **A4** | SFX attack/hit (reuse asset có sẵn nếu có) | "audio polish" |
| **A5** | Achievement "Boss Slayer" lưu vào profile | "progression system" |
| **A6** | Damage number bay lên (đã có label) — đổi thành animation thật | "visual feedback" (gameplay-adjacent) |

---

## DAY 1 — Single-player gameplay

### Gameplay từ ảnh
- [ ] **T1.1** Escape bất kỳ lúc nào
  → [BattleState.java:269-280](../core/src/main/java/com/gdx/game/battle/BattleState.java#L269-L280) bỏ check `escapeChance`, luôn `notify(PLAYER_RUNNING)`
- [ ] **T1.2** Boss buff RABITE10/11/12/20 + cần ≥2 turn
  → KHÔNG đụng RABITE1/2/4/5/6/7/9 (giữ stats gốc).
  → Trong [enemies.json](../core/src/main/resources/scripts/enemies.json) đổi `entityProperties` cho 4 entry sau:

  | EntityID | HP | ATK | DEF | MATK | MDEF | SPD | XP | GP |
  |---|---|---|---|---|---|---|---|---|
  | RABITE10 | 200 | 45 | 32 | 30 | 30 | 11 | 450 | 180 |
  | RABITE11 | 200 | 45 | 32 | 30 | 30 | 11 | 450 | 180 |
  | RABITE12 | 240 | 50 | 36 | 32 | 32 | 11 | 550 | 220 |
  | RABITE20 (BOSS) | **600** | **55** | **34** | **45** | **45** | **13** | 6000 | 2500 |

  → Boss HP=600 với DP player full gear ≈24 → boss ATK 55−24=31 dmg/đòn → player 160 HP chỉ chịu ~5 hit → bắt buộc retry/potion, không thể 1-shot.
- [ ] **T1.3** Lưu HP quái giữa các lần battle
  → trong `BattleState.playerRuns()` (escape) + nhánh `OPPONENT_DEFEATED`:
  ```java
  ProfileManager.getInstance().setProperty(
      currentOpponent.getEntityConfig().getEntityID(),
      currentOpponent.getEntityConfig());
  ```
  → Forest.java/Cave3.java/CastleRoom.java/CastleFinal.java đã có code đọc back (đã verify ở [Forest.java:57-60](../core/src/main/java/com/gdx/game/map/worldMap/Forest.java#L57-L60))
- [ ] **T1.4** Quái không biến mất sau escape
  → verify trong `MapManager`/`Map`: tìm chỗ remove `mapEntities` khi battle kết thúc, chỉ remove khi `OPPONENT_DEFEATED`, KHÔNG remove khi escape
- [ ] **T1.5** Portal mở khi diệt hết quái trong map
  → thêm method `Map.allEnemiesDefeated()`: duyệt `mapEntities`, check HP=0 với mỗi enemy
  → trong [GameScreen.java:212-215](../core/src/main/java/com/gdx/game/screen/GameScreen.java#L212-L215) hook check trước khi `START_BATTLE` portal trigger
  → nếu chưa diệt hết, hiện toast "Hãy diệt hết quái trước!"
- [ ] **T1.6** Quest từ NPC
  → thêm 1 quest đơn giản dùng `HUNTER_WOUNDED` hiện có (đã có config + conversation): "Tìm 3 fur trong rừng"
  → reuse `QuestGraph` infra, chỉ thêm entry json mới

### Bonus features điểm cao
- [ ] **A1** Boss phase 2
  → trong `BattleState.getOpponentAttackCalculationTimer()`: đọc HP, nếu < 50% và chưa phase2:
  ```java
  if (hpRatio < 0.5 && !phase2Activated) {
      currentOpponent.config.setProperty(ATK, currentATK * 1.5);
      currentOpponent.config.setProperty(SPD, currentSPD * 1.3);
      phase2Activated = true;
      notify(BOSS_PHASE_2);  // event mới cho UI nhấp nháy
  }
  ```
- [ ] **A2** Boss drop MANA_CRYSTAL
  → thêm item config + thêm vào `RABITE20_DROPS` trong [drop_tables.json](../core/src/main/resources/scripts/drop_tables.json) `{ itemTypeID: "MANA_CRYSTAL", probability: 1.0 }`
- [ ] **A3** Auto-save on portal
  → trong portal handler GameScreen: gọi `ProfileManager.getInstance().writeProfileToStorage()` sau khi load map mới
- [ ] **A4** SFX attack/hit
  → check `AudioObserver` có sẵn audio events; nếu có asset wav, thêm `notify(AudioCommand.SOUND_PLAY_ONCE, AudioTypeEvent.HIT)` vào `OPPONENT_HIT_DAMAGE` / `PLAYER_HIT_DAMAGE`
  → nếu không có asset: SKIP, không cố ép
- [ ] **A5** Achievement
  → trong `OPPONENT_DEFEATED` check entityID = "RABITE20": `ProfileManager.setProperty("achievement_boss_slayer", true)` + popup label "Achievement Unlocked: Boss Slayer"
- [ ] **A6** Damage number animation
  → label đã có ở [BattleHUD](../core/src/main/java/com/gdx/game/battle/BattleHUD.java); thêm `Actions.sequence(moveBy(0,40,0.5f), fadeOut(0.3f))` thay vì set text tĩnh

- [ ] **TEST D1**: chạy 1 lượt full Village → Forest → Cave3 → CastleRoom → CastleFinal → Boss. Verify mọi feature 1.1-1.6 + A1-A6 hoạt động.

---

## DAY 2 — Co-op realtime

### Hook network + render remote player
- [ ] **T2.1** Lobby UI trên main menu
  → thêm 2 nút: "Host Game", "Join Game"
  → Host: gọi `new GameServer().start()` rồi vào game bình thường
  → Join: dialog input IP, gọi `new GameClient().connect(ip)` rồi vào game
  → Single Player: button cũ, mode = NONE (giữ flow hiện tại)
- [ ] **T2.2** Hook lifecycle network vào `GameScreen`
  → field `GameServer`/`GameClient` (chỉ 1 trong 2 active)
  → trong `dispose()`: gọi `disconnect()`
  → handle disconnect graceful: nếu `NetworkManager.mode != NONE` mà mất kết nối → return về main menu
- [ ] **T2.3** Render RemotePlayer trên map
  → trong `GameScreen.render()`: lặp `gameClient.getRemotePlayers()` (hoặc `gameServer.getPlayers()`), vẽ sprite player ở `(x, y)` mỗi remote
  → reuse `PlayerGraphicsComponent` sprite, **tint khác màu** (red/blue) để phân biệt
  → nếu remote ở map khác (`mapName != currentMap`): không vẽ
- [ ] **T2.4** Send PlayerMovePacket realtime
  → trong `PlayerPhysicsComponent`/`PlayerInputComponent` sau khi update position:
  ```java
  if (NetworkManager.getInstance().getMode() != NONE && tickCounter % 6 == 0) {
      NetworkManager.send(new PlayerMovePacket(myId, x, y, currentMap));
  }
  ```
  → throttle ~10Hz (60 FPS / 6 ticks) để tránh flood

### Sync enemy state
- [ ] **T2.5** Tạo `EnemyHpSyncPacket`
  ```java
  public class EnemyHpSyncPacket {
      public String enemyId;     // RABITE6, RABITE20, ...
      public String mapName;
      public int hp;
      public boolean alive;
  }
  ```
  → register trong [NetworkManager.registerPackets()](../core/src/main/java/com/gdx/game/network/NetworkManager.java#L159-L167)
- [ ] **T2.6** Tạo `EnemyDefeatedPacket` — tương tự, payload chỉ `enemyId + mapName`
- [ ] **T2.7** Hook trong `BattleState` broadcast 2 packet
  → trong `playerRuns()` sau khi `PLAYER_RUNNING`:
  ```java
  if (NetworkManager.getInstance().getMode() != NONE) {
      int hp = parseInt(currentOpponent.config.getProperty(HEALTH_POINTS));
      NetworkManager.send(new EnemyHpSyncPacket(
          currentOpponent.config.getEntityID(),
          currentMapName, hp, hp > 0));
  }
  ```
  → trong nhánh `OPPONENT_DEFEATED`: send `EnemyDefeatedPacket`
- [ ] **T2.8** Xử lý packet ở `GameClient` + `GameServer`
  → khi nhận `EnemyHpSyncPacket`:
    1. Update `ProfileManager.setProperty(enemyId, entityConfig với HP mới)`
    2. Nếu enemy đang trong map hiện tại → update `mapEntities` instance HP
  → khi nhận `EnemyDefeatedPacket`: remove khỏi `mapEntities` + flag `enemyId + "_dead" = true`
  → SERVER cần forward broadcast đến các client còn lại
- [ ] **T2.9** Test E2E 2 máy
  - Chạy host trên máy A, join từ máy B (hoặc localhost 2 process)
  - A vào Forest → thấy B avatar → cùng di chuyển
  - A vào battle RABITE1, đánh còn 5/15 HP → escape
  - B vào ô RABITE1 đó → battle với HP = 5/15 (không phải 15/15)
  - A đánh chết RABITE2 → quái biến mất ở máy B
  - Verify boss CASTLE_FINAL: A đánh boss còn 200/600, escape → B vào với 200 HP

### Hardening
- [ ] **T2.10** Error handling
  - Disconnect giữa chừng → toast "Mất kết nối", về main menu
  - Connect timeout → "Không tìm thấy host"
  - Race condition: 2 player vào cùng quái — ai vào trước thắng race, người sau nhận packet "đang busy" hoặc enemy_dead

---

## Risks & cách giảm

| Rủi ro | Mức | Mitigation |
|---|---|---|
| Lag / packet loss | Trung bình | TCP đã guarantee — skip lo |
| 2 player vào cùng quái cùng lúc | Cao | Lock đơn giản: client nào trigger battle trước, server set `enemy_busy=true`, client kia bị deny |
| Host crash → mọi người mất game | Cao | **Không support host migration v1**, ghi rõ "host mất → game over phiên co-op" |
| Sprite RemotePlayer không có art riêng | Thấp | Tint color khác (red/blue) lên sprite player |
| Firewall LAN block port 54555 | Thấp | Doc hướng dẫn mở firewall + test localhost trước |
| ProfileManager cùng key bị overwrite giữa client/host | Trung bình | Chỉ HOST authoritative — client chỉ render, không write trực tiếp; mọi write đi qua packet |

---

## File cần đụng (tổng kết)

| File | Loại | Lý do |
|---|---|---|
| [BattleState.java](../core/src/main/java/com/gdx/game/battle/BattleState.java) | Sửa | T1.1, T1.3, A1, T2.7 |
| [enemies.json](../core/src/main/resources/scripts/enemies.json) | Sửa | T1.2 |
| [drop_tables.json](../core/src/main/resources/scripts/drop_tables.json) | Sửa | A2 |
| [Map.java](../core/src/main/java/com/gdx/game/map/Map.java) | Sửa | T1.5 (allEnemiesDefeated) |
| [GameScreen.java](../core/src/main/java/com/gdx/game/screen/GameScreen.java) | Sửa | T1.5, T2.2, T2.3, A3 |
| [BattleHUD.java](../core/src/main/java/com/gdx/game/battle/BattleHUD.java) | Sửa | A4, A5, A6 |
| Quest config (json) | Tạo | T1.6 |
| Main menu screen | Sửa | T2.1 |
| `EnemyHpSyncPacket.java`, `EnemyDefeatedPacket.java` | **Tạo** | T2.5, T2.6 |
| [NetworkManager.java](../core/src/main/java/com/gdx/game/network/NetworkManager.java) | Sửa | register packet mới |
| [GameClient.java](../core/src/main/java/com/gdx/game/network/GameClient.java), [GameServer.java](../core/src/main/java/com/gdx/game/network/GameServer.java) | Sửa | T2.8 xử lý packet |
| `PlayerInputComponent.java` hoặc `PlayerPhysicsComponent.java` | Sửa | T2.4 throttle send move |

**Tổng: ~12 file sửa + 3 file mới. KHÔNG đụng tmx.**

---

## Buffer & cắt scope nếu chậm

Ưu tiên cắt theo thứ tự (cắt từ cuối trước):
1. A4 SFX (nếu không có asset)
2. A6 damage number animation
3. A5 achievement
4. A1 boss phase 2
5. T1.6 quest

**Tuyệt đối không cắt**: T1.1-T1.5 (gameplay từ ảnh) + T2.1-T2.9 (co-op core).
