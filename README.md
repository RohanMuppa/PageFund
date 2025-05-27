# BookVest: Predictive Market Simulation for Books (Android)

## Overview
**BookVest** is a mobile application that simulates a virtual investment market for books. Users predict the popularity of books and invest virtual currency. The app is developed using Java (Android Studio) and Firebase (Firestore and Authentication), with real-time valuation changes driven by data from the Google Books API.

## System Architecture
- **Frontend**: Android app using Java and XML for UI components.
- **Backend**: Firebase Firestore for data storage and Firebase Authentication for secure login.
- **APIs**: Google Books API to retrieve book metadata and popularity indicators.

### Data Model
- `users/{userId}`: Stores user credentials and balance.
- `users/{userId}/investments/{bookId}`: Stores invested amount and book details.
- Uses Firestore's subcollections and document snapshots for atomic operations and consistent data handling.

## Features
- **Authentication**: Sign-up and sign-in with Firebase, includes input validation.
- **Real-Time Book Listings**: Books displayed dynamically, retrieved via Google Books API.
- **Investment Transactions**: Users invest in books, with backend validation against current balance.
- **Dynamic Valuation**: Book prices are determined based on a combination of average rating and rating count, updated frequently.
- **Portfolio Management**: Real-time display of current investments, their valuations, and sell capabilities.
- **Session Handling**: App enforces auto logout upon session end or application close.

## Implementation Techniques
- **AsyncTask**: Handles non-blocking API requests.
- **Encapsulation**: Book objects are managed with getters and setters.
- **Firestore Transactions**: Ensures atomic operations when updating user currency or investment values.
- **Composite JSON Parsing**: Parses nested API responses for book valuation inputs.
- **Modular UI Design**: Activities and fragments are used for each screen, maintaining a clean separation of concerns.

## Success Criteria
| Priority | Requirement |
|----------|-------------|
| High     | Register/login via email with validation |
| High     | View list of books dynamically fetched from API |
| High     | Invest in books only if sufficient currency is available |
| High     | Update investment values from external API in real-time |
| Medium   | Add currency to account balance |
| Medium   | View portfolio and its current valuation |
| Low      | Auto logout upon closing the application |

## Testing Highlights
- Registration rejects invalid credentials.
- Book list loads and refreshes as expected.
- Investment denied when funds are insufficient.
- Values refresh dynamically and match source data.
- Logouts triggered on app termination.

## Development Timeline
- 72.5 hours logged.
- Covered 50+ discrete tasks including:
  - UI/UX planning
  - Authentication setup
  - Firebase integration
  - Investment and transaction validation
  - API integration
  - Testing and error handling

## Planned Improvements
- **Share Quantity Support**: Allow buying multiple shares per book.
- **Search Functionality**: Enable real-time book search via RecyclerView.
- **Background Processing**: Use WorkManager to replace AsyncTask for background tasks.
- **Enhanced Testing**: Incorporate unit and UI testing with JUnit and Espresso.

## Libraries & Tools
- Firebase SDK
- Google Books API
- Android Studio + Emulator
- Java 8 (Android-compatible features)
- XML for UI components

## References
- [Firebase Authentication](https://firebase.google.com/docs/auth)
- [Firebase Firestore](https://firebase.google.com/docs/firestore)
- [Google Books API](https://developers.google.com/books)
- [Android Emulator Guide](https://developer.android.com/studio/run/emulator)

## Author
**Rohan Muppa**  
M.S. Computer Engineering – Purdue University  
[LinkedIn](https://www.linkedin.com/in/rohanmuppa)

> "This app is amazing. I’m excited for the future of this app." – *Timothy Edward Gray, Client*
