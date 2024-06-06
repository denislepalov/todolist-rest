package lepdv.todolistrest;


import lepdv.todolistrest.dto.admin.UserForAdminDto;
import lepdv.todolistrest.dto.auth.RegisterDto;
import lepdv.todolistrest.dto.task.CreateTaskDto;
import lepdv.todolistrest.dto.task.ResponseTaskDto;
import lepdv.todolistrest.dto.task.UpdateTaskDto;
import lepdv.todolistrest.dto.user.UserDto;
import lepdv.todolistrest.entity.Role;
import lepdv.todolistrest.entity.Task;
import lepdv.todolistrest.entity.User;
import lombok.experimental.UtilityClass;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;


@UtilityClass
public class Constants {

    public static final User USER = User.builder()
            .id(2L)
            .username("Ivan")
            .password("$2a$10$JfoL9fN.fl4DtP.mUQAF0..OzWxIE2ffAq7nWY4XtXKazpYCd5HSK")
            .fullName("Ivanov Ivan")
            .dateOfBirth(LocalDate.of(2000, Month.JANUARY, 1))
            .role(Role.USER)
            .isNonLocked(true)
            .build();
    public static final User ADMIN = User.builder()
            .id(1L)
            .username("Admin")
            .password("$2a$10$QqbD8Up32CATm2DSVVjIDea08KuxC/RL9.9SFVcMP6FW5nHGl5PIG")
            .fullName("Admin")
            .dateOfBirth(LocalDate.of(1990, Month.JANUARY, 1))
            .role(Role.ADMIN)
            .isNonLocked(true)
            .build();
    public static final User KATYA = User.builder()
            .id(3L)
            .username("Katya")
            .password("$2a$10$f0A/1pjXviu82xuuG5AKreDlb0tiAoWzBMnbphJz1oPNkzaZ2omRe")
            .fullName("Petrova Katya")
            .dateOfBirth(LocalDate.of(2010, Month.JANUARY, 1))
            .role(Role.USER)
            .isNonLocked(true)
            .build();

    public static final List<User> USER_LIST = List.of(ADMIN, USER, KATYA);


    public static final Task TASK = Task.builder()
            .id(1L)
            .description("Ivan task1")
            .dateOfCreation(LocalDate.of(2023, Month.MAY, 1))
            .dueDate(LocalDate.of(2025, Month.MAY, 11))
            .isCompleted("Not completed")
            .user(USER)
            .build();
    public static final Task TASK_2 = Task.builder()
            .id(2L)
            .description("Ivan task2")
            .dateOfCreation(LocalDate.of(2023, Month.MAY, 10))
            .dueDate(LocalDate.of(2025, Month.MAY, 20))
            .isCompleted("Not completed")
            .user(USER)
            .build();
    public static final Task TASK_3 = Task.builder()
            .id(3L)
            .description("Ivan task3")
            .dateOfCreation(LocalDate.of(2023, Month.MAY, 20))
            .dueDate(LocalDate.of(2025, Month.MAY, 30))
            .isCompleted("Not completed")
            .user(USER)
            .build();
    public static final Task TASK_4 = Task.builder()
            .id(4L)
            .description("Katya task1")
            .dateOfCreation(LocalDate.of(2023, Month.JULY, 1))
            .dueDate(LocalDate.of(2025, Month.MAY, 11))
            .isCompleted("Not completed")
            .user(KATYA)
            .build();
    public static final Task TASK_5 = Task.builder()
            .id(5L)
            .description("Katya task2")
            .dateOfCreation(LocalDate.of(2023, Month.JULY, 10))
            .dueDate(LocalDate.of(2025, Month.MAY, 20))
            .isCompleted("Not completed")
            .user(KATYA)
            .build();
    public static final Task TASK_6 = Task.builder()
            .id(6L)
            .description("Katya task3")
            .dateOfCreation(LocalDate.of(2023, Month.JULY, 20))
            .dueDate(LocalDate.of(2025, Month.MAY, 30))
            .isCompleted("Not completed")
            .user(KATYA)
            .build();

    public static final List<Task> TASK_LIST = List.of(TASK, TASK_2, TASK_3, TASK_4, TASK_5, TASK_6);



    public static final RegisterDto REGISTER_DTO = RegisterDto.builder()
            .username("Petr")
            .password("Petr")
            .fullName("Petrov Petr")
            .dateOfBirth(LocalDate.of(1980, Month.AUGUST, 15))
            .build();

    public static final User SAVED_USER = User.builder()
            .id(4L)
            .username("Petr")
            .password("$2a$12$YBkXlt63NPpd6pr0KBQjI.KaSbMkBb.QB2VfBUE58vHmPlb2uXtSS")
            .fullName("Petrov Petr")
            .dateOfBirth(LocalDate.of(1980, Month.AUGUST, 15))
            .role(Role.USER)
            .isNonLocked(true)
            .build();

    public static final UserDto USER_DTO = UserDto.builder()
            .username("Ivan")
            .fullName("Ivanov Ivan")
            .dateOfBirth(LocalDate.of(2000, Month.JANUARY, 1))
            .build();

    public static final UserForAdminDto USER_FOR_ADMIN_DTO = UserForAdminDto.builder()
            .id(2L)
            .username("Ivan")
            .fullName("Ivanov Ivan")
            .dateOfBirth(LocalDate.of(2000, Month.JANUARY, 1))
            .role(Role.USER.name())
            .isNonLocked(true)
            .build();



    public static final CreateTaskDto CREATE_TASK_DTO = CreateTaskDto.builder()
            .description("Ivan task4")
            .dueDate(LocalDate.of(2026, Month.MAY, 11))
            .build();

    public static final UpdateTaskDto UPDATE_TASK_DTO = UpdateTaskDto.builder()
            .description("Updated Ivan task1")
            .dueDate(LocalDate.of(2026, Month.MAY, 31))
            .build();

    public static final ResponseTaskDto RESPONSE_TASK_DTO = ResponseTaskDto.builder()
            .id(1L)
            .description("Ivan task1")
            .dateOfCreation(LocalDate.of(2023, Month.MAY, 1))
            .dueDate(LocalDate.of(2025, Month.MAY, 11))
            .isCompleted("Not completed")
            .user(USER.getUsername())
            .build();



}
