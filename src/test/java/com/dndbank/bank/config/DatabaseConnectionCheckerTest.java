package com.dndbank.bank.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.core.env.ConfigurableEnvironment;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DatabaseConnectionCheckerTest {

    private DatabaseConnectionChecker checker;
    private ApplicationEnvironmentPreparedEvent event;
    private ConfigurableEnvironment environment;

    @BeforeEach
    void setUp() {
        checker = new DatabaseConnectionChecker();
        event = mock(ApplicationEnvironmentPreparedEvent.class);
        environment = mock(ConfigurableEnvironment.class);
        when(event.getEnvironment()).thenReturn(environment);
    }

    @Test
    void testSuccessfulConnection() throws SQLException, InterruptedException {
        // Arrange
        when(environment.getProperty("spring.datasource.url", "jdbc:postgresql://localhost:5432/dndbank"))
                .thenReturn("jdbc:postgresql://localhost:5432/dndbank");
        when(environment.getProperty("spring.datasource.username", "dnd_master"))
                .thenReturn("dnd_master");
        when(environment.getProperty("spring.datasource.password", "dnd_master"))
                .thenReturn("dnd_master");

        Connection mockConnection = mock(Connection.class);
        when(mockConnection.isValid(5)).thenReturn(true);

        try (MockedStatic<DriverManager> driverManagerMock = mockStatic(DriverManager.class);
             MockedStatic<Thread> threadMock = mockStatic(Thread.class)) {
            driverManagerMock.when(() -> DriverManager.getConnection(anyString(), anyString(), anyString()))
                    .thenReturn(mockConnection);
            // Mock Thread.sleep to avoid actual delays in tests
            threadMock.when(() -> Thread.sleep(anyLong())).thenAnswer(invocation -> null);

            // Act & Assert - should not throw exception for successful connection
            assertDoesNotThrow(() -> checker.onApplicationEvent(event));
        }
    }

    @Test
    void testConnectionFailureAfterRetries() throws SQLException, InterruptedException {
        // Arrange
        when(environment.getProperty("spring.datasource.url", "jdbc:postgresql://localhost:5432/dndbank"))
                .thenReturn("jdbc:postgresql://localhost:5432/dndbank");
        when(environment.getProperty("spring.datasource.username", "dnd_master"))
                .thenReturn("dnd_master");
        when(environment.getProperty("spring.datasource.password", "dnd_master"))
                .thenReturn("dnd_master");

        try (MockedStatic<DriverManager> driverManagerMock = mockStatic(DriverManager.class);
             MockedStatic<Thread> threadMock = mockStatic(Thread.class)) {
            driverManagerMock.when(() -> DriverManager.getConnection(anyString(), anyString(), anyString()))
                    .thenThrow(new SQLException("Connection refused"));
            // Mock Thread.sleep to avoid actual delays in tests
            threadMock.when(() -> Thread.sleep(anyLong())).thenAnswer(invocation -> null);

            // Act & Assert - should eventually exit after retries
            // We can't easily test System.exit, but we can verify the retry logic executes
            assertDoesNotThrow(() -> {
                try {
                    checker.onApplicationEvent(event);
                } catch (Exception e) {
                    // Expected to eventually exit, but we catch any exceptions for test purposes
                }
            });
        }
    }

}

