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

![image](https://user-images.githubusercontent.com/2360083/120986163-41188280-c7b7-11eb-8e23-755d645efbed.png)

<code>deployment.yml</code>

- Container에 Volumn Mount

![image](https://user-images.githubusercontent.com/2360083/120983890-175e5c00-c7b5-11eb-9332-04033438cea1.png)

<code>application.yml</code>
- profile: **docker**
- logging.file: PVC Mount 경로

![image](https://user-images.githubusercontent.com/2360083/120983856-10374e00-c7b5-11eb-93d5-42e1178912a8.png)

마운트 경로에 logging file 생성 확인

```
$ kubectl exec -it injection -- /bin/sh
# cd /mnt/azure/logs
# tail -n 20 -f injection.log
```

<img src="https://user-images.githubusercontent.com/2360083/120983881-14fc0200-c7b5-11eb-807d-3aff8271741e.png" width="100%" />


