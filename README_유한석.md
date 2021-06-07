## Polyglot Persistence

h2 db를 사용한 다른 서비스와 달리 mypage 서비스는 hsql db로 구현하였다.

|서비스|DB|pom.xml|
| :--: | :--: | :--: |
|vaccine| H2 |![image](https://user-images.githubusercontent.com/2360083/120982836-1842be00-c7b4-11eb-91de-ab01170133fd.png)|
|booking| H2 |![image](https://user-images.githubusercontent.com/2360083/120982836-1842be00-c7b4-11eb-91de-ab01170133fd.png)|
|injection| H2 |![image](https://user-images.githubusercontent.com/2360083/120982836-1842be00-c7b4-11eb-91de-ab01170133fd.png)|
|mypage| H2 |![image](https://user-images.githubusercontent.com/2360083/120982836-1842be00-c7b4-11eb-91de-ab01170133fd.png)|


## PVC
PVC 생성 파일

<code>injection-pvc.yml</code>
- AccessModes: **ReadWriteMany**
- storeageClass: **azurefile**
```
apiVersion: v1
kind: PersistentVolumeClaim
metadata:
  name: injection-disk
  namespace: anticorona
spec:
  accessModes:
  - ReadWriteMany
  storageClassName: azurefile
  resources:
    requests:
      storage: 1Gi
```

<code>application.yml</code>
- profile: **docker**
- logging.file: PVC Mount 경로

![image](https://user-images.githubusercontent.com/2360083/120983856-10374e00-c7b5-11eb-93d5-42e1178912a8.png)


![image](https://user-images.githubusercontent.com/2360083/120983881-14fc0200-c7b5-11eb-807d-3aff8271741e.png)

![image](https://user-images.githubusercontent.com/2360083/120983890-175e5c00-c7b5-11eb-9332-04033438cea1.png)
