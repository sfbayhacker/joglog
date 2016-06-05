package jogLog.controller;

import jogLog.bean.AuthResponse;
import jogLog.entity.User;
import jogLog.repository.UserDAO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Kish
 */
@Api(value = "/api/authenticate", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE, protocols = "http")
@RestController
@RequestMapping(value = {"/api/authenticate"}, produces = MediaType.APPLICATION_JSON_VALUE)
public class AuthController {

    private static final Logger logger = Logger.getLogger(AuthController.class);
    
    @Autowired
    UserDAO userDAO;
    
    @ApiOperation(value = " Authenticate user ", response = AuthResponse.class)
    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public AuthResponse authenticate(
            @ApiParam(name = "email", value = "User email id", required = true)
            @RequestHeader("email") String email,
            @ApiParam(name = "password", value = "User password", required = true)
            @RequestHeader("password") String password) {
        
        logger.debug("authenticate("+email+","+password+")");
        
        User u = userDAO.findOneByEmailAndPassword(email, password);
        return new AuthResponse((u!=null), u);
    }
    
}
