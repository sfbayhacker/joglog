package jogLog.security;

import org.springframework.security.core.AuthenticationException;

/**
 *
 * @author Kish
 */
public class JwtTokenMalformedException extends AuthenticationException {
    private static final long serialVersionUID = 8723708508585552811L;

    public JwtTokenMalformedException(String msg) {
        super(msg);
    }
}
