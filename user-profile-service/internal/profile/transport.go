package profile

import (
	"encoding/json"
	"net/http"

	"github.com/go-chi/chi/v5"
	"github.com/go-chi/chi/v5/middleware"
	mw "github.com/example/user-profile-service-go/internal/http/middleware"
	"github.com/example/user-profile-service-go/pkg/response"
	"github.com/example/user-profile-service-go/internal/logger"
	"go.uber.org/zap"
)

type Handler struct{ Svc *Service }

func (h *Handler) Routes() http.Handler {
	r := chi.NewRouter()
	r.Use(middleware.RequestID, middleware.RealIP, middleware.Logger, middleware.Recoverer)

	r.Route("/v1", func(r chi.Router) {
		r.Group(func(r chi.Router) {
			r.Use(mw.DemoAuth)
			r.Get("/profiles/me", h.getMe)
			r.Put("/profiles/me", h.putMe)
		})
		r.Get("/profiles/{userId}", h.getByUserID)
	})
	return r
}

func (h *Handler) getMe(w http.ResponseWriter, r *http.Request) {
	uid, _ := r.Context().Value(mw.CtxUserID).(string)
	logger.L.Debug("handling getMe", zap.String("user_id", uid), zap.String("request_id", middleware.GetReqID(r.Context())))
	p, err := h.Svc.GetOrCreate(uid)
	if err != nil {
		logger.L.Error("getMe failed", zap.Error(err))
		response.Error(w, http.StatusInternalServerError, err.Error())
		return
	}
	response.JSON(w, http.StatusOK, p)
}

func (h *Handler) putMe(w http.ResponseWriter, r *http.Request) {
	uid, _ := r.Context().Value(mw.CtxUserID).(string)
	var in UpsertInput
	if err := json.NewDecoder(r.Body).Decode(&in); err != nil {
		logger.L.Warn("putMe invalid json", zap.Error(err))
		response.Error(w, http.StatusBadRequest, "invalid json body")
		return
	}
	logger.L.Debug("handling putMe", zap.String("user_id", uid), zap.String("request_id", middleware.GetReqID(r.Context())))
	p, err := h.Svc.Upsert(uid, in)
	if err != nil {
		logger.L.Error("putMe upsert failed", zap.Error(err))
		response.Error(w, http.StatusInternalServerError, err.Error())
		return
	}
	response.JSON(w, http.StatusOK, p)
}

func (h *Handler) getByUserID(w http.ResponseWriter, r *http.Request) {
	target := chi.URLParam(r, "userId")
	logger.L.Debug("handling getByUserID", zap.String("target", target), zap.String("request_id", middleware.GetReqID(r.Context())))
	p, ok, _ := h.Svc.repo.GetByUserID(target)
	if !ok {
		logger.L.Info("profile not found", zap.String("target", target))
		response.Error(w, http.StatusNotFound, "profile not found")
		return
	}
	response.JSON(w, http.StatusOK, p.PublicClone())
}
