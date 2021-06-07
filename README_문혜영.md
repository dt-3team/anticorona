# 운영

## CI/CD 설정

각 구현체들은 각자의 source repository 에 구성되었고, 사용한 CI/CD 플랫폼은 Azure를 사용하였으며, pipeline build script 는 각 프로젝트 폴더 이하에 cloudbuild.yml 에 포함되었다.


## Deploy / Pipeline

- git에서 소스 가져오기
```
git clone --recurse-submodules https://github.com/dt-3team/anticorona.git
```
- Build 하기
```
cd /anticorona
cd gateway
mvn package

cd ..
cd booking
mvn package

cd ..
cd vaccine
mvn package

cd ..
cd injection
mvn package

cd ..
cd mypage
mvn package
```

- Docker Image Push/deploy/서비스생성(yml이용)
```
-- 기본 namespace 설정
kubectl config set-context --current --namespace=anticorona

-- namespace 생성
kubectl create ns anticorona

cd gateway
az acr build --registry skccanticorona --image skccanticorona.azurecr.io/gateway:latest .

cd kubernetes
kubectl apply -f deployment.yml
kubectl apply -f service.yaml

cd ..
cd booking
az acr build --registry skccanticorona --image skccanticorona.azurecr.io/booking:latest .

cd kubernetes
kubectl apply -f deployment.yml
kubectl apply -f service.yaml

cd ..
cd vaccine
az acr build --registry skccanticorona --image skccanticorona.azurecr.io/vaccine:latest .

cd kubernetes
kubectl apply -f deployment.yml
kubectl apply -f service.yaml

cd ..
cd injection
az acr build --registry skccanticorona --image skccanticorona.azurecr.io/injection:latest .

cd kubernetes
kubectl apply -f deployment.yml
kubectl apply -f service.yaml

cd ..
cd mypage
az acr build --registry skccanticorona --image skccanticorona.azurecr.io/mypage:latest .

cd kubernetes
kubectl apply -f deployment.yml
kubectl apply -f service.yaml

```
- anticorona/gateway/kubernetes/deployment.yml 파일 
```yml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: gateway
  namespace: anticorona
  labels:
    app: gateway
spec:
  replicas: 1
  selector:
    matchLabels:
      app: gateway
  template:
    metadata:
      labels:
        app: gateway
    spec:
      containers:
        - name: gateway
          image: skccanticorona.azurecr.io/gateway:latest
          ports:
            - containerPort: 8080
```	  

- anticorona/gateway/kubernetes/service.yaml 파일 
```yml
apiVersion: v1
kind: Service
metadata:
  name: gateway
  namespace: anticorona
  labels:
    app: gateway
spec:
  ports:
    - port: 8080
      targetPort: 8080
  type: LoadBalancer
  selector:
    app: gateway
```	  

- anticorona/booking/kubernetes/deployment.yml 파일 
```yml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: booking
  namespace: anticorona
  labels:
    app: booking
spec:
  replicas: 1
  selector:
    matchLabels:
      app: booking
  template:
    metadata:
      labels:
        app: booking
    spec:
      containers:
        - name: booking
          image: skccanticorona.azurecr.io/booking:latest
          ports:
            - containerPort: 8080
          env:
            - name: vaccine-url
              valueFrom:
                configMapKeyRef:
                  name: apiurl
                  key: url
```	  

- anticorona/booking/kubernetes/service.yaml 파일 
```yml
apiVersion: v1
kind: Service
metadata:
  name: booking
  namespace: anticorona
  labels:
    app: booking
spec:
  ports:
    - port: 8080
      targetPort: 8080
  selector:
    app: booking
```	  

- deploy 완료(istio 부착기준)

![image](https://user-images.githubusercontent.com/82795806/120998532-24824780-c7c3-11eb-8f01-d73860d68426.png)
***

## 동기식 호출 / 서킷 브레이킹 / 장애격리

### Circuit Breaker
* 서킷 브레이킹 프레임워크의 선택: Spring FeignClient + Istio를 설치하여, anticorona namespace에 주입하여 구현함

시나리오는 예약(booking)-->백신(vaccine) 연결을 RESTful Request/Response 로 연동하여 구현이 되어있고, 예약 요청이 과도할 경우 CB 를 통하여 장애격리.

- Istio 다운로드 및 PATH 추가, 설치, namespace에 istio주입
```
curl -L https://istio.io/downloadIstio | ISTIO_VERSION=1.7.1 TARGET_ARCH=x86_64 sh -
※ istio v1.7.1은 Kubernetes 1.16이상에서만 동작
```
- istio PATH 추가
```
cd istio-1.7.1
export PATH=$PWD/bin:$PATH
```

- istio 설치
```
istioctl install --set profile=demo --set hub=gcr.io/istio-release
※ Docker Hub Rate Limiting 우회 설정
```

- namespace에 istio주입
```
kubectl label anticorona tutorial istio-injection=enabled
```

- Virsual Service 생성 (Timeout 3초 설정)
- anticorona/booking/kubernetes/booking-istio.yaml 파일 
```yml
  apiVersion: networking.istio.io/v1alpha3
  kind: VirtualService
  metadata:
    name: vs-booking-network-rule
    namespace: anticorona
  spec:
    hosts:
    - booking
    http:
    - route:
      - destination:
          host: booking
      timeout: 3s
```	  

![image](https://user-images.githubusercontent.com/82795806/120985451-956f3280-c7b6-11eb-95a4-eb5a8c1ebce4.png)


- Booking 서비스 재배포 후 Pod에 CB 부착 확인

![image](https://user-images.githubusercontent.com/82795806/120985804-ed0d9e00-c7b6-11eb-9f13-8a961c73adc0.png)


- 부하테스터 siege 툴을 통한 서킷 브레이커 동작 확인:
  - 동시사용자 100명, 60초 동안 실시
```
# siege -c100 -t10S -v --content-type "application/json" 'http://booking:8080/bookings POST {"vaccineId":1, "vcName":"FIZER", "userId":5, "status":"BOOCKED"}'
```
![image](https://user-images.githubusercontent.com/82795806/120986972-1549cc80-c7b8-11eb-83e1-7bac5a0e80ed.png)


- 운영시스템은 죽지 않고 지속적으로 CB 에 의하여 적절히 회로가 열림과 닫힘이 벌어지면서 자원을 보호하고 있음을 보여줌. 
- 약 84%정도 정상적으로 처리되었음.

***
## Autoscale (HPA)
### 오토스케일 아웃

앞서 CB 는 시스템을 안정되게 운영할 수 있게 해줬지만 사용자의 요청을 100% 받아들여주지 못했기 때문에 이에 대한 보완책으로 자동화된 확장 기능을 적용하고자 한다. 

- 결제서비스에 대한 replica 를 동적으로 늘려주도록 HPA 를 설정한다. 설정은 CPU 사용량이 15프로를 넘어서면 replica 를 10개까지 늘려준다:
```
kubectl autoscale deploy booking --min=1 --max=10 --cpu-percent=15
```
![image](https://user-images.githubusercontent.com/82795806/120987663-c51f3a00-c7b8-11eb-8cc3-59d725ca2f69.png)


- CB 에서 했던 방식대로 워크로드를 걸어준다.
```
# siege -c200 -t10S -v --content-type "application/json" 'http://booking:8080/bookings POST {"vaccineId":1, "vcName":"FIZER", "userId":5, "status":"BOOCKED"}'
```

- 오토스케일이 어떻게 되고 있는지 모니터링을 걸어둔다:
```
watch kubectl get all
```

- 어느정도 시간이 흐른 후 스케일 아웃이 벌어지는 것을 확인할 수 있다:

* siege 부하테스트 - 전

![image](https://user-images.githubusercontent.com/82795806/120990254-51caf780-c7bb-11eb-98a6-243b69344f12.png)

* siege 부하테스트 - 후

![image](https://user-images.githubusercontent.com/82795806/120989337-66f35680-c7ba-11eb-9b4e-b1425d4a3c2f.png)


- siege 의 로그를 보아도 전체적인 성공률이 높아진 것을 확인 할 수 있다. 

![image](https://user-images.githubusercontent.com/82795806/120990490-93f43900-c7bb-11eb-9295-c3a0a8165ff6.png)

