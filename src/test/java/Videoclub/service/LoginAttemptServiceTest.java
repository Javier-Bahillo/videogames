package Videoclub.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

// Sin @ExtendWith ni mocks: LoginAttemptService no tiene dependencias
class LoginAttemptServiceTest {

    private LoginAttemptService service;

    @BeforeEach
    void setUp() {
        service = new LoginAttemptService();
    }

    @Test
    @DisplayName("sin intentos → no bloqueado")
    void noAttempts_notBlocked() {
        assertThat(service.isBlocked("user1")).isFalse();
    }

    @Test
    @DisplayName("4 intentos fallidos → no bloqueado")
    void fourFailures_notBlocked() {
        for (int i = 0; i < 4; i++) service.failure("user1");

        assertThat(service.isBlocked("user1")).isFalse();
    }

    @Test
    @DisplayName("5 intentos fallidos → bloqueado")
    void fiveFailures_blocked() {
        for (int i = 0; i < 5; i++) service.failure("user1");

        assertThat(service.isBlocked("user1")).isTrue();
    }

    @Test
    @DisplayName("login exitoso resetea el contador")
    void successResetsCounter() {
        for (int i = 0; i < 4; i++) service.failure("user1");
        service.success("user1");
        service.failure("user1"); // 1 fallo tras reset

        assertThat(service.isBlocked("user1")).isFalse();
    }

    @Test
    @DisplayName("bloqueo es por usuario — otros usuarios no se ven afectados")
    void blockIsPerUser() {
        for (int i = 0; i < 5; i++) service.failure("user1");

        assertThat(service.isBlocked("user2")).isFalse();
    }
}
