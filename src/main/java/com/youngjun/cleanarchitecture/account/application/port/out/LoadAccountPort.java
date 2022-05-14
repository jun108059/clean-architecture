package com.youngjun.cleanarchitecture.account.application.port.out;

import com.youngjun.cleanarchitecture.account.domain.Account;

import java.time.LocalDateTime;

import static com.youngjun.cleanarchitecture.account.domain.Account.*;

public interface LoadAccountPort {
    Account loadAccount(AccountId accountId, LocalDateTime baselineDate);
}
