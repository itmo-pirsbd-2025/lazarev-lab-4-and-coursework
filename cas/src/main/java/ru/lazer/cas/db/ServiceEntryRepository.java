package ru.lazer.cas.db;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ServiceEntryRepository extends JpaRepository<ServiceEntry, Long> {
    Optional<ServiceEntry> findByHost(String host);
}
