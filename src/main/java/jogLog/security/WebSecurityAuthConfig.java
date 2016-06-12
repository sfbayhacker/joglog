package jogLog.security;

import jogLog.entity.User;
import jogLog.repository.UserDAO;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configurers.GlobalAuthenticationConfigurerAdapter;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 *
 * @author Kish
 */
//@Configuration
public class WebSecurityAuthConfig extends GlobalAuthenticationConfigurerAdapter {

    private static final Logger logger = Logger.getLogger(WebSecurityAuthConfig.class);

    @Autowired
    UserDAO userDAO;

    @Override
    public void init(AuthenticationManagerBuilder auth) throws Exception {
        logger.debug("init("+auth+")");
        auth.userDetailsService(userDetailsService());
    }

    @Bean
    UserDetailsService userDetailsService() {
        return new UserDetailsService() {

            @Override
            public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
                logger.debug("loadUserByUsername("+username+")");
                
                User user = userDAO.findOneByEmail(username);
                
                if (user != null) {
                    return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), user.getDeleted().equals("N"), true, true, true,
                            AuthorityUtils.createAuthorityList(user.getRole().getId()));
                } else {
                    throw new UsernameNotFoundException("could not find the user '"
                            + username + "'");
                }
            }

        };
    }
}
