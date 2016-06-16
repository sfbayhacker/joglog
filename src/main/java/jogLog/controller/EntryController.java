package jogLog.controller;

import jogLog.entity.Entry;
import jogLog.entity.User;
import jogLog.repository.EntryDAO;
import jogLog.repository.UserDAO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import javax.persistence.EntityManagerFactory;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import jogLog.bean.WeeklySummary;
import jogLog.config.JsonDateSerializer;
import jogLog.entity.Role;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Kish
 */
@Api(value = "/api/entries", produces = MediaType.APPLICATION_JSON_VALUE, 
        consumes = MediaType.APPLICATION_JSON_VALUE, protocols = "https")
@RestController
@RequestMapping(value = {"/api/entries"}, produces = MediaType.APPLICATION_JSON_VALUE)
public class EntryController extends BaseController {

    private static final Logger logger = Logger.getLogger(EntryController.class);

    @Autowired
    private EntryDAO entryDAO;

    @Autowired
    private UserDAO userDAO;
    
    @Autowired
    EntityManagerFactory em;
    
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @ApiOperation(value = " Add a new entry ", response = String.class)
    @ApiImplicitParams({
        @ApiImplicitParam(name = "Authorization", value = "Authorization token", 
                required = true, dataType = "string", paramType = "header")
    })
    @RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<String> create(
            @ApiParam(name = "user", value = "User id", required = true)
            @RequestHeader("user") Long userId,
            @ApiParam(name = "entryDate", value = "Entry date", required = true)
            @RequestHeader("entryDate") String date,
            @ApiParam(name = "time", value = "Entry time", required = true)
            @RequestHeader("time") Integer time,
            @ApiParam(name = "distance", value = "Distance covered", required = true)
            @RequestHeader("distance") Float distance,
            HttpServletRequest request,
            HttpServletResponse response) throws IOException {
        
        logger.info("create()");
        
        User u = userDAO.findOne(userId);
        
        if (u == null) {
            response.sendError(HttpStatus.BAD_REQUEST.value(), BAD_REQUEST_MESSAGE);
            return null;
        }
        
        if (isUnauthorizedAccessByUser(u.getEmail())) {
            response.sendError(HttpStatus.FORBIDDEN.value(), UNAUTHORIZED_ACCESS_MESSAGE);
            return null;
        }
        
        try {
            Entry entry = new Entry();
            entry.setDate(new SimpleDateFormat("yyyy-MM-dd").parse(date));
            entry.setTime(time);
            entry.setDistance(distance);
            entry.setUser(u);
            
            entryDAO.save(entry);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error adding the entry: " + ex.toString()) ;
        }
        
        return ResponseEntity.ok("Entry successfully added!");
    }
    
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @ApiOperation(value = " Get all entries ", response = Entry.class)
    @ApiImplicitParams({
        @ApiImplicitParam(name = "Authorization", value = "Authorization token", 
                required = true, dataType = "string", paramType = "header")
    })
    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<Entry> getEntries(
            @ApiParam(name = "user", value = "User id", required = true, defaultValue="-1")
            @RequestHeader("user") Long userId,
            
            @ApiParam(name = "fromDate", value = "Entry from date", required = false)
            @RequestHeader(name = "fromDate", required = false) @DateTimeFormat(pattern="yyyy-MM-dd") Date fromDate,
            @ApiParam(name = "toDate", value = "Entry to date", required = false)
            @RequestHeader(name = "toDate", required = false) @DateTimeFormat(pattern="yyyy-MM-dd") Date toDate,
            
            @ApiParam(name = "page", value = "The page number", required = false, defaultValue="0")
            @RequestHeader(name = "page", required = false, defaultValue = "0") int page, 
            @ApiParam(name = "size", value = "The page size", required = false, defaultValue="0")
            @RequestHeader(name = "size", required = false, defaultValue = "0") int size,
            HttpServletResponse response) throws IOException {
        logger.info("getEntries()");
        
        PageRequest pageRequest = null;
        
        if (size>0) {
            pageRequest = new PageRequest(page, size);
        }
        
        boolean dateFilter = (fromDate != null && toDate != null);
        
        User user = userDAO.findOne(userId);
        
        boolean userFilter = hasAnyRole(Role.USER);
        
        if (user == null || isUnauthorizedAccessByUser(user.getEmail())) {
            response.sendError(HttpStatus.FORBIDDEN.value(), UNAUTHORIZED_ACCESS_MESSAGE);
            return null;
        }
        
        if (dateFilter) {
            if (userFilter) {
                logger.info("D=1 U=1");
                return entryDAO.findByDateBetweenAndUser_id(fromDate, toDate, userId, pageRequest);
            } else {
                logger.info("D=1 U=0");
                return entryDAO.findByDateBetween(fromDate, toDate, pageRequest);
            }
        }
        
        if (userFilter) {
            logger.info("D=0 U=1");
            return entryDAO.findByUser_id(userId, pageRequest);
        } else {
            logger.info("D=0 U=0");
            return entryDAO.findAll(pageRequest);
        }
    }

    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @ApiOperation(value = " Get entry ", response = Entry.class)
    @ApiImplicitParams({
        @ApiImplicitParam(name = "Authorization", value = "Authorization token", 
                required = true, dataType = "string", paramType = "header")
    })
    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Entry getEntry(
            @ApiParam(name = "id", value = "Entry id", required = true)
            @PathVariable(value="id") Long id,
            HttpServletResponse response) throws IOException {
        logger.info("getEntry()");
        
        Entry entry = entryDAO.findOne(id);
        
        if (entry == null) {
            response.sendError(HttpStatus.NOT_FOUND.value(), NOT_FOUND_MESSAGE);
            return null;
        }
        
        if (isUnauthorizedAccessByUser(entry.getUser().getEmail())) {
            response.sendError(HttpStatus.FORBIDDEN.value(), UNAUTHORIZED_ACCESS_MESSAGE);
            return null;
        }
        
        return entry;
    }

    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @ApiOperation(value = " Remove entry ", response = Void.class)
    @ApiImplicitParams({
        @ApiImplicitParam(name = "Authorization", value = "Authorization token", 
                required = true, dataType = "string", paramType = "header")
    })
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeEntry(
            @ApiParam(name = "id", value = "Entry id", required = true)
            @PathVariable(value="id") Long id,
            HttpServletResponse response) throws IOException {
        logger.info("removeEntry()");
        
        Entry entry = entryDAO.findOne(id);
        
        if (entry == null) {
            response.sendError(HttpStatus.NOT_FOUND.value(), NOT_FOUND_MESSAGE);
            return;
        }
        
        if (isUnauthorizedAccessByUser(entry.getUser().getEmail())) {
            response.sendError(HttpStatus.FORBIDDEN.value(), UNAUTHORIZED_ACCESS_MESSAGE);
            return;
        }
        
        entryDAO.delete(id);
    }

    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @ApiOperation(value = " Update entry ", response = Void.class)
    @ApiImplicitParams({
        @ApiImplicitParam(name = "Authorization", value = "Authorization token", 
                required = true, dataType = "string", paramType = "header")
    })
    @RequestMapping(method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public void updateEntry(
            @ApiParam(name = "id", value = "Entry id", required = true)
            @RequestHeader("id") Long id,
            @ApiParam(name = "entryDate", value = "Entry date", required = true)
            @RequestHeader("entryDate") String date,
            @ApiParam(name = "time", value = "Entry time", required = true)
            @RequestHeader("time") Integer time,
            @ApiParam(name = "distance", value = "Distance covered", required = true)
            @RequestHeader("distance") Float distance,
            HttpServletRequest request,
            HttpServletResponse response) throws IOException {
        logger.info("updateEntry()");
        
        Entry entry = entryDAO.findOne(id);
        
        if (entry == null) {
            response.sendError(HttpStatus.NOT_FOUND.value(), NOT_FOUND_MESSAGE);
            return;
        }
        
        if (isUnauthorizedAccessByUser(entry.getUser().getEmail())) {
            response.sendError(HttpStatus.FORBIDDEN.value(), UNAUTHORIZED_ACCESS_MESSAGE);
            return;
        }
        
        try {
            entry.setDate(new SimpleDateFormat("yyyy-MM-dd").parse(date));
        } catch (ParseException ex) {
            logger.error(ex);
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            response.getWriter().write("Date format is invalid.");
        }
        
        entry.setTime(time);
        entry.setDistance(distance);
        
        entryDAO.save(entry);
    }
    
    @PreAuthorize("hasAnyRole('ROLE_USER')")
    @ApiOperation(value = " Get weekly report ", response = WeeklySummary.class)
    @ApiImplicitParams({
        @ApiImplicitParam(name = "Authorization", value = "Authorization token", 
                required = true, dataType = "string", paramType = "header")
    })
    @RequestMapping(value = "/summary", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<WeeklySummary> getWeeklySummary(
            @ApiParam(name = "user", value = "User id", required = true)
            @RequestParam("user") Long userId,
            @ApiParam(name = "weekStartDate", value = "Entry date", required = false)
            @RequestParam(name = "weekStartDate", required = false) String weekStartDate,
            HttpServletRequest request,
            HttpServletResponse response) throws IOException {
        logger.info("getWeeklySummary()");

        Calendar c;
        if (weekStartDate == null || weekStartDate.trim().equals("")) {
            logger.info("weekStartDate is NULL");
            c = Calendar.getInstance();
        } else {
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
            try {
                Date date = format.parse(weekStartDate);
                c = Calendar.getInstance();
                c.setTime(date);
            } catch (ParseException ex) {
                ex.printStackTrace();
                logger.error(ex.getMessage());
                response.sendError(HttpStatus.BAD_REQUEST.value(), BAD_REQUEST_MESSAGE);
                return null;
            }
        }
        
        c.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        weekStartDate = JsonDateSerializer.DATE_FORMAT.format(c.getTime());
        
        logger.info("weekStartDate :: " + weekStartDate);
        
        List<Object[]> results = em.createEntityManager()
                .createNativeQuery(
                        "select sum(distance) as totalDistance, (sum(distance) * 60 / sum(time)) as "
                        + "averageSpeed from entries where user_id = :userId and date >= :weekStartDate and "
                        + "date <= date_add(:weekStartDate, INTERVAL 6 day)"
                )
                .setParameter("userId", userId)
                .setParameter("weekStartDate", weekStartDate).getResultList();

        Object[] record = results.get(0);
        
        WeeklySummary summary = new WeeklySummary();
        summary.setWeekStartDate(weekStartDate);
        summary.setTotalDistance(
            String.format( "%.1f", (record[0]==null?0.0:(BigDecimal)record[0]).floatValue() )
        );
        summary.setAverageSpeed(
            String.format(  "%.2f", (record[1]==null?0.0:(BigDecimal)record[1]).floatValue() )      
        );

        logger.info(summary);
        
        return ResponseEntity.ok(summary);
    }
}
