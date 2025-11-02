import nodemailer from 'nodemailer';
import logger from './logger.js';

const {
    SMTP_HOST, SMTP_PORT, SMTP_SECURE,
    SMTP_USER, SMTP_PASS, SMTP_FROM
} = process.env;

let transporter = null;

export function initMailer() {
    if (!SMTP_HOST) {
        logger.warn({ msg: 'SMTP not configured, notifications disabled' });
        return;
    }
    transporter = nodemailer.createTransport({
        host: SMTP_HOST,
        port: Number(SMTP_PORT || 587),
        secure: String(SMTP_SECURE || 'false') === 'true',
        auth: SMTP_USER && SMTP_PASS ? { user: SMTP_USER, pass: SMTP_PASS } : undefined
    });
}

export async function notifyStatusChange({ service, prev, next, payload }) {
    if (!transporter) return;

    const to = (service.emails || []).join(',');
    if (!to) return;

    const subject = `[Observability] ${service.name} ${prev} → ${next}`;
    const html = `
    <h3>Estado cambiado: ${service.name}</h3>
    <p><b>Anterior:</b> ${prev}</p>
    <p><b>Actual:</b> ${next}</p>
    <p><b>Endpoint:</b> ${service.endpoint}</p>
    <pre>${payload ? JSON.stringify(payload, null, 2) : '(sin cuerpo)'}</pre>
  `;

    try {
        await transporter.sendMail({
            from: SMTP_FROM || 'observability@example.com',
            to,
            subject,
            html
        });
        logger.info({ msg: 'Notification sent', service: service.name, to });
    } catch (err) {
        logger.error({ msg: 'Notification failed', error: err.message });
    }
}
