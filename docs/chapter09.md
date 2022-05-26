# 9. 애플리케이션 조립하기

유스케이스, 웹 어댑터, 영속성 어댑터를 조합해보자.

애플리케이션이 시작될 때 클래스를 인스턴스화하고 묶기 위해 **의존성 주입 메커니즘**을 이용한다.

> 자바, 스프링, 스프링 부트에서 각각 어떻게 DI(Dependency Injection)를 적용할까?

## 9-1. 왜 조립까지 신경 써야 할까?

유스케이스와 각 어댑터를 그냥 필요할 때 인스턴스화 하면 안될까?

- 코드의 의존성이 올바른 방향을 가리키게 하기 위해서
- 올바른 방향 : 모든 의존성은 안쪽으로, 애플리케이션의 도메인 코드 방향으로 향함
    - `장점1` 도메인 코드가 바깥 계층의 변경으로부터 안전하다
    - `장점2` 테스트하기 쉽다 (mocking, 단위테스트)

객체 인스턴스를 생성할 책임

> 객체 인스턴스를 생성할 책임은 누구에게 있을까?  
> 그리고 어떻게 의존성 규칙을 어기지 않으면서 그렇게 할 수 있을까?

- 아키텍처에 대해 중립적이고 인스턴스 생성을 위해 모든 클래스에 대한 의존성을 가지는 설정 컴포넌트(configuration component)가 있어야 한다
- 중립적인 설정 컴포넌트는 인스턴스 생성을 위해 모든 클래스에 접근할 수 있다!

**설정 컴포넌트의 역할**

- 웹 어댑터 인스턴스 생성
- HTTP 요청이 실제로 웹 어댑터로 전달되도록 보장
- 유스케이스 인스턴스 생성
- 웹 어댑터에 유스케이스 인스턴스 제공
- 영속성 어댑터 인스턴스 생성
- 유스케이스에 영속성 어댑터 인스턴스 제공
- 영속성 어댑터가 실제로 데이터베이스에 접근할 수 있도록 보장

## 9-2. 평범한 코드로 조립하기

```java
package com.book.cleanarchitecture.buckpal;
import com.book.cleanarchitecture.buckpal.account.adapter.in.web.SendMoneyController;
import com.book.cleanarchitecture.buckpal.account.adapter.out.persistence.AccountPersistenceAdapter;
import com.book.cleanarchitecture.buckpal.account.application.port.in.SendMoneyUseCase;
import com.book.cleanarchitecture.buckpal.account.application.service.SendMoneyService;

public class Application {
    public static void main(String[] args) {
        AccountRepository accountRepository = new AccountRepository();
        ActivityRepository activityRepository = new ActivityRepository();
        AccountPersistenceAdapter accountPersistenceAdapter = new AccountPersistenceAdapter(accountRepository, activityRepository);
        SendMoneyUseCase sendMoneyUseCase = new SendMoneyService(
                accountPersistenceAdapter,
                accountPersistenceAdapter
        );
        SendMoneyController sendMoneyController = new SendMoneyController(sendMoneyUseCase);
        
        startProcessingWebRequests(sendMoneyController);
    }
}
```

1. 웹 컨트롤러, 유스케이스, 영속성 어댑터가 단 하나씩만 있는 애플리케이션
2. 각 class 가 속한 package 외부에서 인스턴스를 생성하기 때문에 이 클래스들은 전부 `public`
3. Spring 프레임워크는 package-private 의존성을 유지하면서 이처럼 지저분한 작업을 `DI` 라고 하며 대신해준다.

## 9-3. 스프링의 클래스패스 스캐닝으로 조립하기

- 스프링 프레임워크를 이용해서 애플리케이션을 조립한 결과물을 `애플리케이션 컨텍스트`라고 한다.
    - 애플리케이션 컨텍스트는 애플리케이션을 구성하는 모든 객체(자바 용어로는 빈(`bean`))을 포함
- 스프링은 `클래스패스 스캐닝`으로 클래스패스에서 접근 가능한 모든 클래스를 확인해서 `@Component` 애너테이션이 붙은 클래스를 찾는다.
- 스프링이 인식할 수 있는 **커스텀 애노테이션**도 생성 가능하다.

```java
package com.book.cleanarchitecture.buckpal.shared;
import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Component;
import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface UseCase {
    @AliasFor(annotation = Component.class)
    String value() default "";
}
```

- 메타 애너테이션으로 `@Component` 를 포함하고 있어서 스프링이 클래스패스 스캐닝을 할 때 인스턴스를 생성할 수 있다.

**단점**

1. 클래스에 프레임워크에 특화된 애너테이션을 붙어야 한다는 점에서 **침투적**이다.
2. **마법 같은 일**이 일어날 수 있다.
    - 이 마법은 학습하지 않으면 이해하기 어려울 수 있다.(디버깅이 어렵다)

## 9-4. **스프링의 자바 컨피그로 조립하기**

- `@Configuration` 을 통해 스캐닝을 가지고 찾아야 하는 설정 클래스임을 표시
- 이건 모든 빈을 찾아오는건 아니기 때문에, 마법이 일어날 일은 적다.
- 설정 클래스와 같은 패키지에 넣어놓지 않는 경우에 `public`으로 해야 한다.
- 패키지를 모듈 경계로 사용하고 각 패키지 안에 전용 클래스를 만들 수 있지만, 하위 패키지를 사용할 수 없다.

**Reference**

• [만들면서 배우는 클린 아키텍처 - 자바 코드로 구현하는 클린 웹 애플리케이션](http://www.kyobobook.co.kr/product/detailViewKor.laf?ejkGb=KOR&mallGb=KOR&barcode=9791158392758)