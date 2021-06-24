package anticorona;

public class Applied extends AbstractEvent {

    private Long applyingId;
    private Long bookingId;
    private Long vaccineId;
    private String vcName;
    private Long userId;
    private String status;
    private String apStatus;
    

    public Long getApplyingId() {
        return applyingId;
    }
    public void setApplyingId(Long applyingId) {
        this.applyingId = applyingId;
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
    public String getVcName() {
        return vcName;
    }
    public void setVcName(String vcName) {
        this.vcName = vcName;
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

    public boolean isMe() {
        return false;
    }
}