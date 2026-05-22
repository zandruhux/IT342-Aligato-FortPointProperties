# IT342-Aligato-FortPointProperties

## Project Overview
Fort Point Properties is a web-based property system with a Spring Boot backend. It is designed to help different users browse properties, read articles, save favorites, send messages, submit career applications, and manage accounts or content when allowed by role.

The project is built as a feature-based web application, with shared code for common UI and backend support. The web frontend talks to the backend REST API, and the backend handles authentication, authorization, data access, and shared response handling.

## Who Uses It
- Public visitors
- Registered users
- Real estate agents
- Administrators

## Main Features
- Public pages and property browsing
- Authentication and authorization
- Property management
- Article or blog management
- Favorites
- Messaging
- Profile management
- Career applications
- User management

## Project Structure
- `web/` - React frontend
- `backend/` - Spring Boot backend
- `docs/` - System architecture documentation
- `mobile/` - Planned for future development

## Architecture Summary
- The frontend is organized by feature folders and shared components.
- The backend is organized by domain packages such as auth, properties, article, messaging, favorites, career application, user management, and shared support code.
- Shared code helps avoid repeating common UI, API helpers, response wrappers, exception handling, and security logic across features.

## Mobile Module
The mobile application module is not yet implemented. It is planned for future development and will connect to the same backend REST API when work begins.

## Notes
For a detailed architecture breakdown, implementation proof, and feature-by-feature documentation, see the files in `docs/`, especially `docs/SYSTEM_ARCHITECTURE_README.md`.
