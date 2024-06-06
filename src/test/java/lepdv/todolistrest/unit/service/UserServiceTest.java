package lepdv.todolistrest.unit.service;

import lepdv.todolistrest.dto.auth.CredentialsDto;
import lepdv.todolistrest.dto.user.EditPasswordDto;
import lepdv.todolistrest.dto.user.UserDto;
import lepdv.todolistrest.entity.User;
import lepdv.todolistrest.exception.NotFoundException;
import lepdv.todolistrest.exception.UnitedException;
import lepdv.todolistrest.mapper.Mapper;
import lepdv.todolistrest.repository.UserRepository;
import lepdv.todolistrest.service.UserService;
import lepdv.todolistrest.util.AuthUser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.Optional;

import static java.util.Collections.emptyList;
import static lepdv.todolistrest.Constants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private Mapper mapper;
    @Spy
    @InjectMocks
    private UserService userService;




    @Test
    void loadUserByUsername_shouldGetUserDetails_whenExist() {
        doReturn(Optional.of(USER)).when(userRepository).findByUsername(USER.getUsername());

        UserDetails actualResult = userService.loadUserByUsername(USER.getUsername());

        verify(userRepository).findByUsername(USER.getUsername());
        assertEquals(USER, actualResult);
    }

    @Test
    void loadUserByUsername_shouldThrowException_whenNotExist() {
        doReturn(Optional.empty()).when(userRepository).findByUsername("dummy");

        assertThrows(UsernameNotFoundException.class, () -> userService.loadUserByUsername("dummy"));
        verify(userRepository).findByUsername("dummy");
    }



    @Test
    void register_shouldRegisterNewUser() {
        final User user = User.builder()
                .username("Petr")
                .password("Petr")
                .fullName("Petrov Petr")
                .dateOfBirth(LocalDate.of(1980, Month.AUGUST, 15))
                .build();
        doReturn(user).when(mapper).mapToUser(REGISTER_DTO);
        doReturn(SAVED_USER.getPassword()).when(passwordEncoder).encode(user.getPassword());
        doReturn(SAVED_USER).when(userRepository).save(user);

        userService.register(REGISTER_DTO);

        verify(mapper).mapToUser(REGISTER_DTO);
        verify(passwordEncoder).encode("Petr");
        verify(userRepository).save(user);
    }



    @Test
    void getAllByPageable_shouldGetUserList_whenDefaultArguments() {
        final Pageable pageable = PageRequest.of(0, 20);
        doReturn(USER_LIST).when(userRepository).findAllByOrderByUsername(pageable);

        List<User> actualResult = userService.getAllByPageable(pageable);

        verify(userRepository).findAllByOrderByUsername(pageable);
        assertFalse(actualResult.isEmpty());
        assertThat(actualResult).hasSize(3);
    }

    @Test
    void getAllByPageable_shouldGetPaginatedUserList_whenCustomArguments() {
        final Pageable pageable = PageRequest.of(1, 2);
        doReturn(List.of(KATYA)).when(userRepository).findAllByOrderByUsername(pageable);

        List<User> actualResult = userService.getAllByPageable(pageable);

        verify(userRepository).findAllByOrderByUsername(pageable);
        assertFalse(actualResult.isEmpty());
        assertThat(actualResult).hasSize(1);
    }

    @Test
    void getAllByPageable_shouldGetEmptyUserList_whenNoUsersByCustomArguments() {
        final Pageable pageable = PageRequest.of(999, 999);
        doReturn(emptyList()).when(userRepository).findAllByOrderByUsername(pageable);

        List<User> actualResult = userService.getAllByPageable(pageable);

        verify(userRepository).findAllByOrderByUsername(pageable);
        assertTrue(actualResult.isEmpty());
    }



    @Test
    void getUserDto_shouldGetUserDto() {
        doReturn(USER).when(userService).getAuthUser();
        doReturn(USER_DTO).when(mapper).mapToUserDto(USER);

        UserDto actualResult = userService.getUserDto();

        verify(userService).getAuthUser();
        verify(mapper).mapToUserDto(USER);
        assertEquals(USER_DTO, actualResult);
    }



    @Test
    void getUser_shouldGetUser_whenExist() {
        doReturn(Optional.of(USER)).when(userRepository).findById(USER.getId());

        User actualResult = userService.getUser(USER.getId());

        verify(userRepository).findById(USER.getId());
        assertEquals(USER, actualResult);
    }

    @Test
    void getUser_shouldThrowException_whenNotExist() {
        doReturn(Optional.empty()).when(userRepository).findById(999L);

        assertThrows(NotFoundException.class, () -> userService.getUser(999L));
        verify(userRepository).findById(999L);
    }



    @Test
    void getByUsername_shouldGetOptionalOfUser_whenExist() {
        doReturn(Optional.of(USER)).when(userRepository).findByUsername(USER.getUsername());

        Optional<User> actualResult = userService.getByUsername(USER.getUsername());

        verify(userRepository).findByUsername(USER.getUsername());
        assertTrue(actualResult.isPresent());
        actualResult.ifPresent(actual -> assertEquals(USER, actual));
    }

    @Test
    void getByUsername_shouldGetEmptyOptional_whenNotExist() {
        doReturn(Optional.empty()).when(userRepository).findByUsername("dummy");

        Optional<User> actualResult = userService.getByUsername("dummy");

        verify(userRepository).findByUsername("dummy");
        assertTrue(actualResult.isEmpty());
    }



    @Test
    void update_shouldUpdateUser() {
        final User user = USER.clone();
        doReturn(user).when(userService).getAuthUser();
        doReturn(USER_DTO).when(mapper).mapToUserDto(user);

        UserDto actualResult = userService.update(USER_DTO);

        verify(userService).getAuthUser();
        verify(mapper).mapToUserDto(user);
        assertEquals(USER_DTO, actualResult);
    }



    @Test
    void editPassword_shouldEditPassword_whenDataIsValid() {
        final EditPasswordDto editPasswordDto = EditPasswordDto.builder()
                .username("Ivan")
                .oldPassword("Ivan")
                .newPassword("newIvan")
                .build();
        final User user = USER.clone();
        doReturn(user).when(userService).getAuthUser();
        doReturn(true).when(passwordEncoder).matches(editPasswordDto.getOldPassword(), user.getPassword());
        doReturn("encodedPassword").when(passwordEncoder).encode(editPasswordDto.getNewPassword());

        userService.editPassword(editPasswordDto);

        verify(userService).getAuthUser();
        verify(passwordEncoder).matches(editPasswordDto.getOldPassword(), "$2a$10$JfoL9fN.fl4DtP.mUQAF0..OzWxIE2ffAq7nWY4XtXKazpYCd5HSK");
        verify(passwordEncoder).encode(editPasswordDto.getNewPassword());
    }

    @Test
    void editPassword_shouldThrowException_whenDataIsInvalid() {
        final EditPasswordDto editPasswordDto = EditPasswordDto.builder()
                .username("Ivan")
                .oldPassword("dummy")
                .newPassword("newIvan")
                .build();
        doReturn(USER).when(userService).getAuthUser();
        doReturn(false).when(passwordEncoder).matches(editPasswordDto.getOldPassword(), USER.getPassword());

        assertThrows(UnitedException.class, () -> userService.editPassword(editPasswordDto));
        verify(userService).getAuthUser();
        verify(passwordEncoder).matches(editPasswordDto.getOldPassword(), USER.getPassword());
    }



    @Test
    void deleteUser_shouldDeleteUser_whenDataIsValid() {
        final CredentialsDto credentialsDto = CredentialsDto.builder()
                .username("Ivan")
                .password("Ivan")
                .build();
        doReturn(USER).when(userService).getAuthUser();
        doReturn(true).when(passwordEncoder).matches(credentialsDto.getPassword(), USER.getPassword());
        doNothing().when(userRepository).delete(USER);

        userService.deleteUser(credentialsDto);

        verify(userService).getAuthUser();
        verify(passwordEncoder).matches(credentialsDto.getPassword(), USER.getPassword());
        verify(userRepository).delete(USER);
    }

    @Test
    void deleteUser_shouldThrowException_whenDataIsInvalid() {
        final CredentialsDto credentialsDto = CredentialsDto.builder()
                .username("Ivan")
                .password("dummy")
                .build();
        doReturn(USER).when(userService).getAuthUser();
        doReturn(false).when(passwordEncoder).matches(credentialsDto.getPassword(), USER.getPassword());

        assertThrows(UnitedException.class, () -> userService.deleteUser(credentialsDto));
        verify(userService).getAuthUser();
        verify(passwordEncoder).matches(credentialsDto.getPassword(), USER.getPassword());
    }



    @Test
    void deleteUserById_shouldDeleteUser_whenExist() {
        doReturn(USER).when(userService).getUser(USER.getId());
        doNothing().when(userRepository).deleteById(USER.getId());

        userService.deleteUserById(USER.getId());

        verify(userService).getUser(USER.getId());
        verify(userRepository).deleteById(USER.getId());
    }

    @Test
    void deleteUserById_shouldThrowException_whenNotExist() {
        doThrow(NotFoundException.class).when(userService).getUser(999L);

        assertThrows(NotFoundException.class, () -> userService.deleteUserById(999L));
        verify(userService).getUser(999L);
    }



    @Test
    void getAuthUserId_shouldGetAuthUserId() {
        doReturn(Optional.of(USER)).when(userService).getByUsername(USER.getUsername());

        User actualResult;
        try (MockedStatic<AuthUser> authUserMock = mockStatic(AuthUser.class)) {
            authUserMock.when(AuthUser::getAuthUsername).thenReturn(USER.getUsername());
            actualResult = userService.getAuthUser();

            authUserMock.verify(AuthUser::getAuthUsername);
        }
        verify(userService).getByUsername(USER.getUsername());
        assertEquals(USER, actualResult);
    }


}


















