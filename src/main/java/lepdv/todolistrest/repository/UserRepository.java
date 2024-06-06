package lepdv.todolistrest.repository;

import lepdv.todolistrest.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.history.RevisionRepository;

import java.util.List;
import java.util.Optional;



public interface UserRepository extends JpaRepository<User, Long>,
                                        RevisionRepository<User, Long, Integer> {

    Optional<User> findByUsername(String username);

    List<User> findAllByOrderByUsername(Pageable pageable);





}
