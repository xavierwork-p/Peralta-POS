package com.peraltapos.sales.quote;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface QuoteRepository extends JpaRepository<Quote, UUID> {

    List<Quote> findByCustomerNameContainingIgnoreCaseOrQuoteNumberContainingIgnoreCase(String customerName, String quoteNumber, Sort sort);
}
