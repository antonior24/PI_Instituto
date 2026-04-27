CREATE TABLE Usuario (
    id_usuario INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    correo VARCHAR(100) NOT NULL,
    contraseña VARCHAR(100) NOT NULL,
    rol VARCHAR(50) NOT NULL,
    tiene_imagen BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE usuario_imagen (
    id_usuario INT PRIMARY KEY,
    mime_type VARCHAR(100) NOT NULL,
    datos LONGBLOB NOT NULL,
    FOREIGN KEY (id_usuario) REFERENCES Usuario(id_usuario) ON DELETE CASCADE
);

-- Tabla Profesor
CREATE TABLE Profesor (
    id_profesor INT AUTO_INCREMENT PRIMARY KEY,
    id_usuario INT NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    FOREIGN KEY (id_usuario) REFERENCES Usuario(id_usuario)
);

-- Tabla Asignatura
CREATE TABLE Asignatura (
    id_asignatura INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL
);

-- Tabla Curso
CREATE TABLE Curso (
    id_curso INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL
);

-- Tabla Aula
CREATE TABLE Aula (
    id_aula INT AUTO_INCREMENT PRIMARY KEY,
    codigo VARCHAR(100) NOT NULL
);

-- Tabla Franja
CREATE TABLE Franja (
    id_franja INT AUTO_INCREMENT PRIMARY KEY,
    hora_inicio TIME NOT NULL,
    hora_fin TIME NOT NULL
);

-- Tabla Horario
CREATE TABLE Horario (
    id INT AUTO_INCREMENT PRIMARY KEY,
    id_asignatura INT NOT NULL,
    id_curso INT NULL,
    id_aula INT NULL,
    id_profesor INT NOT NULL,
    dia VARCHAR(20) NOT NULL,
    franja INT NOT NULL,
    FOREIGN KEY (id_asignatura) REFERENCES Asignatura(id_asignatura),
    FOREIGN KEY (id_curso) REFERENCES Curso(id_curso),
    FOREIGN KEY (id_aula) REFERENCES Aula(id_aula),
    FOREIGN KEY (id_profesor) REFERENCES Profesor(id_profesor),
    FOREIGN KEY (franja) REFERENCES Franja(id_franja)
);

-- Tabla Ausencia
CREATE TABLE Ausencia (
    id INT AUTO_INCREMENT PRIMARY KEY,
    descripcion TEXT,
    id_horario INT NOT NULL,
    fecha DATE,
    justificada BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (id_horario) REFERENCES Horario(id)
);

-- Tabla Guardia (Guard Duty)
CREATE TABLE Guardia (
    id INT AUTO_INCREMENT PRIMARY KEY,
    id_profesor INT NOT NULL,
    id_horario_cobertura INT NOT NULL,
    fecha DATE NOT NULL,
    puntos INT NOT NULL DEFAULT 0,
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_profesor) REFERENCES Profesor(id_profesor),
    FOREIGN KEY (id_horario_cobertura) REFERENCES Horario(id)
);
