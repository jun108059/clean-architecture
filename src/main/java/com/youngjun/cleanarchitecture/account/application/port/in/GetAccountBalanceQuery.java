package com.youngjun.cleanarchitecture.account.application.port.in;

import com.youngjun.cleanarchitecture.account.domain.Account;
import com.youngjun.cleanarchitecture.account.domain.Money;

public interface GetAccountBalanceQuery {
    Money getAccountBalance(Account.AccountId accountId);
}
