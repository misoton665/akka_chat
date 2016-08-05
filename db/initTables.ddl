CREATE TABLE chat_user (id serial PRIMARY KEY, name varchar(32) NOT NULL, nickname varchar(64) NOT NULL, bio varchar(500) NOT NULL, sign_up_at timestamp with time zone DEFAULT CURRENT_TIMESTAMP);

CREATE TABLE chat_group (id serial PRIMARY KEY, name varchar(32) NOT NULL, nickname varchar(64) NOT NULL, master int NOT NULL REFERENCES chat_user(id), create_at timestamp with time zone DEFAULT CURRENT_TIMESTAMP);

CREATE TABLE message (id serial PRIMARY KEY, user_id int NOT NULL REFERENCES chat_user(id), group_id int NOT NULL REFERENCES chat_group(id), body varchar(500) NOT NULL, send_at timestamp with time zone DEFAULT CURRENT_TIMESTAMP);
