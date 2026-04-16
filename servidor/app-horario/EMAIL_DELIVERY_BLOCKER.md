# Estado actual del envio de correos de recuperacion

## Resumen

El flujo de recuperacion de contrasena ya puede:

- localizar al usuario por email
- generar una contrasena temporal
- guardar la contrasena cifrada
- preparar el envio al mismo destinatario que solicito la recuperacion

El bloqueo pendiente no es de codigo interno, sino de identidad de envio.

## Bloqueo real

Para que un correo llegue de verdad a cualquier destinatario, hace falta una identidad de envio valida. En la practica eso significa una de estas dos opciones:

- un dominio o subdominio verificado para envio
- un remitente fijo verificable al que se tenga acceso

Sin una de esas dos condiciones:

- no se puede garantizar entrega real
- no se debe prometer un envio general por API
- cualquier fallback visible sigue siendo solo una ayuda de pruebas

## Rutas viables cuando se desbloquee

### Opcion preferida

Proveedor por API con dominio o subdominio verificado.

Ejemplos viables:

- Resend
- Brevo

Ventajas:

- mejor entregabilidad
- menos friccion operativa que SMTP clasico
- trazabilidad mas clara de errores y entregas

### Opcion alternativa

Proveedor por API con remitente fijo verificable por correo.

Ejemplo viable:

- Brevo con sender verificado por OTP

Limitacion:

- depende de disponer de una cuenta remitente fija y verificable

## Decision actual

Con las restricciones decididas hasta ahora:

- sin DNS
- sin remitente fijo verificable
- con necesidad de envio general real

el trabajo queda bloqueado por una dependencia externa.

## Referencias oficiales revisadas

- Resend exige dominio verificado para envio general:
  https://resend.com/docs/knowledge-base/how-do-I-create-an-email-address-or-sender-in-resend
- El dominio por defecto de Resend no sirve para enviar libremente a destinatarios arbitrarios:
  https://resend.com/docs/knowledge-base/403-error-resend-dev-domain
- Brevo permite verificar un sender por OTP recibido por email:
  https://developers.brevo.com/reference/validate-sender-by-otp

## Preparacion tecnica dejada en el proyecto

El backend ya esta en una posicion razonable para integrar un proveedor por API cuando se desbloquee la identidad de envio:

- existe `spring-boot-starter-webflux`
- el proyecto ya usa `WebClient`
- el endpoint de recuperacion no necesita cambiar de contrato de entrada

Cuando se desbloquee la identidad de envio, la siguiente implementacion recomendada es:

1. crear un adaptador de proveedor de email por API
2. mover el envio de recuperacion a ese adaptador
3. mantener rollback si el proveedor falla
4. validar manualmente con `carlos.robles.dominguez.al@iespoligonosur.org`
