-- ============================================================
-- PlayRent MySQL Schema
-- Run this script to initialize the database
-- ============================================================

CREATE DATABASE IF NOT EXISTS playrent_db;
USE playrent_db;

-- ------------------------------------------------------------
-- Table: users
-- Holds all registered customer, staff, and admin accounts
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS users (
    id        VARCHAR(50)  PRIMARY KEY,
    name      VARCHAR(100) NOT NULL,
    email     VARCHAR(100) NOT NULL UNIQUE,
    password  VARCHAR(100) NOT NULL,
    role      VARCHAR(20)  NOT NULL DEFAULT 'CUSTOMER'
);

-- ------------------------------------------------------------
-- Table: equipment
-- Stores the catalog items with their sports and stock units
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS equipment (
    id         VARCHAR(50)  PRIMARY KEY,
    name       VARCHAR(100) NOT NULL,
    sport      VARCHAR(50)  NOT NULL,
    price      DOUBLE       NOT NULL DEFAULT 0.0,
    emoji      VARCHAR(10)  NOT NULL,
    stock      INT          NOT NULL DEFAULT 0,
    desc_text  TEXT
);

-- ------------------------------------------------------------
-- Table: rentals
-- Tracks equipment rentals booked by customers
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS rentals (
    id               VARCHAR(50)  PRIMARY KEY,
    userId           VARCHAR(50)  NOT NULL,
    userName         VARCHAR(100) NOT NULL,
    equipmentId      VARCHAR(50)  NOT NULL,
    equipmentName    VARCHAR(100) NOT NULL,
    equipmentEmoji   VARCHAR(10)  NOT NULL,
    quantity         INT          NOT NULL DEFAULT 1,
    days             INT          NOT NULL DEFAULT 1,
    startDate        VARCHAR(20)  NOT NULL,
    total            DOUBLE       NOT NULL DEFAULT 0.0,
    status           VARCHAR(20)  NOT NULL DEFAULT 'PENDING',
    createdAt        VARCHAR(50)  NOT NULL,
    FOREIGN KEY (userId) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (equipmentId) REFERENCES equipment(id) ON DELETE CASCADE
);
