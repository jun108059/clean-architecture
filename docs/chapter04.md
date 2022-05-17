# 4장. 유스케이스 구현하기

> 헥사고날 아키텍처는 유스케이스를 어떻게 구현할까

각 계층이 아주 느슨하게 결합돼 있기 때문에 필요한 도메인 코드를 자유롭게 모델링 가능

**도메인 코드 모델링 예시**
- DDD
- rich 도메인 모델
- anemic 도메인 모델
- 이 외 우리만의 방식

헥사고날 아키텍처는 **도메인 중심** 의 아키텍처에 적합

> 도메인 엔티티 생성 → 유스케이스 구현

## 1. 도메인 모델 구현하기

### 1-1. 유스케이스

> (1) 내 계좌 → 다른 계좌 송금

- Account 엔티티 생성
- 출금 계좌에서 돈 출금
- 입금 계좌로 입금

```jsx
buckpal
|-- domain
|   |-- Account
|   |-- Activity
|   |-- AccountRepositoryy
|   |-- AccountService
|-- persistence
|   |-- AccountRepositoryImpl
|-- web
    |-- AccountController
```

웹 계층(web), 도메인 계층(domain), 영속성 계층(persistence)로 구분했다.

### 1-1. 문제점

> 계층으로 구성한 패키지 구조가 최적의 구조가 아니다

1. 계층으로 코드를 구성하면 기능적인 측면들이 섞이기 쉬움
2. 어플리케이션의 기능 조각(functional slice)이나 특성(feature)을 구분 짓는 패키지 경계가 없음
   - 서로 연관되지 않은 기능들끼리 예상하지 못한 부수효과를 일으킬 수 있는 클래스들의 묶음으로 변모 가능
3. 어플리케이션이 어떤 유스케이스들을 제공하는지 파악 불가
   - 서비스 내의 어떤 메서드가 특정 기능에 대한 책임을 수행하는지 찾아야 함
4. 패키지 구조를 통해 아키텍처를 파악할 수 없음
   - 어떤 기능이 웹 어댑터에서 호출되는지, 영속성 어댑터가 도메인 계층에 어떤 기능을 제공하는지 한눈에 볼 수 없음

## 2. 기능으로 구성하기

```jsx
buckpal
|-- account
    |-- Account
    |-- AccountController
    |-- AccountRepository
    |-- AccountRepositoryImpl
    |-- SendMoneyService
```

> account 패키지로 묶고 계층 패키지 삭제  
> 여전히 아키텍처가 보이지 않는다

#### 장점
- 패키지 경계를 `package-private` 접근 수준으로 각 기능 사이의 불필요한 의존성을 방지할 수 있음 

#### 단점
- 가시성 떨어짐
- `package-private` 접근 수준을 이용해 도메인 코드가 실수로 영속성 코드에 의존하는 것을 막을 수 없음

## 3. 아키텍처적으로 표현력 있는 패키지 구조

```jsx
buckpal
|-- account
    |-- adapter
    |   |-- in
    |   |   |-- web
    |   |       |-- AccountController
    |   |-- out
    |   |   |-- persistence
    |   |       |-- AccountPersistenceAdapter
    |   |       |-- SpringDataAccountRepository
    |-- domain
    |   |-- Account
    |   |-- Activity
    |-- application
        |-- SendMoneyService
        |-- port
            |-- in
            |   |-- SendMoneyUseCase
            |-- out
            |   |-- LoadAccountPort
            |   |-- UpdateAccountStatePort
```

### 3-1. 핵사고날 아키텍처 패키지 구조

`Account`와 관련된 유스케이스는 모두 account 패키지 안에 있다.

- **domain**
  - 도메인 모델 : `Account`
- **application**
  - 도메인 모델을 둘러싼 서비스 계층 : `SendMoneyService`
  - 인커밍 포트 인터페이스 : `SendMoneyUseCase`
  - 아웃고잉 포트 인터페이스 : `LoadAccountPort`, `UpdateAccountStatePort`
- **adapter**
  - 어플리케이션 계층의 인커밍 포트를 호출하는 인커밍 어댑터 : `Controller`
  - 어플리케이션 계층의 아웃고잉 포트에 대한 구현을 제공하는 아웃고잉 어댑터 : `PersistenceAdapter`, `Repository`

### 3-2. 헥사고날 아키텍처 구조의 장점

#### 장점

1. 아키텍처 요소에 정해진 위치가 있어 **직관적**
2. 어댑터 코드를 자체 패키지로 이동시키면 필요할 경우 **하나의 어댑터를 다른 구현으로 쉽게 교체 가능**
3. DDD 개념을 직접적으로 대응시킬 수 있음
   - 상위 레벨 패키지는 다른 바운디드 컨텍스트와 통신할 전용 진입점과 출구(포트)를 포함하는 바운디드 컨텍스트에 해당
4. 모델-코드 갭(아키텍처-코드 갭)을 효과적으로 다룰 수 있음 
   - 아키텍처 모델에는 항상 코드에 매핑할 수 없는 추상적인 개념, 기술 선택 및 설계 결정이 혼합되어 있다. 
   - 최종 결과는 모델이 정한 구성 요소의 배열과 반드시 일치하지 않는 소스 코드가 될 수 있다. 
5. 패키지간 접근을 제어할 수 있다.
   - `package-private`인 adapter 클래스
     - ⭐️ **모든 클래스는 application 패키지 내의 포트 인터페이스를 통해 바깥에 호출되기 때문에 adapter는 모두 `package-private` 접근 수준이면 됨**
     - 어플리케이션 계층에서 어댑터로 향하는 우발적 의존성은 있을 수 없음
   - `public`이어야 하는 application, domain의 일부 클래스
     - application의 port(in, out)
       - `SendMoneyUseCase`, `LoadAccountPort`, `UpdateAccountStatePort`
     - 도메인 클래스
       - `Account`, `Activity`
   - `package-private`이어도 되는 서비스 클래스
     - 인커밍 포트 인터페이스 뒤에 숨겨지는 서비스는 public일 필요가 없다.
       - `GetAccountBalanceService`

## 4. 의존성 주입의 역할

- **클린 아키텍처의 본질적인 요건**
  - 어플리케이션이 인커밍/아웃고잉 **어댑터에 의존성을 갖지 않아야 함**
- **의존성 역전 원칙 이용**
  - 어플리케이션 계층에 인터페이스(`port`)를 만들고 어댑터에 해당 인터페이스를 구현한 클래스를 둔다.
  - ⭐️ **모든 계층에 의존성을 가진 중립적인 컴포넌트**를 하나 두고, 이 컴포넌트가 아키텍처를 구성하는 **대부분의 클래스를 초기화하는 역할**
  - 웹 컨트롤러가 서비스에 의해 구현된 인커밍 포트를 호출한다. 서비스는 어댑터에 의해 구현된 아웃고잉 포트를 호출한다.

<img width="700" alt="image" src="https://user-images.githubusercontent.com/42997924/167231367-29cb82db-ce46-4149-b95e-0fab96220baa.png">

- AccountController
  - SendMoneyUseCase 인터페이스가 필요하므로 의존성 주입을 통해 SendMoneyService 클래스의 인스턴스를 주입
- SendMoneyService
  - LoadAccount 인터페이스로 가장한 AccountPersistenceAdapter 클래스의 인스턴스 주입

## 5. 유지보수 가능한 소프트웨어를 만드는 데 어떻게 도움이 될까?

- 코드에서 아키텍처의 특정 요소를 찾으려면 **아키텍처 다이어그램의 박스 이름을 따라 패키지 구조를 탐색**하면 된다.
- 이를 통해 **의사소통, 개발, 유지보수**가 더 수월해진다.

**Reference**
- [만들면서 배우는 클린 아키텍처 - 자바 코드로 구현하는 클린 웹 애플리케이션](http://www.kyobobook.co.kr/product/detailViewKor.laf?ejkGb=KOR&mallGb=KOR&barcode=9791158392758)