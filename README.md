# 📝 GoodNote Clone — Android Handwriting Editor

A handwriting editor built on Android using **Jetpack Compose** custom touch logic. Inspired by the Goodnotes app, the project supports stylus input, dynamic canvas handling, and a rich user experience for handwritten notes.

_Current Status: Actively developed — new features and refactors are being added!_

---

## 🚀 New & Notable Features

- 🏠 **Homepage & Navigation** (NEW)
  - Recently added a structured home screen for managing and accessing notes.
  - Seamless navigation between home and editor views.

- 📝 **Handwriting Input** via stylus
  - Natural handwriting experience optimized for stylus and finger input.

- 🤏 **Multi-touch Zoom & Scroll**
  - Pinch-to-zoom and scroll with finger gestures for a fluid navigation.

- 🧽 **Erasing by Stroke Intersection**
  - Erase strokes by intersecting with the eraser tool.

- 🔄 **Undo/Redo System**
  - Behavior stack-based undo/redo for all editing actions.

- 🖼️ **Image Insertion & Caching** (NEW)
  - Import images into notes with in-app image caching for performance.

- 🧩 **Dynamic Canvas Expansion**
  - Canvas automatically grows as you write near the edge, so you never run out of space.

- 🎨 **Pen Color & Width Selection**
  - Change pen color and width with intuitive scroll gestures.

- 🗃️ **Local Database Integration** (NEW)
  - Built with Room for persistent storage of notes, pages, strokes, and regions.

- 🖱️ **Virtual Camera Scrolling**
  - Move "camera" (viewport) over the canvas by moving strokes rather than the screen.

- 🖥️ **Full Screen Mode**
  - Toggle full-screen for distraction-free writing.

- 🔍 **Smooth Scaling and Zooming**
  - Strokes and objects remain crisp and scale-aware at any zoom level.

- 🧩 **Modular Architecture** (NEW)
  - Dependency Injection via Koin, separating concerns for easy testing and scaling.
  - Repositories and ViewModels for major domains (Page, Stroke, Region).

- 🏗️ **Refactored Codebase & Bug Fixes** (NEW)
  - Major refactors for better maintainability and extensibility.
  - Ongoing bug fixes and code improvements.

---

## 🛠️ Tech Stack

- **Kotlin**
- **Jetpack Compose** (UI)
- **Room** (Database)
- **Koin** (DI)
- **Coil** (Image loading)
- **AndroidX Navigation**

---


## 📸 Demo
![image](https://github.com/user-attachments/assets/5cd0792e-ade5-4e06-845d-f246dae979d9)
![image](https://github.com/user-attachments/assets/f3485211-6238-4664-b459-2ea89932ad3c)
![image](https://github.com/user-attachments/assets/1f844231-a90b-44b9-80ae-b20ddd7e66fe)
---

---

## 👨‍💻 Author

**Tran Minh Viet** | UEH | FPTU  
University student, aspiring Android engineer.  
Built this project to explore real-world gesture handling and advanced canvas logic.

Email: trmviet0801@gmail.com

---

## 🗂️ How to Run

1. **Clone the repository:**
    ```sh
    git clone https://github.com/trmviet0801/GoodNote.git
    ```
2. **Open in Android Studio** (Flamingo or later recommended)
3. **Build & Run** on an emulator or physical device (minSdk 24)

---

## 🤝 Contributions

Contributions and suggestions are welcome!  
Open issues or pull requests for bugs, features, or enhancements.

---

## 📄 License

This project is licensed under the MIT License.  
See the [LICENSE](./LICENSE) file for details.

---

## ⭐ Support

If you find this project helpful or impressive:
- ⭐ Star the repo
- 📬 Reach out for collaboration

---
