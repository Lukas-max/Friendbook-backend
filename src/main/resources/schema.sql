DROP TABLE IF EXISTS chat_room CASCADE;
DROP TABLE IF EXISTS feed_comment CASCADE;
DROP TABLE IF EXISTS feed CASCADE;
DROP TABLE IF EXISTS private_chat CASCADE;
DROP TABLE IF EXISTS public_chat CASCADE;
DROP TABLE IF EXISTS verification_token CASCADE;
DROP TABLE IF EXISTS roles CASCADE;
DROP TABLE IF EXISTS user_roles CASCADE;
DROP TABLE IF EXISTS users CASCADE;

CREATE TABLE roles(
    role_id BIGSERIAL PRIMARY KEY,
    role VARCHAR(50) NOT NULL
);

CREATE TABLE users (
    user_id BIGSERIAL PRIMARY KEY,
    user_uuid VARCHAR (255) NOT NULL UNIQUE,
    username VARCHAR (60)  NOT NULL,
    email VARCHAR (255) NOT NULL UNIQUE ,
    password VARCHAR (60) NOT NULL,
    active BOOLEAN NOT NULL,
    locked BOOLEAN NOT NULL,
    logged BOOLEAN,
    storage_size REAL,
    account_created_time TIMESTAMP without time zone,
    role_id BIGINT REFERENCES roles(role_id)
);

CREATE TABLE user_roles(
    user_id BIGINT REFERENCES users(user_id),
    role_id BIGINT REFERENCES roles(role_id),
    PRIMARY KEY (user_id, role_id)
);

CREATE TABLE verification_token(
    token_id BIGSERIAL PRIMARY KEY ,
    token VARCHAR (255) NOT NULL,
    creation_date_time TIMESTAMP without time zone NOT NULL,
    expiration_date_time TIMESTAMP without time zone NOT NULL,
    confirmation_date_time TIMESTAMP without time zone,
    user_id BIGINT REFERENCES users(user_id)
);

CREATE TABLE public_chat(
    id BIGSERIAL PRIMARY KEY,
    content VARCHAR (255) NOT NULL,
    username VARCHAR (60) NOT NULL,
    user_uuid VARCHAR (255) NOT NULL,
    timestamp TIMESTAMP without time zone NOT NULL
);

CREATE TABLE chat_room(
    room_id BIGSERIAL PRIMARY KEY,
    chat_id VARCHAR (255) NOT NULL ,
    sender_uuid VARCHAR (255) NOT NULL ,
    receiver_uuid VARCHAR (255) NOT NULL
);

CREATE TABLE private_chat(
    id BIGSERIAL PRIMARY KEY ,
    chat_id VARCHAR (255) NOT NULL,
    sender_uuid VARCHAR (255) NOT NULL,
    sender_name VARCHAR (255) NOT NULL,
    receiver_uuid VARCHAR (255) NOT NULL,
    receiver_name VARCHAR (255) NOT NULL,
    content VARCHAR (255) NOT NULL,
    timestamp TIMESTAMP without time zone NOT NULL,
    status VARCHAR (50)
);

CREATE TABLE feed_comment(
    id BIGSERIAL PRIMARY KEY,
    feed_id BIGINT NOT NULL,
    username VARCHAR (60) NOT NULL,
    user_uuid VARCHAR (255) NOT NULL ,
    content VARCHAR (255) NOT NULL,
    timestamp TIMESTAMP without time zone NOT NULL,
    last_updated TIMESTAMP without time zone
);

CREATE TABLE feed(
    id BIGSERIAL PRIMARY KEY ,
    text TEXT NOT NULL,
    files BOOLEAN,
    images BOOLEAN,
    timestamp  TIMESTAMP without time zone NOT NULL,
    user_id BIGINT REFERENCES users(user_id) NOT NULL
);


