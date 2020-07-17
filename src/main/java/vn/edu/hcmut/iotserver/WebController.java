package vn.edu.hcmut.iotserver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import vn.edu.hcmut.iotserver.entities.Permissions;
import vn.edu.hcmut.iotserver.entities.User;
import vn.edu.hcmut.iotserver.iotcontroller.IoTService;

import javax.servlet.http.HttpServletRequest;

import static vn.edu.hcmut.iotserver.entities.Attributes.USER_INFO;

@Controller
public class WebController {

    @Autowired
    IoTService ioTService;

    @RequestMapping(value = "/getStatus", method = RequestMethod.POST, produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public @ResponseBody
    Object getStatus(HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute(USER_INFO.toString());
        if (user != null && user.getPermission() == Permissions.ADMIN) {
            return ioTService.getStatus(request);
        } else return new ResponseEntity<>("No permission", HttpStatus.FORBIDDEN);

    }

    @RequestMapping(value = "/changeSetting", method = RequestMethod.POST)
    public @ResponseBody
    Object ChangeSetting(HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute(USER_INFO.toString());
        if (user != null && user.getPermission() == Permissions.ADMIN) {
            return ioTService.ChangeSetting(request);
        } else return new ResponseEntity<>("No permission", HttpStatus.FORBIDDEN);

    }

    @PostMapping(value = "/getSetting", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public @ResponseBody
    Object getSetting(HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute(USER_INFO.toString());
        if (user != null && user.getPermission() == Permissions.ADMIN) {
            return ioTService.getSetting(request);
        } else return new ResponseEntity<>("No permission", HttpStatus.FORBIDDEN);

    }

    @RequestMapping(value = "/setDefault", method = RequestMethod.POST)
    public @ResponseBody
    Object setDefault(HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute(USER_INFO.toString());
        if (user != null && user.getPermission() == Permissions.ADMIN) {
            return ioTService.setDefault(request);
        } else return new ResponseEntity<>("No permission", HttpStatus.FORBIDDEN);

    }

    @RequestMapping(value = "/getDefault", method = RequestMethod.POST, produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public @ResponseBody
    Object getDefault(HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute(USER_INFO.toString());
        if (user != null && user.getPermission() == Permissions.ADMIN) {
            return ioTService.getDefault(request);
        } else return new ResponseEntity<>("No permission", HttpStatus.FORBIDDEN);
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public Object loginPOST(HttpServletRequest request) {
        return ioTService.loginPOST(request);
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public Object registerPOST(ModelMap modelMap, HttpServletRequest request) {
        return ioTService.registerPOST(modelMap, request);
    }

    @RequestMapping(value = "/getStatusNoLogin", method = RequestMethod.POST, produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public @ResponseBody
    Object getStatusNologin(HttpServletRequest request) {
        return ioTService.getStatus(request);
    }

    @RequestMapping(value = "/changeSettingNoLogin", method = RequestMethod.POST)
    public @ResponseBody
    Object ChangeSettingNoLogin(HttpServletRequest request) {
        return ioTService.ChangeSetting(request);
    }

    @PostMapping(value = "/getSettingNoLogin", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public @ResponseBody
    Object getSettingNoLogin(HttpServletRequest request) {
        return ioTService.getSetting(request);
    }

    @RequestMapping(value = "/setDefaultNoLogin", method = RequestMethod.POST)
    public @ResponseBody
    Object setDefaultNoLogin(HttpServletRequest request) {
        return ioTService.setDefault(request);
    }

    @RequestMapping(value = "/getDefaultNoLogin", method = RequestMethod.POST, produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public @ResponseBody
    Object getDefaultNoLogin(HttpServletRequest request) {
        return ioTService.getDefault(request);
    }
}
