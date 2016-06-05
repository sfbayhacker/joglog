package jogLog.bean;

import jogLog.entity.User;

/**
 *
 * @author Kish
 */
public class AuthResponse {
    private boolean success;
    private User user;

    public AuthResponse(boolean success, User user) {
        this.success = success;
        this.user = user;
    }
    
    /**
     * @return the success
     */
    public boolean getSuccess() {
        return success;
    }

    /**
     * @param success the success to set
     */
    public void setSuccess(boolean success) {
        this.success = success;
    }

    /**
     * @return the user
     */
    public User getUser() {
        return user;
    }

    /**
     * @param user the user to set
     */
    public void setUser(User user) {
        this.user = user;
    }
}
