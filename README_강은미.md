# ConfigMap
- 변경 가능성이 있는 설정을 ConfigMap을 사용하여 관리
    - booking 서비스에서 바라보는 vaccine 서비스 url 일부분을 ConfigMap 사용하여 구현​

- in booking src (booking/src/main/java/anticorona/external/VaccineService.java)

    ![configmap-in src](https://user-images.githubusercontent.com/18115456/120984025-35c45780-c7b5-11eb-8181-bfed9a943e67.png)

- booking application.yml (booking/src/main/resources/application.yml)​

    ![configmap-application yml](https://user-images.githubusercontent.com/18115456/120984136-5096cc00-c7b5-11eb-8745-78cb754c0e1b.PNG)

- booking deploy yml (booking/kubernetes/deployment.yml)

    ![configmap-deploy yml](https://user-images.githubusercontent.com/18115456/120984461-a2d7ed00-c7b5-11eb-9f2f-6b09ad0ba9cf.png)

- configmap 생성 후 조회
    ```
    kubectl create configmap apiurl --from-literal=url=vaccine -n anticorona
    ```
    ![configmap-configmap조회](https://user-images.githubusercontent.com/18115456/120985042-2eea1480-c7b6-11eb-9dbc-e73d696c003b.PNG)

- configmap 삭제 후, 에러 확인
    ```
    kubectl delete configmap apiurl
    ```
    ![configmap-오류1](https://user-images.githubusercontent.com/18115456/120985205-5b9e2c00-c7b6-11eb-8ede-df74eff7f344.png)

    ![configmap-오류2](https://user-images.githubusercontent.com/18115456/120985213-5ccf5900-c7b6-11eb-9c06-5402942329a3.png)


# Self-healing (Liveness Probe)
- deployment.yml에 정상 적용되어 있는 livenessProbe 

    ![liveness](https://user-images.githubusercontent.com/18115456/120985784-e97a1700-c7b6-11eb-8ead-209072912fa0.PNG)

- port 및 path 잘못된 값으로 변경 후, retry 시도 확인 (in booking 서비스)
    - booking deploy yml 수정

        ![selfhealing(liveness)-세팅변경](https://user-images.githubusercontent.com/18115456/120985806-ed0d9e00-c7b6-11eb-834f-ffd2c627ecf0.png)

    - retry 시도 확인

        ![selfhealing(liveness)-restarts수](https://user-images.githubusercontent.com/18115456/120985797-ebdc7100-c7b6-11eb-8b29-fed32d4a15a3.png)


# Zero-downtime deploy (Readiness Probe)
- deployment.yml에 정상 적용되어 있는 readinessProbe 

    ![readiness](https://user-images.githubusercontent.com/18115456/120987376-7ffb0800-c7b8-11eb-8672-466a04893c50.PNG)

- 
    ![readiness1](https://user-images.githubusercontent.com/18115456/120987382-812c3500-c7b8-11eb-8b36-c98d162a4238.PNG)

    ![readiness2](https://user-images.githubusercontent.com/18115456/120987386-81c4cb80-c7b8-11eb-84e7-5c00a9b1a2ff.PNG)

    ![readiness3](https://user-images.githubusercontent.com/18115456/120987389-81c4cb80-c7b8-11eb-9a7d-b75ffa11129d.PNG)

    ![readiness4](https://user-images.githubusercontent.com/18115456/120987393-825d6200-c7b8-11eb-887e-d01519123d42.PNG)