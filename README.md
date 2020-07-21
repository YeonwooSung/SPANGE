# Umile LTA (Location Tracking App)

## Work with Spacosa

1. 이 프로젝트는 (주)스파코사의 GPer Device (SKT Lora향 모델 혹은 3G향 모델 사용) 와 Catchloc 위치추적 서버 시스템과 연동한다.

   참고 :
   스파코사 회사 소개 페이지
   http://www.spacosa.com/ko/
   3G 기반 지퍼디바이스 (지퍼 글로벌)
   http://www.gper.me/ko/gper_global.php
   Location Cloud 캐치락 소개페이지
   http://www.catchloc.com/ko/
   
2. 물론 GPer 및 Catchloc 사용자를 위해서는 Spacosa사에서 "지퍼 GPS" 앱이 제공되고 있다.
   https://play.google.com/store/apps/details?id=com.spacosa.android.gper.global
   그럼에도 불구하고 이 프로젝트는 GPer와 Catchloc을 기반으로 자신만의 위치기반 서비스를 구축하고자 앱을 개발할 떄 사용할 수 있도록 제공한다.
   "Umile LTA" 앱은 CatchLoc 개발자 가이드에 기반하여 만들어 졌다. 
   http://developer.catchloc.com/
   
   (*) 개발과정에 도움을 주신 스파코사 관계자분들께 감사를 드립니다.
   
2. Umile LTA는 React Native를 사용한다.
   관련된 기술 내용은 React Native 공식 페이지를 참고한다.
  https://reactnative.dev/
  
## Functions and Screen Shots

## Issues

### Google Map

Google Maps SDK for Android 16.0.0 이하를 사용하게 되면, Android SDK version 28 이상 (Android 9.0 이상)에서 "java.lang.NoClassDefFoundError:failed resolution of :Lorg/apache/http/ProtocolVersion" 에러가 발생한다. 이를 해결해기 위해서 [stackoverflow 페이지의 답변](https://stackoverflow.com/questions/50461881/java-lang-noclassdeffounderrorfailed-resolution-of-lorg-apache-http-protocolve)을 참고하였다.

다만, 이 방식은 임시방편일 뿐, 정확한 해결책은 아니다. 따라서, 구글맵 버전을 17 혹은 그 이상으로 바꾸고 일부 오래된 폰들에 대한 지원을 끊을 것인지, 아니면 일단 이대로 유지를 하고 나중에 버젼업을 할 것인지 정할 필요가 있다.
