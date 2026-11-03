package co.improsvita.domain.service;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import co.improsvita.domain.model.Sowing;
import co.improsvita.domain.model.SowingStatus;
import co.improsvita.domain.repository.ReservationRepository;
import co.improsvita.domain.repository.SeedLotRepository;
import co.improsvita.domain.repository.SeedMovementRepository;
import co.improsvita.domain.repository.SowingRepository;
import co.improsvita.domain.repository.VisitRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Pruebas del logging de auditoría de cambios de estado de siembras.
 */
@ExtendWith(MockitoExtension.class)
class SowingServiceTest {

    @Mock private SowingRepository sowingRepository;
    @Mock private SeedMovementRepository seedMovementRepository;
    @Mock private SeedLotRepository seedLotRepository;
    @Mock private VisitRepository visitRepository;
    @Mock private ReservationRepository reservationRepository;

    private SowingService service;
    private Logger serviceLogger;
    private ListAppender<ILoggingEvent> logs;

    @BeforeEach
    void setUp() {
        service = new SowingService(sowingRepository, seedMovementRepository, seedLotRepository,
                visitRepository, reservationRepository);
        serviceLogger = (Logger) LoggerFactory.getLogger(SowingService.class);
        logs = new ListAppender<>();
        logs.start();
        serviceLogger.addAppender(logs);
    }

    @AfterEach
    void tearDown() {
        serviceLogger.detachAppender(logs);
    }

    private Sowing sowing(SowingStatus status, Integer sown) {
        Sowing sowing = new Sowing();
        sowing.setId(5);
        sowing.setStatus(status);
        sowing.setQuantitySown(sown);
        return sowing;
    }

    @Test
    void changeStatus_validChange_savesNewStatus() {
        when(sowingRepository.getById(5)).thenReturn(sowing(SowingStatus.IN_PROGRESS, 40));
        when(sowingRepository.save(any(Sowing.class))).thenAnswer(inv -> inv.getArgument(0));

        Sowing result = service.changeStatus(5, SowingStatus.COMPLETED);

        assertEquals(SowingStatus.COMPLETED, result.getStatus());
    }

    @Test
    void changeStatus_logsPreviousAndNewStatus() {
        when(sowingRepository.getById(5)).thenReturn(sowing(SowingStatus.IN_PROGRESS, 40));
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
        when(sowingRepository.getById(5)).thenReturn(sowing(SowingStatus.COMPLETED, 40));

        assertThrows(IllegalStateException.class,
                () -> service.changeStatus(5, SowingStatus.IN_PROGRESS));

        verify(sowingRepository, never()).save(any());
        assertTrue(logs.list.stream().anyMatch(e -> e.getFormattedMessage().contains("RECHAZADO")));
    }

    @Test
    void changeStatus_sameStatus_doesNotLogChange() {
        when(sowingRepository.getById(5)).thenReturn(sowing(SowingStatus.IN_PROGRESS, 40));
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
}
