-- Создание ролей
INSERT INTO role (id, name)
VALUES (uuid_generate_v4(), 'player'),
       (uuid_generate_v4(), 'admin')
ON CONFLICT (name) DO NOTHING;
-- Избегаем дублирования ролей

-- Создание пользователей с ролью player
INSERT INTO app_user (id, login, password, rating)
VALUES (uuid_generate_v4(), 'player1', 'password1', 100),
       (uuid_generate_v4(), 'player2', 'password2', 100),
       (uuid_generate_v4(), 'player3', 'password3', 100),
       (uuid_generate_v4(), 'player4', 'password4', 100),
       (uuid_generate_v4(), 'player5', 'password5', 100);
-- Избегаем дублирования пользователей

-- Создание администратора с ролью admin
INSERT INTO app_user (id, login, password, rating)
VALUES (uuid_generate_v4(), 'admin', 'admin', 100); -- Избегаем дублирования пользователей

-- Связывание пользователей с ролью player
INSERT INTO app_user_roles (user_id, roles_id)
SELECT u.id, r.id
FROM app_user u
            JOIN role r ON r.name = 'player'
WHERE u.login IN ('player1', 'player2', 'player3', 'player4', 'player5');

-- Связывание администратора с ролью admin
INSERT INTO app_user_roles (user_id, roles_id)
SELECT u.id, r.id
FROM app_user u
            JOIN role r ON r.name = 'admin'
WHERE u.login = 'admin';
