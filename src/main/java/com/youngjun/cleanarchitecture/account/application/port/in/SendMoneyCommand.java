package com.youngjun.cleanarchitecture.account.application.port.in;

import static java.util.Objects.requireNonNull;

import com.youngjun.cleanarchitecture.account.domain.Account.AccountId;
import com.youngjun.cleanarchitecture.account.domain.Money;

public class SendMoneyCommand {

    private final AccountId sourceAccountId;
    private final AccountId targetAccountId;
    private final Money money;

    public SendMoneyCommand(
        AccountId sourceAccountId,
        AccountId targetAccountId,
        Money money) {
        this.sourceAccountId = sourceAccountId;
        this.targetAccountId = targetAccountId;
        this.money = money;
        requireNonNull(sourceAccountId);
        requireNonNull(targetAccountId);
        requireNonNull(money);
        requireGraterThan(money, 0);
    }
}
