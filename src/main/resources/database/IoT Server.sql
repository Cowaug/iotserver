-- use vuw8gi9vft7kuo7g;

-- --
-- --
-- --
-- -- CREATE USER TABLE -----------------------------------------------------------------------------
-- drop table if exists IoT_USERS;
-- create table IoT_USERS(
-- `userId` varchar(100) primary key,
-- `password` text not null,
-- `permission` varchar(10) default "GUEST" not null
-- );

-- insert into IoT_USERS values
-- ('admin',
-- '296d55544f37a33eaec689b0e13a6f9ef726486c717def9727e18b965e8c59ebb3efe7feba2d526c089444b32be5e9dffd1605f2fcfb3868bdd8607fd7cc50b76ffdaf2fe7793a1740f78972b3ec1e0a2e733fb34e16163a630b15b0ef947ebecb3d92c2db69ae70ad24995fe5df4feaee5950dd055de8c10d9a27cccb00aa3e',
-- 'ADMIN'); -- userId: admin, password: admin

-- --
-- --
-- --
-- -- TABLE SHOWING DEVICES THAT A SENSOR CONTROLLS
-- drop table if exists SENSOR_DEVICE_INFOS;
-- create table SENSOR_DEVICE_INFOS
-- (device_id VARCHAR(20),
-- sensor_id VARCHAR(20),

-- PRIMARY KEY (device_id,sensor_id)
-- );

-- INSERT INTO vuw8gi9vft7kuo7g.SENSOR_DEVICE_INFOS VALUES
-- ('LightD','Light'),
-- ('Speaker','Mois'),

-- ('air_conditioner_1','sensor_temp_1'),
-- ('air_conditioner_2','sensor_temp_2'),

-- ('lightbulb_1','sensor_light_1'),
-- ('lightbulb_2','sensor_light_1'),
-- ('lightbulb_3','sensor_light_2'),
-- ('lightbulb_4','sensor_light_2'),

-- ('motor_1','sensor_plant_1'),
-- ('motor_2','sensor_plant_2');

-- --
-- --
-- --
-- -- OPERATING MODE OF EACH DEVICES
-- drop table if exists DEVICE_MODE;
-- create table DEVICE_MODE(
-- `device_id` VARCHAR(20) primary key,
-- `current_mode` VARCHAR(20),
-- `sensor_value_1` SMALLINT,
-- `sensor_value_2` TINYINT,
-- `schedule_on` VARCHAR(5),
-- `schedule_off` VARCHAR(5)
-- );

-- INSERT INTO vuw8gi9vft7kuo7g.DEVICE_MODE VALUES
-- ('air_conditioner_1','auto',-1,-1,'00:00','00:00'),
-- ('air_conditioner_2','auto',-1,-1,'00:00','00:00'),

-- ('lightbulb_1','auto',-1,-1,'00:00','00:00'),
-- ('lightbulb_2','auto',-1,-1,'00:00','00:00'),
-- ('lightbulb_3','auto',-1,-1,'00:00','00:00'),
-- ('lightbulb_4','auto',-1,-1,'00:00','00:00'),

-- ('motor_1','auto',-1,-1,'00:00','00:00'),
-- ('motor_2','auto',-1,-1,'00:00','00:00'),

-- ('LightD','auto',-1,-1,'00:00','00:00'),
-- ('Speaker','auto',-1,-1,'00:00','00:00');

-- --
-- --
-- --
-- -- THRESHOLD VALUE FOR ACTIVATE/DECATIVE DEVICE WHEN DEVICE_MODE IS SET TO AUTO AND THRESHOLD VALUE IS -1
-- drop table if exists SETTING;
-- CREATE TABLE `SETTING`(
-- `device_key` VARCHAR(5) PRIMARY KEY,
-- `device_value` INT
-- );
-- INSERT INTO `SETTING` VALUES
-- ('temp',-1),
-- ('humid',-1),
-- ('plant',-1),
-- ('light',-1)
-- ;

-- --
-- --
-- --
-- -- SAVING VALUE OF TEMPERATURE/HUMID SENSOR
-- drop table if exists SENSOR_TEMP;
-- create table SENSOR_TEMP
-- (
-- `_timestamp` TIMESTAMP,
-- `device_id` VARCHAR(20),
-- `temp_value` TINYINT,
-- `humid_value` TINYINT,
-- `grouped` TINYINT,

-- PRIMARY KEY (_timestamp,device_id)
-- );

-- --
-- --
-- --
-- -- SAVING VALUE OF LIGHT SENSOR
-- drop table if exists SENSOR_LIGHT;
-- create table SENSOR_LIGHT 
-- (
-- `_timestamp` TIMESTAMP,
-- `device_id` VARCHAR(20),
-- `light_value` SMALLINT,
-- `grouped` TINYINT,

-- PRIMARY KEY (_timestamp,device_id)
-- );

-- --
-- --
-- --
-- -- SAVING VALUE OF PLANT MOISTURE SENSOR
-- drop table if exists SENSOR_PLANT;
-- create table SENSOR_PLANT 
-- (`_timestamp` TIMESTAMP,
-- `device_id` VARCHAR(20),
-- `humid_value` TINYINT,
-- `grouped` TINYINT,

-- PRIMARY KEY (_timestamp,device_id)
-- );

-- --
-- --
-- --
-- -- SAVING STATUS OF INDICATE_LIGHT
-- drop table if exists INDICATE_LIGHT;
-- create table INDICATE_LIGHT 
-- (
-- `_timestamp` TIMESTAMP,
-- `device_id` VARCHAR(20),
-- `color` TINYINT,
-- `grouped` TINYINT,

-- PRIMARY KEY (_timestamp,device_id)
-- );

-- --
-- --
-- --
-- -- SAVING STATUS OF LightD (testing device)
-- drop table if exists LIGHTD;
-- create table LIGHTD
-- (
-- `_timestamp` TIMESTAMP,
-- `device_id` VARCHAR(20),
-- `status` TINYINT,
-- `intensity` SMALLINT,
-- `grouped` TINYINT,

-- PRIMARY KEY (_timestamp,device_id)
-- );

-- --
-- --
-- --
-- -- SAVING STATUS OF Speaker (testing device)
-- drop table if exists SPEAKER;
-- create table SPEAKER
-- (`_timestamp` TIMESTAMP,
-- `device_id` VARCHAR(20),
-- `status` TINYINT,
-- `intensity` SMALLINT,
-- `grouped` TINYINT,

-- PRIMARY KEY (_timestamp,device_id)
-- );

-- --
-- --
-- --
-- -- SAVING STATUS OF LIGHTBULB
-- drop table if exists LIGHTBULB;
-- create table LIGHTBULB 
-- (`_timestamp` TIMESTAMP, 
-- `device_id` VARCHAR(20),
-- `status` TINYINT,
-- `grouped` TINYINT,

-- PRIMARY KEY (_timestamp,device_id)
-- );

-- --
-- --
-- --
-- -- SAVING STATUS OF AIR CONDITIONER
-- drop table if exists AIR_CONDITIONER;
-- create table AIR_CONDITIONER 
-- (`_timestamp` TIMESTAMP,
-- `device_id` VARCHAR(20),
-- `status` TINYINT,
-- `operating_temp` TINYINT,
-- `grouped` TINYINT,

-- PRIMARY KEY (_timestamp,device_id)
-- );

-- --
-- --
-- --
-- -- SAVING STATUS OF MOTOR
-- drop table if exists MOTOR;
-- create table MOTOR 
-- (`_timestamp` TIMESTAMP,
-- `device_id` VARCHAR(20),
-- `status` TINYINT,
-- `operating_capacity` TINYINT,
-- `operating_time_remain` TINYINT,
-- `grouped` TINYINT,

-- PRIMARY KEY (_timestamp,device_id)
-- );

-- --
-- --
-- --
-- -- ADDING VALUE OF SENSORS, DEVICES FOR QUERY TESTING
-- -- Run Generated File

-- --
-- --
-- --
-- -- QUERY TESTING

SELECT table_schema "DB Name",
        ROUND(SUM(data_length + index_length) / 1024 / 1024, 1) "DB Size in MB" 
FROM information_schema.tables 
GROUP BY table_schema; 

select * from vuw8gi9vft7kuo7g.IoT_USERS;
select * from SENSOR_TEMP order by 1 desc;
select * from SENSOR_LIGHT order by 1 desc;
select * from SENSOR_PLANT order by 1 desc;
select * from INDICATE_LIGHT order by 1 desc;
select * from LIGHTBULB order by 1 desc;
select * from AIR_CONDITIONER order by 1 desc;
select * from LIGHTD order by 1 desc;
select * from MOTOR order by 1 desc;
select * from SETTING order by 1 desc;
select * from SPEAKER order by 1 desc;

select * from DEVICE_MODE order by 1 desc;
select * from SENSOR_DEVICE_INFOS order by 1 desc;
select * from DEVICE_INFO order by 1 desc;

select *
        from AIR_CONDITIONER AS D,
             DEVICE_MODE AS M,
             SENSOR_DEVICE_INFOS AS SDI,
             SENSOR_TEMP AS S,

             (select MAX(_timestamp) AS last_update, device_id
              from AIR_CONDITIONER
              GROUP BY device_id) as DT,

             (select MAX(_timestamp) AS last_sensor_update, device_id
              from SENSOR_TEMP
              GROUP BY device_id) as ST

        WHERE D.device_id = DT.device_id
          AND D._timestamp = DT.last_update
          AND S.device_id = ST.device_id
          AND S._timestamp = ST.last_sensor_update
          AND D.device_id = M.device_id
          AND D.device_id = SDI.device_id
          AND SDI.sensor_id = S.device_id;
          
SELECT DATE(S._timestamp) AS days, AVG(S.temp_value), AVG(S.humid_value) 
FROM AIR_CONDITIONER AS D, vuw8gi9vft7kuo7g.SENSOR_DEVICE_INFOS AS SDI, SENSOR_TEMP AS S
WHERE  D.device_id = SDI.device_id AND SDI.sensor_id = S.device_id AND DATE_ADD('2020-07-17 19:17:09', INTERVAL -7 DAY) <= DATE(D._timestamp) <= '2020-07-17 19:17:09' AND D.device_id = 'air_conditioner_1'
GROUP BY days
ORDER BY days;

SELECT * FROM SENSOR_TEMP
order by _timestamp desc;

INSERT INTO SENSOR_TEMP
SELECT subtable._timestamp,
 subtable.device_id, 
 avg(subtable.temp_value), 
--   count(subtable.temp_value), 
 avg(subtable.humid_value),
--   count(subtable.temp_value), 
 1 AS grouped
 FROM
(SELECT CONCAT(
			DATE(_timestamp), " ", 
				TIME_FORMAT(
					SEC_TO_TIME(
						(TIME_TO_SEC(_timestamp) DIV 1200) * 1200
					), '%H:%i:%s')
		) as _timestamp,
        device_id,
		temp_value,
        humid_value,
        _timestamp as full_time
FROM SENSOR_TEMP
WHERE grouped = 0 and
_timestamp < CONCAT(
			DATE(NOW()), " ", 
				TIME_FORMAT(
					SEC_TO_TIME(
						(TIME_TO_SEC(NOW()) DIV 1200) * 1200
					), '%H:%i:%s')
		)
) AS subtable
GROUP BY _timestamp, device_id
order by _timestamp desc;

DELETE FROM SENSOR_TEMP WHERE _timestamp < CONCAT(
			DATE(NOW()), " ", 
				TIME_FORMAT(
					SEC_TO_TIME(
						(TIME_TO_SEC(NOW()) DIV 1200) * 1200
					), '%H:%i:%s'))
	and grouped = 0;

SELECT * FROM SENSOR_PLANT
order by _timestamp desc
LIMIT 0,1000000;