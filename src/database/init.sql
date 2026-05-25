-- ============================================================
-- init.sql - HR Management System Database Initialization
-- ============================================================

CREATE DATABASE IF NOT EXISTS quanlynhansu
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE quanlynhansu;

-- ============================================================
-- Employees table
-- ============================================================
CREATE TABLE IF NOT EXISTS Employees (
    id           VARCHAR(20)   NOT NULL PRIMARY KEY,
    name         VARCHAR(100)  NOT NULL,
    age          INT           NOT NULL,
    type         VARCHAR(30)   NOT NULL COMMENT 'Toan thoi gian / Ban thoi gian / Quan ly',
    department   VARCHAR(100)  DEFAULT '' COMMENT 'Phong ban',
    base_salary  DOUBLE        DEFAULT 0,
    allowance    DOUBLE        DEFAULT 0,
    hours_worked INT           DEFAULT 0,
    hourly_rate  DOUBLE        DEFAULT 0,
    bonus        DOUBLE        DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- Users table
-- ============================================================
CREATE TABLE IF NOT EXISTS Users (
    username VARCHAR(50)  NOT NULL PRIMARY KEY,
    password VARCHAR(100) NOT NULL,
    role     INT          NOT NULL DEFAULT 0 COMMENT '1=Admin, 0=User'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- Seed Data
-- ============================================================
INSERT INTO Users (username, password, role) VALUES
    ('admin', '123', 1),
    ('user', '123',  0);

INSERT INTO Employees (id, name, age, type, department, base_salary, allowance, hours_worked, hourly_rate, bonus) VALUES
    ('NV001', 'Nguyễn Văn An',    30, 'Toàn thời gian', 'Phòng Kỹ thuật',   8000000, 1500000, 0, 0, 0),
    ('NV002', 'Trần Thị Bình',    25, 'Bán thời gian',  'Phòng Kinh doanh', 0,       0, 80, 50000, 0),
    ('NV003', 'Lê Văn Cường',     40, 'Quản lý',        'Phòng Nhân sự',    12000000, 0, 0, 0, 3000000),
    ('NV004', 'Phạm Thị Dung',    28, 'Toàn thời gian', 'Phòng Kỹ thuật',   7500000, 1200000, 0, 0, 0),
    ('NV005', 'Hoàng Văn Em',     35, 'Quản lý',        'Phòng Kinh doanh', 11000000, 0, 0, 0, 2500000);
