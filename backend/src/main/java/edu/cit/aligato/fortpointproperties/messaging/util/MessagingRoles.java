package edu.cit.aligato.fortpointproperties.messaging.util;

public final class MessagingRoles {
    public static final String REGISTERED_USER = "REGISTERED_USER";
    public static final String AGENT = "AGENT";

    private MessagingRoles() {
    }

    public static String normalize(String role) {
        if (role == null) {
            return "";
        }

        String normalized = role.trim().toUpperCase().replace("-", "_").replace(" ", "_");
        // Keep the old misspelling as an alias so stale tokens or rows do not break messaging.
        if (REGISTERED_USER.equals(normalized) || "REGISTERED_USEER".equals(normalized) || "USER".equals(normalized)) {
            return REGISTERED_USER;
        }
        return normalized;
    }

    public static boolean isSupported(String role) {
        String normalized = normalize(role);
        return REGISTERED_USER.equals(normalized) || AGENT.equals(normalized);
    }
}
