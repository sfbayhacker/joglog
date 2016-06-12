package jogLog.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 *
 * @author Kish
 */
@Entity
@Table(name = "users")
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String email;
    private String password;
    private String name;
    private String salt;
    private Date createdDate;
    private String deleted;
    
    @OneToOne
	@JoinColumn(name = "role_id")
    private Role role;
    
//    @OneToMany(mappedBy = "user")
//    private Set<Entry> entries;
    
    public User() {
        this.deleted = "N";
    }
    
    /**
     * @return the id
     */
    public long getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email the email to set
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the r
     */
    public Role getRole() {
        return role;
    }

    /**
     * @param r the r to set
     */
    public void setRole(Role r) {
        this.role = r;
    }

//    /**
//     * @return the entries
//     */
//    public Set<Entry> getEntries() {
//        return entries;
//    }
//
//    /**
//     * @param entries the entries to set
//     */
//    public void setEntries(Set<Entry> entries) {
//        this.entries = entries;
//    }

    /**
     * @return the createdDate
     */
    public Date getCreatedDate() {
        return createdDate;
    }

    /**
     * @param createdDate the createdDate to set
     */
    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    /**
     * @return the deleted
     */
    public String getDeleted() {
        return deleted;
    }

    /**
     * @param deleted the deleted to set
     */
    public void setDeleted(String deleted) {
        this.deleted = deleted;
    }

    /**
     * @return the salt
     */
    @JsonIgnore
    public String getSalt() {
        return salt;
    }

    /**
     * @param salt the salt to set
     */
    public void setSalt(String salt) {
        this.salt = salt;
    }
    
}
