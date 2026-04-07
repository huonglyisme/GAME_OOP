# KE HOACH PHAT TRIEN — ECHOES OF MANAFALL

---

# PHAN 1: HANH TRINH NGUOI CHOI

## Flow tong quan

```
[Vao game] → Chon class → Bat dau o Lang → Di map → Danh quai → Danh boss → Thanh Vua
```

```
MAP 1 (Lang)  →  MAP 2 (Duong rung)  →  MAP 3 (Rung sau)  →  MAP 4 (Duong nui)  →  MAP 5 (Hang boss)
  Hoc choi        Mini boss               Boss 1                Quai kho              Boss cuoi
  Mua do          Lam quen battle         Tho san quest         Gac cau quest         Ket thuc game
  Nhan quest      Level 1-3               Level 4-6             Level 7-9             Level 10+
```

---

## MAP 1: LANG KHOI DAU (da co: Topple)

Noi an toan, khong co quai. Nguoi choi hoc cach choi o day.

**NPCs:**

| NPC | Vai tro | Noi gi |
|-----|---------|--------|
| Truong lang | Giao quest dau tien | *"Con a, quai vat ngay cang dong o con duong phia dong. Con co mana trong nguoi, hay giup dan lang diet chung di."* |
| Tho ren | Ban/nang cap vu khi | *"Tao co may thanh kiem tot day. May diet quai mang nguyen lieu ve, tao ren cho."* |
| Phap su | Ban phep/hoi MP | *"Mana trong nguoi nguoi rat dac biet. Ta co the day nguoi cach dung no hieu qua hon."* |
| Chu quan tro | Hoi mau/luu game | *"Nghi ngoi di, ngay mai con chien dau. 10 vang mot dem."* |
| Dan lang (3-4 nguoi) | Noi chuyen, goi y | *"Nghe noi rung phia dong co quai vat lon lam..."* / *"Cau da phia bac dan den nui, dung di khi con yeu."* |

**Nguoi choi lam gi:**
1. Noi chuyen NPC → hieu boi canh
2. Nhan quest dau tien tu truong lang
3. Mua vu khi/potion co ban
4. Di ra cong lang → sang Map 2

---

## MAP 2: CON DUONG RUNG (da co: Topple_Road_1)

Khu vuc dau tien co quai. De, de nguoi choi lam quen chien dau.

**Quai:**

| Quai | Do kho | Drop |
|------|--------|------|
| Rabite (tho bien di) | Rat de | Potion nho, it vang |
| Rabite manh hon | De | Potion, nguyen lieu |

**MINI BOSS: Rabite chua**
- Con Rabite to gap doi, mau nhieu hon
- Danh bai → nhan chia khoa vao rung sau
- Quest truong lang hoan thanh

**Nguoi choi lam gi:**
1. Danh quai, len level
2. Danh mini boss
3. Quay ve lang nop quest → nhan thuong
4. Di tiep sang Map 3

---

## MAP 3: RUNG SAU (can tao moi)

Kho hon, quai manh hon, co NPC moi.

**Quai:**

| Quai | Do kho | Drop |
|------|--------|------|
| Soi bien di | Trung binh | Vang, da soi |
| Cay quai | Trung binh | Go ma thuat |

**NPCs:**

| NPC | Noi gi |
|-----|--------|
| Tho san bi thuong | *"Toi bi quai tan cong... phia truoc co hang dong, ben trong co con quai rat lon. Can than..."* |
| Lai buon di lac | *"Mua gi khong? Toi bi lac duong, ban re cho."* (shop di dong) |

**BOSS 1: Soi chua**
- Boss that su dau tien
- Danh bai → mo duong den nui

**Nguoi choi lam gi:**
1. Gap tho san → nhan quest phu (mang thuoc cho ong ta)
2. Danh quai, farm do
3. Danh Boss 1
4. Di sang Map 4

---

## MAP 4: DUONG NUI DA (can tao moi)

Khu vuc kho, quai manh.

**Quai:**

| Quai | Do kho | Drop |
|------|--------|------|
| Golem da | Kho | Da quy, vang nhieu |
| Ran doc | Kho | Thuoc giai doc |

**NPCs:**

| NPC | Noi gi |
|-----|--------|
| Nguoi gac cau | *"Muon qua cau phai chung minh suc manh. Danh bai ta di!"* (NPC mini battle) |

**Nguoi choi lam gi:**
1. Danh quai kho hon → can da nang cap do
2. Danh NPC gac cau (hoac tra vang)
3. Di tiep sang Map 5

---

## MAP 5: DINH NUI — HANG BOSS CUOI (can tao moi)

Map cuoi cung. Khong co shop, khong co cho hoi mau. Vao la chien.

**Quai:** Quai manh nhat game, canh gac duong den boss

**BOSS CUOI: Quai vat Manafall**
- Sinh vat manh nhat, sinh ra tu vu no Cay Mana
- Nhieu mau, danh manh, co skill dac biet

**Nguoi choi lam gi:**
1. Danh qua dam quai canh
2. Danh Boss cuoi
3. Thang → Cutscene ket thuc: nhan vat duoc ton lam Vua, dung mana xay dung lai the gioi
4. → Credits

---

## TOM TAT CONTENT

| Thu | So luong |
|-----|----------|
| Map | 5 (2 co san + 3 tao moi) |
| Boss | 3 (1 mini boss + 1 boss + 1 boss cuoi) |
| NPC | ~10 (5 o lang + 2 rung sau + 1 duong nui + dan lang phu) |
| Loai quai | ~6 loai (de → kho dan) |
| Quest chinh | ~4 |
| Quest phu | ~2-3 |

---
---

# PHAN 2: PHAN CONG JIRA — 4 TUAN, 5 NGUOI

## Nhom

| STT | Vai tro | Phu trach |
|-----|---------|-----------|
| 1 | **Leader** | P2P Networking + Review code + Merge |
| 2 | **TV2** | Map & World |
| 3 | **TV3** | NPC & Quest & Hoi thoai |
| 4 | **TV4** | Battle & Enemy & Items |
| 5 | **TV5** | UI & Audio & Effects |

## Epics tren Jira

| Epic | Owner |
|------|-------|
| `NET` - Networking | Leader |
| `MAP` - Map & World | TV2 |
| `NPC` - NPC & Quest | TV3 |
| `BAT` - Battle & Enemy | TV4 |
| `UI` - UI & Audio | TV5 |

## Kanban columns

```
| Backlog | To Do | In Progress | Review (Leader) | Done |
```

---

## TUAN 1 — FIX BUGS + CHUAN BI

**Gate: 0 bug con lai, co tileset, co hoi thoai NPC, Leader ket noi duoc 2 may LAN**

```
Song song:
  Leader ──── NET-1
  TV2    ──── MAP-7
  TV3    ──── NPC-1→5
  TV4    ──── BAT-1 → BAT-2 → BAT-11
  TV5    ──── UI-1 → UI-2 → UI-3
```

| Task ID | Ten task | Ai lam | Cho ai | Output |
|---------|----------|--------|--------|--------|
| NET-1 | Nghien cuu KryoNet, thu ket noi 2 may | Leader | Khong cho | Demo 2 may ket noi LAN |
| MAP-7 | Tim tileset cho rung, nui, hang | TV2 | Khong cho | Co du tileset |
| MAP-1 | Chinh Map 1 (Lang Topple) | TV2 | Cho MAP-7 | Spawn points NPC dung |
| MAP-2 | Chinh Map 2 (Duong rung Topple_Road_1) | TV2 | Cho MAP-1 | Vung spawn quai, vi tri mini boss |
| NPC-1 | Viet hoi thoai Truong lang | TV3 | Khong cho | conversation JSON |
| NPC-2 | Viet hoi thoai Tho ren | TV3 | Khong cho | conversation JSON |
| NPC-3 | Viet hoi thoai Phap su | TV3 | Khong cho | conversation JSON |
| NPC-4 | Viet hoi thoai Chu quan tro | TV3 | Khong cho | conversation JSON |
| NPC-5 | Viet hoi thoai Dan lang (3-4 nguoi) | TV3 | Khong cho | conversation JSON |
| BAT-1 | Fix bug items stacking sai loai | TV4 | Khong cho | InventorySlot.java chay dung |
| BAT-2 | Fix bug drag & drop offset | TV4 | Cho BAT-1 | Keo item hien dung cho |
| BAT-11 | Thiet ke bang items & drop table | TV4 | Cho BAT-2 | Bang thong nhat stats |
| UI-1 | Fix FadeIn effect | TV5 | Khong cho | Chuyen canh muot |
| UI-2 | Fix HUD spacing | TV5 | Cho UI-1 | HP/MP khong nhay layout |
| UI-3 | Fix exit position | TV5 | Cho UI-2 | Thoat game luu dung vi tri |

---

## TUAN 2 — TAO CONTENT

**Gate: Choi duoc tu Map 1 → Map 5, gap quai, noi chuyen NPC, co sound**

```
Song song:
  Leader ──── NET-2 → NET-3 → NET-4
  TV2    ──── MAP-3 → MAP-4 → MAP-5
  TV3    ──── (cho TV2 tung map) → NPC cho map do → Quest
  TV4    ──── (cho TV2 tung map) → Quai + Boss cho map do
  TV5    ──── UI-4 → UI-5 → UI-6
```

| Task ID | Ten task | Ai lam | Cho ai | Output |
|---------|----------|--------|--------|--------|
| NET-2 | Them KryoNet dependency | Leader | Khong cho | build.gradle cap nhat |
| NET-3 | Tao NetworkManager | Leader | Cho NET-2 | Singleton quan ly ket noi |
| NET-4 | Tao GameServer + GameClient | Leader | Cho NET-3 | Server lang nghe, client ket noi |
| MAP-3 | Tao Map 3: Rung sau | TV2 | Khong cho | File .tmx + dang ky MapFactory |
| MAP-4 | Tao Map 4: Duong nui da | TV2 | Cho MAP-3 | File .tmx + dang ky MapFactory |
| MAP-5 | Tao Map 5: Hang boss cuoi | TV2 | Cho MAP-4 | File .tmx + dang ky MapFactory |
| NPC-6 | Tao NPC Tho san bi thuong (Map 3) | TV3 | Cho MAP-3 | Entity config + conversation |
| NPC-7 | Tao NPC Lai buon di lac (Map 3) | TV3 | Cho MAP-3 | Entity config + shop |
| NPC-8 | Tao NPC Nguoi gac cau (Map 4) | TV3 | Cho MAP-4 | Entity config + conversation |
| NPC-9 | Tao Quest chinh 1: Diet quai duong rung | TV3 | Cho NPC-6 | quest JSON |
| NPC-10 | Tao Quest chinh 2: Dep Soi chua | TV3 | Cho NPC-9 | quest JSON |
| NPC-11 | Tao Quest chinh 3: Vuot nui tim boss | TV3 | Cho NPC-10 | quest JSON |
| BAT-3 | Tao enemy: Soi bien di (Map 3) | TV4 | Cho MAP-3 | enemies JSON |
| BAT-4 | Tao enemy: Cay quai (Map 3) | TV4 | Cho MAP-3 | enemies JSON |
| BAT-5 | Tao enemy: Golem da (Map 4) | TV4 | Cho MAP-4 | enemies JSON |
| BAT-6 | Tao enemy: Ran doc (Map 4) | TV4 | Cho MAP-4 | enemies JSON |
| BAT-7 | Tao Mini Boss: Rabite chua (Map 2) | TV4 | Khong cho | enemies JSON |
| BAT-8 | Tao Boss 1: Soi chua (Map 3) | TV4 | Cho BAT-3 | enemies JSON |
| BAT-9 | Tao Boss cuoi: Quai vat Manafall (Map 5) | TV4 | Cho MAP-5 | enemies JSON |
| UI-4 | Sound system trong AudioManager | TV5 | Khong cho | Code xu ly sound |
| UI-5 | Tim/them sound effects | TV5 | Cho UI-4 | File .wav/.mp3 |
| UI-6 | Sound settings OptionScreen | TV5 | Cho UI-5 | Slider volume SFX |

---

## TUAN 3 — KET NOI & CO-OP

**Gate: 2 may ket noi LAN, cung di map, cung danh boss**

```
Song song:
  Leader ──── NET-5 → NET-6
  TV2    ──── MAP-6 + test map
  TV3    ──── NPC-12 + ra soat conversations
  TV4    ──── BAT-10 → BAT-12 (cho NET-6)
  TV5    ──── UI-7 → UI-8
```

| Task ID | Ten task | Ai lam | Cho ai | Output |
|---------|----------|--------|--------|--------|
| NET-5 | Dong bo vi tri player 2 tren map | Leader | Khong cho | 2 nguoi thay nhau di chuyen |
| NET-6 | Dong bo battle co-op | Leader | Cho NET-5 | 2 nguoi cung danh boss |
| MAP-6 | Ket noi cong giua 5 map | TV2 | Khong cho | Di qua lai 5 map OK |
| NPC-12 | Quest phu: Giup tho san | TV3 | Khong cho | Quest phu hoat dong |
| NPC-13 | Ra soat tat ca conversation chay dung | TV3 | Cho NPC-12 | Tat ca NPC noi dung |
| BAT-10 | Can bang stats toan game | TV4 | Khong cho | Choi khong qua de/kho |
| BAT-12 | Battle ho tro 2v1 co-op | TV4 | Cho NET-6 + BAT-10 | 2 players danh 1 boss |
| UI-7 | Tao LobbyScreen (co-op) | TV5 | Khong cho | Man hinh Host/Join |
| UI-8 | Them nut Host/Join vao MenuScreen | TV5 | Cho UI-7 | Menu co 6 nut |

---

## TUAN 4 — TEST & FIX & POLISH

**Gate: Game san sang demo**

```
Song song:
  Leader ──── NET-7 + review + merge final
  TV2    ──── test map + fix
  TV3    ──── test NPC/quest + fix
  TV4    ──── test battle/balance + fix
  TV5    ──── UI-9 + UI-10 + fix UI
```

| Task ID | Ten task | Ai lam | Cho ai | Output |
|---------|----------|--------|--------|--------|
| NET-7 | Xu ly disconnect | Leader | Khong cho | Khong crash khi player 2 thoat |
| TEST-1 | Test single player dau den cuoi | TV3 + TV4 | Khong cho | Danh sach bug |
| TEST-2 | Test co-op dau den cuoi | Leader + TV2 | Khong cho | Danh sach bug |
| FIX-MAP | Fix loi map | TV2 | Cho TEST-1,2 | Map khong loi |
| FIX-NPC | Fix loi NPC/quest | TV3 | Cho TEST-1,2 | Quest chay dung |
| FIX-BAT | Fix loi battle/balance | TV4 | Cho TEST-1,2 | Can bang OK |
| FIX-UI | Fix loi UI/sound | TV5 | Cho TEST-1,2 | UI khong loi |
| UI-9 | CreditsScreen | TV5 | Khong cho | Ten nhom |
| UI-10 | Cutscene ket thuc | TV5 | Cho UI-9 | Thanh vua → credits |
| FINAL | Merge final + build release | Leader | Cho tat ca | Game chay duoc |

---

## SO DO TONG

```
TUAN 1              TUAN 2              TUAN 3             TUAN 4
──────              ──────              ──────             ──────
Fix 5 bugs     ──>  Tao 3 map moi ──>  Ket noi P2P  ──>  Test
Tim tileset    ──>  Tao 6 quai    ──>  Battle 2v1   ──>  Fix bugs
Viet hoi thoai ──>  Tao 3 boss    ──>  Lobby UI     ──>  Credits
NC KryoNet     ──>  Tao 3 quest   ──>  Can bang     ──>  Cutscene
Thiet ke items ──>  Sound system  ──>  Ket noi map  ──>  Build
               ^                  ^                  ^
            GATE 1             GATE 2             GATE 3
          (0 bug,            (choi duoc         (co-op
           co tileset)       single player)     hoat dong)
```
