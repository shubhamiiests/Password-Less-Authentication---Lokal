# PassLess Authentication App (OTP Based)

This is a passwordless authentication Android app built using Jetpack Compose, MVVM, Hilt, Coroutines, and Firebase Analytics.

Instead of password, user login using email + OTP, which is valid for limited time. App also maintain user session and track important analytics events.

This project is mainly for learning clean architecture, Compose UI, and real-world auth flow.

# Features

1. Login using Email

2. 6-digit OTP based authentication

3. OTP expiry timer (60 seconds)

4. Resend OTP with cooldown

5. Limited OTP attempts

6. Session management (login / logout)

7. Firebase Analytics integration

8. Modern UI using Material 3 + Jetpack Compose

## Tech Stack

* Kotlin

* Jetpack Compose

* MVVM Architecture

* Hilt (Dependency Injection)

* Kotlin Coroutines & Flow

* DataStore Preferences

* Firebase Analytics

* Material 3

## Architecture Overview

This app follow Clean Architecture with clear separation of layers.

-> presentation (UI + ViewModels)
-> domain (UseCases + Managers)
-> data (Repository + DataStore)

Flow example:
# UI → ViewModel → UseCase → Manager → Repository → DataStore


No UI directly talks to data layer.

*** Authentication Flow

1. User enters email

2. OTP is generated (valid for 60 sec)

3. User enter OTP

4. OTP is validated

4. Session is created

5. User navigates to Session screen

6. On logout, session is cleared

*** OTP Logic

OTP length = 6 digits

OTP expiry = 60 seconds

Max attempts = 3

OTP stored in-memory (for demo purpose)

Timer updates UI every second

# Firebase Analytics Events

Firebase Analytics is used to track user behavior.

Logged Events:
Event	When it fires
otp_generated	When OTP is sent
otp_validation_success	When OTP is correct
otp_validation_failure	Wrong / expired OTP
user_logout	When user logout
Event Params Example:
{
  "email_domain": "gmail.com",
  "attempts_remaining": 2,
  "session_duration_seconds": 180
}


# Project Structure
app/src/main/java/com/example/passlessauthethication/
```
├── PassLessApp.kt
├── di/
│   ├── AppModule.kt
│   └── AnalyticsModule.kt
├── data/
│   ├── model/
│   │   ├── OtpData.kt
│   │   └── SessionData.kt
│   ├── repository/
│   │   ├── OtpRepository.kt
│   │   └── SessionRepository.kt
│   └── local/
│       └── PreferencesManager.kt
├── domain/
│   ├── usecase/
│   │   ├── GenerateOtpUseCase.kt
│   │   ├── ValidateOtpUseCase.kt
│   │   └── ManageSessionUseCase.kt
│   └── manager/
│       └── OtpManager.kt
├── presentation/
│   ├── navigation/
│   │   └── Navigation.kt
│   ├── login/
│   │   ├── LoginScreen.kt
│   │   ├── LoginViewModel.kt
│   │   └── LoginState.kt
│   ├── otp/
│   │   ├── OtpScreen.kt
│   │   ├── OtpViewModel.kt
│   │   └── OtpState.kt
│   ├── session/
│   │   ├── SessionScreen.kt
│   │   ├── SessionViewModel.kt
│   │   └── SessionState.kt
│   └── components/
│       ├── CustomTextField.kt
│       ├── CustomButton.kt
│       └── OtpInputField.kt
├── analytics/
│   ├── AnalyticsLogger.kt
│   └── AnalyticsEvent.kt
└── ui/
    └── theme/
        ├── Color.kt
        ├── Theme.kt
        └── Type.kt



