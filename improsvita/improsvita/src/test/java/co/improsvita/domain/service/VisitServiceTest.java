package co.improsvita.domain.service;

import co.improsvita.domain.model.Sowing;
import co.improsvita.domain.model.SowingStatus;
import co.improsvita.domain.model.Visit;
import co.improsvita.domain.model.VisitMethod;
import co.improsvita.domain.model.VisitResult;
import co.improsvita.domain.repository.SowingRepository;
import co.improsvita.domain.repository.VisitRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VisitServiceTest {

    @Mock
    private VisitRepository visitRepository;

    @Mock
    private SowingRepository sowingRepository;

    @InjectMocks
    private VisitService visitService;

    private Sowing sowing;

    @BeforeEach
    void setUp() {
        sowing = new Sowing();
        sowing.setId(1);
        sowing.setQuantitySown(100);
        sowing.setSowingDate(LocalDate.now().minusDays(10));
        sowing.setStatus(SowingStatus.IN_PROGRESS);
        sowing.setActive(true);
    }

    private void stubSowing() {
        when(sowingRepository.getById(1)).thenReturn(sowing);
    }

    private void stubSave() {
        when(visitRepository.save(any(Visit.class))).thenAnswer(i -> i.getArgument(0));
    }

    @Test
    void create_valida_guardaLaVisita() {
        stubSowing();
        stubSave();

        Visit visit = visitService.createVisit(
                1, LocalDate.now().minusDays(2), 40, VisitMethod.MUESTREO, VisitResult.CONTEO_FINAL);

        assertEquals(1, visit.getSowingId());
        assertEquals(40, visit.getSuitableEstimated());
        assertEquals(VisitMethod.MUESTREO, visit.getVisitMethod());
        assertEquals(VisitResult.CONTEO_FINAL, visit.getResult());
        assertEquals(true, visit.getActive());
    }

    @Test
    void create_sinFecha_usaHoy() {
        stubSowing();
        stubSave();

        Visit visit = visitService.createVisit(1, null, 10, null, null);

        assertEquals(LocalDate.now(), visit.getVisitDate());
        assertEquals(VisitMethod.INSPECCION_VISUAL, visit.getVisitMethod());
        assertEquals(VisitResult.ESTIMACION, visit.getResult());
    }

    @Test
    void create_aptasIgualASembradas_esValida() {
        stubSowing();
        stubSave();

        Visit visit = visitService.createVisit(1, null, 100, null, null);

        assertEquals(100, visit.getSuitableEstimated());
    }

    @Test
    void create_aptasMayorQueSembradas_lanzaIllegalArgument() {
        stubSowing();

        assertThrows(IllegalArgumentException.class,
                () -> visitService.createVisit(1, null, 101, null, null));
        verify(visitRepository, never()).save(any());
    }

    @Test
    void create_cantidadNegativa_lanzaIllegalArgument() {
        stubSowing();

        assertThrows(IllegalArgumentException.class,
                () -> visitService.createVisit(1, null, -5, null, null));
    }

    @Test
    void create_sinCantidad_lanzaIllegalArgument() {
        stubSowing();

        assertThrows(IllegalArgumentException.class,
                () -> visitService.createVisit(1, null, null, null, null));
    }

    @Test
    void create_fechaFutura_lanzaIllegalArgument() {
        stubSowing();

        assertThrows(IllegalArgumentException.class,
                () -> visitService.createVisit(1, LocalDate.now().plusDays(1), 10, null, null));
    }

    @Test
    void create_fechaAnteriorALaSiembra_lanzaIllegalArgument() {
        stubSowing();

        assertThrows(IllegalArgumentException.class,
                () -> visitService.createVisit(1, LocalDate.now().minusDays(11), 10, null, null));
    }

    @Test
    void create_siembraInexistente_lanzaIllegalArgument() {
        when(sowingRepository.getById(99)).thenReturn(null);

        assertThrows(IllegalArgumentException.class,
                () -> visitService.createVisit(99, null, 10, null, null));
        verify(visitRepository, never()).save(any());
    }

    @Test
    void update_valido_actualizaLaVisita() {
        Visit existing = new Visit();
        existing.setId(5);
        existing.setSowingId(1);
        existing.setVisitDate(LocalDate.now().minusDays(3));
        existing.setSuitableEstimated(20);
        when(visitRepository.getById(5)).thenReturn(existing);
        stubSowing();
        stubSave();

        Visit updated = visitService.updateVisit(
                5, LocalDate.now().minusDays(1), 60, VisitMethod.CONTEO_TOTAL,
                VisitResult.VALIDADA, false);

        assertEquals(60, updated.getSuitableEstimated());
        assertEquals(VisitMethod.CONTEO_TOTAL, updated.getVisitMethod());
        assertEquals(VisitResult.VALIDADA, updated.getResult());
        assertEquals(false, updated.getActive());
    }

    @Test
    void update_sinFecha_conservaLaAnterior() {
        LocalDate previous = LocalDate.now().minusDays(3);
        Visit existing = new Visit();
        existing.setId(5);
        existing.setSowingId(1);
        existing.setVisitDate(previous);
        existing.setSuitableEstimated(20);
        when(visitRepository.getById(5)).thenReturn(existing);
        stubSowing();
        stubSave();

        Visit updated = visitService.updateVisit(5, null, 30, null, null, null);

        assertEquals(previous, updated.getVisitDate());
    }

    @Test
    void update_aptasMayorQueSembradas_lanzaIllegalArgument() {
        Visit existing = new Visit();
        existing.setId(5);
        existing.setSowingId(1);
        when(visitRepository.getById(5)).thenReturn(existing);
        stubSowing();

        assertThrows(IllegalArgumentException.class,
                () -> visitService.updateVisit(5, null, 150, null, null, null));
        verify(visitRepository, never()).save(any());
    }

    @Test
    void latest_sinVisitas_devuelveNull() {
        when(visitRepository.getLatestActiveBySowingId(1)).thenReturn(null);

        assertNull(visitService.getLatestAvailableSeedlings(1));
    }

    @Test
    void latest_conVisitas_devuelveLasAptas() {
        Visit latest = new Visit();
        latest.setSuitableEstimated(75);
        when(visitRepository.getLatestActiveBySowingId(1)).thenReturn(latest);

        assertEquals(75, visitService.getLatestAvailableSeedlings(1));
    }
}
