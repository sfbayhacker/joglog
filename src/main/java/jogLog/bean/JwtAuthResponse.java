package jogLog.bean;

import jogLog.entity.User;

/**
 *
 * @author Kish
 */
public class JwtAuthResponse {

    private final String token;
    private final User user;

    public JwtAuthResponse(User user, String token) {
        this.user = user;
        this.token = token;
    }

    public User getUser() {
        return this.user;
    }
    
    public String getToken() {
        return this.token;
    }
}