CREATE TABLE chat_user (id serial PRIMARY KEY, name varchar(32), nickname varchar(64), bio varchar(500), sign_up_date timestamp with time zone);

CREATE TABLE chat_group (id serial PRIMARY KEY, name varchar(32), nickname varchar(64), create_date timestamp with time zone);

CREATE TABLE message (id serial PRIMARY KEY, user_id int REFERENCES chat_user(id), group_id int REFERENCES chat_group(id), body varchar(500), send_date timestamp with time zone);
