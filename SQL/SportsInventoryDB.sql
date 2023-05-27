CREATE DATABASE  IF NOT EXISTS `sportsinventory`;
USE `sportsinventory`;

DROP TABLE IF EXISTS `users`;
CREATE TABLE `users` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(45) NOT NULL,
  `location` varchar(45) NOT NULL,
  `phone` varchar(10) NOT NULL,
  `username` varchar(20) NOT NULL,
  `password` varchar(200) NOT NULL,
  `usertype` varchar(45) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=31 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

LOCK TABLES `users` WRITE;
INSERT INTO `users` VALUES (17,'Sebastian Romero','Ocaña','1245124','sebastian','vinuni','ADMINISTRATOR'),(18,'Strix','Tonpheung','827394912','strix','strix','EMPLOYEE'),(19,'Hung','Hanoi','9876543210','hung','hieu','ADMINISTRATOR'),(20,'Trial Employee','Local','42341415','emp','emp','EMPLOYEE');
UNLOCK TABLES;

DROP TABLE IF EXISTS `sports`;
CREATE TABLE `sports` (
  `sportid` int NOT NULL AUTO_INCREMENT,
  `sportname` varchar(45) NOT NULL,
  PRIMARY KEY (`sportid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

LOCK TABLES `sports` WRITE;
INSERT INTO `sports` VALUES (1, 'volleyball'),(2, 'basketball'),(3, 'badminton'),(4, 'table tennis'),(5, 'pool'),(6, 'soccer'),(7, 'ultimate');
UNLOCK TABLES;

DROP TABLE IF EXISTS `items`;
CREATE TABLE `items` (
  `itemid` int NOT NULL AUTO_INCREMENT,
  `sportid` int NOT NULL,
  `itemcode` varchar(45) NOT NULL,
  `itemname` varchar(45) NOT NULL,
  `costprice` double NOT NULL,
  `description` varchar(45) NOT NULL,
  PRIMARY KEY (`itemid`),
  UNIQUE KEY `itemid_UNIQUE` (`itemid`),
  FOREIGN KEY (sportid)
      REFERENCES sports(sportid)
      ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB AUTO_INCREMENT=130 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

LOCK TABLES `items` WRITE;
INSERT INTO `items` VALUES (111, 1, 'KIAD123','voleyball',300000,'ball'),(112, 2, 'KIAF123','basketball',400000,'ball'),(113, 3, 'KJAD143','racket',85000,'badminton racket');
UNLOCK TABLES;

DROP TABLE IF EXISTS `booking`;
CREATE TABLE `booking` (
  `bookingid` int NOT NULL AUTO_INCREMENT,
  `itemid` int NOT NULL,
  `userid` int,
  `quantity` int NOT NULL,
  `date` varchar(45) NOT NULL,
  `expirationdate` varchar(45) NOT NULL,
  `penaltyperday` double NOT NULL,
  `returned` bool NOT NULL,
  PRIMARY KEY (`bookingid`),
  FOREIGN KEY (itemid)
      REFERENCES items(itemid),
  FOREIGN KEY (userid)
      REFERENCES users(id)
) ENGINE=InnoDB AUTO_INCREMENT=1012 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

LOCK TABLES `booking` WRITE;
INSERT INTO `booking` VALUES (1001, 111, 17, 10, 'Wed Jan 14 00:15:19 IST 2021', 'Wed Jan 14 00:15:19 IST 2022', 850000, false),(1002, 112, 18, 6, 'Wed Jan 14 00:15:19 IST 2021', 'Wed Jan 14 00:15:19 IST 2021', 34000, false),(1003, 113, 18, 5,'Wed Jan 14 00:15:19 IST 2021', 'Wed Jan 14 00:15:19 IST 2022', 300000, false);
UNLOCK TABLES;

DROP TABLE IF EXISTS `userlogs`;
CREATE TABLE `userlogs` (
  `userid` int NOT NULL,
  `logintime` varchar(45) NOT NULL,
  `logouttime` varchar(45) NOT NULL,
  FOREIGN KEY (userid)
      REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

LOCK TABLES `userlogs` WRITE;
INSERT INTO `userlogs` VALUES (17,'2022-09-01T04:46:55.125709800','2022-09-01T04:47:01.801381'),(19,'2021-09-01T05:02:43.010014','2021-09-01T05:02:50.224787400'),(18,'2021-09-01T05:04:57.690182100','2021-09-01T05:05:04.294274300'),(19,'2021-09-01T05:05:12.269897600','2021-09-01T05:05:16.866792500'),(19,'2021-09-01T05:10:08.728527600','2021-09-01T05:10:16.926883100'),(17,'2021-09-01T06:19:09.326477200','2021-09-01T06:19:21.641620900'),(20,'2021-09-01T06:19:34.536411800','2021-09-01T06:19:43.517392100'),(17,'2021-09-01T06:19:46.811400900','2021-09-01T06:20:10.879660700'),(17,'2021-09-01T14:59:48.661066400','2021-09-01T15:02:09.761864900'),(18,'2021-09-01T15:09:02.964317400','2021-09-01T15:09:14.141324800'),(19,'2021-09-01T15:09:27.889908500','2021-09-01T15:09:48.262387'),(18,'2021-09-01T15:38:48.557639300','2021-09-01T15:40:00.527183800'),(17,'2021-09-01T15:40:22.622326','2021-09-01T15:41:06.461438500'),(19,'2021-09-01T15:44:26.195028100','2021-09-01T15:44:33.071448800'),(18,'2021-09-02T01:42:52.417989900','2021-09-02T01:42:55.226675900'),(19,'2021-09-02T01:43:12.226339400','2021-09-02T01:43:15.818776'),(18,'2021-09-03T02:12:41.021781900','2021-09-03T02:19:11.829873500');
UNLOCK TABLES;

DROP TABLE IF EXISTS `currentstock`;
CREATE TABLE `currentstock` (
  `itemid` int NOT NULL,
  `quantity` int NOT NULL,
  PRIMARY KEY (`itemid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

LOCK TABLES `currentstock` WRITE;
INSERT INTO `currentstock` VALUES (111,7),(112,4),(113,8);
UNLOCK TABLES;

DROP TABLE IF EXISTS `suppliers`;
CREATE TABLE `suppliers` (
  `sid` int NOT NULL AUTO_INCREMENT,
  `suppliercode` varchar(45) NOT NULL,
  `fullname` varchar(45) NOT NULL,
  `location` varchar(45) NOT NULL,
  `mobile` varchar(10) NOT NULL,
  PRIMARY KEY (`sid`)
) ENGINE=InnoDB AUTO_INCREMENT=409 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

LOCK TABLES `suppliers` WRITE;
INSERT INTO `suppliers` VALUES (401,'sup1','Volleyball World','Ho Chi Minh','1800560001'),(402,'sup2','Basketball Store','New York','1800560041'),(403,'sup3','Tennis and More','Bangkook','6546521234'),(408,'sup6','Shelby Company Ltd.','Birmingham','9696969696');
UNLOCK TABLES;

DROP TABLE IF EXISTS `customers`;
CREATE TABLE `customers` (
  `fullname` varchar(50) NOT NULL,
  `location` varchar(100) NOT NULL,
  `phone` varchar(20) NOT NULL,
  `custcode` varchar(30) NOT NULL,
) ENGINE=InnoDB AUTO_INCREMENT=31 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

LOCK TABLES `customers` WRITE;
INSERT INTO `customers` VALUES ('Thai Ba Hung','Vietnam','0983486677','V202200668');
UNLOCK TABLES;