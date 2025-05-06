# Online_Parking_App

This is an Android-based Smart Parking App developed using Java, MVVM Architecture, Room Database, and Firebase. It includes two panels:

- 👤 **User Panel** – For regular users to view, book, and manage parking slots.
- 🛠️ **Admin Panel** – For parking owners to add/manage plots and slots with location via Google Maps.

---

## 🗂 Features

- 🔐 User Authentication (Email/Phone) using Firebase
- 🗺️ Google Maps Integration for adding plots
- 📦 Firebase Realtime Database + Room Database (offline support)
- 📸 Image support for plots and slots
- 🧠 MVVM Architecture with ViewModel, Repository, and LiveData
- 📶 Internet connectivity check + Background Services

---

## 📲 Setup Instructions

### 1. Clone the Repository

```bash
git clone https://github.com/Abhishek-Comps-Engineer/Online_Parking_App.git



---

## 🗺️ 2. Add Google Maps API Key to Manifest

In your `AndroidManifest.xml` (usually in `app/src/main/AndroidManifest.xml`), add this line **within the `<application>` tag**:

```xml
<meta-data
    android:name="com.google.android.geo.API_KEY"
    android:value="YOUR_GOOGLE_MAPS_API_KEY_HERE"/>





3. ## 🔧 Firebase Setup


### 1️⃣ Add `google-services.json`
- Go to [Firebase Console](https://console.firebase.google.com/)
- Select your project → Project Settings → Add App → Android
- Download the `google-services.json`
- Place it in your project at:

### 2️⃣ Update Gradle Files

**Project-level `build.gradle`:**
```gradle
buildscript {
    dependencies {
        classpath 'com.google.gms:google-services:4.3.15' // Use latest
    }
}


App-level:
apply plugin: 'com.google.gms.google-services'







...........................................................................