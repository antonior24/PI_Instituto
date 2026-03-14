CREATE TABLE IF NOT EXISTS actividad_usuario (
    id INT AUTO_INCREMENT PRIMARY KEY,
    usuario VARCHAR(100),
    tipo VARCHAR(50),
    detalle TEXT,
    url VARCHAR(255),
    fecha DATETIME
);