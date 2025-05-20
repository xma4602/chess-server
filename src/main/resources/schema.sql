CREATE TABLE app_user
(
    id       UUID         NOT NULL,
    login    VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    rating   INTEGER,
    avatar   BYTEA,
    CONSTRAINT pk_app_user PRIMARY KEY (id)
);

CREATE TABLE role
(
    id   UUID         NOT NULL,
    name VARCHAR(255) NOT NULL,
    CONSTRAINT pk_role PRIMARY KEY (id)
);

ALTER TABLE role
    ADD CONSTRAINT uc_role_name UNIQUE (name);

CREATE TABLE app_user_roles
(
    roles_id UUID NOT NULL,
    user_id  UUID NOT NULL
);

ALTER TABLE app_user
    ADD CONSTRAINT uc_app_user_login UNIQUE (login);

ALTER TABLE app_user_roles
    ADD CONSTRAINT fk_appuserol_on_role FOREIGN KEY (roles_id) REFERENCES role (id);

ALTER TABLE app_user_roles
    ADD CONSTRAINT fk_appuserol_on_user FOREIGN KEY (user_id) REFERENCES app_user (id);

CREATE TABLE game_conditions
(
    id                   UUID    NOT NULL,
    creator_figure_color SMALLINT,
    match_mode           SMALLINT,
    time_control         SMALLINT,
    move_time            INTEGER NOT NULL,
    party_time           INTEGER NOT NULL,
    CONSTRAINT pk_gameconditions PRIMARY KEY (id)
);

CREATE TABLE game_play
(
    id                 UUID NOT NULL,
    creator_id         UUID NOT NULL,
    opponent_id        UUID,
    active_user_id     UUID,
    start_date_time    TIMESTAMP WITHOUT TIME ZONE,
    game_conditions_id UUID,
    game_engine        BYTEA,
    CONSTRAINT pk_gameplay PRIMARY KEY (id)
);

ALTER TABLE game_play
    ADD CONSTRAINT FK_GAMEPLAY_ON_ACTIVE_USER FOREIGN KEY (active_user_id) REFERENCES app_user (id);

ALTER TABLE game_play
    ADD CONSTRAINT FK_GAMEPLAY_ON_CREATOR FOREIGN KEY (creator_id) REFERENCES app_user (id);

ALTER TABLE game_play
    ADD CONSTRAINT FK_GAMEPLAY_ON_GAME_CONDITIONS FOREIGN KEY (game_conditions_id) REFERENCES game_conditions (id);

ALTER TABLE game_play
    ADD CONSTRAINT FK_GAMEPLAY_ON_OPPONENT FOREIGN KEY (opponent_id) REFERENCES app_user (id);

CREATE TABLE game_room
(
    id                 UUID NOT NULL,
    creator_id         UUID NOT NULL,
    opponent_id        UUID,
    game_conditions_id UUID,
    CONSTRAINT pk_gameroom PRIMARY KEY (id)
);

ALTER TABLE game_room
    ADD CONSTRAINT FK_GAMEROOM_ON_CREATOR FOREIGN KEY (creator_id) REFERENCES app_user (id);

ALTER TABLE game_room
    ADD CONSTRAINT FK_GAMEROOM_ON_GAMECONDITIONS FOREIGN KEY (game_conditions_id) REFERENCES game_conditions (id);

ALTER TABLE game_room
    ADD CONSTRAINT FK_GAMEROOM_ON_OPPONENT FOREIGN KEY (opponent_id) REFERENCES app_user (id);

CREATE TABLE game_chat
(
    id           UUID NOT NULL,
    game_play_id UUID NOT NULL,
    CONSTRAINT pk_gamechat PRIMARY KEY (id)
);

ALTER TABLE game_chat
    ADD CONSTRAINT uc_gamechat_game_play UNIQUE (game_play_id);

ALTER TABLE game_chat
    ADD CONSTRAINT FK_GAMECHAT_ON_GAME_PLAY FOREIGN KEY (game_play_id) REFERENCES game_play (id);

CREATE TABLE game_chat_message
(
    id           UUID                        NOT NULL,
    game_chat_id UUID                        NOT NULL,
    message      VARCHAR(255)                NOT NULL,
    user_id      UUID                        NOT NULL,
    timestamp    TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT pk_gamechatmessage PRIMARY KEY (id)
);

ALTER TABLE game_chat_message
    ADD CONSTRAINT FK_GAMECHATMESSAGE_ON_GAME_CHAT FOREIGN KEY (game_chat_id) REFERENCES game_chat (id);

ALTER TABLE game_chat_message
    ADD CONSTRAINT FK_GAMECHATMESSAGE_ON_USER FOREIGN KEY (user_id) REFERENCES app_user (id);

CREATE TABLE game_history
(
    id                         UUID                        NOT NULL,
    creator_id                 UUID                        NOT NULL,
    opponent_id                UUID                        NOT NULL,
    creator_rating             INTEGER                     NOT NULL,
    opponent_rating            INTEGER                     NOT NULL,
    creator_rating_difference  INTEGER                     NOT NULL,
    opponent_rating_difference INTEGER                     NOT NULL,
    game_conditions_id         UUID                        NOT NULL,
    game_state                 SMALLINT                    NOT NULL,
    timestamp                  TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT pk_gamehistory PRIMARY KEY (id)
);

ALTER TABLE game_history
    ADD CONSTRAINT FK_GAMEHISTORY_ON_CREATOR FOREIGN KEY (creator_id) REFERENCES app_user (id);

ALTER TABLE game_history
    ADD CONSTRAINT FK_GAMEHISTORY_ON_GAME_CONDITIONS FOREIGN KEY (game_conditions_id) REFERENCES game_conditions (id);

ALTER TABLE game_history
    ADD CONSTRAINT FK_GAMEHISTORY_ON_OPPONENT FOREIGN KEY (opponent_id) REFERENCES app_user (id);