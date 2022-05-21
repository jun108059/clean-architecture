package com.youngjun.cleanarchitecture.account.application.port.out;


import com.youngjun.cleanarchitecture.account.domain.Account.AccountId;

public interface AccountLock {

    void lockAccount(AccountId accountId);

    void releaseAccount(AccountId accountId);

}