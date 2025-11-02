package main

import (
	"database/sql"
	"fmt"
	"net/http"
	"time"

	"github.com/example/user-profile-service-go/internal/config"
	httpserver "github.com/example/user-profile-service-go/internal/http"
	"github.com/example/user-profile-service-go/internal/profile"
	"github.com/example/user-profile-service-go/internal/logger"
	_ "github.com/go-sql-driver/mysql"
	"go.uber.org/zap"
)

func main() {
	// init logger
	if err := logger.Init(); err != nil {
		fmt.Printf("failed to init logger: %v\n", err)
		return
	}
	defer logger.Sync()
	logger.L.Info("starting service")

	cfg := config.Load()

	// Construir DSN
	dsn := cfg.MySQLDSN
	if dsn == "" {
		dsn = profile.BuildMySQLDSN(cfg.MySQLHost, cfg.MySQLPort, cfg.MySQLDB, cfg.MySQLUser, cfg.MySQLPass)
	}

	// Retry connect
	var db *sql.DB
	var err error
	for i := 0; i < 30; i++ {
		db, err = sql.Open("mysql", dsn)
		if err == nil {
			if err = db.Ping(); err == nil {
				break
			}
		}
		logger.L.Warn("waiting for mysql", zap.Int("attempt", i+1), zap.Error(err))
		time.Sleep(2 * time.Second)
	}
	if err != nil {
		logger.L.Fatal("mysql connect error", zap.Error(err))
	}
	defer db.Close()

	repo, err := profile.NewMySQLRepository(db)
	if err != nil {
		logger.L.Fatal("repo init failed", zap.Error(err))
	}
	svc := profile.NewService(repo)
	h := (&profile.Handler{Svc: svc}).Routes()

	router := httpserver.NewRouter(h)
	addr := ":" + cfg.Port
	logger.L.Info("listening", zap.String("addr", addr))
	if err := http.ListenAndServe(addr, router); err != nil {
		logger.L.Fatal("http server stopped", zap.Error(err))
	}
}
