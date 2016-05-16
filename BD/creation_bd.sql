-- PRO Groupe 3 --
-- Steiner / Zharkova / Ciani /Rohrer /Selimi
-- Parc informatique

DROP SCHEMA IF EXISTS inventory;
CREATE SCHEMA inventory;
USE inventory;
             
                       
CREATE TABLE program (
    ID INT NOT NULL AUTO_INCREMENT,
    Name  VARCHAR(100) NOT NULL,
    Version VARCHAR(100) NOT NULL,
    PRIMARY KEY(ID)
);



CREATE TABLE processor (
	 ID INT NOT NULL AUTO_INCREMENT,
     Constructor VARCHAR(100) NOT NULL,
     Frequency DECIMAL(4,2) NOT NULL,
     Model VARCHAR(100) NOT NULL,
     NmbrCores INT (2) NOT NULL,
     PRIMARY KEY(ID)
);
               

CREATE TABLE machineState (
    MacAddress VARCHAR(30) NOT NULL,
    CaptureTime DATETIME NOT NULL, 
    HostName VARCHAR (100) NOT NULL,
    OS VARCHAR(100) NOT NULL,
    ProcessorID INT(30) NOT NULL,
    TotalRAM INT NOT NULL,
    IpAddress VARCHAR(30) NOT NULL,
    TotalHardDriveSize DECIMAL(6,2) NOT NULL,
    FreeHardDriveSize DECIMAL(6,2) NOT NULL,
    DeleteDate DATE,
	PRIMARY KEY (MacAddress, CaptureTime)
);


    
CREATE TABLE pc_program (
	pcMacAddress VARCHAR(30) NOT NULL,
	pcCaptureTime DATETIME NOT NULL,
	programID INT NOT NULL 
);
   
    

ALTER TABLE machineState
    ADD FOREIGN KEY (ProcessorID)
        REFERENCES  processor(ID);
ALTER TABLE pc_program
	ADD FOREIGN KEY (pcMacAddress, pcCaptureTime)
		REFERENCES machineState(MacAddress, CaptureTime) ON DELETE CASCADE,
	ADD FOREIGN KEY (programID)
		REFERENCES program(ID);
        
        
