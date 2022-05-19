package com.youngjun.cleanarchitecture.account.domain;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Value;

@Value
@RequiredArgsConstructor
public class Activity {

    @Getter
    private ActivityId id; // 활동이력 index

    @Getter
    @NonNull
    private final Account.AccountId ownerAccountId; // 소유자 계좌 id

    @Getter
    @NonNull
    private final Account.AccountId sourceAccountId; // 활동 주체 계좌 id

    @Getter
    @NonNull
    private final Account.AccountId targetAccountId; // 상대방 계좌 id

    @Getter
    @NonNull
    private final LocalDateTime timestamp; // 계좌 활동 이력 일자

    @Getter
    @NonNull
    private final Money money; // 돈 객체 (biginteger)

    public Activity(
            @NonNull Account.AccountId ownerAccountId,
            @NonNull Account.AccountId sourceAccountId,
            @NonNull Account.AccountId targetAccountId,
            @NonNull LocalDateTime timestamp,
            @NonNull Money money) {
        this.id = null;
        this.ownerAccountId = ownerAccountId;
        this.sourceAccountId = sourceAccountId;
        this.targetAccountId = targetAccountId;
        this.timestamp = timestamp;
        this.money = money;
    }

    @Value
    public static class ActivityId {
        private final Long value;
    }

}