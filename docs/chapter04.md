# 4장. 유스케이스 구현하기

> 헥사고날 아키텍처는 유스케이스를 어떻게 구현할까

각 계층이 아주 느슨하게 결합돼 있기 때문에 필요한 도메인 코드를 자유롭게 모델링 가능

**도메인 코드 모델링 예시**
- DDD
- rich 도메인 모델
- anemic 도메인 모델
- 이 외 우리만의 방식

헥사고날 아키텍처는 **도메인 중심** 의 아키텍처에 적합

> 도메인 엔티티 생성 → 도메인 엔티티 중심으로 유스케이스 구현

## 1. 도메인 모델 구현하기

### 1-1. 유스케이스

> (1) 내 계좌 → 다른 계좌 송금

- Account 엔티티 생성
- 출금 계좌에서 돈 출금
- 입금 계좌로 입금

```java
package com.youngjun.cleanarchitecture.account.domain;

public class Account {
    // 생성자, Getter 생략
    private final AccountId id;
    private final Money baselineBalance;
    private final ActivityWindow activityWindow;

    public Money calculateBalance() {
        return Money.add(
            this.baselineBalance,
            this.activityWindow.calculateBalance(this.id));
    }

    public boolean withdraw(Money money, AccountId targetAccountId) {
        if (!mayWithdraw(money)) {
            return false;
        }

        Activity withdrawal = new Activity(
            this.id,
            this.id,
            targetAccountId,
            LocalDateTime.now(),
            money);
        this.activityWindow.addActivity(withdrawal);
        return true;
    }

    private boolean mayWithdraw(Money money) {
        return Money.add(
                this.calculateBalance(),
                money.negate())
            .isPositiveOrZero();
    }

    public boolean deposit(Money money, AccountId sourceAccountId) {
        Activity deposit = new Activity(
            this.id,
            sourceAccountId,
            this.id,
            LocalDateTime.now(),
            money);
        this.activityWindow.addActivity(deposit);
        return true;
    }
}
```

- Account 엔티티는 실제 계좌의 현재 Snapshot 제공
- 계좌에 대한 모든 입금과 출금은 Activity 엔티티에 Snapshot 된다
  - 한 계좌에 대한 모든 활동(activity)들을 항상 메모리에 한꺼번에 올리는 것은 비효율적임
  - 따라서, Account 엔티티는 ActivityWindow 값 객체(Value Object)에서 일정 기간의 Snapshot만 보유한다

**Reference**
- [만들면서 배우는 클린 아키텍처 - 자바 코드로 구현하는 클린 웹 애플리케이션](http://www.kyobobook.co.kr/product/detailViewKor.laf?ejkGb=KOR&mallGb=KOR&barcode=9791158392758)