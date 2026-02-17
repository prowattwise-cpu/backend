-- =====================================================
-- WattWisePro Database Tables
-- Raw Usage and Aggregation Tables (Daily, Weekly, Monthly)
-- =====================================================

-- =====================================================
-- 0. RAW USAGE TABLE
-- Receives data directly from hardware sensors
-- =====================================================
CREATE TABLE IF NOT EXISTS `rawUsage` (
    `timestamp` TIMESTAMP NOT NULL,
    `voltage(V)` VARCHAR(255) DEFAULT NULL,
    `current(A)` VARCHAR(255) DEFAULT NULL,
    `power(W)` VARCHAR(255) DEFAULT NULL,
    `energy(kWh)` VARCHAR(255) DEFAULT NULL,
    INDEX `idx_timestamp` (`timestamp`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- =====================================================
-- 1. DAILY USAGE TABLE
-- Aggregates data from rawUsage table by day
-- =====================================================
CREATE TABLE IF NOT EXISTS `dailyUsage` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `date` DATE NOT NULL,
    `total_voltage` DECIMAL(10, 2) DEFAULT 0.00,
    `total_current` DECIMAL(10, 2) DEFAULT 0.00,
    `total_power` DECIMAL(10, 2) DEFAULT 0.00,
    `total_energy` DECIMAL(10, 2) DEFAULT 0.00,
    `peak_voltage` DECIMAL(10, 2) DEFAULT 0.00,
    `peak_current` DECIMAL(10, 2) DEFAULT 0.00,
    `peak_power` DECIMAL(10, 2) DEFAULT 0.00,
    `average_voltage` DECIMAL(10, 2) DEFAULT 0.00,
    `average_current` DECIMAL(10, 2) DEFAULT 0.00,
    `average_power` DECIMAL(10, 2) DEFAULT 0.00,
    `record_count` INT DEFAULT 0,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY `unique_date` (`date`),
    INDEX `idx_date` (`date`),
    INDEX `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- =====================================================
-- 2. WEEKLY USAGE TABLE
-- Aggregates data from dailyUsage table by week
-- =====================================================
CREATE TABLE IF NOT EXISTS `weeklyUsage` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `week_start_date` DATE NOT NULL,
    `week_end_date` DATE NOT NULL,
    `week_number` INT NOT NULL,
    `year` INT NOT NULL,
    `total_voltage` DECIMAL(10, 2) DEFAULT 0.00,
    `total_current` DECIMAL(10, 2) DEFAULT 0.00,
    `total_power` DECIMAL(10, 2) DEFAULT 0.00,
    `total_energy` DECIMAL(10, 2) DEFAULT 0.00,
    `peak_voltage` DECIMAL(10, 2) DEFAULT 0.00,
    `peak_current` DECIMAL(10, 2) DEFAULT 0.00,
    `peak_power` DECIMAL(10, 2) DEFAULT 0.00,
    `average_voltage` DECIMAL(10, 2) DEFAULT 0.00,
    `average_current` DECIMAL(10, 2) DEFAULT 0.00,
    `average_power` DECIMAL(10, 2) DEFAULT 0.00,
    `days_count` INT DEFAULT 0,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY `unique_week` (`year`, `week_number`),
    INDEX `idx_week_start` (`week_start_date`),
    INDEX `idx_year_week` (`year`, `week_number`),
    INDEX `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- =====================================================
-- 3. MONTHLY USAGE TABLE
-- Aggregates data from weeklyUsage table by month
-- =====================================================
CREATE TABLE IF NOT EXISTS `monthlyUsage` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `month` INT NOT NULL CHECK (`month` >= 1 AND `month` <= 12),
    `year` INT NOT NULL,
    `month_name` VARCHAR(20) DEFAULT NULL,
    `total_voltage` DECIMAL(10, 2) DEFAULT 0.00,
    `total_current` DECIMAL(10, 2) DEFAULT 0.00,
    `total_power` DECIMAL(10, 2) DEFAULT 0.00,
    `total_energy` DECIMAL(10, 2) DEFAULT 0.00,
    `peak_voltage` DECIMAL(10, 2) DEFAULT 0.00,
    `peak_current` DECIMAL(10, 2) DEFAULT 0.00,
    `peak_power` DECIMAL(10, 2) DEFAULT 0.00,
    `average_voltage` DECIMAL(10, 2) DEFAULT 0.00,
    `average_current` DECIMAL(10, 2) DEFAULT 0.00,
    `average_power` DECIMAL(10, 2) DEFAULT 0.00,
    `weeks_count` INT DEFAULT 0,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY `unique_month` (`year`, `month`),
    INDEX `idx_year_month` (`year`, `month`),
    INDEX `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- =====================================================
-- NOTES:
-- =====================================================
-- 1. Data Flow: rawUsage -> dailyUsage -> weeklyUsage -> monthlyUsage
--    - rawUsage: Hardware sensors insert data here continuously
--    - dailyUsage: Aggregates rawUsage data by date
--    - weeklyUsage: Aggregates dailyUsage data by week
--    - monthlyUsage: Aggregates weeklyUsage data by month
-- 
-- 2. Aggregation Logic:
--    - Daily: Groups rawUsage by date, calculates totals, averages, and peaks
--    - Weekly: Sums/averages dailyUsage values for the week
--    - Monthly: Sums/averages weeklyUsage values for the month
--
-- 3. Processing Schedule:
--    - Daily aggregation: Run after each day ends (e.g., at 00:00:01)
--    - Weekly aggregation: Run after each week ends (e.g., Sunday 23:59:59)
--    - Monthly aggregation: Run after each month ends (e.g., last day 23:59:59)
--
-- 4. Data Types:
--    - DECIMAL(10, 2) for all numeric values (supports up to 99,999,999.99)
--    - DATE for date fields
--    - TIMESTAMP for created_at and updated_at
--
-- 5. Indexes:
--    - Unique constraints prevent duplicate entries
--    - Indexes on date fields improve query performance
--    - Composite indexes on (year, week_number) and (year, month) for efficient lookups

