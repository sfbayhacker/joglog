package jogLog.repository;

import jogLog.entity.Role;
import org.springframework.data.repository.CrudRepository;

/**
 *
 * @author Kish
 */
public interface RoleDAO extends CrudRepository<Role, String> {
    
}
