package jogLog.controller;

import java.util.Arrays;
import jogLog.security.AuthenticatedUser;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 *
 * @author Kish
 */
public class BaseController {

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
}
