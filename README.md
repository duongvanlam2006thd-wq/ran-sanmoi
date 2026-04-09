# 🐍 Snake Game — Rắn Săn Mồi Nâng Cấp

> **Bài tập lớn cuối kỳ môn Lập trình Java**

---

## 👥 Thông tin nhóm (Team Members)

| STT | Họ và Tên | Mã Sinh Viên | Vai trò / Nhiệm vụ | Link GitHub Cá Nhân |
|---|---|---|---|---|
| 1 | Dương Văn Lâm (Nhóm trưởng) | 3120225077 | Code view, Main, Báo cáo, Hightscore, README.md  | [GitHub](https://github.com/duongvanlam2006thd-wq/ran-san-moii) |
| 2 | Bùi Huy Bảo | 3120225009 | Code controller | [GitHub](https://github.com/buihuybao250907-ship-it/ran-san-moi) |
| 3 | Phan Lê Huy Hoàng | 3120225056 | Code model (Snake, Obstacle, Gamestate, Foodx) | [GitHub](https://github.com/phanlehuyhoang293-ops/ran-san-moi) |

---

## 📝 Giới thiệu dự án (Description)

Đây là game **Rắn Săn Mồi (Snake)** kinh điển được xây dựng bằng Java Swing, nâng cấp với hệ thống level, chướng ngại vật ngẫu nhiên và lưu trữ điểm cao. Dự án áp dụng mô hình **MVC** để tách biệt rõ ràng giữa dữ liệu, giao diện và logic điều khiển, giúp code dễ bảo trì và mở rộng.

---

## ✨ Các chức năng chính (Features)

- [x] Điều khiển rắn bằng phím mũi tên hoặc **WASD**
- [x] Ăn food → tăng điểm + rắn dài thêm
- [x] Hệ thống **Level** — cứ 10 điểm lên 1 cấp
- [x] Tốc độ tự động tăng theo level
- [x] **Chướng ngại vật** xuất hiện ngẫu nhiên, tăng dần theo level (16 → 60 blocks)
- [x] Thêm chướng ngại vật giữa chừng mỗi 2 miếng ăn cùng level
- [x] Va chạm tường / thân rắn / chướng ngại vật → **Game Over**
- [x] Lưu & đọc **High Score** vào file `highscore.txt` (File I/O)
- [x] **Best Score** cập nhật trực tiếp trên HUD trong khi chơi
- [x] Nút **Restart** và phím tắt **R** để chơi lại
- [x] Phím **P** để Pause / Resume
- [x] Đổi màu rắn theo level (5 màu cycling)
- [x] Hiệu ứng đồ họa: gradient thân rắn, mắt xoay theo hướng, đá 3D có bóng đổ

---

## 💻 Công nghệ & Thư viện sử dụng (Technologies)

* **Ngôn ngữ:** Java (JDK 8+)
* **Giao diện:** Java Swing, AWT (`paintComponent`, `JPanel`, `JFrame`)
* **Game Loop:** `javax.swing.Timer`
* **Lưu trữ:** File I/O (`BufferedReader` / `BufferedWriter`) — file `highscore.txt`
* **Công cụ:** Git, GitHub, IntelliJ IDEA / VS Code

> ⚠️ Không sử dụng bất kỳ thư viện bên ngoài nào — chỉ Java SE thuần.

---

## 📂 Cấu trúc thư mục (Project Structure)

Mã nguồn tổ chức theo mô hình **MVC (Model — View — Controller)**:

```
📦 snakegame
 ┣ 📂 src
 ┃  ┣ 📂 model
 ┃  ┃  ┣ 📜 SnakeModel.java      # Thân rắn, hướng đi, grow()
 ┃  ┃  ┣ 📜 FoodModel.java       # Vị trí thức ăn, spawn ngẫu nhiên
 ┃  ┃  ┣ 📜 GameState.java       # Score, Level, HighScore, File I/O
 ┃  ┃  ┗ 📜 ObstacleModel.java   # Chướng ngại vật theo level
 ┃  ┣ 📂 view
 ┃  ┃  ┗ 📜 GamePanel.java       # Toàn bộ đồ họa (extends JPanel)
 ┃  ┣ 📂 controller
 ┃  ┃  ┗ 📜 GameController.java  # Game loop, KeyListener, va chạm
 ┃  ┗ 📜 Main.java               # Entry-point khởi động ứng dụng
 ┣ 📂 bin                        # Compiled .class files (gitignored)
 ┣ 📜 compile.bat                # Script build & run (Windows)
 ┣ 📜 .gitignore
 ┗ 📜 README.md
```

---

## 🚀 Hướng dẫn chạy (How to Run)

### Yêu cầu
- Java JDK 8 trở lên đã cài đặt
- Kiểm tra: `java -version`

### Cách 1 — Script (Windows)
```cmd
cd snakegame
compile.bat
```

### Cách 2 — Thủ công
```bash
# Bước 1: Compile
javac -d bin src/model/*.java src/view/*.java src/controller/*.java src/Main.java

# Bước 2: Chạy
java -cp bin Main
```

### Cách 3 — IntelliJ IDEA / VS Code
1. Mở thư mục `snakegame`
2. Đánh dấu `src/` là **Sources Root**
3. Mở `Main.java` → nhấn **Run ▶**

---

## 🎮 Điều khiển (Controls)

| Phím | Tác dụng |
|---|---|
| `↑` `↓` `←` `→` hoặc `W` `A` `S` `D` | Di chuyển rắn |
| `P` | Pause / Resume |
| `R` hoặc nút Restart | Chơi lại |

---
