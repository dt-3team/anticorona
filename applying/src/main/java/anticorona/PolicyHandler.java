
package anticorona;

import anticorona.config.kafka.KafkaProcessor;

import java.util.Optional;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
public class PolicyHandler{
    
    @Autowired ApplyingRepository applyingRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverIssued_UpdateStatus(@Payload Issued issued){

        if(!issued.validate()) return;

        System.out.println("\n\n##### listener UpdateStatus : " + issued.toJson() + "\n\n");


        Optional<Applying> applying = applyingRepository.findById(issued.getApplyingId());
        if(applying.isPresent()){

            Applying applyingValue = applying.get();
            applyingValue.setStatus("ISSUE_COMPLETED");
            applyingRepository.save(applyingValue);
        }
            
    }


    @StreamListener(KafkaProcessor.INPUT)
    public void whatever(@Payload String eventString){}


}
