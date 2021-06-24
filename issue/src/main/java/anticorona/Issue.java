package anticorona;

import javax.persistence.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import java.util.List;
import java.util.Date;

@Entity
@Table(name="Issue_table")
public class  Issue {

    @Transient
    Logger logger = LoggerFactory.getLogger(Issue.class);

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)

    private Long issueId;
    private Long appylingId;
    private Long bookingId;
    private Long vaccineId;
    private Long userId;
    private String status;
    private String apStatus;

    @PreUpdate
    public void onPreUpdate(){
        
        logger.info("Issue on PreUpdate Executed");
        // 발급 완료 처리 //
        Issued issued = new Issued();
        BeanUtils.copyProperties(this, issued);
        issued.setStatus("ISSUE_COMPLETED");
        issued.publishAfterCommit();      

    }

    public Logger getLogger() {
        return logger;
    }

    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    public Long getIssueId() {
        return issueId;
    }

    public void setIssueId(Long issueId) {
        this.issueId = issueId;
    }

    public Long getAppylingId() {
        return appylingId;
    }

    public void setAppylingId(Long appylingId) {
        this.appylingId = appylingId;
    }

    public Long getBookingId() {
        return bookingId;
    }

    public void setBookingId(Long bookingId) {
        this.bookingId = bookingId;
    }

    public Long getVaccineId() {
        return vaccineId;
    }

    public void setVaccineId(Long vaccineId) {
        this.vaccineId = vaccineId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getApStatus() {
        return apStatus;
    }

    public void setApStatus(String apStatus) {
        this.apStatus = apStatus;
    }




}
