package jogLog.controller;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import jogLog.entity.Role;
import jogLog.security.AuthenticatedUser;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 *
 * @author Kish
 */
public class BaseController {

    protected static final String UNAUTHORIZED_ACCESS_MESSAGE = "Access denied";
    protected static final String BAD_CREDENTIALS_MESSAGE = "Bad credentials";
    protected static final String NOT_FOUND_MESSAGE = "Resource not found";
    protected static final String EMAIL_TAKEN = "Sorry, email is already taken! Please try another";
    protected static final String BAD_REQUEST_MESSAGE = "Illegal parameters";
    
    protected String getPrincipalRole() {
        AuthenticatedUser principal
                = (AuthenticatedUser)SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        String r = principal.getAuthorities().iterator().next().getAuthority();

        return r;
    }

    protected boolean hasAnyRole(String... roles) {
        String r = getPrincipalRole();

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

    protected boolean isAuthorizedToAccessRole(String role) {
        boolean hasAccess = false;

        switch (role) {
            case "ROLE_USER":
                hasAccess = hasAnyRole(Role.ADMIN, Role.MANAGER);
                break;
            case "ROLE_MANAGER":
                hasAccess = hasAnyRole(Role.ADMIN);
                break;
            case "ROLE_ADMIN":
                hasAccess = hasAnyRole(Role.ADMIN);
                break;
            default:
                break;
        }

        return hasAccess;
    }
    
    protected Set<String> getAllowedRolesForUserAccess() {
        String r = getPrincipalRole();
        
        Set<String> roles = new HashSet<>();
        
        switch (r) {
            case "ROLE_MANAGER":
                roles.add(Role.USER);
                break;
            case "ROLE_ADMIN":
                roles.add(Role.MANAGER);
                roles.add(Role.ADMIN);
                break;
            default:
                break;
        }
        
        return roles;
    }
}
