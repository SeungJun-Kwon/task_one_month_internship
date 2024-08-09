﻿# Task one month internship
- 한 달 인턴 과제 제출용 레포지토리
- 유저 인증, 인가 및 JUnit 테스트, AWS EC2 배포 프로젝트

### ERD

![image](https://github.com/user-attachments/assets/dda581c3-1c8e-4ea5-a5e6-eb79e12fcbc8)

### API 명세서

|도메인|메소드|기능|URL|Request|Response|Request header|
|---|---|---|---|---|---|---|
|USER|POST|회원가입|/api/v1/users/signup|{<br>"username":"JIN HO",<br>"password":"12341234",<br>"nickname":"Mentos"<br>}|{<br>"username": "JIN HO",<br>"nickname": "Mentos",<br>"authorities": [<br>{<br>"authorityName": "ROLE_USER"<br>}<br>]<br>}|
