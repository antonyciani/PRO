USE inventory;

-- delete all enteries which are "older" than 1 year
DROP EVENT IF EXISTS Delete_Info
DELIMITER |
CREATE EVENT Delete_Info
ON SCHEDULE EVERY 1 DAY 
DO 
 BEGIN 
 DELETE FROM capture 
 WHERE (CURRENT_TIMESTAMP - DateCapture) > 360; 
 END
 |
 DELIMITER ;
 
