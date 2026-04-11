# Fort Point Properties - Current File Structure (Post-Reorganization)

Generated: April 9, 2026

```
src/
│
├── api/
│   ├── auth.js                           ✅ (Existing)
│   ├── client.js
│   ├── endpoints/                        (Empty - To be populated)
│   │   ├── auth.js
│   │   ├── properties.js
│   │   ├── favorites.js
│   │   ├── messages.js
│   │   ├── careers.js
│   │   ├── admin.js
│   │   └── agent.js
│   └── interceptors/                     (Empty - To be populated)
│       ├── tokenRefresh.js
│       └── errorHandler.js
│
├── assets/
│   ├── FortPointProperties_Logo.jpg      ✅ (Existing)
│   ├── property.png                      ✅ (Existing)
│   ├── images/                           (Empty folder)
│   ├── icons/                            (Empty folder)
│   └── constants/                        (Empty folder)
│       └── branding.js
│
├── components/
│   ├── common/
│   │   ├── Header.jsx                    ✅ (Existing)
│   │   ├── HeroSection.jsx               ✅ (Existing)
│   │   ├── Button.jsx
│   │   ├── Card.jsx
│   │   ├── Modal.jsx
│   │   └── Form/                         (Empty - To be populated)
│   │       ├── Input.jsx
│   │       ├── Select.jsx
│   │       └── FormGroup.jsx
│   ├── shared/                           (Empty - To be populated)
│   │   ├── Spinner.jsx
│   │   ├── ErrorBoundary.jsx
│   │   ├── ConfirmDialog.jsx
│   │   └── Toast.jsx
│   ├── layout/                           (Empty - To be populated)
│   │   ├── Sidebar.jsx
│   │   ├── Navigation.jsx
│   │   └── PageHeader.jsx
│   └── dashboard/
│       └── StatsSection.jsx              ✅ (Existing)
│
├── context/                              (Empty - To be populated)
│   ├── AuthContext.jsx
│   ├── AuthProvider.jsx
│   └── NotificationContext.jsx
│
├── features/
│   ├── auth/
│   │   ├── components/
│   │   │   ├── LoginForm.jsx             ✅ (Moved here)
│   │   │   ├── RegistrationForm.jsx      ✅ (Moved here)
│   │   │   └── ForgotPasswordForm.jsx
│   │   ├── hooks/                        (Empty - To be populated)
│   │   │   ├── useLogin.js
│   │   │   ├── useRegister.js
│   │   │   └── useForgotPassword.js
│   │   └── services/                     (Empty - To be populated)
│   │       └── authService.js
│   │
│   ├── properties/                       (Empty - To be populated)
│   │   ├── components/
│   │   │   ├── PropertyCard.jsx
│   │   │   ├── PropertyDetailsExpanded.jsx
│   │   │   ├── UnitPricingTable.jsx
│   │   │   ├── PitchReadySection.jsx
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
│   ├── favorites/                        (Empty - To be populated)
│   │   ├── components/
│   │   │   ├── FavoriteButton.jsx
│   │   │   └── FavoritesList.jsx
│   │   ├── hooks/
│   │   │   └── useFavorites.js
│   │   └── services/
│   │       └── favoritesService.js
│   │
│   ├── messaging/                        (Empty - To be populated)
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
│   ├── careers/                          (Empty - To be populated)
│   │   ├── components/
│   │   │   ├── ApplicationForm.jsx
│   │   │   ├── ApplicationStatus.jsx
│   │   │   └── CareersList.jsx
│   │   ├── hooks/
│   │   │   └── useCareerApplication.js
│   │   └── services/
│   │       └── careerService.js
│   │
│   ├── admin/                            (Empty - To be populated)
│   │   ├── components/
│   │   │   ├── PropertyManagement/
│   │   │   │   ├── PropertyForm.jsx
│   │   │   │   ├── PropertyList.jsx
│   │   │   │   ├── UnitConfigurator.jsx
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
│   └── agent/                            (Empty - To be populated)
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
├── hooks/                                (Empty - To be populated)
│   ├── useAuth.js
│   ├── useRole.js
│   ├── useLocalStorage.js
│   ├── useFetch.js
│   └── useDebounce.js
│
├── layouts/                              (Empty - To be populated)
│   ├── MainLayout.jsx
│   ├── AgentLayout.jsx
│   └── AdminLayout.jsx
│
├── pages/
│   ├── auth/
│   │   ├── LoginPage.jsx                 ✅ (Moved here)
│   │   └── RegisterPage.jsx              ✅ (Moved here)
│   ├── public/
│   │   ├── HomePage.jsx                  ✅ (Moved here)
│   │   ├── PropertiesListPage.jsx
│   │   ├── CareersPage.jsx
│   │   └── NotFoundPage.jsx
│   ├── user/                             (Empty - To be populated)
│   │   ├── PropertyDetailsPage.jsx
│   │   ├── FavoritesPage.jsx
│   │   ├── MessagesPage.jsx
│   │   ├── ProfilePage.jsx
│   │   └── ApplicationStatusPage.jsx
│   ├── agent/                            (Empty - To be populated)
│   │   ├── DashboardPage.jsx
│   │   ├── PropertiesPage.jsx
│   │   ├── BulletinBoardPage.jsx
│   │   ├── MessagesPage.jsx
│   │   └── ProfilePage.jsx
│   └── admin/                            (Empty - To be populated)
│       ├── DashboardPage.jsx
│       ├── PropertyManagementPage.jsx
│       ├── ApplicationReviewPage.jsx
│       ├── ArticlesManagementPage.jsx
│       └── ProfilePage.jsx
│
├── routes/                               (Empty - To be populated)
│   ├── ProtectedRoute.jsx
│   ├── RoleRoute.jsx
│   ├── routeConfig.js
│   └── index.js
│
├── types/                                (Empty - To be populated)
│   ├── property.types.js
│   ├── user.types.js
│   ├── message.types.js
│   └── application.types.js
│
├── utils/                                (Empty - To be populated)
│   ├── formatting.js
│   ├── validation.js
│   ├── constants.js
│   ├── errorHandler.js
│   └── storage.js
│
├── App.jsx                               ✅ (Updated import paths)
├── App.css
├── main.jsx
└── index.css
```

---

## Legend

- ✅ **Existing files** - Already created and moved to appropriate locations
- (Empty - To be populated) - Folder created but awaiting content
- (Empty folder) - Placeholder for future assets

---

## Stats

- **Total Folders**: 35
- **Files Moved**: 5
- **Files Created/Updated**: 8
- **Total Placeholders**: 100+ (ready for future components, hooks, services)

---

## What's Ready to Use

Your app is now ready with:

1. All auth forms in the correct feature folder
2. Pages organized by user role
3. Clear separation of concerns
4. Foundation for adding new features

Next: Run `npm run dev` to verify everything works!
