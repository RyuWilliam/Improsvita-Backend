package co.improsvita.domain.service;

import co.improsvita.domain.model.Sowing;
import co.improsvita.domain.model.SowingReview;
import co.improsvita.domain.model.SowingStatus;
import co.improsvita.domain.repository.SowingRepository;
import co.improsvita.domain.repository.SowingReviewRepository;
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
class SowingReviewServiceTest {

    @Mock
    private SowingRepository sowingRepository;

    @Mock
    private SowingReviewRepository sowingReviewRepository;

    @InjectMocks
    private SowingReviewService sowingReviewService;

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
        when(sowingReviewRepository.save(any(SowingReview.class))).thenAnswer(i -> i.getArgument(0));
    }

    @Test
    void create_valida_guardaLaRevision() {
        stubSowing();
        stubSave();

        SowingReview review = sowingReviewService.createReview(
                1, LocalDate.now().minusDays(2), 40, "  Buena germinación  ");

        assertEquals(1, review.getSowingId());
        assertEquals(40, review.getGerminatedQuantity());
        assertEquals("Buena germinación", review.getNotes());
    }

    @Test
    void create_sinFecha_usaHoy() {
        stubSowing();
        stubSave();

        SowingReview review = sowingReviewService.createReview(1, null, 10, null);

        assertEquals(LocalDate.now(), review.getReviewDate());
    }

    @Test
    void create_germinadaIgualASembrada_esValida() {
        stubSowing();
        stubSave();

        SowingReview review = sowingReviewService.createReview(1, null, 100, null);

        assertEquals(100, review.getGerminatedQuantity());
    }

    @Test
    void create_germinadaMayorQueSembrada_lanzaIllegalArgument() {
        stubSowing();

        assertThrows(IllegalArgumentException.class,
                () -> sowingReviewService.createReview(1, null, 101, null));
        verify(sowingReviewRepository, never()).save(any());
    }

    @Test
    void create_cantidadNegativa_lanzaIllegalArgument() {
        stubSowing();

        assertThrows(IllegalArgumentException.class,
                () -> sowingReviewService.createReview(1, null, -5, null));
    }

    @Test
    void create_sinCantidad_lanzaIllegalArgument() {
        stubSowing();

        assertThrows(IllegalArgumentException.class,
                () -> sowingReviewService.createReview(1, null, null, null));
    }

    @Test
    void create_fechaFutura_lanzaIllegalArgument() {
        stubSowing();

        assertThrows(IllegalArgumentException.class,
                () -> sowingReviewService.createReview(1, LocalDate.now().plusDays(1), 10, null));
    }

    @Test
    void create_fechaAnteriorALaSiembra_lanzaIllegalArgument() {
        stubSowing();

        assertThrows(IllegalArgumentException.class,
                () -> sowingReviewService.createReview(1, LocalDate.now().minusDays(11), 10, null));
    }

    @Test
    void create_siembraInexistente_lanzaIllegalArgument() {
        when(sowingRepository.getById(99)).thenReturn(null);

        assertThrows(IllegalArgumentException.class,
                () -> sowingReviewService.createReview(99, null, 10, null));
    }

    @Test
    void create_siembraTerminal_lanzaIllegalState() {
        sowing.setStatus(SowingStatus.COMPLETED);
        stubSowing();

        assertThrows(IllegalStateException.class,
                () -> sowingReviewService.createReview(1, null, 10, null));
        verify(sowingReviewRepository, never()).save(any());
    }

    @Test
    void create_siembraInactiva_lanzaIllegalState() {
        sowing.setActive(false);
        stubSowing();

        assertThrows(IllegalStateException.class,
                () -> sowingReviewService.createReview(1, null, 10, null));
    }

    @Test
    void latestGerminated_sinRevisiones_devuelveNull() {
        when(sowingReviewRepository.getLatestBySowingId(1)).thenReturn(null);

        assertNull(sowingReviewService.getLatestGerminatedQuantity(1));
    }

    @Test
    void latestGerminated_conRevisiones_devuelveLaUltima() {
        SowingReview latest = new SowingReview();
        latest.setGerminatedQuantity(75);
        when(sowingReviewRepository.getLatestBySowingId(1)).thenReturn(latest);

        assertEquals(75, sowingReviewService.getLatestGerminatedQuantity(1));
    }
}