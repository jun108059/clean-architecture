package com.youngjun.cleanarchitecture.account.application.port.out;

import com.youngjun.cleanarchitecture.account.domain.Account;

public interface UpdateAccountStatePort {

    void updateActivities(Account account);

}
