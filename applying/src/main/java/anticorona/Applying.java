package anticorona;

import javax.persistence.*;
import org.springframework.beans.BeanUtils;
import org.springframework.hateoas.ResourceSupport;

import java.util.List;
import java.util.Date;

@Entity
@Table(name="Applying")
public class Applying extends ResourceSupport {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)

    private Long applyingId;
    private Long bookingId;
    private Long vaccineId;
    private String vcName;
    private Long userId;
    private String status;
    private String apStatus;

    @PrePersist
    public void onPrePersist(){

        //증명서 신청
        this.setStatus("APPLIED");
    }

    @PostPersist
    public void onPostPersist() throws Exception {
        if(ApplyingApplication.applicationContext.getBean(anticorona.external.InjectionService.class)
            .checkStatus(this.bookingId)){
                Applied applied = new Applied();
                BeanUtils.copyProperties(this, applied);
                applied.publishAfterCommit();
            }
        else{
            throw new Exception("접종완료여부확인필요");
        }

    }

    @PreUpdate
    @PostRemove
    public void onCancelled(){
        if("Applying_CANCELLED".equals(this.apStatus)){
            ApplyingCancelled applyingCancelled = new ApplyingCancelled();
            BeanUtils.copyProperties(this, applyingCancelled);
            applyingCancelled.publishAfterCommit();
        }
    }

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

}
