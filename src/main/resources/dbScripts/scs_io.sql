CREATE TABLE IF NOT EXISTS `scs_packing_list` 
( `packing_list_id` INTEGER NOT NULL PRIMARY KEY,
  `sales_order` VARCHAR(20) NOT NULL,
  `packing_list` VARCHAR(50) NOT NULL,
  `delivery_date` DATE NOT NULL,
  `hold` BIT NOT NULL DEFAULT 0
);
CREATE TABLE IF NOT EXISTS `mtbt_out` 
( `id` INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `category` VARCHAR(20),
  `bug_id` INTEGER NOT NULL,
  `packing_list_id` INTEGER,
  `sales_order` VARCHAR(20) NOT NULL,
  `packing_list` VARCHAR(50) NOT NULL,
  `delivery_date` DATE,
  `hold` BIT NOT NULL DEFAULT 0,
  `log` VARCHAR(250),
  `transfering` BIT NOT NULL DEFAULT 0,
  `transfered` BIT NOT NULL DEFAULT 0,
  `transfer_started_when` DATE
);
