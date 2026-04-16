# Configuracion de Resend para recuperacion de contrasena

## Variables necesarias

- `RESEND_API_KEY`
- `RESEND_FROM`
- `RESEND_REPLY_TO` opcional

## Modos soportados

- `app.password-recovery.mode=testing`
  - genera la contrasena temporal y la devuelve en la respuesta
- `app.password-recovery.mode=production`
  - genera la contrasena temporal y la envia usando Resend
  - si falta configuracion de Resend, el flujo falla con error claro

## Notas importantes

- Resend usa `https://api.resend.com`
- La autenticacion se hace con `Authorization: Bearer <api-key>`
- Las peticiones deben incluir `User-Agent`
- Para envio general, el remitente `from` debe pertenecer a un dominio verificado en Resend

## Referencias oficiales

- API de envio:
  https://resend.com/docs/api-reference/emails
- Introduccion y autenticacion:
  https://resend.com/docs/api-reference/introduction
- Restriccion del dominio `resend.dev`:
  https://resend.com/docs/knowledge-base/403-error-resend-dev-domain
