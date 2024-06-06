package lepdv.todolistrest.repository;

import lepdv.todolistrest.entity.Task;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.history.RevisionRepository;

import java.util.List;


public interface TaskRepository extends JpaRepository<Task, Long>,
                 RevisionRepository<Task, Long, Integer> {

    //    List<Task> findAllByOrderByUser(Pageable pageable);

    @Query(value = "select t " +
            "from Task t join User u on t.user.id = u.id " +
            "order by u.username, t.id")
    List<Task> findAllBy(Pageable pageable);


    List<Task> findAllByUserIdOrderById(Long id, Pageable pageable);


}
