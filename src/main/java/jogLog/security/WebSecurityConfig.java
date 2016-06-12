package jogLog.security;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 *
 * @author Kish
 */
//@EnableWebSecurity
//@EnableGlobalMethodSecurity(prePostEnabled=true)
//@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private static final Logger logger = Logger.getLogger(WebSecurityConfig.class);

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        logger.debug("configure("+http+")");
        http.authorizeRequests().
                antMatchers("/api/auth/*").permitAll().
                antMatchers("/api/register/*").permitAll().
                antMatchers("/api/*").fullyAuthenticated().
                and().httpBasic().
                //and().formLogin().loginPage("/#/login").permitAll().
                and().csrf().disable();
    }

}
