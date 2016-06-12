/*
 * Copyright 2015, FixStream Networks, Inc. All rights reserved.
 */
package jogLog.security;

import jogLog.entity.User;
import jogLog.repository.UserDAO;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.dao.SaltSource;
import org.springframework.security.core.userdetails.UserDetails;

/**
 *
 * @author Kish
 */
public class ApplicationSaltSource implements SaltSource {

    private static final Logger logger = Logger.getLogger(ApplicationSaltSource.class);
    
    @Autowired
    private UserDAO userDAO;
    
    @Override
    public Object getSalt(UserDetails ud) {
        logger.info("getSalt()");
        User u = userDAO.findOne( ((AuthenticatedUser)ud).getId()) ;
        
        return (Long)u.getCreatedDate().getTime();
    }
    
}