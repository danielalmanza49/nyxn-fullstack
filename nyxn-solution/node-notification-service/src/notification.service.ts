// ============================================================
// MICROSERVICIO DE NOTIFICACIONES — Node.js + TypeScript
// Patrón de Diseño: STRATEGY
// ============================================================

import express, { Request, Response, NextFunction } from 'express';

// ── 1. INTERFACES (Contratos del patrón Strategy) ─────────────

interface NotificationPayload {
  userId: string;
  message: string;
  channel: 'email' | 'sms' | 'push';
}

interface NotificationResult {
  success: boolean;
  channel: string;
  userId: string;
  sentAt: Date;
  messageId?: string;
}

/**
 * STRATEGY INTERFACE: Define el contrato que todas las estrategias de notificación deben cumplir.
 * El contexto solo conoce esta interfaz, NO las implementaciones concretas.
 */
interface NotificationStrategy {
  send(payload: NotificationPayload): Promise<NotificationResult>;
  readonly channel: string;
}


// ── 2. ESTRATEGIAS CONCRETAS ──────────────────────────────────

/**
 * Estrategia de Email (ej: SendGrid, SES)
 */
class EmailNotificationStrategy implements NotificationStrategy {
  readonly channel = 'email';

  async send(payload: NotificationPayload): Promise<NotificationResult> {
    // En producción: integrar con SendGrid/SES
    console.log(`[EMAIL] Enviando a usuario ${payload.userId}: ${payload.message}`);

    // Simulación de llamada al proveedor de email
    await this.callEmailProvider(payload.userId, payload.message);

    return {
      success: true,
      channel: this.channel,
      userId: payload.userId,
      sentAt: new Date(),
      messageId: `email-${Date.now()}`,
    };
  }

  private async callEmailProvider(userId: string, message: string): Promise<void> {
    // await sendgrid.send({ to: getUserEmail(userId), text: message });
    await new Promise(resolve => setTimeout(resolve, 10)); // Simulación
  }
}

/**
 * Estrategia de SMS (ej: Twilio)
 */
class SmsNotificationStrategy implements NotificationStrategy {
  readonly channel = 'sms';

  async send(payload: NotificationPayload): Promise<NotificationResult> {
    console.log(`[SMS] Enviando SMS a usuario ${payload.userId}: ${payload.message}`);

    // await twilioClient.messages.create({ to: getUserPhone(userId), body: message });
    await new Promise(resolve => setTimeout(resolve, 10));

    return {
      success: true,
      channel: this.channel,
      userId: payload.userId,
      sentAt: new Date(),
      messageId: `sms-${Date.now()}`,
    };
  }
}

/**
 * Estrategia de Push Notification (ej: Firebase FCM)
 */
class PushNotificationStrategy implements NotificationStrategy {
  readonly channel = 'push';

  async send(payload: NotificationPayload): Promise<NotificationResult> {
    console.log(`[PUSH] Enviando notificación push a usuario ${payload.userId}: ${payload.message}`);

    // await fcm.send({ token: getDeviceToken(userId), notification: { body: message } });
    await new Promise(resolve => setTimeout(resolve, 10));

    return {
      success: true,
      channel: this.channel,
      userId: payload.userId,
      sentAt: new Date(),
      messageId: `push-${Date.now()}`,
    };
  }
}


// ── 3. CONTEXTO (Router de estrategias) ──────────────────────

/**
 * NOTIFICATION CONTEXT: Selecciona y ejecuta la estrategia correcta en runtime.
 * Para añadir un nuevo canal (WhatsApp), solo se crea una nueva estrategia
 * y se registra aquí. NO se modifica el endpoint principal, sino que creamos uno nuevo (Principio Open/Closed).
 */
class NotificationContext {
  private strategies: Map<string, NotificationStrategy>;

  constructor(strategies: NotificationStrategy[]) {
    this.strategies = new Map(strategies.map(s => [s.channel, s]));
  }

  async notify(payload: NotificationPayload): Promise<NotificationResult> {
    const strategy = this.strategies.get(payload.channel);

    if (!strategy) {
      throw new Error(
        `Canal no soportado: '${payload.channel}'. ` +
        `Canales disponibles: ${[...this.strategies.keys()].join(', ')}`
      );
    }

    return strategy.send(payload);
  }
}


// ── 4. SETUP DE LA APLICACIÓN ─────────────────────────────────

const app = express();
app.use(express.json());

// Inyección de dependencias: registrar todas las estrategias
const notificationContext = new NotificationContext([
  new EmailNotificationStrategy(),
  new SmsNotificationStrategy(),
  new PushNotificationStrategy(),
]);

// ── 5. ENDPOINT UNIFICADO ─────────────────────────────────────

/**
 * POST /notify
 * Enruta dinámicamente al proveedor correcto según el campo 'channel'.
 */
app.post('/notify', async (req: Request, res: Response, next: NextFunction) => {
  const { userId, message, channel } = req.body as NotificationPayload;

  // Validación de entrada
  if (!userId || !message || !channel) {
    res.status(400).json({
      error: 'BAD_REQUEST',
      message: 'Los campos userId, message y channel son obligatorios.',
    });
    return;
  }

  const validChannels = ['email', 'sms', 'push'];
  if (!validChannels.includes(channel)) {
    res.status(400).json({
      error: 'INVALID_CHANNEL',
      message: `Canal inválido. Use uno de: ${validChannels.join(', ')}`,
    });
    return;
  }

  try {
    const result = await notificationContext.notify({ userId, message, channel });
    res.status(200).json(result);
  } catch (error) {
    next(error);
  }
});

// Middleware global de errores
app.use((err: Error, req: Request, res: Response, _next: NextFunction) => {
  console.error(`[ERROR] ${err.message}`);
  res.status(500).json({
    error: 'INTERNAL_SERVER_ERROR',
    message: err.message,
  });
});

// ── 6. ARRANQUE ───────────────────────────────────────────────

const PORT = process.env.PORT || 3000;
app.listen(PORT, () => {
  console.log(`Notification Service corriendo en puerto ${PORT}`);
});

export { NotificationStrategy, NotificationContext };
