-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';

-- -----------------------------------------------------
-- Schema risks
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema risks
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `risks` DEFAULT CHARACTER SET utf8 ;
USE `risks` ;

-- -----------------------------------------------------
-- Table `risks`.`employee`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `risks`.`employee` (
  `employee_id` INT NOT NULL AUTO_INCREMENT,
  `first_name` VARCHAR(45) NOT NULL,
  `last_name` VARCHAR(45) NOT NULL,
  `company_name` VARCHAR(45) NOT NULL,
  `telephone` VARCHAR(45) NULL,
  PRIMARY KEY (`employee_id`),
  UNIQUE INDEX `employee_id_UNIQUE` (`employee_id` ASC))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `risks`.`contact`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `risks`.`contact` (
  `contact_id` INT NOT NULL AUTO_INCREMENT,
  `first_name` VARCHAR(45) NOT NULL,
  `last_name` VARCHAR(45) NOT NULL,
  `email` VARCHAR(45) NOT NULL,
  `password` VARCHAR(45) NOT NULL,
  `role` VARCHAR(45) NULL,
  PRIMARY KEY (`contact_id`),
  UNIQUE INDEX `contact_id_UNIQUE` (`contact_id` ASC),
  UNIQUE INDEX `email_UNIQUE` (`email` ASC))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `risks`.`employer`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `risks`.`employer` (
  `employer_id` INT NOT NULL AUTO_INCREMENT,
  `first_name` VARCHAR(45) NOT NULL,
  `last_name` VARCHAR(45) NOT NULL,
  `person_type` VARCHAR(45) NOT NULL,
  `company_name` VARCHAR(45) NOT NULL,
  `telephone` VARCHAR(45) NULL,
  `address` VARCHAR(100) NULL,
  `scope_of_work` VARCHAR(45) NULL,
  `passport_number` VARCHAR(45) NULL,
  PRIMARY KEY (`employer_id`),
  UNIQUE INDEX `passport_number_UNIQUE` (`passport_number` ASC))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `risks`.`job`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `risks`.`job` (
  `job_id` INT NOT NULL AUTO_INCREMENT,
  `job_type` VARCHAR(45) NULL,
  `title` VARCHAR(100) NOT NULL,
  `description` VARCHAR(200) NULL,
  `duration_in_days` INT NULL,
  `responsible_employee_id` INT NULL,
  PRIMARY KEY (`job_id`),
  INDEX `fk_job_employee1_idx` (`responsible_employee_id` ASC),
--  CONSTRAINT `fk_job_employee1`
--    FOREIGN KEY (`responsible_employee_id`)
--    REFERENCES `risks`.`employee` (`employee_id`)
--    ON DELETE NO ACTION
--    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `risks`.`project`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `risks`.`project` (
  `project_id` INT NOT NULL AUTO_INCREMENT,
  `title` VARCHAR(45) NOT NULL,
  `start_date` DATETIME NULL,
  `end_date` DATETIME NULL,
  `contact_id` INT NOT NULL,
  `employer_id` INT NOT NULL,
  PRIMARY KEY (`project_id`),
  INDEX `fk_project_contact_idx` (`contact_id` ASC),
  INDEX `fk_project_employer1_idx` (`employer_id` ASC),
  CONSTRAINT `fk_project_contact`
    FOREIGN KEY (`contact_id`)
    REFERENCES `risks`.`contact` (`contact_id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_project_employer1`
    FOREIGN KEY (`employer_id`)
    REFERENCES `risks`.`employer` (`employer_id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `risks`.`project_has_job`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `risks`.`project_has_job` (
  `project_project_id` INT NOT NULL,
  `job_job_id` INT NOT NULL,
  PRIMARY KEY (`project_project_id`, `job_job_id`),
  INDEX `fk_project_has_job_job1_idx` (`job_job_id` ASC),
  INDEX `fk_project_has_job_project1_idx` (`project_project_id` ASC),
  CONSTRAINT `fk_project_has_job_project1`
    FOREIGN KEY (`project_project_id`)
    REFERENCES `risks`.`project` (`project_id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_project_has_job_job1`
    FOREIGN KEY (`job_job_id`)
    REFERENCES `risks`.`job` (`job_id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;

-- -----------------------------------------------------
-- Data for table `risks`.`employee`
-- -----------------------------------------------------
START TRANSACTION;
USE `risks`;
INSERT INTO `risks`.`employee` (`employee_id`, `first_name`, `last_name`, `company_name`, `telephone`) VALUES (1, 'emp1', 'emp1ln', 'company1', '+375291234567');

COMMIT;


-- -----------------------------------------------------
-- Data for table `risks`.`contact`
-- -----------------------------------------------------
START TRANSACTION;
USE `risks`;
INSERT INTO `risks`.`contact` (`contact_id`, `first_name`, `last_name`, `email`, `password`, `role`) VALUES (1, 'Ivan', 'Ivanov', 'ivan@mail.ru', '1111', 'ADMIN');

COMMIT;


-- -----------------------------------------------------
-- Data for table `risks`.`employer`
-- -----------------------------------------------------
START TRANSACTION;
USE `risks`;
INSERT INTO `risks`.`employer` (`employer_id`, `first_name`, `last_name`, `person_type`, `company_name`, `telephone`, `address`, `scope_of_work`, `passport_number`) VALUES (1, 'boss1', 'boss1', 'INDIVIDUAL', 'company1', '+3752900000000', 'address', 'it', 'MP1234567');

COMMIT;

