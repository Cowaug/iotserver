package vn.edu.hcmut.iotserver;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import vn.edu.hcmut.iotserver.entities.DeviceMode;
import vn.edu.hcmut.iotserver.entities.Permissions;
import vn.edu.hcmut.iotserver.entities.User;
import vn.edu.hcmut.iotserver.database.Authentication;
import vn.edu.hcmut.iotserver.database.IoTSensorData;
import vn.edu.hcmut.iotserver.iotcontroller.IoTController;
import vn.edu.hcmut.iotserver.iotcontroller.IoTService;

import javax.servlet.http.HttpServletRequest;

import java.io.InputStreamReader;
import java.sql.SQLException;

import static vn.edu.hcmut.iotserver.entities.Attributes.*;

@Controller
public class WebController {

    @Autowired
    IoTService ioTService;

    @RequestMapping(value = "/getStatus", method = RequestMethod.POST, produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public @ResponseBody
    Object getStatusNologin(HttpServletRequest request){
        User user = (User) request.getSession().getAttribute(USER_INFO.toString());
        if (user != null && user.getPermission() == Permissions.ADMIN) {
            return ioTService.getStatusNologin(request);
        } else return new ResponseEntity<>("No permission", HttpStatus.FORBIDDEN);

    }

    @RequestMapping(value = "/changeSetting", method = RequestMethod.POST)
    public @ResponseBody
    Object ChangeSettingNoLogin(HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute(USER_INFO.toString());
        if (user != null && user.getPermission() == Permissions.ADMIN) {
            return ioTService.ChangeSettingNoLogin(request);
        } else return new ResponseEntity<>("No permission", HttpStatus.FORBIDDEN);

    }

    @PostMapping(value = "/getSetting", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public @ResponseBody
    Object getSettingNoLogin(HttpServletRequest request)  {
        User user = (User) request.getSession().getAttribute(USER_INFO.toString());
        if (user != null && user.getPermission() == Permissions.ADMIN) {
            return ioTService.getSettingNoLogin(request);
        } else return new ResponseEntity<>("No permission", HttpStatus.FORBIDDEN);

    }

    @RequestMapping(value = "/setDefault", method = RequestMethod.POST)
    public @ResponseBody
    Object setDefaultNoLogin(HttpServletRequest request){
        User user = (User) request.getSession().getAttribute(USER_INFO.toString());
        if (user != null && user.getPermission() == Permissions.ADMIN) {
            return ioTService.setDefaultNoLogin(request);
        } else return new ResponseEntity<>("No permission", HttpStatus.FORBIDDEN);

    }

    @RequestMapping(value = "/getDefault", method = RequestMethod.POST, produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public @ResponseBody
    Object getDefaultNoLogin(HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute(USER_INFO.toString());
        if (user != null && user.getPermission() == Permissions.ADMIN) {
            return ioTService.getDefaultNoLogin(request);
        } else return new ResponseEntity<>("No permission", HttpStatus.FORBIDDEN);
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public Object loginPOST(HttpServletRequest request) {
        return ioTService.loginPOST(request);
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public Object registerPOST(ModelMap modelMap, HttpServletRequest request) {
        return ioTService.registerPOST(modelMap,request);
    }
}
