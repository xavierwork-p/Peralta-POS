package com.peraltapos.sales.sale;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public interface SaleRepository extends JpaRepository<Sale, UUID> {

    List<Sale> findByCustomerNameContainingIgnoreCaseOrInvoiceNumberContainingIgnoreCase(String customerName, String invoiceNumber, Sort sort);

    @Query("select coalesce(sum(s.total), 0) from Sale s where s.issuedAt >= :start and s.issuedAt < :end and s.status = :status")
    BigDecimal sumSalesBetweenByStatus(@Param("start") OffsetDateTime start, @Param("end") OffsetDateTime end, @Param("status") SaleStatus status);
}
