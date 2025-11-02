import 'dotenv/config';
import express from 'express';
import pinoHttp from 'pino-http';
import logger from './logger.js';
import {
    registerService, listServices, getService
} from './store.js';
import { initMailer } from './notifier.js';
import { startServiceCheck } from './checker.js';

const app = express();
const PORT = process.env.PORT || 8088;
const DEFAULT_FREQ_MS = Number(process.env.DEFAULT_FREQ_MS || 30000);

app.use(express.json());
app.use(pinoHttp({ logger }));

// Health propio del monitoring-service (Reto 3)
app.get('/health', (req, res) => {
    const upSince = process.uptime(); // seg
    const startedAt = new Date(Date.now() - upSince * 1000).toISOString();
    res.json({
        status: 'UP',
        service: process.env.SERVICE_NAME || 'monitoring-service',
        version: '1.0.0',
        uptime_seconds: Math.round(upSince),
        started_at: startedAt
    });
});

// Registro de microservicios a monitorear (Reto 4)
app.post('/register', (req, res) => {
    const { name, endpoint, frequencyMs, emails } = req.body || {};

    if (!name || !endpoint) {
        return res.status(400).json({ error: 'name and endpoint are required' });
    }

    const freqMs = Number(frequencyMs || DEFAULT_FREQ_MS);
    const item = registerService({
        name, endpoint, freqMs, emails: Array.isArray(emails) ? emails : []
    });

    startServiceCheck(name.toLowerCase());

    res.status(201).json({
        message: 'registered',
        service: {
            name: item.name,
            endpoint: item.endpoint,
            frequencyMs: item.freqMs,
            emails: item.emails
        }
    });
});

// Estado de todos los microservicios (Reto 4)
app.get('/health/all', (req, res) => {
    const list = listServices().map(s => ({
        name: s.name,
        endpoint: s.endpoint,
        status: s.lastStatus,
        lastCheckedAt: s.lastCheckedAt,
        sinceUp: s.sinceUp,
        sinceDown: s.sinceDown
    }));
    res.json({ status: 'OK', services: list });
});

// Estado por microservicio (Reto 4)
app.get('/health/:name', (req, res) => {
    const key = req.params.name.toLowerCase();
    const s = getService(key);
    if (!s) return res.status(404).json({ error: 'not_found' });

    res.json({
        name: s.name,
        endpoint: s.endpoint,
        status: s.lastStatus,
        lastCheckedAt: s.lastCheckedAt,
        payload: s.lastPayload,
        sinceUp: s.sinceUp,
        sinceDown: s.sinceDown
    });
});

app.listen(PORT, () => {
    initMailer();
    logger.info({ msg: 'monitoring_service_started', port: PORT });
});
