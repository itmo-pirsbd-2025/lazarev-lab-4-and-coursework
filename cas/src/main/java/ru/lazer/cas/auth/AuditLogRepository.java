package ru.lazer.cas.auth;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class AuditLogRepository {
    private final JdbcTemplate jdbc;
    public AuditLogRepository(JdbcTemplate jdbc) { this.jdbc = jdbc; }

    public void log(String username, String host, String path, String decision, String remoteAddr) {
        jdbc.update(
                "INSERT INTO audit_log(username, host, path, decision, remote_addr) VALUES (?,?,?,?,?)",
                username, host, path, decision, remoteAddr
        );
    }
}
