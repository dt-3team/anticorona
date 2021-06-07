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
  - [시나리오 테스트결과](#시나리오-테스트결과)
- [분석/설계](#분석설계)
- [구현](#구현)
  - [DDD 의 적용](#ddd-의-적용)
  - [Gateway 적용](#Gateway-적용)
  - [폴리글랏 퍼시스턴스](#폴리글랏-퍼시스턴스)
  - [동기식 호출 과 Fallback 처리](#동기식-호출-과-Fallback-처리)
  - [비동기식 호출 과 Eventual Consistency](#비동기식-호출-과-Eventual-Consistency)
- [운영](#운영)
  - [CI/CD 설정](#cicd설정)
  - [서킷 브레이킹 / 장애격리](#서킷-브레이킹-/-장애격리)
  - [오토스케일 아웃](#오토스케일-아웃)
  - [무정지 재배포](#무정지-재배포)
  

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

## 액터, 커맨드 부착
![image](https://user-images.githubusercontent.com/61259324/120970948-0d356100-c7a7-11eb-956f-faeb5f0d53a6.png)

## 어그리게잇으로 묶기
![image](https://user-images.githubusercontent.com/61259324/120971066-30f8a700-c7a7-11eb-9dfc-d282b5c23e65.png)

## 바운디드 컨텍스트로 묶기
![image](https://user-images.githubusercontent.com/61259324/120972839-23dcb780-c7a9-11eb-92fc-4566835b88e2.png)

## 폴리시 부착/이동 및 컨텍스트 매핑
![image](https://user-images.githubusercontent.com/61259324/120973052-669e8f80-c7a9-11eb-9c5e-e5eed14c32e6.png)

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