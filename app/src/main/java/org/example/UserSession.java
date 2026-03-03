package org.example;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * TSM01-J: Do not let the this reference escape during object construction.
 * This class uses a private constructor and static factory to publish only
 * fully constructed instances.
 */
public final class UserSession {

    private final String sessionId;
    private final String username;
    private final LocalDateTime startedAt;

    private UserSession(String username) {
        this.sessionId = UUID.randomUUID().toString();
        this.username = username;
        this.startedAt = LocalDateTime.now();
    }

    public static UserSession newSession(String username) {
        return new UserSession(username);
    }

    public String getSessionId() {
        return sessionId;
    }

    public String getUsername() {
        return username;
    }

    public LocalDateTime getStartedAt() {
        return LocalDateTime.of(startedAt.toLocalDate(), startedAt.toLocalTime());
    }
}
