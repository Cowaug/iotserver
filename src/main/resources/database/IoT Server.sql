use vuw8gi9vft7kuo7g;
drop table users;

create table IoT_USERS
(userId varchar(100) primary key,
password text not null
);
alter table IoT_USERS add permission varchar(10) default "GUEST" not null ;

create table SENSOR_TEMP
(_timestamp TIMESTAMP,
device_id VARCHAR(20),
temp_value TINYINT,
humid_value TINYINT,

CONSTRAINT sensor_temp_pk PRIMARY KEY USING BTREE (_timestamp,device_id)
);

create table SENSOR_LIGHT 
(_timestamp TIMESTAMP,
device_id VARCHAR(20),
light_value SMALLINT,

CONSTRAINT sensor_light_pk PRIMARY KEY USING BTREE (_timestamp,device_id)
);

create table SENSOR_PLANT 
(_timestamp TIMESTAMP,
device_id VARCHAR(20),
humid_value TINYINT,

CONSTRAINT sensor_plant_pk PRIMARY KEY USING BTREE (_timestamp,device_id)
);

create table NDICATE_LIGHT 
(_timestamp TIMESTAMP,
device_id VARCHAR(20),
color TINYINT,

CONSTRAINT indicate_light_pk PRIMARY KEY USING BTREE (_timestamp,device_id)
);

create table LIGHTBULB 
(_timestamp TIMESTAMP, 
device_id VARCHAR(20),
status TINYINT,

CONSTRAINT lightbulb_pk PRIMARY KEY USING BTREE (_timestamp,device_id)
);

create table AIR_CONDITIONER 
(_timestamp TIMESTAMP,
device_id VARCHAR(20),
status TINYINT,
operating_temp TINYINT,

CONSTRAINT air_conditioner_pk PRIMARY KEY USING BTREE (_timestamp,device_id)
);

create table MOTOR 
(_timestamp TIMESTAMP,
device_id VARCHAR(20),
status TINYINT,
operating_capacity TINYINT,
operating_time_remain TINYINT,

CONSTRAINT motor_pk PRIMARY KEY USING BTREE (_timestamp,device_id)
);


insert into IoT_USERS values
('admin',
'296d55544f37a33eaec689b0e13a6f9ef726486c717def9727e18b965e8c59ebb3efe7feba2d526c089444b32be5e9dffd1605f2fcfb3868bdd8607fd7cc50b76ffdaf2fe7793a1740f78972b3ec1e0a2e733fb34e16163a630b15b0ef947ebecb3d92c2db69ae70ad24995fe5df4feaee5950dd055de8c10d9a27cccb00aa3e',
'ADMIN');

select * from IoT_USERS;
select * from SENSOR_TEMP;
select * from SENSOR_LIGHT;
select * from SENSOR_PLANT;
select * from NDICATE_LIGHT;
select * from LIGHTBULB;
select * from AIR_CONDITIONER;
select * from MOTOR;

