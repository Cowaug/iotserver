package vn.edu.hcmut.iotserver;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import vn.edu.hcmut.iotserver.Entities.DeviceMode;
import vn.edu.hcmut.iotserver.Entities.Permissions;
import vn.edu.hcmut.iotserver.Entities.User;
import vn.edu.hcmut.iotserver.database.Authentication;
import vn.edu.hcmut.iotserver.database.IoTSensorData;
import vn.edu.hcmut.iotserver.database.JawMySQL;

import javax.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.SQLException;

import static vn.edu.hcmut.iotserver.DeviceType.SENSOR_TEMP;
import static vn.edu.hcmut.iotserver.Entities.Attributes.*;
import static vn.edu.hcmut.iotserver.database.IoTSensorData.getDeviceStatus7Day;

@Controller
public class WebController {
    @GetMapping("/greeting")
    public String greeting(@RequestParam(name = "name", required = false, defaultValue = "World") String name, Model model) {
        model.addAttribute("name", name);
        return "greeting";
    }

    @RequestMapping(value = "/getStatus", method = RequestMethod.POST, produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public @ResponseBody
    Object getStatus(HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute(USER_INFO.toString());
        if (user != null && user.getPermission() == Permissions.ADMIN) {
            try {
                JSONParser jsonParser = new JSONParser();
                JSONObject jsonObject = (JSONObject) jsonParser.parse(new InputStreamReader(request.getInputStream()));
                DeviceType deviceType = DeviceType.valueOf((String) jsonObject.get("device_type"));
                String deviceId = (String) jsonObject.get("device_type");
                if (!deviceId.equals("null"))
                    return IoTSensorData.getDeviceStatus7Day(deviceType, deviceId).toJSONString().getBytes();
                else
                    return IoTSensorData.getNewestDeviceStatus(deviceType).toJSONString().getBytes();
            } catch (Exception e) {
                e.printStackTrace();
                return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
            }
        } else return new ResponseEntity<>("No permission", HttpStatus.FORBIDDEN);
    }

    @RequestMapping(value = "/getStatusNoLogin", method = RequestMethod.POST, produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public @ResponseBody
    Object getStatusNologin(HttpServletRequest request) throws SQLException {
        try {
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject) jsonParser.parse(new InputStreamReader(request.getInputStream()));
            DeviceType deviceType = DeviceType.valueOf((String) jsonObject.get("device_type"));
            String deviceId = (String) jsonObject.get("device_name");
            if (!deviceId.equals(""))
                return IoTSensorData.getDeviceStatus7Day(deviceType, deviceId).toJSONString().getBytes();
            else
                return IoTSensorData.getNewestDeviceStatus(deviceType).toJSONString().getBytes();
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        }
    }

    @RequestMapping(value = "/changeSettingNoLogin", method = RequestMethod.POST)
    public @ResponseBody
    Object ChangeSettingNoLogin(HttpServletRequest request) throws SQLException {
        try {
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject) jsonParser.parse(new InputStreamReader(request.getInputStream()));

            String deviceId = (String) jsonObject.get("device_name");
            DeviceMode deviceMode = DeviceMode.valueOf(jsonObject.get("mode").toString().toUpperCase());
            int sensorValue1 = Integer.valueOf(jsonObject.get("sensorValue1").toString());
            int sensorValue2 = Integer.valueOf(jsonObject.get("sensorValue2").toString());

            IoTSensorData.setMode(deviceId, deviceMode, sensorValue1, sensorValue2);

            return new ResponseEntity<>("Succeed", HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        }
    }

    @RequestMapping(value = "/setDefaultNoLogin", method = RequestMethod.POST)
    public @ResponseBody
    Object setDefaultNoLogin(HttpServletRequest request) throws SQLException {
        try {
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject) jsonParser.parse(new InputStreamReader(request.getInputStream()));
            DeviceType deviceType = DeviceType.valueOf((String) jsonObject.get("device_type"));
            String deviceId = (String) jsonObject.get("device_name");
            DeviceMode deviceMode = DeviceMode.valueOf(jsonObject.get("mode").toString().toUpperCase());


            return new ResponseEntity<>("Succeed", HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        }
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
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


    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public Object loginGET(ModelMap modelMap, HttpServletRequest request) {
        try {
            // authentication
            User user = Authentication.login("admin", "admin");

            if (user != null) {
                request.getSession().setAttribute(USER_INFO.toString(), user);
                return new ResponseEntity<>("Login success as " + user.getPermission() + " " + user.getUserId(), HttpStatus.OK);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return new ResponseEntity<>("Wrong username/password", HttpStatus.FORBIDDEN);
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
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
