package com.youngjun.cleanarchitecture.account.application.web;

import com.youngjun.cleanarchitecture.account.application.port.in.SendMoneyCommand;
import com.youngjun.cleanarchitecture.account.application.port.in.SendMoneyUseCase;
import com.youngjun.cleanarchitecture.account.domain.Money;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.youngjun.cleanarchitecture.account.domain.Account.*;

@RestController
@RequiredArgsConstructor
public class SendMoneyController {

    private final SendMoneyUseCase sendMoneyUseCase;

    @PostMapping(path = "/accounts/send/{sourceAccountId}/{targetAccountId}/{amount}")
    void sendMoney(@PathVariable("sourceAccountId") Long sourceAccountId,
                    @PathVariable("targetAccountId") Long targetAccountId,
                    @PathVariable("amount") Long amount) {
        SendMoneyCommand command = new SendMoneyCommand(
                new AccountId(sourceAccountId),
                new AccountId(targetAccountId),
                Money.of(amount));

        sendMoneyUseCase.sendMoney(command);
    }

}
