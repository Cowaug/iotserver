package vn.edu.hcmut.iotserver;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import vn.edu.hcmut.iotserver.Entities.Permissions;
import vn.edu.hcmut.iotserver.Entities.User;
import vn.edu.hcmut.iotserver.database.JawMySQL;

import javax.servlet.http.HttpServletRequest;

import static vn.edu.hcmut.iotserver.Entities.Attributes.*;

@Controller
public class WebController {
    @GetMapping("/greeting")
    public String greeting(@RequestParam(name = "name", required = false, defaultValue = "World") String name, Model model) {
        model.addAttribute("name", name);
        return "greeting";
    }

    @GetMapping(value = "/getStatus", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public @ResponseBody
    Object getStatus(HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute(USER_INFO.toString());
        if (user != null && user.getPermission() == Permissions.ADMIN)
            return "this is encrypted data object file".getBytes();
        else return new ResponseEntity<>("No permission", HttpStatus.FORBIDDEN);
    }

    @GetMapping(value = "/getStatusNoLogin", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public @ResponseBody
    Object getStatusNologin(HttpServletRequest request) {
        return "this is encrypted data object file".getBytes();
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public Object loginPOST(ModelMap modelMap, HttpServletRequest request) {
        try {
            // authentication
            User user = JawMySQL.login(request.getParameter("user-id"), request.getParameter("password"));

            if (user != null) {
                request.getSession().setAttribute(USER_INFO.toString(), user);
                return new ResponseEntity<>("Login success", HttpStatus.OK);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return new ResponseEntity<>("No permission", HttpStatus.FORBIDDEN);
    }


//    @RequestMapping(value = "/login", method = RequestMethod.GET)
//    public Object loginGET(ModelMap modelMap, HttpServletRequest request) {
//        try {
//            // authentication
//            User user = JawMySQL.login("admin", "admin");
//
//            if (user != null) {
//                request.getSession().setAttribute(USER_INFO.toString(), user);
//                return new ResponseEntity<>("Login success as " + user.getPermission() + " " + user.getUserId(), HttpStatus.OK);
//            }
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//        return new ResponseEntity<>("No permission", HttpStatus.FORBIDDEN);
//    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public Object registerPOST(ModelMap modelMap, HttpServletRequest request) {
        try {
            // authentication
            User user = JawMySQL.register(request.getParameter("user-id"), request.getParameter("password"));

            request.getSession().setAttribute(USER_INFO.toString(), user);
            return new ResponseEntity<>("Register success", HttpStatus.OK);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return new ResponseEntity<>("Register fail, check username and password.", HttpStatus.BAD_REQUEST);
    }
}