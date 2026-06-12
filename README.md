# PlayRent — Sports Equipment Rental

## Quick Start

### Prerequisites
- **Java JDK 17+** ([download](https://adoptium.net))
- **MySQL Database Server** (running locally on port `3306`)
- Any modern browser

### Database Setup & Configuration

The application uses MySQL to store users, equipment catalog, and rental history.

1. **Start MySQL Server** on your machine.
2. **Configure Connection Properties**: Open [backend/db.properties](file:///c:/Users/Anees/Desktop/final%20project/playrent_fixed/backend/db.properties) and update the credentials to match your MySQL database setup:
   ```properties
   db.url=jdbc:mysql://localhost:3306/playrent_db
   db.user=root
   db.password=YOUR_MYSQL_PASSWORD_HERE
   ```
3. **Database Initialization**: The backend is configured to automatically detect if `playrent_db` exists, create it, and seed the tables with default users and equipment catalog upon launch.
   > *Optional:* If you want to manual initialize the tables, you can run the SQL script located at [backend/playrent_schema.sql](file:///c:/Users/Anees/Desktop/final%20project/playrent_fixed/backend/playrent_schema.sql).

### Run the Backend

**Windows:**
```
Double-click run.bat
```

**Mac / Linux:**
```bash
bash run-backend.sh
```

The server starts on **http://localhost:8080**

### Open the Frontend

Open `frontend/index.html` in your browser (double-click, or drag into browser).

> If you open the frontend *without* the backend running, it automatically falls back to mock data so you can still browse and test all features.

---

## Demo Accounts

| Role     | Email                    | Password  |
|----------|--------------------------|-----------|
| Customer | demo@playrent.com        | demo123   |
| Staff    | staff4451@gmail.com      | staff1155 |
| Admin    | admin4451@gmail.com      | admin1155 |

---

## Features by Role

- **Customer** — Browse catalog, book equipment, view your rental history
- **Staff** — Approve / reject / mark returned for all rentals
- **Admin** — Analytics dashboard + access to staff portal

---

## Project Structure

```
PlayRent/
├── frontend/
│   ├── index.html                  ← Landing page (hero + scroll story)
│   └── src/
│       ├── assets/images/          ← Athlete photos
│       ├── config/env.js           ← API base URL + mock toggle
│       ├── services/
│       │   ├── apiClient.js        ← REST calls + mock fallback
│       │   └── authService.js      ← Session management
│       ├── utils/
│       │   ├── constants.js        ← Equipment catalog + sport sections
│       │   └── helpers.js          ← Shared utilities (toast, dates)
│       ├── components/
│       │   ├── layout/header.js    ← Shared nav bar
│       │   └── ui/scrollStory.js   ← Scroll-jacked sport sections
│       ├── styles/globals.css      ← All styles
│       └── pages/
│           ├── auth/               ← login.html, register.html
│           ├── dashboard/          ← Customer rental history
│           ├── catalog/            ← Browse equipment
│           ├── equipment/          ← Detail + booking form
│           ├── booking/            ← Confirmation receipt
│           ├── staff/              ← Staff management portal
│           └── admin/              ← Analytics dashboard
├── backend/
│   └── src/com/sportrent/
│       ├── Main.java               ← HTTP server (port 8080)
│       ├── handler/                ← Auth, Equipment, Rental, Root
│       └── service/                ← Json, Http, StorageService
├── run.bat                         ← Windows launcher
└── run-backend.sh                  ← Mac/Linux launcher
```
