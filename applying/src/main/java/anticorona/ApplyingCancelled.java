package anticorona;

public class ApplyingCancelled extends AbstractEvent {

    private Long applyingId;
    private Long bookingId;
    private Long vaccineId;
    private Long userId;
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
    public Long getUserId() {
        return userId;
    }
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    public String getApStatus() {
        return apStatus;
    }
    public void setApStatus(String apStatus) {
        this.apStatus = apStatus;
    }

   
}