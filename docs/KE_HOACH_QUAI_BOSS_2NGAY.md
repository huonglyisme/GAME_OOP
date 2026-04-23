# Kế hoạch phát triển Quái + Boss trong 2 ngày

> Mục tiêu: **giữ nguyên logic battle quái gốc** (HP/ATK/DEF/XP hiện tại) cho Forest / Cave3 / Castle Room,
> **chỉ rebalance riêng quái map cuối** (CASTLE_FINAL: RABITE10, RABITE11, RABITE12, RABITE20) để boss khoẻ hơn.
> Thêm thanh HP (đỏ) + Energy/MP (xanh) cho CẢ player và quái trong màn battle, thêm hiệu ứng chiêu đánh đơn giản.
> Nguyên tắc: **sửa ít nhất có thể**, ưu tiên JSON & code, **không đụng tmx trừ khi thật sự cần**.

---

## 0. Bản đồ hệ thống battle hiện tại — để biết cần đụng vào đâu

| Thành phần | File | Vai trò |
|---|---|---|
| Trigger vào battle | [GameScreen.java:212-215](../core/src/main/java/com/gdx/game/screen/GameScreen.java#L212-L215) | Khi `START_BATTLE` → mở `BattleScreen` |
| Màn battle (nền ảnh) | [BattleScreen.java:155](../core/src/main/java/com/gdx/game/screen/BattleScreen.java#L155) | Vẽ `battleBackgroundMeadow`, không phải tmx |
| HUD battle (player + enemy sprite + damage label) | [BattleHUD.java](../core/src/main/java/com/gdx/game/battle/BattleHUD.java) | **Chỉnh UI + hiệu ứng ở đây** |
| Thanh HP/MP/XP của player | [BattleStatusUI.java](../core/src/main/java/com/gdx/game/battle/BattleStatusUI.java) | Đã có — nhưng **chỉ cho player** |
| Nút Attack/Object/Escape | [BattleUI.java](../core/src/main/java/com/gdx/game/battle/BattleUI.java) | Không cần đụng |
| Logic damage / crit / turn | [BattleState.java](../core/src/main/java/com/gdx/game/battle/BattleState.java) | Không cần đụng |
| Config stats của tất cả rabite | [enemies.json](../core/src/main/resources/scripts/enemies.json) | **Rebalance stats ở đây** |
| Bảng HP/MP theo level player | [level_tables.json](../core/src/main/resources/scripts/level_tables.json) | Tham chiếu để cân bằng |
| Enum tên rabite | [EntityFactory.java:38-41](../core/src/main/java/com/gdx/game/entities/EntityFactory.java#L38-L41) | Đã có RABITE1…20 |
| Gán rabite cho từng map | `worldMap/Forest.java`, `Cave3.java`, `CastleRoom.java`, `CastleFinal.java` | Java `addEnemy(…)` + object name trong tmx phải khớp |
| Object spawn quái trong map | `core/src/main/resources/asset/map/*.tmx` → layer `MAP_ENEMY_SPAWN_LAYER` | Chỉ chỉnh khi đổi vị trí / thêm / bớt quái |

### Map hiện tại đang chứa quái nào (đã khảo sát)
- `VILLAGE`, `CAVE_1`: không có quái.
- `FOREST`: RABITE1, RABITE2.
- `CAVE_3`: RABITE4, RABITE5.
- `CASTLE_ROOM`: RABITE6, RABITE7, RABITE9.
- `CASTLE_FINAL`: RABITE10, RABITE11, RABITE12, RABITE20 (RABITE20 = boss).

---

## 1. Cân bằng stats — CHỈ RABITE MAP CUỐI

File duy nhất cần sửa: [enemies.json](../core/src/main/resources/scripts/enemies.json).

### Công thức damage hiện tại (BattleState)
```
damage = max(0, ATK_người_đánh − DEF_người_bị_đánh)
crit (chance phụ thuộc AP) → damage × 1.5
```
→ Để quái "yếu": DEF thấp + HP thấp + ATK thấp hơn DP player.
→ Để quái "khoẻ": HP cao + DEF cao + ATK cao hơn DP player.

### Stats player tham khảo (warrior mặc định, level 1)
- Base: AP=6, DP=5, HP=50, MP=50
- Full gear tốt ở Castle Room ≈ AP≈22, DP≈18, HP≈130 (level 5)
- Full gear boss tier ≈ AP≈28, DP≈24, HP≈160 (level 5-6)

### 1.1. Forest / Cave3 / Castle Room — GIỮ NGUYÊN (không sửa)

Logic battle hiện tại đã cân bằng ổn cho các tier này. **Không đụng các entry sau trong `enemies.json`**:

| EntityID | Map | HP | ATK | DEF | MATK | MDEF | SPD | XP | GP |
|---|---|---|---|---|---|---|---|---|---|
| RABITE1 | FOREST | 15 | 6 | 5 | 10 | 10 | 5 | 10 | 5 |
| RABITE2 | FOREST | 15 | 6 | 5 | 10 | 10 | 5 | 10 | 5 |
| RABITE4 | CAVE_3 | 40 | 15 | 12 | 14 | 14 | 7 | 50 | 20 |
| RABITE5 | CAVE_3 | 60 | 22 | 16 | 16 | 16 | 8 | 100 | 40 |
| RABITE6 | CASTLE_ROOM | 90 | 30 | 22 | 20 | 20 | 9 | 180 | 70 |
| RABITE7 | CASTLE_ROOM | 90 | 30 | 22 | 20 | 20 | 9 | 180 | 70 |
| RABITE9 | CASTLE_ROOM | 140 | 42 | 30 | 25 | 25 | 10 | 300 | 120 |

→ Giữ nguyên HP/XP nghĩa là số đòn để hạ gục, phần thưởng kinh nghiệm không đổi — không ảnh hưởng balance trước boss, không break tests.

### 1.2. Castle Final (map cuối) — REBALANCE

Đây là tier cần điều chỉnh để boss thật sự khó. Đề xuất:

| EntityID | Map | HP | ATK | DEF | MATK | MDEF | SPD | XP | GP |
|---|---|---|---|---|---|---|---|---|---|
| **RABITE10** (boss-lính) | CASTLE_FINAL | 200 | 45 | 32 | 30 | 30 | 11 | 450 | 180 |
| **RABITE11** (boss-lính) | CASTLE_FINAL | 200 | 45 | 32 | 30 | 30 | 11 | 450 | 180 |
| **RABITE12** (boss-lính) | CASTLE_FINAL | 240 | 50 | 36 | 32 | 32 | 11 | 550 | 220 |
| **RABITE20** (BOSS CUỐI) | CASTLE_FINAL | **600** | **55** | **34** | **45** | **45** | **13** | 6000 | 2500 |

Lý do chọn số:
- RABITE10/11/12: giữa RABITE9 (HP=140, ATK=42, DEF=30) và boss cuối — player full gear AP≈28 deal ~(28-32→bị DEF chặn, crit mới ăn) → cần gear tốt hơn hoặc grind level.
- Boss RABITE20: **600 HP**. Full gear AP≈28, DP≈24 → player đánh ~(28-34<0, phải crit): crit dmg ≈ 1-3 → rất lâu. Cần thêm weapon tier cao hơn (AP>40) hoặc level up. Boss ATK=55 − DP=24 = 31 dmg/đòn → player 160 HP chỉ chịu ~5 hit.
  → Phải **dùng potion + scroll + về inn** hoặc chia **2 lượt battle** (boss bị giảm HP nhưng player chạy ra inn hồi, quay lại đánh tiếp).
  → Đây là cách đơn giản nhất để hiện thực hoá yêu cầu "phải trang bị đủ và đánh 2 player mới win được" — player gần như KHÔNG thể solo-1-shot, phải retry/hồi máu, hoặc level up lên ~L6+ để có HP cao hơn.

> ⚠️ **Lưu ý**: enemy HP không persist qua lần battle khác (`EntityConfig` được clone khi `getEntityByName`). Nghĩa là chạy escape rồi quay lại, boss hồi full HP. Nếu muốn boss giữ HP giữa các battle (yêu cầu "đánh 2 player"), xem mục mở rộng §5.

### Cách sửa
1. Mở [enemies.json](../core/src/main/resources/scripts/enemies.json).
2. **Chỉ** với các entry RABITE10, RABITE11, RABITE12, RABITE20, tìm block `entityProperties` và đổi 8 số: `ENTITY_HEALTH_POINTS`, `ENTITY_PHYSICAL_ATTACK_POINTS`, `ENTITY_PHYSICAL_DEFENSE_POINTS`, `ENTITY_MAGIC_ATTACK_POINTS`, `ENTITY_MAGIC_DEFENSE_POINTS`, `ENTITY_SPEED_POINTS`, `ENTITY_XP_REWARD`, `ENTITY_GP_REWARD` theo bảng §1.2. Giữ nguyên cấu trúc `{ "class": "java.lang.String", "value": X }`.
3. **KHÔNG đụng entry RABITE1/2/4/5/6/7/9.**
4. **KHÔNG đụng tmx, KHÔNG đụng Java.**

---

## 2. Thanh HP/MP cho enemy trong battle — CHỈ SỬA CODE

**Tình trạng hiện tại**: `BattleStatusUI` chỉ render HP/MP/XP cho player. Enemy chưa có thanh nào → người chơi không biết boss còn bao nhiêu máu.

**Cách đơn giản, đúng scope 2 ngày**: thêm một `Window` tương tự BattleStatusUI nhưng rút gọn (chỉ HP+MP, không XP/level), đặt ở góc phải trên, dùng chung atlas `statusui` sẵn có (region `HP_Bar`, `MP_Bar`, `Bar`).

### 2.1. Tạo file mới `core/src/main/java/com/gdx/game/battle/EnemyStatusUI.java`

```java
package com.gdx.game.battle;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.utils.Align;

import static com.gdx.game.manager.ResourceManager.STATUS_UI_SKIN;
import static com.gdx.game.manager.ResourceManager.STATUS_UI_TEXTURE_ATLAS;

/** Thanh HP (đỏ) + MP (xanh) cho quái — hiển thị ở góc phải trên màn battle. */
public class EnemyStatusUI extends Window {

    private Image hpBar;
    private Image mpBar;
    private Label hpValLabel, hpValLabelMax, mpValLabel, mpValLabelMax, nameLabel;

    private int hpMax = 1, hpCur = 1;
    private int mpMax = 1, mpCur = 1;
    private float barWidth, barHeight;

    public EnemyStatusUI() {
        super("", STATUS_UI_SKIN);
        defaults().expand().fill();

        nameLabel = new Label("Enemy", STATUS_UI_SKIN);
        add(nameLabel).align(Align.left).colspan(5);
        row();

        WidgetGroup hpGroup = new WidgetGroup();
        Image hpBg = new Image(STATUS_UI_TEXTURE_ATLAS.findRegion("Bar"));
        hpBar = new Image(STATUS_UI_TEXTURE_ATLAS.findRegion("HP_Bar"));
        hpBar.setPosition(3, 6);
        hpGroup.addActor(hpBg);
        hpGroup.addActor(hpBar);
        add(hpGroup).size(hpBg.getWidth(), hpBg.getHeight()).padRight(10);
        add(new Label(" hp ", STATUS_UI_SKIN));
        hpValLabel = new Label("0", STATUS_UI_SKIN); add(hpValLabel).width(40).right();
        add(new Label("/", STATUS_UI_SKIN));
        hpValLabelMax = new Label("0", STATUS_UI_SKIN); add(hpValLabelMax).width(40).right();
        row();

        WidgetGroup mpGroup = new WidgetGroup();
        Image mpBg = new Image(STATUS_UI_TEXTURE_ATLAS.findRegion("Bar"));
        mpBar = new Image(STATUS_UI_TEXTURE_ATLAS.findRegion("MP_Bar"));
        mpBar.setPosition(3, 6);
        mpGroup.addActor(mpBg);
        mpGroup.addActor(mpBar);
        add(mpGroup).size(mpBg.getWidth(), mpBg.getHeight()).padRight(10);
        add(new Label(" mp ", STATUS_UI_SKIN));
        mpValLabel = new Label("0", STATUS_UI_SKIN); add(mpValLabel).width(40).right();
        add(new Label("/", STATUS_UI_SKIN));
        mpValLabelMax = new Label("0", STATUS_UI_SKIN); add(mpValLabelMax).width(40).right();

        barWidth = hpBar.getWidth();
        barHeight = hpBar.getHeight();
        pack();
    }

    public void setEnemy(String name, int hpMax, int mpMax) {
        this.nameLabel.setText(name);
        this.hpMax = this.hpCur = Math.max(1, hpMax);
        this.mpMax = this.mpCur = Math.max(1, mpMax);
        hpValLabelMax.setText(String.valueOf(hpMax));
        mpValLabelMax.setText(String.valueOf(mpMax));
        setHP(hpCur);
        setMP(mpCur);
    }

    public void setHP(int v) {
        hpCur = MathUtils.clamp(v, 0, hpMax);
        hpValLabel.setText(String.valueOf(hpCur));
        hpBar.setSize(barWidth * ((float) hpCur / hpMax), barHeight);
    }

    public void setMP(int v) {
        mpCur = MathUtils.clamp(v, 0, mpMax);
        mpValLabel.setText(String.valueOf(mpCur));
        mpBar.setSize(barWidth * ((float) mpCur / mpMax), barHeight);
    }
}
```

### 2.2. Sửa [BattleHUD.java](../core/src/main/java/com/gdx/game/battle/BattleHUD.java)

**Thêm field** (gần dòng 47):
```java
private EnemyStatusUI enemyStatusUI;
private int enemyMaxHP = 1;
private int enemyMaxMP = 50;   // cosmetic — enemy chưa dùng MP trong logic
private int enemyCurMP = 50;
```

**Trong constructor**, sau khi tạo `battleStatusUI` (khoảng dòng 108-113), thêm:
```java
enemyStatusUI = new EnemyStatusUI();
enemyStatusUI.setKeepWithinStage(false);
enemyStatusUI.setVisible(true);
enemyStatusUI.setPosition(battleStage.getWidth() / 2, 2 * battleStage.getHeight() / 3);  // góc phải trên
enemyStatusUI.setWidth(battleStage.getWidth() / 2);
enemyStatusUI.setHeight(battleStage.getHeight() / 5);
enemyStatusUI.validate();
```

**Thêm `battleHUDStage.addActor(enemyStatusUI);`** ngay sau dòng `battleHUDStage.addActor(battleStatusUI);` (dòng 163).

**Trong `onNotify(Entity, BattleEvent)`**, sửa case `OPPONENT_ADDED`:
```java
case OPPONENT_ADDED -> {
    opponentImage.setEntity(entity);
    opponentImage.setCurrentAnimation(Entity.AnimationType.IMMOBILE);
    opponentImage.setSize(enemyWidth, enemyHeight);
    opponentImage.setPosition(600, 200);
    currentOpponentImagePosition.set(opponentImage.getX(), opponentImage.getY());

    // THÊM:
    enemyMaxHP = Integer.parseInt(entity.getEntityConfig().getPropertyValue(
            EntityConfig.EntityProperties.ENTITY_HEALTH_POINTS.toString()));
    enemyMaxMP = 50 + 2 * Integer.parseInt(entity.getEntityConfig().getPropertyValue(
            EntityConfig.EntityProperties.ENTITY_MAGIC_ATTACK_POINTS.toString()));
    enemyCurMP = enemyMaxMP;
    enemyStatusUI.setEnemy(entity.getEntityConfig().getEntityID(), enemyMaxHP, enemyMaxMP);
}
```

**Thêm cập nhật HP sau case `OPPONENT_HIT_DAMAGE`** (sau dòng 246):
```java
int enemyHP = Integer.parseInt(entity.getEntityConfig().getPropertyValue(
        EntityConfig.EntityProperties.ENTITY_HEALTH_POINTS.toString()));
enemyStatusUI.setHP(enemyHP);
// MP chỉ mang tính thị giác — mỗi lần bị hit tụt 5
enemyCurMP = Math.max(0, enemyCurMP - 5);
enemyStatusUI.setMP(enemyCurMP);
```

**Trong case `OPPONENT_DEFEATED`** (dòng 254+), thêm `enemyStatusUI.setVisible(false);` để ẩn bar khi quái chết.

**Trong `dispose()`** (dòng 468+), thêm `enemyStatusUI.remove();`.

### 2.3. (Tuỳ chọn) MP cho player đã có sẵn
Player HP+MP đã render ở [BattleStatusUI.java:99-153](../core/src/main/java/com/gdx/game/battle/BattleStatusUI.java#L99-L153) với `HP_Bar` (đỏ) + `MP_Bar` (xanh). Không cần sửa gì.

> ⚠️ **Tránh bug**: đừng tạo atlas mới, chỉ reuse `STATUS_UI_TEXTURE_ATLAS.findRegion("HP_Bar" / "MP_Bar" / "Bar")`. Đừng đổi sprite atlas — sẽ phá UI hiện tại.

---

## 3. Hiệu ứng chiêu đánh — CHỈ SỬA BattleHUD.java

**Tình trạng**: khi đánh, chỉ có label số damage bay lên (dmgPlayerValLabel / dmgOpponentValLabel). Không có hiệu ứng di chuyển / flash / shake.

**Thiết kế đơn giản (không cần asset mới)**:
1. **Player attack**: sprite player "lao tới" enemy 0.2s rồi lùi về → dùng `Actions.sequence(moveTo(...), moveTo(...))` có sẵn LibGDX.
2. **Enemy hit flash**: khi `OPPONENT_HIT_DAMAGE` → enemy sprite đổi color trắng→đỏ→trắng 0.15s dùng `Actions.color`.
3. **Player hit shake**: khi `PLAYER_HIT_DAMAGE` → player sprite dịch x trái-phải nhẹ 3 lần dùng `Actions.sequence(moveBy(±5,0)…)`.

### Patch vào [BattleHUD.java](../core/src/main/java/com/gdx/game/battle/BattleHUD.java)

Thêm import:
```java
import com.badlogic.gdx.graphics.Color;
```
(đã có, kiểm tra dòng 4).

Trong case `OPPONENT_HIT_DAMAGE` (sau dòng 244), thêm:
```java
// hiệu ứng: player lao tới + enemy flash đỏ
playerImage.addAction(com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence(
        com.badlogic.gdx.scenes.scene2d.actions.Actions.moveTo(450, 200, 0.15f),
        com.badlogic.gdx.scenes.scene2d.actions.Actions.moveTo(200, 200, 0.15f)
));
opponentImage.addAction(com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence(
        com.badlogic.gdx.scenes.scene2d.actions.Actions.color(Color.RED, 0.08f),
        com.badlogic.gdx.scenes.scene2d.actions.Actions.color(Color.WHITE, 0.15f)
));
```

Trong case `PLAYER_HIT_DAMAGE` (sau dòng 231), thêm:
```java
// hiệu ứng: quái lao tới + player shake
opponentImage.addAction(com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence(
        com.badlogic.gdx.scenes.scene2d.actions.Actions.moveTo(350, 200, 0.15f),
        com.badlogic.gdx.scenes.scene2d.actions.Actions.moveTo(600, 200, 0.15f)
));
playerImage.addAction(com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence(
        com.badlogic.gdx.scenes.scene2d.actions.Actions.moveBy(5, 0, 0.05f),
        com.badlogic.gdx.scenes.scene2d.actions.Actions.moveBy(-10, 0, 0.05f),
        com.badlogic.gdx.scenes.scene2d.actions.Actions.moveBy(5, 0, 0.05f)
));
```

> ⚠️ **Tránh conflict**: đừng bỏ dòng `playerImage.setCurrentAnimation(LOOK_RIGHT)` ở `render()` (dòng 437). Chuỗi Action chạy xong, render() sẽ reset về LOOK_RIGHT nên không leak state.

---

## 4. Chỉnh vị trí thanh HP + UI cho quái

Tất cả position ở [BattleHUD.java](../core/src/main/java/com/gdx/game/battle/BattleHUD.java):

| UI | Vị trí hiện tại | File:line |
|---|---|---|
| Player HP/MP/XP (BattleStatusUI) | `(0, 0)` — góc trái dưới | [BattleHUD.java:111](../core/src/main/java/com/gdx/game/battle/BattleHUD.java#L111) |
| BattleUI (nút Attack/Escape) | `(width/10, 2*height/3)` — trái trên | [BattleHUD.java:126](../core/src/main/java/com/gdx/game/battle/BattleHUD.java#L126) |
| Player sprite | `(200, 200)` sau khi lao vào | [BattleHUD.java:196](../core/src/main/java/com/gdx/game/battle/BattleHUD.java#L196) |
| Enemy sprite | `(600, 200)` | [BattleHUD.java:204](../core/src/main/java/com/gdx/game/battle/BattleHUD.java#L204) |
| Enemy HP/MP (mới — EnemyStatusUI) | `(width/2, 2*height/3)` — phải trên | (patch §2.2) |
| Damage label player | `padLeft(playerWidth/2) padBottom(playerHeight*4)` | [BattleHUD.java:153](../core/src/main/java/com/gdx/game/battle/BattleHUD.java#L153) |
| Damage label enemy | `padLeft(enemyWidth/2) padBottom(enemyHeight*4)` | [BattleHUD.java:155](../core/src/main/java/com/gdx/game/battle/BattleHUD.java#L155) |

**Đổi vị trí**: chỉnh 2 số trong `setPosition(x, y)`. **Không cần đụng tmx.**

### Đổi UI/UX quái (sprite, tên, màu)
- Đổi **sprite** quái: sửa trường `texturePaths` + `gridPoints` trong [enemies.json](../core/src/main/resources/scripts/enemies.json) (block `animationConfig`). Sprite sheet hiện dùng `sprites/characters/Demon0.png`, `Demon1.png`.
- Đổi **tên hiển thị**: `enemyStatusUI.setEnemy(entity.getEntityConfig().getEntityID(), ...)` → có thể map entityID → tên đẹp bằng switch-case trong BattleHUD (VD: `RABITE20` → `"Mana Lord"`).
- Đổi **size sprite quái**: `enemyWidth`/`enemyHeight` ở [BattleHUD.java:63-64](../core/src/main/java/com/gdx/game/battle/BattleHUD.java#L63-L64). Ví dụ boss to hơn: chỉ riêng RABITE20 set `enemyWidth=100, enemyHeight=100` trong `OPPONENT_ADDED` (check entity ID).

---

## 5. (Tuỳ chọn) Boss cần "2 player" mới thắng — giải pháp đơn giản

Game hiện là single-player (mỗi ProfileManager 1 player). Cách diễn giải khả thi để làm đúng 2 ngày:

### Option A — Recommended (không đụng logic): HP boss đủ cao để chết 1 lần vẫn chưa thắng
- Đặt RABITE20 HP = 600 (theo §1.2).
- Khi player die → `GameOverScreen` → cho load lại save.
- **Trước khi vào CASTLE_FINAL portal**, player bắt buộc đã equip đầy đủ (armor+weapon+shield+helmet+boots+wand) — đây là điều kiện implicit.
- Trải nghiệm thực tế: player rush boss tay không → chết ngay; player full gear + L5+ → vẫn phải sống sót ~20-30 hit, dùng potion.
- **Không cần code thêm gì**, chỉ dùng §1 (stats).

### Option B — Persist boss HP giữa các lần battle (nếu muốn "2 lần battle mới hạ")
Thêm vào [CastleFinal.java](../core/src/main/java/com/gdx/game/map/worldMap/CastleFinal.java) method lưu HP boss còn lại:
```java
// sau khi battle xong, trong BattleState.OPPONENT_DEFEATED/RESUME_OVER
ProfileManager.getInstance().setProperty(
    currentOpponent.getEntityConfig().getEntityID() + "_HP",
    currentOpponent.getEntityConfig().getPropertyValue("ENTITY_HEALTH_POINTS"));
```
Và khi spawn boss, đọc lại:
```java
String saved = ProfileManager.getInstance().getProperty(name + "_HP", String.class);
if (saved != null) entity.getEntityConfig().setPropertyValue("ENTITY_HEALTH_POINTS", saved);
```
→ Phức tạp hơn, dễ bug save/load. **Khuyến nghị bỏ qua**, dùng Option A.

---

## 6. Có cần động tới Tiled / tmx không?

**Không, cho toàn bộ yêu cầu 2 ngày này.**

| Task | Công cụ | File |
|---|---|---|
| Rebalance stats rabite | Text editor | `scripts/enemies.json` |
| Thêm thanh HP/MP quái | Text editor / IDE | `BattleHUD.java`, `EnemyStatusUI.java` (mới) |
| Hiệu ứng chiêu đánh | Text editor / IDE | `BattleHUD.java` |
| Đổi vị trí UI | Text editor / IDE | `BattleHUD.java` |
| Đổi sprite/tên quái | Text editor | `enemies.json` |

**Chỉ cần Tiled khi**:
- Thêm quái MỚI (RABITE21 chẳng hạn) → phải add object `name="RABITE21"` vào `MAP_ENEMY_SPAWN_LAYER` trong tmx, đồng thời add enum + entry JSON.
- Đổi vị trí spawn quái trên map world.
- Đổi portal, player_start.

Yêu cầu hiện tại **không cần** — đủ RABITE1…20 rồi.

---

## 7. Lịch trình 2 ngày

### Ngày 1 (ưu tiên chất lượng, test chạy được)
- [ ] **Buổi sáng (2h)**: Rebalance CHỈ RABITE10/11/12/20 trong [enemies.json](../core/src/main/resources/scripts/enemies.json) theo §1.2. Giữ nguyên các rabite khác. Chạy game vào CASTLE_FINAL đánh thử → xác nhận boss khó hơn, các tier Forest/Cave3/CastleRoom vẫn hoạt động như cũ (test regression).
- [ ] **Buổi chiều (3h)**: Tạo `EnemyStatusUI.java` (§2.1). Patch `BattleHUD.java` (§2.2). Chạy game → vào battle → xác nhận thanh HP+MP quái hiện ở góc phải trên, trừ đúng khi đánh. Check boss RABITE20 thanh HP rất dài (600).

### Ngày 2 (polish)
- [ ] **Buổi sáng (2h)**: Thêm hiệu ứng attack (§3). Chạy game check animation mượt không giật.
- [ ] **Buổi chiều (2h)**: Test toàn bộ chuỗi game (Forest → Cave3 → CastleRoom → CastleFinal boss). Chỉnh stats nếu thấy quá dễ/khó. Viết changelog.
- [ ] **Dự trữ (1-2h)**: Đổi tên đẹp cho boss (RABITE20 → "Mana Lord"), tăng size sprite boss (§4).

---

## 8. Checklist tránh bug & conflict

- ✅ **Không thêm asset mới** — reuse `HP_Bar`, `MP_Bar`, `Bar` trong `statusui.atlas`.
- ✅ **Không đổi observer events** — chỉ thêm logic vào case có sẵn (`OPPONENT_ADDED`, `OPPONENT_HIT_DAMAGE`, `PLAYER_HIT_DAMAGE`, `OPPONENT_DEFEATED`).
- ✅ **Không đụng [BattleState.java](../core/src/main/java/com/gdx/game/battle/BattleState.java)** — công thức damage/crit/turn giữ nguyên.
- ✅ **Không đổi tmx** — tránh breakpoint loader, tránh conflict Tiled version.
- ✅ **Không đụng stats RABITE1/2/4/5/6/7/9** — tests hiện tại (`BattleHUDTest`, `BattleScreenTest`, `PlayerHUDTest` dùng `RABITE1`) tiếp tục pass vì stats không đổi.
- ⚠️ **Nếu thêm sprite boss to** — đổi `enemyWidth` chỉ khi `entity.getEntityConfig().getEntityID().equals("RABITE20")` để không ảnh hưởng các rabite khác.
- ⚠️ **Cân bằng thực tế** — test ít nhất 1 lần đánh boss với full gear và tay không để xác nhận đúng kỳ vọng.

---

## 9. Tóm tắt file cần đụng

| Hành động | File | Loại sửa |
|---|---|---|
| 1 | `core/src/main/resources/scripts/enemies.json` | Sửa JSON |
| 2 | `core/src/main/java/com/gdx/game/battle/EnemyStatusUI.java` | Tạo mới |
| 3 | `core/src/main/java/com/gdx/game/battle/BattleHUD.java` | Sửa code |

**Tổng cộng: 2 file sửa + 1 file mới. Không đụng tmx. Tests giữ nguyên (không đổi stats rabite tier đầu).**
