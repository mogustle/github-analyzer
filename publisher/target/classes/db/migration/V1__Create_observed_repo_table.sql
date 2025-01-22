CREATE TABLE observed_repo (
    id BIGSERIAL PRIMARY KEY,
    repo_name VARCHAR(1000) NOT NULL,
    repo_owner VARCHAR(1000) NOT NULL,
    repo_status VARCHAR(255) NOT NULL,
    licence VARCHAR(100),
    url VARCHAR(1000),
    stars INT,
    open_issues INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_repo_owner ON observed_repo (repo_owner);
CREATE INDEX idx_repo_licence ON observed_repo (licence);
CREATE INDEX idx_repo_status ON observed_repo (repo_status);
CREATE INDEX idx_repo_owner_name ON observed_repo (repo_name, repo_owner);