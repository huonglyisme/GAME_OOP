# THIẾT KẾ MẠNG LAN CO-OP - ECHOES OF MANAFALL

## Tổng quan

Mở rộng chế độ co-op qua mạng LAN cho phép 2 người chơi cùng tham gia:
- Di chuyển trên cùng map
- Phối hợp chiến đấu theo lượt (2v1, 2v2)
- Trade items giữa 2 players
- Lưu/tải co-op session

## Thư viện sử dụng

**KryoNet** - thư viện networking phổ biến cho Java/LibGDX
- Hỗ trợ TCP + UDP
- Serialization tự động với Kryo
- Phù hợp cho LAN (low latency)

## Kiến trúc: Host-Authoritative Client/Server

```
┌─────────────────┐         LAN          ┌─────────────────┐
│   Player 1      │ ◄──── TCP/UDP ─────► │   Player 2      │
│   (Host/Server) │                       │   (Client)       │
│                 │                       │                  │
│ ┌─────────────┐ │                       │ ┌──────────────┐ │
│ │ GameServer  │ │  PlayerMovePacket     │ │ GameClient   │ │
│ │ (authorit.) │ │◄─────────────────────►│ │ (sends input)│ │
│ └─────────────┘ │  SyncStatePacket      │ └──────────────┘ │
│                 │  BattleActionPacket   │                  │
│ ┌─────────────┐ │  TradePacket          │ ┌──────────────┐ │
│ │ Game Loop   │ │                       │ │ Game Loop    │ │
│ │ (full game) │ │                       │ │ (render only)│ │
│ └─────────────┘ │                       │ └──────────────┘ │
└─────────────────┘                       └─────────────────┘
```

**Host** là authoritative: xử lý toàn bộ game logic, gửi kết quả cho Client.
**Client** gửi input, nhận state updates để render.

## Packet Definitions

### PlayerMovePacket
```java
public class PlayerMovePacket {
    public int playerId;
    public float x, y;
    public String direction;  // UP, DOWN, LEFT, RIGHT
    public String state;      // IDLE, WALKING
}
```

### BattleActionPacket
```java
public class BattleActionPacket {
    public int playerId;
    public String action;     // ATTACK, MAGIC, ITEM, FLEE
    public String targetId;
    public String itemId;     // nếu action = ITEM
}
```

### SyncStatePacket
```java
public class SyncStatePacket {
    public float player1X, player1Y;
    public float player2X, player2Y;
    public int player1HP, player2HP;
    public int player1MP, player2MP;
    public String currentMap;
    public String gameState;  // RUNNING, BATTLE, PAUSED
}
```

### TradePacket
```java
public class TradePacket {
    public int fromPlayerId;
    public int toPlayerId;
    public String itemTypeId;
    public int quantity;
    public boolean accepted;
}
```

## Flow chính

### Kết nối
1. Host bấm "Host Game" → GameServer start, lắng nghe port 54555 (TCP) + 54777 (UDP)
2. Client bấm "Join Game" → nhập IP LAN → GameClient connect
3. Server confirm → cả 2 vào LobbyScreen
4. Host bấm "Start" → cả 2 vào GameScreen

### Di chuyển đồng bộ
1. Client nhấn phím → gửi `PlayerMovePacket` cho Server
2. Server validate (collision check) → cập nhật vị trí
3. Server gửi `SyncStatePacket` cho Client (30fps)
4. Client render player 2 tại vị trí nhận được

### Battle co-op
1. Player 1 hoặc 2 chạm enemy → Server khởi tạo battle cho cả 2
2. Turn order: tính speed ratio cho cả 2 players + enemies
3. Mỗi turn: player active gửi `BattleActionPacket`
4. Server xử lý damage → gửi kết quả cho cả 2

### Trade items
1. Player mở trade request → gửi `TradePacket`
2. Player kia nhận notification → accept/decline
3. Nếu accept → Server validate inventory space → swap items

## Files cần tạo

```
core/src/main/java/com/gdx/game/network/
├── NetworkManager.java
├── GameServer.java
├── GameClient.java
├── NetworkProtocol.java
├── NetworkObserver.java
├── NetworkSubject.java
└── packets/
    ├── PlayerMovePacket.java
    ├── BattleActionPacket.java
    ├── SyncStatePacket.java
    └── TradePacket.java
```

## Dependency cần thêm

```toml
# gradle/libs.versions.toml
kryonet = "2.22.9"

[libraries]
kryonet = { group = "com.esotericsoftware", name = "kryonet", version.ref = "kryonet" }
```

## Trạng thái: CHƯA TRIỂN KHAI

Tài liệu này là thiết kế ban đầu. Sẽ được cập nhật khi bắt đầu Sprint 3.
