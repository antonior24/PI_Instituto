-- Insertar franjas horarias con IDs fijos del 1 al 14
INSERT INTO franja (id_franja, hora_inicio, hora_fin) VALUES (1, '08:15:00', '09:15:00') ON DUPLICATE KEY UPDATE hora_inicio=hora_inicio;
INSERT INTO franja (id_franja, hora_inicio, hora_fin) VALUES (2, '09:15:00', '10:15:00') ON DUPLICATE KEY UPDATE hora_inicio=hora_inicio;
INSERT INTO franja (id_franja, hora_inicio, hora_fin) VALUES (3, '10:15:00', '11:15:00') ON DUPLICATE KEY UPDATE hora_inicio=hora_inicio;
INSERT INTO franja (id_franja, hora_inicio, hora_fin) VALUES (4, '11:15:00', '11:45:00') ON DUPLICATE KEY UPDATE hora_inicio=hora_inicio;
INSERT INTO franja (id_franja, hora_inicio, hora_fin) VALUES (5, '11:45:00', '12:45:00') ON DUPLICATE KEY UPDATE hora_inicio=hora_inicio;
INSERT INTO franja (id_franja, hora_inicio, hora_fin) VALUES (6, '12:45:00', '13:45:00') ON DUPLICATE KEY UPDATE hora_inicio=hora_inicio;
INSERT INTO franja (id_franja, hora_inicio, hora_fin) VALUES (7, '13:45:00', '14:45:00') ON DUPLICATE KEY UPDATE hora_inicio=hora_inicio;
INSERT INTO franja (id_franja, hora_inicio, hora_fin) VALUES (8, '15:00:00', '16:00:00') ON DUPLICATE KEY UPDATE hora_inicio=hora_inicio;
INSERT INTO franja (id_franja, hora_inicio, hora_fin) VALUES (9, '16:00:00', '17:00:00') ON DUPLICATE KEY UPDATE hora_inicio=hora_inicio;
INSERT INTO franja (id_franja, hora_inicio, hora_fin) VALUES (10, '17:00:00', '18:00:00') ON DUPLICATE KEY UPDATE hora_inicio=hora_inicio;
INSERT INTO franja (id_franja, hora_inicio, hora_fin) VALUES (11, '18:00:00', '18:15:00') ON DUPLICATE KEY UPDATE hora_inicio=hora_inicio;
INSERT INTO franja (id_franja, hora_inicio, hora_fin) VALUES (12, '18:15:00', '19:15:00') ON DUPLICATE KEY UPDATE hora_inicio=hora_inicio;
INSERT INTO franja (id_franja, hora_inicio, hora_fin) VALUES (13, '19:15:00', '20:15:00') ON DUPLICATE KEY UPDATE hora_inicio=hora_inicio;
INSERT INTO franja (id_franja, hora_inicio, hora_fin) VALUES (14, '20:15:00', '21:15:00') ON DUPLICATE KEY UPDATE hora_inicio=hora_inicio;
