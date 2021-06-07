# 구현
분석/설계 단계에서 도출된 헥사고날 아키텍처에 따라,구현한 각 서비스를 로컬에서 실행하는 방법은 아래와 같다
(각자의 포트넘버는 8081 ~ 8084, 8088 이다)
```
cd vaccine
mvn spring-boot:run

cd booking
mvn spring-boot:run 

cd mypage 
mvn spring-boot:run 

cd injection 
mvn spring-boot:run

cd gateway
mvn spring-boot:run 
```

# DDD 적용 

msaez.io 를 통해 구현한 Aggregate 단위로 Entity 를 선언 후, 구현을 진행하였다.
Entity Pattern 과 Repository Pattern을 적용하기 위해 Spring Data REST 의 RestRepository 를 적용하였다.

```
Booking 서비스의 Book.java

package anticorona;

import javax.persistence.*;
import org.springframework.beans.BeanUtils;
import org.springframework.hateoas.ResourceSupport;

import java.util.List;
import java.util.Date;

@Entity
@Table(name="Booking")
public class Booking extends ResourceSupport {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long bookingId;
    private Long vaccineId;
    private String vcName;
    private Long userId;
    private String status;

    @PrePersist
    public void onPrePersist(){
        this.setStatus("BOOKED");
    }

    @PostPersist
    public void onPostPersist() throws Exception {
        if(BookingApplication.applicationContext.getBean(anticorona.external.VaccineService.class)
            .checkAndBookStock(this.vaccineId)){
                Booked booked = new Booked();
                BeanUtils.copyProperties(this, booked);
                booked.publishAfterCommit();
            }
        else{
            throw new Exception("Out of Stock Exception Raised.");
        }

    }

    @PreUpdate
    @PostRemove
    public void onCancelled(){
        if("BOOKING_CANCELLED".equals(this.status)){
            BookingCancelled bookingCancelled = new BookingCancelled();
            BeanUtils.copyProperties(this, bookingCancelled);
            bookingCancelled.publishAfterCommit();
        }
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

}
```

 Booking 서비스의 PolicyHandler.java
```
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
    
    @Autowired BookingRepository bookingRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverCompleted_UpdateStatus(@Payload Completed completed){

        if(!completed.validate()) return;

        System.out.println("\n\n##### listener UpdateStatus : " + completed.toJson() + "\n\n");
        Optional<Booking> booking = bookingRepository.findById(completed.getBookingId());
        if(booking.isPresent()){
            Booking bookingValue = booking.get();
            bookingValue.setStatus("INJECTION_COMPLETED");
            bookingRepository.save(bookingValue);
        }
            
    }


    @StreamListener(KafkaProcessor.INPUT)
    public void whatever(@Payload String eventString){}


}
```

 Booking 서비스의 BookingRepository.java


``` 
package anticorona;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel="bookings", path="bookings")
public interface BookingRepository extends PagingAndSortingRepository<Booking, Long>{


}
```

DDD 적용 후 REST API의 테스트를 통하여 정상적으로 동작하는 것을 확인할 수 있었다.




# GateWay 적용
API GateWay를 통하여 마이크로 서비스들의 집입점을 통일할 수 있다. 
다음과 같이 GateWay를 적용하였다.

```
server:
  port: 8088
---
spring:
  profiles: default
  cloud:
    gateway:
      routes:
        - id: vaccine
          uri: http://localhost:8081
          predicates:
            - Path=/vaccines/** 
        - id: booking
          uri: http://localhost:8082
          predicates:
            - Path=/bookings/** 
        - id: mypage
          uri: http://localhost:8083
          predicates:
            - Path= /mypages/**
        - id: injection
          uri: http://localhost:8084
          predicates:
            - Path=/injections/**,/cancellations/** 
      globalcors:
        corsConfigurations:
          '[/**]':
            allowedOrigins:
              - "*"
            allowedMethods:
              - "*"
            allowedHeaders:
              - "*"
            allowCredentials: true
---

spring:
  profiles: docker
  cloud:
    gateway:
      routes:
        - id: vaccine
          uri: http://vaccine:8080
          predicates:
            - Path=/vaccines/** 
        - id: booking
          uri: http://booking:8080
          predicates:
            - Path=/bookings/** 
        - id: mypage
          uri: http://mypage:8080
          predicates:
            - Path= /mypages/**
        - id: injection
          uri: http://injection:8080
          predicates:
            - Path=/injections/**,/cancellations/** 
      globalcors:
        corsConfigurations:
          '[/**]':
            allowedOrigins:
              - "*"
            allowedMethods:
              - "*"
            allowedHeaders:
              - "*"
            allowCredentials: true

server:
  port: 8080 
```  
mypage 서비스의 GateWay 적용


![image](https://user-images.githubusercontent.com/82795860/120988904-f0eeef80-c7b9-11eb-92e3-ed97ecc2b047.png)




# CQRS 

Materialized View 를 구현하여, 타 마이크로서비스의 데이터 원본에 접근없이(Composite 서비스나 조인SQL 등 없이) 도 내 서비스의 화면 구성과 잦은 조회가 가능하게 구현해 두었다.
본 프로젝트에서 View 역할은 mypage 서비스가 수행한다.

예약(Booked) 실행 후 myPage 화면
 
![image](https://user-images.githubusercontent.com/82795860/121005958-526b8a00-c7cb-11eb-9bae-ad4bd70ef2eb.png)



![image](https://user-images.githubusercontent.com/82795860/121006311-bb530200-c7cb-11eb-9d85-a7b22d1a2729.png)

` 
위와 같이 주문을 하게되면 Booking -> Vaccine -> Booking  로 예약이 Assigend 되고

예약 취소가 되면 Status가 "BOOKING_CANCELLED"로 Update 되는 것을 볼 수 있다.

또한 Correlation을 key를 활용하여 bookingId가 Key값을 하고 예약과 접종 서비스간의 공유가 이루어 졌다.

위 결과로 서로 다른 마이크로 서비스 간에 트랜잭션이 묶여 있음을 알 수 있다.






# 폴리글랏 
mypage 서비스의 DB와 Bookingn/injection/vaccine 서비스의 DB를 다른 DB를 사용하여 MSA간 서로 다른 종류의 DB간에도 문제 없이 동작하여 다형성을 만족하는지 확인하였다.
(폴리글랏을 만족)

Bookingn/injection/vaccine 의 pom.xml DB 설정 코드 (H2 DB)
 
![image](https://user-images.githubusercontent.com/82795860/120964508-664cc700-c79e-11eb-9de6-8669f1238904.png)

mypage 의 pom.xml DB 설정 코드(Hsql DB)
 
![image](https://user-images.githubusercontent.com/82795860/120964600-83819580-c79e-11eb-8e4d-8be99afa2073.png)


 
# 동기식 호출 과 Fallback 처리

분석단계에서의 조건 중 하나로  접종 예약 수량은 백신 재고수량을 초과 할 수 없으며
예약(Booking)->(Vaccine) 간의 호출은 동기식 일관성을 유지하는 트랜잭션으로 처리하기로 하였다. 
호출 프로토콜은 Rest Repository 에 의해 노출되어있는 REST 서비스를 FeignClient 를 이용하여 호출하도록 한다.



Booking 서비스 내 external.VaccineService
```
package anticorona.external;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;

@FeignClient(name="vaccine", url="http://${api.url.vaccine}:8080")
public interface VaccineService {

    @RequestMapping(method= RequestMethod.GET, path="/vaccines/checkAndBookStock")
    public boolean checkAndBookStock(@RequestParam Long vaccineId);

}
```

동작 확인

접종 예약하기 시도 시  백신의 재고 수량을 체크함

![image](https://user-images.githubusercontent.com/82795860/120994076-1e8a6780-c7bf-11eb-8374-53f7a4336a1a.png)


접종 예약 시 백신 재고수량을 초과하지 않으면 예약 가능

![image](https://user-images.githubusercontent.com/82795860/120997798-78406100-c7c2-11eb-90fa-b8ff71f53c77.png)


접종 예약시 백신 재고수량을 초과하여 예약시 예약안됨

![image](https://user-images.githubusercontent.com/82795860/120993294-5b099380-c7be-11eb-8970-b2b0e28d6e40.png)

