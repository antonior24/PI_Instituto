# Funcionalidad de IA para Horarios

## Descripción

Se ha integrado una funcionalidad que permite consultar al modelo de IA **Gemini 2.5 Flash** de Google sobre cuestiones relacionadas con el horario de un profesor. 

El sistema recupera automáticamente los horarios del profesor autenticado (o del especificado si el usuario es administrador) y los envía junto con la pregunta del usuario al modelo de IA, que proporciona respuestas contextualizadas sobre:

- Información sobre clases específicas
- Conflictos o disponibilidad
- Análisis de carga horaria
- Sugerencias y recomendaciones sobre el horario
- Y cualquier otra consulta relacionada con horarios

## Componentes Añadidos

### Backend (Spring Boot)

1. **`AiServiceImpl.java`** – Servicio que gestiona la comunicación con la API de Google Gemini
   - Recupera los horarios del profesor
   - Construye un contexto con esos horarios
   - Envía la solicitud a Google Gemini usando `WebClient`
   - Extrae la respuesta y la devuelve

2. **`AiService.java`** – Interfaz del servicio

3. **`AiController.java`** – Controlador REST con endpoint `POST /api/horarios/ia`
   - Requiere autenticación (ADMINISTRADOR o PROFESOR)
   - Acepta `idProfesor` opcional (si no se proporciona, usa el del usuario autenticado)
   - Acepta la pregunta en el cuerpo de la solicitud

4. **`HorarioAiRequestDTO.java`** – DTO para parsear solicitudes

### Frontend (Vue 3)

1. **`HorarioAIView.vue`** – Nueva vista con interfaz para consultar la IA
   - Campo de texto para escribir preguntas
   - Botón para enviar
   - Visualización de la respuesta

2. **Router** – Nueva ruta `/horario/ia`

3. **Menu.vue** – Añadido enlace "Horario IA" en la barra de navegación

### Configuración

1. **`pom.xml`** – Añadida dependencia `spring-boot-starter-webflux` para usar `WebClient`

2. **`application.properties`** – Propiedad `google.gemini.api.key` para la clave de API

3. **`SecurityConfig.java`** – Regla de seguridad añadida para el endpoint `/api/horarios/ia`

## Configuración Requerida

Para utilizar esta funcionalidad, debes configurar tu clave de API de Google Gemini (100% GRATUITA):

### Paso 1: Obtener la clave
1. Ve a https://aistudio.google.com/apikey
2. Haz clic en **"Create API Key"** (NO requiere tarjeta de crédito)
3. Copia la clave

### Paso 2: Configurar la clave

**Opción A: Variable de entorno (recomendado)**
```bash
export GOOGLE_GEMINI_API_KEY="tu-clave-aqui"
```

**Opción B: Variable de entorno permanente**

En Linux/Mac:
```bash
echo 'export GOOGLE_GEMINI_API_KEY="tu-clave-aqui"' >> ~/.bashrc
source ~/.bashrc
```

En Windows (PowerShell):
```powershell
[Environment]::SetEnvironmentVariable("GOOGLE_GEMINI_API_KEY", "tu-clave-aqui", "User")
```

**Opción C: Directo en `application.properties` (NO recomendado)**
```properties
google.gemini.api.key=tu-clave-aqui
```
⚠️ **Importante:** Si usas esta opción, **nunca hagas commit** de la clave en Git.

## Uso

1. Accede a `/horario/ia` desde la interfaz web (botón "Horario IA" en la barra de navegación)
2. Escribe tu pregunta sobre el horario
3. Haz clic en "Enviar"
4. La IA proporcionará una respuesta contextualizada según tu horario

## Ejemplos de Consultas

- "¿A qué horas tengo clases los lunes?"
- "¿Cuántas horas de clase tengo esta semana?"
- "¿Qué asignaturas imparto en el aula 101?"
- "¿Tengo algún hueco entre las 10 y las 12?"
- "Sugiere cómo optimizar mi carga horaria"
- "¿Qué días tengo más clases?"

## Notas Técnicas

- El servicio utiliza **Google Gemini 2.5 Flash** (modelo más reciente y rápido)
- Utiliza `WebClient` (WebFlux) para realizar llamadas a la API de Google
- La respuesta se obtiene de forma síncrona mediante `.block()` en el contexto de solicitud HTTP
- El sistema incluye manejo de errores robusto (403, 401, 429, etc.)
- Los datos enviados a Gemini incluyen el horario completo del profesor (sin información de ausencias)
- **Límites gratuitos:** 60 solicitudes por minuto (más que suficiente para uso normal)

## Seguridad

- Solo usuarios autenticados (ADMINISTRADOR o PROFESOR) pueden acceder al endpoint
- Cada profesor solo ve sus propios horarios (excepto administradores que pueden consultar por idProfesor)
- La clave de API debe mantenerse protegida (variable de entorno, no en control de versiones)

## Ventajas de Google Gemini

✅ **100% Gratis** - Sin tarjeta de crédito  
✅ **60 solicitudes/minuto** - Más que suficiente  
✅ **Respuestas rápidas** - Modelo optimizado  
✅ **Multilingüe** - Responde perfectamente en español  
✅ **Contexto largo** - Soporta hasta 1M tokens de entrada
