-- Создание ролей
INSERT INTO role (id, name)
VALUES (uuid_generate_v4(), 'player'),
       (uuid_generate_v4(), 'admin')
ON CONFLICT (name) DO NOTHING;
-- Избегаем дублирования ролей

-- Создание пользователей с ролью player
INSERT INTO app_user (id, login, password, rating)
VALUES (uuid_generate_v4(), 'player1', '$2a$10$HTcU2Yv8REAHPs7kpPNTue2VmjpevxwnhriOMq4iYQcZuxXBGeQr6', 1600), --password1
       (uuid_generate_v4(), 'player2', '$2a$10$GiJI4IX.MMSpccD9rlQyrOUWLKM6BytKAt2A77sBpC8qlbqg9Df16', 1600), --password2
       (uuid_generate_v4(), 'player3', '$2a$10$PERfmhAFFDSUIQvKWfT4NeisrS.NdMeKDWv8aGz96jel9VFc85oHu', 1600), --password3
       (uuid_generate_v4(), 'player4', '$2a$10$LWKZ99JFF/PHjzZU2IrbQOWhW674.ddfvjE0kpPDDuzg590iAV1vS', 1600), --password4
       (uuid_generate_v4(), 'player5', '$2a$10$.KQ7yZah/PlyKeD0qc4WLOuGrqgF9WNfJsV4tB59jg5Sb/rFH3W4W', 1600); --password5
-- Избегаем дублирования пользователей

-- Создание администратора с ролью admin
INSERT INTO app_user (id, login, password, rating)
VALUES (uuid_generate_v4(), 'admin', '$2a$10$EyXjFmbhOtNjCHaHj5j/W.FncFQinCLkul747ewituzOx0oWDAbcq', 1600); -- Избегаем дублирования пользователей

-- Связывание пользователей с ролью player
INSERT INTO app_user_roles (user_id, roles_id)
SELECT u.id, r.id
FROM app_user u JOIN role r ON r.name = 'player'
WHERE u.login IN ('player1', 'player2', 'player3', 'player4', 'player5');

-- Связывание администратора с ролью admin
INSERT INTO app_user_roles (user_id, roles_id)
SELECT u.id, r.id
FROM app_user u JOIN role r ON r.name = 'admin'
WHERE u.login = 'admin';
