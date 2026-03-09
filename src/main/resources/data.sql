-- CPUs (Intel 13th/14th Gen & AMD Ryzen 7000)
INSERT IGNORE INTO hardware_parts (id, category, manufacturer, name, price, stock_level) VALUES
(1, 'CPU', 'Intel', 'Core i9-14900K', 629.99, 15),
(2, 'CPU', 'Intel', 'Core i7-14700K', 409.99, 25),
(3, 'CPU', 'Intel', 'Core i5-13600K', 299.99, 40),
(4, 'CPU', 'AMD', 'Ryzen 9 7950X3D', 699.00, 10),
(5, 'CPU', 'AMD', 'Ryzen 7 7800X3D', 449.00, 8),
(6, 'CPU', 'AMD', 'Ryzen 5 7600X', 229.00, 30);

-- GPUs (NVIDIA RTX 40-Series & AMD Radeon 7000)
INSERT IGNORE INTO hardware_parts (id, category, manufacturer, name, price, stock_level) VALUES
(7, 'GPU', 'NVIDIA', 'GeForce RTX 4090', 1899.99, 3),
(8, 'GPU', 'NVIDIA', 'GeForce RTX 4080 Super', 1199.99, 5),
(9, 'GPU', 'NVIDIA', 'GeForce RTX 4070 Ti', 799.99, 12),
(10, 'GPU', 'NVIDIA', 'GeForce RTX 4060', 299.99, 50),
(11, 'GPU', 'AMD', 'Radeon RX 7900 XTX', 999.99, 8),
(12, 'GPU', 'AMD', 'Radeon RX 7800 XT', 499.99, 20);

-- Motherboards (Z790 & X670)
INSERT IGNORE INTO hardware_parts (id, category, manufacturer, name, price, stock_level) VALUES
(13, 'Motherboard', 'ASUS', 'ROG Maximus Z790 Hero', 699.99, 7),
(14, 'Motherboard', 'MSI', 'MAG B650 Tomahawk WiFi', 219.99, 25),
(15, 'Motherboard', 'Gigabyte', 'Z790 AORUS Elite AX', 259.99, 18),
(16, 'Motherboard', 'ASRock', 'X670E Taichi', 499.99, 6);

-- RAM (DDR5)
INSERT IGNORE INTO hardware_parts (id, category, manufacturer, name, price, stock_level) VALUES
(17, 'RAM', 'Corsair', 'Vengeance 32GB (2x16GB) DDR5-6000', 114.99, 60),
(18, 'RAM', 'G.Skill', 'Trident Z5 RGB 64GB (2x32GB) DDR5-6400', 229.99, 15),
(19, 'RAM', 'Kingston', 'Fury Beast 16GB (2x8GB) DDR5-5200', 69.99, 45);

-- Storage (NVMe SSDs)
INSERT IGNORE INTO hardware_parts (id, category, manufacturer, name, price, stock_level) VALUES
(20, 'Storage', 'Samsung', '990 PRO 2TB NVMe SSD', 169.99, 35),
(21, 'Storage', 'Western Digital', 'WD_BLACK SN850X 1TB', 89.99, 50),
(22, 'Storage', 'Crucial', 'P3 Plus 4TB NVMe SSD', 229.99, 10);

-- Power Supplies (PSU)
INSERT IGNORE INTO hardware_parts (id, category, manufacturer, name, price, stock_level) VALUES
(23, 'PSU', 'Corsair', 'RM1000x Shift 1000W 80+ Gold', 189.99, 20),
(24, 'PSU', 'Seasonic', 'Vertex GX-1200 1200W ATX 3.0', 249.99, 8),
(25, 'PSU', 'EVGA', 'SuperNOVA 850 GT 850W', 129.99, 25);

-- PC Cases
INSERT IGNORE INTO hardware_parts (id, category, manufacturer, name, price, stock_level) VALUES
(26, 'Case', 'Lian Li', 'O11 Dynamic EVO', 159.99, 12),
(27, 'Case', 'NZXT', 'H9 Flow', 159.99, 15),
(28, 'Case', 'Corsair', '4000D Airflow', 99.99, 40),
(29, 'Case', 'Fractal Design', 'North Charcoal Black', 139.99, 10);