-- Script para vincular profesores con usuarios de prueba
-- IMPORTANTE: Ejecutar DESPUÉS de crear usuarios y profesores

-- Actualizar profesores con sus respectivos usuarios (primeros 5 profesores)
UPDATE Profesor SET id_usuario = 1 WHERE idProfesor = 1;  -- Carlos Gómez
UPDATE Profesor SET id_usuario = 2 WHERE idProfesor = 2;  -- Lucía Herrera
UPDATE Profesor SET id_usuario = 5 WHERE idProfesor = 3;  -- Sara Núñez
UPDATE Profesor SET id_usuario = 4 WHERE idProfesor = 4;  -- Pedro López
UPDATE Profesor SET id_usuario = 3 WHERE idProfesor = 5;  -- Eva Díaz

-- Insertar horarios de GUARDIA para los profesores de prueba
-- Carlos Gómez (profesor 1) tiene guardia todos los lunes de 8-9 AM
INSERT INTO Horario (id_asignatura, id_curso, id_aula, id_profesor, dia, franja) VALUES
(1, 64, 1, 1, 'L', 1);  -- Guardia A - Lunes - Franja 1 (10:00-11:00)

-- Lucía Herrera (profesor 2) tiene guardia todos los martes de 9-10 AM
INSERT INTO Horario (id_asignatura, id_curso, id_aula, id_profesor, dia, franja) VALUES
(1, 65, 1, 2, 'M', 2);  -- Guardia B - Martes - Franja 2 (11:00-12:00)

-- Sara Núñez (profesor 3) tiene guardia todos los miércoles recreo
INSERT INTO Horario (id_asignatura, id_curso, id_aula, id_profesor, dia, franja) VALUES
(1, 66, 1, 3, 'X', 3);  -- Guardia Biblioteca - Miércoles - Franja 3 (12:00-13:00)

-- Pedro López (profesor 4) tiene guardia recreo todos los jueves
INSERT INTO Horario (id_asignatura, id_curso, id_aula, id_profesor, dia, franja) VALUES
(1, 67, 1, 4, 'J', 2);  -- Guardia Recreo - Jueves - Franja 2 (11:00-12:00)

-- Eva Díaz (profesor 5) tiene guardia tarde todos los viernes
INSERT INTO Horario (id_asignatura, id_curso, id_aula, id_profesor, dia, franja) VALUES
(1, 69, 1, 5, 'V', 4);  -- Guardia Tarde - Viernes - Franja 4 (13:00-14:00)

-- Ahora todos los profesores tienen al menos un horario de guardia asignado
-- Al conectarse como carlos@example.com (Carlos Gómez), verá sus horarios de guardia
