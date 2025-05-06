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





3. Add google-services.json
Go to Firebase Console

Choose your project → Project settings → Add app → Android

Download the google-services.json

Place it at:

bash
Copy
Edit
<project-root>/app/google-services.json
Also ensure build.gradle files include:

Project-level:

gradle
Copy
Edit
classpath 'com.google.gms:google-services:4.3.15' // latest
App-level:
apply plugin: 'com.google.gms.google-services'
