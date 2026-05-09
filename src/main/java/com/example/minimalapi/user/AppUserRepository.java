package com.example.minimalapi.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {

    Optional<AppUser> findByUsername(String username);

    boolean existsByUsername(String username);

    @Query("SELECT DISTINCT u FROM AppUser u "
            + "LEFT JOIN FETCH u.roles r "
            + "LEFT JOIN FETCH r.authorities "
            + "WHERE u.username = :username")
    Optional<AppUser> findWithRolesAndAuthoritiesByUsername(@Param("username") String username);
}
