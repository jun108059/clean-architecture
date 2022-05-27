# 10장. 아키텍처 경계 강제하기

**경계를 강제해야 하는 이유**

- 일정 규모 이상의 프로젝트는 시간이 지나면 아키텍처는 무너지게된다.
- 계층 간의 경계 약화 → 점점 테스트가 어려워 짐 → 새로운 기능을 구현하는데 점점 많은 시간 소요

> `경계를 강제하는 방법`과 `아키텍처 붕괴에 맞서 싸우기 위한 조치`를 알아보자

## 10-1. 경계와 의존성

**경계를 강제한다**

- 의존성이 올바른 방향(안쪽 방향)을 향하도록 강제한다는 것

![https://user-images.githubusercontent.com/16996054/159099232-0e21d2ba-df6d-4c6b-b4b3-b3d1a0f634be.png](https://user-images.githubusercontent.com/16996054/159099232-0e21d2ba-df6d-4c6b-b4b3-b3d1a0f634be.png)

⬆️ 출처 : [Get Your Hands Dirty on Clean Architecture](https://subscription.packtpub.com/book/web_development/9781839211966/2/ch02lvl1sec11/hexagonal-architecture)

> 점선은 허용되지 않은 의존성을 의미한다.

**의존성 관계**

- 애플리케이션 → 도메인
- 어댑터 → 인커밍 포트 → 서비스 → 아웃고잉 포트 → 어댑터
- 설정 계층 → 어댑터 & 서비스

> 가장 안쪽 계층에 도메인 엔티티가 있고, 설정 계층은 `의존성 주입(DI)` 메커니즘을 제공한다.

## 10-2. 접근 제한자

`package-private (=default)` 접근 제한자의 중요성

- 클래스들을 응집적인 **모듈**로 만들어 준다
- 패키지 내에서 클래스끼리 서로 접근 가능하고 패키지 바깥에서는 접근 불가
- 모듈의 진입점이 되는 클래스는 public으로 선언하면 된다!

> **경계간 외부로 드러난 port를 이용하자**  
> 올바른 의존성 방향 규칙을 위반할 위험이 줄어든다.

![https://user-images.githubusercontent.com/16996054/159099487-e8d60bea-d5b5-4970-b480-6e53a8780c18.png](https://user-images.githubusercontent.com/16996054/159099487-e8d60bea-d5b5-4970-b480-6e53a8780c18.png)

⬆️ 출처 : [Get Your Hands Dirty on Clean Architecture](https://subscription.packtpub.com/book/web_development/9781839211966/2/ch02lvl1sec11/hexagonal-architecture)

- persistence 패키지에 있는 클래스들은 외부에서 접근할 필요가 없기 때문에 package-private 으로 만들 수 있다.
- SendMoneyService 도 같은 이유로 package-private

> DI 메커니즘은 일반적으로 `reflection`을 이용하기 때문에 package-private 으로 만들어도 인스턴스 생성이 가능  
> 이 원리로 클래스패스 스캐닝을 통한 애플리케이션 조립도 가능

**package-private 단점**

- 클래스가 특정 개수를 넘어가면 혼란스러워 지고, 이를 해결하기 위해 하위 패키지를 만들게 되면 다른 패키지로 취급되기 때문에 접근이 제한된다.

> 따라서, 클래스 수가 적을 때 효과적이다!

## 10-3. 컴파일 후 체크

의존성 규칙의 위반 여부를 체크하기 위해 코드가 **컴파일된 후**에 **런타임에서 체크**하는 방법

자바용 도구로는 ArchUnit (의존성 방향이 기대한 대로 잘 설정되 있는지 체크할 수 있는 API 제공)

> 런타임 체크는 CI 환경에서 자동화된 테스트 과정에서 가장 잘 동작한다!

```java
class DependencyRuleTests {
    @Test
    void domainLayerDoesNotDependOnApplicationLayer() {
        noClasses()
                .that()
                .resideInAPackage("buckpal.domain..")
                .should()
                .dependOnClassesThat()
                .resideInAnyPackage("buckpal.application..")
                .check(new ClassFileImporter()
                        .importPackages("buckpal.."));
    }
		
    @Test
    void validateRegistrationContextArchitecture() {
        HexagonalArchitecture.boundedContext("account")
                .withDomainLayer("domain")
                .withAdaptersLayer("adapter")
                .incoming("web")
                .outgoing("persistence")
                .and()
                .withApplicationLayer("application")
                .service("service")
                .incomingPorts("port.in")
                .outgoingPorts("port.out")
                .and()
                .withConfiguration("configuration")
                .check(new ClassFileImporter()
                        .importPackages("buckpal.."));
    }
}
```

ArchUnit API를 이용한 헥사고날 아키텍처 내에 관련된 모든 패키지를 명시할 수 있는 일종의 DSL을 만들 수 있음.

**단점**

- 실패에 안전하지 않다
    - 패키지명 오타가 있다면 검증 불가
- 코드 리팩토링시 함께 유지보수해야 한다

> 이런한 단점을 보완하기 위해 의존성 테스트 코드를 작성해야한다.

---

## 10-4. 빌드 아티팩트

> 자바에는 Maven과 Gradle이 대표적이다.

- 의존성을 해결하기 위함(코드베이스가 의존하고 있는 모든 아티팩트가 사용 가능한지 확인)
    - 없다면 아티팩트 repository에서 다운로드 시도 후 실패하면 컴파일 에러 발생
- 계층간 유효성 검사를 위한 활용
    - 각 모듈의 빌드 스크립트에서 **아키텍처에서 허용하는 의존성을 지정**
    - 존재하지 않는 클래스는 에러 발생(잘못된 의존성을 만들 수 없음)

![https://user-images.githubusercontent.com/16996054/159099995-a26116ed-6df1-449f-a7a0-e0ecd524fd6b.png](https://user-images.githubusercontent.com/16996054/159099995-a26116ed-6df1-449f-a7a0-e0ecd524fd6b.png)

⬆️ 출처 : [Get Your Hands Dirty on Clean Architecture](https://subscription.packtpub.com/book/web_development/9781839211966/2/ch02lvl1sec11/hexagonal-architecture)

**장점(패키지 구분보다 좋은 점)**

- 순환 의존성을 허용하지 않음
- 모듈간 의존성 제어 용이
- 특정 모듈의 코드를 격리한 채로 변경 가능
- 의존성이 스크립트에 명시적으로 선언(의식적인 행동)

**단점**

- 빌드 스크립트의 유지보수 비용
    - 빌드 모듈을 나누기 전에 아키텍처가 어느 정도 안정된 상태를 제공
- 모듈간 매핑을 더 많이 수행해야 함
    - 대신, 한 모듈에 대한 의존성이 줄어든다는 장점도 있음

## 10-5. 정리

- 의존성이 올바른 방향을 가리키고 았는지 지속적으로 확인이 필요하다.
- package-private 접근 제한자를 이용해서 의도치 않은 의존성을 피한다.
- 하나의 모듈 안에서 아키텍처 경계를 강제해야하고, 패키지 구조상 package-private 접근 제한자를 사용할 수 없다면, ArchUnit 같은 컴파일 후 체크 도구를 이용한다.

> 아키텍처 경계를 강화하고 시간이 지나도 유지보수하기 좋은 코드를 만들기 위해 세 가지 접근 방식 모두 함께 조합해서 사용할 수 있다.

**Reference**
- [만들면서 배우는 클린 아키텍처 - 자바 코드로 구현하는 클린 웹 애플리케이션](http://www.kyobobook.co.kr/product/detailViewKor.laf?ejkGb=KOR&mallGb=KOR&barcode=9791158392758)