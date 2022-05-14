package com.youngjun.cleanarchitecture.account.application.service;

import com.youngjun.cleanarchitecture.account.application.port.in.GetAccountBalanceQuery;
import com.youngjun.cleanarchitecture.account.application.port.out.LoadAccountPort;
import com.youngjun.cleanarchitecture.account.domain.Money;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

import static com.youngjun.cleanarchitecture.account.domain.Account.*;

@RequiredArgsConstructor
public class GetAccountBalanceService implements GetAccountBalanceQuery {

    private final LoadAccountPort loadAccountPort;

    @Override
    public Money getAccountBalance(AccountId accountId) {
        return loadAccountPort.loadAccount(accountId, LocalDateTime.now())
                .calculateBalance();
    }
}
