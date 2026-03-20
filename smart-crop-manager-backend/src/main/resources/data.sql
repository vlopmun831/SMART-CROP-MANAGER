INSERT INTO usuario (nombre, email, password, rol) VALUES 
('Juan Agricultor', 'juan@campo.com', '$2a$10$vI8p7MslZpAgH.n.5K0Yre8LhPz8G90NTo09q43A.N.M4W91.N.mS', 'USUARIO'),
('Maria Hortelana', 'maria@huerta.com', '$2a$10$vI8p7MslZpAgH.n.5K0Yre8LhPz8G90NTo09q43A.N.M4W91.N.mS', 'USUARIO');

-- 2. ZONAS DE CULTIVO
-- Repartimos: Zona 1 para el Admin (id 1), Zonas 2 y 3 para Juan (id 2), Zona 4 para Maria (id 3)
INSERT INTO zona_cultivo (var_cultivo, ubicacion, hum_suelo_min_config, hum_suelo_max_config, temp_max_config, id_usuario) VALUES 
('VARIEDAD_1', 'Invernadero Principal', 30.0, 80.0, 35.0, 1),
('VARIEDAD_2', 'Sector Olivos Sur', 15.0, 90.0, 45.0, 2),
('VARIEDAD_3', 'Huerta Tomates', 50.0, 95.0, 28.0, 2),
('VARIEDAD_1', 'Parcela Exterior', 30.0, 80.0, 35.0, 3);

-- 3. REGISTROS (Historial de sensores)
-- Metemos un par de lecturas para la zona del admin y las de los usuarios
INSERT INTO registro (fecha, temperatura, humedad_suelo, humedad_aire, lluvia, id_zona) VALUES 
(NOW(), 24.5, 45.0, 60.0, 0, 1),
(DATE_SUB(NOW(), INTERVAL 2 HOUR), 22.1, 48.2, 65.0, 0, 1),
(NOW(), 18.0, 70.0, 80.0, 1, 2),
(NOW(), 30.5, 25.0, 40.0, 0, 3);

-- 4. RIEGOS
-- Un riego finalizado y uno activo (sin hora_fin)
INSERT INTO riego (fecha, hora_inicio, hora_fin, id_zona) VALUES 
(CURDATE(), DATE_SUB(NOW(), INTERVAL 3 HOUR), DATE_SUB(NOW(), INTERVAL 2 HOUR), 1),
(CURDATE(), NOW(), NULL, 3);

-- 5. ALERTAS
-- Diferentes estados y tipos basados en tus Enums
INSERT INTO alerta (tipo_alerta, descripcion, max, min, estado, fecha, id_zona) VALUES 
('SUELO_SECO', 'Humedad crítica en tomates', 80.0, 30.0, 'PENDIENTE', NOW(), 3),
('CALOR_EXTREMO', 'Aviso de temperatura alta', 35.0, 10.0, 'RESUELTA', DATE_SUB(NOW(), INTERVAL 1 DAY), 1),
('FALLO_SENSOR', 'Sensor de humedad no responde', NULL, NULL, 'IGNORADA', NOW(), 2);