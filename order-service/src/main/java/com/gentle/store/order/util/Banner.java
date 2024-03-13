package com.gentle.store.order.util;

import org.springframework.boot.SpringBootVersion;
import org.springframework.core.SpringVersion;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Locale;

public final class Banner {

    private static final String FIGLET = """
                           _                                    _         \s
              ___  _ __ __| | ___ _ __      ___  ___ _ ____   _(_) ___ ___\s
             / _ \\| '__/ _` |/ _ \\ '__|____/ __|/ _ \\ '__\\ \\ / / |/ __/ _ \\
            | (_) | | | (_| |  __/ | |_____\\__ \\  __/ |   \\ V /| | (_|  __/
             \\___/|_|  \\__,_|\\___|_|       |___/\\___|_|    \\_/ |_|\\___\\___|
                                                                          \s""";
    private static final String SERVICE_HOST = System.getenv("CUSTOMER_SERVICE_HOST");
    private static final String KUBERNETES = SERVICE_HOST == null
        ? "N/A"
        : STR."CUSTOMER_SERVICE_HOST=\{SERVICE_HOST}, CUSTOMER_SERVICE_PORT=\{System.getenv("CUSTOMER_SERVICE_PORT")}";

    /**
     * Banner für den Server-Start.
     */
    public static final String TEXT = STR."""

        \{FIGLET}
        (C) Caleb Gyamfi, Gentle Bank
        Version             2024.2.23
        Spring Boot         \{SpringBootVersion.getVersion()}
        Spring Security     keine Security
        Spring Framework    \{SpringVersion.getVersion()}
        Hibernate           \{org.hibernate.Version.getVersionString()}
        Java                \{Runtime.version()} - \{System.getProperty("java.vendor")}
        Betriebssystem      \{System.getProperty("os.name")}
        Rechnername         \{getLocalhost().getHostName()}
        IP-Adresse          \{getLocalhost().getHostAddress()}
        Heap: Size          \{Runtime.getRuntime().totalMemory() / (1024L * 1024L)} MiB
        Heap: Free          \{Runtime.getRuntime().freeMemory() / (1024L * 1024L)} MiB
        Kubernetes          \{KUBERNETES}
        Username            \{System.getProperty("user.name")}
        JVM Locale          \{Locale.getDefault().toString()}
        GraphiQL            /graphiql   {"Authorization": "Basic YWRtaW46cA=="}
        OpenAPI             /swagger-ui.html /v3/api-docs.yaml
        H2 Console          /h2-console (JDBC URL: "jdbc:h2:mem:testdb" mit User "sa" und Passwort "")
        """;

    private Banner() {
    }

    private static InetAddress getLocalhost() {
        try {
            return InetAddress.getLocalHost();
        } catch (final UnknownHostException ex) {
            throw new IllegalStateException(ex);
        }
    }
}
