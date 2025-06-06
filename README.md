# PageFund: Predictive Investment Simulator for Books

## Overview
**PageFund** is a native Android application that simulates a dynamic, data-driven market where users invest virtual currency into books. Leveraging real-time metadata from the Google Books API and Firebase's serverless infrastructure, we transform cultural prediction into an interactive financial model. It is designed to reward users for accurately forecasting future literary popularity using quantitative metrics such as average rating, rating count, and temporal sentiment proxies.

---

## System Architecture
### Frontend
- **Platform**: Android (Java, Android Studio)
- **UI Composition**: Modular design using Fragments, RecyclerViews, and ConstraintLayout
- **Data Handling**: Asynchronous data binding with Java callbacks and LiveData proxies
- **UI Responsiveness**: Dynamic rendering based on Firestore snapshot listeners, leveraging Observer patterns

### Backend
- **Database**: Firebase Firestore (NoSQL, document-oriented)
- **Auth**: Firebase Authentication (JWT tokens, session management)
- **Security**: Firestore rule-based access control, context-aware reads/writes
- **Concurrency**: Firestore transaction blocks for ACID-compliant investment operations

### External APIs
- **Google Books API**: Provides book metadata, used to derive valuation factors
- **Network Layer**: HTTP requests via Java `AsyncTask` with JSON parsing through native `JSONObject` structures

---

## Data Model
- **Users Collection**
  - `uid`: string (auto-generated by Firebase Auth)
  - `balance`: double
  - `createdAt`: timestamp
- **Investments Subcollection** (`users/{uid}/investments/{bookId}`)
  - `bookId`: string
  - `title`: string
  - `author`: string
  - `avgRating`: double
  - `ratingCount`: int
  - `shares`: int
  - `purchasePrice`: double
  - `currentValuation`: double
  - `lastUpdated`: timestamp

---

## Algorithms
- **Valuation Algorithm**: Combines average rating and log-scaled rating count to generate a weighted score, favoring books with both quality and engagement.
- **Balance Validation Algorithm**: Ensures that a user's virtual currency is sufficient to perform a transaction before committing any Firestore write.
- **Transactional Logic**: Enforces atomic updates for both buying and selling books, guaranteeing consistency in concurrent sessions.
- **Snapshot-Based UI Refresh**: Uses Firestore listeners to reflect real-time changes in valuation and portfolio contents on the frontend.
- **Background Valuation Update**: A scheduled algorithm fetches external data and updates book prices without user prompts or manual refreshes.

---
## Feature Set
- 🔐 **Secure Auth Layer**: Enforced multi-step validation on email/password during signup; session token invalidation on logout or app close
- 📚 **Dynamic Book Marketplace**: Google Books API integration using real-time query responses mapped to Book objects via reflection-based deserialization
- 💸 **Investment Execution Engine**: Validates balance, updates Firestore atomically, and logs all transactions with timestamped metadata
- 🔄 **Valuation Engine**: Periodically polls for updated book metrics and recalculates market price using logarithmic weighting models
- 📊 **Portfolio Analytics Dashboard**: Fetches and aggregates investment performance in real-time with sell functionality and share-level tracking
- 🧠 **User Feedback Loop**: Contextual snackbars, toasts, and alert dialogs enhance transaction clarity and error recovery
- 🧾 **Persistent Session State**: Authentication context maintained via FirebaseAuth.getCurrentUser() and auto-cleared on session end

## Core Technical Strategies
- **Encapsulation**: Modular `Book` class with accessors/mutators to enforce encapsulation and facilitate polymorphic data handling
- **Error Management**: Centralized error catching with UI-level exception messaging and input rejection at both client and database level
- **Data Consistency**: Firestore transactions handle buy/sell atomicity and prevent dirty writes or lost updates under concurrent sessions
- **UI Scalability**: RecyclerView adapters auto-update based on snapshot deltas; supports future upgrades for pagination and lazy loading
- **Lifecycle Safety**: Background tasks are lifecycle-aware to prevent memory leaks and ensure main-thread safety post-view destruction

---

## Development Workflow Summary
- **Version Control**: Git-based iteration with frequent commits tied to individual features or bugfixes
- **Testing**:
  - Manual testing on Android Emulator
  - Test cases for every success criterion (registration, portfolio accuracy, balance integrity, valuation sync)
- **Task Management**: 50+ itemized actions tracked with estimates and timestamps (see full task breakdown)

---

## Success Criteria Fulfillment
| Priority | Requirement | Status |
|----------|-------------|--------|
| High     | Email-based signup/login with validation | ✅ Completed |
| High     | Dynamic book browsing via API | ✅ Completed |
| High     | Validated investment actions | ✅ Completed |
| High     | Real-time value updates | ✅ Completed |
| Medium   | Currency deposit support | ✅ Completed |
| Medium   | Portfolio inspection and history | ✅ Completed |
| Low      | Auto logout on app termination | ✅ Completed |

---

## Future Enhancements
- 📈 **Multiple Share Tiers**: Integrate per-investment quantity scaling and unit pricing history
- 🔍 **Advanced Search & Filtering**: Live search with RecyclerView filter hooks, debounced input, and genre-based segmentation
- 🔧 **Background Sync**: Replace AsyncTask with WorkManager for scalable background fetch operations
- 🧪 **Automated Testing**: Add Espresso/JUnit5 test suites for authentication, navigation, and transaction validation
- 🌐 **Multilingual Support**: Add localization via resource bundles for broader accessibility

---

## Technical References
- [Firebase Authentication](https://firebase.google.com/docs/auth)
- [Firebase Firestore Transactions](https://firebase.google.com/docs/firestore/manage-data/transactions)
- [Google Books API](https://developers.google.com/books)
- [Android Architecture Components](https://developer.android.com/topic/libraries/architecture)
- [AsyncTask (deprecated)](https://developer.android.com/reference/android/os/AsyncTask)

---

## Contact
**Developer**: Rohan Muppa  
**Program**: B.S. Computer Engineering + Mathemetics, Purdue University  
**LinkedIn**: [linkedin.com/in/rohanmuppa](https://linkedin.com/in/rohanmuppa)

> "PageFund is the future. Absolutely brilliant." – *Timothy Edward Gray, Client*
