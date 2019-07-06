-- ----------------------------  
-- Table structure for `T_USER`  
-- ----------------------------  
DROP TABLE IF EXISTS `T_USER`;  
CREATE TABLE `T_USER` (
   `ID` bigint(20) NOT NULL AUTO_INCREMENT,
   `ACCOUNT` varchar(100) DEFAULT NULL,
   `PASSWORD` varchar(255) DEFAULT NULL,
   `SALT` varchar(100) DEFAULT NULL,
   `NICK_NAME` varchar(100) DEFAULT NULL,
   `REAL_NAME` varchar(100) DEFAULT NULL,
   `ID_CARD` varchar(150) DEFAULT NULL,
   `MOBILE_PHONE` varchar(45) DEFAULT NULL,
   `EMAIL` varchar(100) DEFAULT NULL,
   `CREATE_TIME` datetime DEFAULT NULL,
   `EXPIRE_TIME` datetime DEFAULT NULL,
   `DELETE_FLAG` int(1) DEFAULT NULL,
   PRIMARY KEY (`ID`),
   UNIQUE KEY `accountUnique` (`ACCOUNT`),
   UNIQUE KEY `phoneUnique` (`MOBILE_PHONE`) USING BTREE
 ) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------  
-- Table structure for `T_USER_ATTRIBUTE`  
-- ----------------------------  
DROP TABLE IF EXISTS `T_USER_ATTRIBUTE`;  
CREATE TABLE `T_USER_ATTRIBUTE` (
   `ID` bigint(20) NOT NULL AUTO_INCREMENT,
   `USER_ID` bigint(20) NOT NULL,
   `KEY` varchar(100) NOT NULL,
   `VALUE` varchar(255) DEFAULT NULL,
   `TYPE` varchar(10) DEFAULT NULL,
   PRIMARY KEY (`ID`),
   UNIQUE KEY `UNIQUE_T_USER_ATTRIBUTE` (`USER_ID`,`KEY`),
   FOREIGN KEY (`USER_ID`) REFERENCES T_USER(`ID`)
 ) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------  
-- Table structure for `T_ROLE`  
-- ----------------------------  
DROP TABLE IF EXISTS `T_ROLE`;  
CREATE TABLE `T_ROLE` (  
  `ID` bigint NOT NULL AUTO_INCREMENT,  
  `NAME` varchar(50) DEFAULT NULL,  
  `IDENTITY` varchar(50) DEFAULT NULL,  
  `DESCRIPTION` varchar(255) DEFAULT NULL,  
  `DELETE_FLAG` int(1) DEFAULT 0,
  PRIMARY KEY (`ID`)                 
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------  
-- Table structure for `T_GROUP`  
-- ----------------------------  
DROP TABLE IF EXISTS `T_GROUP`;  
CREATE TABLE `T_GROUP` (  
  `ID` bigint NOT NULL AUTO_INCREMENT,  
  `NAME` varchar(50) DEFAULT NULL,  
  `IDENTITY` varchar(50) DEFAULT NULL,  
  `DESCRIPTION` varchar(255) DEFAULT NULL,  
  `DELETE_FLAG` int(1) DEFAULT 0,
  PRIMARY KEY (`ID`)                 
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------  
-- Table structure for `T_RESOURCE`  
-- ----------------------------  
DROP TABLE IF EXISTS `T_RESOURCE`;  
CREATE TABLE `T_RESOURCE` (  
  `ID` bigint NOT NULL AUTO_INCREMENT,  
  `NAME` varchar(255) DEFAULT NULL,                                                            
  `TYPE` int(11) DEFAULT NULL,    
  `IDENTITY` varchar(255) DEFAULT NULL,                                                          
  `URL` varchar(255) DEFAULT NULL,                                                             
  `ICON` varchar(100) DEFAULT NULL,   
  `PRIORITY` int(2) DEFAULT NULL,                                                           
  `DELETE_FLAG` int(1) DEFAULT 0,                                                     
  `PARENT_ID` varchar(255) DEFAULT NULL,  
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------  
-- Table structure for `T_PERMISSION`  
-- ---------------------------- 
DROP TABLE IF EXISTS `T_PERMISSION`;
CREATE TABLE `T_PERMISSION` (             
  `ID` bigint NOT NULL AUTO_INCREMENT,     
  `AUTH_STATUS` int(11) DEFAULT NULL,        
  `EXTEND_STATUS` int(11) DEFAULT NULL,      
  `PRINCIPAL_ID` bigint DEFAULT NULL,  
  `PRINCIPAL_TYPE` int(11) DEFAULT NULL,     
  `RESOURCE_ID` bigint DEFAULT NULL,   
  PRIMARY KEY (`ID`)               
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------  
-- Table structure for `T_USER_ROLE`  
-- ----------------------------  
DROP TABLE IF EXISTS `T_USER_ROLE`;  
CREATE TABLE `T_USER_ROLE` ( 
  `ID` bigint,
  `USER_ID` bigint NOT NULL,  
  `ROLE_ID` bigint NOT NULL,  
  `PRIORITY` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`USER_ID`,`ROLE_ID`),
  FOREIGN KEY (`USER_ID`) REFERENCES T_USER(`ID`),
  FOREIGN KEY (`ROLE_ID`) REFERENCES T_ROLE(`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------  
-- Table structure for `T_USER_GROUP`  
-- ----------------------------  
DROP TABLE IF EXISTS `T_USER_GROUP`;  
CREATE TABLE `T_USER_GROUP` (  
  `ID` bigint,
  `USER_ID` bigint NOT NULL,  
  `GROUP_ID` bigint NOT NULL,
  `PRIORITY` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`USER_ID`,`GROUP_ID`),
  FOREIGN KEY (`USER_ID`) REFERENCES T_USER(`ID`),
  FOREIGN KEY (`GROUP_ID`) REFERENCES T_GROUP(`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------  
-- Table structure for `T_GROUP_ROLE`  
-- ----------------------------  
DROP TABLE IF EXISTS `T_GROUP_ROLE`;  
CREATE TABLE `T_GROUP_ROLE` (  
  `ID` bigint,
  `GROUP_ID` bigint NOT NULL,  
  `ROLE_ID` bigint NOT NULL,
  `PRIORITY` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`GROUP_ID`,`ROLE_ID`),
  FOREIGN KEY (`GROUP_ID`) REFERENCES T_GROUP(`ID`),
  FOREIGN KEY (`ROLE_ID`) REFERENCES T_ROLE(`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------  
-- Table structure for `T_ROLE_RESOURCE`  
-- ----------------------------  
DROP TABLE IF EXISTS `T_ROLE_RESOURCE`;  
CREATE TABLE `T_ROLE_RESOURCE` (
  `ID` bigint,
  `ROLE_ID` bigint NOT NULL,  
  `RESOURCE_ID` bigint NOT NULL,  
  PRIMARY KEY (`ROLE_ID`,`RESOURCE_ID`),
  FOREIGN KEY (`ROLE_ID`) REFERENCES T_ROLE(`ID`),
  FOREIGN KEY (`RESOURCE_ID`) REFERENCES T_RESOURCES(`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8; 
