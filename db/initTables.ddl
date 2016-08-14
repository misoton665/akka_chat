CREATE TABLE chat_user (id varchar(32) PRIMARY KEY NOT NULL, name varchar(64) NOT NULL, create_at timestamp with time zone DEFAULT CURRENT_TIMESTAMP);

CREATE TABLE chat_message (id serial PRIMARY KEY, user_id varchar(32) NOT NULL REFERENCES chat_user(id), body varchar(140) NOT NULL, create_at timestamp with time zone DEFAULT CURRENT_TIMESTAMP);
