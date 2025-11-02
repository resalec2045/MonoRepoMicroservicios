import nock from 'nock';
import request from 'supertest';
import http from 'http';
import express from 'express';
import 'dotenv/config';

import pinoHttp from 'pino-http';
import logger from '../monitoring-service/src/logger.js';
import { registerService, removeAll } from '../monitoring-service/src/store.js';
import { startServiceCheck } from '../monitoring-service/src/checker.js';

const app = express();
app.use(express.json());
app.use(pinoHttp({ logger }));

app.post('/register', (req, res) => {
    const { name, endpoint, frequencyMs, emails } = req.body || {};
    const item = registerService({
        name, endpoint, freqMs: Number(frequencyMs || 1000), emails: emails || []
    });
    startServiceCheck(name.toLowerCase());
    res.status(201).json({ message: 'registered', service: { name: item.name } });
});

app.get('/health/all', (req, res) => res.json({ ok: true }));

let server;

beforeAll((done) => {
    server = http.createServer(app).listen(0, done);
});

afterAll((done) => {
    server.close(done);
    removeAll();
    nock.cleanAll();
});

test('register and check service health', async () => {
    const base = 'http://fake-service.local';
    nock(base).get('/health').times(3).reply(200, { status: 'UP' });

    const res = await request(server)
        .post('/register')
        .send({ name: 'users', endpoint: `${base}/health`, frequencyMs: 500 });

    expect(res.status).toBe(201);
});
