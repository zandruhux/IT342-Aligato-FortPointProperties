# IT342-Aligato-FortPointProperties

## Project Overview

Fort Point Properties is a property-focused platform with a React web frontend, Spring Boot backend, Android mobile app, PostgreSQL / Supabase database, and Supabase storage where implemented. It is designed to help public visitors browse properties, registered users save favorites and send messages, mobile registered users use core property and messaging flows, and admins or agents manage content and inquiries on the web.s

The project is organized as a client-server system. The web frontend and Android mobile app communicate with the backend REST API, while the backend handles authentication, authorization, data access, file storage integration, and response handling.

## Who Uses It

- Public visitors
- Registered users
- Real estate agents
- Administrators
- Mobile registered users

## Main Features

- Public pages and property browsing
- Authentication and authorization
- Google OAuth / Google Sign-In
- Property management
- Article or blog management
- Favorites
- Messaging
- Profile management
- Career applications
- User management
- Android mobile app for registered-user workflows

## Project Structure

- `web/` - React frontend
- `backend/` - Spring Boot backend
- `mobile/` - Android mobile app
- `docs/` - Documentation and SDD files

## Architecture Summary

- The web frontend is organized by feature folders and shared utilities.
- The backend is organized by domain packages such as auth, properties, article, messaging, favorites, career application, user management, and shared support code.
- The mobile app is organized by feature folders and shared network/auth helpers.
- Shared code helps avoid repeating common UI, API helpers, response wrappers, exception handling, security logic, and token management across the system.

## Mobile App

The mobile app is implemented for registered-user workflows. It supports login, registration, profile access, property browsing, favorites, articles, career application submission, and messaging. Admin and agent management workflows remain available through the web app only.
