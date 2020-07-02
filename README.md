# Spangi

## Issues

### Google Map

Google Maps SDK for Android 16.0.0 이하를 사용하게 되면, Android SDK version 28 이상 (Android 9.0 이상)에서 "java.lang.NoClassDefFoundError:failed resolution of :Lorg/apache/http/ProtocolVersion" 에러가 발생한다. 이를 해결해기 위해서 [stackoverflow 페이지의 답변](https://stackoverflow.com/questions/50461881/java-lang-noclassdeffounderrorfailed-resolution-of-lorg-apache-http-protocolve)을 참고하였다.

다만, 이 방식은 임시방편일 뿐, 정확한 해결책은 아니다. 따라서, 구글맵 버전을 17 혹은 그 이상으로 바꾸고 일부 오래된 폰들에 대한 지원을 끊을 것인지, 아니면 일단 이대로 유지를 하고 나중에 버젼업을 할 것인지 정할 필요가 있다.
