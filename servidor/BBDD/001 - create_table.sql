CREATE TABLE Usuario (
    id_usuario INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    correo VARCHAR(100) NOT NULL,
    contraseña VARCHAR(100) NOT NULL,
    rol VARCHAR(50) NOT NULL
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
    FOREIGN KEY (id_horario) REFERENCES Horario(id)
);