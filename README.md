# HERO - 보이는 인사관리,통합 HR 대시보드

<div align="center">
  <img width="492" height="200" alt="image" src="https://github.com/user-attachments/assets/a5a984c1-b005-43a8-bec6-f07fafe8d7e9" />
</div>

<hr>

## 목차
#### [팀 소개](#팀-소개-1)
#### [프로젝트 소개](#프로젝트-소개-1)
#### [주요 기능](#주요-기능-1)
#### [기술 스택](#기술-스택-1)
#### [시스템 아키텍처](#시스템-아키텍처-1)
#### [WBS](#wbs-1)
#### [요구사항 명세서](#요구사항-명세서-1)
#### [DDD](#ddd-1)
#### [ERD](#erd-1)
#### [Wire Frame](#wire-frame-1)
#### [API 명세서](#api-명세서-1)
#### [단위 테스트](#단위-테스트-1)
#### [UI / UX 테스트](#ui--ux-단위-테스트)
#### [CI / CD 계획서](#ci--cd-계획서-1)
#### [프로젝트 회고](#프로젝트-회고-1)
<hr>

## 팀 소개

<br>
<div align="center">
  <table>
  <tr>
    <td align="center">
      <img width="192" height="322" alt="image" src="https://github.com/user-attachments/assets/4ee8a86c-3f8a-4a64-bdfa-7ea92219c7f6" />
    </td>
    <td align="center">
      <img width="192" height="322" alt="image" src="https://github.com/user-attachments/assets/7ecc1ad8-c44a-4c4f-96c2-ce68e0a14ed5" />
    </td>
    <td align="center">
      <img width="192" height="322" alt="image" src="https://github.com/user-attachments/assets/61d2af39-81fd-4cc8-956c-5a5550c609dd" />
    </td>
    <td align="center">
      <img width="192" height="322" alt="image" src="https://github.com/user-attachments/assets/0a339929-90bb-4600-8476-206d1cf52346" />
    </td>
    <td align="center">
      <img width="192" height="322" alt="image" src="https://github.com/user-attachments/assets/75176e7f-bafc-4e43-a559-36c677395664" />
    </td>
    <td align="center">
      <img width="192" height="322" alt="image" src="https://github.com/user-attachments/assets/3c5ab666-965b-4e7c-b184-719b8b8ba41e" />
    </td>
  </tr>
  <tr>
    <td align="center">
      <a href="https://github.com/dddd0ng"><b>곽동근</b></a>
    </td>
    <td align="center">
      <a href="https://github.com/indy0322"><b>김승민</b></a>
    </td>
    <td align="center">
      <a href="https://github.com/bynmch"><b>변민철</b></a>
    </td>
    <td align="center">
      <a href="https://github.com/Seung-Geon"><b>이승건</b></a>
    </td>
    <td align="center">
      <a href="https://github.com/Easy-going12"><b>이지윤</b></a>
    </td>
    <td align="center">
      <a href="https://github.com/haenin"><b>최혜원</b></a>
    </td>
  </tr>
</table>
</div>
<br>
<div align="right">
  <a href="#목차">🔝 맨 위로</a>
</div>
<br>

<hr>

## 프로젝트 소개
**HERO** 는 기업 내에 분산되어 있던 **근태, 휴가, 평가, 결재 데이터**를 하나의 흐름으로 통합 관리하는 **인사(HR) 시스템**입니다.

각각 독립적으로 운영되던 인사 데이터를 유기적으로 연결함으로써
**데이터 취합 및 관리에 소요되던 시간을 획기적으로 줄이고**, <br>
핵심 인사 지표를 **대시보드로 한눈에 파악**할 수 있도록 설계되었습니다.

또한,
**역할(Role) 기반 권한 관리**를 통해
관리자, 부서장, 일반 사원 등 사용자 유형에 따라 **필요한 기능만 노출되는 UI**를 제공하여 <br>
보안성과 사용성을 동시에 고려한 인사 운영 환경을 구현합니다.

HERO는 단순한 기능 나열이 아닌,
**인사 데이터 간의 시너지를 극대화하여 효율적인 조직 운영을 지원하는 HR 플랫폼을 목표**로 합니다.

<br>
<div align="right">
  <a href="#목차">🔝 맨 위로</a>
</div>
<hr>


## 주요 기능
### 조직 운영을 고려한 UX
- 개인 근태 이력, 부서별 휴가 일정, 알림 설정 등을 사용자가 이해하기 쉽도록 디자인 하였습니다.

### 대시보드를 이용한 시각화
- 인건비, 사원 평가, 퇴사 데이터 등을 대시보드를 사용하여 시각화시켰습니다.
  
### AI를 사용한 업무 공수 절감
- 평가 가이드 라인 위반 여부, 우수 사원 조사 등의 번거로운 업무를 AI를 사용하여 작업 시간을 절감하였습니다.

### 사용자별 권한 세분
- 권한에 따른 접근 페이지를 제한함으로써 보안적 측면을 강화화였습니다.

<br>
<div align="right">
  <a href="#목차">🔝 맨 위로</a>
</div>
<hr>

## 기술 스택
### 🧩 Backend - Spring
![Java](https://img.shields.io/badge/java-007396?style=for-the-badge&logo=java&logoColor=white)
![Spring Boot](https://img.shields.io/badge/spring%20boot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)
![Spring Security](https://img.shields.io/badge/spring%20security-6DB33F?style=for-the-badge&logo=springsecurity&logoColor=white)
![Spring Data JPA](https://img.shields.io/badge/spring%20data%20jpa-6DB33F?style=for-the-badge)
![JWT](https://img.shields.io/badge/jwt-000000?style=for-the-badge)
![Lombok](https://img.shields.io/badge/lombok-BC4521?style=for-the-badge)
![Gradle](https://img.shields.io/badge/gradle-02303A?style=for-the-badge&logo=gradle&logoColor=white)
![Swagger](https://img.shields.io/badge/swagger-85EA2D?style=for-the-badge&logo=swagger&logoColor=white)
![WebSocket](https://img.shields.io/badge/websocket-000000?style=for-the-badge&logo=socketdotio&logoColor=white)


### 🤖 Backend - Python
![Python](https://img.shields.io/badge/python-3670A0?style=for-the-badge&logo=python&logoColor=ffdd54)
![FastAPI](https://img.shields.io/badge/FastAPI-005571?style=for-the-badge&logo=fastapi)
![ChatGPT](https://img.shields.io/badge/chatGPT-74aa9c?style=for-the-badge&logo=openai&logoColor=white)
![LangChain](https://img.shields.io/badge/langchain-%231C3C3C.svg?style=for-the-badge&logo=langchain&logoColor=white)

### 🎨 Frontend
![Vue.js](https://img.shields.io/badge/vue.js-4FC08D?style=for-the-badge&logo=vuedotjs&logoColor=white)
![Pinia](https://img.shields.io/badge/pinia-FFD859?style=for-the-badge)
![Vue Router](https://img.shields.io/badge/vue%20router-4FC08D?style=for-the-badge)
![Vite](https://img.shields.io/badge/vite-646CFF?style=for-the-badge&logo=vite&logoColor=white)
![Axios](https://img.shields.io/badge/axios-5A29E4?style=for-the-badge)
![TypeScript](https://img.shields.io/badge/typescript-3178C6?style=for-the-badge&logo=typescript&logoColor=white)
![Chart.js](https://img.shields.io/badge/chart.js-FF6384?style=for-the-badge&logo=chartdotjs&logoColor=white)
![HTML5](https://img.shields.io/badge/html5-E34F26?style=for-the-badge&logo=html5&logoColor=white)
![CSS3](https://img.shields.io/badge/css3-1572B6?style=for-the-badge&logo=css3&logoColor=white)

### 🗄️ Database
![MariaDB](https://img.shields.io/badge/mariadb-003545?style=for-the-badge&logo=mariadb&logoColor=white)
![Amazon RDS](https://img.shields.io/badge/amazon%20rds-527FFF?style=for-the-badge&logo=amazonrds&logoColor=white)

### ☁️ Cloud / Infrastructure
![AWS](https://img.shields.io/badge/aws-232F3E?style=for-the-badge&logo=amazonaws&logoColor=white)
![AWS IAM](https://img.shields.io/badge/aws%20iam-FF9900?style=for-the-badge)
![Amazon S3](https://img.shields.io/badge/amazon%20s3-569A31?style=for-the-badge&logo=amazons3&logoColor=white)


### 🔄 CI / CD 
![GitHub Actions](https://img.shields.io/badge/github%20actions-2088FF?style=for-the-badge&logo=githubactions&logoColor=white)


### 🛠 Tools
![Git](https://img.shields.io/badge/git-F05032?style=for-the-badge&logo=git&logoColor=white)
![GitHub](https://img.shields.io/badge/github-181717?style=for-the-badge&logo=github)
![Notion](https://img.shields.io/badge/notion-000000?style=for-the-badge&logo=notion)
![Discord](https://img.shields.io/badge/discord-5865F2?style=for-the-badge&logo=discord&logoColor=white)
![Figma](https://img.shields.io/badge/figma-F24E1E?style=for-the-badge&logo=figma&logoColor=white)


<br>
<div align="right">
  <a href="#목차">🔝 맨 위로</a>
</div>
<hr>

## 시스템 아키텍처
<img width="2121" height="1262" alt="KakaoTalk_Photo_2026-01-05-16-59-19" src="https://github.com/user-attachments/assets/db665141-4fce-48d2-806f-c887f7e971c5" />


### 🖥️ 프론트엔드 (Frontend: Vue.js)

* **배포 방식:** GitHub Actions를 통해 빌드된 결과물이 EC2(Nginx)에 배포됩니다. 
* **접속 경로:** Route 53에서 도메인 주소를 **ALB**의 DNS 명으로 연결합니다.
* **역할:** ALB가 요청을 받아 각 가용 영역(AZ-a, AZ-c)에 있는 EC2 인스턴스의 **Nginx**로 트래픽을 분산하며, Nginx는 정적 자원을 서빙합니다.

### 🧩 백엔드 (Backend: Spring Boot & Python)

* **구조:** 보안을 위해 **Private Subnet** 내부에 위치하며, 외부에서 직접 접근이 불가능합니다.
* **배포 관리:** Elastic Beanstalk 기반의 **Blue-Green 방식**을 사용하여 무중단 배포를 실현합니다.
* **통신:** 프론트엔드 서버나 외부 요청은 ALB를 거쳐 내부 Target Group(TG)으로 전달됩니다.

### 🔁 Blue-Green 배포 (무중단 배포 전략)

* **이중화 환경:** 현재 운영 중인 'Blue' 환경과 새 버전이 올라갈 'Green' 환경을 독립적으로 유지합니다.
* **전환 및 롤백:** 배포 완료 후 **Target Group의 대상(EC2 인스턴스)을 교체**하거나 ALB의 가중치를 조절하여 트래픽을 전환합니다.
* **안정성:** 설명하신 대로 수동 확인 과정을 거침으로써, 신규 버전의 로그를 CloudWatch로 모니터링한 뒤 최종 전환하여 리스크를 최소화합니다.

### 🗃️ 데이터베이스 및 보안

* **RDS (MariaDB):** Private Subnet에 위치하여 오직 내부 백엔드 서버를 통해서만 데이터에 접근할 수 있도록 설계되었습니다.
* **Bastion Host:** 관리자가 내부 서버(Private Subnet)에 SSH 접속이나 관리가 필요할 때 사용하는 관문 역할을 합니다.
* **NAT Gateway:** Private Subnet의 서버들이 외부 라이브러리를 업데이트하거나 외부 API와 통신하기 위해 필요한 아웃바운드 통로입니다.



<br>
<div align="right">
  <a href="#목차">🔝 맨 위로</a>
</div>
<hr>

## WBS
<img width="1241" height="581" alt="스크린샷 2026-01-08 오전 10 35 48" src="https://github.com/user-attachments/assets/b992c4e2-6c2f-4a3c-8207-f8cfc5bbb3f6" />

<br>
<div align="right">
  <a href="#목차">🔝 맨 위로</a>
</div>
<hr>

## 요구사항 명세서
<img width="845" height="630" alt="스크린샷 2026-01-08 오후 1 48 17" src="https://github.com/user-attachments/assets/8ed93daf-634e-4b8e-b71d-9be077c9c43b" />
<img width="845" height="630" alt="스크린샷 2026-01-08 오후 1 48 40" src="https://github.com/user-attachments/assets/23e94920-cf4e-49ea-8f3b-d0a2b9da3f60" />
<img width="845" height="571" alt="스크린샷 2026-01-08 오후 1 48 53" src="https://github.com/user-attachments/assets/de2bb6e4-ac83-4e5d-86af-5b65646e4480" />

<br>
<div align="right">
  <a href="#목차">🔝 맨 위로</a>
</div>
<hr>

## DDD
<img width="1190" height="643" alt="스크린샷 2026-01-05 오후 5 06 50" src="https://github.com/user-attachments/assets/46fb17fe-65dd-4aa6-b901-7007a671e5b9" />
<br>
<div align="right">
  <a href="#목차">🔝 맨 위로</a>
</div>
<hr>

## ERD
<img width="1302" height="743" alt="스크린샷 2026-01-05 오후 4 44 13" src="https://github.com/user-attachments/assets/8958b9d9-7e29-4254-b8ed-522a6ca3f700" />
<br>
<div align="right">
  <a href="#목차">🔝 맨 위로</a>
</div>
<hr>


## Wire Frame
<details>
  <summary>Wire Frame</summary>
<details>
  <summary>로그인</summary>
<img width="1213" height="346" alt="image" src="https://github.com/user-attachments/assets/252531b9-16df-4056-8a91-0b225bda94b0" />
</details>
<details>
  <summary>근태 관리</summary>
<img width="1102" height="815" alt="image" src="https://github.com/user-attachments/assets/604003cd-3daf-47dd-8fd0-4d30c82cffb5" />
</details>
<details>
  <summary>휴가 / 연차</summary>
<img width="1147" height="717" alt="image" src="https://github.com/user-attachments/assets/2e57414d-64c5-4a7d-892c-27cb421bc64e" />
</details>
<details>
  <summary>전자 결재</summary>
<img width="1137" height="786" alt="image" src="https://github.com/user-attachments/assets/fed47ffc-ab2c-4bf7-b696-bfc81fad9bcd" />
</details>
<details>
  <summary>성과 평가</summary>
<img width="1045" height="827" alt="image" src="https://github.com/user-attachments/assets/36a5087e-4073-465b-8f52-c52358323e62" />
<img width="608" height="762" alt="image" src="https://github.com/user-attachments/assets/fc6947e4-061f-418f-b668-a5f88fc58890" />
</details>
<details>
  <summary>급여</summary>
<img width="888" height="598" alt="image" src="https://github.com/user-attachments/assets/2f75eac6-3773-4457-b761-652aefaecd23" />
</details>
<details>
  <summary>급여 관리</summary>
<img width="1206" height="733" alt="image" src="https://github.com/user-attachments/assets/8257b82f-e08d-433d-ba72-b54ab7fe1e53" />
<img width="420" height="831" alt="image" src="https://github.com/user-attachments/assets/dcc2f6b6-71c8-465e-889b-0e613b596701" />
</details>
<details>
  <summary>사원 관리</summary>
<img width="652" height="802" alt="image" src="https://github.com/user-attachments/assets/ecf0407c-7387-46b9-ac4e-06b3a3fbbc0d" />
</details>
<details>
  <summary>조직도</summary>
<img width="1100" height="410" alt="image" src="https://github.com/user-attachments/assets/afff67ec-b6e1-427c-a73d-95328d197936" />
</details>
<details>
  <summary>알림</summary>
<img width="1027" height="667" alt="image" src="https://github.com/user-attachments/assets/d3ed9362-45c5-4c9f-9754-f68f59d8762b" />
</details>
<details>
  <summary>설정</summary>
<img width="943" height="762" alt="image" src="https://github.com/user-attachments/assets/3a5dea74-5e36-4062-8773-4590977bedb7" />
</details>
</details>
<br>
<div align="right">
  <a href="#목차">🔝 맨 위로</a>
</div>
<hr>


## API 명세서
<details>
  <summary>REST API</summary>
<details>
  <summary>급여 수당 마스터 API</summary>
<img width="1301" height="328" alt="image" src="https://github.com/user-attachments/assets/03b17f62-a15f-4f98-acfb-0ec021b0b8c3" />
</details>
<details>
  <summary>퇴직 관리 API</summary>
<img width="1301" height="399" alt="image" src="https://github.com/user-attachments/assets/a3ab9d5e-a1fc-4a75-be55-566369603630" />
</details>
<details>
  <summary>알림 설정 API</summary>
<img width="1301" height="165" alt="image" src="https://github.com/user-attachments/assets/f6333a7e-ec52-4c3f-9d8c-ee48ddfc34a8" />
</details>
<details>
  <summary>내 급여 리포트 API</summary>
<img width="1301" height="165" alt="image" src="https://github.com/user-attachments/assets/e9aa76f4-36d3-4483-8ef4-02353702c732" />
</details>
<details>
  <summary>휴가 API</summary>
<img width="1301" height="207" alt="image" src="https://github.com/user-attachments/assets/6050d313-a929-4be5-a270-98aa72473023" />
</details>
<details>
  <summary>급여 배치 API</summary>
<img width="1301" height="466" alt="image" src="https://github.com/user-attachments/assets/c1e1c74f-416e-432f-8366-6868f5e6094d" />
</details>
<details>
  <summary>급여 정책 설정 참조 API</summary>
<img width="1301" height="155" alt="image" src="https://github.com/user-attachments/assets/f3768b6a-642a-436f-9cb4-a3a76944ccdf" />
</details>
<details>
  <summary>홈 대시보드 API</summary>
<img width="1301" height="462" alt="image" src="https://github.com/user-attachments/assets/8cc23109-f072-495f-ae57-dcb4e4b5d137" />
</details>
<details>
  <summary>급여 조회 API</summary>
<img width="1301" height="159" alt="image" src="https://github.com/user-attachments/assets/35f20297-0c8f-474b-8a22-b1d70a5fd7fd" />
</details>
<details>
  <summary>급여 분석 API</summary>
<img width="1301" height="211" alt="image" src="https://github.com/user-attachments/assets/a83eac1c-4da7-4e82-8acd-aff314d27fbc" />
</details>
<details>
  <summary>급여 계좌 API</summary>
<img width="1301" height="309" alt="image" src="https://github.com/user-attachments/assets/403f947d-ba71-405c-8838-8e6dca68c775" />
</details>
<details>
  <summary>급여 정책 관리 API</summary>
<img width="1301" height="596" alt="image" src="https://github.com/user-attachments/assets/09681d08-ca8e-4dff-9a1c-d314929e5bf9" />
<img width="1301" height="304" alt="image" src="https://github.com/user-attachments/assets/b376564f-f99b-4a3f-9122-7fff45ac9c5b" />
</details>
<details>
  <summary>근태 API</summary>
<img width="1301" height="548" alt="image" src="https://github.com/user-attachments/assets/7a33639e-0ce3-4d91-98e5-3bfd300ccc9e" />
</details>
<details>
  <summary>급여 공제 마스터 API</summary>
<img width="1301" height="302" alt="image" src="https://github.com/user-attachments/assets/eec6e7c7-2ac5-48b9-b03a-e8612cb43fa1" />
</details>
<details>
  <summary>파이썬 연동 API</summary>
<img width="1301" height="218" alt="image" src="https://github.com/user-attachments/assets/54116da4-1c10-4e97-a7fa-ba6b4bc8bf23" />
</details>
<details>
  <summary>알림 API</summary>
<img width="1301" height="454" alt="image" src="https://github.com/user-attachments/assets/f1802456-81e1-49b5-a541-a857cfca8b13" />
</details>
<details>
  <summary>급여 명세서 API</summary>
<img width="1301" height="112" alt="image" src="https://github.com/user-attachments/assets/7069b5a5-36c6-47cd-9e58-0314368ff3d6" />
</details>
<details>
  <summary>평가 템플릿 API</summary>
<img width="1301" height="689" alt="image" src="https://github.com/user-attachments/assets/b9efcecb-c52a-4fc8-9400-3bb5a8c985f3" />
<img width="1301" height="550" alt="image" src="https://github.com/user-attachments/assets/022e2734-b0d8-4eaa-990c-83e0b880f753" />
</details>
<details>
  <summary>직원 API</summary>
<img width="899" height="688" alt="image" src="https://github.com/user-attachments/assets/f552bd6e-3a7e-473d-9cdc-193f08a46870" />
</details>
<details>
  <summary>사원 패스워드 API</summary>
<img width="1797" height="289" alt="image" src="https://github.com/user-attachments/assets/f1111c46-55dd-4d1e-a22d-2568318358ee" />
</details>
<details>
  <summary>인증 API</summary>
<img width="1804" height="297" alt="image" src="https://github.com/user-attachments/assets/13d8d6da-df16-44c7-8938-ed2eb0562681" />
</details>
<details>
  <summary>결재 API</summary>
<img width="1301" height="745" alt="image" src="https://github.com/user-attachments/assets/28f44dff-c672-459e-8c33-75a50d1ea3b1" />
</details>
<details>
  <summary>환경설정 API</summary>
<img width="895" height="920" alt="image" src="https://github.com/user-attachments/assets/2bc22fef-2af5-4c2c-be28-c7926b9323cc" />
</details>
<details>
  <summary>퇴직 API</summary>
<img width="897" height="309" alt="image" src="https://github.com/user-attachments/assets/6414793b-60d8-40b9-8796-a08add614b58" />
</details>
<details>
  <summary>승진 API</summary>
<img width="898" height="535" alt="image" src="https://github.com/user-attachments/assets/5284a8e2-63d4-4316-b8b0-6444c7bdd0d7" />
</details>
<details>
  <summary>조직도 API</summary>
<img width="897" height="194" alt="image" src="https://github.com/user-attachments/assets/5cdebc31-8db5-42e8-9ab9-c5c8a4c0abec" />
</details>

</details>
<br>
<div align="right">
  <a href="#목차">🔝 맨 위로</a>
</div>
<hr>



## 단위 테스트

<details>
  <summary>테스트 목록</summary>
<details>
  <summary>근태 관리 단위 테스트</summary>
  <details>
    <summary>근태 조회</summary>
    <img width="1236" height="490" alt="image" src="https://github.com/user-attachments/assets/d1542966-fddb-4e5b-9a61-450cee6c3267" />
  </details>
  <details>
    <summary>근태 이벤트</summary>
    <img width="1556" height="617" alt="image" src="https://github.com/user-attachments/assets/c7f53848-784f-4ffa-b6dd-f0904cbc9d1b" />
  </details>
  <details>
<summary>근무제 템플릿 목록 조회</summary>
<img width="1315" height="379" alt="스크린샷 2026-01-08 오후 3 54 18" src="https://github.com/user-attachments/assets/cc7f0351-13de-4f3c-90af-6dc3d5f837ff" />
</details>

<details>
<summary>근무제 템플릿 등록/수정</summary>
<img width="1443" height="647" alt="스크린샷 2026-01-08 오후 3 54 26" src="https://github.com/user-attachments/assets/fa81e764-9072-460c-b9ea-6497a0ab1ee6" />
</details>
</details>
<details>
  <summary>휴가/연차 단위 테스트</summary>
<img width="1301" height="399" alt="image" src="" />
</details>
<details>
  <summary>전자 결재 단위 테스트</summary>
<img width="1296" height="593" alt="image" src="https://github.com/user-attachments/assets/22ecd0a5-6f73-4710-b9c2-32135926af6f" />
</details>
<details>
  <summary>성과 평가 단위 테스트</summary>
<img width="1436" height="895" alt="image" src="https://github.com/user-attachments/assets/ed5b7825-ebe4-4c8a-b605-7aceed00014c" />
</details>
<details>
  <summary>급여 관리 단위 테스트</summary>
<details>
<summary>급여 명세서 서비스 단위 테스트</summary>
<img width="1323" height="391" alt="스크린샷 2026-01-08 오후 4 28 29" src="https://github.com/user-attachments/assets/e5ee5cc7-a9a3-4f21-8bcd-3d92a31d99d3" />
</details>
  <details>
<summary>급여 리포트 서비스 단위 테스트</summary>
<img width="1365" height="433" alt="스크린샷 2026-01-08 오후 4 34 44" src="https://github.com/user-attachments/assets/782ec8a4-2ca9-401c-977a-1649b2f4c1c1" />
</details>
  <details>
<summary>급여 지급 조회 서비스 단위 테스트</summary>
<img width="1484" height="508" alt="스크린샷 2026-01-08 오후 4 24 03" src="https://github.com/user-attachments/assets/40cf8d54-c192-4109-9584-cd4cedae095d" />
</details>
  <details>
<summary>급여 배치 서비스 단위 테스트</summary>
<img width="1499" height="761" alt="스크린샷 2026-01-08 오후 4 16 38" src="https://github.com/user-attachments/assets/646655d2-a7c0-458d-83a2-dc1f29a63d83" />
</details>
  <details>
<summary>급여 계좌 서비스 단위 테스트</summary>
<img width="1463" height="657" alt="스크린샷 2026-01-08 오후 4 12 51" src="https://github.com/user-attachments/assets/e43ee36e-fbda-4097-8c52-fd6794dd8b26" />
</details>
<details>
<summary>수당 서비스 단위 테스트</summary>
<img width="1100" height="600" alt="스크린샷 2026-01-08 오후 3 55 46" src="https://github.com/user-attachments/assets/fae703ca-71fe-481d-91d5-95224f933b74" />
</details>
  <details>
<summary>공제 서비스 단위 테스트</summary>
<img width="1317" height="656" alt="스크린샷 2026-01-08 오후 4 17 11" src="https://github.com/user-attachments/assets/b8f9a1f7-c926-4cd6-b79f-59cf61729883" />
</details>
  <details>
<summary>급여 정책 서비스 단위 테스트</summary>
<img width="1236" height="672" alt="스크린샷 2026-01-08 오후 4 32 26" src="https://github.com/user-attachments/assets/3ff1630e-6bf3-488a-ba7f-841d6c276094" />
</details>
  <details>
<summary>급여 분석 Overview 서비스 단위 테스트</summary>
<img width="1374" height="443" alt="스크린샷 2026-01-08 오후 4 11 56" src="https://github.com/user-attachments/assets/00de89f6-5eab-4c8b-af40-388d24325654" />
</details>
  <details>
<summary>급여 구성 분석 서비스 단위 테스트</summary>
<img width="1224" height="417" alt="스크린샷 2026-01-08 오후 3 56 43" src="https://github.com/user-attachments/assets/b24cf811-96d3-405f-8955-861d42f779a0" />
</details>
  <details>
<summary>급여 조직 분석 서비스 단위 테스트</summary>
<img width="1374" height="443" alt="스크린샷 2026-01-08 오후 4 10 49" src="https://github.com/user-attachments/assets/695b7d2d-59cf-4da2-8984-56a7826112bb" />
</details>
</details>
<details>
  <summary>조직도 단위 테스트</summary>
<img width="1125" height="596" alt="image" src="https://github.com/user-attachments/assets/6a1575c6-8215-4bf7-8b74-67ac866fa683" />
</details>
<details>
  <summary>사원 관리 단위 테스트</summary>
  <details>
    <summary>사원 CUD</summary>
    <img width="1269" height="582" alt="image" src="https://github.com/user-attachments/assets/cfeddccf-8706-4d45-b8b0-318eaa6315a5" />
  </details>
  <details>
    <summary>사원 비밀번호 변경</summary>
    <img width="1315" height="572" alt="image" src="https://github.com/user-attachments/assets/6ca3c450-e611-4a60-b5f4-b88179f6a52b" />
  </details>
  <details>
    <summary>사원 프로필 정보RU</summary>
    <img width="1095" height="499" alt="image" src="https://github.com/user-attachments/assets/213b56da-2a1e-4605-baa0-51deb07a113f" />
  </details>
  <details>
    <summary>사원 정보 조회</summary>
    <img width="940" height="486" alt="image" src="https://github.com/user-attachments/assets/a60dbf96-1244-42a9-a7e2-3ae63d609e2b" />
  </details>
  <details>
    <summary>사원 직인 CUD</summary>
    <img width="1000" height="525" alt="image" src="https://github.com/user-attachments/assets/1c406a10-9d53-4318-90f9-596ffd1a2758" />
  </details>
  <details>
    <summary>승진 관련 조회</summary>
    <img width="1167" height="526" alt="image" src="https://github.com/user-attachments/assets/c2d723be-569b-4ea1-9567-6a15897fa9ef" />
  </details>
  <details>
    <summary>승진 관련 CU</summary>
    <img width="1019" height="530" alt="image" src="https://github.com/user-attachments/assets/ed509107-56b8-46aa-a6d3-9b94ce12cf88" />
  </details>
  <details>
    <summary>승진 처리</summary>
    <img width="1202" height="526" alt="image" src="https://github.com/user-attachments/assets/f919b032-f851-4b3b-b31e-3caf2c3b2689" />
  </details>
  <details>
    <summary>퇴사 현황 조회</summary>
    <img width="1043" height="489" alt="image" src="https://github.com/user-attachments/assets/5a6e1f96-9260-4ee2-88bc-35fbcc2ec581" />
  </details>
  
</details>
<details>
  <summary>알림 단위 테스트</summary>
<details>
<summary>알림 조회 서비스 단위 테스트</summary>
<img width="1315" height="429" alt="스크린샷 2026-01-08 오후 3 41 39" src="https://github.com/user-attachments/assets/60c330fe-4541-421f-a750-0bd2fcfa23f6" />
</details>
  <details>
<summary>알림 등록 및 웹소켓 전송</summary>
<img width="1315" height="429" alt="스크린샷 2026-01-08 오후 3 51 11" src="https://github.com/user-attachments/assets/790c77b8-0c63-4ac9-8a0d-ecc3a129cba7" />
</details>
  <details>
<summary>사원별 알림 설정 조회</summary>
<img width="1315" height="429" alt="스크린샷 2026-01-08 오후 3 47 56" src="https://github.com/user-attachments/assets/9fdce0fd-25b1-45d5-b33c-56c93739190f" />
</details>
  <details>
<summary>알림 설정 수정</summary>
<img width="1315" height="429" alt="스크린샷 2026-01-08 오후 3 48 00" src="https://github.com/user-attachments/assets/7c335dea-1329-4088-af1b-653c1474f36e" />
</details>
<details>
<summary>알림 상태/삭제 처리</summary>
<img width="1315" height="488" alt="스크린샷 2026-01-08 오후 3 51 17" src="https://github.com/user-attachments/assets/9f4d8895-6f60-4fd9-9d48-dd0bcd717bd9" />
</details>
<details>
<summary>삭제 알림 정리 배치</summary>
<img width="1315" height="429" alt="스크린샷 2026-01-08 오후 3 51 08" src="https://github.com/user-attachments/assets/21714159-9cc8-4309-9aca-e5df520d5009" />
</details>
<details>
<summary>출근 처리(지각 알림 이벤트)</summary>
<img width="1315" height="488" alt="스크린샷 2026-01-08 오후 3 52 51" src="https://github.com/user-attachments/assets/0d99aa00-c4cf-4ada-bc22-d0e1614385ce" />
</details>
<details>
<summary>퇴근 처리(주간 근무시간 경고 이벤트)</summary>
<img width="1315" height="488" alt="스크린샷 2026-01-08 오후 3 52 56" src="https://github.com/user-attachments/assets/37f17728-b71f-4208-9df7-03a6fbd013e4" />
</details>

<details>
<summary>출근 미체크 직원 조회</summary>
<img width="1315" height="379" alt="스크린샷 2026-01-08 오후 3 53 05" src="https://github.com/user-attachments/assets/e71819e8-287d-426c-b7b2-2f44c24d9d35" />
</details>


</details>
<details>
  <summary>파이썬 서버 연동 단위 테스트</summary>
<img width="1196" height="622" alt="image" src="https://github.com/user-attachments/assets/086b21ef-4534-4984-b419-b4d624b11798" />
</details>
</details>

<br>
<div align="right">
  <a href="#목차">🔝 맨 위로</a>
</div>
<hr>

## UI / UX 단위 테스트
<details>
  <summary>UI/UX 단위 테스트 작성중</summary>
<details>
<summary>로그인</summary>

![로그인화면 로그인](https://github.com/user-attachments/assets/d57cc4c2-376a-4c26-9973-8cbe70cc8147)
</details>

<details>
<summary>로그아웃</summary>

![로그아웃](https://github.com/user-attachments/assets/a1034b96-7875-4d7b-b4bd-3a3a96ab5f15)
</details>

<details>
<summary>마이페이지</summary>

![마이페이지 진입](https://github.com/user-attachments/assets/3ec83a52-2aa0-4669-be07-e134bcf17911)
</details>

<details>
<summary>개인 근태 이력</summary>

![근태 관리 - 근태기록 - 개인근태이력](https://github.com/user-attachments/assets/e3c20a26-150f-40c0-af92-61d3c036d41b)
</details>

<details>
<summary>근무 유형 변경 이력</summary>

![근태 관리 - 근태기록 - 근무유형변경이력](https://github.com/user-attachments/assets/123c792f-2de4-4f6a-bf10-595ea3bc27f9)
</details>

<details>
<summary>지연 근무 수정 이력</summary>

![근태 관리 - 근태기록 - 지연근무수정이력](https://github.com/user-attachments/assets/9099efbf-8c54-4035-a33e-1c5684158361)
</details>


<details>
<summary>초과 근무 이력</summary>

![근태 관리 - 근태기록 - 초과근무이력](https://github.com/user-attachments/assets/8204d81c-e678-402d-ab46-6432e80501b4)
</details>

<details>
<summary>근태 대시 보드</summary>

![근태 관리 - 근태대시보드](https://github.com/user-attachments/assets/09817ee0-4918-4c9b-8a0e-95f31e2a60a2)
</details>

<details>
<summary>부서 근태 현황</summary>

![근태 관리 - 부서 근태 현황](https://github.com/user-attachments/assets/061eea88-3346-48f5-952d-e307b699927d)
</details>

<details>
<summary>이력조회</summary>

![마이페이지 - 이력조회](https://github.com/user-attachments/assets/3c544f20-4e8c-4490-9eb9-eb427c9f01ec)
</details>

<details>
<summary>비밀번호수정</summary>

![마이페이지-비밀번호수정](https://github.com/user-attachments/assets/41748421-71dd-4143-8a23-c8bde61a765b)
</details>


<details>
<summary>연락처 수정</summary>

![마이페이지-연락처수정](https://github.com/user-attachments/assets/c5c8ff38-3af9-4dd9-80ff-279f2191976b)
</details>

<details>
<summary>직인 편집</summary>

![직인편집](https://github.com/user-attachments/assets/c084c8d5-31fe-4bf9-9928-e334f1c4d0f1)
</details>

<details>
<summary>직인 편집 변경</summary>

![직인편집-테스트로 변경](https://github.com/user-attachments/assets/b4a304c1-3e04-4ea8-881c-4fe97bfccd7f)
</details>

<details>
<summary>출근</summary>

![출근](https://github.com/user-attachments/assets/2de8f058-2b93-484d-8d5f-90151847ebdc)
</details>

<details>
<summary>퇴근</summary>

![퇴근](https://github.com/user-attachments/assets/d2883f01-c397-44dd-8025-89fd019de43c)
</details>

<details>
<summary>메인 대시보드</summary>

![메인 대시보드](https://github.com/user-attachments/assets/fc89b692-6951-49ab-be86-96fa3e0960d0)
</details>

<details>
<summary>인사 발령 품의서</summary>

![전자결재 - 결재문서서식 - 인사발령품의서](https://github.com/user-attachments/assets/a036330c-3de9-48bd-b7b5-ace1bd4d7cc3)
</details>

<details>
<summary>사직서</summary>

![전자결재 - 결재문서서식 - 사직서](https://github.com/user-attachments/assets/57c8b464-7404-49be-8b7a-52d025849f79)
</details>

<details>
<summary>승진 계획서</summary>

![전자결재 - 결재문서서식 - 승진계획서](https://github.com/user-attachments/assets/fce944ce-cc24-4a28-a9ed-8ccc90144c99)
</details>

<details>
<summary>결재 문서 서식</summary>

![전자결재 - 결재문서서식](https://github.com/user-attachments/assets/9ad97f30-9b23-4835-8b9e-0d97a237742d)
</details>

<details>
<summary>부서 휴가 현황</summary>

![휴가_연차 - 부서 휴가 현황](https://github.com/user-attachments/assets/21c801fd-aa4f-46de-869f-1f9763d8409b)
</details>

<details>
<summary>휴가 이력</summary>

![휴가_연차 - 휴가 이력](https://github.com/user-attachments/assets/cb5d2557-78e1-4b24-ba3d-f9cd2b925eef)
</details>

<details>
<summary>급여 인상 신청서</summary>

![전자결재 - 결재문서서식 - 급여인상신청서](https://github.com/user-attachments/assets/8611b256-0340-4e54-b9a7-7442b1c5c07c)
</details>

<details>
<summary>근무 변경 신청서</summary>

![전자결재 - 결재문서서식 - 근무변경신청서](https://github.com/user-attachments/assets/0e6c35f3-67ab-4f83-8aa5-9cc4cab664a4)
</details>

<details>
<summary>급여 조정 신청서</summary>

![전자결재 - 결재문서서식 - 급여조정신청서](https://github.com/user-attachments/assets/7a6433df-18fa-4100-819f-4b1c1b579ccb)
</details>

<details>
<summary>지연 출근 보고서</summary>

![전자결재 - 결재문서서식 - 지연출근보고서](https://github.com/user-attachments/assets/0718e0ad-b1a3-44c4-aee2-9539c328ccd4)
</details>

<details>
<summary>휴가 신청서</summary>

![전자결재 - 결재문서서식 - 휴가신청서](https://github.com/user-attachments/assets/16e51913-56d0-466a-b143-364516788a46)
</details>

<details>
<summary>초과 근무 신청서</summary>

![전자결재 - 결재문서서식 - 초과근무신청서](https://github.com/user-attachments/assets/7c4b6923-37df-4456-81b6-33cc71b112b0)
</details>

<details>
<summary>결재 문서함</summary>

![전자결재 - 결재문서함](https://github.com/user-attachments/assets/7bb51cbb-e260-4614-9c3b-266fd10ddd0c)
</details>

<details>
<summary>평가 템플릿 상세</summary>

![성과평가 - 평가템플릿 - 평가템플릿 상세](https://github.com/user-attachments/assets/0a2b505c-02de-4368-8429-20f777f7bc1c)
</details>

<details>
<summary>평가 템플릿 생성</summary>

![성과평가 - 평가템플릿 - 평가템플릿 생성](https://github.com/user-attachments/assets/c1f5a9f1-35e8-4942-8ce9-9723dc99969e)
</details>

<details>
<summary>평가 생성</summary>

![성과평가 - 평가 목록 - 평가 생성](https://github.com/user-attachments/assets/093821cf-9edb-4261-8df4-176f0e21d8d6)
</details>

<details>
<summary>평가 상세</summary>

![성과 평가 - 생성된 평가 - 평가 상세](https://github.com/user-attachments/assets/cbb35129-4771-4e16-bb97-362ace86fda9)
</details>

<details>
<summary>생성된 평가</summary>

![성과 평가 - 생성된 평가](https://github.com/user-attachments/assets/79cf8bce-c5b3-4151-8d29-860e1cc2f323)
</details>

<details>
<summary>평가 확인</summary>

![성과 평가 - 생성된 평가 - 평가 확인](https://github.com/user-attachments/assets/5ac5e7af-725e-45df-94c8-44841c75ab83)
</details>

<details>
<summary>평가 가이드</summary>

![성과 평가 - 평가 가이드](https://github.com/user-attachments/assets/87411b75-8c38-42c6-a481-299d1b34ba54)
</details>


<details>
<summary>팀원별 역량 상세 분석</summary>
<img width="1511" height="710" alt="성과 평가 - 팀 평가 대시보드 - 팀원별역량상세분석1" src="https://github.com/user-attachments/assets/a79798e9-c237-4969-bad9-f8c8b9501615" />
</details>

<details>
<summary>부서 등급 분포</summary>

![성과 평가 - 팀 평가 대시보드 - 부서등급분포](https://github.com/user-attachments/assets/d55f2a5c-aad3-4aec-8428-aa9ea78df946)
</details>

<details>
<summary>부서별 점수 비교</summary>

![성과 평가 - 팀 평가 대시보드 - 부서별점수비교](https://github.com/user-attachments/assets/ca48d341-8084-41ce-af05-a9bd44c7a56f)
</details>

<details>
<summary>팀원별 평가 점수 트렌드</summary>

![성과 평가 - 팀 평가 대시보드 - 팀원별평가점수트렌드](https://github.com/user-attachments/assets/366e2252-b68c-4738-89f0-c7b3cc8fd9f0)
</details>

</details>

<br>
<div align="right">
  <a href="#목차">🔝 맨 위로</a>
</div>
<hr>

## CI / CD 계획서
<details>
  <summary>내용 & 제목 수정 가능</summary>
</details>


<br>
<div align="right">
  <a href="#목차">🔝 맨 위로</a>
</div>
<hr>

## 프로젝트 회고

<h3>곽동근</h3>

**이번 프로젝트에서 맡은 역할**

-

**잘한점**

-

**아쉬운점**

-


**배운점**

-

<hr/>

<h3>김승민</h3>

**이번 프로젝트에서 맡은 역할**

- 평가 도메인 UI/UX 설계
- 평가 도메인 DB 모델링
- 평가 도메인 기능 개발
- 파이썬 서버 구축 및 AI 기능 개발

**잘한점**

- 프로젝트 초기에 계획했던 3차 목표인 'RAG를 사용한 AI기능을 개발'까지 완료함.
- 기능 구현에 있어서 유효성 검사, 예외 처리 코드를 추가하여 에러 발생 가능성을 낮춤.
- 원활한 협업을 위해 사전에 약속한 코딩 컨벤션을 지키면서 코드를 작성함.
- 처음 사용하는 프레임워크인 Fast API와 RAG 지식을 공부하며 기능에 적용하기 위한 노력을 함.

**아쉬운점**

- 평가 가이드 위반을 탐지하는 기능에서 AI의 답변을 일정하게 추출하도록 기능 개발을 하지 못함.
- 회사 평가에 대한 지식이 부족하여 평가 프로세스가 어색한 부분이 있음.


**배운점**

- HR 시스템의 특징과 현업에서 사용되는 용어 및 평가 방식.
- 권한에 따른 사용자 기능 분할과 Security Config에서 경로 접근 권한 설정의 중요성.
- 스프링 스케줄러를 사용하여 원하는 시간에 원하는 서비스를 작동시키는 로직.
- Fast API의 서버 구축 방법 및 텍스트를 원하는 chunk 단위로 잘라 임베딩 시키고 벡터 데이터베이스에 저장하는 로직.
- RAG를 사용하여 주어진 데이터에서 원하는 값을 추출하는 로직.

<hr/>

<h3>변민철</h3>

**이번 프로젝트에서 맡은 역할**

- 결재 도메인 API 및 UI 구현
- 결재 연동 관련 도메인 API 검증 및 로직 수정

**잘한 점**

- 비즈니스 규칙을 적용하여 서비스 로직을 구성했으며, 예외처리 및 동시성 제어를 수행하여 기술적인 완벽을 기함.
- 이벤트 리스너를 활용함으로써 타 도메인과의 API 연동 과정에서 기능이 정상적인 동작을 하는지 검증한 점.

**아쉬운 점**

- 긴 시간이 주어졌음에도 기획단계에서 주제가 명확하지 않았음. 
- 요구사항 및 해당 요구사항에 대한 비즈니스 규칙을 세심하게 검토하지 못했던 점.
- 주장에 대한 설득이 부족하다는 핑계로 아이디어를 제시하지 못함.

**배운 점**

- 코드 구현(API, 프론트) 전, 도메인 규칙과 기준을 명확히 세우지 않아 개발 후반부 수정 비용이 급증함.
- 명확한 기준 수립이 개발 효율과 완성도를 결정함을 체감.
- 막연한 기능 구현이 아닌, '어떤 문제를 해결하기 위한 기능인가'에 대한 명확한 목적의식이 필수적임을 학습.
- '요구사항 명세 → 프로세스 설계 → 구현'이라는 정석적인 순서의 중요성을 몸소 깨달음.
- 어떤 문제를 해결하기 이전에 해당 도메인에 대한 이해가 해결책을 끌어내는 열쇠라는 것을 깨닫음.

<hr/>

<h3>이승건</h3>

**이번 프로젝트에서 맡은 역할**

-

**잘한점**

-

**아쉬운점**

-


**배운점**

- 

<hr/>

<h3>이지윤</h3>

**이번 프로젝트에서 맡은 역할**

-

**잘한점**

-

**아쉬운점**

-


**배운점**

-

<hr/>

<h3>최혜원</h3>

**이번 프로젝트에서 맡은 역할**

-

**잘한점**

-

**아쉬운점**

-


**배운점**

-



<br>
<div align="right">
  <a href="#목차">🔝 맨 위로</a>
</div>
