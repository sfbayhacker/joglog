package jogLog.controller;

import jogLog.entity.Role;
import jogLog.entity.User;
import jogLog.repository.UserDAO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Kish
 */
@Api(value = "/api/registration", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE, protocols = "http")
@RestController
@RequestMapping(value = {"/api/registration"}, produces = MediaType.APPLICATION_JSON_VALUE)
public class RegistrationController {

    private static final Logger logger = Logger.getLogger(RegistrationController.class);
    
    @Autowired
    UserDAO userDAO;
    
    @ApiOperation(value = " Register a new user ", response = User.class)
    @RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public User register(
            @ApiParam(name = "email", value = "User email id", required = true)
            @RequestHeader("email") String email,
            @ApiParam(name = "name", value = "User name", required = true)
            @RequestHeader("name") String name,
            @ApiParam(name = "password", value = "User password", required = true)
            @RequestHeader("password") String password,
            HttpServletResponse response) {
        
        logger.debug("create()");
 
        User user = null;
        try {
            
            user = userDAO.findOneByEmail(email);
            
            if (user!=null) {
                response.sendError(HttpStatus.BAD_REQUEST.value(), 
                        "Sorry, email is already taken. Please try again!");
                return null;
            }
            
            user = new User();
            user.setEmail(email);
            user.setName(name);
            user.setPassword(password);
            user.setRole(new Role("USER"));
            
            user = userDAO.save(user);
        } catch (Exception ex) {
            logger.error(ex.getMessage());
        }
        
        return user;
    }
}
