package com.peraltapos.dashboard;

import com.peraltapos.accounting.receivable.AccountReceivableRepository;
import com.peraltapos.accounting.receivable.AccountReceivableStatus;
import com.peraltapos.catalog.product.ProductRepository;
import com.peraltapos.catalog.product.ProductService;
import com.peraltapos.crm.customer.CustomerRepository;
import com.peraltapos.hr.employee.EmployeeRepository;
import com.peraltapos.sales.quote.QuoteRepository;
import com.peraltapos.sales.sale.SaleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DashboardService {

    private final ProductRepository productRepository;
    private final ProductService productService;
    private final CustomerRepository customerRepository;
    private final EmployeeRepository employeeRepository;
    private final QuoteRepository quoteRepository;
    private final SaleService saleService;
    private final AccountReceivableRepository accountReceivableRepository;

    public DashboardService(
            ProductRepository productRepository,
            ProductService productService,
            CustomerRepository customerRepository,
            EmployeeRepository employeeRepository,
            QuoteRepository quoteRepository,
            SaleService saleService,
            AccountReceivableRepository accountReceivableRepository
    ) {
        this.productRepository = productRepository;
        this.productService = productService;
        this.customerRepository = customerRepository;
        this.employeeRepository = employeeRepository;
        this.quoteRepository = quoteRepository;
        this.saleService = saleService;
        this.accountReceivableRepository = accountReceivableRepository;
    }

    @Transactional(readOnly = true)
    public DashboardSummaryResponse summary() {
        return new DashboardSummaryResponse(
                productRepository.count(),
                productService.lowStockCount(),
                customerRepository.count(),
                employeeRepository.count(),
                quoteRepository.count(),
                productService.inventoryCostValue(),
                saleService.salesToday(),
                accountReceivableRepository.sumBalanceByStatusIn(List.of(
                        AccountReceivableStatus.PENDING,
                        AccountReceivableStatus.OVERDUE
                ))
        );
    }
}
