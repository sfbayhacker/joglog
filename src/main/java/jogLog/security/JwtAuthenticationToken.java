package jogLog.security;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

/**
 *
 * @author Kish
 */
public class JwtAuthenticationToken extends UsernamePasswordAuthenticationToken {
    private static final long serialVersionUID = -8596686267765275415L;

    private String token;

    public JwtAuthenticationToken(String email, String password) {
        super(email, password);
    }
    
    public JwtAuthenticationToken(String token) {
        super(null, null);
        this.token = token;
    }

    public String getToken() {
        return token;
    }
}
