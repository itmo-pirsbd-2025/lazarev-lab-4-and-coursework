package ru.lazer.cas.db;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PermissionRepository extends JpaRepository<Permission, PermissionKey> {

    void deleteByRoleId(Long roleId);
    void deleteByServiceId(Long serviceId);
    List<Permission> findByServiceId(Long serviceId);
}
