package lepdv.todolistrest.service;

import lepdv.todolistrest.dto.admin.UserForAdminDto;
import lepdv.todolistrest.dto.admin.UserListDto;
import lepdv.todolistrest.dto.auth.CredentialsDto;
import lepdv.todolistrest.dto.task.ResponseTaskDto;
import lepdv.todolistrest.dto.task.TaskListDto;
import lepdv.todolistrest.entity.Role;
import lepdv.todolistrest.entity.User;
import lepdv.todolistrest.exception.UnitedException;
import lepdv.todolistrest.mapper.Mapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional(readOnly = true)
@PreAuthorize("hasAuthority('ADMIN')")
@Slf4j
@RequiredArgsConstructor
public class AdminService {

    private final UserService userService;
    private final TaskService taskService;
    private final Mapper mapper;
    private final PasswordEncoder passwordEncoder;




    public UserListDto getAllUsers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        UserListDto userListDto = new UserListDto();
        userListDto.setUserList(userService.getAllByPageable(pageable)
                .stream()
                .map(mapper::mapToUserForAdminDto)
                .toList());
        return userListDto;
    }



    public UserForAdminDto getUserForAdminDto(Long id) {
        return mapper.mapToUserForAdminDto(userService.getUser(id));
    }



    @Transactional
    public void lockUser(Long id) {
        User user = userService.getUser(id);
        if (user.getRole().equals(Role.ADMIN)) {
            throw new UnitedException("You can't lock Administrator");
        }
        user.setIsNonLocked(false);
        log.info("User id={}, username={} was locked", id, user.getUsername());
    }



    @Transactional
    public void unlockUser(Long id) {
        User user = userService.getUser(id);
        user.setIsNonLocked(true);
        log.info("User id={}, username={} was unlocked", id, user.getUsername());
    }



    @Transactional
    public void deleteUser(CredentialsDto credentialsDto, Long deletableUserId) {
        User admin = userService.getAuthUser();
        User deletableUser = userService.getUser(deletableUserId);

        if (credentialsDto.getUsername().equals(admin.getUsername()) &&
                passwordEncoder.matches(credentialsDto.getPassword(), admin.getPassword())) {

            if (deletableUser.getRole().equals(Role.ADMIN)) {
                throw new UnitedException("You can't delete Administrator");
            }
            userService.deleteUserById(deletableUserId);
        } else {
            throw new UnitedException("Incorrect credentials");
        }
    }


    public TaskListDto getAllTasks(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        TaskListDto taskListDto = new TaskListDto();

        taskListDto.setTaskList(taskService.getAllByPageable(pageable)
                .stream()
                .map(mapper::mapToResponseTaskDto)
                .toList());
        return taskListDto;
    }


    public ResponseTaskDto getTaskById(Long id) {
        return taskService.getTaskDtoById(id);
    }


}
