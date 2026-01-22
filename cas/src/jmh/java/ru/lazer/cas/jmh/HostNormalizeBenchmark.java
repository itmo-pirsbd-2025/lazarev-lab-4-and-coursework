package ru.lazer.cas.jmh;

import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 5, time = 300, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 10, time = 300, timeUnit = TimeUnit.MILLISECONDS)
@Fork(2)
@State(Scope.Thread)
public class HostNormalizeBenchmark {

    @Param({"grafana.localtest.me", "prom.localtest.me", "whoami.localtest.me", "cas.localtest.me"})
    public String host;

    @Benchmark
    public String normalize_v1() {
        int dot = host.indexOf('.');
        if (dot <= 0) return host;
        return host.substring(0, dot);
    }

    @Benchmark
    public String normalize_v2_lowercase() {
        String h = host.toLowerCase();
        int dot = h.indexOf('.');
        if (dot <= 0) return h;
        return h.substring(0, dot);
    }
}
