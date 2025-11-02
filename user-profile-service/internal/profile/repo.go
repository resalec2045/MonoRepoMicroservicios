package profile

type Repository interface {
	GetByUserID(userID string) (Profile, bool, error)
	Upsert(p Profile) error
	ListAll() ([]Profile, error)
}
