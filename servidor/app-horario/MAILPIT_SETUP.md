# Recuperación de Contraseña - Mailpit SMTP
## 🚀 Despliegue Local (Desarrollo - 2 Docker separados)

### 1. Mailpit
```bash
docker run -d \
  --restart unless-stopped \
  --name=mailpit \
  -p 8025:8025 \
  -p 1025:1025 \
  axllent/mailpit
```
**Emails:** http://localhost:8025

**Config properties:**
```
spring.mail.host=172.17.0.1
spring.mail.port=1025
app.password-recovery.mode=production
```

## 🧪 Prueba Local
Login → \"Olvidé contraseña\" → email → localhost:8025

## ☁️ Despliegue AWS (App JVM + Mailpit Docker misma máquina)

### 1. AWS VM (Ubuntu/EC2)
```
sudo apt update
sudo apt install docker.io docker-compose
sudo systemctl enable docker
sudo usermod -aG docker $USER
```

### 2. Mailpit Docker
```
docker run -d \
  --restart unless-stopped \
  --name=mailpit \
  -p 8025:8025 \
  -p 1025:1025 \
  axllent/mailpit
```

### 3. App JAR
```
wget app-horario.jar
java -jar app-horario.jar
```

**Config properties AWS:**
```
spring.mail.host=localhost
spring.mail.port=1025
app.password-recovery.mode=production
```
**localhost** funciona (misma máquina)

### 4. Frontend NGINX/PM2

## 🔧 Troubleshooting

| Error | Fix |
|-------|------|
| Connection refused 1025 | Mailpit down |
| UnknownHost | local:172.17.0.1 / AWS:localhost |
| Resend logs | Limpio |

## 🎉 Status

- ✅ Resend eliminado
- ✅ EmailService.enviarCorreo() tu spec
- ✅ Docker local/AWS ready
- ✅ mvn compile OK

¡Desplegado todos entornos!

