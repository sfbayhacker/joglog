package jogLog.bean;

/**
 *
 * @author Kish
 */
public class JwtAuthResponse {

    private final String token;

    public JwtAuthResponse(String token) {
        this.token = token;
    }

    public String getToken() {
        return this.token;
    }
}