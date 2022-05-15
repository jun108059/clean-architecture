package com.youngjun.cleanarchitecture.adapter.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

interface AccountRepository extends JpaRepository<AccountJpaEntity, Long> {
}
