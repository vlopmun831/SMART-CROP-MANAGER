-- =========================
-- SCRIPT DE INICIALIZACIÓN 
-- ==========================
SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE alerta;
TRUNCATE TABLE riego;
TRUNCATE TABLE registro;
TRUNCATE TABLE zona_cultivo;
TRUNCATE TABLE usuario;
SET FOREIGN_KEY_CHECKS = 1;

	-- 1. INSERTAR USUARIOS (Contraseña de todos encriptada con BCrypt: 'admin1234' y '1234')
	INSERT INTO usuario (id, nombre, email, password, rol) VALUES
	(1, 'Vanesa Directora', 'admin@smartcrop.com', '$2a$10$epMlyz1Y5tI0V1G5SXZ5H.5vEaQhM/T1O6J9Q2Nq6rL4W2W5Q1k2e', 'ADMIN'),
	(2, 'Carlos Agrónomo', 'carlos@smartcrop.com', '$2a$10$epMlyz1Y5tI0V1G5SXZ5H.5vEaQhM/T1O6J9Q2Nq6rL4W2W5Q1k2e', 'USUARIO'),
	(3, 'Laura Operaria', 'laura@smartcrop.com', '$2a$10$epMlyz1Y5tI0V1G5SXZ5H.5vEaQhM/T1O6J9Q2Nq6rL4W2W5Q1k2e', 'USUARIO'),
	(4, 'Pedro Viticultor', 'pedro@smartcrop.com', '$2a$10$epMlyz1Y5tI0V1G5SXZ5H.5vEaQhM/T1O6J9Q2Nq6rL4W2W5Q1k2e', 'USUARIO');
	
	
	-- 2. INSERTAR ZONAS DE CULTIVO
	INSERT INTO zona_cultivo (id, var_cultivo, ubicacion, hum_suelo_min_config, hum_suelo_max_config, temp_max_config, id_usuario) VALUES
	(1, 'TOMATE', 'Invernadero A - Sector Norte', 45.0, 85.0, 32.0, 2),
	(2, 'OLIVO', 'Finca Olivar Sur', NULL, NULL, NULL, 3), 
	(3, 'VID', 'Parcela Ladera Oeste', 35.0, 75.0, 38.0, 4),
	(4, 'ALMENDRO', 'Sector Secano Este', 25.0, 70.0, 40.0, 2);
	
	
	-- 3. INSERTAR REGISTROS HISTÓRICOS DE SENSORES
	INSERT INTO registro (id, fecha, temperatura, humedad_suelo, lluvia, id_zona) VALUES
	-- Zona 1: Tomate (Carlos) -> Estado actual: Seco (Provoca Alerta y Riego Activo)
	(1, '2026-05-15 09:00:00', 22.4, 60.0, 0, 1),
	(2, '2026-05-15 15:00:00', 31.2, 52.5, 0, 1),
	(3, '2026-05-16 10:00:00', 26.8, 41.0, 0, 1), 
	
	-- Zona 2: Olivo (Laura) -> Estado actual: Ola de Calor Extremo
	(4, '2026-05-15 12:00:00', 35.0, 40.0, 0, 2),
	(5, '2026-05-16 14:30:00', 46.2, 35.0, 0, 2), 
	
	-- Zona 3: Vid (Pedro) -> Estado actual: Óptimo Recuperado (Historial cerrado)
	(6, '2026-05-15 08:30:00', 20.1, 30.0, 0, 3), 
	(7, '2026-05-16 09:15:00', 24.5, 78.0, 0, 3), 
	
	-- Zona 4: Almendro (Carlos) -> Estado actual: Registro de pico térmico previo
	(8, '2026-05-15 16:00:00', 43.5, 30.0, 0, 4), -- Provocó alerta por exceso de temperatura (Límite: 40)
	(9, '2026-05-16 11:00:00', 28.0, 28.0, 0, 4);
	
	
	-- 4. INSERTAR HISTORIAL DE RIEGOS SIMULADOS
	INSERT INTO riego (id, fecha, hora_inicio, hora_fin, id_zona) VALUES
	(1, '2026-05-16', '2026-05-16 10:00:00', NULL, 1),
	(2, '2026-05-15', '2026-05-15 08:30:00', '2026-05-15 09:45:00', 3);
	
	
	-- 5. INSERTAR ALERTAS (PENDIENTES, RESUELTAS E IGNORADAS)
	INSERT INTO alerta (id, tipo_alerta, descripcion, min, max, estado, fecha, id_zona) VALUES
	-- Alerta pendiente en el panel de Carlos (Zona 1)
	(1, 'SUELO_SECO', 'Humedad crítica bajo el límite: 41.0% (Mín configurado: 45.0%)', 45.0, 85.0, 'PENDIENTE', '2026-05-16 10:00:00', 1),
	
	-- Alerta pendiente en el panel de Laura (Zona 2)
	(2, 'CALOR_EXTREMO', 'Temperatura ambiental excesiva en olivar: 46.2°C (Máx: 45.0°C)', 20.0, 45.0, 'PENDIENTE', '2026-05-16 14:30:00', 2),
	
	-- Alerta histórica resuelta automáticamente por tu lógica en el panel de Pedro (Zona 3)
	(3, 'SUELO_SECO', 'Estrés hídrico detectado: 30.0%', 35.0, 75.0, 'RESUELTA', '2026-05-15 08:30:00', 3),
	
	-- ⚡ NUEVA ALERTA IGNORADA MANUALMENTE: Panel de Carlos (Zona 4 - Almendro)
	(4, 'CALOR_EXTREMO', 'Aviso térmico descartado por el operario: 43.5°C (Máx configurado: 40.0°C)', 0.0, 40.0, 'IGNORADA', '2026-05-15 16:00:00', 4);