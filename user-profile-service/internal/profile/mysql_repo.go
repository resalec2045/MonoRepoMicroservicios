package profile

import (
	"context"
	"database/sql"
	"encoding/json"
	"errors"
	"fmt"
	_ "github.com/go-sql-driver/mysql"
	"github.com/example/user-profile-service-go/internal/logger"
	"go.uber.org/zap"
)

type mysqlRepo struct {
	db *sql.DB
}

func NewMySQLRepository(db *sql.DB) (Repository, error) {
	r := &mysqlRepo{db: db}
	logger.L.Info("running migrations for profiles table")
	if err := r.migrate(context.Background()); err != nil {
		logger.L.Error("migration failed", zap.Error(err))
		return nil, err
	}
	logger.L.Info("migrations complete")
	return r, nil
}

func (r *mysqlRepo) migrate(ctx context.Context) error {
	ddl := `CREATE TABLE IF NOT EXISTS profiles (
		id VARCHAR(36) PRIMARY KEY,
		user_id VARCHAR(255) NOT NULL UNIQUE,
		nickname VARCHAR(255) NULL,
		personal_url VARCHAR(512) NULL,
		contact_public TINYINT(1) NOT NULL DEFAULT 0,
		mailing_address TEXT NULL,
		bio TEXT NULL,
		organization VARCHAR(255) NULL,
		country VARCHAR(64) NULL,
		socials JSON NULL,
		created_at DATETIME NOT NULL,
		updated_at DATETIME NOT NULL
	) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;`
	_, err := r.db.ExecContext(ctx, ddl)
	if err != nil {
		logger.L.Error("failed to execute migration DDL", zap.Error(err))
	}
	return err
}

func (r *mysqlRepo) GetByUserID(userID string) (Profile, bool, error) {
	row := r.db.QueryRow(`SELECT id,user_id,nickname,personal_url,contact_public,mailing_address,bio,organization,country,socials,created_at,updated_at FROM profiles WHERE user_id=?`, userID)
	var p Profile
	var socialsRaw sql.NullString
	var contactPublic bool
	if err := row.Scan(&p.ID, &p.UserID, &p.Nickname, &p.PersonalURL, &contactPublic, &p.MailingAddress, &p.Bio, &p.Organization, &p.Country, &socialsRaw, &p.CreatedAt, &p.UpdatedAt); err != nil {
		if errors.Is(err, sql.ErrNoRows) {
			logger.L.Debug("profile not found by user_id", zap.String("user_id", userID))
			return Profile{}, false, nil
		}
		logger.L.Error("error scanning profile row", zap.String("user_id", userID), zap.Error(err))
		return Profile{}, false, err
	}
	p.ContactPublic = contactPublic
	p.Socials = SocialLinks{}
	if socialsRaw.Valid && socialsRaw.String != "" {
		_ = json.Unmarshal([]byte(socialsRaw.String), &p.Socials)
	}
	return p, true, nil
}

func (r *mysqlRepo) Upsert(p Profile) error {
	socialsJSON, _ := json.Marshal(p.Socials)
	// Insert or update by user_id uniqueness
	q := `INSERT INTO profiles (id,user_id,nickname,personal_url,contact_public,mailing_address,bio,organization,country,socials,created_at,updated_at)
		VALUES (?,?,?,?,?,?,?,?,?,?,?,?)
		ON DUPLICATE KEY UPDATE
		nickname=VALUES(nickname),
		personal_url=VALUES(personal_url),
		contact_public=VALUES(contact_public),
		mailing_address=VALUES(mailing_address),
		bio=VALUES(bio),
		organization=VALUES(organization),
		country=VALUES(country),
		socials=VALUES(socials),
		updated_at=VALUES(updated_at)`
	_, err := r.db.Exec(q, p.ID, p.UserID, p.Nickname, p.PersonalURL, p.ContactPublic, p.MailingAddress, p.Bio, p.Organization, p.Country, string(socialsJSON), p.CreatedAt, p.UpdatedAt)
	if err != nil {
		logger.L.Error("upsert failed", zap.String("user_id", p.UserID), zap.Error(err))
	}
	return err
}

func (r *mysqlRepo) ListAll() ([]Profile, error) {
	rows, err := r.db.Query(`SELECT id,user_id,nickname,personal_url,contact_public,mailing_address,bio,organization,country,socials,created_at,updated_at FROM profiles`)
	if err != nil {
		logger.L.Error("list all query failed", zap.Error(err))
		return nil, err }
	defer rows.Close()
	var out []Profile
	for rows.Next() {
		var p Profile
		var socialsRaw sql.NullString
		var contactPublic bool
		if err := rows.Scan(&p.ID, &p.UserID, &p.Nickname, &p.PersonalURL, &contactPublic, &p.MailingAddress, &p.Bio, &p.Organization, &p.Country, &socialsRaw, &p.CreatedAt, &p.UpdatedAt); err != nil {
			logger.L.Error("row scan failed in list all", zap.Error(err))
			return nil, err }
		p.ContactPublic = contactPublic
		p.Socials = SocialLinks{}
		if socialsRaw.Valid && socialsRaw.String != "" { _ = json.Unmarshal([]byte(socialsRaw.String), &p.Socials) }
		out = append(out, p)
	}
	return out, rows.Err()
}

func BuildMySQLDSN(host, port, db, user, pass string) string {
	return fmt.Sprintf("%s:%s@tcp(%s:%s)/%s?parseTime=true&charset=utf8mb4&collation=utf8mb4_unicode_ci", user, pass, host, port, db)
}
