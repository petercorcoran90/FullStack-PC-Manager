DELETE FROM app_users WHERE username = 'sqluser';
INSERT INTO app_users (username, password, role) VALUES ('sqluser', '$2a$10$XURPShQNCsLjp1ESc2laoObo9QZDhxz73hJPaEv7/cBha4pk0AgP.', 'ROLE_USER');