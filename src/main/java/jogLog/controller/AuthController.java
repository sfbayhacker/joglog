package jogLog.controller;

import com.google.common.hash.Hashing;
import jogLog.entity.User;
import jogLog.repository.UserDAO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import jogLog.bean.JwtAuthResponse;
import jogLog.security.JwtUtil;
import org.apache.commons.codec.Charsets;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Kish
 */
@Api(value = "/auth", produces = MediaType.APPLICATION_JSON_VALUE, 
        consumes = MediaType.APPLICATION_JSON_VALUE, protocols = "https")
@RestController
@RequestMapping(value = {"/auth"}, produces = MediaType.APPLICATION_JSON_VALUE)
public class AuthController extends BaseController {

    private static final Logger logger = Logger.getLogger(AuthController.class);
    
    @Autowired
    private UserDAO userDAO;

    @Autowired
    private JwtUtil jwtTokenUtil;

    @ApiOperation(value = " Authenticate user ", response = JwtAuthResponse.class)
    @RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> authenticate(
            @ApiParam(name = "email", value = "User email id", required = true)
            @RequestHeader("email") String email,
            @ApiParam(name = "password", value = "User password", required = true)
            @RequestHeader("password") String password) throws AuthenticationException {
        logger.info("authenticate("+email+","+password+")");
        
        User user = userDAO.findOneByEmail(email);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(BAD_CREDENTIALS_MESSAGE);
        }
        
        final String hashed = Hashing.sha256()
        .hashString(password + user.getSalt(), Charsets.UTF_8)
        .toString();
        
        logger.info("hashString :: " + (password + user.getSalt()));
        logger.info("hash :: " + hashed);
        
        if (!hashed.equals(user.getPassword())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(BAD_CREDENTIALS_MESSAGE);
        }
        
        final String token = jwtTokenUtil.generateToken(user);

        // Return the token
        return ResponseEntity.ok(new JwtAuthResponse(user, token));
    }
    
}
