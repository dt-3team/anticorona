# 3rd Team Project
## 코로나 백신 접종 알리미

### Repositories

- **게이트웨이** - [https://github.com/dt-3team/gateway.git](https://github.com/dt-3team/gateway.git)

- **백신 관리** - [https://github.com/dt-3team/vaccine.git](https://github.com/dt-3team/vaccine.git)

- **예약 관리** - [https://github.com/dt-3team/booking.git](https://github.com/dt-3team/booking.git)

- **접종 관리** - [https://github.com/dt-3team/injection.git](https://github.com/dt-3team/injection.git)

- **My Page** - [https://github.com/dt-3team/mypage.git](https://github.com/dt-3team/mypage.git)

- **Front End** - [https://github.com/dt-3team/frontend.git](https://github.com/dt-3team/frontend.git)

*전체 소스 받기*
```
git clone --recurse-submodules https://github.com/dt-3team/anticorona.git
```

### Table of contents

- [서비스 시나리오](#서비스-시나리오)
  - [기능적 요구사항](##기능적-요구사항)
  - [비기능적 요구사항](##비기능적-요구사항)
- [분석/설계](#분석설계)
  - [AS-IS 조직 (Horizontally-Aligned)](##AS-IS-조직-(Horizontally-Aligned))
  - [TO-BE 조직 (Vertically-Aligned)](##TO-BE-조직-(Vertically-Aligned))
  - [Event 도출](##Event-도출)
  - [부적격 이벤트 제거](##부적격-이벤트-제거)
  - [액터, 커맨드 부착](##액터,-커맨드-부착)
  - [어그리게잇으로 묶기](##어그리게잇으로-묶기)
  - [바운디드 컨텍스트로 묶기](##바운디드-컨텍스트로-묶기)
  - [폴리시 부착/이동 및 컨텍스트 매핑](##폴리시-부착/이동-및-컨텍스트-매핑)
  - [Event Storming 최종 결과](##Event-Storming-최종-결과)
  - [기능 요구사항 Coverage](##기능-요구사항-Coverage)
  - [헥사고날 아키텍처 다이어그램 도출](##헥사고날-아키텍처-다이어그램-도출)
  - [System Architecture](##System-Architecture)
- [구현](#구현)
  - [DDD(Domain Driven Design)의 적용](##DDD(Domain-Driven-Design)의-적용)
  - [Gateway 적용](##Gateway-적용)
  - [폴리글랏 퍼시스턴스](##폴리글랏-퍼시스턴스)
  - [동기식 호출과 Fallback 처리](##동기식-호출과-Fallback-처리)
  - [비동기식 호출과 Eventual Consistency](##비동기식-호출과-Eventual-Consistency)
- [운영](#운영)
  - [Deploy/ Pipeline](#Deploy/Pipeline)
  - [Config Map](##Config-Map)
  - [Persistence Volume](##Persistence-Volume)
  - [Autoscale (HPA)](##Autoscale-(HPA))
  - [Circuit Breaker](##Circuit-Breaker)
  - [Zero-Downtime deploy (Readiness Probe)](##Zero-Downtime-deploy (Readiness Probe))
  - [Self-healing (Liveness Probe)](##Self-healing-(Liveness-Probe))


# 서비스 시나리오

## 기능적 요구사항

* 백신 관리자는 백신정보 및 재고를 등록한다.
* 백신 관리자는 백신 재고를 추가한다.
* 고객은 접종을 예약한다.
* 고객은 접종 예약을 취소 할 수 있다.
* 접종 예약수량은 백신 재고수량을 초과 할 수 없다.
* 고객이 접종 완료 하면, 예약 수량과 재고 수량이 감소한다.
* 고객이 방문하여 접종하면 접종 관리자에 의해 접종완료된다.
* 고객은 예약정보를 확인 할 수 있다. 
* 예약 서비스는 게이트웨이를 통해 고객과 통신한다.


## 비기능적 요구사항
* 트랜잭션
    * 예약 수량은 재고 수량을 초과하여 예약 할 수 없다. (Sync 호출)
        |값|계산식|
        |---:|------|
        |예약 가능|백신 재고 수 - 예약 백신 수 >= 1|
* 장애격리
    * 백신접종 기능이 수행되지 않더라도 백신예약은 365일 24시간 받을 수 있어야 한다. Async (event-driven), Eventual Consistency
    * 예약시스템이 과중 되면 사용자를 잠시동안 받지 않고 예약을 잠시후에 하도록 유도한다. Circuit breaker, fallback
* 성능
    * 고객은 MyPage에서 본인 예약 상태를 확인 할 수 있어야 한다. (CQRS)
    
# 분석/설계

## AS-IS 조직 (Horizontally-Aligned)
![Horizontally-Aligned](https://user-images.githubusercontent.com/2360083/119254418-278d0d80-bbf1-11eb-83d1-494ba83aeaf1.png)

## TO-BE 조직 (Vertically-Aligned)
![Vertically-Aligned](https://user-images.githubusercontent.com/2360083/119254421-2eb41b80-bbf1-11eb-82fe-53c5dcd366f7.png)

## Event 도출
![image](https://user-images.githubusercontent.com/61259324/120970337-43beac00-c7a6-11eb-87ec-1bccc37c0fb5.png)

## 부적격 이벤트 제거
![image](https://user-images.githubusercontent.com/61259324/120970404-5afd9980-c7a6-11eb-93a4-ec60cf3c4ea0.png)

```
- 이벤트를 식별하여 타임라인으로 배치하고 중복되거나 잘못된 도메인 이벤트들을 걸러내는 작업을 수행함
- 현업이 사용하는 용어를 그대로 사용(Ubiquitous Language) 
```
## 액터, 커맨드 부착
![image](https://user-images.githubusercontent.com/61259324/120970948-0d356100-c7a7-11eb-956f-faeb5f0d53a6.png)

```
- Event를 발생시키는 Command와 Command를 발생시키는주체, 담당자 또는 시스템을 식별함 
- Command : 백신등록, 백신수량 추가, 접종 예약, 접종예약 취소, 접종, 체크 및 예약수량 변경
- Actor : 백신관리자, 접종자, 접종관리자, 시스템
```
## 어그리게잇으로 묶기
![image](https://user-images.githubusercontent.com/61259324/120971066-30f8a700-c7a7-11eb-9dfc-d282b5c23e65.png)

```
- 연관있는 도메인 이벤트들을 Aggregate 로 묶었음 
- Aggregate : 백신정보, 예약정보, 접종정보
```
## 바운디드 컨텍스트로 묶기
![image](https://user-images.githubusercontent.com/61259324/120972839-23dcb780-c7a9-11eb-92fc-4566835b88e2.png)

```
도메인 서열 분리
- Core Domain (접종예약 서비스)
  . 없어서는 안될 핵심 서비스이며, 연간 Up-time SLA 수준을 99.999% 목표, 배포주기는 1주일 1회 미만
- Supporting Domain (백신 서비스)
  . 백신 재고및 예약수량 관리 위한 서비스이며, SLA 수준은 연간 80% 이상 uptime 목표, 배포주기는 각 팀의 자율이나 표준 스프린트 주기가 1주일 이므로 1주일 1회 이상을 기준으로 함
- General Domain (접종 서비스) : 접종완료이력을 관리하기 위한 서비스
```

## 폴리시 부착/이동 및 컨텍스트 매핑
![image](https://user-images.githubusercontent.com/61259324/120973052-669e8f80-c7a9-11eb-9c5e-e5eed14c32e6.png)

```
- Policy의 이동과 컨텍스트 매핑 (점선은 Pub/Sub, 실선은 Req/Res)
```

## Event Storming 최종 결과
![image](https://user-images.githubusercontent.com/61259324/120962973-c130ef00-c79b-11eb-852f-0afc93b6e759.png)


![image](https://user-images.githubusercontent.com/61259324/120963262-356b9280-c79c-11eb-94f0-2cd88bc66c5e.png)


## 기능 요구사항 Coverage

![image](https://user-images.githubusercontent.com/61259324/120993819-df5c1680-c7be-11eb-86c0-0c0cc1655310.png)

![image](https://user-images.githubusercontent.com/61259324/120994060-1b8f7700-c7bf-11eb-8576-c9942300dcc2.png)

![image](https://user-images.githubusercontent.com/61259324/120994206-3d88f980-c7bf-11eb-842b-73118d6e89ce.png)

## 헥사고날 아키텍처 다이어그램 도출
![image](https://user-images.githubusercontent.com/61259324/120964341-27b70c80-c79e-11eb-8573-015794496e99.png)

## System Architecture
![image](https://user-images.githubusercontent.com/61259324/120966586-626e7400-c7a1-11eb-9d91-0960a88e675d.png)


![image](https://user-images.githubusercontent.com/61259324/120966961-e4f73380-c7a1-11eb-8064-32f5363703c3.png)