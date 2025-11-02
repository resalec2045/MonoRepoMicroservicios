package middleware

import (
	"context"
	"net/http"
	"strings"

	"github.com/example/user-profile-service-go/internal/logger"
	"go.uber.org/zap"
)

type ctxKey string

const (
	CtxUserID  ctxKey = "userID"
	CtxIsAdmin ctxKey = "isAdmin"
)

// DemoAuth: Authorization: Bearer <userId>
func DemoAuth(next http.Handler) http.Handler {
	return http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		auth := r.Header.Get("Authorization")
		parts := strings.Split(auth, " ")
		if len(parts) == 2 && strings.EqualFold(parts[0], "Bearer") && parts[1] != "" {
			ctx := context.WithValue(r.Context(), CtxUserID, parts[1])
			logger.L.Debug("authenticated request", zap.String("user_id", parts[1]))
			next.ServeHTTP(w, r.WithContext(ctx))
			return
		}
		logger.L.Warn("unauthorized request - missing or invalid Authorization header", zap.String("auth_header", auth))
		w.Header().Set("WWW-Authenticate", "Bearer")
		w.WriteHeader(http.StatusUnauthorized)
		_, _ = w.Write([]byte("missing or invalid Authorization header"))
	})
}
