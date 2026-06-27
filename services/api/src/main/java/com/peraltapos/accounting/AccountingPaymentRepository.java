package com.peraltapos.accounting;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AccountingPaymentRepository extends JpaRepository<AccountingPayment, UUID> {

    List<AccountingPayment> findByReceivableId(UUID receivableId, Sort sort);

    List<AccountingPayment> findByPayableId(UUID payableId, Sort sort);
}
