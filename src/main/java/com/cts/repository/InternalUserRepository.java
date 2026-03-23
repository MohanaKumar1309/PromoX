package com.cts.repository;

import java.util.List;
import java.util.Optional;

import com.cts.entity.InternalUser;
import com.cts.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InternalUserRepository extends JpaRepository<InternalUser, Long> {
    Optional<InternalUser> findByEmail(String email);
    List<InternalUser> findByRole(Role role);
}
