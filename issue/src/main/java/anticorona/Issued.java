package anticorona;

public class Issued extends AbstractEvent {

    private Long issueId;
    private Long appylingId;
    private Long bookingId;
    private Long vaccineId;
    private Long userId;
    private String status;
    private String spStatus;

    
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
    public String getSpStatus() {
        return spStatus;
    }
    public void setSpStatus(String spStatus) {
        this.spStatus = spStatus;
    }

   
    
}