package com.communitychat.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.communitychat.model.entity.Group;
import com.communitychat.model.entity.User;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {

    // Optional: global uniqueness check
    Optional<Group> findByName(String name);

    // Fixed: uniqueness check per owner
    Optional<Group> findByNameAndOwner(String name, User owner);
}
