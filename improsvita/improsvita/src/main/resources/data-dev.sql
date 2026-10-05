-- =====================================================================
-- Improsvita :: dev seed data (IDEMPOTENTE)
-- Se ejecuta en cada arranque (data-dev.sql solo perfil dev).
-- Cada INSERT con ID fijo tiene doble guarda (PK ocupada O llave
-- natural existente): respeta datos preexistentes y reiniciar NO duplica.
-- users/tokens fuera: el admin lo crea AdminInitializer.
-- =====================================================================

-- ---------------------------------------------------------------------
-- 1. SUPPLIERS
-- ---------------------------------------------------------------------
INSERT INTO suppliers (supplier_id, name, phone, email, created_date, last_updated, active)
SELECT 1, 'Semillas del Valle', '3101234567', 'contacto@semillasvalle.com', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, true
WHERE NOT EXISTS (SELECT 1 FROM suppliers WHERE supplier_id = 1 OR email = 'contacto@semillasvalle.com');

INSERT INTO suppliers (supplier_id, name, phone, email, created_date, last_updated, active)
SELECT 2, 'AgroSeed Colombia', '3119876543', 'ventas@agroseed.co', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, true
WHERE NOT EXISTS (SELECT 1 FROM suppliers WHERE supplier_id = 2 OR email = 'ventas@agroseed.co');

-- ---------------------------------------------------------------------
-- 2. SEEDS 1-10
-- ---------------------------------------------------------------------
INSERT INTO seeds (seed_id, name, seed_type, description, created_date, last_updated, active)
SELECT 1, 'Frijol Modificado', 'MODIFIED', 'Semilla de frijol modificada para mejorar su resistencia y rendimiento.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, true
WHERE NOT EXISTS (SELECT 1 FROM seeds WHERE seed_id = 1 OR name = 'Frijol Modificado');

INSERT INTO seeds (seed_id, name, seed_type, description, created_date, last_updated, active)
SELECT 2, 'Maíz Híbrido', 'HYBRID', 'Semilla de maíz híbrido seleccionada para obtener un buen rendimiento agrícola.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, true
WHERE NOT EXISTS (SELECT 1 FROM seeds WHERE seed_id = 2 OR name = 'Maíz Híbrido');

INSERT INTO seeds (seed_id, name, seed_type, description, created_date, last_updated, active)
SELECT 3, 'Arroz Tradicional', 'TRADITIONAL', 'Variedad tradicional de arroz utilizada para cultivos de producción alimentaria.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, true
WHERE NOT EXISTS (SELECT 1 FROM seeds WHERE seed_id = 3 OR name = 'Arroz Tradicional');

INSERT INTO seeds (seed_id, name, seed_type, description, created_date, last_updated, active)
SELECT 4, 'Tomate Híbrido', 'HYBRID', 'Semilla de tomate híbrido con características orientadas a una producción uniforme.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, true
WHERE NOT EXISTS (SELECT 1 FROM seeds WHERE seed_id = 4 OR name = 'Tomate Híbrido');

INSERT INTO seeds (seed_id, name, seed_type, description, created_date, last_updated, active)
SELECT 5, 'Papa Modificada', 'MODIFIED', 'Semilla de papa modificada para mejorar determinadas características de cultivo.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, true
WHERE NOT EXISTS (SELECT 1 FROM seeds WHERE seed_id = 5 OR name = 'Papa Modificada');

INSERT INTO seeds (seed_id, name, seed_type, description, created_date, last_updated, active)
SELECT 6, 'Lechuga Tradicional', 'TRADITIONAL', 'Variedad tradicional de lechuga destinada a cultivos de producción hortícola.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, true
WHERE NOT EXISTS (SELECT 1 FROM seeds WHERE seed_id = 6 OR name = 'Lechuga Tradicional');

INSERT INTO seeds (seed_id, name, seed_type, description, created_date, last_updated, active)
SELECT 7, 'Zanahoria Híbrida', 'HYBRID', 'Semilla de zanahoria híbrida seleccionada para favorecer un crecimiento uniforme.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, true
WHERE NOT EXISTS (SELECT 1 FROM seeds WHERE seed_id = 7 OR name = 'Zanahoria Híbrida');

INSERT INTO seeds (seed_id, name, seed_type, description, created_date, last_updated, active)
SELECT 8, 'Soya Modificada', 'MODIFIED', 'Semilla de soya modificada con características orientadas a mejorar su producción.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, true
WHERE NOT EXISTS (SELECT 1 FROM seeds WHERE seed_id = 8 OR name = 'Soya Modificada');

INSERT INTO seeds (seed_id, name, seed_type, description, created_date, last_updated, active)
SELECT 9, 'Cebolla Tradicional', 'TRADITIONAL', 'Variedad tradicional de cebolla utilizada en cultivos agrícolas.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, true
WHERE NOT EXISTS (SELECT 1 FROM seeds WHERE seed_id = 9 OR name = 'Cebolla Tradicional');

INSERT INTO seeds (seed_id, name, seed_type, description, created_date, last_updated, active)
SELECT 10, 'Pepino Híbrido', 'HYBRID', 'Semilla de pepino híbrido seleccionada para obtener plantas de desarrollo uniforme.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, true
WHERE NOT EXISTS (SELECT 1 FROM seeds WHERE seed_id = 10 OR name = 'Pepino Híbrido');

-- ---------------------------------------------------------------------
-- 3. LOCATIONS
-- ---------------------------------------------------------------------
INSERT INTO locations (location_id, location_name, active, created_at, last_updated)
SELECT 1, 'BODEGA-01', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM locations WHERE location_id = 1 OR location_name = 'BODEGA-01');

-- ---------------------------------------------------------------------
-- 4. SEEDS_SUPPLIERS (guarda: unique(seed_id, supplier_id))
--    seeds 1-5 -> supplier 1 | seeds 6-10 -> supplier 2
-- ---------------------------------------------------------------------
INSERT INTO seeds_suppliers (seed_id, supplier_id, created_date, active)
SELECT 1, 1, CURRENT_TIMESTAMP, true WHERE NOT EXISTS (SELECT 1 FROM seeds_suppliers WHERE seed_id = 1 AND supplier_id = 1);
INSERT INTO seeds_suppliers (seed_id, supplier_id, created_date, active)
SELECT 2, 1, CURRENT_TIMESTAMP, true WHERE NOT EXISTS (SELECT 1 FROM seeds_suppliers WHERE seed_id = 2 AND supplier_id = 1);
INSERT INTO seeds_suppliers (seed_id, supplier_id, created_date, active)
SELECT 3, 1, CURRENT_TIMESTAMP, true WHERE NOT EXISTS (SELECT 1 FROM seeds_suppliers WHERE seed_id = 3 AND supplier_id = 1);
INSERT INTO seeds_suppliers (seed_id, supplier_id, created_date, active)
SELECT 4, 1, CURRENT_TIMESTAMP, true WHERE NOT EXISTS (SELECT 1 FROM seeds_suppliers WHERE seed_id = 4 AND supplier_id = 1);
INSERT INTO seeds_suppliers (seed_id, supplier_id, created_date, active)
SELECT 5, 1, CURRENT_TIMESTAMP, true WHERE NOT EXISTS (SELECT 1 FROM seeds_suppliers WHERE seed_id = 5 AND supplier_id = 1);
INSERT INTO seeds_suppliers (seed_id, supplier_id, created_date, active)
SELECT 6, 2, CURRENT_TIMESTAMP, true WHERE NOT EXISTS (SELECT 1 FROM seeds_suppliers WHERE seed_id = 6 AND supplier_id = 2);
INSERT INTO seeds_suppliers (seed_id, supplier_id, created_date, active)
SELECT 7, 2, CURRENT_TIMESTAMP, true WHERE NOT EXISTS (SELECT 1 FROM seeds_suppliers WHERE seed_id = 7 AND supplier_id = 2);
INSERT INTO seeds_suppliers (seed_id, supplier_id, created_date, active)
SELECT 8, 2, CURRENT_TIMESTAMP, true WHERE NOT EXISTS (SELECT 1 FROM seeds_suppliers WHERE seed_id = 8 AND supplier_id = 2);
INSERT INTO seeds_suppliers (seed_id, supplier_id, created_date, active)
SELECT 9, 2, CURRENT_TIMESTAMP, true WHERE NOT EXISTS (SELECT 1 FROM seeds_suppliers WHERE seed_id = 9 AND supplier_id = 2);
INSERT INTO seeds_suppliers (seed_id, supplier_id, created_date, active)
SELECT 10, 2, CURRENT_TIMESTAMP, true WHERE NOT EXISTS (SELECT 1 FROM seeds_suppliers WHERE seed_id = 10 AND supplier_id = 2);

-- ---------------------------------------------------------------------
-- 5. SEED_LOTS
--    available ya neto de los movimientos extra (lote 1: 380, lote 4: 0
--    DEPLETED, lote 2: 700)
-- ---------------------------------------------------------------------
INSERT INTO seed_lots (lot_id, lot_number, seed_id, location_id, entry_date, due_date, initial_quantity, available_quantity, status)
SELECT 1, 1001, 1, 1, DATE '2026-09-01', DATE '2028-09-01', 500, 380, 'AVAILABLE' WHERE NOT EXISTS (SELECT 1 FROM seed_lots WHERE lot_id = 1 OR lot_number = 1001);
INSERT INTO seed_lots (lot_id, lot_number, seed_id, location_id, entry_date, due_date, initial_quantity, available_quantity, status)
SELECT 2, 1002, 2, 1, DATE '2026-09-02', DATE '2028-09-02', 750, 700, 'AVAILABLE' WHERE NOT EXISTS (SELECT 1 FROM seed_lots WHERE lot_id = 2 OR lot_number = 1002);
INSERT INTO seed_lots (lot_id, lot_number, seed_id, location_id, entry_date, due_date, initial_quantity, available_quantity, status)
SELECT 3, 1003, 3, 1, DATE '2026-09-03', DATE '2028-09-03', 600, 600, 'AVAILABLE' WHERE NOT EXISTS (SELECT 1 FROM seed_lots WHERE lot_id = 3 OR lot_number = 1003);
INSERT INTO seed_lots (lot_id, lot_number, seed_id, location_id, entry_date, due_date, initial_quantity, available_quantity, status)
SELECT 4, 1004, 4, 1, DATE '2026-09-04', DATE '2028-03-04', 450, 0, 'DEPLETED' WHERE NOT EXISTS (SELECT 1 FROM seed_lots WHERE lot_id = 4 OR lot_number = 1004);
INSERT INTO seed_lots (lot_id, lot_number, seed_id, location_id, entry_date, due_date, initial_quantity, available_quantity, status)
SELECT 5, 1005, 5, 1, DATE '2026-09-05', DATE '2028-09-05', 800, 800, 'AVAILABLE' WHERE NOT EXISTS (SELECT 1 FROM seed_lots WHERE lot_id = 5 OR lot_number = 1005);
INSERT INTO seed_lots (lot_id, lot_number, seed_id, location_id, entry_date, due_date, initial_quantity, available_quantity, status)
SELECT 6, 1006, 6, 1, DATE '2026-09-06', DATE '2027-09-06', 350, 350, 'AVAILABLE' WHERE NOT EXISTS (SELECT 1 FROM seed_lots WHERE lot_id = 6 OR lot_number = 1006);
INSERT INTO seed_lots (lot_id, lot_number, seed_id, location_id, entry_date, due_date, initial_quantity, available_quantity, status)
SELECT 7, 1007, 7, 1, DATE '2026-09-07', DATE '2028-09-07', 550, 550, 'AVAILABLE' WHERE NOT EXISTS (SELECT 1 FROM seed_lots WHERE lot_id = 7 OR lot_number = 1007);
INSERT INTO seed_lots (lot_id, lot_number, seed_id, location_id, entry_date, due_date, initial_quantity, available_quantity, status)
SELECT 8, 1008, 8, 1, DATE '2026-09-08', DATE '2028-09-08', 700, 700, 'AVAILABLE' WHERE NOT EXISTS (SELECT 1 FROM seed_lots WHERE lot_id = 8 OR lot_number = 1008);
INSERT INTO seed_lots (lot_id, lot_number, seed_id, location_id, entry_date, due_date, initial_quantity, available_quantity, status)
SELECT 9, 1009, 9, 1, DATE '2026-09-09', DATE '2027-09-09', 400, 400, 'AVAILABLE' WHERE NOT EXISTS (SELECT 1 FROM seed_lots WHERE lot_id = 9 OR lot_number = 1009);
INSERT INTO seed_lots (lot_id, lot_number, seed_id, location_id, entry_date, due_date, initial_quantity, available_quantity, status)
SELECT 10, 1010, 10, 1, DATE '2026-09-10', DATE '2028-09-10', 650, 650, 'AVAILABLE' WHERE NOT EXISTS (SELECT 1 FROM seed_lots WHERE lot_id = 10 OR lot_number = 1010);

-- ---------------------------------------------------------------------
-- 6. SEED_MOVEMENT: 10 ENTRY (guarda: compuesto lot/type/qty/reason)
-- ---------------------------------------------------------------------
INSERT INTO seed_movement (supplier_id, lot_id, movement_type, quantity, movement_date, reason)
SELECT 1, 1, 'ENTRY', 500, TIMESTAMP '2026-09-01 08:00:00', 'Entrada lote 1001'
WHERE NOT EXISTS (SELECT 1 FROM seed_movement WHERE lot_id = 1 AND movement_type = 'ENTRY' AND quantity = 500 AND reason = 'Entrada lote 1001');
INSERT INTO seed_movement (supplier_id, lot_id, movement_type, quantity, movement_date, reason)
SELECT 1, 2, 'ENTRY', 750, TIMESTAMP '2026-09-02 08:00:00', 'Entrada lote 1002'
WHERE NOT EXISTS (SELECT 1 FROM seed_movement WHERE lot_id = 2 AND movement_type = 'ENTRY' AND quantity = 750 AND reason = 'Entrada lote 1002');
INSERT INTO seed_movement (supplier_id, lot_id, movement_type, quantity, movement_date, reason)
SELECT 1, 3, 'ENTRY', 600, TIMESTAMP '2026-09-03 08:00:00', 'Entrada lote 1003'
WHERE NOT EXISTS (SELECT 1 FROM seed_movement WHERE lot_id = 3 AND movement_type = 'ENTRY' AND quantity = 600 AND reason = 'Entrada lote 1003');
INSERT INTO seed_movement (supplier_id, lot_id, movement_type, quantity, movement_date, reason)
SELECT 1, 4, 'ENTRY', 450, TIMESTAMP '2026-09-04 08:00:00', 'Entrada lote 1004'
WHERE NOT EXISTS (SELECT 1 FROM seed_movement WHERE lot_id = 4 AND movement_type = 'ENTRY' AND quantity = 450 AND reason = 'Entrada lote 1004');
INSERT INTO seed_movement (supplier_id, lot_id, movement_type, quantity, movement_date, reason)
SELECT 1, 5, 'ENTRY', 800, TIMESTAMP '2026-09-05 08:00:00', 'Entrada lote 1005'
WHERE NOT EXISTS (SELECT 1 FROM seed_movement WHERE lot_id = 5 AND movement_type = 'ENTRY' AND quantity = 800 AND reason = 'Entrada lote 1005');
INSERT INTO seed_movement (supplier_id, lot_id, movement_type, quantity, movement_date, reason)
SELECT 1, 6, 'ENTRY', 350, TIMESTAMP '2026-09-06 08:00:00', 'Entrada lote 1006'
WHERE NOT EXISTS (SELECT 1 FROM seed_movement WHERE lot_id = 6 AND movement_type = 'ENTRY' AND quantity = 350 AND reason = 'Entrada lote 1006');
INSERT INTO seed_movement (supplier_id, lot_id, movement_type, quantity, movement_date, reason)
SELECT 1, 7, 'ENTRY', 550, TIMESTAMP '2026-09-07 08:00:00', 'Entrada lote 1007'
WHERE NOT EXISTS (SELECT 1 FROM seed_movement WHERE lot_id = 7 AND movement_type = 'ENTRY' AND quantity = 550 AND reason = 'Entrada lote 1007');
INSERT INTO seed_movement (supplier_id, lot_id, movement_type, quantity, movement_date, reason)
SELECT 1, 8, 'ENTRY', 700, TIMESTAMP '2026-09-08 08:00:00', 'Entrada lote 1008'
WHERE NOT EXISTS (SELECT 1 FROM seed_movement WHERE lot_id = 8 AND movement_type = 'ENTRY' AND quantity = 700 AND reason = 'Entrada lote 1008');
INSERT INTO seed_movement (supplier_id, lot_id, movement_type, quantity, movement_date, reason)
SELECT 1, 9, 'ENTRY', 400, TIMESTAMP '2026-09-09 08:00:00', 'Entrada lote 1009'
WHERE NOT EXISTS (SELECT 1 FROM seed_movement WHERE lot_id = 9 AND movement_type = 'ENTRY' AND quantity = 400 AND reason = 'Entrada lote 1009');
INSERT INTO seed_movement (supplier_id, lot_id, movement_type, quantity, movement_date, reason)
SELECT 1, 10, 'ENTRY', 650, TIMESTAMP '2026-09-10 08:00:00', 'Entrada lote 1010'
WHERE NOT EXISTS (SELECT 1 FROM seed_movement WHERE lot_id = 10 AND movement_type = 'ENTRY' AND quantity = 650 AND reason = 'Entrada lote 1010');

-- ---------------------------------------------------------------------
-- 7. Movimientos extra de prueba (lote 1 EXIT, lote 4 EXIT total,
--    lote 2 ADJUSTMENT). supplier NULL como los genera el servicio.
-- ---------------------------------------------------------------------
INSERT INTO seed_movement (supplier_id, lot_id, movement_type, quantity, movement_date, reason)
SELECT NULL, 1, 'EXIT', 120, TIMESTAMP '2026-09-15 10:00:00', 'Venta cliente La Finca'
WHERE NOT EXISTS (SELECT 1 FROM seed_movement WHERE lot_id = 1 AND movement_type = 'EXIT' AND quantity = 120 AND reason = 'Venta cliente La Finca');
INSERT INTO seed_movement (supplier_id, lot_id, movement_type, quantity, movement_date, reason)
SELECT NULL, 4, 'EXIT', 450, TIMESTAMP '2026-09-16 10:00:00', 'Venta total'
WHERE NOT EXISTS (SELECT 1 FROM seed_movement WHERE lot_id = 4 AND movement_type = 'EXIT' AND quantity = 450 AND reason = 'Venta total');
INSERT INTO seed_movement (supplier_id, lot_id, movement_type, quantity, movement_date, reason)
SELECT NULL, 2, 'ADJUSTMENT', -50, TIMESTAMP '2026-09-17 10:00:00', 'Merma por humedad'
WHERE NOT EXISTS (SELECT 1 FROM seed_movement WHERE lot_id = 2 AND movement_type = 'ADJUSTMENT' AND quantity = -50 AND reason = 'Merma por humedad');

-- ---------------------------------------------------------------------
-- 8. Avanzar secuencias IDENTITY tras inserts con ID fijo
-- ---------------------------------------------------------------------
SELECT setval(pg_get_serial_sequence('suppliers', 'supplier_id'), (SELECT MAX(supplier_id) FROM suppliers));
SELECT setval(pg_get_serial_sequence('seeds', 'seed_id'), (SELECT MAX(seed_id) FROM seeds));
SELECT setval(pg_get_serial_sequence('locations', 'location_id'), (SELECT MAX(location_id) FROM locations));
SELECT setval(pg_get_serial_sequence('seeds_suppliers', 'seed_provider_id'), (SELECT MAX(seed_provider_id) FROM seeds_suppliers));
SELECT setval(pg_get_serial_sequence('seed_lots', 'lot_id'), (SELECT MAX(lot_id) FROM seed_lots));
SELECT setval(pg_get_serial_sequence('seed_movement', 'transaction_id'), (SELECT MAX(transaction_id) FROM seed_movement));
