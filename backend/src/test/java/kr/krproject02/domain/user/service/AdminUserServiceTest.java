package kr.krproject02.domain.user.service;

import kr.krproject02.domain.user.constants.UserRole;
import kr.krproject02.domain.user.dto.admin.AdminUserLoginRequest;
import kr.krproject02.domain.user.dto.admin.AdminUserResponse;
import kr.krproject02.domain.user.entity.UserAccount;
import kr.krproject02.domain.user.error.UserErrorCode;
import kr.krproject02.domain.user.error.UserException;
import kr.krproject02.domain.user.repository.UserAccountRepository;
import kr.krproject02.domain.user.validation.UserAccountValidator;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AdminUserServiceTest {

    private final UserAccountRepository userAccountRepository = mock(UserAccountRepository.class);
    private final UserPasswordService userPasswordService = new UserPasswordService();
    private final UserAccountValidator userAccountValidator = new UserAccountValidator(new UserBirthDateCalculator());
    private final AdminUserService service = new AdminUserService(
        userAccountRepository,
        userPasswordService,
        userAccountValidator
    );

    @Test
    void loginFindsOnlyAdminAccountAndRecordsLogin() {
        UserAccount adminAccount = UserAccount.Companion.create(
            "900101",
            userPasswordService.encode("4068"),
            UserRole.ADMIN
        );
        when(userAccountRepository.findFirstByBirthDateAndRoleAndActiveTrueAndDeletedFalse("900101", UserRole.ADMIN))
            .thenReturn(adminAccount);

        AdminUserResponse response = service.login(new AdminUserLoginRequest("900101", "4068"));

        assertThat(response.getBirthDate()).isEqualTo("900101");
        assertThat(response.getActive()).isTrue();
        assertThat(adminAccount.getLastLoginAt()).isNotNull();
        verify(userAccountRepository).findFirstByBirthDateAndRoleAndActiveTrueAndDeletedFalse("900101", UserRole.ADMIN);
    }

    @Test
    void loginRejectsNormalScopeAccount() {
        when(userAccountRepository.findFirstByBirthDateAndRoleAndActiveTrueAndDeletedFalse("900101", UserRole.ADMIN))
            .thenReturn(null);

        assertThatThrownBy(() -> service.login(new AdminUserLoginRequest("900101", "4068")))
            .isInstanceOf(UserException.class)
            .extracting("errorCode")
            .isEqualTo(UserErrorCode.INVALID_CREDENTIALS);
        verify(userAccountRepository).findFirstByBirthDateAndRoleAndActiveTrueAndDeletedFalse("900101", UserRole.ADMIN);
    }
}
