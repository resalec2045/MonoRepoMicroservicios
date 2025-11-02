package logger

import (
	"os"
	"strings"

	"go.uber.org/zap"
	"go.uber.org/zap/zapcore"
)

var L *zap.Logger

func Init() error {
	level := zapcore.InfoLevel
	s := strings.ToLower(os.Getenv("LOG_LEVEL"))
	switch s {
	case "debug":
		level = zapcore.DebugLevel
	case "warn", "warning":
		level = zapcore.WarnLevel
	case "error":
		level = zapcore.ErrorLevel
	case "fatal":
		level = zapcore.FatalLevel
	default:
		level = zapcore.InfoLevel
	}

	cfg := zap.NewProductionConfig()
	cfg.Level = zap.NewAtomicLevelAt(level)
	cfg.Encoding = "json"
	// keep time in ISO8601
	cfg.EncoderConfig.TimeKey = "ts"
	cfg.EncoderConfig.EncodeTime = zapcore.ISO8601TimeEncoder

	logger, err := cfg.Build(zap.AddCaller())
	if err != nil {
		return err
	}
	L = logger
	return nil
}

func Sync() {
	if L != nil {
		_ = L.Sync()
	}
}

