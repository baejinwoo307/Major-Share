# 1. Major Share (전공 자산 선순환 네트워크)

| Student No | 22211987 |
| :--- | :--- |
| Name | 배진우|
| E-mail | ajdjjaja292983474@gmail.com |


## [ Revision history ]
| Revision date | Version # | Description | Author |
| :--- | :--- | :--- | :--- |
| 2026/03/27 | 1.00 | First draft | 배진우 |
| 2026/05/04 | 1.01 | Use case수정 | 배진우 |

## = Contents =
1. [Business purpose](#1-business-purpose)
2. [System context diagram](#2-system-context-diagram)
3. [Use case list](#3-use-case-list)
4. [Concept of operation](#4-concept-of-operation)
5. [Problem statement](#5-problem-statement)
6. [Glossary](#6-glossary)
7. [References](#7-references)

---

## 1. Business purpose
- **Project background & Motivation:** 매 학기 시작마다 대학 생활 중 요구되는 전공 및 교양 서적과 실험 기자재(ex 아두이노, 라즈베리 파이) 구입비는 적게는 수만 원에서 많게는 수십만 원에 달하는 가격입니다. 이 가격은 대학생들에게는 꽤나 큰 부담을 주는 금액들입니다. 게다가 이러한 자산들은 특정 학기의 수업 기간에만 집중적으로 사용된 후 나머지 기간에는 유휴 자산으로 방치되는 경우가 많고 추후에 사용하더라도 자원 활용의 효율성이 낮습니다.이러한 부담을 줄이는 방법인 에브리타임이나 당근마켓과 같은 기존 중고 거래 플랫폼은 존재하지만 이러한 플랫폼들은 일회성 매매에만 편중되어 있습니다.특히나 전공서적의 경우 지금 당장은 사용하지않더라도 추후에 사용할 가능성이 꽤나 크기에 매매를 하는것은 다소 꺼려질 수 있습니다.또한 단기간만 물건이 필요한 학생들에게는 매번 사고파는 과정이 번거로울 뿐만 아니라 대여를 원하더라도 신뢰할 수 있는 중개 시스템과 반납 확인 로직이 없어 개인 간 대여가 활성화되지 못하고 있습니다.이러한 환경은 학생들의 경제적 부담을 가중시킬 뿐만 아니라, 캠퍼스 내 자원의 효율적 배분을 저해하는 요소가 됩니다.따라서 자산의 소유권을 완전히 이전하는 매매와 일시적인 사용권을 공유하는 대여를 모두 가능하게 해줄 새로운 대안이 필요합니다.
 
<p align="center">
  <img src="https://github.com/user-attachments/assets/5dd71caf-cb29-4ceb-97fe-f0977679b088" alt="대학서적" width="60%">
  <br>
  <em>&lt;그림 1 : 2019 대학생 대상 설문조사&gt;</em>
</p>

  
- **Goal:** Major Share는 전공 자산의 매매와 대여를 통합 관리하는 플랫폼입니다. 특히 실시간 예약 및 자동 연장 제어 시스템을 통해 자원 독점을 방지하고 활용도를 극대화하여 공정한 선순환 경제를 형성하는 것이 목표입니다.
- **Target market:** 전국의 대학생들.

## 2. System context diagram

<img width="763" height="548" alt="system diagram" src="https://github.com/user-attachments/assets/c81ee6df-f143-4763-8348-60484b2d2b5e" />



* **Login :** 로그인
* **Register Item :** 판매/대여 물품 상세 정보 등록
* **Request Extension :** 대여 중인 물품의 반납 기한 연장 신청
* **Reserve Item :** 대여 중인 물품에 대한 차기 대기 예약 신청
* **Accept Reservation :** 들어온 예약 신청 확인 및 승인 또는 거절
* **Search & Filter :** 카테고리 및 거래 방식별 물품 검색 요청
* **Request Transaction :** 매매 구매 또는 대여/예약 신청 전송
* **Manage Requests :** 공급자의 거래/연장/예약 요청 승인 및 거절
* **Confirm Pickup/Return :** 물품 수령 및 반납 상호 인증
* **Transaction Review :** 거래 종료 후 물품 상태 및 유저 매너 평가 기록
* **Chat & Messaging :** 거래 당사자 끼리 거래 세부 사항을 실시간으로 문의하고 답변한다
* **System Monitoring :** 시스템 로그 및 이상 거래 데이터 모니터링

## 3. Use case list

### 1) Login
| **Actor** | Seller/Lender,Buyer/Borrower/Admin |
| :--- | :--- |
| **Description** | 각자 자신의 계정으로 로그인하고 올바른 정보를 입력했다면 로그인에 성공하게 된다. |

### 2) Register Item
| **Actor** | Seller/Lender |
| :--- | :--- |
| **Description** | 판매 또는 대여하고자 하는 전공 자산의 상세 정보(상태, 가격, 대여 기간 등)를 시스템에 등록한다. |

### 3) Request Extension
| **Actor** | Buyer/Borrower |
| :--- | :--- |
| **Description** | 대여 중인 물품의 기간 연장을 신청한다. 단, 예약 대기자가 없을 때만 시스템에 의해 활성화된다. |

### 4) Reserve Item
| **Actor** | Buyer/Borrower |
| :--- | :--- |
| **Description** | 현재 타 사용자가 이용 중인 물품에 대해 차기 대여 예약을 신청한다. |

### 5) Accept Reservation
| **Actor** | Seller/Lender |
| :--- | :--- |
| **Description** | 들어온 예약 신청 목록을 확인하고 특정 사용자의 예약 요청을 승인하여 우선권을 부여한다. |

### 6) Search & Filter
| **Actor** | Buyer/Borrower |
| :--- | :--- |
| **Description** | 물품 카테고리, 거래 방식(매매/대여), 예약 가능 여부 등 필터를 적용하여 검색한다. |

### 7) Request Transaction
| **Actor** | Buyer/Borrower |
| :--- | :--- |
| **Description** | 특정 물품에 대해 구매 요청 또는 대여/예약 요청을 공급자에게 전송한다. |

### 8) Manage Requests
| **Actor** | Seller/Lender |
| :--- | :--- |
| **Description** | 본인의 물품에 들어온 거래 및 연장/예약 요청을 대시보드에서 통합 관리한다. |

### 9) Confirm Pickup/Return
| **Actor** | Seller/Lender, Buyer/Borrower |
| :--- | :--- |
| **Description** | 대면 거래 후 물품 수령 및 반납 완료를 상호 확인하여 시스템 상태를 업데이트한다. |

### 10) Transaction Review
| **Actor** | Seller/Lender, Buyer/Borrower |
| :--- | :--- |
| **Description** | 거래 종료 후 상대방의 매너 및 물품 보존 상태에 대한 별점과 후기를 기록한다. |

### 11) Chat & Messaging
| **Actor** | Seller/Lender, Buyer/Borrower |
| :--- | :--- |
| **Description** | 거래 세부 사항(직거래 시간, 장소 등)을 조율하거나 물품 상태에 대해 실시간으로 문의하고 답변한다. |

### 12) System Monitoring
| **Actor** | Admin |
| :--- | :--- |
| **Description** | 전체 거래 데이터를 감시하여 부정 거래나 부적절한 게시물 및 채팅을 관리한다. |

## 4. Concept of operation

### 1) Login
<table>
  <tr><td><b>Purpose</b></td><td>어플의 사용을 위해 등록된 계정임을 확인받아 기능들을 사용하기 위함.</td></tr>
  <tr><td><b>Approach</b></td><td>어플 실행 시 올바른 정보를 입력했다면 로그인이 되어 기능들을 사용할 수 있게 하고, 잘못된 정보 입력 시 재입력, 아이디/비밀번호 찾기, 회원가입 등의 기능을 제공함.</td></tr>
  <tr><td><b>Dynamics</b></td><td>시스템 진입 후 사용자가 자신의 정보를 입력할 때 작동함.</td></tr>
  <tr><td><b>Goals</b></td><td>허위 사용자 유입 차단 및 거래 데이터의 무결성 보장.</td></tr>
</table>

### 2) Register Item
<table>
  <tr><td><b>Purpose</b></td><td>사용자의 유휴 전공 자산을 시장에 공개하여 공유 경제 가치를 창출함.</td></tr>
  <tr><td><b>Approach</b></td><td>물품 사진, 상태 설명, 가격, 대여 가능 기간 등을 DB에 상세히 등록함.</td></tr>
  <tr><td><b>Dynamics</b></td><td>공급자(Seller/Lender)가 물품 등록 메뉴를 통해 정보를 최종 입력하는 시점에 트리거됨.</td></tr>
  <tr><td><b>Goals</b></td><td>플랫폼 내 풍부한 자산 데이터베이스 확보 및 활발한 자원 공유 유도.</td></tr>
</table>

### 3) Request Extension
<table>
  <tr><td><b>Purpose</b></td><td>대여 중인 물품의 사용 기간을 연장하여 사용자의 학습 편의를 도모함.</td></tr>
  <tr><td><b>Approach</b></td><td>대여 현황 페이지에서 연장을 신청하며, 차기 예약자 유무를 시스템이 판단함.</td></tr>
  <tr><td><b>Dynamics</b></td><td>대여 종료일 전, 사용자가 연장 버튼을 클릭할 때 트리거됨.</td></tr>
  <tr><td><b>Goals</b></td><td>유연한 대여 정책 제공을 통한 사용자 만족도 및 자원 활용도 제고.</td></tr>
</table>

### 4) Reserve Item
<table>
  <tr><td><b>Purpose</b></td><td>현재 이용 중인 인기 자산에 대해 차기 사용 권한을 미리 확보하여 대기 시간을 관리함.</td></tr>
  <tr><td><b>Approach</b></td><td>예약 대기 큐(Queue)에 사용자를 등록하고 우선순위 순번을 부여함.</td></tr>
  <tr><td><b>Dynamics</b></td><td>물품 상태가 대여 중일 때 수요자가 예약 신청 버튼을 클릭하면 작동함.</td></tr>
  <tr><td><b>Goals</b></td><td>자원 독점 방지 및 투명하고 공정한 예약 대기.</td></tr>
</table>

### 5) Accept Reservation
<table>
  <tr><td><b>Purpose</b></td><td>특정 예약자의 신뢰도를 확인하고 공급자가 최종 대여 순번을 확정함.</td></tr>
  <tr><td><b>Approach</b></td><td>공급자의 관리 대시보드에서 대기 리스트를 검토한 후 승인/거절 상태를 업데이트함.</td></tr>
  <tr><td><b>Dynamics</b></td><td>수요자의 예약 신청 이벤트 이후 공급자가 승인 버튼을 누르는 시점에 발생함.</td></tr>
  <tr><td><b>Goals</b></td><td>공급자의 자율적인 예약 관리 권한 보장.</td></tr>
</table>

### 6) Search & Filter
<table>
  <tr><td><b>Purpose</b></td><td>플랫폼 내 등록된 수많은 전공 자산 중 사용자가 원하는 물품을 신속히 탐색함.</td></tr>
  <tr><td><b>Approach</b></td><td>카테고리, 대학별 거점, 거래 방식(매매/대여), 물품 상태 등 다중 필터 검색 기능을 제공함.</td></tr>
  <tr><td><b>Dynamics</b></td><td>사용자가 검색바를 이용하거나 필터 옵션을 선택하여 조회 요청을 보낼 때 작동함.</td></tr>
  <tr><td><b>Goals</b></td><td>탐색 시간 단축을 통한 사용자 경험 최적화 및 거래 연결성 향상.</td></tr>
</table>

### 7) Request Transaction
<table>
  <tr><td><b>Purpose</b></td><td>특정 물품에 대한 구매 또는 대여 의사를 공식적으로 전달하여 거래를 개시함.</td></tr>
  <tr><td><b>Approach</b></td><td>물품 상세 페이지에서 신청 폼을 작성하여 공급자에게 실시간 알림을 전송함.</td></tr>
  <tr><td><b>Dynamics</b></td><td>수요자(Buyer/Borrower)가 구매 신청 또는 대여 신청 버튼을 클릭할 때 발생함.</td></tr>
  <tr><td><b>Goals</b></td><td>거래 매칭 성공 및 명확한 거래 의사소통 지원.</td></tr>
</table>

### 8) Manage Requests
<table>
  <tr><td><b>Purpose</b></td><td>공급자에게 들어온 수많은 매매/대여/연장 요청을 한눈에 파악하고 처리함.</td></tr>
  <tr><td><b>Approach</b></td><td>통합 대시보드에서 각 요청의 상태를 실시간으로 확인하고 승인 여부를 결정함.</td></tr>
  <tr><td><b>Dynamics</b></td><td>공급자가 관리 메뉴에 접속하거나 새로운 요청 알림을 클릭할 때 활성화됨.</td></tr>
  <tr><td><b>Goals</b></td><td>거래 프로세스의 투명성 확보 및 다중 거래의 효율적 제어.</td></tr>
</table>

### 9) Confirm Pickup/Return
<table>
  <tr><td><b>Purpose</b></td><td>실제 오프라인 거래의 성사 여부를 시스템에 최종적으로 동기화함.</td></tr>
  <tr><td><b>Approach</b></td><td>상호 확정 버튼 클릭 혹은 디지털 인증 수단을 통해 대면 거래 완료를 증명함.</td></tr>
  <tr><td><b>Dynamics</b></td><td>물리적인 물품 전달 또는 반납이 이루어지는 현장에서 트리거됨.</td></tr>
  <tr><td><b>Goals</b></td><td>거래 데이터 정합성 유지 및 물품 소유권/사용권의 명확한 이전 기록.</td></tr>
</table>

### 10) Transaction Review
<table>
  <tr><td><b>Purpose</b></td><td>거래 경험을 공개하여 사용자 간의 신뢰도를 높이고 커뮤니티 신뢰성을 유지함.</td></tr>
  <tr><td><b>Approach</b></td><td>거래 종료 후 상대방의 매너 및 자산 보존 상태를 별점과 후기로 기록함.</td></tr>
  <tr><td><b>Dynamics</b></td><td>거래 완료 상태로 전환된 직후 시스템이 사용자에게 평가 작성을 유도함.</td></tr>
  <tr><td><b>Goals</b></td><td>불량 사용자 필터링 및 건강한 캠퍼스 공유 문화 정착.</td></tr>
</table>

### 11) Chat & Messaging
<table>
  <tr><td><b>Purpose</b></td><td>개인 연락처 노출 없이 안전하게 거래 세부 사항을 조율하고, 물품에 대한 추가 정보를 교환하여 거래 성사율을 높임.</td></tr>
  <tr><td><b>Approach</b></td><td>게시물에 있는 채팅버튼을 활성화 시에 1:1 실시간 채팅 인터페이스를 통해 텍스트 및 이미지 데이터를 주고받음.</td></tr>
  <tr><td><b>Dynamics</b></td><td>각 게시물에 존재하는 채팅하기 버튼을 클릭하는 시점에 트리거됨.</td></tr>
  <tr><td><b>Goals</b></td><td>플랫폼 내 사용자끼리의 커뮤니케이션 채널 확보.</td></tr>
</table>

### 12) System Monitoring
<table>
  <tr><td><b>Purpose</b></td><td>플랫폼 전체의 안정성을 유지하고 부정 거래나 부적절한 게시물을 관리함.</td></tr>
  <tr><td><b>Approach</b></td><td>관리자 전용 대시보드를 통해 시스템 로그 및 거래 데이터를 감시함.</td></tr>
  <tr><td><b>Dynamics</b></td><td>관리자가 운영 도구에 접속하거나 시스템 내 이상 징후 발생 시 트리거됨.</td></tr>
  <tr><td><b>Goals</b></td><td>서비스 안정성 확보 및 대규모 네트워크 운영의 가용성 보장.</td></tr>
</table>

## 5. Problem statement

### 5.1 Overview
**Major Share**는 전국 대학생들이 전공 서적 및 기자재를 매매/대여함으로써 학업 비용을 절감하고 자원 활용의 효율성을 높이는 플랫폼이다. 단순 중고 거래를 넘어 실시간 예약과 자동 권한 제어라는 정교한 로직을 통해 캠퍼스 내 공정한 선순환 경제 구축을 목표로 한다. 이를 안정적으로 구현하기 위해 해결해야 할 기술적·운영적 과제(Problem Statement)들을 다음과 같이 정의한다.

---

### 5.2 Problem Definition
* **Problem #1: Concurrent Reservation Control (동시성 제어)**
  전공 자산은 학기 초나 시험 기간에 수요가 폭발적으로 집중된다. 이때 여러 사용자가 동시에 동일한 물품에 대해 예약(`Reserve Item`) 및 연장(`Request Extension`)을 신청할 경우, 데이터 경합(`Race Condition`)으로 인한 순번 오류나 중복 예약 문제가 발생할 수 있다. 따라서 수천 명의 동시 접속 상황에서도 예약 큐(`Queue`)의 순서를 엄격히 보장하고 데이터 무결성을 유지할 수 있는 백엔드 동시성 처리 로직이 필수적이다.

* **Problem #2: Complex Automation Logic (자동화 로직의 정교함)**
  본 서비스의 핵심인 `Auto-Succession`과 `Block Extension`은 매우 복잡한 상태 전이를 수반한다. 기존 사용자의 반납 즉시 시스템이 차기 예약자를 찾아 권한을 넘기는 과정에서 발생할 수 있는 다양한 예외 시나리오(예: 반납 지연, 예약 취소 등)를 고려한 정밀한 트리거 설계와 시스템 제어 로직이 요구된다.

* **Problem #3: Notification Reliability (알림의 신뢰성)**
  메시지가 도착했음에도 푸시 알림이 오지 않거나 늦게 도착한다면 사용자는 거래 기회를 놓칠 수도 있다. 특히 예약 순번이 돌아왔을 때나 긴급한 반납 요청 메시지의 경우, 앱이 백그라운드 상태에서도 정확하고 빠르게 알림을 전달할 수 있는 기능이 필수적이다.

* **Problem #4: Information Security (정보 보안)**
  로그인 정보와 1:1 채팅 내역은 철저히 보호되어야 할 민감 데이터이다. P2P 거래 과정에서 개인정보가 무분별하게 노출되지 않도록 암호화 기술을 적용하고, 관리자가 사용하는 모니터링 대시보드에서도 개인별 접근 권한 관리를 통해 데이터 보안을 철저히 관리해야 한다.

---

### 5.3 Non-Functional Requirements (NFRs)
1. **성능(Efficiency):** 대규모 트래픽 환경에서도 핵심 기능(검색, 예약)의 응답 시간을 **평균 3초 이내**로 유지해야 한다.
2. **사용성(Usability):** 메시지 교환 및 여러 페이지를 빈번하게 이동하는 사용 환경을 고려하여, 각 페이지의 로드 시간 또한 **3초 이내**로 유지되어야 한다.
3. **신뢰성(Reliability):** 학기 초나 시험 기간 등 트래픽이 집중되는 시기에도 **99% 이상의 가용성**을 보장하여 시스템 중단이 발생하지 않도록 해야 한다.


## 6. Glossary

| 용어 (Term) | 정의 (Definition) |
| :--- | :--- |
| **Major Share** | 대학 전공 서적 및 기자재의 선순환을 목표로 하는 대학생 전용 매매/대여 통합 플랫폼의 명칭이다. |
| **Lender / Borrower** | 자산을 공급하는 대여자(공급자)와 자산을 필요로 하는 차용자(수요자)를 의미하며, 모든 대학생 사용자는 두 역할을 동시에 수행할 수 있다. |
| **Reservation Queue** | 특정 인기 물품에 대해 대기 중인 예약자들의 순번과 우선순위를 관리하는 시스템 내 데이터 구조이다. |
| **Auto-Succession** | 기존 대여자의 반납이 확인되는 즉시, 시스템 트리거에 의해 차기 예약자에게 대여 권한이 자동으로 이전되는 로직이다. |
| **Block Extension** | 예약 대기자가 존재할 경우, 자원 독점을 방지하기 위해 시스템이 기존 대여자의 연장 신청 기능을 강제로 비활성화하는 제어 기능이다. |
| **Concurrency Control** | 다수의 사용자가 동시에 예약이나 거래를 신청할 때, 데이터의 충돌 없이 정확한 순서를 보장하기 위한 백엔드 처리 기술이다. |
| **P2P (Peer-to-Peer)** | 중개인 없이 개인과 개인 간에 직접 물품을 거래하거나 대여하는 방식을 의미한다. |

## 7. References
1) 그림 1에 대한 정보: https://www.gnunews.kr/news/articleView.html?idxno=8711
