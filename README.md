# Finance Tracker (Android)

A sleek, modern Android application built to seamlessly track, categorize, and visualize your financial transactions. Built entirely with Jetpack Compose, this app allows you to automatically import bank statements, auto-categorize your spending based on custom rules, and monitor your monthly habits using beautiful Material 3 charts.

## Features

- **Smart Statement Import**: Import CSV statements directly from major banks (American Express, Scotiabank, Rogers Bank, etc.) with custom tailored parsers that handle the heavy lifting.
- **Auto-Categorization Engine**: Say goodbye to manual entry! Create custom categories (e.g. "Dining", "Groceries", "Subscriptions") and assign keyword filters (e.g. "Uber Eats", "Walmart", "Netflix"). The app will instantly scan and assign incoming transactions to the right buckets.
- **Dynamic Dashboard**: Visualize your spending habits through interactive pie charts and breakdowns that update in real-time based on the month and year.
- **Centralized Data Management**: Keep your data safe with full SQLite database support (via Android Room). Includes easy 1-click database backup & restore functionality.
- **Premium UI/UX**: Designed using Google's modern Material 3 guidelines, featuring smooth micro-animations, glassmorphic UI elements, customizable themes, and robust scrollable views.

## Tech Stack

- **UI Framework**: [Jetpack Compose](https://developer.android.com/jetpack/compose) (Material 3)
- **Architecture**: MVVM (Model-View-ViewModel)
- **Local Database**: [Room Database](https://developer.android.com/training/data-storage/room) (SQLite)
- **Concurrency**: Kotlin Coroutines & Flow
- **Language**: 100% Kotlin

## Getting Started

### Prerequisites
- [Android Studio](https://developer.android.com/studio) (Iguana or newer recommended)
- Minimum SDK: API 26 (Android 8.0)

### Installation

1. **Clone the Repository**
   ```bash
   git clone https://github.com/your-username/Finance-Tracker.git
   ```
2. **Open the Project**
   Launch Android Studio, select **Open**, and navigate to the folder where you cloned the repository.
3. **Sync & Build**
   Allow Gradle to sync the dependencies. If Android Studio prompts you to update any plugins or tools, go ahead and accept.
4. **Run the App**
   Connect a physical Android device or start an Emulator, and hit the **Run** button (the green play arrow) in Android Studio!

## Usage Guide

### 1. Data Management (Settings)
Start in the **Settings** tab. Here, you can click "Load Default Categories" to instantly inject a comprehensive list of default spending categories and keyword filters (like Amazon, Starbucks, etc.). 

### 2. Importing Transactions
Navigate to the **Dashboard** and tap "Import CSV". Select a statement from your phone's file explorer. The app will automatically detect whether it's an AMEX, Rogers, or Scotiabank format and pull in all the records.

### 3. Managing Auto-Categorization
Head over to the **Settings** tab to view your Auto-Categorization Rules. Here you can add new keyword tags, edit category names, or delete rules entirely. Any changes made here can be instantly applied to your history by hitting "Re-Categorize All Transactions".

## License
This project is for personal use and is not currently licensed for public distribution.
