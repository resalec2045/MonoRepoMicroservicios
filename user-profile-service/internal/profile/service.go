package profile

import (
	"time"

	"github.com/google/uuid"
	"github.com/example/user-profile-service-go/internal/logger"
	"go.uber.org/zap"
)

type Service struct{ repo Repository }

func NewService(r Repository) *Service { return &Service{repo: r} }

func (s *Service) GetOrCreate(userID string) (Profile, error) {
	if p, ok, _ := s.repo.GetByUserID(userID); ok {
		logger.L.Debug("get existing profile", zap.String("user_id", userID))
		return p, nil
	}
	now := time.Now().UTC()
	p := Profile{
		ID: uuid.NewString(), UserID: userID, ContactPublic: false,
		Socials: map[string]string{}, CreatedAt: now, UpdatedAt: now,
	}
	logger.L.Info("creating new profile", zap.String("user_id", userID), zap.String("profile_id", p.ID))
	return p, s.repo.Upsert(p)
}

type UpsertInput struct {
	Nickname       *string            `json:"nickname"`
	PersonalURL    *string            `json:"personal_url"`
	ContactPublic  *bool              `json:"contact_public"`
	MailingAddress *string            `json:"mailing_address"`
	Bio            *string            `json:"bio"`
	Organization   *string            `json:"organization"`
	Country        *string            `json:"country"`
	Socials        *map[string]string `json:"socials"`
}

func (s *Service) Upsert(userID string, in UpsertInput) (Profile, error) {
	p, err := s.GetOrCreate(userID)
	if err != nil {
		return p, err
	}
	if in.Nickname != nil {
		p.Nickname = *in.Nickname
	}
	if in.PersonalURL != nil {
		p.PersonalURL = *in.PersonalURL
	}
	if in.ContactPublic != nil {
		p.ContactPublic = *in.ContactPublic
	}
	if in.MailingAddress != nil {
		p.MailingAddress = *in.MailingAddress
	}
	if in.Bio != nil {
		p.Bio = *in.Bio
	}
	if in.Organization != nil {
		p.Organization = *in.Organization
	}
	if in.Country != nil {
		p.Country = *in.Country
	}
	if in.Socials != nil {
		p.Socials = *in.Socials
	}
	p.UpdatedAt = time.Now().UTC()
	logger.L.Info("upserting profile", zap.String("user_id", userID), zap.String("profile_id", p.ID))
	return p, s.repo.Upsert(p)
}
