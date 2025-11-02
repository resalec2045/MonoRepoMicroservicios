package config

import "os"

type Config struct {
	Port        string
	MySQLHost   string
	MySQLPort   string
	MySQLDB     string
	MySQLUser   string
	MySQLPass   string
	MySQLDSN    string // opcional, si se define se usa directo
}

func getenv(k, def string) string {
	v := os.Getenv(k)
	if v == "" { return def }
	return v
}

func Load() Config {
	return Config{
		Port:      getenv("PORT", "8084"),
		MySQLHost: getenv("MYSQL_HOST", "db"),
		MySQLPort: getenv("MYSQL_PORT", "3306"),
		MySQLDB:   getenv("MYSQL_DB", "profiles_db"),
		MySQLUser: getenv("MYSQL_USER", "profile_user"),
		MySQLPass: getenv("MYSQL_PASSWORD", "profile_pass"),
		MySQLDSN:  os.Getenv("MYSQL_DSN"),
	}
}
