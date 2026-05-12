package edu.eci.patricia.DOSW_patricia.application.usecase;

import edu.eci.patricia.DOSW_patricia.application.dto.external.UserDto;
import edu.eci.patricia.DOSW_patricia.application.dto.request.ChangePasswordRequestDto;
import edu.eci.patricia.DOSW_patricia.domain.exceptions.InvalidCredentialsException;
import edu.eci.patricia.DOSW_patricia.domain.exceptions.TokenInvalidException;
import edu.eci.patricia.DOSW_patricia.domain.ports.out.UserServicePort;
import edu.eci.patricia.DOSW_patricia.domain.valueobjects.RolEnum;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChangePasswordUseCaseTest {

    @Mock private UserServicePort userServicePort;
    @Mock private PasswordEncoder passwordEncoder;
    @InjectMocks private ChangePasswordUseCase useCase;

    private static final UUID USER_UUID = UUID.fromString("00000000-0000-0000-0000-000000000001");
    private static final String USER_ID = USER_UUID.toString();
    private static final String EMAIL = "user@mail.escuelaing.edu.co";
    private static final String CURRENT = "OldPass!";
    private static final String HASHED = "$2a$10$hash";

    @Test
    void shouldChangePasswordSuccessfully() {
        UserDto user = new UserDto(USER_UUID, EMAIL, HASHED, true, RolEnum.STUDENT);
        when(userServicePort.findById(USER_ID)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(CURRENT, HASHED)).thenReturn(true);
        when(passwordEncoder.encode("NewPass!")).thenReturn("new-hashed");

        useCase.changePassword(new ChangePasswordRequestDto(USER_ID, CURRENT, "NewPass!"));

        verify(userServicePort).updatePassword(USER_ID, "new-hashed");
    }

    @Test
    void shouldThrowWhenUserNotFound() {
        when(userServicePort.findById(USER_ID)).thenReturn(Optional.empty());

        assertThrows(TokenInvalidException.class, () ->
                useCase.changePassword(new ChangePasswordRequestDto(USER_ID, CURRENT, "NewPass!")));
    }

    @Test
    void shouldThrowWhenCurrentPasswordIsWrong() {
        UserDto user = new UserDto(USER_UUID, EMAIL, HASHED, true, RolEnum.STUDENT);
        when(userServicePort.findById(USER_ID)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(CURRENT, HASHED)).thenReturn(false);

        assertThrows(InvalidCredentialsException.class, () ->
                useCase.changePassword(new ChangePasswordRequestDto(USER_ID, CURRENT, "NewPass!")));
        verify(userServicePort, never()).updatePassword(anyString(), anyString());
    }
}
