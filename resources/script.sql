CREATE DATABASE IF NOT EXISTS jojoba_pet_center
    CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE jojoba_pet_center;

-- Hapus dari tabel anak ke tabel induk (urutan terbalik dari CREATE)
DROP TABLE IF EXISTS transaction_items;
DROP TABLE IF EXISTS transactions;
DROP TABLE IF EXISTS pets;
DROP TABLE IF EXISTS services;
DROP TABLE IF EXISTS products;
DROP TABLE IF EXISTS customers;

-- ---------------------------------------------------------------------
-- STRUKTUR TABEL (induk dulu, baru tabel yang punya FOREIGN KEY)
-- ---------------------------------------------------------------------

CREATE TABLE customers (
    id      INT AUTO_INCREMENT PRIMARY KEY,
    name    VARCHAR(100) NOT NULL,
    phone   VARCHAR(20)  NOT NULL,
    address VARCHAR(255)
) ENGINE=InnoDB;

CREATE TABLE products (
    id       INT AUTO_INCREMENT PRIMARY KEY,
    name     VARCHAR(100)  NOT NULL,
    category VARCHAR(50)   NOT NULL,
    price    DECIMAL(12,2) NOT NULL,
    stock    INT           NOT NULL DEFAULT 0
) ENGINE=InnoDB;

CREATE TABLE services (
    id    INT AUTO_INCREMENT PRIMARY KEY,
    name  VARCHAR(100)  NOT NULL,
    price DECIMAL(12,2) NOT NULL,
    unit  VARCHAR(20)   NOT NULL DEFAULT 'kali'   -- 'kali' atau 'hari'
) ENGINE=InnoDB;

CREATE TABLE pets (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    customer_id INT          NOT NULL,
    name        VARCHAR(100) NOT NULL,
    species     VARCHAR(50)  NOT NULL,
    gender      VARCHAR(10)  NOT NULL,            -- 'Jantan' atau 'Betina'
    FOREIGN KEY (customer_id) REFERENCES customers(id)
) ENGINE=InnoDB;

CREATE TABLE transactions (
    id               INT AUTO_INCREMENT PRIMARY KEY,
    customer_id      INT NULL,                    -- NULL = pembeli umum
    pet_id           INT NULL,                    -- terisi bila transaksi layanan hewan
    transaction_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    notes            VARCHAR(255) NULL,
    total            DECIMAL(12,2) NOT NULL,
    FOREIGN KEY (customer_id) REFERENCES customers(id),
    FOREIGN KEY (pet_id)      REFERENCES pets(id)
) ENGINE=InnoDB;

CREATE TABLE transaction_items (
    id             INT AUTO_INCREMENT PRIMARY KEY,
    transaction_id INT NOT NULL,
    item_type      ENUM('PRODUCT','SERVICE') NOT NULL,
    product_id     INT NULL,                      -- terisi bila item_type = PRODUCT
    service_id     INT NULL,                      -- terisi bila item_type = SERVICE
    description    VARCHAR(150) NOT NULL,         -- nama item saat transaksi
    unit_price     DECIMAL(12,2) NOT NULL,        -- harga satuan saat transaksi
    quantity       INT NOT NULL,
    subtotal       DECIMAL(12,2) NOT NULL,
    FOREIGN KEY (transaction_id) REFERENCES transactions(id),
    FOREIGN KEY (product_id)     REFERENCES products(id),
    FOREIGN KEY (service_id)     REFERENCES services(id)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- DATA AWAL (seed)
-- ---------------------------------------------------------------------

-- Layanan: 5 layanan per "kali" + penitipan per "hari"
INSERT INTO services (name, price, unit) VALUES
    ('Pengobatan',  150000.00, 'kali'),
    ('Vaksinasi',   200000.00, 'kali'),
    ('Sterilisasi', 600000.00, 'kali'),
    ('Vitamin',      50000.00, 'kali'),
    ('Perawatan',   100000.00, 'kali'),
    ('Penitipan',    50000.00, 'hari');

-- Produk: ada 3 produk dengan stok < 5 (Tali Tuntun, Obat Cacing,
-- Vitamin Bulu) dan 1 produk stok 0 supaya Dashboard dan validasi
-- stok di Kasir langsung bisa diuji.
INSERT INTO products (name, category, price, stock) VALUES
    ('Makanan Kucing Dewasa 1kg',  'Makanan',      85000.00, 25),
    ('Makanan Anak Kucing 400g',   'Makanan',      48000.00, 18),
    ('Makanan Anjing Dewasa 1,5kg','Makanan',     145000.00, 12),
    ('Pasir Kucing Gumpal 5L',     'Perlengkapan', 45000.00, 30),
    ('Kalung Kucing Lonceng',      'Aksesori',     15000.00, 40),
    ('Tali Tuntun Anjing',         'Aksesori',     35000.00,  3),
    ('Shampoo Anti Jamur 200ml',   'Perawatan',    55000.00, 15),
    ('Obat Cacing',                'Obat',         40000.00,  4),
    ('Vitamin Bulu Gel',           'Obat',         95000.00,  2),
    ('Mainan Bola Karet',          'Aksesori',     12000.00,  0);

-- Customer: customer ke-3 (Raffi) sengaja TIDAK punya hewan dan TIDAK punya
-- transaksi, jadi bisa dipakai menguji hapus customer yang berhasil.
-- Customer lain punya hewan dan transaksi, jadi hapusnya harus ditolak.
INSERT INTO customers (name, phone, address) VALUES
    ('Rakhi',           '081234567890', 'Jl. Buah batu No. 12, Bandung'),
    ('Daniel',          '082198765432', 'Jl. Cipacing No. 5, Jatinangor'),
    ('Raffi',           '085711223344', 'Perum Griya Asri Blok C3, Jatinangor'),
    ('Haris',           '081377001122', 'Jl. Cibiru 67, Bandung'),
    ('Banung Ganteng',  '082233445566', 'Jl. Soekarno Hatta No. 21, Bandung');

-- Hewan (hanya kucing dan anjing). customer_id mengacu ke id customer di atas:
-- 1 = Rakhi, 2 = Daniel, 4 = Haris, 5 = Banung Ganteng
INSERT INTO pets (customer_id, name, species, gender) VALUES
    (1, 'Mochi',  'Kucing', 'Betina'),   -- id 1
    (1, 'Bruno',  'Anjing', 'Jantan'),   -- id 2
    (2, 'Luna',   'Kucing', 'Betina'),   -- id 3
    (2, 'Coco',   'Anjing', 'Jantan'),   -- id 4
    (4, 'Oyen',   'Kucing', 'Jantan'),   -- id 5
    (4, 'Milo',   'Anjing', 'Jantan'),   -- id 6
    (5, 'Simba',  'Kucing', 'Jantan'),   -- id 7
    (5, 'Choco',  'Anjing', 'Betina');   -- id 8

-- ---------------------------------------------------------------------
-- Transaksi contoh supaya Dashboard dan riwayat langsung terisi.
-- Tanggal dibuat relatif terhadap waktu script dijalankan, jadi selalu
-- terlihat "baru". Stok produk di atas dianggap sudah termasuk penjualan
-- ini (script tidak mengurangi stok lagi).
-- Id layanan: 1 Pengobatan, 2 Vaksinasi, 3 Sterilisasi, 4 Vitamin,
--             5 Perawatan, 6 Penitipan
-- Id produk : 1 Makanan Kucing, 2 Anak Kucing, 3 Makanan Anjing, 4 Pasir,
--             5 Kalung, 6 Tali Tuntun, 7 Shampoo, 8 Obat Cacing,
--             9 Vitamin Bulu, 10 Mainan Bola
-- ---------------------------------------------------------------------
INSERT INTO transactions (customer_id, pet_id, transaction_date, notes, total) VALUES
    (NULL, NULL, DATE_SUB(NOW(), INTERVAL 20 DAY),  'Pembeli umum',          130000.00),  -- id 1
    (5,    8,    DATE_SUB(NOW(), INTERVAL 12 DAY),  'Vaksin tahunan',        200000.00),  -- id 2
    (1,    1,    DATE_SUB(NOW(), INTERVAL 6 DAY),   'Vaksin + makanan',      370000.00),  -- id 3
    (1,    2,    DATE_SUB(NOW(), INTERVAL 5 DAY),   'Pengobatan + obat cacing', 190000.00), -- id 4
    (2,    3,    DATE_SUB(NOW(), INTERVAL 4 DAY),   'Sterilisasi',           600000.00),  -- id 5
    (NULL, NULL, DATE_SUB(NOW(), INTERVAL 3 DAY),   'Pembeli umum',          204000.00),  -- id 6
    (4,    5,    DATE_SUB(NOW(), INTERVAL 2 DAY),   'Grooming',              155000.00),  -- id 7
    (5,    7,    DATE_SUB(NOW(), INTERVAL 1 DAY),   'Penitipan 3 hari',      240000.00),  -- id 8
    (4,    6,    DATE_SUB(NOW(), INTERVAL 3 HOUR),  'Vitamin',               145000.00),  -- id 9
    (NULL, NULL, DATE_SUB(NOW(), INTERVAL 1 HOUR),  'Pembeli umum',          111000.00);  -- id 10

INSERT INTO transaction_items
    (transaction_id, item_type, product_id, service_id, description, unit_price, quantity, subtotal) VALUES
    -- 1
    (1,  'PRODUCT', 4,    NULL, 'Pasir Kucing Gumpal 5L',        45000.00,  1,  45000.00),
    (1,  'PRODUCT', 1,    NULL, 'Makanan Kucing Dewasa 1kg',     85000.00,  1,  85000.00),
    -- 2
    (2,  'SERVICE', NULL, 2,    'Vaksinasi',                    200000.00,  1, 200000.00),
    -- 3
    (3,  'SERVICE', NULL, 2,    'Vaksinasi',                    200000.00,  1, 200000.00),
    (3,  'PRODUCT', 1,    NULL, 'Makanan Kucing Dewasa 1kg',     85000.00,  2, 170000.00),
    -- 4
    (4,  'SERVICE', NULL, 1,    'Pengobatan',                   150000.00,  1, 150000.00),
    (4,  'PRODUCT', 8,    NULL, 'Obat Cacing',                   40000.00,  1,  40000.00),
    -- 5
    (5,  'SERVICE', NULL, 3,    'Sterilisasi',                  600000.00,  1, 600000.00),
    -- 6
    (6,  'PRODUCT', 3,    NULL, 'Makanan Anjing Dewasa 1,5kg',  145000.00,  1, 145000.00),
    (6,  'PRODUCT', 6,    NULL, 'Tali Tuntun Anjing',            35000.00,  1,  35000.00),
    (6,  'PRODUCT', 10,   NULL, 'Mainan Bola Karet',             12000.00,  2,  24000.00),
    -- 7
    (7,  'SERVICE', NULL, 5,    'Perawatan',                    100000.00,  1, 100000.00),
    (7,  'PRODUCT', 7,    NULL, 'Shampoo Anti Jamur 200ml',      55000.00,  1,  55000.00),
    -- 8
    (8,  'SERVICE', NULL, 6,    'Penitipan',                     50000.00,  3, 150000.00),
    (8,  'PRODUCT', 4,    NULL, 'Pasir Kucing Gumpal 5L',        45000.00,  2,  90000.00),
    -- 9
    (9,  'SERVICE', NULL, 4,    'Vitamin',                       50000.00,  1,  50000.00),
    (9,  'PRODUCT', 9,    NULL, 'Vitamin Bulu Gel',              95000.00,  1,  95000.00),
    -- 10
    (10, 'PRODUCT', 2,    NULL, 'Makanan Anak Kucing 400g',      48000.00,  2,  96000.00),
    (10, 'PRODUCT', 5,    NULL, 'Kalung Kucing Lonceng',         15000.00,  1,  15000.00);