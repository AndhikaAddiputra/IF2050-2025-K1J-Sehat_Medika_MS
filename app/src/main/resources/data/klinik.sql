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
  `userId` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `username` VARCHAR(50) NOT NULL,
  `password` VARCHAR(50) NOT NULL,
  `email` VARCHAR(255) NOT NULL,
  `phoneNumber` VARCHAR(25) NOT NULL,
  `fullName` VARCHAR(255) NOT NULL,
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
  `userId` INT UNSIGNED NOT NULL,
  `salaryDoctor` INT NOT NULL,
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
  `userId` INT UNSIGNED NOT NULL,
  `bloodType` ENUM('A', 'B','AB' ,'O') NOT NULL, 
  `allergies` VARCHAR(255) NULL,
  `height` INT NOT NULL,
  `weight`INT NOT NULL,
  `emergencyContact` VARCHAR(25) NULL,
  `insuranceInfo` VARCHAR(255) NULL,
  `insuranceNumber` VARCHAR(15) NOT NULL,
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
  `treatment` VARCHAR(255) NULL, 
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
  `appointmentStatus` ENUM('REQUESTED', 'ACCEPTED') NOT NULL, -- Using VARCHAR as per class diagram 'AppointmentStatus' (string representation)
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
  `userId` INT UNSIGNED NOT NULL,
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
  `userId` INT UNSIGNED NOT NULL,
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

-- 1. User (Total 10 User)
-- Distribusi Peran:
-- User 1-2: DOCTOR
-- User 3-5: PATIENT
-- User 6: RECEPTIONIST
-- User 7: PHARMACIST
-- User 8: ADMIN
-- User 9: DOCTOR (Total 3 dokter)
-- User 10: PATIENT (Total 4 pasien)

INSERT INTO `User` (`userId`, `username`, `password`, `email`, `phoneNumber`,`fullName`, `role`, `lastLogin`) VALUES
(1, 'dr.house', 'pass_doc1', 'dr.house@example.com', '081234567001','James House', 'DOCTOR', NOW()),
(2, 'dr.grey', 'pass_doc2', 'dr.grey@example.com', '081234567002', 'Shin Grey','DOCTOR', NOW()),
(3, 'john.doe', 'pass_pat1', 'john.doe@example.com', '081234567003', 'John Doe', 'PATIENT', NOW()),
(4, 'jane.roe', 'pass_pat2', 'jane.roe@example.com', '081234567004', 'Jane Roe', 'PATIENT', NOW()),
(5, 'peter.pan', 'pass_pat3', 'peter.pan@example.com', '081234567005', 'Peter Pan', 'PATIENT', NOW()),
(6, 'susan.staff', 'pass_rec1', 'susan.staff@example.com', '081234567006', 'Susan Blouse', 'RECEPTIONIST', NOW()),
(7, 'walter.white', 'pass_phm1', 'walter.white@example.com', '081234567007', 'Walter White', 'PHARMACIST', NOW()),
(8, 'admin.boss', 'pass_adm1', 'admin.boss@example.com', '081234567008', 'Admin Dhika', 'ADMIN', NOW()),
(9, 'dr.strange', 'pass_doc3', 'dr.strange@example.com', '081234567009', 'Athar Strange', 'DOCTOR', NOW()),
(10, 'mary.jane', 'pass_pat4', 'mary.jane@example.com', '081234567010', 'Mary Jane', 'PATIENT', NOW());

-- 2. Doctor (Total 3 Dokter, berdasarkan User ID 1, 2, 9)
INSERT INTO `Doctor` (`doctorId`, `userId`, `salaryDoctor`, `specialization`, `licenseNumber`, `availableDays`, `availableHours`) VALUES
('DOC001', 1, 85000000, 'Diagnostics', 'LIC-DIAG-001', 'Monday, Tuesday, Wednesday', '08:00-16:00'),
('DOC002', 2, 90000000, 'Surgery', 'LIC-SURG-002', 'Thursday, Friday', '09:00-17:00'),
('DOC003', 9, 95000000, 'Neurosurgery', 'LIC-NEURO-003', 'Monday, Wednesday, Friday', '10:00-18:00');

-- 3. Patient (Total 4 Pasien, berdasarkan User ID 3, 4, 5, 10)
INSERT INTO `Patient` (`patientId`, `userId`, `bloodType`, `allergies`, `height`, `weight`, `emergencyContact`, `insuranceInfo`, `insuranceNumber`, `registrationDate`) VALUES
('PAT001', 3, 'O', 'Pollen', '182', '78', '081111000001', 'Alpha Insurance Plan Gold', '102983624825', NOW()),
('PAT002', 4, 'A', 'None', '155', '45', '081111000002', 'Beta Insurance Plan Silver', '184056244390', NOW()),
('PAT003', 5, 'B', 'Peanuts, Dust Mites', '168', '59', '081111000003', 'Gamma Corp Coverage', '152066346502', NOW()),
('PAT004', 10, 'AB', 'Shellfish', '157', '49', '081111000004', 'No Insurance', '1850275638591', NOW());

-- 4. Pharmacist (Total 1 Apoteker, berdasarkan User ID 7)
INSERT INTO `Pharmacist` (`pharmacistId`, `userId`, `licenseNumber`) VALUES
('PHM001', 7, 'LIC-PHARM-XYZ123');

-- 5. Receptionist (Total 1 Resepsionis, berdasarkan User ID 6)
INSERT INTO `Receptionist` (`receptionistId`, `userId`, `department`) VALUES
('REC001', 6, 'Front Desk & Appointments');

-- 6. Medication (10 item obat)
INSERT INTO `Medication` (`name`, `genericName`, `category`, `unit`, `stockQuantity`, `minStockLevel`, `expiryDate`, `sideEffects`, `contraindication`) VALUES
('Vicodin', 'Hydrocodone/Acetaminophen', 'Opioid Analgesic', 'Tablet', 150, 30, DATE_ADD(NOW(), INTERVAL 1 YEAR), 'Drowsiness, Constipation', 'Severe respiratory depression'),
('Amoxicillin 500mg', 'Amoxicillin', 'Antibiotic', 'Capsule', 400, 100, DATE_ADD(NOW(), INTERVAL 2 YEAR), 'Nausea, Diarrhea', 'Penicillin allergy'),
('Lisinopril 10mg', 'Lisinopril', 'ACE Inhibitor', 'Tablet', 300, 50, DATE_ADD(NOW(), INTERVAL 18 MONTH), 'Cough, Dizziness', 'Angioedema history'),
('Metformin 850mg', 'Metformin', 'Antidiabetic', 'Tablet', 250, 50, DATE_ADD(NOW(), INTERVAL 2 YEAR), 'GI upset, Lactic acidosis (rare)', 'Severe renal impairment'),
('Atorvastatin 20mg', 'Atorvastatin', 'Statin', 'Tablet', 200, 40, DATE_ADD(NOW(), INTERVAL 3 YEAR), 'Muscle pain, Liver enzyme increase', 'Active liver disease'),
('Albuterol Inhaler', 'Salbutamol', 'Bronchodilator', 'Inhaler', 100, 20, DATE_ADD(NOW(), INTERVAL 1 YEAR), 'Tremor, Tachycardia', 'Hypersensitivity'),
('Omeprazole 40mg', 'Omeprazole', 'Proton Pump Inhibitor', 'Capsule', 180, 30, DATE_ADD(NOW(), INTERVAL 2 YEAR), 'Headache, Abdominal pain', 'Clopidogrel interaction'),
('Sertraline 50mg', 'Sertraline', 'SSRI Antidepressant', 'Tablet', 120, 25, DATE_ADD(NOW(), INTERVAL 1 YEAR), 'Nausea, Insomnia', 'MAOI use within 14 days'),
('Warfarin 5mg', 'Warfarin', 'Anticoagulant', 'Tablet', 90, 15, DATE_ADD(NOW(), INTERVAL 6 MONTH), 'Bleeding, Bruising', 'Active major bleeding'),
('Furosemide 40mg', 'Furosemide', 'Loop Diuretic', 'Tablet', 220, 45, DATE_ADD(NOW(), INTERVAL 2 YEAR), 'Dehydration, Electrolyte imbalance', 'Anuria');

-- 7. Prescription (Maksimal 10 resep, menggunakan dokter dan pasien yang ada)
-- Menggunakan DOC001, DOC002, DOC003 dan PAT001, PAT002, PAT003, PAT004
INSERT INTO `Prescription` (`patientId`, `doctorId`, `prescriptionDate`, `medications`, `instructions`) VALUES
('PAT001', 'DOC001', NOW(), 'Vicodin, Amoxicillin 500mg', 'Vicodin: 1 tab q6h prn pain. Amoxicillin: 1 cap tid for 7 days.'),
('PAT002', 'DOC002', DATE_SUB(NOW(), INTERVAL 1 DAY), 'Lisinopril 10mg', '1 tab daily in the morning.'),
('PAT003', 'DOC001', DATE_SUB(NOW(), INTERVAL 2 DAY), 'Metformin 850mg, Atorvastatin 20mg', 'Metformin: 1 tab bid with meals. Atorvastatin: 1 tab daily at bedtime.'),
('PAT004', 'DOC003', DATE_SUB(NOW(), INTERVAL 3 DAY), 'Albuterol Inhaler', '2 puffs q4-6h prn shortness of breath.'),
('PAT001', 'DOC002', DATE_SUB(NOW(), INTERVAL 4 DAY), 'Omeprazole 40mg', '1 cap daily before breakfast for 4 weeks.'),
('PAT002', 'DOC001', DATE_SUB(NOW(), INTERVAL 5 DAY), 'Sertraline 50mg', '1 tab daily, may increase after 1 week if needed.'),
('PAT003', 'DOC003', DATE_SUB(NOW(), INTERVAL 6 DAY), 'Warfarin 5mg', '1 tab daily, monitor INR closely.'),
('PAT004', 'DOC001', DATE_SUB(NOW(), INTERVAL 7 DAY), 'Furosemide 40mg', '1 tab daily in the morning, monitor electrolytes.'),
('PAT001', 'DOC003', DATE_SUB(NOW(), INTERVAL 8 DAY), 'Amoxicillin 500mg (repeat)', '1 cap tid for 10 days for recurrent infection.'),
('PAT002', 'DOC002', DATE_SUB(NOW(), INTERVAL 9 DAY), 'Vicodin (for acute pain)', '1-2 tabs q4-6h prn, max 8 tabs/day for 3 days only.');

-- 8. PrescriptionItem
-- Asumsi medicationId: 1(Vicodin), 2(Amox), 3(Lisinopril), 4(Metformin), 5(Atorvastatin), 6(Albuterol), 7(Omeprazole), 8(Sertraline), 9(Warfarin), 10(Furosemide)
-- Asumsi prescriptionId akan di-generate otomatis: 1, 2, 3, ...
INSERT INTO `PrescriptionItem` (`prescriptionId`, `itemId`, `dosage`, `frequency`, `duration`, `notes`) VALUES
(1, 1, '1 tablet', 'q6h prn pain', 'As needed', 'May cause drowsiness.'),
(1, 2, '500mg', '3 times a day', '7 days', 'Finish all medication.'),
(2, 3, '10mg', 'Once daily (morning)', '30 days', 'Monitor blood pressure.'),
(3, 4, '850mg', 'Twice daily (with meals)', 'Ongoing', 'Check kidney function regularly.'),
(3, 5, '20mg', 'Once daily (bedtime)', 'Ongoing', 'Monitor liver enzymes.'),
(4, 6, '2 puffs', 'q4-6h prn', 'As needed', 'For asthma attacks.'),
(5, 7, '40mg', 'Once daily (before breakfast)', '4 weeks', 'For acid reflux.'),
(6, 8, '50mg', 'Once daily', 'Ongoing', 'May take weeks to see full effect.'),
(7, 9, '5mg', 'Once daily (evening)', 'As directed', 'Requires frequent INR monitoring.'),
(8, 10, '40mg', 'Once daily (morning)', 'As needed', 'Monitor for dehydration.'),
(9, 2, '500mg', '3 times a day', '10 days', 'For persistent infection.'), -- Item untuk resep ke-9
(10, 1, '1-2 tablets', 'q4-6h prn (max 8/day)', '3 days', 'For severe acute back pain.'); -- Item untuk resep ke-10


-- 9. MedicalRecord (Maksimal 10, menggunakan dokter dan pasien yang ada)
INSERT INTO `MedicalRecord` (`patientId`, `doctorId`, `recordDate`, `diagnosis`, `symptoms`, `treatment`, `notes`, `attachments`) VALUES
('PAT001', 'DOC001', NOW(), 'Acute Bronchitis with Post-Operative Pain', 'Cough, chest pain, surgical site pain','Prescription', 'Prescribed Vicodin for pain, Amoxicillin for suspected infection.', 'post_op_summary.pdf, chest_scan.dicom'),
('PAT002', 'DOC002', DATE_SUB(NOW(), INTERVAL 1 DAY), 'Hypertension Stage 2', 'Headaches, high BP readings','Prescription' ,'Started Lisinopril. Lifestyle modification advised.', 'bp_chart.png, ecg_report.pdf'),
('PAT003', 'DOC001', DATE_SUB(NOW(), INTERVAL 2 DAY), 'Type 2 Diabetes, Hyperlipidemia', 'Fatigue, high blood sugar & cholesterol', 'Prescription', 'Metformin and Atorvastatin initiated.', 'lab_results_full.pdf'),
('PAT004', 'DOC003', DATE_SUB(NOW(), INTERVAL 3 DAY), 'Asthma, Moderate Persistent', 'Wheezing, shortness of breath episodes','CT-Scan, Prescription', 'Albuterol inhaler for rescue. Discussed controller meds.', 'spirometry.pdf'),
('PAT001', 'DOC002', DATE_SUB(NOW(), INTERVAL 4 DAY), 'GERD', 'Heartburn, regurgitation','Prescription, Bedrest', 'Prescribed Omeprazole. Dietary advice given.', 'gastroscopy_notes.txt'),
('PAT002', 'DOC001', DATE_SUB(NOW(), INTERVAL 5 DAY), 'Major Depressive Disorder', 'Low mood, anhedonia, sleep disturbance','Prescription', 'Started Sertraline. Therapy referral.', 'phq9_score.pdf'),
('PAT003', 'DOC003', DATE_SUB(NOW(), INTERVAL 6 DAY), 'Atrial Fibrillation - Anticoagulation Mngmt', 'Irregular heartbeat, starting Warfarin','Prescription', 'Counseled on Warfarin, INR schedule.', 'echo_report.pdf, inr_target.doc'),
('PAT004', 'DOC001', DATE_SUB(NOW(), INTERVAL 7 DAY), 'Edema, Suspected Heart Failure', 'Swollen ankles, shortness of breath', 'Prescription','Prescribed Furosemide. Further cardiac workup.', 'cardiac_referral.pdf'),
('PAT001', 'DOC003', DATE_SUB(NOW(), INTERVAL 8 DAY), 'Recurrent Sinusitis', 'Facial pain, nasal congestion', 'Prescription','Repeat course of Amoxicillin.', 'ct_sinus_prev.dicom'),
('PAT002', 'DOC002', DATE_SUB(NOW(), INTERVAL 9 DAY), 'Acute Lumbago', 'Severe lower back pain after lifting', 'Prescription','Prescribed short course of Vicodin. Advised rest.', 'mri_lumbar_request.pdf');

-- 10. Appointment (Maksimal 10, menggunakan dokter dan pasien yang ada. doctorConfirmation diasumsikan TINYINT(1))
INSERT INTO `Appointment` (`patientId`, `doctorId`, `appointmentDate`, `duration`, `reason`, `appointmentStatus`, `queueNumber`) VALUES
('PAT001', 'DOC001', DATE_ADD(NOW(), INTERVAL 1 DAY), 30, 'Follow-up post-op & bronchitis', 'REQUESTED', 1),
('PAT002', 'DOC002', DATE_ADD(NOW(), INTERVAL 1 DAY), 20, 'BP check & Lisinopril review', 'REQUESTED', 2),
('PAT003', 'DOC001', DATE_ADD(NOW(), INTERVAL 2 DAY), 45, 'Diabetes & Cholesterol management', 'REQUESTED', 1),
('PAT004', 'DOC003', DATE_ADD(NOW(), INTERVAL 2 DAY), 30, 'Asthma action plan review', 'REQUESTED', 2),
('PAT001', 'DOC002', DATE_ADD(NOW(), INTERVAL 3 DAY), 20, 'GERD symptom check', 'REQUESTED', 1),
('PAT002', 'DOC001', DATE_SUB(NOW(), INTERVAL -1 HOUR), 40, 'Psychiatry follow-up (Sertraline)', 'REQUESTED', 5),
('PAT003', 'DOC003', DATE_SUB(NOW(), INTERVAL -2 DAY), 15, 'INR check for Warfarin', 'ACCEPTED', 3),
('PAT004', 'DOC001', DATE_ADD(NOW(), INTERVAL 4 DAY), 30, 'Edema follow-up & cardiac results', 'REQUESTED', 1),
('PAT001', 'DOC003', DATE_ADD(NOW(), INTERVAL 5 DAY), 25, 'Sinusitis follow-up', 'REQUESTED', 1),
('PAT002', 'DOC002', DATE_SUB(NOW(), INTERVAL -1 DAY), 20, 'Acute back pain evaluation', 'ACCEPTED', 6);