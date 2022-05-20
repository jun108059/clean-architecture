package com.youngjun.cleanarchitecture.account.application.port.in;

import static java.util.Objects.requireNonNull;

import com.youngjun.cleanarchitecture.account.domain.Account.AccountId;
import com.youngjun.cleanarchitecture.account.domain.Money;
import common.SelfValidation;

import javax.validation.constraints.NotNull;

/**
 * 입력 모델
 */
public class SendMoneyCommand extends SelfValidation<SendMoneyCommand> {

    @NotNull
    private final AccountId sourceAccountId;
    @NotNull
    private final AccountId targetAccountId;
    @NotNull
    private final Money money;

    /**
     * 송금 입력 모델 생성자
     *
     * @param sourceAccountId 출금 계좌 ID
     * @param targetAccountId 입금 계좌 ID
     * @param money           송금할 금액
     */
    public SendMoneyCommand(AccountId sourceAccountId,
                             AccountId targetAccountId,
                             Money money) {
        this.sourceAccountId = sourceAccountId;
        this.targetAccountId = targetAccountId;
        this.money = money;
        // 생성자에서 입력 유효성 검증
        requireGraterThan(money, 0); // TODO SelfValidation 에서 구현
        this.validateSelf();
    }
}
