package jogLog.controller;

import org.springframework.security.core.context.SecurityContextHolder;

/**
 *
 * @author Kish
 */
public class BaseController {

    protected boolean hasAnyRole(String... roles) {
        org.springframework.security.core.userdetails.User principal
                = (org.springframework.security.core.userdetails.User) 
                SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        String r = principal.getAuthorities().iterator().next().getAuthority();

        for(String role: roles) {
            if (role.equals(r)) return true;
        }
        
        return false;
    }
    
    protected String getPrincipalEmail() {
        org.springframework.security.core.userdetails.User principal
                = (org.springframework.security.core.userdetails.User) 
                SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return principal.getUsername();
    }
}
