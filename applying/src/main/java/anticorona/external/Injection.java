package anticorona.external;

public class Injection {

    private Long injectionId;
    private Long bookingId;
    private Long vaccineId;
    private Long userId;
    private String status;

    
    public Long getInjectionId() {
        return injectionId;
    }
    public void setInjectionId(Long injectionId) {
        this.injectionId = injectionId;
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

}
