package com.example.fortpointproperties.shared.auth

enum class SessionAccessLevel {
    GUEST,
    REGISTERED_USER
}

object SessionManager {

    fun normalizeRole(role: String?): String {
        // Backend/web role values have appeared in a few formats; normalize once before access checks.
        val normalized = role
            ?.trim()
            ?.uppercase()
            ?.replace("-", "_")
            ?.replace(" ", "_")
            .orEmpty()

        return when (normalized) {
            "USER", "REGISTERED_USER", "REGISTERED_USEER" -> "REGISTERED_USER"
            else -> normalized
        }
    }

    fun currentAccessLevel(): SessionAccessLevel {
        return if (isRegisteredUser()) {
            SessionAccessLevel.REGISTERED_USER
        } else {
            SessionAccessLevel.GUEST
        }
    }

    fun isGuest(): Boolean {
        return currentAccessLevel() == SessionAccessLevel.GUEST
    }

    fun isRegisteredUser(): Boolean {
        return normalizeRole(TokenManager.getUserRole()) == "REGISTERED_USER"
    }

    fun canAccessRestrictedFeatures(): Boolean {
        return isRegisteredUser()
    }

    fun hasStoredRole(): Boolean {
        return !TokenManager.getUserRole().isNullOrBlank()
    }

    fun getStoredRole(): String? {
        return normalizeRole(TokenManager.getUserRole()).takeIf { it.isNotBlank() }
    }

    fun isRegisteredUserRole(role: String?): Boolean {
        return normalizeRole(role) == "REGISTERED_USER"
    }

    fun isPrivilegedRole(role: String?): Boolean {
        val normalized = normalizeRole(role)
        return normalized == "ADMIN" || normalized == "AGENT"
    }
}
