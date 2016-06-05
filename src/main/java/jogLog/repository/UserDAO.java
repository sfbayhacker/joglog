package jogLog.repository;

import jogLog.entity.User;
import java.util.List;
import javax.transaction.Transactional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

/**
 *
 * @author Kish
 */
@Transactional
public interface UserDAO extends CrudRepository<User, Long> {
    List<User> findAll(Pageable pageable);
    List<User> findByRole_id(String roleId, Pageable pageable);
    User findOneByEmail(String email);
    User findOneByEmailAndPassword(String email, String password);
    void deleteByEmail(String email);
}
