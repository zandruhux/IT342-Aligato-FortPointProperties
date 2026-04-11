# Fort Point Properties - Frontend Folder Structure

## Overview

This document outlines the reorganized folder structure for the Fort Point Properties React + Vite web application. The structure is designed to support 4 user roles: Guest/Public Users, Registered Users, Agents, and Admins.

---

## Folder Structure

```
src/
│
├── api/
│   ├── client.js                              (Axios instance with auth headers)
│   ├── auth.js                                (Existing auth API calls)
│   ├── endpoints/                             (Organization by domain)
│   │   ├── auth.js
│   │   ├── properties.js
│   │   ├── favorites.js
│   │   ├── messages.js
│   │   ├── careers.js
│   │   ├── admin.js
│   │   └── agent.js
│   └── interceptors/                          (JWT refresh, error handling)
│       ├── tokenRefresh.js
│       └── errorHandler.js
│
├── assets/
│   ├── FortPointProperties_Logo.jpg           (Existing logo)
│   ├── property.png                           (Existing property image)
│   ├── images/                                (Additional images)
│   ├── icons/                                 (SVG icons, logos)
│   └── constants/
│       └── branding.js                        (Color schemes, brand constants)
│
├── components/                                (Dumb, reusable UI components only)
│   ├── common/
│   │   ├── Header.jsx                         (Already here)
│   │   ├── HeroSection.jsx                    (Already here)
│   │   ├── Button.jsx                         (Reusable button)
│   │   ├── Card.jsx                           (Reusable card container)
│   │   ├── Modal.jsx                          (Reusable modal)
│   │   └── Form/
│   │       ├── Input.jsx                      (Form input field)
│   │       ├── Select.jsx                     (Form select dropdown)
│   │       └── FormGroup.jsx                  (Form wrapper)
│   ├── shared/
│   │   ├── Spinner.jsx                        (Loading spinner)
│   │   ├── ErrorBoundary.jsx                  (Error boundary wrapper)
│   │   ├── ConfirmDialog.jsx                  (Confirmation modal)
│   │   └── Toast.jsx                          (Toast notifications)
│   ├── layout/
│   │   ├── Sidebar.jsx                        (Role-specific sidebar)
│   │   ├── Navigation.jsx                     (Navigation component)
│   │   └── PageHeader.jsx                     (Page header with breadcrumbs)
│   └── dashboard/
│       └── StatsSection.jsx                   (Already here)
│
├── context/
│   ├── AuthContext.jsx                        (User role, JWT, permissions)
│   ├── AuthProvider.jsx                       (Wrapper for auth state)
│   └── NotificationContext.jsx                (Toast notifications state)
│
├── features/                                  (Domain-specific, logic-heavy modules)
│   ├── auth/
│   │   ├── components/
│   │   │   ├── LoginForm.jsx                  (Moved here from components/common)
│   │   │   ├── RegistrationForm.jsx           (Moved here from components/common)
│   │   │   └── ForgotPasswordForm.jsx         (Future)
│   │   ├── hooks/
│   │   │   ├── useLogin.js
│   │   │   ├── useRegister.js
│   │   │   └── useForgotPassword.js
│   │   └── services/
│   │       └── authService.js
│   │
│   ├── properties/
│   │   ├── components/
│   │   │   ├── PropertyCard.jsx
│   │   │   ├── PropertyDetailsExpanded.jsx
│   │   │   ├── UnitPricingTable.jsx           (3-column pricing per SDD)
│   │   │   ├── PitchReadySection.jsx          (Agent-specific)
│   │   │   ├── DeveloperResourcesSection.jsx
│   │   │   └── PropertyForm.jsx
│   │   ├── hooks/
│   │   │   ├── useProperties.js
│   │   │   ├── usePropertyDetail.js
│   │   │   ├── usePropertyCRUD.js
│   │   │   └── useUnitManagement.js
│   │   └── services/
│   │       └── propertyService.js
│   │
│   ├── favorites/
│   │   ├── components/
│   │   │   ├── FavoriteButton.jsx
│   │   │   └── FavoritesList.jsx
│   │   ├── hooks/
│   │   │   └── useFavorites.js
│   │   └── services/
│   │       └── favoritesService.js
│   │
│   ├── messaging/
│   │   ├── components/
│   │   │   ├── Chatbox.jsx
│   │   │   ├── MessageList.jsx
│   │   │   ├── ConversationHeader.jsx
│   │   │   ├── ConversationList.jsx
│   │   │   └── InquiryBroadcast.jsx
│   │   ├── hooks/
│   │   │   ├── useConversations.js
│   │   │   ├── useMessages.js
│   │   │   └── useInquiry.js
│   │   └── services/
│   │       └── messagingService.js
│   │
│   ├── careers/
│   │   ├── components/
│   │   │   ├── ApplicationForm.jsx
│   │   │   ├── ApplicationStatus.jsx
│   │   │   └── CareersList.jsx
│   │   ├── hooks/
│   │   │   └── useCareerApplication.js
│   │   └── services/
│   │       └── careerService.js
│   │
│   ├── admin/                                  (Admin-specific features)
│   │   ├── components/
│   │   │   ├── PropertyManagement/
│   │   │   │   ├── PropertyForm.jsx           (Create/Edit)
│   │   │   │   ├── PropertyList.jsx
│   │   │   │   ├── UnitConfigurator.jsx       (Structured grid per SDD)
│   │   │   │   └── ResourceUploader.jsx
│   │   │   ├── ApplicationReview/
│   │   │   │   ├── ApplicationReviewList.jsx
│   │   │   │   ├── ApplicationDetailCard.jsx
│   │   │   │   └── StatusUpdateButtons.jsx
│   │   │   └── ArticleManagement/
│   │   │       ├── ArticleEditor.jsx
│   │   │       └── ArticleList.jsx
│   │   ├── hooks/
│   │   │   ├── useAdminProperties.js
│   │   │   ├── useAdminApplications.js
│   │   │   └── useAdminArticles.js
│   │   └── services/
│   │       └── adminService.js
│   │
│   └── agent/                                  (Agent-specific features)
│       ├── components/
│       │   ├── Dashboard/
│       │   │   ├── AgentDashboard.jsx
│       │   │   ├── PropertyPortfolio.jsx
│       │   │   └── ClientInsights.jsx
│       │   ├── BulletinBoard/
│       │   │   ├── BulletinList.jsx
│       │   │   └── BulletinDetail.jsx
│       │   └── ClientMessaging/
│       │       ├── InquiryList.jsx
│       │       └── ConversationWithClient.jsx
│       ├── hooks/
│       │   ├── useAgentDashboard.js
│       │   ├── useAgentMessages.js
│       │   └── useBulletinBoard.js
│       └── services/
│           └── agentService.js
│
├── hooks/                                     (Global custom hooks)
│   ├── useAuth.js                             (Get current user & role)
│   ├── useRole.js                             (Check if user has specific role)
│   ├── useLocalStorage.js                     (Persist data client-side)
│   ├── useFetch.js                            (Generic data fetching)
│   └── useDebounce.js                         (For search/filtering)
│
├── layouts/                                   (Role-specific wrapper layouts)
│   ├── MainLayout.jsx                         (Guests, Registered Users)
│   ├── AgentLayout.jsx                        (Agents only)
│   └── AdminLayout.jsx                        (Admins only)
│
├── pages/                                     (View components organized by role)
│   ├── auth/
│   │   ├── LoginPage.jsx                      (Moved here)
│   │   └── RegisterPage.jsx                   (Moved here)
│   ├── public/
│   │   ├── HomePage.jsx                       (Moved here)
│   │   ├── PropertiesListPage.jsx             (Basic listings)
│   │   ├── CareersPage.jsx                    (Job postings)
│   │   └── NotFoundPage.jsx                   (404 page)
│   ├── user/
│   │   ├── PropertyDetailsPage.jsx            (Expanded view with pricing)
│   │   ├── FavoritesPage.jsx
│   │   ├── MessagesPage.jsx
│   │   ├── ProfilePage.jsx
│   │   └── ApplicationStatusPage.jsx
│   ├── agent/
│   │   ├── DashboardPage.jsx
│   │   ├── PropertiesPage.jsx
│   │   ├── BulletinBoardPage.jsx
│   │   ├── MessagesPage.jsx
│   │   └── ProfilePage.jsx
│   └── admin/
│       ├── DashboardPage.jsx
│       ├── PropertyManagementPage.jsx
│       ├── ApplicationReviewPage.jsx
│       ├── ArticlesManagementPage.jsx
│       └── ProfilePage.jsx
│
├── routes/                                    (Route definitions & protection)
│   ├── ProtectedRoute.jsx                     (Requires authentication)
│   ├── RoleRoute.jsx                          (Requires specific role(s))
│   ├── routeConfig.js                         (Centralized route definitions)
│   └── index.js                               (Export)
│
├── types/                                     (Optional: Zod schemas or TS types)
│   ├── property.types.js
│   ├── user.types.js
│   ├── message.types.js
│   └── application.types.js
│
├── utils/                                     (Helper functions)
│   ├── formatting.js                          (formatPrice, formatDate)
│   ├── validation.js                          (Email, password regex)
│   ├── constants.js                           (API_BASE_URL, ROLES, STATUS)
│   ├── errorHandler.js                        (Parse API errors)
│   └── storage.js                             (localStorage helpers)
│
├── App.jsx                                    (Root routing configuration)
├── App.css
├── main.jsx
└── index.css
```

---

## File Movements Summary

| File                   | Original Location       | New Location                | Reason                              |
| ---------------------- | ----------------------- | --------------------------- | ----------------------------------- |
| `LoginForm.jsx`        | `components/common/`    | `features/auth/components/` | Auth-specific feature component     |
| `RegistrationForm.jsx` | `components/common/`    | `features/auth/components/` | Auth-specific feature component     |
| `HomePage.jsx`         | `pages/`                | `pages/public/`             | Guest/public-accessible page        |
| `LoginPage.jsx`        | `pages/`                | `pages/auth/`               | Authentication flow page            |
| `RegisterPage.jsx`     | `pages/`                | `pages/auth/`               | Authentication flow page            |
| `Header.jsx`           | `components/common/`    | `components/common/`        | Kept (reusable shared component)    |
| `HeroSection.jsx`      | `components/common/`    | `components/common/`        | Kept (reusable shared component)    |
| `StatsSection.jsx`     | `components/dashboard/` | `components/dashboard/`     | Kept (reusable dashboard component) |

---

## Key Design Principles

### 1. **Role-Based Separation**

- Pages are organized by role access level: `public/`, `user/`, `agent/`, `admin/`
- Prevents role-bleeding and makes permissions clear
- Example: Admin-only pages live in `pages/admin/`

### 2. **Feature-Based Features Folder**

- Features are organized by business domain, not tech stack
- Each feature has `components/`, `hooks/`, and `services/`
- Example: All messaging logic stays in `features/messaging/`

### 3. **Components vs Features**

- **`components/`**: Dumb, reusable UI components (Button, Card, Modal)
- **`features/`**: Smart, domain-specific components (LoginForm, PropertyCard with logic)

### 4. **Centralized API Management**

- `api/endpoints/` groups endpoints by domain
- `api/interceptors/` handles auth tokens and errors
- Prevents scattered API calls across the codebase

### 5. **Context for Authentication**

- `context/AuthContext.jsx` manages user role and JWT
- Enables role-based route guards and conditional UI
- Replaces prop-drilling for user info

### 6. **Layout Patterns**

- Three role-specific layouts: Main, Agent, Admin
- Each layout encapsulates navigation, sidebar, and role-specific UI
- Ensures consistent navigation per user role

---

## Next Steps

1. **Implement AuthContext** (Phase 1)
   - Create `context/AuthContext.jsx` to manage user state
   - Replace localStorage checks with context

2. **Create Route Guards** (Phase 1-2)
   - Implement `routes/ProtectedRoute.jsx` for authentication
   - Implement `routes/RoleRoute.jsx` for role-specific access

3. **Organize API Endpoints** (Phase 2)
   - Move API calls into `features/*/services/`
   - Centralize in `api/endpoints/` by domain

4. **Build Feature Components** (Phase 3-5)
   - Create components under `features/properties/`, `features/messaging/`, etc.
   - Keep business logic in hooks

5. **Create Layouts** (Phase 3)
   - Implement MainLayout, AgentLayout, AdminLayout
   - Add role-specific navigation

---

## Current Status

✅ **Completed:**

- Folder structure created
- Auth forms moved to `features/auth/components/`
- Pages reorganized into role-based folders
- `App.jsx` updated with new import paths

⏳ **Next:**

- Create `AuthContext` for user state management
- Implement `ProtectedRoute` for authentication
- Build remaining feature components
- Connect all pages to routes

---

## Notes for Development

- Always import from the most specific location (e.g., `features/properties/services/` not generic `services/`)
- Keep `components/common/` for truly reusable, context-agnostic components
- Use `hooks/` for app-level hooks, feature-specific hooks go in `features/*/hooks/`
- API calls should be abstracted in `services/`, components should use hooks
- Pages should be **thin** (minimal logic), logic should be in features
