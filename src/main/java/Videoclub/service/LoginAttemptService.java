package Videoclub.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class LoginAttemptService {

    private static final int MAX_ATTEMPTS = 5;
    private static final int BLOCK_MINUTES = 15;

    private record Attempt(int count, LocalDateTime lastAt) {}

    private final Map<String, Attempt> tracker = new ConcurrentHashMap<>();

    public void success(String username) {
        tracker.remove(username);
    }

    public void failure(String username) {
        tracker.merge(username,
                new Attempt(1, LocalDateTime.now()),
                (existing, ignored) -> new Attempt(existing.count() + 1, LocalDateTime.now()));
        int count = tracker.get(username).count();
        log.warn("Failed login attempt {}/{} for user '{}'", count, MAX_ATTEMPTS, username);
    }

    public boolean isBlocked(String username) {
        Attempt a = tracker.get(username);
        if (a == null || a.count() < MAX_ATTEMPTS) return false;
        if (a.lastAt().plusMinutes(BLOCK_MINUTES).isAfter(LocalDateTime.now())) return true;
        tracker.remove(username);
        return false;
    }
}
