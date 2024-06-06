package lepdv.todolistrest.integration.service;

import lepdv.todolistrest.integration.IT;
import lepdv.todolistrest.dto.auth.CredentialsDto;
import lepdv.todolistrest.dto.user.EditPasswordDto;
import lepdv.todolistrest.dto.user.UserDto;
import lepdv.todolistrest.entity.User;
import lepdv.todolistrest.exception.NotFoundException;
import lepdv.todolistrest.exception.UnitedException;
import lepdv.todolistrest.service.UserService;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.Optional;

import static lepdv.todolistrest.Constants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;


@IT
@RequiredArgsConstructor
class UserServiceIT /*extends IntegrationTestBase*/ {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;




    @Test
    void loadUserByUsername_shouldGetUserDetails_whenExist() {
        UserDetails actualResult = userService.loadUserByUsername(USER.getUsername());

        assertEquals(USER, actualResult);
    }

    @Test
    void loadUserByUsername_shouldThrowException_whenNotExist() {
        assertThrows(UsernameNotFoundException.class, () -> userService.loadUserByUsername("dummy"));
    }



    @Test
    void register_shouldRegisterNewUser() {
        userService.register(REGISTER_DTO);

        final Optional<User> optionalUser = userService.getByUsername(REGISTER_DTO.getUsername());
        optionalUser.ifPresent(actual -> assertAll(
                () -> assertEquals(SAVED_USER.getId(), actual.getId()),
                () -> assertEquals(SAVED_USER.getUsername(), actual.getUsername()),
                () -> assertEquals(SAVED_USER.getFullName(), actual.getFullName()),
                () -> assertEquals(SAVED_USER.getDateOfBirth(), actual.getDateOfBirth())
        ));
    }



    @Test
    void getAllByPageable_shouldGetUserList_whenDefaultArguments() {
        final Pageable pageable = PageRequest.of(0, 20);

        List<User> actualResult = userService.getAllByPageable(pageable);

        assertFalse(actualResult.isEmpty());
        assertThat(actualResult).hasSize(3);
        assertEquals(USER_LIST, actualResult);
    }

    @Test
    void getAllByPageable_shouldGetPaginatedUserList_whenCustomArguments() {
        final Pageable pageable = PageRequest.of(1, 2);

        List<User> actualResult = userService.getAllByPageable(pageable);

        assertFalse(actualResult.isEmpty());
        assertThat(actualResult).hasSize(1);
        assertEquals(List.of(KATYA), actualResult);
    }

    @Test
    void getAllByPageable_shouldGetEmptyUserList_whenNoUsersByCustomArguments() {
        final Pageable pageable = PageRequest.of(999, 999);

        List<User> actualResult = userService.getAllByPageable(pageable);

        assertTrue(actualResult.isEmpty());
    }



    @Test
    @WithMockUser(username = "Ivan", authorities = "USER")
    void getUserDto_shouldGetUserDto() {
        UserDto actualResult = userService.getUserDto();

        assertEquals(USER_DTO, actualResult);
    }



    @Test
    void getUser_shouldGetUser_whenExist() {
        User actualResult = userService.getUser(USER.getId());

        assertEquals(USER, actualResult);
    }

    @Test
    void getUser_shouldThrowException_whenNotExist() {
        assertThrows(NotFoundException.class, () -> userService.getUser(999L));
    }



    @Test
    void getByUsername_shouldGetOptionalOfUser_whenExist() {
        Optional<User> actualResult = userService.getByUsername(USER.getUsername());

        actualResult.ifPresent(actual -> assertEquals(USER, actual));
    }

    @Test
    void getByUsername_shouldGetEmptyOptional_whenNotExist() {
        Optional<User> actualResult = userService.getByUsername("dummy");

        assertTrue(actualResult.isEmpty());
    }



    @Test
    @WithMockUser(username = "Ivan", authorities = "USER")
    void update_shouldUpdateUser() {
        final UserDto userDto = UserDto.builder()
                .username("Ivan")
                .fullName("Updated full name")
                .dateOfBirth(LocalDate.of(2000, Month.JANUARY, 1))
                .build();

        UserDto actualResult = userService.update(userDto);

        assertEquals(userDto, actualResult);
    }



    @Test
    @WithMockUser(username = "Ivan", authorities = "USER")
    void editPassword_shouldEditPassword_whenDataIsValid() {
        final EditPasswordDto editPasswordDto = EditPasswordDto.builder()
                .username("Ivan")
                .oldPassword("Ivan")
                .newPassword("newIvan")
                .build();

        userService.editPassword(editPasswordDto);

        User editedUser = userService.getUser(USER.getId());
        assertTrue(passwordEncoder.matches(editPasswordDto.getNewPassword(), editedUser.getPassword()));
    }

    @Test
    @WithMockUser(username = "Ivan", authorities = "USER")
    void editPassword_shouldThrowException_whenDataIsInvalid() {
        final EditPasswordDto editPasswordDto = EditPasswordDto.builder()
                .username("Ivan")
                .oldPassword("dummy")
                .newPassword("newIvan")
                .build();

        assertThrows(UnitedException.class, () -> userService.editPassword(editPasswordDto));
    }



    @Test
    @WithMockUser(username = "Ivan", authorities = "USER")
    void deleteUser_shouldDeleteUser_whenDataIsValid() {
        final CredentialsDto credentialsDto = CredentialsDto.builder()
                .username("Ivan")
                .password("Ivan")
                .build();

        userService.deleteUser(credentialsDto);

        assertThrows(NotFoundException.class, () -> userService.getUser(USER.getId()));
    }

    @Test
    @WithMockUser(username = "Ivan", authorities = "USER")
    void deleteUser_shouldThrowException_whenDataIsInvalid() {
        final CredentialsDto credentialsDto = CredentialsDto.builder()
                .username("Ivan")
                .password("dummy")
                .build();

        assertThrows(UnitedException.class, () -> userService.deleteUser(credentialsDto));
    }



    @Test
    void deleteUserById_shouldDeleteUser_whenExist() {
        userService.deleteUserById(USER.getId());

        assertThrows(NotFoundException.class, () -> userService.getUser(USER.getId()));
    }

    @Test
    void deleteUserById_shouldThrowException_whenNotExist() {
        assertThrows(NotFoundException.class, () -> userService.deleteUserById(999L));
    }



    @Test
    @WithMockUser(username = "Ivan", authorities = "USER")
    void getAuthUser_shouldGetAuthUser() {
        User actualResult = userService.getAuthUser();

        assertEquals(USER, actualResult);
    }



}















