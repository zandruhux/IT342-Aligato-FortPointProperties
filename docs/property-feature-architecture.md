# Property Feature Architecture

## 1. Purpose and Usefulness

The Property feature manages real estate listings for FortPoint Properties. It lets admins create and maintain property records, lets agents browse full read-only property information, and lets public or registered users browse available properties.

The feature is useful because it centralizes the core listing workflow of the application:

- Admins can create, update, delete, hide/show, feature, and manage property details, units, photos, financing types, listing types, amenities, and resources.
- Agents can search and view full property details for client assistance without gaining write access.
- Public users can view property cards and limited listing information.
- Registered users can view limited details and use favorites through the connected Favorites feature.

## 2. System Architecture

The Property feature uses a client-server architecture:

- React/Vite renders role-based property pages and shared UI components.
- Frontend hooks and `propertyApi.js` call Spring Boot REST endpoints.
- Spring controllers enforce role-specific routes and delegate to `PropertyService`.
- The service maps request DTOs to entities, resolves amenities, saves units/photos, and returns role-specific DTOs.
- Repositories query PostgreSQL/Supabase tables.
- Supabase Storage is used for admin property photo uploads; the database stores the resulting public URL and display order.

Frontend vertical slice:

```text
web/src/features/properties
  api/propertyApi.js
  hooks/useProperties.js
  hooks/usePropertySearch.js
  hooks/usePropertyDetailAccess.js
  pages/PropertyListPage.jsx
  pages/AgentPropertiesListPage.jsx
  pages/AdminPropertiesListPage.jsx
  components/PropertyCard.jsx
  components/PropertyCardBase.jsx
  components/PropertyDetailModal.jsx
  components/admin/AdminPropertyCreateModal.jsx
```

Backend vertical slice:

```text
backend/src/main/java/edu/cit/aligato/fortpointproperties/properties
  controller/
  dto/
  entity/
  enums/
  repository/
  service/
```

Layered flow:

```text
UI Component -> Hook/API Layer -> Controller -> Service -> Repository -> Database
                                            -> PropertyPhotoStorageService -> Supabase Storage
```

Example frontend API role routing:

```js
export const searchProperties = async (role, params = {}) => {
  const finalRole = normalizeRole(role);
  return searchPropertiesByRole(finalRole, params);
};
```

Example backend controller to service flow:

```java
return ResponseEntity.ok(ApiResponse.success(
        propertyService.searchCards(name, location, developer, listingType, minPrice, maxPrice, true)));
```

## 3. Component Interaction

### Frontend

The role pages own workflow state:

- `PropertyListPage.jsx` handles public/registered browsing, search, details, and favorite actions.
- `AgentPropertiesListPage.jsx` handles agent search and read-only detail viewing.
- `AdminPropertiesListPage.jsx` handles admin list/search, create modal state, delete actions, and edit/detail modal state.

Shared components remain role-neutral:

- `PropertyCard.jsx` receives action props such as `onEdit`, `onDelete`, `onView`, and favorite props.
- `PropertyCardBase.jsx` renders the reusable card shell and cover photo.
- `PropertyDetailModal.jsx` renders role-aware details and admin edit controls.
- `PropertySearchFilter.jsx` is re-exported from `shared/components/properties` so property pages and public home search use the same search UI.

Property card role behavior is driven by capabilities, not role checks:

```jsx
<PropertyCard
  property={property}
  onClick={handlePropertyClick}
  onEdit={handleEditProperty}
  onDelete={handleDeleteProperty}
/>
```

Admin create/edit uses file uploads only. Uploaded file previews are shown before submit, then files are uploaded through `uploadPropertyPhoto`.

```js
for (const [index, file] of selectedPhotoFiles.entries()) {
  uploadedPhotos.push(await propertyApi.uploadPropertyPhoto(file, index));
}
```

### Backend

Controllers are split by access level:

- `PropertyPublicController`
- `PropertyRegisteredController`
- `PropertyAgentController`
- `PropertyAdminController`

DTOs are also role-specific:

- `PropertyCardDTO`
- `PropertyUserDetailDTO`
- `PropertyAgentDetailDTO`
- `PropertyAdminDetailDTO`
- `PropertyCreateRequestDTO`
- `PropertyUpdateRequestDTO`

`PropertyService` owns the core business mapping:

```java
property.setListingTypes(new HashSet<>(safeList(request.listingTypes)));
property.setFinancingTypes(new HashSet<>(safeList(request.financingTypes)));
property.setAmenities(resolveAmenities(request.amenityIds, request.customAmenities));
```

Card responses use optimized repository projections instead of loading full lazy collections:

```java
return propertyRepository.findCardRows(false).stream()
        .map(this::toCard)
        .toList();
```

The repository projection computes card price range and cover photo:

```sql
MIN(pu.total_selling_price) AS "priceRangeMin",
MAX(pu.total_selling_price) AS "priceRangeMax",
(
    SELECT pp.photo_url
    FROM property_photos pp
    WHERE pp.property_id = p.id
    ORDER BY pp.display_order ASC, pp.id ASC
    LIMIT 1
) AS "coverPhotoUrl"
```

## 4. Data Flow

### Creating a Property

1. Admin opens `AdminPropertyCreateModal`.
2. The modal collects core fields, listing types, financing types, amenities, units, and image files.
3. Image files are uploaded with `propertyApi.uploadPropertyPhoto`.
4. The create payload sends uploaded `photos`, `units`, `amenityIds`, and `customAmenities`.
5. `PropertyAdminController.createProperty` receives `PropertyCreateRequestDTO`.
6. `PropertyService.createProperty` applies the request, resolves amenities, attaches units/photos, sets `createdBy`, and saves.
7. The admin list refreshes and displays the new card.

### Updating a Property and Units

1. Admin opens `PropertyDetailModal` in edit mode.
2. Existing photos can be reordered, removed, or combined with newly uploaded files.
3. `toUpdatePayload` normalizes photo order by array index.
4. Admin unit edits are staged in local form state first; adding, editing, or deleting a unit does not call the backend immediately.
5. `PropertyService.updateProperty` reapplies property-level fields and replaces units only when the saved form explicitly supplies the full unit list.
6. The frontend derives a fresh `coverPhotoUrl` from the sorted photos so the list/card view updates immediately.

### Uploading and Ordering Property Photos

1. Admin selects image files.
2. Frontend validates and previews files locally.
3. `uploadPropertyPhoto` posts multipart data to `/admin/properties/photos/upload`.
4. `PropertyPhotoStorageService` uploads to Supabase Storage and returns the public URL.
5. The property save request stores only `photoUrl`, `displayOrder`, and `propertyId` through `PropertyPhoto`.
6. Card/list queries use the lowest `display_order` photo as the cover.

### Displaying Property Cards and Details

Cards use `PropertyCardDTO`, which includes only card fields: name, description, location, listing types, promo flag, price range, and cover photo.

Details use role-specific DTOs:

- Public users get cards only.
- Registered users get limited details.
- Agents get full read-only details.
- Admins get full editable details.

### Managing Amenities

The backend stores amenities in `amenities` and connects them through `property_amenities`.

Amenities are fetched with computed usage counts:

```java
return (defaultsOnly
        ? amenityRepository.findDefaultAmenitiesWithUsageCounts()
        : amenityRepository.findAllWithUsageCounts())
        .stream()
        .map(this::toAmenityDTO)
        .toList();
```

The create/edit UI displays the top five most-used amenities and provides a search input. Existing amenities can be selected, and new typed amenities are sent as `customAmenities`. The backend avoids duplicates by resolving names case-insensitively.

When an admin opens edit mode from the detail view, the UI loads the full amenity list and falls back to the current property's detail amenities for selected labels. This keeps selected amenities visible by name even before the full list finishes loading.

## 5. Notes on Cleanup

Cleanup performed:

- Removed unused legacy frontend files:
  - `web/src/features/properties/components/admin/AdminPropertiesSection.jsx`
  - `web/src/features/properties/components/agent/AgentPropertiesSection.jsx`
  - `web/src/features/properties/hooks/usePropertyDetails.js`
  - `web/src/features/properties/components/admin/AdminPropertyDetailsModal.jsx`
  - `web/src/features/properties/components/agent/AgentPropertyDetailsModal.jsx`
- Removed unused backend repository:
  - `backend/src/main/java/edu/cit/aligato/fortpointproperties/properties/repository/PropertyPhotoRepository.java`
- Removed unused service methods from `PropertyService`:
  - `searchByListingType`
  - `convertPropertyToCardDTO`
- Removed duplicate Favorites helpers from `propertyApi.js`; the Favorites feature owns those calls.
- Removed unused role-specific property search wrappers in `propertyApi.js`; active code uses the generic role-aware `searchProperties`.
- Removed unused default React imports and stale commented-out code.
- Removed noisy console logging from the property path and the directly connected favorites API.
- Cleaned noisy comments and broken currency glyphs in active property components.

Comments added:

- No new code comments were needed beyond existing concise comments. The cleanup favored removing noisy comments and documenting architecture here.

Files touched outside the main property folders:

- `web/src/app/Routes.jsx`: property dashboard route dependency.
- `web/src/features/careerApplication/pages/AgentDashboardPage.jsx`: Agent dashboard placeholder requested by the user.
- `web/src/features/favorites/api/favoritesApi.js`: directly connected to registered property favorites; removed stale comments and logging.
- `docs/property-feature-architecture.md`: this report.

Checks run:

- Scoped frontend lint passed:
  - `npx eslint src/features/properties src/shared/components/properties src/shared/utils/propertyHelpers.js src/shared/utils/searchHelpers.js src/features/favorites/api/favoritesApi.js`
- Frontend build passed:
  - `npm run build`
- Backend compile/test passed:
  - `.\mvnw.cmd test`
- Full frontend lint was also run, but it still fails due to unrelated messaging feature issues in:
  - `features/messaging/components/FloatingChatWidget.jsx`
  - `features/messaging/pages/AgentInboxPage.jsx`
  - `features/messaging/pages/ConversationPage.jsx`
  - `features/messaging/pages/RegisteredUserMessagesPage.jsx`
- No database schema changes were made.
