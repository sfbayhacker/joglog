package jogLog.repository;

import jogLog.entity.Entry;
import java.util.Date;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

/**
 *
 * @author Kish
 */
public interface EntryDAO extends CrudRepository<Entry, Long> {
    List<Entry> findAll(Pageable pageable);
    List<Entry> findByUser_id(Long userId, Pageable pageable);
    List<Entry> findByDate(Date date);
    List<Entry> findByDateBetweenAndTimeBetween(Date fromDate, Date toDate, 
            int fromTime, int toTime, Pageable pageable);
    List<Entry> findByDateBetweenAndTimeBetweenAndUser_id(Date fromDate, Date toDate, 
            int fromTime, int toTime, Long userId, Pageable pageable);
    List<Entry> findByDateBetween(Date fromDate, Date toDate, Pageable pageable);
    List<Entry> findByDateBetweenAndUser_id(Date fromDate, Date toDate, 
            Long userId, Pageable pageable);
    List<Entry> findByTimeBetween(int fromTime, int toTime, Pageable pageable);
    List<Entry> findByTimeBetweenAndUser_id(int fromTime, int toTime, 
            Long userId, Pageable pageable);
}
