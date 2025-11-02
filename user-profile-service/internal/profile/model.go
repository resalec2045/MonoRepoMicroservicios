package profile

import "time"

type SocialLinks map[string]string

type Profile struct {
	ID             string      `json:"id"`
	UserID         string      `json:"user_id"`
	Nickname       string      `json:"nickname"`
	PersonalURL    string      `json:"personal_url"`
	ContactPublic  bool        `json:"contact_public"`
	MailingAddress string      `json:"mailing_address"`
	Bio            string      `json:"bio"`
	Organization   string      `json:"organization"`
	Country        string      `json:"country"`
	Socials        SocialLinks `json:"socials"`
	CreatedAt      time.Time   `json:"created_at"`
	UpdatedAt      time.Time   `json:"updated_at"`
}

func (p Profile) PublicClone() Profile {
	if p.ContactPublic { return p }
	cl := p
	cl.MailingAddress = ""
	cl.Socials = SocialLinks{}
	return cl
}
