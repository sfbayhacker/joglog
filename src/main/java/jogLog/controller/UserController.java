package jogLog.controller;

import com.google.common.hash.Hashing;
import jogLog.entity.Role;
import jogLog.entity.User;
import jogLog.repository.EntryDAO;
import jogLog.repository.RoleDAO;
import jogLog.repository.UserDAO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import java.io.IOException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.codec.Charsets;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
@Api(value = "/api/users", produces = MediaType.APPLICATION_JSON_VALUE, 
        consumes = MediaType.APPLICATION_JSON_VALUE, protocols = "https")
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

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MANAGER')")
    @ApiOperation(value = " Creates a user ", response = String.class)
    @ApiImplicitParams({
        @ApiImplicitParam(name = "Authorization", value = "Authorization token", 
                required = true, dataType = "string", paramType = "header")
    })
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
            @ApiParam(name = "role", value = "User role", required = true, 
                    defaultValue = "USER", allowableValues = "ADMIN, MANAGER, USER")
            @RequestHeader("role") String roleId,
            HttpServletRequest request,
            HttpServletResponse response) throws IOException {

        logger.info("create()");

        if (hasAnyRole(Role.MANAGER) && !Role.USER.equals(roleId)) {
            response.sendError(HttpStatus.FORBIDDEN.value(), UNAUTHORIZED_ACCESS_MESSAGE);
            return null;
        }
        
        try {

            User user = userDAO.findOneByEmail(email);

            if (user != null) {
                return ResponseEntity.badRequest().body(EMAIL_TAKEN);
            }

            user = new User();
            user.setEmail(email);
            user.setName(name);
            
            long salt = Calendar.getInstance().getTime().getTime();
            
            final String hashed = Hashing.sha256()
            .hashString(password + salt, Charsets.UTF_8)
            .toString();
            
            System.out.println("hashString :: " + (password + salt));
            
            user.setPassword(hashed);
            user.setRole(new Role(roleId));
            user.setSalt(""+salt);

            userDAO.save(user);
        } catch (Exception ex) {
            response.sendError(HttpStatus.INTERNAL_SERVER_ERROR.value(), 
                    "Oops! There was an error creating the user. Please try again later!");
            return new ResponseEntity<>("Error creating the user: " + ex.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>("User succesfully created!", HttpStatus.CREATED);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_MANAGER')")
    @ApiOperation(value = " Get users. Allows filtering based on role and email. ", response = User.class)
    @ApiImplicitParams({
        @ApiImplicitParam(name = "Authorization", value = "Authorization token", 
                required = true, dataType = "string", paramType = "header")
    })
    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<User> getUsers(
            @ApiParam(name = "field", value = "The filter field", required = false, 
                    defaultValue = "role", allowableValues = "role, email")
            @RequestParam(name = "field", required = false) String field,
            @ApiParam(name = "value", value = "The field value", required = false)
            @RequestParam(name = "value", required = false) String value,
            @ApiParam(name = "page", value = "The page number", required = false, defaultValue = "0")
            @RequestHeader(name = "page", required = false) Integer page,
            @ApiParam(name = "size", value = "The page size", required = false, defaultValue = "0")
            @RequestHeader(name = "size", required = false) Integer size,
            HttpServletResponse response) throws IOException {
        logger.info("getUsers(" + field + "," + value + ")");
        
        PageRequest pageRequest = null;

        if (size != null && size > 0) {
            pageRequest = new PageRequest(page, size);
        }
        
        if (value == null) {
            return userDAO.findByRole_idIn(getAllowedRolesForUserAccess(), pageRequest);
        } else {
            if ("role".equals(field)) {  
                
                if (isAuthorizedToAccessRole(value)) {
                    return userDAO.findByRole_id(value, pageRequest);    
                } else {
                    //send error
                    response.sendError(HttpStatus.FORBIDDEN.value(), UNAUTHORIZED_ACCESS_MESSAGE);
                    return null;
                }

            } else {
                User u = userDAO.findOneByEmail(value);
                
                if (u != null) {
                    if (isAuthorizedToAccessRole(u.getRole().getId())) {
                        return Arrays.asList(u);    
                    } else {
                        //send error
                        response.sendError(HttpStatus.FORBIDDEN.value(), UNAUTHORIZED_ACCESS_MESSAGE);
                        return null;
                    }
                } else {
                    //@todo -- send resource not found error
                    response.sendError(HttpStatus.NOT_FOUND.value(), NOT_FOUND_MESSAGE);
                    return null;
                }
            }
        }
    }

    @ApiOperation(value = " Get user ", response = User.class)
    @ApiImplicitParams({
        @ApiImplicitParam(name = "Authorization", value = "Authorization token", 
                required = true, dataType = "string", paramType = "header")
    })
    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public User getUser(
            @ApiParam(name = "id", value = "User id", required = true)
            @PathVariable(value = "id") Long id,
            HttpServletResponse response) throws IOException {
        logger.info("getUser()");

        User u = userDAO.findOne(id);

        if (u == null) {
            response.sendError(HttpStatus.NOT_FOUND.value(), NOT_FOUND_MESSAGE);
            return null;
        }
        
        if ( !isManagerOfUsers() && !isAuthenticatedUser(u.getEmail()) ) {
            response.sendError(HttpStatus.FORBIDDEN.value(), UNAUTHORIZED_ACCESS_MESSAGE);
            return null;
        }
        
        return u;
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_MANAGER')")
    @ApiOperation(value = " Remove user ", response = User.class)
    @ApiImplicitParams({
        @ApiImplicitParam(name = "Authorization", value = "Authorization token", 
                required = true, dataType = "string", paramType = "header")
    })
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeUser(
            @ApiParam(name = "id", value = "User id", required = true)
            @PathVariable(value = "id") Long id,
            HttpServletResponse response) throws IOException {
        logger.info("removeUser()");

        User u = userDAO.findOne(id);
        
        if (u == null) {
            response.sendError(HttpStatus.NOT_FOUND.value(), NOT_FOUND_MESSAGE);
            return;
        }
        
        if ( hasAnyRole(Role.MANAGER) && !Role.USER.equals(u.getRole().getId()) ) {
            response.sendError(HttpStatus.FORBIDDEN.value(), UNAUTHORIZED_ACCESS_MESSAGE);
            return;
        }
        
        userDAO.delete(id);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_MANAGER')")
    @ApiOperation(value = " Update user ")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "Authorization", value = "Authorization token", 
                required = true, dataType = "string", paramType = "header")
    })
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
            @ApiParam(name = "role", value = "User role", required = true, 
                    defaultValue = "ROLE_USER", allowableValues = "ROLE_ADMIN, ROLE_MANAGER, ROLE_USER")
            @RequestHeader("role") String roleId,
            HttpServletResponse response) throws IOException {
        logger.info("updateUser()");

        User u = userDAO.findOne(id);
        
        if (u == null) {
            response.sendError(HttpStatus.NOT_FOUND.value(), NOT_FOUND_MESSAGE);
            return;
        }
        
        if ( hasAnyRole(Role.MANAGER) && !Role.USER.equals(u.getRole().getId()) ) {
            response.sendError(HttpStatus.FORBIDDEN.value(), UNAUTHORIZED_ACCESS_MESSAGE);
            return;
        }

        String oldEmail = u.getEmail();
        User eUser;

        if (!oldEmail.equals(email)) {
            eUser = userDAO.findOneByEmail(email);
            if (eUser != null) {
                response.sendError(HttpStatus.BAD_REQUEST.value(), 
                        EMAIL_TAKEN);
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
