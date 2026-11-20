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
    breed       VARCHAR(100),
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

-- Customer: customer ke-3 sengaja TIDAK punya hewan, jadi bisa dipakai
-- menguji hapus customer yang berhasil. Customer 1 dan 2 punya hewan,
-- jadi hapusnya harus ditolak.
INSERT INTO customers (name, phone, address) VALUES
    ('Rakhi', '081234567890', 'Jl. Raya Jatinangor No. 12, Sumedang'),
    ('Daniel',   '082198765432', 'Jl. Cipacing No. 5, Jatinangor'),
    ('Raffi', '085711223344', 'Perum Griya Asri Blok C3, Jatinangor');

-- Hewan: customer_id mengacu ke id customer di atas (1 = Budi, 2 = Siti)
INSERT INTO pets (customer_id, name, species, breed, gender) VALUES
    (1, 'Mochi', 'Kucing',  'Persia',          'Betina'),
    (1, 'Bruno', 'Anjing',  'Golden Retriever','Jantan'),
    (2, 'Luna',  'Kucing',  'Domestik',        'Betina'),
    (2, 'Coco',  'Kelinci', 'Holland Lop',     'Jantan');
