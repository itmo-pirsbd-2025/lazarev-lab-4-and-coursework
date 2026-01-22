package ru.lazer.cas.db;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {
    Optional<UserAccount> findByUsername(String username);

    /**
     * user_roles is the join table created by @ManyToMany(UserAccount.roles).
     * When deleting a Role we must cleanup this join table to avoid FK violations.
     */
    @Modifying
    @Query(value = "DELETE FROM user_roles WHERE role_id = :roleId", nativeQuery = true)
    void deleteRoleLinks(@Param("roleId") Long roleId);
}
