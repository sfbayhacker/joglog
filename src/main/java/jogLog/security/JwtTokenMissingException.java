package jogLog.security;

import org.springframework.security.core.AuthenticationException;

/**
 *
 * @author Kish
 */
public class JwtTokenMissingException extends AuthenticationException {
    private static final long serialVersionUID = 3215084135015963117L;


    public JwtTokenMissingException(String msg) {
        super(msg);
    }
}
