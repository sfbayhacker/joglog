package jogLog.controller;

import jogLog.entity.Role;
import jogLog.entity.User;
import jogLog.repository.EntryDAO;
import jogLog.repository.RoleDAO;
import jogLog.repository.UserDAO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Kish
 */
@Api(value = "/api/users", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE, protocols = "http")
@RestController
@RequestMapping(value = {"/api/users"}, produces = MediaType.APPLICATION_JSON_VALUE)
public class UserController extends BaseController {

    private static final Logger logger = Logger.getLogger(UserController.class);

    @Autowired
    UserDAO userDAO;

    @Autowired
    EntryDAO entryDAO;

    @Autowired
    RoleDAO roleDAO;

    @ApiOperation(value = " Creates a user ", response = String.class)
    @RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<String> create(
            @ApiParam(name = "email", value = "User email id", required = true)
            @RequestHeader("email") String email,
            @ApiParam(name = "name", value = "User name", required = true)
            @RequestHeader("name") String name,
            @ApiParam(name = "password", value = "User password", required = true)
            @RequestHeader("password") String password,
            @ApiParam(name = "role", value = "User role", required = true, defaultValue = "USER", allowableValues = "ADMIN, MANAGER, USER")
            @RequestHeader("role") String roleId,
            HttpServletRequest request,
            HttpServletResponse response) throws IOException {

        logger.debug("create()");

        if (!hasAnyRole("ADMIN", "MANAGER") || 
                (!Role.USER.equals(roleId) && !hasAnyRole("ADMIN"))
            ) {
            response.sendError(HttpStatus.UNAUTHORIZED.value(), "Bad Credentials");
            return null;
        }
        
        try {

            User user = userDAO.findOneByEmail(email);

            if (user != null) {
                response.sendError(HttpStatus.BAD_REQUEST.value(), "Sorry, email is already taken!");
                return new ResponseEntity<>("Sorry, email is already taken!", HttpStatus.BAD_REQUEST);
            }

            user = new User();
            user.setEmail(email);
            user.setName(name);
            user.setPassword(password);
            user.setRole(new Role(roleId));

            userDAO.save(user);
        } catch (Exception ex) {
            response.sendError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Oops! There was an error creating the user. Please try again later!");
            return new ResponseEntity<>("Error creating the user: " + ex.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>("User succesfully created!", HttpStatus.CREATED);
    }

    @ApiOperation(value = " Get users. Allows filtering based on role and email. ", response = User.class)
    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<User> getUsers(
            @ApiParam(name = "field", value = "The filter field", required = false, defaultValue = "role", allowableValues = "role, email")
            @RequestParam(name = "field", required = false) String field,
            @ApiParam(name = "value", value = "The field value", required = false)
            @RequestParam(name = "value", required = false) String value,
            @ApiParam(name = "page", value = "The page number", required = false, defaultValue = "0")
            @RequestHeader(name = "page", required = false) Integer page,
            @ApiParam(name = "size", value = "The page size", required = false, defaultValue = "0")
            @RequestHeader(name = "size", required = false) Integer size,
            HttpServletResponse response) throws IOException {
        logger.debug("getUsers(" + field + "," + value + ")");
        
        if (!hasAnyRole("ADMIN", "MANAGER")) {
            response.sendError(HttpStatus.UNAUTHORIZED.value(), "Bad Credentials");
            return null;
        }
        
        //System.out.println("Principal :: " + p);
        
        PageRequest pageRequest = null;
        
        if (size != null && size > 0) {
            pageRequest = new PageRequest(page, size);
        }
        
        if (value == null) {
            return userDAO.findAll(pageRequest);
        } else {
            if ("role".equals(field)) {
                return userDAO.findByRole_id(value, pageRequest);
            } else {
                return Arrays.asList(userDAO.findOneByEmail(value));
            }
        }
    }

    @ApiOperation(value = " Get user ", response = User.class)
    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public User getUser(
            @ApiParam(name = "id", value = "User id", required = true)
            @PathVariable(value = "id") Long id,
            HttpServletResponse response) throws IOException {
        logger.debug("getUser()");

        User u = userDAO.findOne(id);
        
        boolean sameUser = getPrincipalEmail().equals(u.getEmail());
        
        if (!sameUser && !hasAnyRole("ADMIN", "MANAGER")) {
            response.sendError(HttpStatus.UNAUTHORIZED.value(), "Bad Credentials");
            return null;
        }
        
        return u;
    }

    @ApiOperation(value = " Remove user ", response = User.class)
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeUser(
            @ApiParam(name = "id", value = "User id", required = true)
            @PathVariable(value = "id") Long id,
            HttpServletResponse response) throws IOException {
        logger.debug("removeUser()");

        User u = userDAO.findOne(id);
        
        if (!hasAnyRole("ADMIN", "MANAGER") || 
                (!Role.USER.equals(u.getRole().getId()) && !hasAnyRole("ADMIN"))
            ) {
            response.sendError(HttpStatus.UNAUTHORIZED.value(), "Bad Credentials");
            return;
        }
        
        userDAO.delete(id);
    }

    @ApiOperation(value = " Update user ")
    @RequestMapping(method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public void updateUser(
            @ApiParam(name = "id", value = "User id", required = true)
            @RequestHeader("id") Long id,
            @ApiParam(name = "email", value = "User email id", required = true)
            @RequestHeader("email") String email,
            @ApiParam(name = "name", value = "User name", required = true)
            @RequestHeader("name") String name,
            @ApiParam(name = "password", value = "User password", required = true)
            @RequestHeader("password") String password,
            @ApiParam(name = "role", value = "User role", required = true, defaultValue = "USER", allowableValues = "ADMIN, MANAGER, USER")
            @RequestHeader("role") String roleId,
            HttpServletResponse response) throws IOException {
        logger.debug("updateUser()");

        User u = userDAO.findOne(id);
        
        if (!hasAnyRole("ADMIN", "MANAGER") || 
                (!Role.USER.equals(u.getRole().getId()) && !hasAnyRole("ADMIN"))
            ) {
            response.sendError(HttpStatus.UNAUTHORIZED.value(), "Bad Credentials");
            return;
        }

        String oldEmail = u.getEmail();
        User eUser;

        if (!oldEmail.equals(email)) {
            eUser = userDAO.findOneByEmail(email);
            if (eUser != null) {
                response.sendError(HttpStatus.BAD_REQUEST.value(), "Email id is already taken. Please try another.");
                return;
            }

            u.setEmail(email);
        }

        u.setName(name);
        u.setPassword(password);

        Role r = roleDAO.findOne(roleId);
        u.setRole(r);

        userDAO.save(u);
    }

}