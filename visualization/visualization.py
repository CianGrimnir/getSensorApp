import firebase_admin
from firebase_admin import db
import numpy as np
import pandas as pd
import folium
from folium import plugins
from folium.plugins import HeatMap
import webbrowser
import firebase_admin
import matplotlib.pyplot as plt
import datetime

cred_obj = firebase_admin.credentials.Certificate("sensordata-80073-firebase-adminsdk-lkx9i-2e5c0fe1d3.json")
default_app = firebase_admin.initialize_app(cred_obj,
                                            {'databaseURL': 'https://sensordata-80073-default-rtdb.firebaseio.com'})

# forecasted and user's current location temperature

forecast_ref = db.reference("forecast_data")
forecast_data = forecast_ref.get()

user_id = list(forecast_data.keys())

forecast_user_data = []
forecast_user_data1 = []
for i in forecast_data:
    for j in forecast_data[i]:
        for x in forecast_data[i][j]:
            temp = forecast_data[i][j][x]
            formatted = datetime.datetime.utcfromtimestamp(3600 * ((temp['epochTime'] + 1800) // 3600)).strftime(
                "%d/%m/%Y %H:%M:%S")
            if temp['user'] == user_id[0]:
                forecast_user_data.append(
                    [temp['latitude'], temp['longitude'], temp['temperature'], temp['wind_speed'], temp['epochTime'],
                     formatted])
            else:
                forecast_user_data1.append(
                    [temp['latitude'], temp['longitude'], temp['temperature'], temp['wind_speed'], temp['epochTime'],
                     formatted])

forecast_user1 = sorted(forecast_user_data, key=lambda x: x[-2])
forecast_user2 = sorted(forecast_user_data1, key=lambda x: x[-2])
columns = ['latitude', 'longitude', 'temperature', 'wind_speed', 'timestamp', 'formatted_timestamp']
df1 = pd.DataFrame(forecast_user1, columns=columns)
df2 = pd.DataFrame(forecast_user2, columns=columns)
forecasted_timein = df1.iloc[:, -1]
forecasted_temp = df1.iloc[:, 2]
forecasted_wind_speed = df1.iloc[:, 3]
forecasted_timein1 = df2.iloc[:, -1]
forecasted_temp1 = df2.iloc[:, 2]
forecasted_wind_speed1 = df2.iloc[:, 3]

weather_ref = db.reference("weather_data")
weather_data = weather_ref.get()

user_weather_data = []
user1_weather_data = []
for i in weather_data:
    for j in weather_data[i]:
        for x in weather_data[i][j]:
            temp = weather_data[i][j][x]
            formatted = datetime.datetime.utcfromtimestamp(3600 * ((temp['time_stamp'] + 1800) // 3600)).strftime(
                "%d/%m/%Y %H:%M:%S")
            if temp['user'] == user_id[0]:
                user_weather_data.append(
                    [temp['latitude'], temp['longitude'], temp['temperature'], temp['wind_speed'], temp['time_stamp'],
                     formatted])
            else:
                user1_weather_data.append(
                    [temp['latitude'], temp['longitude'], temp['temperature'], temp['wind_speed'], temp['time_stamp'],
                     formatted])

user1_weather = sorted(user_weather_data, key=lambda x: x[-2])
user2_weather = sorted(user1_weather_data, key=lambda x: x[-2])
df_user1 = pd.DataFrame(user1_weather, columns=columns)
df_user2 = pd.DataFrame(user2_weather, columns=columns)
df1 = df_user1.drop_duplicates(subset=['formatted_timestamp'], keep='first')
df2 = df_user2.drop_duplicates(subset=['formatted_timestamp'], keep='first')

time_in = df1.iloc[:, -1]
temperature = df1.iloc[:, 2]
time_in1 = df2.iloc[:, -1]
temperature1 = df2.iloc[:, 2]

fig, ax = plt.subplots()
ax.plot_date(time_in, temperature, color='blue', linestyle='-', label=f'Temperature user {user_id[0]}')
ax.plot_date(time_in1, temperature1, color='red', linestyle='-', label=f'Temperature user {user_id[1]}')
ax.plot_date(forecasted_timein, forecasted_temp, color='lightblue', linestyle='-',
             label=f'Forecasted user {user_id[0]}')
ax.plot_date(forecasted_timein1, forecasted_temp1, color='indianred', linestyle='-',
             label=f'Forecasted user {user_id[1]}')
fig.autofmt_xdate()
plt.xlabel('Time')
plt.ylabel('Temperature')
plt.legend()
plt.show()

# forecasted wind speed and  current location wind speed

time_in = df1.iloc[:, -1]
wind_speed = df1.iloc[:, 3]
time_in1 = df2.iloc[:, -1]
wind_speed1 = df2.iloc[:, 3]
fig, ax = plt.subplots()
ax.plot_date(time_in, wind_speed, color='blue', linestyle='-', label=f'wind speed user {user_id[0]}')
ax.plot_date(time_in1, wind_speed1, color='red', linestyle='-', label=f'wind speed user {user_id[1]}')
ax.plot_date(forecasted_timein, forecasted_wind_speed, color='lightblue', linestyle='-',
             label=f'Forecasted wind speed user {user_id[0]}')
ax.plot_date(forecasted_timein1, forecasted_wind_speed1, color='indianred', linestyle='-',
             label=f'Forecasted wind speed {user_id[1]}')
fig.autofmt_xdate()
plt.xlabel('Time')
plt.ylabel('Wind Speed')
plt.legend()
plt.show()

# plot for feels-like temperature

time_in = df1.iloc[:, -1]
feel_temperature = df1.iloc[:, 3]
time_in1 = df2.iloc[:, -1]
feel_temperature1 = df2.iloc[:, 3]
fig, ax = plt.subplots()
ax.plot_date(time_in, feel_temperature, color='lightblue', linestyle='-', label=f'user {user_id[0]}')
ax.plot_date(time_in1, feel_temperature1, color='red', linestyle='-', label=f'user {user_id[1]}')
fig.autofmt_xdate()
plt.xlabel('Time')
plt.ylabel('Feels like - Temperature')
plt.legend()
plt.show()

# plot for aqi data

aqi_ref = db.reference("aqi_data")
aqi_data = aqi_ref.get()
user_id = list(aqi_data.keys())
aqi_user_data = []
aqi_user_data1 = []
for i in aqi_data:
    for j in aqi_data[i]:
        for x in aqi_data[i][j]:
            temp = aqi_data[i][j][x]
            formatted = datetime.datetime.utcfromtimestamp(3600 * ((temp['time_stamp'] + 1800) // 3600)).strftime(
                "%d/%m/%Y %H:%M:%S")
            if temp['user'] == user_id[0]:
                aqi_user_data.append([temp['latitude'], temp['longitude'], temp['aqi'], temp['time_stamp'], formatted])
            else:
                aqi_user_data1.append([temp['latitude'], temp['longitude'], temp['aqi'], temp['time_stamp'], formatted])

aqi_user1 = sorted(aqi_user_data, key=lambda x: x[-2])
aqi_user2 = sorted(aqi_user_data1, key=lambda x: x[-2])
columns = ['latitude', 'longitude', 'aqi', 'timestamp', 'formatted_timestamp']
df1 = pd.DataFrame(aqi_user1, columns=columns)
df2 = pd.DataFrame(aqi_user2, columns=columns)

time_in = df1.iloc[:, -1]
aqi = df1.iloc[:, 2]
time_in1 = df2.iloc[:, -1]
aqi1 = df2.iloc[:, 2]

fig, ax = plt.subplots()
ax.plot_date(time_in, aqi, color='lightblue', linestyle='-', label=f'user {user_id[0]}')
ax.plot_date(time_in1, aqi1, color='red', linestyle='-', label=f'user {user_id[1]}')
fig.autofmt_xdate()
plt.xlabel('Time')
plt.ylabel('AQI')
plt.legend()
plt.show()

# visualization for less than and more than  average

columns = ['latitude', 'longitude', 'temperature', 'wind_speed', 'timestamp', 'formatted_timestamp']
user_weather = []
for i in weather_data:
    for j in weather_data[i]:
        for x in weather_data[i][j]:
            temp = weather_data[i][j][x]
            formatted = datetime.datetime.utcfromtimestamp(3600 * ((temp['time_stamp'] + 1800) // 3600)).strftime("%d/%m/%Y %H:%M:%S")
            user_weather.append([temp['latitude'], temp['longitude'], temp['temperature'], temp['wind_speed'], temp['time_stamp'], formatted])

df = pd.DataFrame(user_weather, columns=columns)
max_lat = max(df.iloc[:, 0])
max_lon = max(df.iloc[:, 1])
avg = df['temperature'].mean()
more_than_avg = df[df['temperature'] > avg]
less_than_avg = df[df['temperature'] < avg]

# less than average temperature
data = less_than_avg.iloc[:, 0:3].values.tolist()
m = folium.Map([max_lat, max_lon], zoom_start=12)
HeatMap(data).add_to(folium.FeatureGroup(name='HeatMap').add_to(m))
folium.LayerControl().add_to(m)

for i in data:
    folium.Marker(location=[i[0], i[1]], popup=f"temperature - {i[2]}").add_to(m)

m.save("C:\\temperature_less_final.html")
webbrowser.open("C:\\temperature_less_final.html")

# more than average temperature

data = more_than_avg.iloc[:, 0:3].values.tolist()
m = folium.Map([max_lat, max_lon], zoom_start=12)
HeatMap(data).add_to(folium.FeatureGroup(name='HeatMap').add_to(m))
folium.LayerControl().add_to(m)

for i in data:
    folium.Marker(location=[i[0], i[1]], popup=f"temperature - {i[2]}").add_to(m)

m.save("C:\\temperature_more_final.html")
webbrowser.open("C:\\temperature_more_final.html")


avg = df['wind_speed'].mean()
more_than_avg = df[df['wind_speed'] > avg]
less_than_avg = df[df['wind_speed'] < avg]

# less than average wind speed
data = less_than_avg.iloc[:, 0:3].values.tolist()
m = folium.Map([max_lat, max_lon], zoom_start=12)
HeatMap(data).add_to(folium.FeatureGroup(name='HeatMap').add_to(m))
folium.LayerControl().add_to(m)

for i in data:
    folium.Marker(location=[i[0], i[1]], popup=f"wind speed - {i[2]}").add_to(m)

m.save("C:\\wind_speed_less_final.html")
webbrowser.open("C:\\wind_speed_less_final.html")

# more than average temperature

data = more_than_avg.iloc[:, 0:3].values.tolist()
m = folium.Map([max_lat, max_lon], zoom_start=12)
HeatMap(data).add_to(folium.FeatureGroup(name='HeatMap').add_to(m))
folium.LayerControl().add_to(m)

for i in data:
    folium.Marker(location=[i[0], i[1]], popup=f"temperature - {i[2]}").add_to(m)

m.save("C:\\wind_speed_more_final.html")
webbrowser.open("C:\\wind_speed_more_final.html")

