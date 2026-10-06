
-- 1. CRIAR OS UTILIZADORES
INSERT INTO users (username, password, role) VALUES ('backoffice', 'password123', 'BACKOFFICE');
INSERT INTO users (username, password, role) VALUES ('admin', 'admin123', 'ADMIN');
INSERT INTO users (username, password, role) VALUES ('atcc_user', 'atcc123', 'ATCC');
INSERT INTO users (username, password, role) VALUES ('tech', 'tech123', 'MAINTENANCE_TECH');
INSERT INTO users (username, password, role) VALUES ('supervisor', 'super123', 'MAINTENANCE_SUPERVISOR');

-- 2. CRIAR AEROPORTOS
INSERT INTO airport (iata_code, name, city, country, timezone, status, email, phone_number, opening_time, closing_time)
VALUES ('OPO', 'Francisco Sá Carneiro', 'Porto', 'Portugal', 'WET', 'OPERATIONAL', 'info@porto.pt', '+351229432400', '04:00', '00:00');

INSERT INTO airport (iata_code, name, city, country, timezone, status, email, phone_number, opening_time, closing_time)
VALUES ('LIS', 'Humberto Delgado', 'Lisbon', 'Portugal', 'WET', 'OPERATIONAL', 'info@lisbon.pt', '+351218413500', '00:00', '23:59');

INSERT INTO airport (iata_code, name, city, country, timezone, status, email, phone_number, opening_time, closing_time)
VALUES ('MAD', 'Adolfo Suárez Madrid-Barajas', 'Madrid', 'Spain', 'CET', 'OPERATIONAL', 'info@madrid.es', '+34913211000', '00:00', '23:59');

INSERT INTO airport (iata_code, name, city, country, timezone, status, email, phone_number, opening_time, closing_time)
VALUES ('JFK', 'John F. Kennedy International', 'New York', 'USA', 'EST', 'OPERATIONAL', 'info@jfk.com', '+17182444444', '00:00', '23:59');

INSERT INTO airport (iata_code, name, city, country, timezone, status, email, phone_number, opening_time, closing_time)
VALUES ('CDG', 'Charles De Gaule', 'Paris', 'France', 'CET', 'OPERATIONAL', 'info@paris.com', '+33170363950', '00:00', '23:59');

-- 3. CRIAR INSTALAÇÕES PARA O AEROPORTO DO PORTO
INSERT INTO facility (airport_iata, type, capacity, description) VALUES ('OPO', 'Terminal', 10000, 'Terminal Principal');
INSERT INTO facility (airport_iata, type, capacity, description) VALUES ('OPO', 'Gate', 150, 'Porta 14 - Voos Schengen');

-- 4. CRIAR IMAGENS PARA O AEROPORTO
INSERT INTO media_image (airport_iata, image_url, description) VALUES ('OPO', 'https://www.nacionalidadeportuguesa.com.br/wp-content/uploads/2020/07/aeroporto-porto-e1596446056716.jpg', 'Fachada do Aeroporto');
INSERT INTO media_image (airport_iata, image_url, description) VALUES ('CDG', 'https://dicaparis.com/wp-content/uploads/sites/31/2017/02/Aeroporto-Charles-de-Gaulle.jpg', 'Vista Aérea');
INSERT INTO media_image (airport_iata, image_url, description) VALUES ('LIS', 'https://upload.wikimedia.org/wikipedia/commons/2/20/Aeroporto_Humberto_Delgado_%28Portela_Airport_Aeroporto_de_Lisboa%29%2C_Lisbon%2C_Portugal_%2851323558287%29.jpg', 'Fachada do aeroprto');
INSERT INTO media_image (airport_iata, image_url, description) VALUES ('MAD', 'https://www.moso-bamboo.com/wp-content/uploads/2006001_20060724-06_S_HR_Airport-Madrid-ES-WEB.jpg', 'Interior do Aeroporto');
INSERT INTO media_image (airport_iata, image_url, description) VALUES ('JFK', 'https://www.aviontourism.com/images/1920-1920-fix/7ad1add6-7fc7-4a43-89de-0da850924207', 'Fachada do Aeroporto');

-- 5. CRIAR ROTAS
INSERT INTO route (origin_id, destination_id, minimum_range, minimum_capacity, is_active) VALUES ('OPO', 'LIS', 300, 50, true);
INSERT INTO route (origin_id, destination_id, minimum_range, minimum_capacity, is_active) VALUES ('LIS', 'MAD', 500, 100, true);
INSERT INTO route (origin_id, destination_id, minimum_range, minimum_capacity, is_active) VALUES ('MAD', 'JFK', 6000, 200, true);
INSERT INTO route (origin_id, destination_id, minimum_range, minimum_capacity, is_active) VALUES ('OPO', 'MAD', 450, 80, true);
INSERT INTO route (origin_id, destination_id, minimum_range, minimum_capacity, is_active) VALUES ('OPO', 'CDG', 1240, 140, true);
=======

-- 2. Bootstrap de Fabricantes e Modelos (WP #0A / WP #1A)
-- Isto ajuda a Pessoa 2 (Aeronaves)
INSERT INTO manufacturers (id, name) VALUES (1, 'Airbus'), (2, 'Boeing'), (3, 'Embraer');

-- 4. Templates de Manutenção (WP #4A)
-- Isto ajuda a Pessoa 4
INSERT INTO maintenance_templates (name, type) VALUES ('Check A', 'INSPECTION'), ('Check B', 'SCHEDULED');
