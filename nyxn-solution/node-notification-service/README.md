# NYXN Notification Microservice — Node.js

Microservicio de notificaciones implementando el **Patrón Strategy** en TypeScript.

## Stack Tecnologico que usamos
- Node.js 20 + TypeScript
- Express.js
- Patrón de Diseño: Strategy

## Instalación y ejecución

```bash
npm install
npm run dev       # Desarrollo con ts-node
npm run build     # Compilar TypeScript
npm start         # Producción
```

## Endpoint

### POST /notify
```json
{
  "userId": "user-123",
  "message": "Tu pedido #456 ha sido confirmado.",
  "channel": "email"   // "email" | "sms" | "push"
}
```

**Respuesta exitosa (200):**
```json
{
  "success": true,
  "channel": "email",
  "userId": "user-123",
  "sentAt": "2025-01-15T10:30:00.000Z",
  "messageId": "email-1736934600000"
}
```

## Estructura

```
src/
└── notification.service.ts
    ├── NotificationStrategy (interface)
    ├── EmailNotificationStrategy
    ├── SmsNotificationStrategy
    ├── PushNotificationStrategy
    ├── NotificationContext (router de estrategias)
    └── Express app + POST /notify
```

## Añadir un nuevo canal (ej: WhatsApp)

1. Crear `WhatsAppNotificationStrategy implements NotificationStrategy`
2. Registrarla en el `NotificationContext`
3. **Sin tocar el endpoint `/notify`** (Open/Closed Principle)
