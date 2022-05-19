package com.youngjun.cleanarchitecture.account.application.port.in;

import static java.util.Objects.requireNonNull;

import com.youngjun.cleanarchitecture.account.domain.Account.AccountId;
import com.youngjun.cleanarchitecture.account.domain.Money;

/**
 * 입력 모델
 */
public class SendMoneyCommand {

    private final AccountId sourceAccountId;
    private final AccountId targetAccountId;
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
        requireNonNull(sourceAccountId);
        requireNonNull(targetAccountId);
        requireNonNull(money);
        requireGraterThan(money, 0);
    }
}
