FROM node:20-alpine

WORKDIR /app

# Copia package.json y package-lock.json (el patrón package*.json los incluye)
COPY package*.json ./

# Instala solo dependencias de producción, usando el lockfile
RUN npm ci --omit=dev

# Copia el código
COPY src ./src
COPY .env.example ./.env

ENV NODE_ENV=production
EXPOSE 8088
CMD ["node", "src/index.js"]
