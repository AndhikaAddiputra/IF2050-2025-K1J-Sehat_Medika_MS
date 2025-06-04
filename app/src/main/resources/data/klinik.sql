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
  `userId` INT NOT NULL AUTO_INCREMENT,
  `username` VARCHAR(50) NOT NULL,
  `password` VARCHAR(50) NOT NULL,
  `email` VARCHAR(255) NOT NULL,
  `phoneNumber` VARCHAR(25) NOT NULL,
  `role` ENUM('DOCTOR', 'PATIENT', 'RECEPTIONIST', 'PHARMACIST', 'ADMIN') NOT NULL, -- Restricted roles
  `lastLogin` DATETIME NULL,
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
  `userId` INT NOT NULL,
  `specialization` VARCHAR(100) NULL,
  `licenseNumber` VARCHAR(50) NULL,
  `availableDays` VARCHAR(255) NULL,
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
  `userId` INT NOT NULL,
  `bloodType` ENUM('A', 'B','AB' ,'O') NOT NULL, 
  `allergies` VARCHAR(255) NULL,
  `emergencyContact` VARCHAR(25) NULL,
  `insuranceInfo` VARCHAR(255) NULL,
  `registrationDate` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
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
  `medicationId` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(255) NOT NULL,
  `genericName` VARCHAR(255) NULL,
  `category` VARCHAR(50) NULL,
  `unit` VARCHAR(50) NULL, -- Optional for unit of measurement
  `stockQuantity` INT NOT NULL DEFAULT 0,
  `minStockLevel` INT NULL DEFAULT 0,
  `expiryDate` DATETIME NOT NULL,
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
  `prescriptionId` INT NOT NULL AUTO_INCREMENT,
  `patientId` VARCHAR(10) NOT NULL,
  `doctorId` VARCHAR(10) NOT NULL,
  `prescriptionDate` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `medications` VARCHAR(511) NULL,  
  `instructions` VARCHAR(255) NOT NULL,
  `prescriptionStatus` ENUM('CREATED','PROCESSED','COMPLETED','CANCELLED') NOT NULL,
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
  `itemId` INT NOT NULL, 
  `prescriptionId` INT NOT NULL,
  `dosage` VARCHAR(50) NOT NULL,
  `frequency` VARCHAR(50) NOT NULL,
  `duration` VARCHAR(50) NOT NULL,
  `notes` VARCHAR(511) NOT NULL,
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
  `recordId` INT NOT NULL AUTO_INCREMENT,
  `patientId` VARCHAR(10) NOT NULL,
  `doctorId` VARCHAR(10) NOT NULL,
  `recordDate` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `diagnosis` VARCHAR(255) NOT NULL,
  `symptoms` VARCHAR(255) NOT NULL,
  `notes` VARCHAR(255) NULL,
  `attachments` VARCHAR(511) NOT NULL,
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
  `appointmentId` INT NOT NULL AUTO_INCREMENT,
  `patientId` VARCHAR(10) NOT NULL,
  `doctorId` VARCHAR(10) NOT NULL,
  `appointmentDate` DATETIME NOT NULL,
  `duration` INT NOT NULL,
  `reason` VARCHAR(255) NOT NULL,
  `appointmentStatus` ENUM('SCHEDULED', 'CANCELLED', 'COMPLETED', 'NOSHOW', 'INPROGRESS') NOT NULL, -- Using VARCHAR as per class diagram 'AppointmentStatus' (string representation)
  `queueNumber` INT NOT NULL,
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
  `userId` INT NOT NULL,
  `licenseNumber` VARCHAR(50) NOT NULL,
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
  `userId` INT NOT NULL,
  `department` VARCHAR(100) NULL,
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

-- Data Dummy

-- 1. User
INSERT INTO `mydb`.`User` (`username`, `password`, `email`, `phoneNumber`, `role`, `lastLogin`) VALUES
('dr.adams', 'pass123', 'adams.doc@example.com', '081234567890', 'DOCTOR', NOW()),
('dr.brown', 'pass123', 'brown.doc@example.com', '081234567891', 'DOCTOR', NOW()),
('patient.alice', 'pass123', 'alice.pat@example.com', '081234567892', 'PATIENT', NOW()),
('patient.bob', 'pass123', 'bob.pat@example.com', '081234567893', 'PATIENT', NOW()),
('patient.charlie', 'pass123', 'charlie.pat@example.com', '081234567894', 'PATIENT', NOW()),
('reception.diana', 'pass123', 'diana.rec@example.com', '081234567895', 'RECEPTIONIST', NOW()),
('pharma.edward', 'pass123', 'edward.phm@example.com', '081234567896', 'PHARMACIST', NOW()),
('admin.frank', 'pass123', 'frank.adm@example.com', '081234567897', 'ADMIN', NOW()),
('dr.grace', 'pass123', 'grace.doc@example.com', '081234567898', 'DOCTOR', NOW()),
('patient.hannah', 'pass123', 'hannah.pat@example.com', '081234567899', 'PATIENT', NOW());

-- Simpan userId yang relevan untuk kemudahan
-- Misal userId: 1 (dr.adams), 2 (dr.brown), 3 (patient.alice), 4 (patient.bob), 5 (patient.charlie),
-- 6 (reception.diana), 7 (pharma.edward), 8 (admin.frank), 9 (dr.grace), 10 (patient.hannah)

-- 2. Doctor (menggunakan userId dari User dengan role DOCTOR)
INSERT INTO `mydb`.`Doctor` (`doctorId`, `userId`, `specialization`, `licenseNumber`, `availableDays`, `availableHours`) VALUES
('DOC001', 1, 'Cardiology', 'LIC-CARD-001', 'Monday, Wednesday, Friday', '09:00-17:00'),
('DOC002', 2, 'Pediatrics', 'LIC-PED-002', 'Tuesday, Thursday', '10:00-18:00'),
('DOC003', 9, 'General Practitioner', 'LIC-GP-003', 'Monday-Friday', '08:00-16:00');
-- Tambahkan 7 dokter lagi jika ada 7 user dokter lagi. Untuk contoh, saya buat 3 dulu berdasarkan user di atas.

-- 3. Patient (menggunakan userId dari User dengan role PATIENT)
INSERT INTO `mydb`.`Patient` (`patientId`, `userId`, `bloodType`, `allergies`, `emergencyContact`, `insuranceInfo`, `registrationDate`) VALUES
('PAT001', 3, 'A', 'Pollen, Dust', '081111111111', 'Insurance XYZ Plan A', NOW()),
('PAT002', 4, 'O', NULL, '082222222222', 'Insurance ABC Plan B', NOW()),
('PAT003', 5, 'B', 'Peanuts', '083333333333', 'No Insurance', NOW()),
('PAT004', 10, 'AB', 'Shellfish', '084444444444', 'Insurance QRS Gold', NOW());
-- Tambahkan 6 pasien lagi jika ada 6 user pasien lagi.

-- 4. Pharmacist (menggunakan userId dari User dengan role PHARMACIST)
INSERT INTO `mydb`.`Pharmacist` (`pharmacistId`, `userId`, `licenseNumber`) VALUES
('PHM001', 7, 'LIC-PHM-001');
-- Tambahkan 9 apoteker lagi jika ada user apoteker lagi.

-- 5. Receptionist (menggunakan userId dari User dengan role RECEPTIONIST)
INSERT INTO `mydb`.`Receptionist` (`receptionistId`, `userId`, `department`) VALUES
('REC001', 6, 'Front Desk');
-- Tambahkan 9 resepsionis lagi jika ada user resepsionis lagi.

-- 6. Medication
INSERT INTO `mydb`.`Medication` (`name`, `genericName`, `category`, `unit`, `stockQuantity`, `minStockLevel`, `expiryDate`, `sideEffects`, `contraindication`) VALUES
('Amoxicillin 250mg', 'Amoxicillin', 'Antibiotic', 'Tablet', 200, 50, DATE_ADD(CURDATE(), INTERVAL 2 YEAR), 'Nausea, Rash', 'Penicillin Allergy'),
('Paracetamol 500mg', 'Acetaminophen', 'Analgesic', 'Tablet', 500, 100, DATE_ADD(CURDATE(), INTERVAL 3 YEAR), 'Rare liver damage with overdose', 'Severe liver disease'),
('Ibuprofen 200mg', 'Ibuprofen', 'NSAID', 'Tablet', 300, 70, DATE_ADD(CURDATE(), INTERVAL 2 YEAR), 'Stomach upset', 'Active peptic ulcer'),
('Loratadine 10mg', 'Loratadine', 'Antihistamine', 'Tablet', 150, 30, DATE_ADD(CURDATE(), INTERVAL 1 YEAR), 'Drowsiness (rare)', 'Hypersensitivity'),
('Omeprazole 20mg', 'Omeprazole', 'Proton Pump Inhibitor', 'Capsule', 100, 20, DATE_ADD(CURDATE(), INTERVAL 2 YEAR), 'Headache, Diarrhea', 'Clopidogrel co-administration'),
('Metformin 500mg', 'Metformin', 'Antidiabetic', 'Tablet', 250, 50, DATE_ADD(CURDATE(), INTERVAL 3 YEAR), 'Gastrointestinal upset', 'Renal impairment'),
('Salbutamol Inhaler', 'Salbutamol', 'Bronchodilator', 'Inhaler', 80, 15, DATE_ADD(CURDATE(), INTERVAL 1 YEAR), 'Tremor, Palpitations', 'Cardiac arrhythmias'),
('Amlodipine 5mg', 'Amlodipine', 'Calcium Channel Blocker', 'Tablet', 180, 40, DATE_ADD(CURDATE(), INTERVAL 2 YEAR), 'Edema, Dizziness', 'Severe hypotension'),
('Vitamin C 500mg', 'Ascorbic Acid', 'Vitamin', 'Tablet', 400, 100, DATE_ADD(CURDATE(), INTERVAL 1 YEAR), 'High doses: Diarrhea', 'Kidney stones history'),
('Cough Syrup DXM', 'Dextromethorphan', 'Antitussive', 'Syrup', 120, 25, DATE_ADD(CURDATE(), INTERVAL 18 MONTH), 'Dizziness, Drowsiness', 'MAOI use');

-- 7. Prescription (menggunakan patientId dan doctorId yang sudah ada)
-- Asumsi patientId: PAT001, PAT002, PAT003, PAT004
-- Asumsi doctorId: DOC001, DOC002, DOC003
INSERT INTO `mydb`.`Prescription` (`patientId`, `doctorId`, `prescriptionDate`, `instructions`, `prescriptionStatus`, `medications`) VALUES
('PAT001', 'DOC001', NOW(), 'Take one tablet three times a day for 7 days.', 'CREATED', 'Amoxicillin 250mg'),
('PAT002', 'DOC002', NOW(), 'Take one tablet as needed for pain, max 4 per day.', 'CREATED', 'Paracetamol 500mg'),
('PAT003', 'DOC001', NOW(), 'Take one tablet twice a day after meals.', 'PROCESSED', 'Ibuprofen 200mg'),
('PAT001', 'DOC003', DATE_SUB(NOW(), INTERVAL 1 DAY), 'One tablet daily in the morning.', 'COMPLETED', 'Loratadine 10mg'),
('PAT004', 'DOC002', NOW(), 'One capsule before breakfast for 14 days.', 'CREATED', 'Omeprazole 20mg'),
('PAT002', 'DOC001', DATE_SUB(NOW(), INTERVAL 2 DAY), 'One tablet with meals, twice daily.', 'COMPLETED', 'Metformin 500mg'),
('PAT003', 'DOC003', NOW(), 'Inhale two puffs when needed.', 'PROCESSED', 'Salbutamol Inhaler'),
('PAT004', 'DOC001', DATE_SUB(NOW(), INTERVAL 3 DAY), 'One tablet daily.', 'CANCELLED', 'Amlodipine 5mg'),
('PAT001', 'DOC002', NOW(), 'One tablet daily for immune support.', 'CREATED', 'Vitamin C 500mg'),
('PAT002', 'DOC003', DATE_SUB(NOW(), INTERVAL 1 DAY), '10ml every 6 hours as needed for cough.', 'COMPLETED', 'Cough Syrup DXM');

-- Simpan prescriptionId yang relevan: 1, 2, 3, 4, 5, 6, 7, 8, 9, 10

-- 8. PrescriptionItem (menggunakan prescriptionId dan medicationId yang sudah ada)
-- Asumsi medicationId: 1 (Amox), 2 (Para), 3 (Ibu), 4 (Lora), 5 (Omep), 6 (Metf), 7 (Salb), 8 (Amlo), 9 (VitC), 10 (CoughS)
INSERT INTO `mydb`.`PrescriptionItem` (`prescriptionId`, `itemId`, `dosage`, `frequency`, `duration`, `notes`) VALUES
(1, 1, '250mg', '3 times a day', '7 days', 'Finish the course.'),
(2, 2, '500mg', 'As needed, max 4/day', 'Until pain subsides', 'Do not exceed maximum dose.'),
(3, 3, '200mg', '2 times a day', '5 days', 'Take with food.'),
(4, 4, '10mg', '1 time a day', '30 days', 'For allergies.'),
(5, 5, '20mg', '1 time a day', '14 days', 'Before breakfast.'),
(6, 6, '500mg', '2 times a day', 'Ongoing', 'Monitor blood sugar.'),
(7, 7, '2 puffs', 'As needed', 'As needed', 'For asthma attacks.'),
-- Prescription 8 was cancelled, so no items or items could be marked as not dispensed. For simplicity, not adding items for cancelled one.
(9, 9, '500mg', '1 time a day', '60 days', 'Boost immunity.'),
(10, 10, '10ml', 'Every 6 hours', '3 days', 'For dry cough.');
-- Tambahkan item untuk prescriptionId lainnya jika diperlukan

-- 9. MedicalRecord
INSERT INTO `mydb`.`MedicalRecord` (`patientId`, `doctorId`, `recordDate`, `diagnosis`, `symptoms`, `notes`, `attachments`) VALUES
('PAT001', 'DOC001', NOW(), 'Bronchitis', 'Cough, Fever, Shortness of breath', 'Prescribed Amoxicillin.', 'chest_xray_ref_001.url'),
('PAT002', 'DOC002', NOW(), 'Mild Headache', 'Headache, No fever', 'Advised Paracetamol.', 'none'),
('PAT003', 'DOC001', DATE_SUB(NOW(), INTERVAL 1 DAY), 'Muscle Strain', 'Pain in shoulder', 'Prescribed Ibuprofen, rest.', 'physio_referral.pdf'),
('PAT001', 'DOC003', DATE_SUB(NOW(), INTERVAL 2 DAY), 'Allergic Rhinitis', 'Sneezing, Runny nose', 'Prescribed Loratadine.', 'allergy_test_results.url'),
('PAT004', 'DOC002', NOW(), 'Acid Reflux', 'Heartburn, Indigestion', 'Prescribed Omeprazole.', 'endoscopy_notes_prev.doc'),
('PAT002', 'DOC001', DATE_SUB(NOW(), INTERVAL 3 DAY), 'Type 2 Diabetes', 'Increased thirst, Frequent urination', 'Continuing Metformin.', 'blood_sugar_chart.xls'),
('PAT003', 'DOC003', NOW(), 'Asthma Exacerbation', 'Wheezing, Difficulty breathing', 'Salbutamol inhaler provided.', 'spirometry_report.pdf'),
('PAT004', 'DOC001', DATE_SUB(NOW(), INTERVAL 4 DAY), 'Hypertension Follow-up', 'Slightly elevated BP', 'Monitor BP, lifestyle changes advised.', 'bp_log.csv'),
('PAT001', 'DOC002', NOW(), 'General Checkup - Advised Vitamins', 'Feeling tired', 'Vitamin C recommended.', 'none'),
('PAT002', 'DOC003', DATE_SUB(NOW(), INTERVAL 1 DAY), 'Common Cold with Cough', 'Cough, Sore throat', 'Prescribed cough syrup.', 'throat_swab_neg.txt');

-- 10. Appointment
INSERT INTO `mydb`.`Appointment` (`patientId`, `doctorId`, `appointmentDate`, `duration`, `reason`, `appointmentStatus`, `queueNumber`) VALUES
('PAT001', 'DOC001', DATE_ADD(NOW(), INTERVAL 1 DAY), 30, 'Follow-up consultation', 'SCHEDULED', 1),
('PAT002', 'DOC002', DATE_ADD(NOW(), INTERVAL 1 DAY), 20, 'Pediatric checkup', 'SCHEDULED', 2),
('PAT003', 'DOC001', DATE_ADD(NOW(), INTERVAL 2 DAY), 30, 'Shoulder pain assessment', 'SCHEDULED', 1),
('PAT001', 'DOC003', DATE_ADD(NOW(), INTERVAL 2 DAY), 15, 'Allergy review', 'SCHEDULED', 3),
('PAT004', 'DOC002', DATE_ADD(NOW(), INTERVAL 3 DAY), 30, 'Digestive issues', 'SCHEDULED', 1),
('PAT002', 'DOC001', DATE_SUB(NOW(), INTERVAL 1 DAY), 25, 'Diabetes management', 'COMPLETED', 5),
('PAT003', 'DOC003', DATE_SUB(NOW(), INTERVAL 1 HOUR), 20, 'Breathing difficulty', 'INPROGRESS', 10),
('PAT004', 'DOC001', DATE_SUB(NOW(), INTERVAL 2 DAY), 15, 'BP check', 'COMPLETED', 7),
('PAT001', 'DOC002', DATE_ADD(NOW(), INTERVAL 4 DAY), 30, 'General health query', 'SCHEDULED', 4),
('PAT002', 'DOC003', DATE_SUB(NOW(), INTERVAL 0 DAY), 20, 'Cough not improving', 'COMPLETED', 12);