package co.improsvita.domain.service;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import co.improsvita.domain.model.Sowing;
import co.improsvita.domain.model.SowingStatus;
import co.improsvita.domain.repository.SowingRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Pruebas de actualización de estado (RF 2.3), germinación (RF 3.1)
 * y del logging de auditoría de cambios de estado.
 */
@ExtendWith(MockitoExtension.class)
class SowingServiceTest {

    @Mock private SowingRepository sowingRepository;

    private SowingService service;
    private Logger serviceLogger;
    private ListAppender<ILoggingEvent> logs;

    @BeforeEach
    void setUp() {
        service = new SowingService(sowingRepository);
        serviceLogger = (Logger) LoggerFactory.getLogger(SowingService.class);
        logs = new ListAppender<>();
        logs.start();
        serviceLogger.addAppender(logs);
    }

    @AfterEach
    void tearDown() {
        serviceLogger.detachAppender(logs);
    }

    private Sowing sowing(SowingStatus status, String sown) {
        Sowing sowing = new Sowing();
        sowing.setId(5);
        sowing.setStatus(status);
        sowing.setQuantitySown(new BigDecimal(sown));
        return sowing;
    }

    // ---------- cambio de estado ----------

    @Test
    void changeStatus_validChange_savesNewStatus() {
        when(sowingRepository.getById(5)).thenReturn(sowing(SowingStatus.IN_PROGRESS, "40"));
        when(sowingRepository.save(any(Sowing.class))).thenAnswer(inv -> inv.getArgument(0));

        Sowing result = service.changeStatus(5, SowingStatus.COMPLETED);

        assertEquals(SowingStatus.COMPLETED, result.getStatus());
    }

    @Test
    void changeStatus_logsPreviousAndNewStatus() {
        when(sowingRepository.getById(5)).thenReturn(sowing(SowingStatus.IN_PROGRESS, "40"));
        when(sowingRepository.save(any(Sowing.class))).thenAnswer(inv -> inv.getArgument(0));

        service.changeStatus(5, SowingStatus.COMPLETED);

        assertTrue(logs.list.stream().anyMatch(e ->
                        e.getFormattedMessage().contains("id=5")
                                && e.getFormattedMessage().contains("IN_PROGRESS")
                                && e.getFormattedMessage().contains("COMPLETED")),
                "debe registrar el cambio de estado con id, estado anterior y nuevo");
    }

    @Test
    void changeStatus_fromTerminalState_throwsAndLogsRejection() {
        when(sowingRepository.getById(5)).thenReturn(sowing(SowingStatus.COMPLETED, "40"));

        assertThrows(IllegalStateException.class,
                () -> service.changeStatus(5, SowingStatus.IN_PROGRESS));

        verify(sowingRepository, never()).save(any());
        assertTrue(logs.list.stream().anyMatch(e -> e.getFormattedMessage().contains("RECHAZADO")));
    }

    @Test
    void changeStatus_sameStatus_doesNotLogChange() {
        when(sowingRepository.getById(5)).thenReturn(sowing(SowingStatus.IN_PROGRESS, "40"));
        when(sowingRepository.save(any(Sowing.class))).thenAnswer(inv -> inv.getArgument(0));

        service.changeStatus(5, SowingStatus.IN_PROGRESS);

        assertTrue(logs.list.isEmpty());
    }

    @Test
    void changeStatus_unknownSowing_throws() {
        when(sowingRepository.getById(99)).thenReturn(null);

        assertThrows(IllegalArgumentException.class,
                () -> service.changeStatus(99, SowingStatus.COMPLETED));
    }

    // ---------- germinación / plántulas ----------

    @Test
    void updateGermination_valid_savesQuantity() {
        when(sowingRepository.getById(5)).thenReturn(sowing(SowingStatus.IN_PROGRESS, "40"));
        when(sowingRepository.save(any(Sowing.class))).thenAnswer(inv -> inv.getArgument(0));

        Sowing result = service.updateGermination(5, new BigDecimal("35"));

        assertEquals(0, new BigDecimal("35").compareTo(result.getGerminatedQuantity()));
    }

    @Test
    void updateGermination_moreThanSown_throws() {
        when(sowingRepository.getById(5)).thenReturn(sowing(SowingStatus.IN_PROGRESS, "40"));

        assertThrows(IllegalArgumentException.class,
                () -> service.updateGermination(5, new BigDecimal("41")));
        verify(sowingRepository, never()).save(any());
    }

    @Test
    void updateGermination_negative_throws() {
        when(sowingRepository.getById(5)).thenReturn(sowing(SowingStatus.IN_PROGRESS, "40"));

        assertThrows(IllegalArgumentException.class,
                () -> service.updateGermination(5, new BigDecimal("-1")));
    }
}
