package edu.eci.patricia.DOSW_patricia.domain.valueobjects;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Controls who can see the user's profile. " +
        "PUBLIC: visible to all users in search and feed. " +
        "PRIVATE: only the user themselves can see the profile. " +
        "MATCH_ONLY: visible only to affinity matches — hidden from public search and feed.")
public enum ProfileVisibility {
    PUBLIC,
    PRIVATE,
    MATCH_ONLY
}
