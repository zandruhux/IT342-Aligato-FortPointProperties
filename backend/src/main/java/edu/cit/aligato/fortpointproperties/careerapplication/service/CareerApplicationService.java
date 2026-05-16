package edu.cit.aligato.fortpointproperties.careerapplication.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import edu.cit.aligato.fortpointproperties.auth.entity.User;
import edu.cit.aligato.fortpointproperties.auth.repository.UserRepository;
import edu.cit.aligato.fortpointproperties.careerapplication.dto.CareerApplicationResponseDTO;
import edu.cit.aligato.fortpointproperties.careerapplication.dto.CreateCareerApplicationDTO;
import edu.cit.aligato.fortpointproperties.careerapplication.dto.UpdateCareerApplicationStatusDTO;
import edu.cit.aligato.fortpointproperties.careerapplication.entity.CareerApplication;
import edu.cit.aligato.fortpointproperties.careerapplication.entity.CareerApplicationStatus;
import edu.cit.aligato.fortpointproperties.careerapplication.repository.CareerApplicationRepository;
import edu.cit.aligato.fortpointproperties.careerapplication.service.SupabaseStorageService.UploadedFile;

@Service
public class CareerApplicationService {

    private static final String REGISTERED_USER_ROLE = "REGISTERED_USER";
    private static final String AGENT_ROLE = "AGENT";
    private static final Set<String> ALLOWED_RESUME_CONTENT_TYPES = Set.of(
            "application/pdf",
            "application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
    private static final Set<String> ALLOWED_RESUME_EXTENSIONS = Set.of("pdf", "doc", "docx");

    private final CareerApplicationRepository careerApplicationRepository;
    private final UserRepository userRepository;
    private final SupabaseStorageService supabaseStorageService;

    public CareerApplicationService(CareerApplicationRepository careerApplicationRepository,
            UserRepository userRepository,
            SupabaseStorageService supabaseStorageService) {
        this.careerApplicationRepository = careerApplicationRepository;
        this.userRepository = userRepository;
        this.supabaseStorageService = supabaseStorageService;
    }

    @Transactional
    public CareerApplicationResponseDTO submitCareerApplication(CreateCareerApplicationDTO request) {
        User currentUser = getCurrentUser();
        String role = normalizeRole(currentUser.getRole());

        if (!REGISTERED_USER_ROLE.equals(role)) {
            throw new IllegalArgumentException("Only registered users can submit career applications");
        }

        if (careerApplicationRepository.existsByUserAndStatus(currentUser, CareerApplicationStatus.PENDING)) {
            throw new IllegalArgumentException("You already have a pending career application");
        }

        validateSubmission(request);
        UploadedFile uploadedResume = supabaseStorageService.uploadCareerResume(currentUser.getId(), request.getResume());

        CareerApplication application = new CareerApplication();
        application.setUser(currentUser);
        application.setFirstname(currentUser.getFirstname());
        application.setLastname(currentUser.getLastname());
        application.setEmail(currentUser.getEmail());
        application.setPhoneNumber(request.getPhoneNumber().trim());
        application.setResumePath(uploadedResume.getPath());
        application.setResumeOriginalFilename(uploadedResume.getOriginalFilename());
        application.setResumeUrl(uploadedResume.getPath());
        application.setResumeFilename(uploadedResume.getOriginalFilename());
        application.setCoverLetter(request.getCoverLetter().trim());
        application.setStatus(CareerApplicationStatus.PENDING);

        return convertToDTO(careerApplicationRepository.save(application));
    }

    public List<CareerApplicationResponseDTO> getAllCareerApplications(CareerApplicationStatus status) {
        List<CareerApplication> applications = status == null
                ? careerApplicationRepository.findAll()
                : careerApplicationRepository.findByStatus(status);

        return applications.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public CareerApplicationResponseDTO getCareerApplicationById(String id) {
        CareerApplication application = careerApplicationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Career application not found"));
        return convertToDTO(application);
    }

    public CareerApplicationResponseDTO getCurrentUserCareerApplication() {
        User currentUser = getCurrentUser();
        return careerApplicationRepository.findFirstByUserOrderBySubmittedAtDesc(currentUser)
                .map(this::convertToDTO)
                .orElse(null);
    }

    public String createResumeSignedUrl(String applicationId) {
        CareerApplication application = careerApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new IllegalArgumentException("Career application not found"));
        return supabaseStorageService.createSignedUrl(application.getResumePath());
    }

    @Transactional
    public CareerApplicationResponseDTO acceptCareerApplication(String id, UpdateCareerApplicationStatusDTO request) {
        CareerApplication application = getPendingApplication(id);
        User reviewer = getCurrentUser();
        User applicant = application.getUser();

        application.setStatus(CareerApplicationStatus.ACCEPTED);
        application.setReviewedAt(LocalDateTime.now());
        application.setReviewedBy(reviewer);
        application.setAdminRemarks(request == null ? null : request.getRemarks());

        applicant.setRole(AGENT_ROLE);
        userRepository.save(applicant);

        return convertToDTO(careerApplicationRepository.save(application));
    }

    @Transactional
    public CareerApplicationResponseDTO rejectCareerApplication(String id, UpdateCareerApplicationStatusDTO request) {
        CareerApplication application = getPendingApplication(id);
        User reviewer = getCurrentUser();

        application.setStatus(CareerApplicationStatus.REJECTED);
        application.setReviewedAt(LocalDateTime.now());
        application.setReviewedBy(reviewer);
        application.setAdminRemarks(request == null ? null : request.getRemarks());

        return convertToDTO(careerApplicationRepository.save(application));
    }

    private CareerApplication getPendingApplication(String id) {
        CareerApplication application = careerApplicationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Career application not found"));

        if (application.getStatus() != CareerApplicationStatus.PENDING) {
            throw new IllegalArgumentException("Career application has already been reviewed");
        }

        return application;
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getName())) {
            throw new IllegalArgumentException("User is not authenticated");
        }

        return userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    private void validateSubmission(CreateCareerApplicationDTO request) {
        if (request.getPhoneNumber() == null || request.getPhoneNumber().trim().isEmpty()) {
            throw new IllegalArgumentException("Phone number is required");
        }
        if (request.getCoverLetter() == null || request.getCoverLetter().trim().isEmpty()) {
            throw new IllegalArgumentException("Cover letter is required");
        }
        if (request.getCoverLetter().length() > 5000) {
            throw new IllegalArgumentException("Cover letter must not exceed 5000 characters");
        }
        validateResume(request.getResume());
    }

    private void validateResume(MultipartFile resume) {
        if (resume == null || resume.isEmpty()) {
            throw new IllegalArgumentException("Resume file is required");
        }

        String originalFilename = StringUtils.cleanPath(resume.getOriginalFilename() == null
                ? ""
                : resume.getOriginalFilename());
        String extension = getFileExtension(originalFilename);
        String contentType = resume.getContentType();

        if (!ALLOWED_RESUME_EXTENSIONS.contains(extension)
                || contentType == null
                || !ALLOWED_RESUME_CONTENT_TYPES.contains(contentType)) {
            throw new IllegalArgumentException("Resume must be a PDF, DOC, or DOCX file");
        }
    }

    private String getFileExtension(String filename) {
        int extensionIndex = filename.lastIndexOf('.');
        if (extensionIndex < 0 || extensionIndex == filename.length() - 1) {
            return "";
        }
        return filename.substring(extensionIndex + 1).toLowerCase(Locale.ROOT);
    }

    private String normalizeRole(String role) {
        if (role == null) {
            return "";
        }
        if ("registered_user".equalsIgnoreCase(role) || "USER".equalsIgnoreCase(role)) {
            return REGISTERED_USER_ROLE;
        }
        return role.toUpperCase(Locale.ROOT);
    }

    private CareerApplicationResponseDTO convertToDTO(CareerApplication application) {
        CareerApplicationResponseDTO dto = new CareerApplicationResponseDTO();
        dto.setId(application.getId());
        dto.setUserId(application.getUser().getId());
        dto.setFirstname(application.getFirstname());
        dto.setLastname(application.getLastname());
        dto.setEmail(application.getEmail());
        dto.setPhoneNumber(application.getPhoneNumber());
        dto.setResumeUrl("/api/admin/career-applications/" + application.getId() + "/resume");
        dto.setResumeOriginalFilename(application.getResumeOriginalFilename());
        dto.setCoverLetter(application.getCoverLetter());
        dto.setStatus(application.getStatus());
        dto.setSubmittedAt(application.getSubmittedAt());
        dto.setReviewedAt(application.getReviewedAt());
        dto.setAdminRemarks(application.getAdminRemarks());

        if (application.getReviewedBy() != null) {
            dto.setReviewedBy(application.getReviewedBy().getEmail());
        }

        return dto;
    }
}
