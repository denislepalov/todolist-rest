package lepdv.todolistrest.integration.mapper;

import lepdv.todolistrest.integration.IT;
import lepdv.todolistrest.dto.admin.UserForAdminDto;
import lepdv.todolistrest.dto.task.ResponseTaskDto;
import lepdv.todolistrest.dto.user.UserDto;
import lepdv.todolistrest.entity.Task;
import lepdv.todolistrest.entity.User;
import lepdv.todolistrest.mapper.Mapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.Month;

import static lepdv.todolistrest.Constants.*;
import static org.junit.jupiter.api.Assertions.assertEquals;


@IT
@RequiredArgsConstructor
class MapperIT /*extends IntegrationTestBase*/ {

    private final Mapper mapper;




    @Test
    void mapToUser_shouldMapRegisterDtoToUser() {
        final User expectedResult = User.builder()
                .username("Petr")
                .password("Petr")
                .fullName("Petrov Petr")
                .dateOfBirth(LocalDate.of(1980, Month.AUGUST, 15))
                .build();

        User actualResult = mapper.mapToUser(REGISTER_DTO);

        assertEquals(expectedResult, actualResult);
    }



    @Test
    void mapToUserDto_shouldMapUserToUserDto() {
        UserDto actualResult = mapper.mapToUserDto(USER);

        assertEquals(USER_DTO, actualResult);
    }



    @Test
    void mapToUserForAdminDto_shouldMapUserToUserForAdminDto() {
        UserForAdminDto actualResult = mapper.mapToUserForAdminDto(USER);

        assertEquals(USER_FOR_ADMIN_DTO, actualResult);
    }



    @Test
    void mapToTask_shouldMapCreateTaskDtoToTask() {
        final Task expectedResult = Task.builder()
                .description("Ivan task4")
                .dueDate(LocalDate.of(2026, Month.MAY, 11))
                .build();

        Task actualResult = mapper.mapToTask(CREATE_TASK_DTO);

        assertEquals(expectedResult, actualResult);
    }



    @Test
    void mapToResponseTaskDto_shouldMapTaskToResponseTaskDto() {
        ResponseTaskDto actualResult = mapper.mapToResponseTaskDto(TASK);

        assertEquals(RESPONSE_TASK_DTO, actualResult);
    }


}


