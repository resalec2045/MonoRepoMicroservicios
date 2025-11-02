package httpserver

import (
	"net/http"
	"time"

	"github.com/go-chi/chi/v5"
	"github.com/example/user-profile-service-go/pkg/response"
	"github.com/example/user-profile-service-go/internal/logger"
	"go.uber.org/zap"
)

var startedAt = time.Now().UTC()

func formatTime(t time.Time) string {
	return t.Format(time.RFC3339Nano)
}

func NewRouter(api http.Handler) http.Handler {
	r := chi.NewRouter()
	r.Mount("/", api)

	version := "1.1.0"

	r.Get("/health", func(w http.ResponseWriter, r *http.Request) {
		uptime := int(time.Since(startedAt).Seconds())
		resp := map[string]interface{}{
			"status": "UP",
			"version": version,
			"uptime_seconds": uptime,
			"checks": []map[string]interface{}{
				{
					"name": "Readiness check",
					"status": "UP",
					"data": map[string]string{"from": formatTime(startedAt), "status": "READY"},
				},
				{
					"name": "Liveness check",
					"status": "UP",
					"data": map[string]string{"from": formatTime(startedAt), "status": "ALIVE"},
				},
			},
		}
		logger.L.Info("health requested", zap.String("path", "/health"), zap.Int("uptime_seconds", uptime))
		response.JSON(w, http.StatusOK, resp)
	})

	r.Get("/health/live", func(w http.ResponseWriter, r *http.Request) {
		uptime := int(time.Since(startedAt).Seconds())
		resp := map[string]interface{}{
			"status": "UP",
			"version": version,
			"uptime_seconds": uptime,
			"checks": []map[string]interface{}{
				{
					"name": "Liveness check",
					"status": "UP",
					"data": map[string]string{"from": formatTime(startedAt), "status": "ALIVE"},
				},
			},
		}
		logger.L.Debug("liveness requested", zap.Int("uptime_seconds", uptime))
		response.JSON(w, http.StatusOK, resp)
	})

	r.Get("/health/ready", func(w http.ResponseWriter, r *http.Request) {
		uptime := int(time.Since(startedAt).Seconds())
		resp := map[string]interface{}{
			"status": "UP",
			"version": version,
			"uptime_seconds": uptime,
			"checks": []map[string]interface{}{
				{
					"name": "Readiness check",
					"status": "UP",
					"data": map[string]string{"from": formatTime(startedAt), "status": "READY"},
				},
			},
		}
		logger.L.Debug("readiness requested", zap.Int("uptime_seconds", uptime))
		response.JSON(w, http.StatusOK, resp)
	})
	return r
}
