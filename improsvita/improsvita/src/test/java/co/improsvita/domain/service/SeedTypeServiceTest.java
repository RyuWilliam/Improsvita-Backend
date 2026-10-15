package co.improsvita.domain.service;

import co.improsvita.domain.model.SeedTypeCatalog;
import co.improsvita.domain.repository.SeedTypeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SeedTypeServiceTest {

    @Mock
    private SeedTypeRepository seedTypeRepository;

    @InjectMocks
    private SeedTypeService seedTypeService;

    private SeedTypeCatalog existing(Integer id, String name) {
        return new SeedTypeCatalog(id, name, null);
    }

    @Test
    void create_guardaNombreSinEspacios() {
        when(seedTypeRepository.getByName("ORGANIC")).thenReturn(null);
        when(seedTypeRepository.save(any(SeedTypeCatalog.class))).thenAnswer(i -> i.getArgument(0));

        SeedTypeCatalog created = seedTypeService.createSeedType("  ORGANIC  ", "Orgánica");

        assertEquals("ORGANIC", created.getName());
        assertEquals("Orgánica", created.getDescription());
    }

    @Test
    void create_nombreVacio_lanzaIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () -> seedTypeService.createSeedType("   ", null));
        verify(seedTypeRepository, never()).save(any());
    }

    @Test
    void create_nombreDuplicado_lanzaIllegalState() {
        when(seedTypeRepository.getByName("hybrid")).thenReturn(existing(1, "HYBRID"));

        assertThrows(IllegalStateException.class, () -> seedTypeService.createSeedType("hybrid", null));
        verify(seedTypeRepository, never()).save(any());
    }

    @Test
    void update_conservaSuPropioNombre() {
        when(seedTypeRepository.getById(1)).thenReturn(existing(1, "HYBRID"));
        when(seedTypeRepository.getByName("HYBRID")).thenReturn(existing(1, "HYBRID"));
        when(seedTypeRepository.save(any(SeedTypeCatalog.class))).thenAnswer(i -> i.getArgument(0));

        SeedTypeCatalog updated = seedTypeService.updateSeedType(1, "HYBRID", "Nueva descripción");

        assertEquals("Nueva descripción", updated.getDescription());
    }

    @Test
    void update_nombreDeOtroTipo_lanzaIllegalState() {
        when(seedTypeRepository.getById(1)).thenReturn(existing(1, "HYBRID"));
        when(seedTypeRepository.getByName("MODIFIED")).thenReturn(existing(3, "MODIFIED"));

        assertThrows(IllegalStateException.class, () -> seedTypeService.updateSeedType(1, "MODIFIED", null));
        verify(seedTypeRepository, never()).save(any());
    }

    @Test
    void delete_conSemillasAsociadas_lanzaIllegalStateYNoBorra() {
        when(seedTypeRepository.getById(1)).thenReturn(existing(1, "HYBRID"));
        when(seedTypeRepository.isInUse(1)).thenReturn(true);

        assertThrows(IllegalStateException.class, () -> seedTypeService.deleteSeedType(1));
        verify(seedTypeRepository, never()).deleteById(any());
    }

    @Test
    void delete_sinSemillas_borra() {
        when(seedTypeRepository.getById(5)).thenReturn(existing(5, "ORGANIC"));
        when(seedTypeRepository.isInUse(5)).thenReturn(false);

        seedTypeService.deleteSeedType(5);

        verify(seedTypeRepository).deleteById(5);
    }

    @Test
    void getById_inexistente_devuelveNull() {
        when(seedTypeRepository.getById(99)).thenReturn(null);

        assertNull(seedTypeService.getSeedTypeById(99));
    }

        @Test
    void resolve_porId_devuelveElTipo() {
        when(seedTypeRepository.getById(1)).thenReturn(existing(1, "HYBRID"));

        assertEquals("HYBRID", seedTypeService.resolveSeedType(1, null).getName());
    }

    @Test
    void resolve_porNombreDeTextoViejo_devuelveElTipo() {
        when(seedTypeRepository.getByName("HYBRID")).thenReturn(existing(1, "HYBRID"));

        assertEquals(1, seedTypeService.resolveSeedType(null, " HYBRID ").getId());
    }

    @Test
    void resolve_sinDatos_devuelveNullYConIdInexistenteLanzaError() {
        assertNull(seedTypeService.resolveSeedType(null, "  "));

        when(seedTypeRepository.getById(99)).thenReturn(null);
        assertThrows(IllegalArgumentException.class, () -> seedTypeService.resolveSeedType(99, null));
    }
}