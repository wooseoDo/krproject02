package kr.krproject02.domain.user.service;

import kr.krproject02.domain.user.constants.UserRole;
import kr.krproject02.domain.user.dto.normal.NormalUserLoginRequest;
import kr.krproject02.domain.user.dto.normal.NormalUserRegisterRequest;
import kr.krproject02.domain.user.dto.normal.NormalUserResponse;
import kr.krproject02.domain.user.entity.UserAccount;
import kr.krproject02.domain.user.error.UserErrorCode;
import kr.krproject02.domain.user.error.UserException;
import kr.krproject02.domain.user.event.UserRegisteredEvent;
import kr.krproject02.domain.user.repository.UserAccountRepository;
import kr.krproject02.domain.user.validation.UserAccountValidator;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.context.ApplicationEventPublisher;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class NormalUserServiceTest {

    private final UserAccountRepository userAccountRepository = mock(UserAccountRepository.class);
    private final UserPasswordService userPasswordService = new UserPasswordService();
    private final UserAccountValidator userAccountValidator = new UserAccountValidator(new UserBirthDateCalculator());
    private final ApplicationEventPublisher eventPublisher = mock(ApplicationEventPublisher.class);
    private final NormalUserService service = new NormalUserService(
        userAccountRepository,
        userPasswordService,
        userAccountValidator,
        eventPublisher
    );

    @Test
    void registerCreatesNormalUserWithEncodedPasswordAndPublishesEvent() {
        when(userAccountRepository.save(any(UserAccount.class))).thenAnswer(invocation -> {
            UserAccount account = invocation.getArgument(0);
            account.setUserId(UUID.randomUUID());
            return account;
        });

        NormalUserResponse response = service.register(new NormalUserRegisterRequest("950312", "4068"));

        ArgumentCaptor<UserAccount> userCaptor = ArgumentCaptor.forClass(UserAccount.class);
        verify(userAccountRepository).save(userCaptor.capture());
        UserAccount savedUser = userCaptor.getValue();

        assertThat(response.getBirthDate()).isEqualTo("950312");
        assertThat(savedUser.getRole()).isEqualTo(UserRole.NORMAL);
        assertThat(savedUser.getPasswordHash()).isNotEqualTo("4068");
        assertThat(userPasswordService.matches("4068", savedUser.getPasswordHash())).isTrue();
        verify(eventPublisher).publishEvent(any(UserRegisteredEvent.class));
    }

    @Test
    void loginRecordsLoginWhenNormalCredentialsMatch() {
        UserAccount account = UserAccount.Companion.create(
            "950312",
            userPasswordService.encode("4068"),
            UserRole.NORMAL
        );
        when(userAccountRepository.findFirstByBirthDateAndRoleAndActiveTrueAndDeletedFalse("950312", UserRole.NORMAL))
            .thenReturn(account);

        NormalUserResponse response = service.login(new NormalUserLoginRequest("950312", "4068"));

        assertThat(response.getBirthDate()).isEqualTo("950312");
        assertThat(account.getLastLoginAt()).isNotNull();
        verify(userAccountRepository).findFirstByBirthDateAndRoleAndActiveTrueAndDeletedFalse("950312", UserRole.NORMAL);
    }

    @Test
    void loginRejectsWrongPassword() {
        UserAccount account = UserAccount.Companion.create(
            "950312",
            userPasswordService.encode("4068"),
            UserRole.NORMAL
        );
        when(userAccountRepository.findFirstByBirthDateAndRoleAndActiveTrueAndDeletedFalse("950312", UserRole.NORMAL))
            .thenReturn(account);

        assertThatThrownBy(() -> service.login(new NormalUserLoginRequest("950312", "wrong")))
            .isInstanceOf(UserException.class)
            .extracting("errorCode")
            .isEqualTo(UserErrorCode.INVALID_CREDENTIALS);
        verify(userAccountRepository).findFirstByBirthDateAndRoleAndActiveTrueAndDeletedFalse("950312", UserRole.NORMAL);
        verify(eventPublisher, never()).publishEvent(any());
    }
}
