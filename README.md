# FishTrail

**FishTrail** is a modern Android fishing logbook application designed to help anglers record, manage, and explore their fishing activities in one place.

The application provides anglers with tools to record catches, save fishing locations, view their fishing history, and use an interactive map to find and select fishing locations.

---

## Features

### Authentication

* User registration
* User login
* Input validation
* Username validation
* Password validation
* Registration success and error feedback
* Login validation
* User-specific application experience

### Catch Logging

Users can record details about their catches, including:

* Fish species
* Weight
* Length
* Fishing location name
* GPS coordinates
* Date and time of the catch

Saved catches are stored locally and can be viewed later through the History section.

### Fishing Location & GPS

FishTrail uses the device's location services to help users determine their current fishing location.

The application supports:

* Requesting location permission
* Retrieving the user's current GPS coordinates
* Displaying latitude and longitude
* Selecting locations on the map
* Saving fishing location information
* Using location information when recording catches

### Interactive Map

The application includes an interactive map designed to help anglers work with fishing locations.

Users can:

* View their current location
* Move around the map
* Select locations
* Search for fishing locations
* View location information
* Use map-based location selection when recording fishing activities
* Use Google Maps direction capabilities

### Fishing History

The History section allows users to review previously recorded catches.

Saved information can include:

* Species
* Weight
* Length
* Location
* GPS coordinates
* Catch date

### Settings

The Settings section provides a dedicated area for application preferences and configuration.


The application is structured so that additional languages can be added in the future.
FishTrail uses local database storage to allow important fishing information to remain available without requiring a continuous internet connection.
The application is designed with offline usage and future synchronisation capabilities in mind.


## Application Structure

The project follows a modular package structure to keep the application organised and maintainable.

```text
FishTrailApp/
│
├── app/
│   └── src/
│       └── main/
│           ├── java/
│           │   └── com/
│           │       └── fishtrail/
│           │           └── app/
│           │               │
│           │               ├── auth/
│           │               ├── catchlog/
│           │               ├── data/
│           │               ├── history/
│           │               ├── home/
│           │               ├── map/
│           │               ├── settings/
│           │               ├── viewmodel/
│           │               └── MainActivity.kt
│           │
│           └── res/
│               ├── drawable/
│               ├── mipmap/
│               ├── values/
│               └── xml/
│
├── gradle/
├── build.gradle.kts
├── settings.gradle.kts
├── gradle.properties
├── gradlew
├── gradlew.bat
└── README.md
```

---

## Technology Stack

FishTrail is built using modern Android development technologies.

| Technology              | Purpose                                   |
| ----------------------- | ----------------------------------------- |
| Kotlin                  | Primary programming language              |
| Jetpack Compose         | User interface development                |
| Material 3              | UI components and design system           |
| Android SDK             | Android application development           |
| Android Studio          | Development environment                   |
| Room                    | Local database storage                    |
| ViewModel               | Application state management              |
| Navigation Compose      | Screen navigation                         |
| Google Maps             | Interactive mapping and location features |
| GPS / Location Services | User location functionality               |
| Gradle                  | Project build system                      |

---

## Architecture

FishTrail follows an organised architecture based around Android's modern application development practices.

The application separates responsibilities between:

* **UI screens** – Display application content and collect user input.
* **ViewModels** – Manage UI state and application logic.
* **Data layer** – Handles local database operations.
* **DAO interfaces** – Provide access to stored database information.
* **Navigation** – Controls movement between application screens.

This structure makes the application easier to maintain, test, and extend.

---

## Main Application Screens

FishTrail currently contains the following main sections:

### Authentication

Handles:

* Registration
* Login
* Validation
* Authentication feedback

### Home

Provides the main entry point to the application after authentication.

### Catch Log

Allows users to create and save fishing records.

### Map

Provides location-based functionality and an interactive map.

### History

Displays previously saved fishing records.

### Settings

Provides application settings and preferences.

---

## Database

FishTrail uses a local database to store application information.

The database is used to persist fishing records so that information is not lost when the application is closed.

The data layer includes components such as:

```text
Catch.kt
CatchDao.kt
CatchViewModel.kt
CatchViewModelFactory.kt
```

The database architecture can be extended in the future to support additional entities and synchronisation with a remote REST API.

---

## Location Services

FishTrail uses Android location services to obtain GPS information.

When the user grants location permission, the application can retrieve the device's location and use it for fishing-related functionality.

Location information can be represented using:

```text
Latitude
Longitude
Location Name
```

Users can also select locations through the map interface.

---

## Permissions

FishTrail may request Android permissions required for specific functionality.

Depending on the features being used, these can include:

* Location permission
* Internet access
* Notification permission

Users remain in control of whether location access is granted to the application.

---

## User Input Validation

FishTrail includes validation to help ensure that users enter appropriate information.

Validation is applied to areas such as:

* Registration
* Login
* Catch logging
* Required fields
* Numeric values
* Usernames
* Passwords

The purpose of validation is to prevent invalid or incomplete information from being stored in the application.

---

## Installation

### Requirements

To build and run FishTrail, you will need:

* Android Studio
* Android SDK
* JDK 21
* An Android device or Android Emulator
* Internet connection for downloading project dependencies
* A Google Maps API key for map functionality

### Clone the Repository

```bash
git clone [<YOUR-GITHUB-REPOSITORY-URL>](https://github.com/CeejayAlughere/FishTrailApp.git )
```

Navigate into the project:

```bash
cd FishTrailApp
```

Open the project in Android Studio.

Allow Gradle to synchronise the project and download the required dependencies.

---

## Google Maps API Configuration

FishTrail uses Google Maps functionality for its location features.

A valid Google Maps API key must be configured for the map functionality to work correctly.

The API key should **not** be committed directly to a public Git repository.

Before publishing the project, make sure sensitive credentials and API keys are protected using an appropriate configuration method.

---

## Running the Application

1. Open **Android Studio**.
2. Select **Open**.
3. Open the `FishTrailApp` project.
4. Allow Gradle to synchronise.
5. Connect an Android device or start an Android Emulator.
6. Select the application configuration.
7. Click **Run**.

The application should launch on the selected Android device or emulator.

---

## User Workflow

A typical FishTrail user workflow is:

```text
Register
   ↓
Login
   ↓
Home
   ↓
Record Catch
   ↓
Select / Enter Fishing Location
   ↓
Save Catch
   ↓
View History
```

Users can also access the Map and Settings sections from the application's navigation.

---

String resources are separated from application logic so that translations can be maintained independently from the user interface code.

This approach also makes it easier to add additional languages in future versions.

---


## Project Goals

The main goals of FishTrail are to:

1. Provide anglers with an easy way to record fishing catches.
2. Make fishing records accessible from one central application.
3. Provide location-based fishing functionality.
4. Allow users to review their fishing history.
5. Provide a modern and accessible Android interface.

---

## Development

FishTrail is developed using Android Studio and Kotlin.

The project makes use of modern Android development practices including:

* Jetpack Compose
* Material 3
* ViewModels
* Room database
* Navigation Compose
* Android location services
* Google Maps
* Resource-based localisation

The project structure is designed to allow individual features to be developed and maintained independently.

---

## Testing

Testing is performed throughout development to verify that the application's functionality works as expected.

Areas tested include:

* User registration
* User login
* Input validation
* Navigation
* Catch creation
* Catch storage
* Catch history
* Location permissions
* GPS coordinates
* Map functionality
* Database operations
* Application builds

Additional automated and manual testing can be added as development continues.

---

## Security Considerations

FishTrail is designed with user data protection in mind.

Important security considerations include:

* Validating user input
* Avoiding unnecessary storage of sensitive information
* Protecting API keys
* Using secure authentication practices
* Avoiding hard-coded credentials
* Protecting database access
* Applying appropriate Android permissions

---

## Project Status

**Development Status: Active Development**

FishTrail's core application functionality has been implemented, including authentication, navigation, catch logging, local database storage, history, settings, GPS functionality, and map functionality.

Additional features and improvements are being developed as the project progresses.

---


## License

This project is currently intended for educational and development purposes.

A formal open-source license can be added if the project is released publicly.
