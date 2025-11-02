import { readFileSync } from 'fs';
import { dirname, join } from 'path';
import { fileURLToPath } from 'url';

const __dirname = dirname(fileURLToPath(import.meta.url));

function loadProperties() {
    try {
        const propertiesPath = join(__dirname, '..', 'application.properties');
        const content = readFileSync(propertiesPath, 'utf-8');
        const properties = {};

        content.split('\n').forEach(line => {
            line = line.trim();
            if (line && !line.startsWith('#')) {
                const [key, value] = line.split('=').map(part => part.trim());
                if (key && value) {
                    process.env[key] = value;
                    properties[key] = value;
                }
            }
        });

        return properties;
    } catch (error) {
        console.error('Error loading application.properties:', error.message);
        return {};
    }
}

export const config = loadProperties();