CREATE TABLE profiles(
    id UUID PRIMARY KEY,
    profile_username VARCHAR(32) UNIQUE NOT NULL,
    skin_type VARCHAR(32) NOT NULL CHECK (
        skin_type IN ('DEFAULT', 'SLIM')
    )
);

CREATE TABLE accounts(
    id UUID PRIMARY KEY,
    username VARCHAR(64) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    profile_uuid UUID UNIQUE NOT NULL REFERENCES profiles (id)
);

CREATE TABLE session_tokens (
    session_token VARCHAR(255) PRIMARY KEY,
    client_token VARCHAR(255) NOT NULL,
    profile_uuid UUID NOT NULL REFERENCES profiles(id),
    issued_at TIMESTAMP WITH TIME ZONE NOT NULL,
    expires_at TIMESTAMP WITH TIME ZONE NOT NULL,
    token_state VARCHAR(32) NOT NULL CHECK (
        token_state IN ('VALID', 'TEMPORARILY_INVALID', 'INVALID')
    )
);

CREATE INDEX idx_session_tokens_profile_uuid_issued_at
    ON session_tokens(profile_uuid, issued_at);

CREATE TABLE textures(
    id UUID PRIMARY KEY,
    profile_uuid UUID NOT NULL REFERENCES profiles(id),
    texture_type VARCHAR(32) NOT NULL CHECK (
        texture_type IN ('SKIN', 'CAPE')
    ),
    texture_hash VARCHAR(255) NOT NULL,
    skin_bytes BYTEA NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE INDEX idx_textures_profile_uuid
    ON textures(profile_uuid);
