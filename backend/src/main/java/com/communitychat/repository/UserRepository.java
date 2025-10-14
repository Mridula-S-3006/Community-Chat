package com.communitychat.repository;

import java.util.Optional;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.communitychat.model.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findTop5ByUsernameContainingIgnoreCase(String username);

    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);

    Optional<User> findByUsernameIgnoreCase(String username);

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);
}
