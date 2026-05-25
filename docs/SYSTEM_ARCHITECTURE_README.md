# System Architecture README

## 1. System Project Introduction

This document is the working architecture README for the Fort Point Properties system.

The system is a property-focused platform with a web frontend, Android mobile app, and Spring Boot backend. It helps different user roles browse properties, manage accounts, publish articles, submit career applications, exchange messages, and perform admin tasks where allowed.

This README is updated step by step as each feature is reviewed and confirmed in the codebase.

The current project structure shows four main parts:

- A web frontend in `web/`
- A Spring Boot backend in `backend/`
- An Android mobile app in `mobile/`
- Documentation and architecture notes in `docs/`

The web and Android mobile clients connect to the same backend API, so both clients share the same business logic, database, authentication flow, security rules, and service layer.

The goal of this README is to keep a clear, presentation-ready record of the system purpose, the problem it addresses, the intended users, the project goals, the architecture, component interactions, and the implementation evidence that has been verified in the code.

## 2. Main Features of the System

<!-- FEATURES_START -->
<!-- FEATURE:User Management Feature START -->
### User Management Feature

The user management feature lets admins manage users from a dedicated admin screen.

Who uses it:

- Admin users who manage accounts and access

Why it matters:

- It gives admins a central place to manage who can access the system.
- It supports role-based administration for users, agents, and admins.
- It helps keep user records organized and searchable.

Capabilities proven by code:

- Listing users
- Searching users by name
- Filtering users by role
- Creating users
- Updating user roles
- Deleting users
- Viewing user details

Frontend proof files:

- `web/src/features/usermanagement/index.js`
- `web/src/features/usermanagement/pages/UserManagementPage.jsx`
- `web/src/features/usermanagement/api/userManagementApi.js`
- `web/src/features/usermanagement/hooks/useUserManagement.js`
- `web/src/features/usermanagement/components/UserList.jsx`
- `web/src/features/usermanagement/components/UserListItem.jsx`
- `web/src/features/usermanagement/components/UserDetailsModal.jsx`
- `web/src/features/usermanagement/components/EditUserRoleModal.jsx`
- `web/src/features/usermanagement/components/AddUserModal.jsx`
- `web/src/features/usermanagement/components/DeleteUserConfirmModal.jsx`
- `web/src/features/usermanagement/components/UserRoleFilter.jsx`
- `web/src/App.jsx`
- `web/src/app/Routes.jsx`
- `web/src/shared/components/layout/AdminSidebar.jsx`

Backend proof files:

- `backend/src/main/java/edu/cit/aligato/fortpointproperties/usermanagement/controller/AdminUserController.java`
- `backend/src/main/java/edu/cit/aligato/fortpointproperties/usermanagement/service/AdminUserService.java`
- `backend/src/main/java/edu/cit/aligato/fortpointproperties/usermanagement/dto/AdminUserResponseDTO.java`
- `backend/src/main/java/edu/cit/aligato/fortpointproperties/usermanagement/dto/AdminCreateUserRequestDTO.java`
- `backend/src/main/java/edu/cit/aligato/fortpointproperties/usermanagement/dto/AdminUpdateUserRoleRequestDTO.java`
- `backend/src/main/java/edu/cit/aligato/fortpointproperties/auth/entity/User.java`
- `backend/src/main/java/edu/cit/aligato/fortpointproperties/auth/repository/UserRepository.java`

Implementation status: Implemented.
<!-- FEATURE:User Management Feature END -->
<!-- FEATURE:Authentication and Authorization Feature START -->
### Authentication and Authorization Feature

The authentication and authorization feature lets users register, log in, sign in with Google, and stay signed in through token-based session handling.

Who uses it:

- New users who register for an account
- Returning users who log in with email and password
- Users who sign in with Google OAuth
- Logged-in users whose role determines what routes and actions they can access

Why it matters:

- It protects the application and keeps user sessions consistent.
- It separates public access from authenticated access.
- It supports role-based route behavior for admin, agent, registered user, and public states.

Capabilities proven by code:

- Registration
- Login
- Authenticated user profile lookup
- JWT access token and refresh token creation
- Password hashing with BCrypt
- Google OAuth sign-in
- Token storage in frontend local storage
- Protected route behavior and role-based redirects

Frontend proof files:

- `web/src/features/auth/index.js`
- `web/src/features/auth/pages/LoginPage.jsx`
- `web/src/features/auth/pages/RegisterPage.jsx`
- `web/src/features/auth/pages/GoogleOAuthCallbackPage.jsx`
- `web/src/features/auth/api/authApi.js`
- `web/src/features/auth/hooks/useAuth.js`
- `web/src/features/auth/components/LoginForm.jsx`
- `web/src/features/auth/components/RegistrationForm.jsx`
- `web/src/shared/context/AuthContext.jsx`
- `web/src/shared/context/AuthContextBase.js`
- `web/src/shared/context/useAuthContext.js`
- `web/src/shared/utils/api.js`
- `web/src/App.jsx`
- `web/src/app/Routes.jsx`

Backend proof files:

- `backend/src/main/java/edu/cit/aligato/fortpointproperties/auth/controller/AuthController.java`
- `backend/src/main/java/edu/cit/aligato/fortpointproperties/auth/service/AuthService.java`
- `backend/src/main/java/edu/cit/aligato/fortpointproperties/auth/service/GoogleOAuthSuccessHandler.java`
- `backend/src/main/java/edu/cit/aligato/fortpointproperties/auth/repository/UserRepository.java`
- `backend/src/main/java/edu/cit/aligato/fortpointproperties/auth/entity/User.java`
- `backend/src/main/java/edu/cit/aligato/fortpointproperties/auth/dto/AuthResponse.java`
- `backend/src/main/java/edu/cit/aligato/fortpointproperties/auth/dto/LoginRequest.java`
- `backend/src/main/java/edu/cit/aligato/fortpointproperties/auth/dto/RegisterRequest.java`
- `backend/src/main/java/edu/cit/aligato/fortpointproperties/auth/dto/UserDTO.java`
- `backend/src/main/java/edu/cit/aligato/fortpointproperties/shared/security/SecurityConfig.java`
- `backend/src/main/java/edu/cit/aligato/fortpointproperties/shared/security/JwtUtil.java`
- `backend/src/main/java/edu/cit/aligato/fortpointproperties/shared/security/JwtAuthenticationFilter.java`
- `backend/src/main/java/edu/cit/aligato/fortpointproperties/shared/utils/PasswordValidator.java`

Mobile proof files:

- `mobile/app/src/main/java/com/example/fortpointproperties/features/auth/ui/LoginActivity.kt`
- `mobile/app/src/main/java/com/example/fortpointproperties/features/auth/ui/RegisterActivity.kt`
- `mobile/app/src/main/java/com/example/fortpointproperties/shared/auth/TokenManager.kt`
- `mobile/app/src/main/java/com/example/fortpointproperties/shared/auth/SessionManager.kt`
- `mobile/app/src/main/java/com/example/fortpointproperties/shared/network/ApiClient.kt`

Implementation status: Implemented.
<!-- FEATURE:Authentication and Authorization Feature END -->
<!-- FEATURE:Article Feature START -->
### Article Feature

The article feature lets users browse blog cards, open article details after login, and lets admins create, edit, and delete blog posts.

Who uses it:

- Public visitors who can browse the article list
- Logged-in users who can read full article details
- Admins who manage blog content
- Agents who can access article detail routes because the backend allows their role

Why it matters:

- It provides news, guides, and property insight content for the platform.
- It supports content publishing and content maintenance through the admin flow.
- It connects public browsing with authenticated article reading and admin management.

Capabilities proven by code:

- Article listing
- Article title search
- Article details
- Admin article creation
- Admin article editing
- Admin article deletion
- Cover photo upload and replacement in the admin form

Frontend proof files:

- `web/src/features/article/index.js`
- `web/src/features/article/pages/ArticleListPage.jsx`
- `web/src/features/article/pages/ArticleDetailsPage.jsx`
- `web/src/features/article/pages/ArticleCreatePage.jsx`
- `web/src/features/article/pages/ArticleEditPage.jsx`
- `web/src/features/article/api/articleApi.js`
- `web/src/features/article/components/ArticleList.jsx`
- `web/src/features/article/components/ArticleCard.jsx`
- `web/src/features/article/components/ArticleForm.jsx`
- `web/src/features/article/components/ArticleAdminActions.jsx`
- `web/src/features/article/components/PublicArticlePrompt.jsx`

Backend proof files:

- `backend/src/main/java/edu/cit/aligato/fortpointproperties/article/controller/ArticleController.java`
- `backend/src/main/java/edu/cit/aligato/fortpointproperties/article/controller/ArticleAdminController.java`
- `backend/src/main/java/edu/cit/aligato/fortpointproperties/article/service/ArticleService.java`
- `backend/src/main/java/edu/cit/aligato/fortpointproperties/article/service/ArticleStorageService.java`
- `backend/src/main/java/edu/cit/aligato/fortpointproperties/article/repository/ArticleRepository.java`
- `backend/src/main/java/edu/cit/aligato/fortpointproperties/article/entity/Article.java`
- `backend/src/main/java/edu/cit/aligato/fortpointproperties/article/dto/ArticleCardDTO.java`
- `backend/src/main/java/edu/cit/aligato/fortpointproperties/article/dto/ArticleDTO.java`
- `backend/src/main/java/edu/cit/aligato/fortpointproperties/article/dto/ArticleCreateRequestDTO.java`
- `backend/src/main/java/edu/cit/aligato/fortpointproperties/article/dto/ArticleUpdateRequestDTO.java`

Mobile proof files:

- `mobile/app/src/main/java/com/example/fortpointproperties/features/article/ui/ArticleListActivity.kt`
- `mobile/app/src/main/java/com/example/fortpointproperties/features/article/ui/ArticleDetailActivity.kt`
- `mobile/app/src/main/java/com/example/fortpointproperties/features/article/ui/adapter/ArticleAdapter.kt`
- `mobile/app/src/main/java/com/example/fortpointproperties/features/article/network/ArticleApi.kt`
- `mobile/app/src/main/java/com/example/fortpointproperties/features/article/data/repository/ArticleRepository.kt`

Implementation status: Implemented.
<!-- FEATURE:Article Feature END -->
<!-- FEATURE:Property Feature START -->
### Property Feature

The property feature lets users browse, search, and open property records, while admins can create, edit, delete, and manage property details.

Who uses it:

- Public visitors who browse available properties
- Registered users who can view richer property details
- Agents who view assigned property listings
- Admins who manage property records

Why it matters:

- It is the main property browsing and management flow in the system.
- It connects the public site, role-based dashboards, and the backend data model.
- It supports the core property content that the rest of the system depends on.

Capabilities proven by code:

- Property listing
- Property detail viewing
- Search by name, location, and developer
- Filter and sort property results
- Admin property creation
- Admin property editing
- Admin property deletion
- Property photo upload in the admin flow
- Property unit management in the admin flow
- Role-based property detail views for public, registered users, agents, and admins

Frontend proof files:

- `web/src/features/properties/index.js`
- `web/src/features/properties/pages/PropertyListPage.jsx`
- `web/src/features/properties/pages/AgentPropertiesListPage.jsx`
- `web/src/features/properties/pages/AdminPropertiesListPage.jsx`
- `web/src/features/properties/api/propertyApi.js`
- `web/src/features/properties/components/PropertyDetailModal.jsx`
- `web/src/features/properties/components/admin/AdminPropertyCreateModal.jsx`
- `web/src/shared/components/properties/PropertySearchFilter.jsx`
- `web/src/shared/utils/propertyHelpers.js`
- `web/src/shared/utils/searchHelpers.js`

Backend proof files:

- `backend/src/main/java/edu/cit/aligato/fortpointproperties/properties/controller/PropertyPublicController.java`
- `backend/src/main/java/edu/cit/aligato/fortpointproperties/properties/controller/PropertyRegisteredController.java`
- `backend/src/main/java/edu/cit/aligato/fortpointproperties/properties/controller/PropertyAgentController.java`
- `backend/src/main/java/edu/cit/aligato/fortpointproperties/properties/controller/PropertyAdminController.java`
- `backend/src/main/java/edu/cit/aligato/fortpointproperties/properties/service/PropertyService.java`
- `backend/src/main/java/edu/cit/aligato/fortpointproperties/properties/service/PropertyPhotoStorageService.java`
- `backend/src/main/java/edu/cit/aligato/fortpointproperties/properties/repository/PropertyRepository.java`
- `backend/src/main/java/edu/cit/aligato/fortpointproperties/properties/entity/Property.java`
- `backend/src/main/java/edu/cit/aligato/fortpointproperties/properties/entity/PropertyUnit.java`
- `backend/src/main/java/edu/cit/aligato/fortpointproperties/properties/entity/PropertyPhoto.java`
- `backend/src/main/java/edu/cit/aligato/fortpointproperties/properties/dto/PropertyCreateRequestDTO.java`
- `backend/src/main/java/edu/cit/aligato/fortpointproperties/properties/dto/PropertyUpdateRequestDTO.java`
- `backend/src/main/java/edu/cit/aligato/fortpointproperties/properties/dto/PropertyCardDTO.java`
- `backend/src/main/java/edu/cit/aligato/fortpointproperties/properties/dto/PropertyUserDetailDTO.java`
- `backend/src/main/java/edu/cit/aligato/fortpointproperties/properties/dto/PropertyAgentDetailDTO.java`
- `backend/src/main/java/edu/cit/aligato/fortpointproperties/properties/dto/PropertyAdminDetailDTO.java`

Mobile proof files:

- `mobile/app/src/main/java/com/example/fortpointproperties/features/properties/ui/PropertyListActivity.kt`
- `mobile/app/src/main/java/com/example/fortpointproperties/features/properties/ui/PropertyDetailActivity.kt`
- `mobile/app/src/main/java/com/example/fortpointproperties/features/properties/ui/adapter/PropertyAdapter.kt`
- `mobile/app/src/main/java/com/example/fortpointproperties/features/properties/network/PropertyApi.kt`
- `mobile/app/src/main/java/com/example/fortpointproperties/features/properties/data/repository/PropertyRepository.kt`
- `mobile/app/src/main/res/layout/item_property_card.xml`
- `mobile/app/src/main/res/layout/activity_property_detail.xml`

Implementation status: Implemented.
<!-- FEATURE:Property Feature END -->
<!-- FEATURE:Favorites Feature START -->
### Favorites Feature

The favorites feature lets authenticated users save property records, view their saved list, check favorite status, and remove saved properties.

Who uses it:

- Logged-in users who want to save properties for later
- Registered users in the property card UI that shows the favorite button

Why it matters:

- It gives users a quick way to bookmark properties they want to revisit.
- It connects property browsing with a personal saved list.
- It keeps favorite data tied to the logged-in user account.

Capabilities proven by code:

- Adding properties to favorites
- Removing properties from favorites
- Listing a user's favorite properties
- Checking whether a property is favorited
- Getting a favorite count for the current user

Frontend proof files:

- `web/src/features/favorites/index.js`
- `web/src/features/favorites/pages/FavoritePage.jsx`
- `web/src/features/favorites/components/FavoriteButton.jsx`
- `web/src/features/favorites/api/favoritesApi.js`
- `web/src/features/favorites/hooks/useFavorites.js`
- `web/src/features/favorites/hooks/useFavoritesUI.js`
- `web/src/shared/components/ui/FeaturedProperties.jsx`
- `web/src/features/properties/pages/PropertyListPage.jsx`
- `web/src/shared/utils/constants.js`

Backend proof files:

- `backend/src/main/java/edu/cit/aligato/fortpointproperties/favorites/controller/FavoriteController.java`
- `backend/src/main/java/edu/cit/aligato/fortpointproperties/favorites/service/FavoriteService.java`
- `backend/src/main/java/edu/cit/aligato/fortpointproperties/favorites/repository/FavoriteRepository.java`
- `backend/src/main/java/edu/cit/aligato/fortpointproperties/favorites/entity/Favorite.java`
- `backend/src/main/java/edu/cit/aligato/fortpointproperties/favorites/dto/FavoriteDTO.java`
- `backend/src/main/java/edu/cit/aligato/fortpointproperties/auth/entity/User.java`
- `backend/src/main/java/edu/cit/aligato/fortpointproperties/auth/repository/UserRepository.java`
- `backend/src/main/java/edu/cit/aligato/fortpointproperties/properties/entity/Property.java`
- `backend/src/main/java/edu/cit/aligato/fortpointproperties/properties/repository/PropertyRepository.java`

Mobile proof files:

- `mobile/app/src/main/java/com/example/fortpointproperties/features/favorites/ui/FavoritesActivity.kt`
- `mobile/app/src/main/java/com/example/fortpointproperties/features/favorites/network/FavoriteApi.kt`
- `mobile/app/src/main/java/com/example/fortpointproperties/features/favorites/data/repository/FavoriteRepository.kt`

Implementation status: Implemented.
<!-- FEATURE:Favorites Feature END -->
<!-- FEATURE:Messaging Feature START -->
### Messaging Feature

The messaging feature lets logged-in registered users start conversations and exchange messages with agents, while agents can view inbox conversations, reply, and track unread updates.

Who uses it:

- Registered users who want to contact the team
- Agents who manage incoming conversations and replies

Why it matters:

- It provides a direct support and inquiry channel inside the system.
- It keeps conversation history in one place for both sides.
- It supports live updates so new messages can appear without a full page refresh.

Capabilities proven by code:

- Starting a conversation
- Viewing conversations
- Viewing message history
- Sending messages
- Marking conversations as read
- Tracking unread counts
- Real-time inbox and message updates through WebSocket/STOMP
- Agent conversation assignment when an agent replies to an open conversation

Frontend proof files:

- `web/src/features/messaging/index.js`
- `web/src/features/messaging/pages/RegisteredUserMessagesPage.jsx`
- `web/src/features/messaging/pages/AgentInboxPage.jsx`
- `web/src/features/messaging/pages/ConversationPage.jsx`
- `web/src/features/messaging/components/FloatingChatWidget.jsx`
- `web/src/features/messaging/components/ConversationList.jsx`
- `web/src/features/messaging/components/MessageThread.jsx`
- `web/src/features/messaging/components/MessageInput.jsx`
- `web/src/features/messaging/api/messagingApi.js`
- `web/src/features/messaging/hooks/useMessagingSocket.js`
- `web/src/features/messaging/utils/messagingHelpers.js`
- `web/src/app/Routes.jsx`
- `web/src/shared/components/layout/AgentSidebar.jsx`

Backend proof files:

- `backend/src/main/java/edu/cit/aligato/fortpointproperties/messaging/controller/MessagingController.java`
- `backend/src/main/java/edu/cit/aligato/fortpointproperties/messaging/service/MessagingService.java`
- `backend/src/main/java/edu/cit/aligato/fortpointproperties/messaging/repository/ConversationRepository.java`
- `backend/src/main/java/edu/cit/aligato/fortpointproperties/messaging/repository/MessageRepository.java`
- `backend/src/main/java/edu/cit/aligato/fortpointproperties/messaging/repository/ConversationReadStateRepository.java`
- `backend/src/main/java/edu/cit/aligato/fortpointproperties/messaging/entity/Conversation.java`
- `backend/src/main/java/edu/cit/aligato/fortpointproperties/messaging/entity/Message.java`
- `backend/src/main/java/edu/cit/aligato/fortpointproperties/messaging/entity/ConversationReadState.java`
- `backend/src/main/java/edu/cit/aligato/fortpointproperties/messaging/dto/ConversationDTO.java`
- `backend/src/main/java/edu/cit/aligato/fortpointproperties/messaging/dto/MessageDTO.java`
- `backend/src/main/java/edu/cit/aligato/fortpointproperties/messaging/dto/CreateConversationDTO.java`
- `backend/src/main/java/edu/cit/aligato/fortpointproperties/messaging/dto/SendMessageDTO.java`
- `backend/src/main/java/edu/cit/aligato/fortpointproperties/messaging/WebSocketConfig.java`
- `backend/src/main/java/edu/cit/aligato/fortpointproperties/messaging/util/MessagingRoles.java`
- `backend/src/main/java/edu/cit/aligato/fortpointproperties/auth/repository/UserRepository.java`

Mobile proof files:

- `mobile/app/src/main/java/com/example/fortpointproperties/features/messaging/ui/ConversationsActivity.kt`
- `mobile/app/src/main/java/com/example/fortpointproperties/features/messaging/ui/ConversationDetailActivity.kt`
- `mobile/app/src/main/java/com/example/fortpointproperties/features/messaging/network/MessagingApi.kt`
- `mobile/app/src/main/java/com/example/fortpointproperties/features/messaging/network/MessagingSocketClient.kt`
- `mobile/app/src/main/java/com/example/fortpointproperties/features/messaging/data/repository/MessagingRepository.kt`
- `mobile/app/src/main/java/com/example/fortpointproperties/features/messaging/ui/adapter/ConversationAdapter.kt`
- `mobile/app/src/main/java/com/example/fortpointproperties/features/messaging/ui/adapter/MessageAdapter.kt`

Implementation status: Implemented.
<!-- FEATURE:Messaging Feature END -->
<!-- FEATURE:Profile Feature START -->
### Profile Feature

The profile feature lets logged-in users view their account details, edit profile information, and update or remove their profile image.

Who uses it:

- Registered users managing their own profile
- Agents managing their own profile
- Admins managing their own profile

Why it matters:

- It keeps account details current for communication and identification.
- It gives each role a dedicated profile screen without needing a separate backend profile package.
- It supports profile image management for the signed-in user.

Capabilities proven by code:

- Viewing current user profile details
- Updating first name, last name, and phone number
- Uploading a profile image
- Removing a profile image
- Keeping email read-only from the profile page

Frontend proof files:

- `web/src/features/profile/index.js`
- `web/src/features/profile/pages/RegisteredUserProfile.jsx`
- `web/src/features/profile/pages/AgentProfile.jsx`
- `web/src/features/profile/pages/AdminProfile.jsx`
- `web/src/features/profile/components/ProfilePanel.jsx`
- `web/src/features/profile/components/ProfileImageUploader.jsx`
- `web/src/features/profile/hooks/useProfile.js`
- `web/src/features/profile/api/profileApi.js`
- `web/src/App.jsx`
- `web/src/app/Routes.jsx`
- `web/src/shared/components/layout/AgentSidebar.jsx`
- `web/src/shared/components/layout/AdminSidebar.jsx`

Backend proof files:

- `backend/src/main/java/edu/cit/aligato/fortpointproperties/auth/controller/AuthController.java`
- `backend/src/main/java/edu/cit/aligato/fortpointproperties/auth/service/AuthService.java`
- `backend/src/main/java/edu/cit/aligato/fortpointproperties/auth/service/ProfileImageStorageService.java`
- `backend/src/main/java/edu/cit/aligato/fortpointproperties/auth/repository/UserRepository.java`
- `backend/src/main/java/edu/cit/aligato/fortpointproperties/auth/entity/User.java`
- `backend/src/main/java/edu/cit/aligato/fortpointproperties/auth/dto/UserDTO.java`
- `backend/src/main/java/edu/cit/aligato/fortpointproperties/auth/dto/UpdateProfileRequest.java`

Mobile proof files:

- `mobile/app/src/main/java/com/example/fortpointproperties/features/auth/ui/ProfileActivity.kt`
- `mobile/app/src/main/java/com/example/fortpointproperties/features/auth/data/UpdateProfileRequest.kt`
- `mobile/app/src/main/res/layout/activity_profile.xml`

Implementation status: Implemented.
<!-- FEATURE:Profile Feature END -->
<!-- FEATURE:Career Application Feature START -->
### Career Application Feature

The career application feature lets registered users submit an application to become an agent, and lets admins review applications and decide whether to accept or reject them.

Who uses it:

- Registered users who want to apply as agents
- Admin users who review applications

Why it matters:

- It gives the system a structured way to collect career applications.
- It stores resume and cover letter data for admin review.
- It supports an approval flow that can promote an accepted applicant to agent role.

Capabilities proven by code:

- Submitting career applications
- Viewing the current user's application status
- Listing applications for admin review
- Viewing an application in detail
- Accepting applications
- Rejecting applications
- Resume file upload and resume download via signed URL

Frontend proof files:

- `web/src/features/careerApplication/index.js`
- `web/src/features/careerApplication/pages/CareerApplicationPage.jsx`
- `web/src/features/careerApplication/pages/AdminCareerApplicationsPage.jsx`
- `web/src/features/careerApplication/pages/AdminCareerApplicationDetailsPage.jsx`
- `web/src/features/careerApplication/components/CareerApplicationForm.jsx`
- `web/src/features/careerApplication/components/CareerApplicationStatusCard.jsx`
- `web/src/features/careerApplication/components/CareerApplicationTable.jsx`
- `web/src/features/careerApplication/components/CareerApplicationDetails.jsx`
- `web/src/features/careerApplication/components/ApplicationActionButtons.jsx`
- `web/src/features/careerApplication/api/careerApplicationApi.js`
- `web/src/features/careerApplication/hooks/useCareerApplication.js`
- `web/src/features/careerApplication/hooks/useAdminCareerApplications.js`
- `web/src/App.jsx`
- `web/src/app/Routes.jsx`
- `web/src/shared/components/layout/AdminSidebar.jsx`

Backend proof files:

- `backend/src/main/java/edu/cit/aligato/fortpointproperties/careerapplication/controller/CareerApplicationController.java`
- `backend/src/main/java/edu/cit/aligato/fortpointproperties/careerapplication/service/CareerApplicationService.java`
- `backend/src/main/java/edu/cit/aligato/fortpointproperties/careerapplication/repository/CareerApplicationRepository.java`
- `backend/src/main/java/edu/cit/aligato/fortpointproperties/careerapplication/entity/CareerApplication.java`
- `backend/src/main/java/edu/cit/aligato/fortpointproperties/careerapplication/entity/CareerApplicationStatus.java`
- `backend/src/main/java/edu/cit/aligato/fortpointproperties/careerapplication/dto/CareerApplicationDTO.java`
- `backend/src/main/java/edu/cit/aligato/fortpointproperties/careerapplication/dto/CareerApplicationResponseDTO.java`
- `backend/src/main/java/edu/cit/aligato/fortpointproperties/careerapplication/dto/CreateCareerApplicationDTO.java`
- `backend/src/main/java/edu/cit/aligato/fortpointproperties/careerapplication/dto/UpdateCareerApplicationStatusDTO.java`
- `backend/src/main/java/edu/cit/aligato/fortpointproperties/careerapplication/service/SupabaseStorageService.java`
- `backend/src/main/java/edu/cit/aligato/fortpointproperties/auth/entity/User.java`
- `backend/src/main/java/edu/cit/aligato/fortpointproperties/auth/repository/UserRepository.java`

Mobile proof files:

- `mobile/app/src/main/java/com/example/fortpointproperties/features/careerApplication/ui/CareerApplicationActivity.kt`
- `mobile/app/src/main/java/com/example/fortpointproperties/features/careerApplication/network/CareerApplicationApi.kt`
- `mobile/app/src/main/java/com/example/fortpointproperties/features/careerApplication/data/repository/CareerApplicationRepository.kt`

Implementation status: Implemented.
<!-- FEATURE:Career Application Feature END -->
<!-- FEATURE:Public Pages Feature START -->
### Public Pages Feature

The public pages feature provides the main landing experience for visitors and lets them browse featured properties, use the hero search, and move into the property browsing flow.

Who uses it:

- Public visitors
- Logged-out users
- Logged-in users who still access the landing page

Why it matters:

- It is the first entry point into the system.
- It presents the public-facing brand, hero content, and featured property preview.
- It links visitors to the property browsing flow through search and navigation.

Capabilities proven by code:

- Landing page display
- Featured property preview
- Property search entry from the hero section
- Navigation to the public properties page
- Property detail opening from featured cards after login

Frontend proof files:

- `web/src/features/public/index.js`
- `web/src/features/public/pages/HomePage.jsx`
- `web/src/shared/components/ui/HeroSection.jsx`
- `web/src/shared/components/ui/FeaturedProperties.jsx`
- `web/src/shared/components/layout/Header.jsx`
- `web/src/app/Routes.jsx`
- `web/src/App.jsx`

Backend proof files:

- `backend/src/main/java/edu/cit/aligato/fortpointproperties/properties/controller/PropertyPublicController.java`
- `backend/src/main/java/edu/cit/aligato/fortpointproperties/properties/service/PropertyService.java`
- `backend/src/main/java/edu/cit/aligato/fortpointproperties/properties/repository/PropertyRepository.java`
- `backend/src/main/java/edu/cit/aligato/fortpointproperties/properties/dto/PropertyCardDTO.java`

Implementation status: Implemented.
<!-- FEATURE:Public Pages Feature END -->
<!-- FEATURES_END -->

## 3. System Architecture Overview

The visible project structure shows a client-server architecture with two clients: a React web frontend and an Android mobile app. Both clients communicate with the Spring Boot backend through REST APIs, and the messaging feature also uses WebSocket/STOMP for real-time updates.

- The web frontend entry points are `web/src/main.jsx` and `web/src/App.jsx`.
- The web frontend is organized with shared app logic and feature folders under `web/src/`.
- Shared web code lives in `web/src/shared`, where reusable context, UI components, utilities, and API helpers are exported for use across multiple features.
- The Android mobile app lives in `mobile/` and uses a feature-based structure under `mobile/app/src/main/java/com/example/fortpointproperties/features`.
- The mobile shared authentication and networking code lives under `mobile/app/src/main/java/com/example/fortpointproperties/shared/auth` and `mobile/app/src/main/java/com/example/fortpointproperties/shared/network`.
- The backend entry point is `backend/src/main/java/edu/cit/aligato/fortpointproperties/FortpointpropertiesApplication.java`.
- The backend is grouped by domain packages such as `auth`, `article`, `careerapplication`, `favorites`, `messaging`, `properties`, `shared`, and `usermanagement`.
- Shared backend code lives in `backend/src/main/java/edu/cit/aligato/fortpointproperties/shared`, where reusable response wrappers, exception handling, security helpers, and validation utilities are defined once and reused by multiple domains.
- Backend configuration is defined in `backend/src/main/resources/application.properties`.

The backend follows a layered structure: controller, service, repository, entity, and DTO. The web and mobile clients both reuse that backend business logic, database, authentication, and external storage services.

The Android mobile app is organized using a vertical-slice structure. Each implemented feature has its own UI layer, Retrofit API interface, data models, and repository layer. Shared mobile infrastructure includes `TokenManager`, `SessionManager`, `ApiClient`, and `AuthInterceptor`.

Mobile technology proof in the codebase includes:

- Retrofit for REST API calls.
- OkHttp for HTTP and WebSocket transport.
- Gson converter for JSON mapping.
- Glide for image loading.
- TokenManager and SessionManager for mobile session and role handling.
- ApiClient and AuthInterceptor for shared backend connection and Bearer token handling.

Messaging uses REST for conversation and message loading/sending, while SockJS/STOMP WebSocket is used for server-push updates through `/user/queue/messages`.

## 4. Component Interaction and Data Flow

<!-- DATA_FLOW_START -->
<!-- DATA_FLOW:User Management Feature START -->
### User Management Feature Data Flow

`UserManagementPage.jsx`
-> `web/src/features/usermanagement/api/userManagementApi.js`
-> `AdminUserController.java`
-> `AdminUserService.java`
-> `UserRepository.java`
-> `User.java` and the `users` table
-> `ApiResponse`
-> frontend user list, details modal, role modal, and delete confirmation update

Search and role filtering flow:

`UserManagementPage.jsx`
-> `getUsers` in `userManagementApi.js`
-> `AdminUserController.java`
-> `AdminUserService.java`
-> `UserRepository.java`
-> `User.java` and the `users` table
-> backend list response
-> filtered user list on the frontend

Create, role update, and delete flow:

`AddUserModal.jsx`, `EditUserRoleModal.jsx`, and `DeleteUserConfirmModal.jsx`
-> `createUser`, `updateUserRole`, and `deleteUser` in `userManagementApi.js`
-> `AdminUserController.java`
-> `AdminUserService.java`
-> `UserRepository.java`
-> `User.java` and the `users` table
-> backend response
-> refreshed user list or modal state update

The code proves listing, searching, role filtering, create, role update, and delete flows. No separate activate/deactivate flow is shown in the inspected files.
<!-- DATA_FLOW:User Management Feature END -->
<!-- DATA_FLOW:Authentication and Authorization Feature START -->
### Authentication and Authorization Data Flow

Login or Register Page
-> `web/src/features/auth/api/authApi.js`
-> `AuthController.java`
-> `AuthService.java`
-> `UserRepository.java`
-> `users` table through `User.java`
-> `AuthResponse` with access token and refresh token
-> `AuthContext.jsx` and localStorage
-> `App.jsx` and `Routes.jsx` apply role-based redirects and protected routes

Google OAuth flow proven by the code:

`LoginForm.jsx`
-> Google authorization URL from `authApi.js`
-> Spring OAuth login handled by `SecurityConfig.java`
-> `GoogleOAuthSuccessHandler.java`
-> `AuthService.java`
-> `UserRepository.java`
-> `AuthResponse` tokens returned to the frontend callback
-> `GoogleOAuthCallbackPage.jsx`
-> frontend stores auth data and redirects by role

Token handling proven by the code:

`JwtUtil.java` creates and validates tokens.
`JwtAuthenticationFilter.java` reads the Bearer token, resolves the user, and sets the authenticated role in the security context.

The inspected code proves the login, registration, token, OAuth, and route-guard flow. A dedicated refresh-token exchange endpoint was not shown in the reviewed files, so refresh behavior is only partially proven.
<!-- DATA_FLOW:Authentication and Authorization Feature END -->
<!-- DATA_FLOW:Article Feature START -->
### Article Feature Data Flow

ArticleListPage
-> `web/src/features/article/api/articleApi.js`
-> `ArticleController` for listing
-> `ArticleService.java`
-> `ArticleRepository.java`
-> `Article.java` and the `articles` table
-> `ApiResponse`
-> article card grid and search UI update

ArticleDetailsPage
-> `web/src/features/article/api/articleApi.js`
-> `ArticleController` for details
-> `ArticleService.java`
-> `ArticleRepository.java`
-> `Article.java` and the `articles` table
-> `ApiResponse`
-> article detail page update

ArticleCreatePage and ArticleEditPage
-> `web/src/features/article/components/ArticleForm.jsx`
-> `web/src/features/article/api/articleApi.js`
-> `ArticleAdminController` for admin create and update actions
-> `ArticleService.java`
-> `ArticleStorageService.java`
-> `Article.java` and the `articles` table
-> Supabase article storage for cover photos
-> `ApiResponse`
-> frontend navigation or success state

For delete:

`ArticleListPage.jsx` and `ArticleAdminActions.jsx`
-> `deleteArticle` in `articleApi.js`
-> `ArticleAdminController`
-> `ArticleService.java`
-> `ArticleRepository.java`
-> `Article.java` and the `articles` table
-> backend response
-> article list update

For cover photo upload and replacement, the form component handles the image input and the backend storage service handles the upload and replacement.

Missing or unproven layers are not claimed here. The inspected code shows the full article list, details, admin management, and photo storage flow.
<!-- DATA_FLOW:Article Feature END -->
<!-- DATA_FLOW:Property Feature START -->
### Property Feature Data Flow

Main property browsing and detail flow:

`PropertyListPage.jsx` / `AgentPropertiesListPage.jsx` / `AdminPropertiesListPage.jsx`
-> `web/src/features/properties/api/propertyApi.js`
-> `PropertyPublicController` / `PropertyRegisteredController` / `PropertyAgentController` / `PropertyAdminController`
-> `PropertyService.java`
-> `PropertyRepository.java`
-> `Property.java`, `PropertyUnit.java`, `PropertyPhoto.java` and the related tables such as `properties`, `property_units`, `property_photos`, `property_listing_types`, `property_financing_types`, and `property_amenities`
-> `ApiResponse`
-> frontend UI update in the property pages and modal components

Admin create and photo upload flow:

`AdminPropertyCreateModal.jsx` or `PropertyDetailModal.jsx`
-> `uploadPropertyPhoto`, `createProperty`, `updateProperty`, or `deleteProperty` in `propertyApi.js`
-> `PropertyAdminController`
-> `PropertyPhotoStorageService` for photo upload and `PropertyService` for property save/update/delete logic
-> Supabase storage for photo files and the property-related database tables for property records
-> backend response
-> frontend modal or page refresh

If a layer is missing or not proven, it is not claimed here. For the property feature, the code does show all the layers above.
<!-- DATA_FLOW:Property Feature END -->
<!-- DATA_FLOW:Favorites Feature START -->
### Favorites Feature Data Flow

Favorite button or Favorites page
-> `web/src/features/favorites/api/favoritesApi.js`
-> `FavoriteController.java`
-> `FavoriteService.java`
-> `FavoriteRepository.java`
-> `Favorite.java` and the `favorites` table
-> `ApiResponse`
-> frontend favorite state, list view, or heart button update

Add flow:

`FavoriteButton.jsx`, `FeaturedProperties.jsx`, or `PropertyListPage.jsx`
-> `addToFavorites` in `favoritesApi.js`
-> `FavoriteController.java`
-> `FavoriteService.java`
-> `FavoriteRepository.java`
-> `Favorite.java` and the `favorites` table
-> backend response
-> favorite heart state updates on the property card

List and remove flow:

`FavoritePage.jsx`
-> `getAllFavorites` or `removeFromFavorites` in `favoritesApi.js`
-> `FavoriteController.java`
-> `FavoriteService.java`
-> `FavoriteRepository.java`
-> `Favorite.java` and the `favorites` table
-> backend response
-> favorite list refreshes or the item is removed from the UI

Check and count flow:

`useFavorites.js`
-> `checkIfFavorited` or `getFavoriteCount` in `favoritesApi.js`
-> `FavoriteController.java`
-> `FavoriteService.java`
-> `FavoriteRepository.java`
-> `Favorite.java` and the `favorites` table
-> backend response
-> frontend favorite badge or state update

The code proves the authenticated user flow and the property-linked favorite flow. No anonymous favorite flow is shown in the inspected files.
<!-- DATA_FLOW:Favorites Feature END -->
<!-- DATA_FLOW:Messaging Feature START -->
### Messaging Feature Data Flow

Registered User Messages Page or Agent Inbox Page
-> `web/src/features/messaging/api/messagingApi.js`
-> `MessagingController.java`
-> `MessagingService.java`
-> `ConversationRepository.java`, `MessageRepository.java`, and `ConversationReadStateRepository.java`
-> `conversations`, `messages`, and `conversation_read_states` tables through `Conversation.java`, `Message.java`, and `ConversationReadState.java`
-> `ApiResponse` or direct DTO responses
-> conversation list, message thread, unread badge, and read-state updates in the frontend UI

Create and send flow:

`RegisteredUserMessagesPage.jsx`, `AgentInboxPage.jsx`, `ConversationPage.jsx`, or `FloatingChatWidget.jsx`
-> `createConversation` or `sendMessage` in `messagingApi.js`
-> `MessagingController.java`
-> `MessagingService.java`
-> `ConversationRepository.java` and `MessageRepository.java`
-> `Conversation.java` and `Message.java`
-> backend response
-> updated conversation list, selected thread, or message thread in the UI

Read-state flow:

`ConversationPage.jsx`
-> `getMessages` or `markConversationRead` in `messagingApi.js`
-> `MessagingController.java`
-> `MessagingService.java`
-> `ConversationReadStateRepository.java`
-> `ConversationReadState.java`
-> backend response
-> unread count and read badge updates in the frontend

Real-time flow:

`useMessagingSocket.js`
-> STOMP over `/ws`
-> `WebSocketConfig.java`
-> `MessagingService.java`
-> `SimpMessagingTemplate`
-> `/topic/agents/inbox`, `/topic/agents/conversations/{id}/messages`, and `/user/queue/messages`
-> socket event payloads such as conversation notifications, lock events, and new messages
-> frontend conversation preview and message thread updates

The code proves HTTP conversation and message handling plus real-time socket updates. No email delivery or external notification service is shown in the inspected files.
<!-- DATA_FLOW:Messaging Feature END -->
<!-- DATA_FLOW:Profile Feature START -->
### Profile Feature Data Flow

Profile page or component
-> `web/src/features/profile/api/profileApi.js`
-> `AuthController.java`
-> `AuthService.java`
-> `UserRepository.java`
-> `User.java` and the `users` table
-> `ApiResponse`
-> profile panel, profile image uploader, and profile state update in the frontend

View and update flow:

`RegisteredUserProfile.jsx`, `AgentProfile.jsx`, or `AdminProfile.jsx`
-> `getProfile` or `updateProfile` in `profileApi.js`
-> `AuthController.java`
-> `AuthService.java`
-> `UserRepository.java`
-> `User.java`
-> backend response
-> updated profile panel fields and context state

Profile image flow:

`ProfileImageUploader.jsx`
-> `uploadProfileImage` or `removeProfileImage` in `profileApi.js`
-> `AuthController.java`
-> `AuthService.java`
-> `ProfileImageStorageService.java`
-> `UserRepository.java`
-> `User.java`
-> backend response
-> updated avatar preview and saved profile image state

The code proves account detail viewing, profile edits, and image upload/removal. It does not show a separate backend profile package, so the auth package is the correct proof source.
<!-- DATA_FLOW:Profile Feature END -->
<!-- DATA_FLOW:Career Application Feature START -->
### Career Application Feature Data Flow

CareerApplicationPage or admin career application page
-> `web/src/features/careerApplication/api/careerApplicationApi.js`
-> `CareerApplicationController.java`
-> `CareerApplicationService.java`
-> `CareerApplicationRepository.java`
-> `CareerApplication.java` and the `career_applications` table
-> `ApiResponse`
-> frontend form, table, status card, and admin review UI update

Submission flow:

`CareerApplicationForm.jsx`
-> `submitCareerApplication` in `careerApplicationApi.js`
-> `CareerApplicationController.java`
-> `CareerApplicationService.java`
-> `SupabaseStorageService.java`
-> `CareerApplicationRepository.java`
-> `CareerApplication.java`
-> backend response
-> success state and application status card on the frontend

Admin review flow:

`AdminCareerApplicationsPage.jsx` or `AdminCareerApplicationDetailsPage.jsx`
-> `getAllCareerApplications`, `getCareerApplicationById`, `acceptCareerApplication`, or `rejectCareerApplication` in `careerApplicationApi.js`
-> `CareerApplicationController.java`
-> `CareerApplicationService.java`
-> `CareerApplicationRepository.java`
-> `CareerApplication.java`
-> backend response
-> applications table, details panel, and review action buttons update

The code proves submission, admin review, resume upload, and resume signed URL access. No separate non-admin review flow is shown in the inspected files.
<!-- DATA_FLOW:Career Application Feature END -->
<!-- DATA_FLOW:Public Pages Feature START -->
### Public Pages Feature Data Flow

HomePage
-> `web/src/shared/components/ui/HeroSection.jsx` or `web/src/shared/components/ui/FeaturedProperties.jsx`
-> public property API calls from `web/src/features/properties/api/propertyApi.js`
-> `PropertyPublicController.java`
-> `PropertyService.java`
-> `PropertyRepository.java`
-> `Property.java` and the property tables
-> `ApiResponse`
-> hero section navigation, featured property cards, and property detail modal updates

Hero search flow:

`HeroSection.jsx`
-> navigation to `/properties` with search params
-> `PropertyListPage.jsx`
-> property API calls in `propertyApi.js`
-> `PropertyPublicController.java`
-> `PropertyService.java`
-> `PropertyRepository.java`
-> backend response
-> public property search results on the frontend

Featured property flow:

`FeaturedProperties.jsx`
-> `getPublicProperties` in `propertyApi.js`
-> `PropertyPublicController.java`
-> `PropertyService.java`
-> `PropertyRepository.java`
-> `Property.java`
-> backend response
-> featured property cards and detail modal update

The public pages do not have their own backend package. The only proven backend dependency here is the public property API.
<!-- DATA_FLOW:Public Pages Feature END -->
<!-- DATA_FLOW:Mobile Application START -->
### Mobile Application Data Flow

General mobile REST flow:

Mobile Activity
-> Feature Repository
-> Feature API interface
-> Shared `ApiClient` / `AuthInterceptor`
-> Spring Boot Controller
-> Service
-> Repository
-> Entity / Database
-> DTO or `ApiResponse`
-> Mobile UI update

Authentication:

`LoginActivity.kt` / `RegisterActivity.kt`
-> `AuthApi.kt`
-> `ApiClient.kt`
-> `AuthController.java`
-> `AuthService.java`
-> `UserRepository.java`
-> JWT response
-> `TokenManager.kt` / `SessionManager.kt`
-> role-based mobile access

Properties:

`PropertyListActivity.kt`
-> `PropertyRepository.kt`
-> `PropertyApi.kt`
-> `PropertyRegisteredController.java`
-> `PropertyService.java`
-> backend `PropertyRepository.java`
-> `PropertyCardDTO` / `PropertyUserDetailDTO`
-> `PropertyAdapter.kt` / `PropertyDetailActivity.kt`

Favorites:

`PropertyAdapter.kt` or `FavoritesActivity.kt`
-> `FavoriteRepository.kt`
-> `FavoriteApi.kt`
-> `FavoriteController.java`
-> `FavoriteService.java`
-> backend `FavoriteRepository.java`
-> `FavoriteDTO`
-> mobile card state or favorites list update

Articles:

`ArticleListActivity.kt`
-> `ArticleRepository.kt`
-> `ArticleApi.kt`
-> `ArticleController.java`
-> `ArticleService.java`
-> backend `ArticleRepository.java`
-> `ArticleCardDTO` / `ArticleDTO`
-> `ArticleAdapter.kt` / `ArticleDetailActivity.kt`

Career Application:

`CareerApplicationActivity.kt`
-> `CareerApplicationRepository.kt`
-> `CareerApplicationApi.kt`
-> `CareerApplicationController.java`
-> `CareerApplicationService.java`
-> `SupabaseStorageService.java`
-> backend `CareerApplicationRepository.java`
-> status or response DTO
-> mobile status and form update

Messaging:

`ConversationsActivity.kt` / `ConversationDetailActivity.kt`
-> `MessagingRepository.kt`
-> `MessagingApi.kt`
-> `MessagingController.java`
-> `MessagingService.java`
-> `ConversationRepository.java` / `MessageRepository.java` / `ConversationReadStateRepository.java`
-> `ConversationDTO` / `MessageDTO`
-> mobile conversation list or thread update

Real-time Messaging:

`MessagingSocketClient.kt`
-> SockJS/STOMP connection to `/ws/{serverId}/{sessionId}/websocket`
-> Authorization Bearer token in STOMP `CONNECT`
-> `/user/queue/messages` subscription
-> socket event received
-> REST reload of conversation list or active message thread
-> mobile UI refresh
<!-- DATA_FLOW:Mobile Application END -->
<!-- DATA_FLOW_END -->

## 5. Proof of Implementation

<!-- PROOF_START -->
<!-- PROOF:Shared Architecture START -->
### Shared Architecture Proof of Implementation

| System Part | File / Folder | What It Proves |
|---|---|---|
| Frontend Shared Folder | `web/src/shared` | Centralized reusable frontend code for auth context, shared components, shared utilities, and API helpers used across multiple features. |
| Frontend Shared Folder Entry | `web/src/shared/index.js` | The shared frontend layer is exposed through a single entry point that re-exports context, components, constants, and the axios helper. |
| Backend Shared Package | `backend/src/main/java/edu/cit/aligato/fortpointproperties/shared` | Centralized backend support code for API responses, error formatting, exception handling, JWT/security helpers, password validation, and upload validation. |
| Security / Config Dependency | `backend/src/main/java/edu/cit/aligato/fortpointproperties/shared/security/SecurityConfig.java`, `backend/src/main/java/edu/cit/aligato/fortpointproperties/shared/security/JwtAuthenticationFilter.java`, `backend/src/main/java/edu/cit/aligato/fortpointproperties/shared/security/JwtUtil.java`, `backend/src/main/resources/application.properties` | Shared security code is configured through application properties and reused by multiple backend domains for authentication, authorization, and request filtering. |
| Auth Dependency for Shared Security | `backend/src/main/java/edu/cit/aligato/fortpointproperties/auth/entity/User.java`, `backend/src/main/java/edu/cit/aligato/fortpointproperties/auth/repository/UserRepository.java`, `backend/src/main/java/edu/cit/aligato/fortpointproperties/auth/service/GoogleOAuthSuccessHandler.java` | Shared security depends on the auth package for user lookup, role extraction, and OAuth sign-in handoff. |
<!-- PROOF:Shared Architecture END -->
<!-- PROOF:User Management Feature START -->
### User Management Feature Proof of Implementation

| System Part | File / Folder | What It Proves |
|---|---|---|
| Frontend Feature | `web/src/features/usermanagement` | The user management feature is organized as its own frontend module. |
| Frontend Page / Component | `web/src/features/usermanagement/pages/UserManagementPage.jsx` | Admin user management screen with search, filters, details, add, role change, and delete actions. |
| Frontend Page / Component | `web/src/features/usermanagement/components/UserList.jsx` and `web/src/features/usermanagement/components/UserListItem.jsx` | User listing and item display in the admin user management screen. |
| Frontend Page / Component | `web/src/features/usermanagement/components/UserDetailsModal.jsx` | User details view with actions to change role or remove a user. |
| Frontend Page / Component | `web/src/features/usermanagement/components/EditUserRoleModal.jsx` | User role update form. |
| Frontend Page / Component | `web/src/features/usermanagement/components/AddUserModal.jsx` | Admin create-user form. |
| Frontend Page / Component | `web/src/features/usermanagement/components/DeleteUserConfirmModal.jsx` | Delete confirmation flow for users. |
| Frontend API Layer | `web/src/features/usermanagement/api/userManagementApi.js` | API calls for listing, searching, creating, updating roles, and deleting users. |
| Backend Controller | `backend/src/main/java/edu/cit/aligato/fortpointproperties/usermanagement/controller/AdminUserController.java` | Admin endpoints for listing, creating, updating roles, and deleting users. |
| Backend Service | `backend/src/main/java/edu/cit/aligato/fortpointproperties/usermanagement/service/AdminUserService.java` | Core user management logic for search, create, role update, and delete. |
| Backend Repository | `backend/src/main/java/edu/cit/aligato/fortpointproperties/auth/repository/UserRepository.java` | User lookup, name search, and role filtering queries. |
| Entity / DTO | `backend/src/main/java/edu/cit/aligato/fortpointproperties/auth/entity/User.java` | User database entity and `users` table mapping. |
| Entity / DTO | `backend/src/main/java/edu/cit/aligato/fortpointproperties/usermanagement/dto/AdminUserResponseDTO.java` | Admin user response data shown in the UI. |
| Entity / DTO | `backend/src/main/java/edu/cit/aligato/fortpointproperties/usermanagement/dto/AdminCreateUserRequestDTO.java` | Admin create-user request shape. |
| Entity / DTO | `backend/src/main/java/edu/cit/aligato/fortpointproperties/usermanagement/dto/AdminUpdateUserRoleRequestDTO.java` | Admin role update request shape. |
| Security / Role Config | `backend/src/main/java/edu/cit/aligato/fortpointproperties/shared/security/SecurityConfig.java` | Admin-only access for `/api/admin/users` endpoints. |
| Security / Role Config | `web/src/App.jsx` and `web/src/app/Routes.jsx` | Frontend route behavior that sends admins to admin screens and protects admin routes. |

No proven layer appears missing for the capabilities listed above. The code does not show a separate activate/deactivate action, so that should not be claimed.
<!-- PROOF:User Management Feature END -->
<!-- PROOF:Authentication and Authorization Feature START -->
### Authentication and Authorization Proof of Implementation

| System Part | File / Folder | What It Proves |
|---|---|---|
| Frontend Auth Feature | `web/src/features/auth` | The authentication feature is organized as its own frontend module. |
| Login / Register Page | `web/src/features/auth/pages/LoginPage.jsx` and `web/src/features/auth/pages/RegisterPage.jsx` | The login and registration screens are implemented in the frontend. |
| Login / Register Page | `web/src/features/auth/pages/GoogleOAuthCallbackPage.jsx` | The frontend completes Google OAuth sign-in and applies the returned tokens. |
| Frontend Auth API Layer | `web/src/features/auth/api/authApi.js` | Frontend calls for login, registration, profile lookup, and Google auth URL generation. |
| Frontend Auth State / Context | `web/src/shared/context/AuthContext.jsx` and `web/src/shared/context/useAuthContext.js` | Auth state is stored in context and hydrated from localStorage. |
| Frontend Session Handling | `web/src/shared/utils/api.js` | The HTTP client adds the Bearer token and clears auth storage on 401 responses. |
| Frontend Route Behavior | `web/src/App.jsx` and `web/src/app/Routes.jsx` | Logged-in users are redirected by role and protected routes are enforced on the frontend. |
| Backend Auth Controller | `backend/src/main/java/edu/cit/aligato/fortpointproperties/auth/controller/AuthController.java` | Login, registration, profile lookup, and profile update endpoints. |
| Backend Auth Service | `backend/src/main/java/edu/cit/aligato/fortpointproperties/auth/service/AuthService.java` | User registration, password hashing, login validation, and Google user handling. |
| Backend OAuth Handler | `backend/src/main/java/edu/cit/aligato/fortpointproperties/auth/service/GoogleOAuthSuccessHandler.java` | Google OAuth sign-in returns tokens to the frontend callback. |
| User Repository | `backend/src/main/java/edu/cit/aligato/fortpointproperties/auth/repository/UserRepository.java` | User lookup, existence checks, and role/name queries. |
| User Entity / DTO | `backend/src/main/java/edu/cit/aligato/fortpointproperties/auth/entity/User.java` and `backend/src/main/java/edu/cit/aligato/fortpointproperties/auth/dto/UserDTO.java` | The user record and response shape include email, name, role, and profile image fields. |
| Auth Request / Response DTOs | `backend/src/main/java/edu/cit/aligato/fortpointproperties/auth/dto/LoginRequest.java`, `backend/src/main/java/edu/cit/aligato/fortpointproperties/auth/dto/RegisterRequest.java`, `backend/src/main/java/edu/cit/aligato/fortpointproperties/auth/dto/AuthResponse.java` | The request and response payloads for login, registration, and token return. |
| Security Config | `backend/src/main/java/edu/cit/aligato/fortpointproperties/shared/security/SecurityConfig.java` | Public auth endpoints, authenticated endpoints, role-based endpoints, CORS, and OAuth login setup. |
| JWT / Filter / Token Utility | `backend/src/main/java/edu/cit/aligato/fortpointproperties/shared/security/JwtUtil.java` and `backend/src/main/java/edu/cit/aligato/fortpointproperties/shared/security/JwtAuthenticationFilter.java` | Token creation, validation, Bearer token parsing, and security context role population. |
| Password Policy Helper | `backend/src/main/java/edu/cit/aligato/fortpointproperties/shared/utils/PasswordValidator.java` | Registration password length validation. |
| Application Config | `backend/src/main/resources/application.properties` | JWT, OAuth, database, and frontend callback configuration used by auth. |

No proven layer appears missing in the inspected auth code. Refresh-token exchange behavior is not shown as a standalone endpoint, so that part should be documented carefully if it is needed later.
<!-- PROOF:Authentication and Authorization Feature END -->
<!-- PROOF:Article Feature START -->
### Article Feature Proof of Implementation

| System Part | File / Folder | What It Proves |
|---|---|---|
| Frontend Feature | `web/src/features/article` | The article feature is organized as its own frontend module. |
| Frontend Page / Component | `web/src/features/article/pages/ArticleListPage.jsx` | Article listing, title search, admin create entry point, and read-more navigation. |
| Frontend Page / Component | `web/src/features/article/pages/ArticleDetailsPage.jsx` | Full article detail display for authenticated users. |
| Frontend Page / Component | `web/src/features/article/pages/ArticleCreatePage.jsx` | Admin article creation flow. |
| Frontend Page / Component | `web/src/features/article/pages/ArticleEditPage.jsx` | Admin article editing flow. |
| Frontend Page / Component | `web/src/features/article/components/ArticleForm.jsx` | Title, description, and cover photo form handling for create and edit. |
| Frontend API Layer | `web/src/features/article/api/articleApi.js` | Public article list fetch, authenticated article details, and admin create/update/delete API calls. |
| Backend Controller | `backend/src/main/java/edu/cit/aligato/fortpointproperties/article/controller/ArticleController.java` | Public article list and article detail endpoints. |
| Backend Controller | `backend/src/main/java/edu/cit/aligato/fortpointproperties/article/controller/ArticleAdminController.java` | Admin article create, update, and delete endpoints. |
| Backend Service | `backend/src/main/java/edu/cit/aligato/fortpointproperties/article/service/ArticleService.java` | Core article listing, search, detail, create, update, delete, and cover photo handling. |
| Backend Service | `backend/src/main/java/edu/cit/aligato/fortpointproperties/article/service/ArticleStorageService.java` | Cover photo upload, signed URL generation, and storage deletion for articles. |
| Backend Repository | `backend/src/main/java/edu/cit/aligato/fortpointproperties/article/repository/ArticleRepository.java` | Article list ordering and title search. |
| Entity / DTO | `backend/src/main/java/edu/cit/aligato/fortpointproperties/article/entity/Article.java` | Article entity and database table mapping. |
| Entity / DTO | `backend/src/main/java/edu/cit/aligato/fortpointproperties/article/dto/ArticleCardDTO.java` | Article card response for the list page. |
| Entity / DTO | `backend/src/main/java/edu/cit/aligato/fortpointproperties/article/dto/ArticleDTO.java` | Full article detail response. |
| Entity / DTO | `backend/src/main/java/edu/cit/aligato/fortpointproperties/article/dto/ArticleCreateRequestDTO.java` | Admin create request shape, including cover photo upload. |
| Entity / DTO | `backend/src/main/java/edu/cit/aligato/fortpointproperties/article/dto/ArticleUpdateRequestDTO.java` | Admin update request shape, including optional cover photo replacement. |
| Security / Config | `web/src/app/Routes.jsx` | Frontend routes that separate public article browsing from authenticated detail pages and admin pages. |
| Security / Config | `backend/src/main/java/edu/cit/aligato/fortpointproperties/shared/security/SecurityConfig.java` | Backend access control for public article listing, authenticated article details, and admin article actions. |
| Security / Config | `backend/src/main/resources/application.properties` | Backend configuration used by the article feature, including JWT, database, and Supabase settings. |

No proven layer appears missing in the inspected article code. The article feature is supported by frontend, backend, security, and storage code.
<!-- PROOF:Article Feature END -->
<!-- PROOF:Property Feature START -->
### Property Feature Proof of Implementation

| System Part | File / Folder | What It Proves |
|---|---|---|
| Frontend Feature | `web/src/features/properties` | The property feature is organized as its own frontend module. |
| Frontend Page / Component | `web/src/features/properties/pages/PropertyListPage.jsx` | Public property listing, search, filter, and detail entry points. |
| Frontend Page / Component | `web/src/features/properties/pages/AgentPropertiesListPage.jsx` | Agent property listing, filtering, and detail access. |
| Frontend Page / Component | `web/src/features/properties/pages/AdminPropertiesListPage.jsx` | Admin property management, including create, edit, and delete actions. |
| Frontend API Layer | `web/src/features/properties/api/propertyApi.js` | Role-based property API calls, search, CRUD calls, and property photo upload. |
| Frontend Shared UI | `web/src/shared/components/properties/PropertySearchFilter.jsx` | Search, filter, and sort controls used by the property pages. |
| Backend Controller | `backend/src/main/java/edu/cit/aligato/fortpointproperties/properties/controller/PropertyPublicController.java` | Public property list, search, and detail endpoints. |
| Backend Controller | `backend/src/main/java/edu/cit/aligato/fortpointproperties/properties/controller/PropertyRegisteredController.java` | Authenticated property list, search, and registered-user detail endpoint. |
| Backend Controller | `backend/src/main/java/edu/cit/aligato/fortpointproperties/properties/controller/PropertyAgentController.java` | Agent property list, search, and agent detail endpoint. |
| Backend Controller | `backend/src/main/java/edu/cit/aligato/fortpointproperties/properties/controller/PropertyAdminController.java` | Admin create, read, update, delete, search, amenity, photo upload, and unit endpoints. |
| Backend Service | `backend/src/main/java/edu/cit/aligato/fortpointproperties/properties/service/PropertyService.java` | Core property logic, including search, detail mapping, create, update, delete, and unit handling. |
| Backend Service | `backend/src/main/java/edu/cit/aligato/fortpointproperties/properties/service/PropertyPhotoStorageService.java` | Property photo upload to external storage and public photo URL generation. |
| Backend Repository | `backend/src/main/java/edu/cit/aligato/fortpointproperties/properties/repository/PropertyRepository.java` | Property query and search support for cards and filtered results. |
| Entity / DTO | `backend/src/main/java/edu/cit/aligato/fortpointproperties/properties/entity/Property.java` | Main property entity and table mapping. |
| Entity / DTO | `backend/src/main/java/edu/cit/aligato/fortpointproperties/properties/entity/PropertyUnit.java` | Property unit inventory entity and table mapping. |
| Entity / DTO | `backend/src/main/java/edu/cit/aligato/fortpointproperties/properties/entity/PropertyPhoto.java` | Property photo entity and table mapping. |
| Entity / DTO | `backend/src/main/java/edu/cit/aligato/fortpointproperties/properties/dto/PropertyCreateRequestDTO.java` | Admin create request shape for property data. |
| Entity / DTO | `backend/src/main/java/edu/cit/aligato/fortpointproperties/properties/dto/PropertyUpdateRequestDTO.java` | Admin update request shape for property data. |
| Entity / DTO | `backend/src/main/java/edu/cit/aligato/fortpointproperties/properties/dto/PropertyCardDTO.java` | Property card response used by list and search screens. |
| Entity / DTO | `backend/src/main/java/edu/cit/aligato/fortpointproperties/properties/dto/PropertyUserDetailDTO.java` | Registered-user property detail response. |
| Entity / DTO | `backend/src/main/java/edu/cit/aligato/fortpointproperties/properties/dto/PropertyAgentDetailDTO.java` | Agent property detail response. |
| Entity / DTO | `backend/src/main/java/edu/cit/aligato/fortpointproperties/properties/dto/PropertyAdminDetailDTO.java` | Admin property detail response with management fields. |
| Security / Config | `web/src/app/Routes.jsx` | Frontend route guards for public, agent, and admin property screens. |
| Security / Config | `backend/src/main/java/edu/cit/aligato/fortpointproperties/shared/security/SecurityConfig.java` | Backend access control for public, authenticated, agent, and admin property endpoints. |
| Security / Config | `backend/src/main/resources/application.properties` | Backend configuration for database, JWT, and Supabase storage used by the property feature. |

No layer appears missing in the code that was inspected for this feature. The README is still code-based proof, so a live screenshot or API capture can be added later if needed for presentation.
<!-- PROOF:Property Feature END -->
<!-- PROOF:Favorites Feature START -->
### Favorites Feature Proof of Implementation

| System Part | File / Folder | What It Proves |
|---|---|---|
| Frontend Feature | `web/src/features/favorites` | The favorites feature is organized as its own frontend module. |
| Frontend Component / Page | `web/src/features/favorites/pages/FavoritePage.jsx` | The favorites screen lists saved properties and supports removing items from the saved list. |
| Frontend Component / Page | `web/src/features/favorites/components/FavoriteButton.jsx` | The heart button UI supports adding and removing a property from favorites. |
| Frontend Component / Page | `web/src/shared/components/ui/FeaturedProperties.jsx` | Favorite buttons are wired into the featured property cards for logged-in users. |
| Frontend API Layer | `web/src/features/favorites/api/favoritesApi.js` | Frontend API calls for list, add, remove, check, and count operations. |
| Backend Controller | `backend/src/main/java/edu/cit/aligato/fortpointproperties/favorites/controller/FavoriteController.java` | Endpoints for listing, adding, removing, checking, and counting favorites. |
| Backend Service | `backend/src/main/java/edu/cit/aligato/fortpointproperties/favorites/service/FavoriteService.java` | Core favorites logic that links users to visible property records. |
| Backend Repository | `backend/src/main/java/edu/cit/aligato/fortpointproperties/favorites/repository/FavoriteRepository.java` | Queries for user favorites, favorite checks, and favorite counts. |
| Entity / DTO | `backend/src/main/java/edu/cit/aligato/fortpointproperties/favorites/entity/Favorite.java` | Database entity and `favorites` table mapping. |
| Entity / DTO | `backend/src/main/java/edu/cit/aligato/fortpointproperties/favorites/dto/FavoriteDTO.java` | Response shape returned to the favorites page. |
| Auth / Security Dependency | `web/src/app/Routes.jsx` and `backend/src/main/java/edu/cit/aligato/fortpointproperties/shared/security/SecurityConfig.java` | Favorites routes require login in the frontend and authenticated access in the backend. |
| Auth / Security Dependency | `web/src/shared/utils/constants.js` | Favorite API endpoints are defined as authenticated user routes. |

No proven layer appears missing for the capabilities listed above. The code shows favorites tied to authenticated users and visible property records.
<!-- PROOF:Favorites Feature END -->
<!-- PROOF:Messaging Feature START -->
### Messaging Feature Proof of Implementation

| System Part | File / Folder | What It Proves |
|---|---|---|
| Frontend Feature | `web/src/features/messaging` | The messaging feature is organized as its own frontend module. |
| Frontend Component / Page | `web/src/features/messaging/pages/RegisteredUserMessagesPage.jsx` | Registered user inbox, conversation creation, and unread conversation list. |
| Frontend Component / Page | `web/src/features/messaging/pages/AgentInboxPage.jsx` | Agent inbox for open and assigned conversations. |
| Frontend Component / Page | `web/src/features/messaging/pages/ConversationPage.jsx` | Conversation thread view, message history, read-state handling, and send message action. |
| Frontend Component / Page | `web/src/features/messaging/components/FloatingChatWidget.jsx` | Floating chat launcher with quick access to conversations and new conversation entry. |
| Frontend API Layer | `web/src/features/messaging/api/messagingApi.js` | API calls for create, list, fetch messages, send, and mark-as-read operations. |
| Backend Controller | `backend/src/main/java/edu/cit/aligato/fortpointproperties/messaging/controller/MessagingController.java` | REST endpoints for conversations, messages, and read-state updates. |
| Backend Service | `backend/src/main/java/edu/cit/aligato/fortpointproperties/messaging/service/MessagingService.java` | Core messaging logic, access checks, unread counts, and WebSocket broadcasts. |
| Backend Repository | `backend/src/main/java/edu/cit/aligato/fortpointproperties/messaging/repository/ConversationRepository.java` | Conversation listing, locking, and update queries. |
| Backend Repository | `backend/src/main/java/edu/cit/aligato/fortpointproperties/messaging/repository/MessageRepository.java` | Message history and unread-count queries. |
| Backend Repository | `backend/src/main/java/edu/cit/aligato/fortpointproperties/messaging/repository/ConversationReadStateRepository.java` | Read-state lookup and storage for unread tracking. |
| Entity / DTO | `backend/src/main/java/edu/cit/aligato/fortpointproperties/messaging/entity/Conversation.java` | Conversation table mapping and status fields. |
| Entity / DTO | `backend/src/main/java/edu/cit/aligato/fortpointproperties/messaging/entity/Message.java` | Message table mapping and sender role fields. |
| Entity / DTO | `backend/src/main/java/edu/cit/aligato/fortpointproperties/messaging/entity/ConversationReadState.java` | Read-state table mapping for unread tracking. |
| Entity / DTO | `backend/src/main/java/edu/cit/aligato/fortpointproperties/messaging/dto/ConversationDTO.java` | Conversation response shape with unread counts and latest message preview. |
| Entity / DTO | `backend/src/main/java/edu/cit/aligato/fortpointproperties/messaging/dto/MessageDTO.java` | Message response shape used by the thread UI. |
| Auth / Security Dependency | `backend/src/main/java/edu/cit/aligato/fortpointproperties/messaging/WebSocketConfig.java` | JWT-authenticated STOMP connection setup for real-time messaging. |
| Auth / Security Dependency | `backend/src/main/java/edu/cit/aligato/fortpointproperties/shared/security/SecurityConfig.java` | Messaging endpoints require authentication in the backend security rules. |
| Auth / Security Dependency | `backend/src/main/java/edu/cit/aligato/fortpointproperties/auth/repository/UserRepository.java` | Authenticated user lookup used to resolve the current user from the token identity. |
| Auth / Security Dependency | `web/src/app/Routes.jsx` | Frontend route guards that send logged-in users to `/messages` and agents to `/agent/messages`. |

The code shows a clear REST plus WebSocket messaging flow. No separate email or push notification path is proven here.
<!-- PROOF:Messaging Feature END -->
<!-- PROOF:Profile Feature START -->
### Profile Feature Proof of Implementation

| System Part | File / Folder | What It Proves |
|---|---|---|
| Frontend Feature | `web/src/features/profile` | The profile feature is organized as its own frontend module. |
| Frontend Component / Page | `web/src/features/profile/pages/RegisteredUserProfile.jsx` | Registered user profile screen that loads and shows the current user profile. |
| Frontend Component / Page | `web/src/features/profile/pages/AgentProfile.jsx` | Agent profile screen with the shared profile panel. |
| Frontend Component / Page | `web/src/features/profile/pages/AdminProfile.jsx` | Admin profile screen with the shared profile panel. |
| Frontend Component / Page | `web/src/features/profile/components/ProfilePanel.jsx` | Profile editing UI for name, email display, phone number, and profile image actions. |
| Frontend Component / Page | `web/src/features/profile/components/ProfileImageUploader.jsx` | Avatar upload and removal UI with preview support. |
| Frontend API Layer | `web/src/features/profile/api/profileApi.js` | API calls for viewing, updating, uploading, and removing profile data. |
| Backend Controller | `backend/src/main/java/edu/cit/aligato/fortpointproperties/auth/controller/AuthController.java` | Profile, current-user, profile image, and profile update endpoints. |
| Backend Service | `backend/src/main/java/edu/cit/aligato/fortpointproperties/auth/service/AuthService.java` | Profile data loading, profile update logic, and profile image storage handling. |
| Backend Service | `backend/src/main/java/edu/cit/aligato/fortpointproperties/auth/service/ProfileImageStorageService.java` | Profile image upload and deletion handling. |
| Backend Repository | `backend/src/main/java/edu/cit/aligato/fortpointproperties/auth/repository/UserRepository.java` | Current-user lookup and persistence for profile updates. |
| User Entity / DTO | `backend/src/main/java/edu/cit/aligato/fortpointproperties/auth/entity/User.java` | User table mapping for profile fields and image fields. |
| User Entity / DTO | `backend/src/main/java/edu/cit/aligato/fortpointproperties/auth/dto/UserDTO.java` | Profile response shape returned to the frontend. |
| User Entity / DTO | `backend/src/main/java/edu/cit/aligato/fortpointproperties/auth/dto/UpdateProfileRequest.java` | Request shape for profile field updates. |
| Auth / Security Dependency | `backend/src/main/java/edu/cit/aligato/fortpointproperties/shared/security/SecurityConfig.java` | Profile endpoints are authenticated and tied to the current logged-in user. |
| Auth / Security Dependency | `web/src/App.jsx` and `web/src/app/Routes.jsx` | Role-based routing to registered user, agent, and admin profile screens. |

No proven layer appears missing for the profile features listed above. The code does not show a separate profile backend package, so the auth package is the right implementation proof.
<!-- PROOF:Profile Feature END -->
<!-- PROOF:Career Application Feature START -->
### Career Application Feature Proof of Implementation

| System Part | File / Folder | What It Proves |
|---|---|---|
| Frontend Feature | `web/src/features/careerApplication` | The career application feature is organized as its own frontend module. |
| Frontend Component / Page | `web/src/features/careerApplication/pages/CareerApplicationPage.jsx` | Registered user application screen with current status and submit flow. |
| Frontend Component / Page | `web/src/features/careerApplication/pages/AdminCareerApplicationsPage.jsx` | Admin application list and review dashboard. |
| Frontend Component / Page | `web/src/features/careerApplication/pages/AdminCareerApplicationDetailsPage.jsx` | Admin detail view for reviewing one application. |
| Frontend Component / Page | `web/src/features/careerApplication/components/CareerApplicationForm.jsx` | Form fields for phone number, resume upload, and cover letter submission. |
| Frontend Component / Page | `web/src/features/careerApplication/components/ApplicationActionButtons.jsx` | Admin accept and reject actions with optional remarks. |
| Frontend API Layer | `web/src/features/careerApplication/api/careerApplicationApi.js` | API calls for submit, list, detail, accept, reject, current application, and resume URL. |
| Backend Controller | `backend/src/main/java/edu/cit/aligato/fortpointproperties/careerapplication/controller/CareerApplicationController.java` | Endpoints for submitting, viewing, listing, and reviewing applications. |
| Backend Service | `backend/src/main/java/edu/cit/aligato/fortpointproperties/careerapplication/service/CareerApplicationService.java` | Core application validation, storage, review, status changes, and role promotion logic. |
| Backend Repository | `backend/src/main/java/edu/cit/aligato/fortpointproperties/careerapplication/repository/CareerApplicationRepository.java` | Queries for application lists, current user application lookup, and pending-application checks. |
| Entity / DTO | `backend/src/main/java/edu/cit/aligato/fortpointproperties/careerapplication/entity/CareerApplication.java` | Career application database entity and table mapping. |
| Entity / DTO | `backend/src/main/java/edu/cit/aligato/fortpointproperties/careerapplication/entity/CareerApplicationStatus.java` | Application status values: pending, accepted, and rejected. |
| Entity / DTO | `backend/src/main/java/edu/cit/aligato/fortpointproperties/careerapplication/dto/CreateCareerApplicationDTO.java` | Multipart submission request for phone number, resume, and cover letter. |
| Entity / DTO | `backend/src/main/java/edu/cit/aligato/fortpointproperties/careerapplication/dto/CareerApplicationResponseDTO.java` | Response shape returned to the frontend. |
| Auth / Security Dependency | `backend/src/main/java/edu/cit/aligato/fortpointproperties/shared/security/SecurityConfig.java` | Registered user and admin access rules for career application endpoints. |
| Auth / Security Dependency | `backend/src/main/java/edu/cit/aligato/fortpointproperties/auth/entity/User.java` and `backend/src/main/java/edu/cit/aligato/fortpointproperties/auth/repository/UserRepository.java` | Current user lookup and role update support during application review. |

No proven layer appears missing for the capabilities listed above. Resume file upload and admin review are both shown in code.
<!-- PROOF:Career Application Feature END -->
<!-- PROOF:Public Pages Feature START -->
### Public Pages Feature Proof of Implementation

| System Part | File / Folder | What It Proves |
|---|---|---|
| Frontend Public Feature | `web/src/features/public` | The public pages feature is organized as its own frontend module. |
| Frontend Page / Component | `web/src/features/public/pages/HomePage.jsx` | Public landing page with hero content, featured properties, and footer content. |
| Shared Component | `web/src/shared/components/ui/HeroSection.jsx` | Hero banner with property search entry point and public landing presentation. |
| Shared Component | `web/src/shared/components/ui/FeaturedProperties.jsx` | Featured property preview and property detail access from the landing page. |
| Shared Component | `web/src/shared/components/layout/Header.jsx` | Public navigation to the landing page, properties, and blogs. |
| Frontend API Dependency | `web/src/features/properties/api/propertyApi.js` | Public property fetch and public featured property fetch used by the landing page. |
| Backend API Dependency | `backend/src/main/java/edu/cit/aligato/fortpointproperties/properties/controller/PropertyPublicController.java` | Public property list, detail, and search endpoints used by the landing page flow. |
| Backend API Dependency | `backend/src/main/java/edu/cit/aligato/fortpointproperties/properties/service/PropertyService.java` | Public property data assembly for cards and search results. |
| Backend API Dependency | `backend/src/main/java/edu/cit/aligato/fortpointproperties/properties/repository/PropertyRepository.java` | Property queries used for public cards and search. |
| Backend API Dependency | `backend/src/main/java/edu/cit/aligato/fortpointproperties/properties/dto/PropertyCardDTO.java` | Public property card response shape. |

No separate public backend package exists. The public pages are powered by shared frontend UI and the public property API.
<!-- PROOF:Public Pages Feature END -->
<!-- PROOF:Mobile Application START -->
### Mobile Application Proof of Implementation

| System Part | File / Folder | What It Proves |
|---|---|---|
| Mobile App Root | `mobile/app` | Android application module is present in the project. |
| Mobile Build Config | `mobile/app/build.gradle.kts` | Android build setup and dependencies for the mobile app. |
| Mobile Manifest | `mobile/app/src/main/AndroidManifest.xml` | Registered Android activities and app configuration. |
| Mobile Feature Folders | `mobile/app/src/main/java/com/example/fortpointproperties/features` | Vertical-slice mobile feature organization. |
| Shared Mobile Auth | `mobile/app/src/main/java/com/example/fortpointproperties/shared/auth/TokenManager.kt` | Mobile token persistence for authenticated requests. |
| Shared Mobile Auth | `mobile/app/src/main/java/com/example/fortpointproperties/shared/auth/SessionManager.kt` | Mobile role normalization and registered-user access checks. |
| Shared Mobile Network | `mobile/app/src/main/java/com/example/fortpointproperties/shared/network/ApiClient.kt` | Shared Retrofit backend connection. |
| Shared Mobile Network | `mobile/app/src/main/java/com/example/fortpointproperties/shared/network/AuthInterceptor.kt` | Bearer token attachment for mobile API calls. |
| Mobile Authentication | `mobile/app/src/main/java/com/example/fortpointproperties/features/auth/ui/LoginActivity.kt` and `mobile/app/src/main/java/com/example/fortpointproperties/features/auth/ui/RegisterActivity.kt` | Login and registration screens for mobile users. |
| Mobile Profile | `mobile/app/src/main/java/com/example/fortpointproperties/features/auth/ui/ProfileActivity.kt` | Profile view/edit, phone update, profile image upload, and image removal. |
| Mobile Properties | `mobile/app/src/main/java/com/example/fortpointproperties/features/properties` | Property list, detail, search, image loading, and image preview implementation. |
| Mobile Favorites | `mobile/app/src/main/java/com/example/fortpointproperties/features/favorites` | Favorite list, add/remove integration, and registered-user favorites API access. |
| Mobile Articles | `mobile/app/src/main/java/com/example/fortpointproperties/features/article` | Article list, title search, and article detail implementation. |
| Mobile Career Application | `mobile/app/src/main/java/com/example/fortpointproperties/features/careerApplication` | Career application form, status display, resume upload, and validation. |
| Mobile Messaging | `mobile/app/src/main/java/com/example/fortpointproperties/features/messaging` | Conversations list, conversation detail, REST sending, and real-time receiving. |
| Mobile Messaging Socket | `mobile/app/src/main/java/com/example/fortpointproperties/features/messaging/network/MessagingSocketClient.kt` | SockJS/STOMP WebSocket connection and `/user/queue/messages` subscription. |
| Mobile Layouts | `mobile/app/src/main/res/layout` | XML screens and card layouts for completed mobile modules. |
| Mobile Drawables | `mobile/app/src/main/res/drawable` | Mobile icons, card backgrounds, avatar backgrounds, navigation icons, and message bubbles. |
| Mobile Values | `mobile/app/src/main/res/values` | Shared colors and strings used by the Android screens. |

The mobile proof is registered-user focused. Admin and Agent mobile screens are not implemented and should not be claimed.
<!-- PROOF:Mobile Application END -->
<!-- PROOF_END -->

## 6. Mobile Application Module - Implemented

The Android mobile application is implemented and connected to the same Spring Boot backend used by the web frontend.

The mobile app focuses on registered-user workflows. It supports authentication, profile view/edit, property browsing, property details, property search, property image preview, favorites, articles, article title search, career application with resume upload, and messaging.

The mobile app uses Retrofit, OkHttp, Gson, and Glide. Shared mobile infrastructure includes `ApiClient`, `AuthInterceptor`, `TokenManager`, and `SessionManager`.

Messaging on mobile uses REST for creating conversations, loading messages, sending messages, and marking read state. Real-time receiving uses SockJS/STOMP WebSocket with the `/user/queue/messages` subscription.

Admin and Agent mobile screens are not implemented. Admin and Agent functions remain available through the web frontend where documented.

## 7. Suggested System Demonstration Flow

Use this section as a presentation guide for the features that have already been documented.

<!-- DEMO_FLOW_START -->
<!-- DEMO:User Management Feature START -->
### User Management Feature Demo Step

1. Show the admin user management screen at `/admin/users`.
2. Search for a user, open the user details modal, create a user, or update a user role.
3. Show the matching backend proof in `AdminUserController.java` and `AdminUserService.java`, plus `userManagementApi.js` on the frontend.
4. The expected result is that the user list refreshes and the updated user data appears in the UI.
<!-- DEMO:User Management Feature END -->
<!-- DEMO:Authentication and Authorization Feature START -->
### Authentication and Authorization Demo Step

1. Show the login screen or registration screen.
2. Perform a login, registration, or Google sign-in action.
3. Show the resulting redirect, stored session state, or role-based landing page.
4. Show the backend auth controller, auth service, and security config as proof of the flow.
5. If appropriate, show the token-bearing request behavior or protected route redirect in the frontend.
<!-- DEMO:Authentication and Authorization Feature END -->
<!-- DEMO:Article Feature START -->
### Article Feature Demo Step

1. Show the article list on `/blogs` or the mobile Articles screen.
2. Perform a title search or open an article card to read more.
3. Show the matching API proof in `articleApi.js` and the backend proof in `ArticleController.java` or `ArticleAdminController.java`.
4. If demonstrating admin work, show the create or edit screen and submit an article with a cover photo.
5. The expected result is that the article list updates, the detail page opens correctly, or the admin action saves successfully.
<!-- DEMO:Article Feature END -->
<!-- DEMO:Property Feature START -->
### Property Feature Demo Step

1. Show the property screen first, either `/properties`, `/admin/properties`, or the mobile Properties screen.
2. Perform one visible action such as searching by name/location/developer, opening a property detail, creating a property, or editing a property as an admin.
3. Show the matching backend proof file or endpoint, such as `PropertyPublicController.java`, `PropertyAdminController.java`, or `propertyApi.js`.
4. Show the returned property data or updated response, including property cards, detail data, or the new or updated property payload.
5. If photo upload is part of the demo, show the property photo upload flow and the resulting photo URL or stored photo record.
6. The expected result is that the property screen updates correctly and the backend response matches the action performed.
<!-- DEMO:Property Feature END -->
<!-- DEMO:Favorites Feature START -->
### Favorites Feature Demo Step

1. Show the web property list, featured property cards, or mobile Properties screen where the heart button appears for a logged-in user.
2. Click the favorite heart to add a property, then open the `/favorites` page or mobile Favorites screen to show the saved list.
3. Show the matching backend proof in `FavoriteController.java` and `FavoriteService.java`, plus `favoritesApi.js` on the frontend.
4. The expected result is that the property appears in the saved list, the heart state updates, and removing it updates the UI.
<!-- DEMO:Favorites Feature END -->
<!-- DEMO:Messaging Feature START -->
### Messaging Feature Demo Step

1. Show the registered user messaging screen, mobile Conversations screen, or the web agent inbox screen.
2. Start a new conversation or open an existing thread, then send a message.
3. Show the matching backend proof in `MessagingController.java`, `MessagingService.java`, and `messagingApi.js` on the frontend.
4. If demonstrating live updates, show the WebSocket connection in `WebSocketConfig.java` and the socket hook in `useMessagingSocket.js`.
5. The expected result is that the conversation list updates, the message thread refreshes, and unread counts or inbox state change correctly.
<!-- DEMO:Messaging Feature END -->
<!-- DEMO:Profile Feature START -->
### Profile Feature Demo Step

1. Show one of the web profile screens or the mobile registered-user Profile screen.
2. Edit the first name, last name, or phone number, then upload or remove a profile image.
3. Show the matching backend proof in `AuthController.java` and `AuthService.java`, plus `profileApi.js` on the frontend.
4. The expected result is that the profile panel updates and the new profile data or avatar is reflected in the UI.
<!-- DEMO:Profile Feature END -->
<!-- DEMO:Career Application Feature START -->
### Career Application Feature Demo Step

1. Show the registered user career application screen at `/career` or the mobile Career screen.
2. Submit a resume, phone number, and cover letter, then show the status card update.
3. Show the matching backend proof in `CareerApplicationController.java` and `CareerApplicationService.java`, plus `careerApplicationApi.js` on the frontend.
4. For admin review, show `/admin/career-applications` or the detail page, then accept or reject an application.
5. The expected result is that the application appears in the admin table, the detail status updates, and an accepted application becomes an agent role in the backend.
<!-- DEMO:Career Application Feature END -->
<!-- DEMO:Public Pages Feature START -->
### Public Pages Feature Demo Step

1. Show the home page at `/`.
2. Use the hero search or open the featured properties section.
3. Show the matching frontend proof in `HomePage.jsx`, `HeroSection.jsx`, and `FeaturedProperties.jsx`, plus `PropertyPublicController.java` and `propertyApi.js`.
4. The expected result is that visitors can browse the landing page, navigate to property browsing, and see featured properties load correctly.
<!-- DEMO:Public Pages Feature END -->
<!-- DEMO:Mobile Application START -->
### Mobile Application Demo Step

1. Run the Android mobile app.
2. Log in as a registered user.
3. Show the shared mobile header and bottom navigation.
4. Open Properties, search by project name/location/developer, and open a property detail.
5. Toggle a property favorite, then open Favorites.
6. Open Articles, search by title, and open article detail.
7. Open Career, submit or show application status and resume upload behavior.
8. Open Profile and show editable profile fields and profile image behavior.
9. Open Messaging from the header icon, start or open a conversation, send a message, and show a real-time reply from the web agent side.
10. Briefly show proof files such as `ApiClient.kt`, `TokenManager.kt`, `PropertyApi.kt`, `CareerApplicationApi.kt`, and `MessagingSocketClient.kt`.
<!-- DEMO:Mobile Application END -->
<!-- DEMO_FLOW_END -->

## 8. Rubric Alignment Checklist

- Clear system introduction
- Visible system structure documented
- Frontend and backend separation explained
- Shared architecture explained as reusable code
- REST API data flow explained
- Mobile-web-backend interaction explained
- Mobile proof files included
- Real-time messaging proof included
- File upload proof included
- Mobile module documented as implemented and connected to the backend
- Proof of implementation reserved for confirmed evidence
- Future updates can be added one feature at a time

## 9. Notes and Limitations

This section records shared limitations, partial evidence, and any feature-specific proof gaps that should be called out during presentation.

<!-- LIMITATIONS_START -->
<!-- LIMITATION:Shared Architecture START -->
### Shared Architecture Notes

`web/src/shared` is the proven shared frontend layer in the current project structure.

No separate `web/src/features/shared` implementation was found during inspection, so shared feature reuse should be documented through `web/src/shared` instead.

The backend shared package is present and actively used by auth, routing, and feature-specific controllers, so it is safe to describe as reusable infrastructure rather than a feature-specific module.
<!-- LIMITATION:Shared Architecture END -->
<!-- LIMITATION:Mobile Application START -->
### Mobile Application Notes

The mobile app currently focuses on registered-user workflows.

Admin and Agent mobile screens are not implemented. Admin and Agent management features remain available in the web app where documented.

The mobile app uses the same backend data, authentication flow, REST endpoints, and service layer as the web app.

Mobile messaging sends and loads messages through REST. It receives live updates through SockJS/STOMP WebSocket and then refreshes the relevant mobile screen from REST.

Mobile file upload is proven for profile images and career application resumes.

The code does not prove mobile push notifications or email notifications, so those should not be claimed.
<!-- LIMITATION:Mobile Application END -->
<!-- LIMITATION:User Management Feature START -->
### User Management Feature Notes

The code proves user listing, search, role filtering, creation, role update, and deletion.

The feature does not show a separate activate/deactivate workflow, so that should not be claimed.

The backend response marks users as active, but no independent active/inactive toggle was found in the inspected files.

The inspected code shows admin-only user management access through security and route protection.
<!-- LIMITATION:User Management Feature END -->
<!-- LIMITATION:Authentication and Authorization Feature START -->
### Authentication and Authorization Notes

The inspected code proves login, registration, Google OAuth, JWT creation and validation, password hashing, and frontend route protection.

A dedicated refresh-token exchange endpoint was not shown in the reviewed files, so refresh-token lifecycle behavior is only partially proven here.

Future auth updates should stay focused on one capability at a time, such as refresh handling or a profile-specific flow.
<!-- LIMITATION:Authentication and Authorization Feature END -->
<!-- LIMITATION:Article Feature START -->
### Article Feature Notes

The article feature is implemented in code, but this README does not include a live screenshot, API capture, or database export yet.

Public article browsing is proven by the list page and public controller, while full article details are protected by the authenticated route and backend security rules.

Admin article actions are proven by the admin page, admin API layer, admin controller, and article storage service.
<!-- LIMITATION:Article Feature END -->
<!-- LIMITATION:Property Feature START -->
### Property Feature Notes

The property feature is implemented in code, but this README does not include a live screenshot or database export.

The documentation is based on the inspected frontend, backend, route, and configuration files only.

Role-based behavior is proven by the route guards and backend security configuration that were inspected.

Future prompts should update one feature only so the document stays accurate and easy to review.
<!-- LIMITATION:Property Feature END -->
<!-- LIMITATION:Favorites Feature START -->
### Favorites Feature Notes

The code proves favorites are tied to authenticated users and visible property records.

The feature does not show an anonymous favorites flow, so that should not be claimed.

The code shows add, remove, list, check, and count behavior, but it does not show a separate public favorites management screen beyond the logged-in favorites page and property card heart button.
<!-- LIMITATION:Favorites Feature END -->
<!-- LIMITATION:Messaging Feature START -->
### Messaging Feature Notes

The code proves authenticated user and agent messaging with REST endpoints, read-state tracking, and WebSocket updates.

The code does not show email delivery or any external notification service, so those should not be claimed.

The conversation content suggests property questions, but the code does not attach a property ID directly to conversations, so property-linked messaging should not be claimed unless a future feature proves it.
<!-- LIMITATION:Messaging Feature END -->
<!-- LIMITATION:Profile Feature START -->
### Profile Feature Notes

The code proves profile viewing, profile field updates, and profile image upload/removal through the auth package.

The profile page does not allow email changes from the profile screen, so that should not be described as a profile edit capability.

There is no separate backend profile package in the inspected files, so the profile proof should stay attached to the auth package and shared user model.
<!-- LIMITATION:Profile Feature END -->
<!-- LIMITATION:Career Application Feature START -->
### Career Application Feature Notes

The code proves career application submission, admin review, resume upload, and resume signed URL access.

The application flow is tied to registered users for submission and admins for review, so those roles should be named explicitly.

Accepting an application updates the applicant's role to agent in the backend, but this should be described as part of the acceptance flow rather than a separate role-management feature.
<!-- LIMITATION:Career Application Feature END -->
<!-- LIMITATION:Public Pages Feature START -->
### Public Pages Feature Notes

The public pages feature is mostly a presentation layer, but it does call the public property API for featured properties and property searches.

The feature does not have a separate backend package of its own.

The public pages should not be described as authenticated behavior, and article data is not proven from the public feature files alone.
<!-- LIMITATION:Public Pages Feature END -->
<!-- LIMITATIONS_END -->
