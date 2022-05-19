package com.youngjun.cleanarchitecture.account.domain;

import java.time.LocalDateTime;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Value;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Account {

    private final AccountId id;

    @Getter
    private final Money baselineBalance; // 잔고

    @Getter
    private final ActivityWindow activityWindow; // 계좌 활동 이력 List (SnapShot)

    public Money calculateBalance() {
        return Money.add(
                this.baselineBalance,
                this.activityWindow.calculateBalance(this.id));
    }
    // 생성자 1) Id 없이 생성
    public static Account withoutId(Money baselineBalance, ActivityWindow activityWindow) {
        return new Account(null, baselineBalance, activityWindow);
    }
    // 생성자 2) Id, 잔고, 계좌 활동 이력
    public static Account withId(AccountId accountId, Money baselineBalance,
                                    ActivityWindow activityWindow) {
        return new Account(accountId, baselineBalance, activityWindow);
    }
    // Id Getter(Optional Get)
    public Optional<AccountId> getId() {
        return Optional.ofNullable(this.id);
    }
    // 출금
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
    // 출금 가능한지 (비즈니스 규칙 검사)
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

    @Value
    public static class AccountId {
        private Long value;
    }
}