package com.peraltapos.sales.quote;

import com.peraltapos.catalog.product.Product;
import com.peraltapos.catalog.product.ProductRepository;
import com.peraltapos.common.web.BusinessException;
import com.peraltapos.sales.sale.Sale;
import com.peraltapos.sales.sale.SaleResponse;
import com.peraltapos.sales.sale.SaleService;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
public class QuoteService {

    private static final DateTimeFormatter QUOTE_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");

    private final QuoteRepository quoteRepository;
    private final ProductRepository productRepository;
    private final SaleService saleService;

    public QuoteService(QuoteRepository quoteRepository, ProductRepository productRepository, SaleService saleService) {
        this.quoteRepository = quoteRepository;
        this.productRepository = productRepository;
        this.saleService = saleService;
    }

    @Transactional(readOnly = true)
    public List<QuoteResponse> list(String search) {
        Sort sort = Sort.by("issueDate").descending().and(Sort.by("quoteNumber").descending());
        List<Quote> quotes = (search == null || search.isBlank())
                ? quoteRepository.findAll(sort)
                : quoteRepository.findByCustomerNameContainingIgnoreCaseOrQuoteNumberContainingIgnoreCase(search, search, sort);

        return quotes.stream().map(QuoteResponse::from).toList();
    }

    @Transactional
    public QuoteResponse create(QuoteRequest request) {
        validateDates(request);

        Quote quote = new Quote();
        quote.updateHeader(generateQuoteNumber(request.issueDate()), request);
        quote.replaceItems(request.items().stream().map(this::quoteItemFrom).toList());

        return QuoteResponse.from(quoteRepository.save(quote));
    }

    @Transactional
    public QuoteResponse update(UUID id, QuoteRequest request) {
        validateDates(request);

        Quote quote = findEntity(id);
        quote.updateHeader(quote.getQuoteNumber(), request);
        quote.replaceItems(request.items().stream().map(this::quoteItemFrom).toList());

        return QuoteResponse.from(quoteRepository.save(quote));
    }

    @Transactional
    public SaleResponse invoice(UUID id) {
        Quote quote = findEntity(id);
        if (quote.getConvertedSale() != null || quote.getStatus() == QuoteStatus.CONVERTED) {
            throw new BusinessException("Esta cotizacion ya fue facturada");
        }

        Sale sale = saleService.createFromApprovedQuote(quote);
        quote.markConverted(sale);
        return SaleResponse.from(sale);
    }

    @Transactional(readOnly = true)
    public QuoteResponse get(UUID id) {
        return QuoteResponse.from(findEntity(id));
    }

    private String generateQuoteNumber(LocalDate issueDate) {
        // Generador simple para la fase inicial. En produccion esto se reemplaza
        // por una tabla de secuencias por sucursal/caja para evitar duplicados.
        long next = quoteRepository.count() + 1;
        return "COT-" + issueDate.format(QUOTE_DATE_FORMAT) + "-" + String.format("%05d", next);
    }

    private void validateDates(QuoteRequest request) {
        if (request.validUntil().isBefore(request.issueDate())) {
            throw new BusinessException("La fecha de validez no puede ser menor que la fecha de emision");
        }
    }

    private QuoteItem quoteItemFrom(QuoteItemRequest request) {
        QuoteItem item = QuoteItem.from(request);
        if (request.productId() != null) {
            Product product = productRepository.findById(request.productId())
                    .orElseThrow(() -> new BusinessException("Producto no encontrado en la cotizacion"));
            item.setProduct(product);
        }
        return item;
    }

    private Quote findEntity(UUID id) {
        return quoteRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Cotizacion no encontrada"));
    }
}
