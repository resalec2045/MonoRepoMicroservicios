import axios from 'axios';
import logger from './logger.js';
import {
    getService, updateService, listServices, clearServiceTimer
} from './store.js';
import { notifyStatusChange } from './notifier.js';

const REQ_TIMEOUT = Number(process.env.REQUEST_TIMEOUT_MS || 5000);

export function startServiceCheck(key) {
    const svc = getService(key);
    if (!svc) return;

    if (svc.timer) clearServiceTimer(key);

    const freq = Number(svc.freqMs);
    const timer = setInterval(() => performCheck(key), freq);
    // Ejecuta una vez inmediatamente:
    performCheck(key);

    updateService(key, (s) => ({ ...s, timer }));
}

export async function performCheck(key) {
    const svc = getService(key);
    if (!svc) return;

    const url = svc.endpoint; // ideal: /health
    const prev = svc.lastStatus;

    try {
        const { data, status } = await axios.get(url, { timeout: REQ_TIMEOUT });

        // Lógica simple para interpretar UP/DOWN:
        // Si el endpoint responde 2xx y status en JSON es "UP" → UP, si no → ALARM
        const json = (typeof data === 'object') ? data : null;
        const logical =
            status >= 200 && status < 300
                ? (json?.status === 'UP' ? 'UP' : 'ALARM')
                : 'DOWN';

        const now = new Date().toISOString();
        const next = updateService(key, (s) => ({
            ...s,
            lastStatus: logical,
            lastPayload: json,
            lastCheckedAt: now,
            sinceUp: logical === 'UP' && s.lastStatus !== 'UP' ? now : s.sinceUp,
            sinceDown: logical !== 'UP' && s.lastStatus === 'UP' ? now : s.sinceDown
        }));

        logger.info({
            msg: 'health_checked',
            service: svc.name,
            endpoint: svc.endpoint,
            httpStatus: status,
            logicalStatus: logical
        });

        if (prev && prev !== logical) {
            await notifyStatusChange({ service: next, prev, next: logical, payload: json });
        }
    } catch (err) {
        const now = new Date().toISOString();
        const next = updateService(key, (s) => ({
            ...s,
            lastStatus: 'DOWN',
            lastPayload: { error: err.message },
            lastCheckedAt: now,
            sinceDown: s.lastStatus === 'UP' ? now : s.sinceDown
        }));

        logger.warn({
            msg: 'health_failed',
            service: svc.name,
            endpoint: svc.endpoint,
            error: err.message
        });

        if (prev && prev !== 'DOWN') {
            await notifyStatusChange({ service: next, prev, next: 'DOWN', payload: { error: err.message } });
        }
    }
}

export function startAll() {
    for (const svc of listServices()) {
        const key = svc.name.toLowerCase();
        startServiceCheck(key);
    }
}
