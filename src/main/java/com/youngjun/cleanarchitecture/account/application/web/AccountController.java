package com.youngjun.cleanarchitecture.account.application.web;

import com.youngjun.cleanarchitecture.account.application.port.in.GetAccountBalanceQuery;
import com.youngjun.cleanarchitecture.account.application.port.in.SendMoneyUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
class AccountController {

    private final GetAccountBalanceQuery getAccountBalanceQuery;
    private final ListAccountsQuery listAccountsQuery;
    private final LoadAccountQuery loadAccountQuery;

    private final SendMoneyUseCase sendMoneyUseCase;
    private final CreateAccountUseCase createAccountUseCase;

    @GetMapping("/accounts")
    List<AccountResource> listAccounts() {
        // ...
    }

    @GetMapping("/accounts/id")
    AccountResource getAccount(@PathVariable("accountId") Long accountId) {
        // ...
    }

    @GetMapping("/accounts/{id}/balance")
    long getAccountBalance(@PathVariable("accountId") Long accountId) {
        // ...
    }

    @PostMapping("/accounts")
    AccountResource createAccount(@RequestBody AccountResource account) {
        // ...
    }

    @PostMapping("/accounts/send/{sourceAccountId}/{targetAccountId}/{amount}")
    void sendMoney(@PathVariable("sourceAccountId") Long sourceAccountId,
                   @PathVariable("targetAccountId") Long targetAccountId,
                   @PathVariable("amount") Long amount) {
        // ...
    }
}