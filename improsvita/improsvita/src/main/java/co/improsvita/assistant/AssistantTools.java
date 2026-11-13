package co.improsvita.assistant;

import co.improsvita.domain.model.Bed;
import co.improsvita.domain.model.Contact;
import co.improsvita.domain.model.ContactRole;
import co.improsvita.domain.model.Reservation;
import co.improsvita.domain.model.ReservationStatus;
import co.improsvita.domain.model.Sale;
import co.improsvita.domain.model.Seed;
import co.improsvita.domain.model.SeedLot;
import co.improsvita.domain.model.SeedLotStatus;
import co.improsvita.domain.model.Sowing;
import co.improsvita.domain.model.SowingStatus;
import co.improsvita.domain.service.BedService;
import co.improsvita.domain.service.ContactService;
import co.improsvita.domain.service.InventoryService;
import co.improsvita.domain.service.ReservationService;
import co.improsvita.domain.service.SaleService;
import co.improsvita.domain.service.SeedService;
import co.improsvita.domain.service.SowingService;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.text.Normalizer;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class AssistantTools {

    private static final Set<SowingStatus> ACTIVE_SOWING_STATUSES =
            Set.of(SowingStatus.PLANNED, SowingStatus.IN_PROGRESS);
    private static final Set<ReservationStatus> LIVE_RESERVATION_STATUSES =
            Set.of(ReservationStatus.PENDING, ReservationStatus.CONFIRMED);
    private static final Set<String> SEARCH_STOPWORDS = Set.of(
            "el", "la", "los", "las", "un", "una", "unos", "unas", "de", "del", "al",
            "que", "hay", "tenemos", "tiene", "tienes", "tienen", "semilla", "semillas",
            "cliente", "clientes", "inventario", "stock", "disponible", "disponibles",
            "por", "para", "con", "en", "me", "mi", "y", "o", "como", "cual", "cuales",
            "cuanto", "cuanta", "cuantos", "cuantas");
    private static final DateTimeFormatter DATE = DateTimeFormatter.ISO_LOCAL_DATE;

    private final SeedService seedService;
    private final SowingService sowingService;
    private final SaleService saleService;
    private final ReservationService reservationService;
    private final InventoryService inventoryService;
    private final ContactService contactService;
    private final BedService bedService;
    private final AssistantToolCallTracker tracker;
    private final int maxRows;

    public AssistantTools(SeedService seedService,
                          SowingService sowingService,
                          SaleService saleService,
                          ReservationService reservationService,
                          InventoryService inventoryService,
                          ContactService contactService,
                          BedService bedService,
                          AssistantToolCallTracker tracker,
                          @Value("${assistant.max-tool-rows:20}") int maxRows) {
        this.seedService = seedService;
        this.sowingService = sowingService;
        this.saleService = saleService;
        this.reservationService = reservationService;
        this.inventoryService = inventoryService;
        this.contactService = contactService;
        this.bedService = bedService;
        this.tracker = tracker;
        this.maxRows = Math.max(1, maxRows);
    }

    @Tool(description = """
            Consulta las plántulas disponibles para vender, agrupadas por semilla.
            La disponibilidad se calcula con la última visita activa de cada siembra menos
            las ventas activas. Úsala para preguntas como "¿cuántas plántulas de tomate hay?"
            o "¿qué hay disponible para vender?".
            """)
    public DisponibilidadPlantulas consultarDisponibilidadPlantulas(
            @ToolParam(description = "Nombre o parte del nombre de la semilla. Vacío o null para todas.",
                    required = false) String semilla) {
        tracker.record("disponibilidad");
        Snapshot snapshot = snapshot();
        String query = normalize(semilla);

        Map<Integer, List<DetalleDisponibilidad>> grouped = new LinkedHashMap<>();
        Map<Integer, Seed> seedsById = new LinkedHashMap<>();

        for (Sowing sowing : activeSowings(snapshot)) {
            Seed seed = seedForSowing(sowing, snapshot);
            if (seed == null) {
                continue;
            }
            if (!query.isEmpty() && !normalize(seed.getName()).contains(query)) {
                continue;
            }

            SaleService.Availability availability = saleService.getAvailabilityDetail(sowing.getId());
            Bed bed = snapshot.beds().get(sowing.getBedId());
            grouped.computeIfAbsent(seed.getId(), ignored -> new ArrayList<>())
                    .add(new DetalleDisponibilidad(
                            sowing.getId(),
                            bed != null ? bed.getCode() : "Cama " + sowing.getBedId(),
                            sowing.getStatus() != null ? sowing.getStatus().name() : "DESCONOCIDO",
                            availability.seedlingsAvailable(),
                            safeInt(availability.soldQuantity()),
                            safeInt(availability.available())));
            seedsById.putIfAbsent(seed.getId(), seed);
        }

        List<DisponibilidadPorSemilla> groups = grouped.entrySet().stream()
                .limit(maxRows)
                .map(entry -> {
                    Seed seed = seedsById.get(entry.getKey());
                    List<DetalleDisponibilidad> details = entry.getValue().stream()
                            .limit(10)
                            .toList();
                    int total = entry.getValue().stream()
                            .mapToInt(DetalleDisponibilidad::disponibles)
                            .sum();
                    long stock = seed != null && seed.getTotalAvailable() != null
                            ? seed.getTotalAvailable()
                            : 0;
                    return new DisponibilidadPorSemilla(
                            seed != null ? seed.getName() : "Semilla " + entry.getKey(),
                            total,
                            stock,
                            details);
                })
                .toList();

        int total = grouped.values().stream()
                .flatMap(List::stream)
                .mapToInt(DetalleDisponibilidad::disponibles)
                .sum();

        return new DisponibilidadPlantulas(
                semilla == null || semilla.isBlank() ? "Todas" : semilla.trim(),
                total,
                groups);
    }

    @Tool(description = """
            Lista las reservas vigentes (PENDING o CONFIRMED) con cliente, siembra, semilla,
            cama, estado y valor. Úsala para preguntas sobre compromisos o reservas pendientes.
            Si no se envían fechas, devuelve todas las reservas vigentes.
            """)
    public ReservasVigentes listarReservasVigentes(
            @ToolParam(description = "Fecha inicial en formato ISO yyyy-MM-dd. Opcional.",
                    required = false) LocalDate desde,
            @ToolParam(description = "Fecha final en formato ISO yyyy-MM-dd. Opcional.",
                    required = false) LocalDate hasta) {
        tracker.record("reservas");
        Snapshot snapshot = snapshot();

        List<Reservation> filtered = liveReservations().stream()
                .filter(reservation -> reservation.getReservationDate() != null)
                .filter(reservation -> desde == null
                        || !reservation.getReservationDate().isBefore(desde))
                .filter(reservation -> hasta == null
                        || !reservation.getReservationDate().isAfter(hasta))
                .sorted(Comparator.comparing(Reservation::getReservationDate).reversed())
                .toList();

        List<ReservaDetalle> reservas = filtered.stream()
                .limit(maxRows)
                .map(reservation -> {
                    Sowing sowing = snapshot.sowings().get(reservation.getSowingId());
                    Seed seed = seedForSowing(sowing, snapshot);
                    Bed bed = sowing != null ? snapshot.beds().get(sowing.getBedId()) : null;
                    Contact client = snapshot.contacts().get(reservation.getClientId());
                    return new ReservaDetalle(
                            reservation.getId(),
                            reservation.getReservationDate().format(DATE),
                            client != null ? client.getName() : "Cliente " + reservation.getClientId(),
                            reservation.getSowingId(),
                            seed != null ? seed.getName() : "Semilla desconocida",
                            bed != null ? bed.getCode() : "Cama desconocida",
                            reservation.getReservationStatus() != null
                                    ? reservation.getReservationStatus().name()
                                    : "DESCONOCIDO",
                            reservation.getTotalPrice());
                })
                .toList();

        return new ReservasVigentes(filtered.size(), reservas);
    }

    @Tool(description = """
            Lista las siembras activas (PLANNED o IN_PROGRESS) con semilla, cama, fecha,
            cantidad sembrada y plántulas disponibles. Úsala para revisar el estado operativo.
            """)
    public SiembrasActivas listarSiembrasActivas() {
        tracker.record("siembras");
        Snapshot snapshot = snapshot();
        List<Sowing> active = activeSowings(snapshot);

        List<SiembraDetalle> siembras = active.stream()
                .limit(maxRows)
                .map(sowing -> {
                    Seed seed = seedForSowing(sowing, snapshot);
                    Bed bed = snapshot.beds().get(sowing.getBedId());
                    SaleService.Availability availability =
                            saleService.getAvailabilityDetail(sowing.getId());
                    return new SiembraDetalle(
                            sowing.getId(),
                            seed != null ? seed.getName() : "Semilla desconocida",
                            bed != null ? bed.getCode() : "Cama desconocida",
                            sowing.getStatus() != null ? sowing.getStatus().name() : "DESCONOCIDO",
                            sowing.getSowingDate() != null ? sowing.getSowingDate().format(DATE) : null,
                            safeInt(sowing.getQuantitySown()),
                            availability.seedlingsAvailable(),
                            safeInt(availability.soldQuantity()),
                            safeInt(availability.available()));
                })
                .toList();

        return new SiembrasActivas(active.size(), siembras);
    }

    @Tool(description = """
            Resume las ventas activas en un rango de fechas: número de ventas, plántulas
            vendidas, valor total y ventas por semilla. Si no se envían fechas, usa los
            últimos 30 días.
            """)
    public ResumenVentas resumenVentas(
            @ToolParam(description = "Fecha inicial en formato ISO yyyy-MM-dd. Opcional.",
                    required = false) LocalDate desde,
            @ToolParam(description = "Fecha final en formato ISO yyyy-MM-dd. Opcional.",
                    required = false) LocalDate hasta) {
        tracker.record("ventas");
        LocalDate from = desde != null ? desde : LocalDate.now().minusDays(30);
        LocalDate to = hasta != null ? hasta : LocalDate.now();
        if (from.isAfter(to)) {
            throw new IllegalArgumentException("La fecha inicial no puede ser posterior a la final");
        }

        Snapshot snapshot = snapshot();
        List<Sale> sales = activeSales(from, to);

        Map<String, List<Sale>> bySeed = new LinkedHashMap<>();
        for (Sale sale : sales) {
            bySeed.computeIfAbsent(seedNameForSale(sale, snapshot), ignored -> new ArrayList<>())
                    .add(sale);
        }

        List<VentaPorSemilla> porSemilla = bySeed.entrySet().stream()
                .map(entry -> new VentaPorSemilla(
                        entry.getKey(),
                        entry.getValue().stream().mapToInt(sale -> safeInt(sale.getQuantity())).sum(),
                        totalOf(entry.getValue())))
                .sorted(Comparator.comparingInt(VentaPorSemilla::plantulasVendidas).reversed())
                .limit(5)
                .toList();

        return new ResumenVentas(
                from.format(DATE),
                to.format(DATE),
                sales.size(),
                sales.stream().mapToInt(sale -> safeInt(sale.getQuantity())).sum(),
                totalOf(sales),
                porSemilla);
    }

    @Tool(description = """
            Lista los lotes de semilla disponibles que vencen dentro de los próximos días.
            Si no se indica el número de días, usa 60.
            """)
    public LotesPorVencer listarLotesPorVencer(
            @ToolParam(description = "Días hacia adelante para revisar vencimientos. Opcional.",
                    required = false) Integer dias) {
        tracker.record("lotes-por-vencer");
        int days = dias != null && dias > 0 ? dias : 60;
        LocalDate today = LocalDate.now();
        LocalDate limit = today.plusDays(days);
        Snapshot snapshot = snapshot();

        List<SeedLot> candidates = inventoryService.getAllLots().stream()
                .filter(lot -> lot.getStatus() == SeedLotStatus.AVAILABLE)
                .filter(lot -> lot.getDueDate() != null)
                .filter(lot -> !lot.getDueDate().isBefore(today)
                        && !lot.getDueDate().isAfter(limit))
                .sorted(Comparator.comparing(SeedLot::getDueDate))
                .toList();

        List<LoteDetalle> lotes = candidates.stream()
                .limit(maxRows)
                .map(lot -> {
                    Seed seed = snapshot.seeds().get(lot.getSeedId());
                    return new LoteDetalle(
                            lot.getId(),
                            lot.getLotNumber(),
                            seed != null ? seed.getName() : "Semilla desconocida",
                            safeInt(lot.getAvailableQuantity()),
                            lot.getDueDate().format(DATE),
                            ChronoUnit.DAYS.between(today, lot.getDueDate()));
                })
                .toList();

        return new LotesPorVencer(candidates.size(), days, lotes);
    }

    @Tool(description = """
            Lista las semillas cuyo stock total en inventario está por debajo del umbral.
            Si no se indica umbral, usa 100 unidades. No confundir con plántulas disponibles.
            """)
    public StockBajo listarStockBajo(
            @ToolParam(description = "Umbral de stock en unidades. Opcional.", required = false)
            Integer umbral) {
        tracker.record("stock-bajo");
        int threshold = umbral != null && umbral > 0 ? umbral : 100;

        List<Seed> seeds = seedService.getAllSeeds().stream()
                .filter(seed -> seed.getTotalAvailable() != null
                        && seed.getTotalAvailable() < threshold)
                .sorted(Comparator.comparing(Seed::getTotalAvailable))
                .toList();

        List<StockSemilla> result = seeds.stream()
                .limit(maxRows)
                .map(seed -> new StockSemilla(
                        seed.getName(),
                        seed.getTotalAvailable(),
                        threshold - seed.getTotalAvailable()))
                .toList();

        return new StockBajo(threshold, seeds.size(), result);
    }

    @Tool(description = """
            Consulta el inventario de semillas en almacén, con nombre, tipo y stock total.
            No devuelve plántulas disponibles. Úsala para preguntas como "¿qué semillas hay?",
            "¿qué inventario tenemos?", "¿hay semillas de lechuga?" o
            "¿cuántas semillas de papa modificada hay?". Recibe un nombre o parte del nombre.
            """)
    public InventarioSemillas consultarInventarioSemillas(
            @ToolParam(description = "Nombre o parte del nombre de la semilla. Vacío o null para todas.",
                    required = false) String semilla) {
        tracker.record("semillas");
        String query = searchQuery(semilla);

        List<Seed> seeds = seedService.getAllSeeds().stream()
                .filter(seed -> Boolean.TRUE.equals(seed.getActive()))
                .filter(seed -> query.isEmpty() || matchesSearch(seed.getName(), query))
                .sorted(Comparator.comparing(Seed::getName, String.CASE_INSENSITIVE_ORDER))
                .toList();

        List<SemillaInventario> result = seeds.stream()
                .limit(maxRows)
                .map(seed -> new SemillaInventario(
                        seed.getId(),
                        seed.getName(),
                        seed.getSeedType() != null ? seed.getSeedType().getName() : "SIN TIPO",
                        seed.getTotalAvailable() != null ? seed.getTotalAvailable() : 0))
                .toList();

        return new InventarioSemillas(
                semilla == null || semilla.isBlank() ? "Todas" : semilla.trim(),
                seeds.size(),
                result);
    }

    @Tool(description = """
            Busca clientes activos por nombre o parte del nombre y devuelve teléfono, correo y
            roles. Si no se envía nombre, lista los clientes. Úsala para preguntas como
            "¿hay algún cliente llamado Raúl?" o "¿qué clientes tenemos?".
            """)
    public ClientesEncontrados buscarClientes(
            @ToolParam(description = "Nombre o parte del nombre del cliente. Opcional.",
                    required = false) String nombre) {
        tracker.record("clientes");
        String query = searchQuery(nombre);

        List<Contact> clients = contactService.getAllContacts(ContactRole.CLIENT, false).stream()
                .filter(contact -> query.isEmpty() || matchesSearch(contact.getName(), query))
                .sorted(Comparator.comparing(Contact::getName, String.CASE_INSENSITIVE_ORDER))
                .toList();

        List<ClienteDetalle> result = clients.stream()
                .limit(maxRows)
                .map(contact -> new ClienteDetalle(
                        contact.getId(),
                        contact.getName(),
                        contact.getPhone(),
                        contact.getEmail(),
                        contact.getRoles() == null ? "" : contact.getRoles().stream()
                                .map(Enum::name)
                                .sorted()
                                .collect(Collectors.joining(", "))))
                .toList();

        return new ClientesEncontrados(
                nombre == null || nombre.isBlank() ? "Todos" : nombre.trim(),
                clients.size(),
                result);
    }

    @Tool(description = """
            Devuelve un resumen operativo compacto del vivero: siembras activas, plántulas
            disponibles, reservas vigentes, ventas de los últimos 30 días, semillas con stock
            bajo y lotes próximos a vencer. Úsala para preguntas generales como "¿cómo va el vivero?".
            """)
    public ResumenOperativo resumenOperativo() {
        tracker.record("resumen");
        Snapshot snapshot = snapshot();
        List<Sowing> active = activeSowings(snapshot);
        int available = active.stream()
                .mapToInt(sowing -> safeInt(saleService.getAvailabilityDetail(sowing.getId()).available()))
                .sum();

        LocalDate from = LocalDate.now().minusDays(30);
        List<Sale> sales = activeSales(from, LocalDate.now());
        int lowStock = (int) seedService.getAllSeeds().stream()
                .filter(seed -> seed.getTotalAvailable() != null && seed.getTotalAvailable() < 100)
                .count();

        LocalDate today = LocalDate.now();
        LocalDate limit = today.plusDays(60);
        int lotsDue = (int) inventoryService.getAllLots().stream()
                .filter(lot -> lot.getStatus() == SeedLotStatus.AVAILABLE)
                .filter(lot -> lot.getDueDate() != null)
                .filter(lot -> !lot.getDueDate().isBefore(today)
                        && !lot.getDueDate().isAfter(limit))
                .count();

        return new ResumenOperativo(
                today.format(DATE),
                active.size(),
                available,
                liveReservations().size(),
                sales.size(),
                sales.stream().mapToInt(sale -> safeInt(sale.getQuantity())).sum(),
                totalOf(sales),
                lowStock,
                lotsDue);
    }

    private Snapshot snapshot() {
        Map<Integer, SeedLot> lots = new HashMap<>();
        inventoryService.getAllLots().forEach(lot -> lots.put(lot.getId(), lot));

        Map<Integer, Seed> seeds = new HashMap<>();
        seedService.getAllSeeds().forEach(seed -> seeds.put(seed.getId(), seed));

        Map<Integer, Sowing> sowings = new HashMap<>();
        sowingService.getAllSowings().forEach(sowing -> sowings.put(sowing.getId(), sowing));

        Map<Integer, Bed> beds = new HashMap<>();
        bedService.getAllBeds().forEach(bed -> beds.put(bed.getId(), bed));

        Map<Integer, Contact> contacts = new HashMap<>();
        contactService.getAllContacts(null, true).forEach(contact -> contacts.put(contact.getId(), contact));

        return new Snapshot(lots, seeds, sowings, beds, contacts);
    }

    private List<Sowing> activeSowings(Snapshot snapshot) {
        return snapshot.sowings().values().stream()
                .filter(sowing -> Boolean.TRUE.equals(sowing.getActive()))
                .filter(sowing -> ACTIVE_SOWING_STATUSES.contains(sowing.getStatus()))
                .sorted(Comparator.comparing(Sowing::getId))
                .toList();
    }

    private List<Reservation> liveReservations() {
        return reservationService.findReservations(null, null, null).stream()
                .filter(reservation -> Boolean.TRUE.equals(reservation.getActive()))
                .filter(reservation -> LIVE_RESERVATION_STATUSES.contains(
                        reservation.getReservationStatus()))
                .toList();
    }

    private List<Sale> activeSales(LocalDate from, LocalDate to) {
        return saleService.findSales(null, null, from, to).stream()
                .filter(sale -> Boolean.TRUE.equals(sale.getActive()))
                .toList();
    }

    private Seed seedForSowing(Sowing sowing, Snapshot snapshot) {
        if (sowing == null) {
            return null;
        }
        SeedLot lot = snapshot.lots().get(sowing.getLotId());
        return lot != null ? snapshot.seeds().get(lot.getSeedId()) : null;
    }

    private String seedNameForSale(Sale sale, Snapshot snapshot) {
        Sowing sowing = snapshot.sowings().get(sale.getSowingId());
        Seed seed = seedForSowing(sowing, snapshot);
        return seed != null ? seed.getName() : "Semilla desconocida";
    }

    private BigDecimal totalOf(List<Sale> sales) {
        return sales.stream()
                .map(Sale::getTotal)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private int safeInt(Integer value) {
        return value != null ? value : 0;
    }

    private String normalize(String value) {
        if (value == null) {
            return "";
        }
        return Normalizer.normalize(value, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase(Locale.ROOT)
                .trim();
    }

    private String searchQuery(String value) {
        String normalized = normalize(value);
        if (normalized.isEmpty()) {
            return "";
        }
        return Arrays.stream(normalized.split("\\s+"))
                .filter(token -> token.length() > 2 && !SEARCH_STOPWORDS.contains(token))
                .collect(Collectors.joining(" "));
    }

    private boolean matchesSearch(String text, String query) {
        String normalizedText = normalize(text);
        if (normalizedText.contains(query)) {
            return true;
        }
        return Arrays.stream(query.split("\\s+"))
                .filter(token -> token.length() > 2)
                .anyMatch(normalizedText::contains);
    }

    private record Snapshot(
            Map<Integer, SeedLot> lots,
            Map<Integer, Seed> seeds,
            Map<Integer, Sowing> sowings,
            Map<Integer, Bed> beds,
            Map<Integer, Contact> contacts) {
    }

    public record DisponibilidadPlantulas(
            String consulta,
            int totalPlantulasDisponibles,
            List<DisponibilidadPorSemilla> semillas) {
    }

    public record DisponibilidadPorSemilla(
            String semilla,
            int plantulasDisponibles,
            long semillasEnInventario,
            List<DetalleDisponibilidad> siembras) {
    }

    public record DetalleDisponibilidad(
            int siembraId,
            String cama,
            String estado,
            Integer plantulasAptasUltimaVisita,
            int vendidas,
            int disponibles) {
    }

    public record ReservasVigentes(int total, List<ReservaDetalle> reservas) {
    }

    public record ReservaDetalle(
            int id,
            String fecha,
            String cliente,
            int siembraId,
            String semilla,
            String cama,
            String estado,
            BigDecimal total) {
    }

    public record SiembrasActivas(int total, List<SiembraDetalle> siembras) {
    }

    public record SiembraDetalle(
            int id,
            String semilla,
            String cama,
            String estado,
            String fechaSiembra,
            int cantidadSembrada,
            Integer plantulasAptas,
            int vendidas,
            int disponibles) {
    }

    public record ResumenVentas(
            String desde,
            String hasta,
            int ventas,
            int plantulasVendidas,
            BigDecimal total,
            List<VentaPorSemilla> porSemilla) {
    }

    public record VentaPorSemilla(
            String semilla,
            int plantulasVendidas,
            BigDecimal total) {
    }

    public record LotesPorVencer(int total, int diasConsultados, List<LoteDetalle> lotes) {
    }

    public record LoteDetalle(
            int loteId,
            Integer numeroLote,
            String semilla,
            int disponibles,
            String vence,
            long diasRestantes) {
    }

    public record StockBajo(int umbral, int total, List<StockSemilla> semillas) {
    }

    public record StockSemilla(String semilla, long disponibles, long faltanteParaUmbral) {
    }

    public record InventarioSemillas(
            String consulta,
            int total,
            List<SemillaInventario> semillas) {
    }

    public record SemillaInventario(
            Integer id,
            String semilla,
            String tipo,
            long stockSemillas) {
    }

    public record ClientesEncontrados(
            String consulta,
            int total,
            List<ClienteDetalle> clientes) {
    }

    public record ClienteDetalle(
            Integer id,
            String cliente,
            String telefono,
            String correo,
            String roles) {
    }

    public record ResumenOperativo(
            String fecha,
            int siembrasActivas,
            int plantulasDisponibles,
            int reservasVigentes,
            int ventasUltimos30Dias,
            int plantulasVendidasUltimos30Dias,
            BigDecimal totalVentasUltimos30Dias,
            int semillasStockBajo,
            int lotesPorVencer60Dias) {
    }
}
