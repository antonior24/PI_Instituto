-- INSERTS DE EJEMPLO

-- Usuarios
INSERT INTO Usuario (nombre, correo, contraseña, rol) VALUES
('Carlos Gómez', 'carlos@example.com', 'pass123', 'profesor'),
('Lucía Herrera', 'lucia@example.com', 'abc456', 'profesor'),
('Pedro López', 'pedro@example.com', 'pedro123', 'administrador'),
('Sara Núñez', 'sara@example.com', 'sara456', 'profesor'),
('Eva Díaz', 'eva@example.com', 'eva789', 'profesor');

-- Franjas
INSERT INTO Franja (hora_inicio, hora_fin) VALUES
('10:00:00', '11:00:00'),
('11:00:00', '12:00:00'),
('12:00:00', '13:00:00'),
('13:00:00', '14:00:00');

-- Horarios
INSERT INTO Horario (id_asignatura, id_curso, id_aula, id_profesor, dia, franja) VALUES
(3, 3, 3, 3, 'X', 3),
(4, 2, 2, 4, 'J', 2),
(5, 1, 1, 5, 'V', 1),
(2, 2, 2, 6, 'M', 4),
(1, 4, 4, 7, 'L', 2),
(1, 3, 1, 1, 'L', 1),
(2, 1, 2, 2, 'M', 2),
(3, 2, 3, 3, 'X', 3),
(4, 4, 4, 4, 'J', 4),
(5, 3, 1, 5, 'V', 1);

-- Ausencias
INSERT INTO Ausencia (descripcion, id_horario) VALUES
('Falta por reunión', 3),
('Permiso médico', 5),
('Viaje institucional', 6),
('Falta sin justificar', 7),
('Retraso por tráfico', 9);
