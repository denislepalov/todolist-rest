package lepdv.todolistrest.mapper;

import lepdv.todolistrest.dto.admin.UserForAdminDto;
import lepdv.todolistrest.dto.auth.RegisterDto;
import lepdv.todolistrest.dto.task.CreateTaskDto;
import lepdv.todolistrest.dto.task.ResponseTaskDto;
import lepdv.todolistrest.dto.user.UserDto;
import lepdv.todolistrest.entity.Task;
import lepdv.todolistrest.entity.User;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class Mapper {

    private final ModelMapper modelMapper;




    public User mapToUser(RegisterDto registerDto) {
        return modelMapper.map(registerDto, User.class);
    }

    public UserDto mapToUserDto(User user) {
        return modelMapper.map(user, UserDto.class);
    }

    public UserForAdminDto mapToUserForAdminDto(User user) {
        return modelMapper.map(user, UserForAdminDto.class);
    }

    public Task mapToTask(CreateTaskDto createTaskDto) { return modelMapper.map(createTaskDto, Task.class); }

    public ResponseTaskDto mapToResponseTaskDto(Task task) {
        return modelMapper.map(task, ResponseTaskDto.class);
    }


}
