import { v4 as uuid } from 'uuid';

const services = new Map();
/*
  Estructura:
  key: name
  value: {
    id, name, endpoint, freqMs, emails[],
    lastStatus: 'UP' | 'DOWN' | 'ALARM',
    lastPayload: object | null,
    lastCheckedAt: ISO string | null,
    sinceUp: ISO string | null,
    sinceDown: ISO string | null,
    timer: NodeJS.Timer
  }
*/

export function registerService({ name, endpoint, freqMs, emails }) {
    const key = name.trim().toLowerCase();
    if (services.has(key)) throw new Error('Service already registered');

    const data = {
        id: uuid(),
        name,
        endpoint,
        freqMs,
        emails,
        lastStatus: 'UNKNOWN',
        lastPayload: null,
        lastCheckedAt: null,
        sinceUp: null,
        sinceDown: null,
        timer: null
    };

    services.set(key, data);
    return data;
}

export function updateService(key, updater) {
    const data = services.get(key);
    if (!data) return null;
    const next = updater(data);
    services.set(key, next);
    return next;
}

export function getService(key) {
    return services.get(key) || null;
}

export function listServices() {
    return Array.from(services.values());
}

export function clearServiceTimer(key) {
    const svc = services.get(key);
    if (svc?.timer) clearInterval(svc.timer);
}

export function removeAll() {
    for (const key of services.keys()) clearServiceTimer(key);
    services.clear();
}
