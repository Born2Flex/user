package ua.edu.ukma.user.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import ua.edu.ukma.user.entity.UserEntity;

public interface UserRepository extends JpaRepository<UserEntity, Integer> {
    Optional<UserEntity> findByEmail(String email);
}
