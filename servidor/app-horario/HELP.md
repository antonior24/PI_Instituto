# Getting Started

### Reference Documentation
For further reference, please consider the following sections:

* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
* [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/3.4.4/maven-plugin)
* [Create an OCI image](https://docs.spring.io/spring-boot/3.4.4/maven-plugin/build-image.html)
* [Spring Web](https://docs.spring.io/spring-boot/3.4.4/reference/web/servlet.html)
* [Spring Data JPA](https://docs.spring.io/spring-boot/3.4.4/reference/data/sql.html#data.sql.jpa-and-spring-data)

### Guides
The following guides illustrate how to use some features concretely:

* [Building a RESTful Web Service](https://spring.io/guides/gs/rest-service/)
* [Serving Web Content with Spring MVC](https://spring.io/guides/gs/serving-web-content/)
* [Building REST services with Spring](https://spring.io/guides/tutorials/rest/)
* [Accessing Data with JPA](https://spring.io/guides/gs/accessing-data-jpa/)

### Funcionalidad IA

La API dispone ahora de un nuevo endpoint **POST /api/horarios/ia**. El cuerpo debe contener un JSON con los campos `pregunta` y opcionalmente `idProfesor`. Si `idProfesor` no se especifica se usará el usuario autenticado para determinar el profesor.

Este endpoint se conecta con Google Gemini (modelo `gemini-2.5-flash`) para generar respuestas naturales a consultas sobre el horario. La clave de Gemini se obtiene en https://aistudio.google.com/apikey y debe configurarse en la propiedad `google.gemini.api.key` (o mediante la variable de entorno `GOOGLE_GEMINI_API_KEY`).

Ejemplo de cuerpo:

```json
{ "pregunta": "¿Qué clases tengo el lunes?" }
```

### Maven Parent overrides

Due to Maven's design, elements are inherited from the parent POM to the project POM.
While most of the inheritance is fine, it also inherits unwanted elements like `<license>` and `<developers>` from the parent.
To prevent this, the project POM contains empty overrides for these elements.
If you manually switch to a different parent and actually want the inheritance, you need to remove those overrides.

