CREATE TABLE users_projects(
    id BIGSERIAL PRIMARY KEY NOT NULL,
    user_id BIGINT references users(id),
    project_id BIGINT references  projects(id)
)