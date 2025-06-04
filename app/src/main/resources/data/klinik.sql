-- MySQL Script (Final Revision: ERD for PK/FK, Class Diagram TOP FRACTION for Attributes, Restricted User Roles)
SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema mydb
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `mydb` DEFAULT CHARACTER SET utf8 ;
USE `mydb` ;

-- -----------------------------------------------------
-- Table `mydb`.`User`
-- PK from ERD. Attributes ONLY from User class (top fraction) in Class Diagram.
-- User.role is now an ENUM with specified values.
-- -----------------------------------------------------
DROP TABLE IF EXISTS `mydb`.`User` ;

CREATE TABLE IF NOT EXISTS `mydb`.`User` (
  `userId` VARCHAR(10) NOT NULL,
  `username` VARCHAR(50) NOT NULL,
  `password` VARCHAR(50) NOT NULL,
  `email` VARCHAR(100) NOT NULL,
  `role` ENUM('DOCTOR', 'PATIENT', 'RECEPTIONIST', 'PHARMACIST') NOT NULL, -- Restricted roles
  `lastLogin` DATETIME NULL,
  `isAdmin` TINYINT(1) NOT NULL DEFAULT 0,
  `isActive` TINYINT(1) NOT NULL DEFAULT 1,
  `passwordExpDate` DATETIME NULL,
  `dailyNotification` VARCHAR(255) NULL,
  PRIMARY KEY (`userId`),
  UNIQUE INDEX `idx_User_username_unique` (`username` ASC) VISIBLE,
  UNIQUE INDEX `idx_User_email_unique` (`email` ASC) VISIBLE)
ENGINE = InnoDB;

-- -----------------------------------------------------
-- Table `mydb`.`Doctor`
-- PK/FK from ERD. Attributes ONLY from Doctor class (top fraction) in Class Diagram.
-- -----------------------------------------------------
DROP TABLE IF EXISTS `mydb`.`Doctor` ;

CREATE TABLE IF NOT EXISTS `mydb`.`Doctor` (
  `doctorId` VARCHAR(10) NOT NULL,
  `userId` VARCHAR(10) NOT NULL,
  `specialization` VARCHAR(100) NULL,
  `availableHours` VARCHAR(255) NULL,
  PRIMARY KEY (`doctorId`),
  UNIQUE INDEX `idx_Doctor_userId_unique` (`userId` ASC) VISIBLE,
  CONSTRAINT `fk_Doctor_User_userId`
    FOREIGN KEY (`userId`)
    REFERENCES `mydb`.`User` (`userId`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB;

-- -----------------------------------------------------
-- Table `mydb`.`Patient`
-- PK/FK from ERD. Attributes ONLY from Patient class (top fraction) in Class Diagram.
-- -----------------------------------------------------
DROP TABLE IF EXISTS `mydb`.`Patient` ;

CREATE TABLE IF NOT EXISTS `mydb`.`Patient` (
  `patientId` VARCHAR(10) NOT NULL,
  `userId` VARCHAR(10) NOT NULL,
  `height` FLOAT NULL,
  `weight` FLOAT NULL,
  `bloodType` VARCHAR(5) NULL, 
  `allergies` JSON NULL,
  `insuranceInfo` JSON NULL,
  `emergencyContact` VARCHAR(25) NULL,
  PRIMARY KEY (`patientId`),
  UNIQUE INDEX `idx_Patient_userId_unique` (`userId` ASC) VISIBLE,
  CONSTRAINT `fk_Patient_User_userId`
    FOREIGN KEY (`userId`)
    REFERENCES `mydb`.`User` (`userId`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB;

-- -----------------------------------------------------
-- Table `mydb`.`Medication`
-- PK from ERD. Attributes ONLY from Medication class (top fraction) in Class Diagram.
-- -----------------------------------------------------
DROP TABLE IF EXISTS `mydb`.`Medication` ;

CREATE TABLE IF NOT EXISTS `mydb`.`Medication` (
  `medicationId` VARCHAR(10) NOT NULL,
  `name` VARCHAR(100) NOT NULL,
  `genericName` VARCHAR(100) NULL,
  `category` VARCHAR(50) NULL,
  `stockQuantity` INT NOT NULL DEFAULT 0,
  `minimumStockLevel` INT NULL DEFAULT 0,
  `expirationDate` DATETIME NULL,
  `sideEffects` VARCHAR(255) NULL,
  `contraindication` VARCHAR(255) NULL,
  PRIMARY KEY (`medicationId`))
ENGINE = InnoDB;

-- -----------------------------------------------------
-- Table `mydb`.`Prescription`
-- PK/FK from ERD. Attributes ONLY from Prescription class (top fraction) in Class Diagram.
-- -----------------------------------------------------
DROP TABLE IF EXISTS `mydb`.`Prescription` ;

CREATE TABLE IF NOT EXISTS `mydb`.`Prescription` (
  `prescriptionId` VARCHAR(10) NOT NULL,
  `patientId` VARCHAR(10) NOT NULL,
  `doctorId` VARCHAR(10) NOT NULL,
  `prescriptionDate` DATETIME NOT NULL,
  `instructions` VARCHAR(255) NOT NULL,
  `diagnosis` VARCHAR(255) NULL,
  PRIMARY KEY (`prescriptionId`),
  INDEX `idx_Prescription_patientId` (`patientId` ASC) VISIBLE,
  INDEX `idx_Prescription_doctorId` (`doctorId` ASC) VISIBLE,
  CONSTRAINT `fk_Prescription_Patient_patientId`
    FOREIGN KEY (`patientId`)
    REFERENCES `mydb`.`Patient` (`patientId`)
    ON DELETE RESTRICT
    ON UPDATE CASCADE,
  CONSTRAINT `fk_Prescription_Doctor_doctorId`
    FOREIGN KEY (`doctorId`)
    REFERENCES `mydb`.`Doctor` (`doctorId`)
    ON DELETE RESTRICT
    ON UPDATE CASCADE)
ENGINE = InnoDB;

-- -----------------------------------------------------
-- Table `mydb`.`PrescriptionItem`
-- PK/FK from ERD. Attributes ONLY from PrescriptionItem class (top fraction) in Class Diagram.
-- -----------------------------------------------------
DROP TABLE IF EXISTS `mydb`.`PrescriptionItem` ;

CREATE TABLE IF NOT EXISTS `mydb`.`PrescriptionItem` (
  `prescriptionId` VARCHAR(10) NOT NULL,
  `itemId` VARCHAR(10) NOT NULL, 
  `dosage` VARCHAR(50) NOT NULL,
  `frequency` VARCHAR(50) NOT NULL,
  `duration` VARCHAR(50) NOT NULL,
  `notes` JSON NULL,
  PRIMARY KEY (`prescriptionId`, `itemId`),
  INDEX `idx_PrescriptionItem_itemId` (`itemId` ASC) VISIBLE,
  CONSTRAINT `fk_PrescriptionItem_Prescription_prescriptionId`
    FOREIGN KEY (`prescriptionId`)
    REFERENCES `mydb`.`Prescription` (`prescriptionId`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_PrescriptionItem_Medication_itemId`
    FOREIGN KEY (`itemId`)
    REFERENCES `mydb`.`Medication` (`medicationId`)
    ON DELETE RESTRICT
    ON UPDATE CASCADE)
ENGINE = InnoDB;

-- -----------------------------------------------------
-- Table `mydb`.`MedicalRecord`
-- PK/FK from ERD. Attributes ONLY from MedicalRecord class (top fraction) in Class Diagram + Essential Date.
-- -----------------------------------------------------
DROP TABLE IF EXISTS `mydb`.`MedicalRecord` ;

CREATE TABLE IF NOT EXISTS `mydb`.`MedicalRecord` (
  `recordId` VARCHAR(10) NOT NULL,
  `patientId` VARCHAR(10) NOT NULL,
  `doctorId` VARCHAR(10) NOT NULL,
  `recordDate` DATETIME NOT NULL,
  `symptoms` VARCHAR(255) NOT NULL,
  `diagnosis` VARCHAR(255) NOT NULL,
  `treatment` VARCHAR(255) NOT NULL,
  `notes` JSON NULL,
  PRIMARY KEY (`recordId`),
  INDEX `idx_MedicalRecord_patientId` (`patientId` ASC) VISIBLE,
  INDEX `idx_MedicalRecord_doctorId` (`doctorId` ASC) VISIBLE,
  CONSTRAINT `fk_MedicalRecord_Patient_patientId`
    FOREIGN KEY (`patientId`)
    REFERENCES `mydb`.`Patient` (`patientId`)
    ON DELETE RESTRICT
    ON UPDATE CASCADE,
  CONSTRAINT `fk_MedicalRecord_Doctor_doctorId`
    FOREIGN KEY (`doctorId`)
    REFERENCES `mydb`.`Doctor` (`doctorId`)
    ON DELETE RESTRICT
    ON UPDATE CASCADE)
ENGINE = InnoDB;

-- -----------------------------------------------------
-- Table `mydb`.`Appointment`
-- PK/FK from ERD. Attributes ONLY from Appointment class (top fraction) in Class Diagram.
-- -----------------------------------------------------
DROP TABLE IF EXISTS `mydb`.`Appointment` ;

CREATE TABLE IF NOT EXISTS `mydb`.`Appointment` (
  `appointmentId` VARCHAR(10) NOT NULL,
  `patientId` VARCHAR(10) NOT NULL,
  `doctorId` VARCHAR(10) NOT NULL,
  `appointmentDate` DATETIME NOT NULL, -- Renamed from 'date' for clarity
  `patientIssue` JSON NULL,
  `queueNumber` INT NULL,
  `status` VARCHAR(20) NOT NULL, -- Using VARCHAR as per class diagram 'AppointmentStatus' (string representation)
  PRIMARY KEY (`appointmentId`),
  INDEX `idx_Appointment_patientId` (`patientId` ASC) VISIBLE,
  INDEX `idx_Appointment_doctorId` (`doctorId` ASC) VISIBLE,
  CONSTRAINT `fk_Appointment_Patient_patientId`
    FOREIGN KEY (`patientId`)
    REFERENCES `mydb`.`Patient` (`patientId`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_Appointment_Doctor_doctorId`
    FOREIGN KEY (`doctorId`)
    REFERENCES `mydb`.`Doctor` (`doctorId`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB;

-- -----------------------------------------------------
-- Table `mydb`.`Pharmacist`
-- PK/FK from ERD. Attributes ONLY from Pharmacist class (top fraction) in Class Diagram.
-- -----------------------------------------------------
DROP TABLE IF EXISTS `mydb`.`Pharmacist` ;

CREATE TABLE IF NOT EXISTS `mydb`.`Pharmacist` (
  `pharmacistId` VARCHAR(10) NOT NULL,
  `userId` VARCHAR(10) NOT NULL,
  `licenseId` VARCHAR(50) NOT NULL,
  `yearsOfExperience` INT NULL,
  `specialization` VARCHAR(100) NULL, -- Changed from speciality to specialization
  `isAvailable` TINYINT(1) NOT NULL DEFAULT 1,
  PRIMARY KEY (`pharmacistId`),
  UNIQUE INDEX `idx_Pharmacist_userId_unique` (`userId` ASC) VISIBLE,
  CONSTRAINT `fk_Pharmacist_User_userId`
    FOREIGN KEY (`userId`)
    REFERENCES `mydb`.`User` (`userId`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB;

-- -----------------------------------------------------
-- Table `mydb`.`Receptionist`
-- PK/FK from ERD. Attributes ONLY from Receptionist class (top fraction) in Class Diagram.
-- -----------------------------------------------------
DROP TABLE IF EXISTS `mydb`.`Receptionist` ;

CREATE TABLE IF NOT EXISTS `mydb`.`Receptionist` (
  `receptionistId` VARCHAR(10) NOT NULL,
  `userId` VARCHAR(10) NOT NULL,
  `station` VARCHAR(50) NULL,
  `status` VARCHAR(50) NOT NULL, -- Using VARCHAR as per class diagram 'status: string'
  `officePhoneNumber` VARCHAR(25) NULL,
  `shift` VARCHAR(255) NULL,
  PRIMARY KEY (`receptionistId`),
  UNIQUE INDEX `idx_Receptionist_userId_unique` (`userId` ASC) VISIBLE,
  CONSTRAINT `fk_Receptionist_User_userId`
    FOREIGN KEY (`userId`)
    REFERENCES `mydb`.`User` (`userId`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB;

SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;

-- Mengatur variabel untuk tanggal dan waktu (jika belum di-set dalam sesi)
SET @current_datetime = NOW();
SET @current_date = CURDATE();
SET @future_date_pass_exp = DATE_ADD(@current_datetime, INTERVAL 1 YEAR);

-- -----------------------------------------------------
-- Table `mydb`.`User` (10 records)
-- -----------------------------------------------------
DELETE FROM `User`; -- Hapus data lama jika ada, untuk menghindari konflik PK saat testing
INSERT INTO `User` (`userId`, `username`, `password`, `email`, `role`, `lastLogin`, `isAdmin`, `isActive`, `passwordExpDate`, `dailyNotification`) VALUES
('USR0001', 'dr.andi', 'pass_andi', 'andi.r@clinic.com', 'DOCTOR', @current_datetime, 0, 1, @future_date_pass_exp, 'Jadwal operasi besok pagi.'),
('USR0002', 'dr.bella', 'pass_bella', 'bella.s@clinic.com', 'DOCTOR', @current_datetime, 0, 1, @future_date_pass_exp, 'Rapat tim medis jam 14:00.'),
('USR0003', 'dr.chandra', 'pass_chandra', 'chandra.w@clinic.com', 'DOCTOR', @current_datetime, 0, 1, @future_date_pass_exp, NULL),
('USR0004', 'pasien.diana', 'pass_diana', 'diana.k@example.com', 'PATIENT', @current_datetime, 0, 1, @future_date_pass_exp, 'Pengingat janji temu Anda besok.'),
('USR0005', 'pasien.eko', 'pass_eko', 'eko.p@example.com', 'PATIENT', @current_datetime, 0, 1, @future_date_pass_exp, 'Hasil lab sudah tersedia.'),
('USR0006', 'pasien.fitri', 'pass_fitri', 'fitri.a@example.com', 'PATIENT', @current_datetime, 0, 1, @future_date_pass_exp, NULL),
('USR0007', 'apt.gina', 'pass_gina', 'gina.h@clinic.com', 'PHARMACIST', @current_datetime, 0, 1, @future_date_pass_exp, 'Stok obat X menipis.'),
('USR0008', 'apt.hari', 'pass_hari', 'hari.m@clinic.com', 'PHARMACIST', @current_datetime, 0, 1, @future_date_pass_exp, 'Verifikasi resep baru.'),
('USR0009', 'rcp.indah', 'pass_indah', 'indah.l@clinic.com', 'RECEPTIONIST', @current_datetime, 0, 1, @future_date_pass_exp, '5 Pendaftaran pasien baru hari ini.'),
('USR0010', 'sys.admin', 'pass_sysadmin', 'admin@clinic.com', 'RECEPTIONIST', @current_datetime, 1, 1, @future_date_pass_exp, 'Maintenance sistem malam ini.');

-- -----------------------------------------------------
-- Table `mydb`.`Doctor` (3 records, sesuai User dengan role DOCTOR)
-- -----------------------------------------------------
DELETE FROM `Doctor`;
INSERT INTO `Doctor` (`doctorId`, `userId`, `specialization`, `availableHours`) VALUES
('DOC001', 'USR0001', 'Dokter Umum', 'Senin-Jumat, 08:00-16:00'),
('DOC002', 'USR0002', 'Dokter Anak', 'Senin, Rabu, Jumat, 09:00-17:00'),
('DOC003', 'USR0003', 'Dokter Jantung', 'Selasa, Kamis, 10:00-15:00 (Dengan Janji)');

-- -----------------------------------------------------
-- Table `mydb`.`Patient` (3 records, sesuai User dengan role PATIENT)
-- -----------------------------------------------------
DELETE FROM `Patient`;
INSERT INTO `Patient` (`patientId`, `userId`, `height`, `weight`, `bloodType`, `allergies`, `insuranceInfo`, `emergencyContact`) VALUES
('PAT001', 'USR0004', 160.5, 55.2, 'A+', '{"food": ["Udang"], "medication": ["Penisilin"]}', '{"provider": "Asuransi Sehat Sentosa", "policyNo": "SS12345"}', '08123456001'),
('PAT002', 'USR0005', 175.0, 70.1, 'B-', NULL, '{"provider": "Proteksi Medika", "policyNo": "PM67890"}', '08123456002'),
('PAT003', 'USR0006', 155.2, 62.0, 'O+', '{"environment": ["Debu"]}', '{"provider": "BPJS Kesehatan", "policyNo": "BPJS00123"}', '08123456003');

-- -----------------------------------------------------
-- Table `mydb`.`Pharmacist` (2 records, sesuai User dengan role PHARMACIST)
-- -----------------------------------------------------
DELETE FROM `Pharmacist`;
INSERT INTO `Pharmacist` (`pharmacistId`, `userId`, `licenseId`, `yearsOfExperience`, `specialization`, `isAvailable`) VALUES
('PHM001', 'USR0007', 'SIPA-001/2020', 5, 'Farmasi Klinis', 1),
('PHM002', 'USR0008', 'SIPA-002/2018', 7, 'Manajemen Farmasi', 1);

-- -----------------------------------------------------
-- Table `mydb`.`Receptionist` (2 records, sesuai User dengan role RECEPTIONIST)
-- -----------------------------------------------------
DELETE FROM `Receptionist`;
INSERT INTO `Receptionist` (`receptionistId`, `userId`, `station`, `status`, `officePhoneNumber`, `shift`) VALUES
('RCP001', 'USR0009', 'Meja Depan A', 'Active', '021-7000123 ext. 101', 'Pagi (07:00-15:00)'),
('RCP002', 'USR0010', 'Meja Informasi', 'Active', '021-7000123 ext. 100', 'Sore (14:00-22:00)');

-- -----------------------------------------------------
-- Table `mydb`.`Medication` (10 records)
-- -----------------------------------------------------
DELETE FROM `Medication`;
INSERT INTO `Medication` (`medicationId`, `name`, `genericName`, `category`, `stockQuantity`, `minimumStockLevel`, `expirationDate`, `sideEffects`, `contraindication`) VALUES
('MED001', 'Panadol Biru', 'Paracetamol', 'Analgesik', 500, 50, @current_datetime + INTERVAL 2 YEAR, 'Jarang: ruam kulit', 'Penyakit hati berat'),
('MED002', 'Amoxsan Forte Syrup', 'Amoxicillin', 'Antibiotik', 300, 30, @current_datetime + INTERVAL 1 YEAR, 'Mual, diare', 'Alergi penisilin'),
('MED003', 'Incidal OD', 'Cetirizine', 'Antihistamin', 400, 40, @current_datetime + INTERVAL 3 YEAR, 'Mengantuk', 'Hipersensitif'),
('MED004', 'Omeprazole 20mg Kapsul', 'Omeprazole', 'PPI', 250, 25, @current_datetime + INTERVAL 2 YEAR, 'Sakit kepala, diare', 'Penggunaan bersama Nelfinavir'),
('MED005', 'Glucophage 500mg', 'Metformin', 'Antidiabetik', 350, 35, @current_datetime + INTERVAL 2 YEAR, 'Gangguan GI', 'Asidosis metabolik'),
('MED006', 'Tensivask 5mg', 'Amlodipine', 'Antihipertensi', 200, 20, @current_datetime + INTERVAL 18 MONTH, 'Edema, pusing', 'Syok kardiogenik'),
('MED007', 'Ventolin Inhaler', 'Salbutamol', 'Bronkodilator', 100, 10, @current_datetime + INTERVAL 1 YEAR, 'Tremor, palpitasi', 'Ancaman aborsi'),
('MED008', 'Cataflam 50mg', 'Diclofenac Potassium', 'NSAID', 150, 15, @current_datetime + INTERVAL 2 YEAR, 'Nyeri perut, mual', 'Ulkus peptikum aktif'),
('MED009', 'Neurobion Forte', 'Vitamin B Complex', 'Vitamin', 600, 60, @current_datetime + INTERVAL 3 YEAR, 'Jarang terjadi', 'Hipersensitif terhadap komponen'),
('MED010', 'Lipitor 20mg', 'Atorvastatin', 'Statin', 220, 20, @current_datetime + INTERVAL 2 YEAR, 'Nyeri otot', 'Penyakit hati aktif, kehamilan');

-- -----------------------------------------------------
-- Table `mydb`.`Appointment` (10 records)
-- -----------------------------------------------------
DELETE FROM `Appointment`;
INSERT INTO `Appointment` (`appointmentId`, `patientId`, `doctorId`, `appointmentDate`, `patientIssue`, `queueNumber`, `status`) VALUES
('APP001', 'PAT001', 'DOC001', CONCAT(@current_date, ' 09:00:00'), '{"keluhan": "Demam 2 hari", "detail": "Sakit kepala dan batuk ringan"}', 1, 'SCHEDULED'),
('APP002', 'PAT002', 'DOC002', CONCAT(@current_date, ' 10:00:00'), '{"keluhan": "Kontrol rutin anak", "vaksinasi": "Campak"}', 2, 'SCHEDULED'),
('APP003', 'PAT003', 'DOC003', CONCAT(@current_date, ' 11:00:00'), '{"keluhan": "Nyeri dada saat aktivitas"}', 3, 'CONFIRMED'),
('APP004', 'PAT001', 'DOC002', CONCAT(DATE_ADD(@current_date, INTERVAL 1 DAY), ' 09:30:00'), '{"keluhan": "Anak batuk pilek"}', 1, 'SCHEDULED'),
('APP005', 'PAT002', 'DOC003', CONCAT(DATE_ADD(@current_date, INTERVAL 1 DAY), ' 14:00:00'), '{"keluhan": "Konsultasi hasil EKG"}', 2, 'SCHEDULED'),
('APP006', 'PAT003', 'DOC001', CONCAT(DATE_ADD(@current_date, INTERVAL 2 DAY), ' 10:30:00'), '{"keluhan": "Pemeriksaan kesehatan umum"}', 1, 'SCHEDULED'),
('APP007', 'PAT001', 'DOC001', CONCAT(DATE_ADD(@current_date, INTERVAL 2 DAY), ' 13:00:00'), '{"keluhan": "Sakit tenggorokan"}', 2, 'SCHEDULED'),
('APP008', 'PAT002', 'DOC002', CONCAT(DATE_ADD(@current_date, INTERVAL 3 DAY), ' 11:30:00'), '{"keluhan": "Imunisasi DPT lanjutan"}', 1, 'SCHEDULED'),
('APP009', 'PAT003', 'DOC003', CONCAT(DATE_ADD(@current_date, INTERVAL 3 DAY), ' 15:00:00'), '{"keluhan": "Cek tekanan darah"}', 2, 'CONFIRMED'),
('APP010', 'PAT001', 'DOC001', CONCAT(DATE_ADD(@current_date, INTERVAL 4 DAY), ' 08:00:00'), '{"keluhan": "Pusing berulang"}', 1, 'SCHEDULED');

-- -----------------------------------------------------
-- Table `mydb`.`Prescription` (10 records)
-- -----------------------------------------------------
DELETE FROM `Prescription`;
INSERT INTO `Prescription` (`prescriptionId`, `patientId`, `doctorId`, `prescriptionDate`, `instructions`, `diagnosis`) VALUES
('PRE001', 'PAT001', 'DOC001', CONCAT(@current_date, ' 09:15:00'), 'Minum setelah makan, habiskan.', 'ISPA Viral'),
('PRE002', 'PAT002', 'DOC002', CONCAT(@current_date, ' 10:15:00'), 'Berikan sesuai jadwal.', 'Imunisasi Dasar'),
('PRE003', 'PAT003', 'DOC003', CONCAT(@current_date, ' 11:15:00'), 'Minum 1 jam sebelum makan.', 'Angina Pectoris Stabil'),
('PRE004', 'PAT001', 'DOC002', CONCAT(DATE_ADD(@current_date, INTERVAL 1 DAY), ' 09:45:00'), 'Jika demam berikan paracetamol.', 'Batuk Pilek Anak'),
('PRE005', 'PAT002', 'DOC003', CONCAT(DATE_ADD(@current_date, INTERVAL 1 DAY), ' 14:15:00'), 'Lanjutkan pengobatan sebelumnya.', 'Follow up Angina'),
('PRE006', 'PAT003', 'DOC001', CONCAT(DATE_ADD(@current_date, INTERVAL 2 DAY), ' 10:45:00'), 'Vitamin diminum pagi hari.', 'Suplemen Kesehatan'),
('PRE007', 'PAT001', 'DOC001', CONCAT(DATE_ADD(@current_date, INTERVAL 2 DAY), ' 13:15:00'), 'Kumur dengan air garam hangat.', 'Faringitis Akut'),
('PRE008', 'PAT002', 'DOC002', CONCAT(DATE_ADD(@current_date, INTERVAL 3 DAY), ' 11:45:00'), 'Kompres jika bengkak.', 'Post Imunisasi DPT'),
('PRE009', 'PAT003', 'DOC003', CONCAT(DATE_ADD(@current_date, INTERVAL 3 DAY), ' 15:15:00'), 'Obat diminum rutin setiap hari.', 'Hipertensi grade 1'),
('PRE010', 'PAT001', 'DOC001', CONCAT(DATE_ADD(@current_date, INTERVAL 4 DAY), ' 08:15:00'), 'Hindari pemicu stres.', 'Vertigo non-spesifik');

-- -----------------------------------------------------
-- Table `mydb`.`PrescriptionItem` (Contoh 15 items untuk 10 Prescriptions)
-- -----------------------------------------------------
DELETE FROM `PrescriptionItem`;
INSERT INTO `PrescriptionItem` (`prescriptionId`, `itemId`, `dosage`, `frequency`, `duration`, `notes`) VALUES
('PRE001', 'MED001', '1 tablet', '3x sehari', '5 hari', '{"tambahan": "Jika demam > 38.5C"}'),
('PRE001', 'MED002', '1 sendok takar (5ml)', '3x sehari', '7 hari', '{"tambahan": "Kocok dahulu"}'),
('PRE003', 'MED010', '1 tablet', '1x sehari malam', '30 hari', NULL),
('PRE004', 'MED001', '1/2 tablet (anak)', '3x sehari', '3 hari', '{"tambahan": "Jika perlu"}'),
('PRE005', 'MED010', '1 tablet', '1x sehari malam', 'Lanjutan', '{"tambahan": "Bawa hasil lab kontrol"}'),
('PRE006', 'MED009', '1 tablet', '1x sehari pagi', '30 hari', NULL),
('PRE007', 'MED008', '1 tablet', '2x sehari', '3 hari', '{"tambahan": "Untuk nyeri tenggorokan"}'),
('PRE009', 'MED006', '1 tablet', '1x sehari pagi', '30 hari', '{"tambahan": "Cek tensi rutin"}'),
('PRE010', 'MED009', '1 tablet', '2x sehari', '7 hari', '{"tambahan": "Jika pusing masih berlanjut"}'),
('PRE002', 'MED001', 'Sesuai kebutuhan', 'Jika demam', '1 hari', '{"tambahan": "Untuk antisipasi demam pasca imunisasi"}'),
('PRE003', 'MED008', 'Sesuai kebutuhan', 'Jika nyeri', 'Saat nyeri', '{"tambahan": "Maksimal 3x sehari"}'),
('PRE007', 'MED003', '1 tablet', '1x sehari', '5 hari', '{"tambahan": "Untuk alergi"}'),
('PRE008', 'MED001', 'Sesuai kebutuhan', 'Jika demam', '1 hari', NULL),
('PRE009', 'MED001', 'Sesuai kebutuhan', 'Jika pusing', 'Saat pusing', NULL),
('PRE010', 'MED007', '1-2 semprot', 'Jika sesak', 'Saat sesak', NULL);


-- -----------------------------------------------------
-- Table `mydb`.`MedicalRecord` (10 records)
-- -----------------------------------------------------
DELETE FROM `MedicalRecord`;
INSERT INTO `MedicalRecord` (`recordId`, `patientId`, `doctorId`, `recordDate`, `symptoms`, `diagnosis`, `treatment`, `notes`) VALUES
('REC001', 'PAT001', 'DOC001', CONCAT(@current_date, ' 09:10:00'), 'Demam, batuk, pilek.', 'ISPA', 'Istirahat, Paracetamol, Amoxicillin.', '{"suhu": "38.2C", "tensi": "110/70"}'),
('REC002', 'PAT002', 'DOC002', CONCAT(@current_date, ' 10:10:00'), 'Imunisasi campak.', 'Profilaksis Imunisasi', 'Pemberian vaksin campak.', '{"reaksi_lokal": "Tidak ada"}'),
('REC003', 'PAT003', 'DOC003', CONCAT(@current_date, ' 11:10:00'), 'Nyeri dada kiri menjalar.', 'Angina Pectoris', 'Observasi, ISDN drip, rujuk EKG.', '{"skala_nyeri": "7/10"}'),
('REC004', 'PAT001', 'DOC002', CONCAT(DATE_ADD(@current_date, INTERVAL 1 DAY), ' 09:40:00'), 'Anak batuk, pilek, tidak demam.', 'Common Cold', 'Istirahat, banyak minum, simptomatik.', '{"kondisi_umum": "Baik"}'),
('REC005', 'PAT002', 'DOC003', CONCAT(DATE_ADD(@current_date, INTERVAL 1 DAY), ' 14:10:00'), 'Evaluasi hasil EKG.', 'Susp. CAD', 'Atorvastatin, edukasi gaya hidup.', '{"ekg_result": "Non-spesifik T wave changes"}'),
('REC006', 'PAT003', 'DOC001', CONCAT(DATE_ADD(@current_date, INTERVAL 2 DAY), ' 10:40:00'), 'Check up tahunan.', 'Sehat Jasmani', 'Edukasi pola hidup sehat.', '{"lab_result": "Dalam batas normal"}'),
('REC007', 'PAT001', 'DOC001', CONCAT(DATE_ADD(@current_date, INTERVAL 2 DAY), ' 13:10:00'), 'Nyeri menelan, tenggorokan merah.', 'Faringitis Akut Viral', 'Kumur antiseptik, analgesik.', '{"pemeriksaan_fisik": "Faring hiperemis"}'),
('REC008', 'PAT002', 'DOC002', CONCAT(DATE_ADD(@current_date, INTERVAL 3 DAY), ' 11:40:00'), 'Imunisasi DPT.', 'Profilaksis Imunisasi Lanjutan', 'Pemberian vaksin DPT.', '{"catatan": "Tidak ada KIPI berat"}'),
('REC009', 'PAT003', 'DOC003', CONCAT(DATE_ADD(@current_date, INTERVAL 3 DAY), ' 15:10:00'), 'Tensi 150/90 mmHg.', 'Hipertensi Stage 1', 'Amlodipine 5mg.', '{"target_tensi": "<140/90"}'),
('REC010', 'PAT001', 'DOC001', CONCAT(DATE_ADD(@current_date, INTERVAL 4 DAY), ' 08:10:00'), 'Pusing berputar, mual.', 'Vertigo BPPV', 'Manuver Epley, Betahistine.', '{"tes_dix_hallpike": "Positif"}');