## Openssl 학습

```shell
# RSA 방식으로 키를 생성해서 key.pem에 저장. 키 크리를 4096 비트로 설정
openssl genrsa -out key.pem 4096
```

```shell
# 자체 서명 할때는 -x509 넣어야함
# cert.pem 파일에 인증서를 저장 1년이라는 유효기간을 설정한다
openssl req -new -x509 -key key.pem -out cert.pem -days 365
```

### CA의 역할

- 서버는 인증서를 생성하기 위해 CA로 CSR을 전송. CSR에는 서버 공개키, DN(국가 코드, 도, 시, 조직 이름, 이메일 주소) 등이 포함
- CA는 디지털 서명을 추가하고 인증서를 발행해서 공개키에 서명한다. 디지털 서명은 CA의 개인키로 인코딩 되는 메세지다. 
- 브라우저는 CA의 공개키를 가지고 있고, 이를 통해 CA의 개인키로 인코딩된 정보를 디코딩할 수 있다.

