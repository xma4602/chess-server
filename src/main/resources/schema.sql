create table if not exists game_conditions
(
    id                   uuid    not null,
    figure_color         smallint,
    match_mode           smallint,
    move_time            integer not null,
    party_time           integer not null,
    time_control         smallint,
    creator_figure_color smallint,
    constraint game_conditions_pkey
        primary key (id),
    constraint game_conditions_figure_color_check
        check ((figure_color >= 0) AND (figure_color <= 2)),
    constraint game_conditions_match_mode_check
        check ((match_mode >= 0) AND (match_mode <= 1)),
    constraint game_conditions_time_control_check
        check ((time_control >= 0) AND (time_control <= 1)),
    constraint game_conditions_creator_figure_color_check
        check ((creator_figure_color >= 0) AND (creator_figure_color <= 2))
);

create table if not exists app_user
(
    id       uuid    not null,
    login    varchar(255),
    password varchar(255),
    rating   integer not null,
    constraint app_user_pkey
        primary key (id)
);

create table if not exists game_play
(
    id                 uuid not null,
    active_user_id     uuid,
    creator_id         uuid,
    creator_login      varchar(255),
    opponent_id        uuid,
    opponent_login     varchar(255),
    start_date_time    timestamp(6),
    game_conditions_id uuid,
    game_engine        bytea,
    constraint game_play_pkey
        primary key (id),
    constraint uk7g8uf8l3vav6sum1rqipls2jg
        unique (game_conditions_id),
    constraint fki9agdg7u4mby7ftu9p0eymey5
        foreign key (game_conditions_id) references game_conditions,
    constraint fkoowg3xou5x64q5l5gcox6cp7f
        foreign key (active_user_id) references app_user,
    constraint fkqc9q8iusrdfgrkt2nag115smq
        foreign key (creator_id) references app_user,
    constraint fkh5kbi0tgxker7c6l3mo95wuxw
        foreign key (opponent_id) references app_user
);

create table if not exists game_room
(
    id                 uuid not null,
    creator_id         uuid,
    creator_login      varchar(255),
    opponent_id        uuid,
    opponent_login     varchar(255),
    game_conditions_id uuid,
    constraint game_room_pkey
        primary key (id),
    constraint ukqa5rirk4wdhtbh7uc9k5ch760
        unique (game_conditions_id),
    constraint fka12tt60657qj4ysnua9fny3lm
        foreign key (game_conditions_id) references game_conditions,
    constraint fkihnxmtaqcbses2u9ipnmnlnl7
        foreign key (creator_id) references app_user,
    constraint fkq3dpiy0mquj1j1odgmh8b8s4f
        foreign key (opponent_id) references app_user
);

create table if not exists role
(
    id   uuid default uuid_generate_v4() not null,
    name varchar(255)                    not null,
    constraint role_pkey
        primary key (id),
    constraint role_name_key
        unique (name)
);

create table if not exists app_user_roles
(
    user_id  uuid not null,
    roles_id uuid not null,
    constraint app_user_roles_pkey
        primary key (user_id, roles_id),
    constraint app_user_roles_user_id_fkey
        foreign key (user_id) references app_user
            on delete cascade,
    constraint app_user_roles_roles_id_fkey
        foreign key (roles_id) references role
            on delete cascade
);

create table if not exists game_chat
(
    id           uuid not null,
    game_play_id uuid not null,
    constraint game_chat_pkey
        primary key (id),
    constraint uktgiicch61eex3sstedvasqs1
        unique (game_play_id),
    constraint fkta36nwsg0uqn0v6x0rethh1d3
        foreign key (game_play_id) references game_play
);

create table if not exists game_chat_message
(
    id           uuid         not null,
    message      varchar(255) not null,
    timestamp    timestamp(6) not null,
    game_chat_id uuid         not null,
    user_id      uuid         not null,
    constraint game_chat_message_pkey
        primary key (id),
    constraint fka27029an6k4dj3k14dwh7suqf
        foreign key (game_chat_id) references game_chat,
    constraint fki318s62mxwui1i5ro5yiuljm5
        foreign key (user_id) references app_user
);

