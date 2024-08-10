# Task one month internship
- 한 달 인턴 과제 제출용 레포지토리
- 유저 인증, 인가 및 JUnit 테스트, AWS EC2 배포 프로젝트

### ERD

![image](https://github.com/user-attachments/assets/49a56d22-a021-4369-8261-b3c6296a8f5d)

### API 명세서

|도메인|메소드|기능|URL|Request|Response|Request header|
|---|---|---|---|---|---|---|
|USER|POST|회원가입|/api/v1/users/signup|{<br>"username":"JIN HO",<br>"password":"12341234",<br>"nickname":"Mentos"<br>}|{<br>"username": "JIN HO",<br>"nickname": "Mentos",<br>"authorities": [<br>{<br>"authorityName": "ROLE_USER"<br>}<br>]<br>}|

### JWT 인증 과정

1. 유저 로그인 시도
2. ```JwtAuthenticationFilter```에서 유저 검증 및 토큰 발행
3. ```AccessToken```은 ```HttpSurvletResponse```의 **Header**에, ```RefreshToken```은 DB의 ```UserRefreshTokens``` 테이블에 저장
4. 이후 Request Header에 ```AccessToken```을 넣어 요청
5. ```JwtAuthorizationFilter```에서 토큰 검증
6. ```AccessToken``` **만료** 검증
7. 만료 시 토큰의 ```Claims```에 있는 유저 정보를 바탕으로 ```RefreshToken``` 조회
8. ```RefreshToken```의 유효 및 만료 검증
9. 새로운 ```AccessToken``` 발행 및 ```HttpSurvletResponse```의 **Header**에 저장

### 배포 환경

AWS 프리티어 EC2 서버 리눅스 환경에 배포

http://43.203.224.161:8080

![image](https://github.com/user-attachments/assets/8c83adf0-9974-4323-96e7-c25b5e90e866)
