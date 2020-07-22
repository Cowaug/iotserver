import datetime as dt
import random as r

### CONFIG VALUES ###

day_ahead = 0
day_before = 7
step_in_seconds = 360

#####################

today = dt.datetime.now().replace(microsecond=0)
loop = int(3600 * 24 * (day_before - 1) / step_in_seconds)
current_loop = int(3600 * 24 / 10)

TABLES = [
    "SENSOR_TEMP",
    "SENSOR_LIGHT",
    "SENSOR_PLANT",
    "INDICATE_LIGHT",
    "LIGHTBULB",
    "AIR_CONDITIONER",
    "MOTOR",
    "SPEAKER",
    "LIGHTD"
]

VALUES = [
    [], [], [], [], [], [], [], [], []
]

start_date_time = today + dt.timedelta(days=day_ahead)
stop_date_time = today - dt.timedelta(days=day_before)

current_date_time = start_date_time


# while False:
while current_date_time > stop_date_time:
    print("\rGenerating: " + str(current_date_time - stop_date_time) + " remain", end='')
    if((current_date_time - today).total_seconds() > -21100.0):
        current_date_time = current_date_time - dt.timedelta(seconds=10)
    else:
        current_date_time = current_date_time - dt.timedelta(seconds=3600)

    # SENSOR_TEMP
    VALUES[0] += [
        (str(current_date_time), "sensor_temp_1", r.randint(20, 30), r.randint(20, 70),0),
        (str(current_date_time), "sensor_temp_2", r.randint(20, 30), r.randint(20, 70),0)
    ]

    # SENSOR_LIGHT
    VALUES[1] += [
        (str(current_date_time), "sensor_light_1", r.randint(400, 800),0),
        (str(current_date_time), "sensor_light_2", r.randint(400, 800),0),
        (str(current_date_time), "Light", r.randint(400, 800),0)
    ]

    # SENSOR_PLANT
    VALUES[2] += [
        (str(current_date_time), "sensor_plant_1", r.randint(40, 80),0),
        (str(current_date_time), "sensor_plant_2", r.randint(40, 80),0),
        (str(current_date_time), "Mois", r.randint(40, 80),0)
    ]

    # INDICATE_LIGHT
    VALUES[3] += [
        (str(current_date_time), "indicate_light_1", r.randint(0, 2),0),
        (str(current_date_time), "indicate_light_2", r.randint(0, 2),0)
    ]

    # LIGHTBULB
    VALUES[4] += [
        (str(current_date_time), "lightbulb_1", r.randint(0, 1),0),
        (str(current_date_time), "lightbulb_2", r.randint(0, 1),0),
        (str(current_date_time), "lightbulb_3", r.randint(0, 1),0),
        (str(current_date_time), "lightbulb_4", r.randint(0, 1),0)
    ]

    # AIR_CONDITIONER
    VALUES[5] += [
        (str(current_date_time), "air_conditioner_1", r.randint(0, 1), r.randint(16, 28),0),
        (str(current_date_time), "air_conditioner_2", r.randint(0, 1), r.randint(16, 28),0)
    ]

    # MOTOR
    VALUES[6] += [
        (str(current_date_time), "motor_1", r.randint(0, 1), r.randint(20, 30), r.randint(5,10),0),
        (str(current_date_time), "motor_2", r.randint(0, 1), r.randint(20, 30), r.randint(5,10),0)
    ]

    # SPEAKER
    VALUES[7] += [
        (str(current_date_time), "Speaker", r.randint(0, 1), r.randint(100, 200),0)
    ]

    # LIGHTD
    VALUES[8] += [
        (str(current_date_time), "LightD", r.randint(0, 1), r.randint(80, 240),0)
    ]


sql_file = open("generateData.sql", "w+")
for i in range(len(VALUES)):
    db_name = TABLES[i]
    sql_query = "truncate table " + db_name + ";\n\n"
    sql_file.writelines(sql_query)

for i in range(len(VALUES)):
    db_name = TABLES[i]
    sql_query = "insert into " + db_name + " values "

    for j in range(len(VALUES[i])):
        sql_query += str(VALUES[i][j]) + ','
        print("\rConcatenating ("+ str(i+1) + "/" + str(len(VALUES)) +"): " + "{:.2f}".format((j + 1)*100.0/len(VALUES[i])) + "%", end='')

    sql_query = sql_query[:-1] + ';\n'
    print("\rWriting ("+ str(i+1) + "/" + str(len(VALUES)) +") ...                  ", end='')
    sql_file.writelines(sql_query)    

print("\rDone.                          ")

sql_file.writelines("""
SELECT table_schema "DB Name",
        ROUND(SUM(data_length + index_length) / 1024 / 1024, 1) "DB Size in MB" 
FROM information_schema.tables 
GROUP BY table_schema; 
"""
)
sql_file.close()
