package ru.lazer.cas.db;

import jakarta.persistence.*;

@Entity
@Table(name = "services")
public class ServiceEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 128)
    private String name;

    @Column(nullable = false, unique = true, length = 255)
    private String host;

    @Column(nullable = false)
    private boolean enabled = true;

    public Long getId() { return id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getHost() { return host; }
    public void setHost(String host) { this.host = host; }

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
}
