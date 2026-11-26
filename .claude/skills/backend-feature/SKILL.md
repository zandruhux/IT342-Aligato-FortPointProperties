---
name: backend-feature
description: Scaffold or extend a Spring Boot feature in backend/ following the project's Controller-Service-Repository + DTO conventions (package layout, AppException, ApiResponse envelope, validation, security rules, tests). Use when adding an endpoint, entity, or feature module to the backend.
---

# Backend feature (Spring Boot 3.5 / Java 17)

Base package: `edu.cit.aligato.fortpointproperties`. Each feature is a vertical slice:

```
<feature>/
  controller/  <Name>Controller.java      @RestController, thin: validate -> call service -> wrap ApiResponse
  service/     <Name>Service.java         @Service, business rules, @Transactional on writes
  repository/  <Name>Repository.java      extends JpaRepository
  entity/      <Name>.java                @Entity, never returned from controllers
  dto/         <Name>Request/ResponseDTO  prefer Java records for new DTOs
```

## Rules (enforced by ArchUnit / Checkstyle / SpotBugs)

1. Controllers depend on services only — never on repositories or entities.
   Resolve the current user in a service (e.g. `AuthService`), not via `UserRepository` in the controller.
2. Constructor injection only (`private final` fields). No `@Autowired` fields, no `@Value` fields.
   Bind config with `@ConfigurationProperties` or constructor `@Value` params.
3. Request bodies: `@Valid @RequestBody XxxRequestDTO` with Bean Validation annotations
   (`@NotBlank`, `@Email`, `@Size`, `@Positive` ...).
4. Errors: throw `AppException(code, message, HttpStatus)` from services.
   `shared/exception/GlobalExceptionHandler` maps it to `ApiResponse.error(ErrorDetail)`.
   Never return stack traces or exception messages from unexpected errors to clients.
5. Responses: always wrap in `ApiResponse.success(data)`; use proper status codes (201 for create, 204 for delete).
6. Security: add every new route to `shared/security/SecurityConfig` explicitly with the
   narrowest role. Prefer `@PreAuthorize` for per-method rules. Never `permitAll()` a write endpoint.
   Check resource ownership in the service (user can only touch their own records).
7. Logging: SLF4J only, no `System.out`. Never log tokens, passwords, or full request bodies.
8. Strings → bytes: always pass `StandardCharsets.UTF_8`.
9. Files: treat `MultipartFile.getOriginalFilename()` as nullable; validate type/size with `ImageUploadValidator`.

## Tests to add

- `<Name>ServiceTest` — JUnit 5 + Mockito (`@ExtendWith(MockitoExtension.class)`), one test per rule/branch.
- `<Name>ControllerTest` — `@WebMvcTest` + `@MockitoBean` service, assert status + JSON envelope + validation errors.
- Test names: `method_scenario_expected` (e.g. `addToFavorites_alreadyFavorited_returnsFalse`).

Finish by running `./mvnw -B verify` from `backend/`.
