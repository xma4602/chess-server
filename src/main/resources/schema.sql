CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
-- Включаем расширение для работы с UUID

-- Создание таблицы ролей
CREATE TABLE IF NOT EXISTS role
(
    id   UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(255) NOT NULL UNIQUE
);

-- Создание таблицы пользователей
CREATE TABLE IF NOT EXISTS app_user
(
    id       UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    login    VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    rating   INTEGER          DEFAULT 100
);

-- Создание таблицы для связывания пользователей и ролей
CREATE TABLE IF NOT EXISTS app_user_roles
(
    user_id  UUID NOT NULL,
    roles_id UUID NOT NULL,
    PRIMARY KEY (user_id, roles_id),
    FOREIGN KEY (user_id) REFERENCES app_user (id) ON DELETE CASCADE,
    FOREIGN KEY (roles_id) REFERENCES role (id) ON DELETE CASCADE
);
