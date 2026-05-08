package edu.eci.patricia.DOSW_patricia.application.usecase;

import edu.eci.patricia.DOSW_patricia.application.dto.external.UserDto;
import edu.eci.patricia.DOSW_patricia.application.dto.request.ChangePasswordRequestDto;
import edu.eci.patricia.DOSW_patricia.domain.exceptions.InvalidCredentialsException;
import edu.eci.patricia.DOSW_patricia.domain.exceptions.TokenInvalidException;
import edu.eci.patricia.DOSW_patricia.domain.ports.out.UserServicePort;
import edu.eci.patricia.DOSW_patricia.domain.valueobjects.RolEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChangePasswordUseCaseTest {

    @Mock private UserServicePort userServicePort;
    @Mock private PasswordEncoder passwordEncoder;

    @InjectMocks private ChangePasswordUseCase changePasswordUseCase;

    private static final String USER_ID = "user-uuid-001";
    private static final String EMAIL = "user@mail.escuelaing.edu.co";
    private static final String HASHED = "$2a$10$currentHash";

    private ChangePasswordRequestDto dto;
    private UserDto usuario;

    @BeforeEach
    void setUp() {
        dto = new ChangePasswordRequestDto(USER_ID, "ActualPass123!", "NuevaPass456!");
        usuario = new UserDto(USER_ID, EMAIL, HASHED, true, RolEnum.STUDENT);
    }

    @Test
    void changePassword_contrasenaActualCorrecta_actualizaContrasena() {
        when(userServicePort.findById(USER_ID)).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("ActualPass123!", HASHED)).thenReturn(true);
        when(passwordEncoder.encode("NuevaPass456!")).thenReturn("$2a$10$newHash");

        changePasswordUseCase.changePassword(dto);

        verify(userServicePort).updatePassword(eq(EMAIL), eq("$2a$10$newHash"));
    }

    @Test
    void changePassword_usuarioNoExiste_lanzaTokenInvalid() {
        when(userServicePort.findById(USER_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> changePasswordUseCase.changePassword(dto))
                .isInstanceOf(TokenInvalidException.class);
    }

    @Test
    void changePassword_contrasenaActualIncorrecta_lanzaInvalidCredentials() {
        when(userServicePort.findById(USER_ID)).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("ActualPass123!", HASHED)).thenReturn(false);

        assertThatThrownBy(() -> changePasswordUseCase.changePassword(dto))
                .isInstanceOf(InvalidCredentialsException.class);
    }
}
