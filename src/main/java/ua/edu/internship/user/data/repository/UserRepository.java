package ua.edu.internship.user.data.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import ua.edu.internship.user.data.entity.UserEntity;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByEmail(String email);
}
