# ESPAÑOL

# 📘 Proyecto Horarios - Backend (API)


## 📋 Descripción

Este documento describe los pasos para desplegar exclusivamente la parte del **Servidor (Backend y Base de Datos)** del Proyecto Horarios.

### 🏗️ Arquitectura de Contenedores

| Contenedor | Tecnología | Puertos Expuestos |
|------------|------------|-------------------|
| `horarios-back` | Java 21 + Maven + Spring Boot + MariaDB | 8080, 8081, 3306 |

---

## ✅ Requisitos Previos

### 1.1 Ubicación en Windows (PowerShell)

Abre PowerShell y navega a la carpeta raíz que contiene el directorio `servidor/`:

```powershell
# Cambia a la ruta donde tengas tu proyecto
cd "C:\Users\Aritz\proyecto\PI_Instituto"

# Verificación: Debe aparecer la carpeta 'servidor'
dir
```

### 1.2 Configuración CORS (Backend)

Para evitar errores de conexión entre el Front y el Back, edita el siguiente archivo en VS Code:

**Ruta:** `servidor/app-horario/src/main/java/com/ies/poligono/sur/app/horario/security/CorsConfig.java`

**Acción:** Añade el puerto 5147 a la lista de orígenes permitidos:

```java
config.setAllowedOrigins(List.of("http://localhost:5173", "http://172.17.0.2:5173", "http://localhost:5147"));
```

> 💾 **Nota:** Guarda el archivo (`Ctrl + S`) después de realizar el cambio.

---

## 🐳 Despliegue del Contenedor

### 2.1 Crear y ejecutar el contenedor

Ejecuta el siguiente comando en PowerShell para levantar el contenedor Ubuntu mapeando la carpeta actual:

```powershell
# Windows PowerShell
docker run -it --name horarios-back -p 8080:8080 -p 8081:8081 -p 5147:5147 -p 3306:3306 -v "${PWD}:/workspace" ubuntu:22.04 bash
```

> 🔄 **Si el contenedor ya existe**, usa:
```powershell
docker start -ai horarios-back
```

Una vez dentro, el prompt cambiará a `root@....`

---

## ⚙️ Instalación de Dependencias

### 3.1 Instalar Java 21, Maven y MariaDB

> ⚠️ **Nota:** Esta sección ignora Node.js (solo necesario para el Frontend).

Copia y pega el siguiente bloque de comandos en la terminal de Docker:

```bash
# Actualizar repositorios
apt update

# Instalar dependencias principales
apt install -y curl software-properties-common openjdk-21-jdk maven mariadb-server mariadb-client
```

> ⏱️ **Tiempo estimado:** 5-10 minutos dependiendo de tu conexión.

---

## 🗄️ Configuración de Base de Datos

> ⚠️ **Importante:** El servicio MariaDB debe iniciarse y configurarse **manualmente cada vez** que se crea el contenedor de cero.

### 4.1 Iniciar el servicio de MariaDB

```bash
# Iniciar el servicio
service mariadb start

# Verificar estado
service mariadb status
```

### 4.2 Crear Base de Datos y Usuario Administrador

```bash
# Crear BD, usuario y otorgar permisos
mariadb -u root -e "CREATE DATABASE IF NOT EXISTS \`app-horario\`; CREATE USER IF NOT EXISTS 'admin'@'%' IDENTIFIED BY 'admin'; GRANT ALL PRIVILEGES ON \`app-horario\`.* TO 'admin'@'%'; FLUSH PRIVILEGES;"
```

> ✅ **Verificación:** Puedes verificar la creación con:
```bash
mariadb -u root -e "SHOW DATABASES;"
```

---

## 🚀 Ejecución del Servidor (Backend)

### 5.1 Lanzar Spring Boot

Navega a la carpeta del proyecto y ejecuta el servidor:

```bash
# Navegar al directorio del backend
cd /workspace/servidor/app-horario

# Ejecutar Spring Boot
mvn spring-boot:run
```

> ⏳ **Esperar** hasta ver el mensaje:
> ```
> Started AppHorarioApplication in X.XXX seconds
> ```

> ⚠️ **No cierres esta terminal.** Déjala minimizada si necesitas liberar la sesión.

---

## 📖 Referencia Rápida

| Paso | Comando / Acción |
|------|-------------------|
| Crear contenedor | `docker run -it --name horarios-back -p 8080:8080 -p 8081:8081 -p 5147:5147 -p 3306:3306 -v "${PWD}:/workspace" ubuntu:22.04 bash` |
| Iniciar contenedor existente | `docker start -ai horarios-back` |
| Acceder a contenedor | `docker exec -it horarios-back bash` |
| Iniciar MariaDB | `service mariadb start` |
| Crear BD | `mariadb -u root -e "CREATE DATABASE IF NOT EXISTS \\\`app-horario\\\`; CREATE USER IF NOT EXISTS 'admin'@'%' IDENTIFIED BY 'admin'; GRANT ALL PRIVILEGES ON \\\`app-horario\\\`.* TO 'admin'@'%'; FLUSH PRIVILEGES;"` |
| Ejecutar backend | `cd /workspace/servidor/app-horario && mvn spring-boot:run` |

---


# ENGLISH

# 📘 Schedules Project - Backend (API)


## 📋 Description

This document describes the steps to exclusively deploy the **Server (Backend and Database)** part of the Schedules Project.

### 🏗️ Container Architecture

| Container | Technology | Exposed Ports |
|---------|------------|-------------------|
| `back-schedules` | Java 21 + Maven + Spring Boot + MariaDB | 8080, 8081, 3306 |

---

## ✅ Prerequisites

### 1.1 Location in Windows (PowerShell)

Open PowerShell and navigate to the root folder containing the `server/` directory:

``powershell
# Change to the path where you have your project
cd "C:\Users\Aritz\project\PI_Institute"

# Verification: The 'server' folder should appear
say
```

### 1.2 CORS Configuration (Backend)

To avoid connection errors between Front and Back, edit the following file in VS Code:

**Path:** `servidor/app-horario/src/main/java/com/ies/poligono/sur/app/horario/security/CorsConfig.java`

**Action:** Add port 5147 to the list of allowed sources:

```java
config.setAllowedOrigins(List.of("http://localhost:5173", "http://172.17.0.2:5173", "http://localhost:5147"));
```

> 💾 **Note:** Save the file (`Ctrl + S`) after making the change.

---

## 🐳 Container Deployment

### 2.1 Create and run the container

Run the following command in PowerShell to raise the Ubuntu container by mapping the current folder:

``powershell
# Windows PowerShell
docker run -it --name schedules-back -p 8080:8080 -p 8081:8081 -p 5147:5147 -p 3306:3306 -v "${PWD}:/workspace" ubuntu:22.04 bash
```

> 🔄 **If the container already exists**, use:
``powershell
docker start -ai schedules-back
```

Once inside, the prompt will change to `root@....`

---

## ⚙️ Installation of Dependencies

### 3.1 Install Java 21, Maven and MariaDB

> ⚠️ **Note:** This section ignores Node.js (only necessary for the Frontend).

Copy and paste the following command block into the Docker terminal:

```bash
# Update repositories
apt update

# Install core dependencies
apt install -y curl software-properties-common openjdk-21-jdk maven mariadb-server mariadb-client
```

> ⏱️ **Estimated time:** 5-10 minutes depending on your connection.

---

## 🗄️ Database Configuration

> ⚠️ **Important:** The MariaDB service must be started and configured **manually each time** the container is created from scratch.

### 4.1 Start the MariaDB service

```bash
# Start the service
service mariadb start

# Check status
service mariadb status
```

### 4.2 Create Database and Administrator User

```bash
# Create DB, user and grant permissions
mariadb -u root -e "CREATE DATABASE IF NOT EXISTS \`app-horario\`; CREATE USER IF NOT EXISTS 'admin'@'%' IDENTIFIED BY 'admin'; GRANT ALL PRIVILEGES ON \`app-horario\`.* TO 'admin'@'%'; FLUSH PRIVILEGES;"
```

> ✅ **Verification:** You can verify the creation with:
```bash
mariadb -u root -e "SHOW DATABASES;"
```

---

## 🚀 Server Execution (Backend)

### 5.1 Launch Spring Boot

Navigate to the project folder and run the server:

```bash
# Navigate to the backend directory
cd /workspace/server/app-schedule

# Run Spring Boot
mvn spring-boot:run
```

> ⏳ **Wait** until you see the message:
> ```
> Started AppScheduleApplication in X.XXX seconds
> ```

> ⚠️ **Do not close this terminal.** Leave it minimized if you need to release the session.

---

## 📖 Quick Reference

| Step | Command/Action |
|------|-----|
| Create container | `docker run -it --name schedules-back -p 8080:8080 -p 8081:8081 -p 5147:5147 -p 3306:3306 -v "${PWD}:/workspace" ubuntu:22.04 bash` |
| Start existing container | `docker start -ai schedules-back` |
| Access container | `docker exec -it schedules-back bash` |
| Start MariaDB | `service mariadb start` |
| Create DB | `mariadb -u root -e "CREATE DATABASE IF NOT EXISTS \\\`app-horario\\\`; CREATE USER IF NOT EXISTS 'admin'@'%' IDENTIFIED BY 'admin'; GRANT ALL PRIVILEGES ON \\\`app-horario\\\`.* TO 'admin'@'%'; FLUSH PRIVILEGES;"` |
| Run backend | `cd /workspace/server/app-schedule && mvn spring-boot:run` |

---