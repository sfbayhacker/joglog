package jogLog.controller;

import java.util.Arrays;
import jogLog.entity.Role;
import jogLog.security.AuthenticatedUser;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 *
 * @author Kish
 */
public class BaseController {

    protected static final String UNAUTHORIZED_ACCESS_MESSAGE = "Unauthorized access";
    protected static final String BAD_CREDENTIALS_MESSAGE = "Bad credentials";
    
    protected boolean hasAnyRole(String... roles) {
        AuthenticatedUser principal
                = (AuthenticatedUser)SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        String r = principal.getAuthorities().iterator().next().getAuthority();

        System.out.println("user role :: " + r);
        
        return Arrays.stream(roles).anyMatch(role -> role.equals(r));
    }
    
    protected String getPrincipalEmail() {
        AuthenticatedUser principal
                = (AuthenticatedUser)SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        System.out.println("principal :: " + principal);
        
        return principal.getUsername();
    }
    
    protected boolean isAuthenticatedUser(String email) {
        return getPrincipalEmail().equals(email);
    }
    
    protected boolean isManagerOfUsers() {
        return !(hasAnyRole(Role.USER));
    }
    
    protected boolean isUnauthorizedAccessByUser(String email) {
        return (hasAnyRole(Role.USER) && !getPrincipalEmail().equals(email));
    }
}
