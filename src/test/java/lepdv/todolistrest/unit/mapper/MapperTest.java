package lepdv.todolistrest.unit.mapper;

import lepdv.todolistrest.dto.admin.UserForAdminDto;
import lepdv.todolistrest.dto.task.ResponseTaskDto;
import lepdv.todolistrest.dto.user.UserDto;
import lepdv.todolistrest.entity.Task;
import lepdv.todolistrest.entity.User;
import lepdv.todolistrest.mapper.Mapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.time.LocalDate;
import java.time.Month;

import static lepdv.todolistrest.Constants.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;


@ExtendWith(MockitoExtension.class)
class MapperTest {

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private Mapper mapper;



    @Test
    void mapToUser_shouldMapRegisterDtoToUser() {
        final User user = User.builder()
                .username("Petr")
                .password("Petr")
                .fullName("Petrov Petr")
                .dateOfBirth(LocalDate.of(1980, Month.AUGUST, 15))
                .build();
        doReturn(user).when(modelMapper).map(REGISTER_DTO, User.class);

        User actualResult = mapper.mapToUser(REGISTER_DTO);

        verify(modelMapper).map(REGISTER_DTO, User.class);
        assertEquals(user, actualResult);
    }



    @Test
    void mapToUserDto_shouldMapUserToUserDto() {
        doReturn(USER_DTO).when(modelMapper).map(USER, UserDto.class);

        UserDto actualResult = mapper.mapToUserDto(USER);

        verify(modelMapper).map(USER, UserDto.class);
        assertEquals(USER_DTO, actualResult);
    }



    @Test
    void mapToUserForAdminDto_shouldMapUserToUserForAdminDto() {
        doReturn(USER_FOR_ADMIN_DTO).when(modelMapper).map(USER, UserForAdminDto.class);

        UserForAdminDto actualResult = mapper.mapToUserForAdminDto(USER);

        verify(modelMapper).map(USER, UserForAdminDto.class);
        assertEquals(USER_FOR_ADMIN_DTO, actualResult);
    }



    @Test
    void mapToTask_shouldMapCreateTaskDtoToTask() {
        final Task task = Task.builder()
                .description("Ivan task4")
                .dueDate(LocalDate.of(2026, Month.MAY, 11))
                .build();
        doReturn(task).when(modelMapper).map(CREATE_TASK_DTO, Task.class);

        Task actualResult = mapper.mapToTask(CREATE_TASK_DTO);

        verify(modelMapper).map(CREATE_TASK_DTO, Task.class);
        assertEquals(task, actualResult);
    }



    @Test
    void mapToResponseTaskDto_shouldMapTaskToResponseTaskDto() {
        doReturn(RESPONSE_TASK_DTO).when(modelMapper).map(TASK, ResponseTaskDto.class);

        ResponseTaskDto actualResult = mapper.mapToResponseTaskDto(TASK);

        verify(modelMapper).map(TASK, ResponseTaskDto.class);
        assertEquals(RESPONSE_TASK_DTO, actualResult);
    }


}



















