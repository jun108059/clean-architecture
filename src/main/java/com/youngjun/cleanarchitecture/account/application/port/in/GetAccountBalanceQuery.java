package com.youngjun.cleanarchitecture.account.application.port.in;

import com.youngjun.cleanarchitecture.account.domain.Account;
import com.youngjun.cleanarchitecture.account.domain.Money;

/**
 * 쿼리를 위한 인커밍 전용 포트
 */
public interface GetAccountBalanceQuery {
    Money getAccountBalance(Account.AccountId accountId);
}
