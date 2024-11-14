package ua.edu.ukma.user.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import ua.edu.ukma.user.entity.RoleEntity;

public interface RoleRepository extends JpaRepository<RoleEntity, Integer> {
    Optional<RoleEntity> findByName(String name);
}
