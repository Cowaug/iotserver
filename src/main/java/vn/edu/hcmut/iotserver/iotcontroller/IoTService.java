package vn.edu.hcmut.iotserver.iotcontroller;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import vn.edu.hcmut.iotserver.DeviceType;
import vn.edu.hcmut.iotserver.database.Authentication;
import vn.edu.hcmut.iotserver.database.IoTSensorData;
import vn.edu.hcmut.iotserver.entities.DeviceMode;
import vn.edu.hcmut.iotserver.entities.Permissions;
import vn.edu.hcmut.iotserver.entities.User;

import javax.servlet.http.HttpServletRequest;
import java.io.InputStreamReader;
import java.sql.SQLException;

import static vn.edu.hcmut.iotserver.entities.Attributes.USER_INFO;

@Service
public class IoTService {

    @Autowired
    IoTSensorData ioTSensorData;

    public Object getStatusNologin(HttpServletRequest request) {
        try {
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject) jsonParser.parse(new InputStreamReader(request.getInputStream()));
            DeviceType deviceType = DeviceType.valueOf((String) jsonObject.get("device_type"));
            String deviceId = (String) jsonObject.get("device_name");
            if (!deviceId.equals(""))
                return IoTSensorData.getDeviceStatus7Day(deviceType, deviceId).toJSONString().getBytes();
            else
//                return IoTSensorData.getNewestDeviceStatus(deviceType).toJSONString().getBytes();
                return ioTSensorData.getNewestDeviceStatus(deviceType).toJSONString().getBytes();
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        }
    }

    public Object ChangeSettingNoLogin(HttpServletRequest request) {
        try {
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject) jsonParser.parse(new InputStreamReader(request.getInputStream()));

            String deviceId = (String) jsonObject.get("device_name");
            DeviceMode deviceMode = DeviceMode.valueOf(jsonObject.get("mode").toString().toUpperCase());
            int sensorValue1 = Integer.valueOf(jsonObject.get("sensorValue1").toString());
            int sensorValue2 = Integer.valueOf(jsonObject.get("sensorValue2").toString());
            String sheduleOn = jsonObject.get("schedule_on").toString();
            String scheduleOff = jsonObject.get("schedule_off").toString();

            ioTSensorData.setMode(deviceId, deviceMode, sensorValue1, sensorValue2, sheduleOn, scheduleOff);

            return new ResponseEntity<>("Succeed", HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        }
    }

    public Object getSettingNoLogin(HttpServletRequest request) {
        try {
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject) jsonParser.parse(new InputStreamReader(request.getInputStream()));

            String deviceId = (String) jsonObject.get("device_name");

            JSONObject ret = new JSONObject();
            Object[] settings = IoTSensorData.getMode(deviceId);
            ret.put("mode", settings[0].toString());
            ret.put("sensorValue1", settings[1]);
            ret.put("sensorValue2", settings[2]);
            ret.put("shedule_on", settings[3]);
            ret.put("shedule_off", settings[4]);

            return ret.toJSONString().getBytes();
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        }
    }

    public Object setDefaultNoLogin(HttpServletRequest request){
        try {
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject) jsonParser.parse(new InputStreamReader(request.getInputStream()));

            try {
                int tmp = Integer.valueOf(jsonObject.get("humid").toString());
                ioTSensorData.setDefault("humid", tmp);
                IoTController.setHumidityThreshold(tmp);
            } catch (Exception ignored) {

            }
            try {
                int tmp = Integer.valueOf(jsonObject.get("light").toString());
                ioTSensorData.setDefault("light", tmp);
                IoTController.setLightThreshold(tmp);
            } catch (Exception ignored) {

            }
            try {
                int tmp = Integer.valueOf(jsonObject.get("plant").toString());
                ioTSensorData.setDefault("plant", tmp);
                IoTController.setPlantThreshold(tmp);
            } catch (Exception ignored) {

            }
            try {
                int tmp = Integer.valueOf(jsonObject.get("temp").toString());
                ioTSensorData.setDefault("temp", tmp);
                IoTController.setTemperatureThreshold(tmp);
            } catch (Exception ignored) {

            }
            try {
                int tmp = Integer.valueOf(jsonObject.get("s_on").toString());
                ioTSensorData.setDefault("schedule_on", tmp);
                IoTController.setTemperatureThreshold(tmp);
            } catch (Exception ignored) {

            }
            try {
                int tmp = Integer.valueOf(jsonObject.get("s_off").toString());
                ioTSensorData.setDefault("schedule_off", tmp);
                IoTController.setTemperatureThreshold(tmp);
            } catch (Exception ignored) {

            }

            return new ResponseEntity<>("Succeed", HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        }
    }


    public Object getDefaultNoLogin(HttpServletRequest request)  {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("temp", IoTController.getTemperatureThreshold());
            jsonObject.put("plant", IoTController.getPlantThreshold());
            jsonObject.put("light", IoTController.getLightThreshold());
            jsonObject.put("humid", IoTController.getHumidityThreshold());
            jsonObject.put("schedule_on", IoTController.getHumidityThreshold());
            jsonObject.put("schedule_off", IoTController.getHumidityThreshold());

            return jsonObject.toJSONString().getBytes();
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        }
    }

    public Object loginPOST(HttpServletRequest request) {
        String userId = request.getParameter("user-id");
        String password = request.getParameter("password");

        if (userId == null && password == null)
            try {
                JSONParser jsonParser = new JSONParser();
                JSONObject jsonObject = (JSONObject) jsonParser.parse(new InputStreamReader(request.getInputStream()));
                userId = (String) jsonObject.get("user-id");
                password = (String) jsonObject.get("password");
            } catch (Exception e) {
                e.printStackTrace();
            }

        if (userId == null || password == null)
            return new ResponseEntity<>("Missing username/password", HttpStatus.BAD_REQUEST);

        try {
            // authentication
            User user = Authentication.login(userId, password);

            if (user != null) {
                request.getSession().setAttribute(USER_INFO.toString(), user);
                return new ResponseEntity<>("Login success", HttpStatus.OK);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return new ResponseEntity<>("Wrong username/password", HttpStatus.BAD_REQUEST);
    }

    public Object registerPOST(ModelMap modelMap, HttpServletRequest request) {
        try {
            // authentication
            User user = Authentication.register(request.getParameter("user-id"), request.getParameter("password"));

            request.getSession().setAttribute(USER_INFO.toString(), user);
            return new ResponseEntity<>("Register success", HttpStatus.OK);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return new ResponseEntity<>("Register fail, check username and password.", HttpStatus.BAD_REQUEST);
    }
}
