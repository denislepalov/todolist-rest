package lepdv.todolistrest.service;

import lepdv.todolistrest.dto.task.*;
import lepdv.todolistrest.entity.Role;
import lepdv.todolistrest.entity.Task;
import lepdv.todolistrest.entity.User;
import lepdv.todolistrest.exception.NotFoundException;
import lepdv.todolistrest.exception.UnitedException;
import lepdv.todolistrest.mapper.Mapper;
import lepdv.todolistrest.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static lepdv.todolistrest.util.AuthUser.getAuthUsername;


@Service
@Transactional(readOnly = true)
@Slf4j
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserService userService;
    private final Mapper mapper;




    @Transactional
    public ResponseTaskDto create(CreateTaskDto createTaskDto) {

        User authUser = userService.getAuthUser();
        Task task = mapper.mapToTask(createTaskDto);
        task.setDateOfCreation(LocalDate.now());
        task.setIsCompleted("Not completed");
        task.setUser(authUser);

        Task savedTask = taskRepository.save(task);
        log.info("New task id={} was created", savedTask.getId());
        return mapper.mapToResponseTaskDto(savedTask);
    }



    public List<Task> getAllByPageable(Pageable pageable) {
        return taskRepository.findAllBy(pageable);
    }



    public TaskListDto getAllByAuthUser(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        User authUser = userService.getAuthUser();
        List<Task> taskPage = taskRepository.findAllByUserIdOrderById(authUser.getId(), pageable);

        TaskListDto todoList = new TaskListDto();
        todoList.setTaskList(taskPage.stream()
                .map(mapper::mapToResponseTaskDto)
                .toList());
        return todoList;

    }



    public ResponseTaskDto getTaskDtoById(Long id) {
        Task taskFromDB = getTask(id);
        User authUser = userService.getAuthUser();
        if (!authUser.getRole().equals(Role.ADMIN) && !taskFromDB.getUser().getUsername().equals(authUser.getUsername())) {
            throw new UnitedException("Task with id=" + id + " belongs to another user");
        }
        return mapper.mapToResponseTaskDto(taskFromDB);
    }



    @Transactional
    public ResponseTaskDto update(Long id, UpdateTaskDto updateTaskDto) {
        Task taskFromDB = getTask(id);
        if (!taskFromDB.getUser().getUsername().equals(getAuthUsername())) {
            throw new UnitedException("Task with id=" + id + " belongs to another user");
        }
        Optional.ofNullable(updateTaskDto.getDescription()).ifPresent(taskFromDB::setDescription);
        Optional.ofNullable(updateTaskDto.getDueDate()).ifPresent(taskFromDB::setDueDate);
        log.info("Task id={} was updated", id);
        return mapper.mapToResponseTaskDto(taskFromDB);
    }



    @Transactional
    public void markAsCompleted(Long id) {
        Task task = getTask(id);
        if (!task.getUser().getUsername().equals(getAuthUsername())) {
            throw new UnitedException("Task with id=" + id + " belongs to another user");
        }
        task.setIsCompleted("Completed");
        log.info("Task id={} was marked as completed", id);
    }



    @Transactional
    public void delete(Long id) {
        Task task = getTask(id);
        if (!task.getUser().getUsername().equals(getAuthUsername())) {
            throw new UnitedException("Task with id=" + id + " belongs to another user");
        }
        taskRepository.deleteById(id);
        log.info("Task id={} was deleted", id);
    }



    public Task getTask(Long id) {
        return taskRepository.findById(id).
                orElseThrow(() -> new NotFoundException("There is no task with id=" + id + " in database"));
    }



}
