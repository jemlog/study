# TLS 인증서 학습

```shell
openssl genrsa -out key.pem 4096
```

```shell
openssl req -new -x509 -key key.pem -out cert.pem -days 365
```