package com.peraltapos.catalog.inventory;

import com.peraltapos.catalog.product.Product;
import com.peraltapos.catalog.product.ProductRepository;
import com.peraltapos.common.web.BusinessException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

@Service
public class InventoryMovementService {

    private final InventoryMovementRepository inventoryMovementRepository;
    private final ProductRepository productRepository;

    public InventoryMovementService(InventoryMovementRepository inventoryMovementRepository, ProductRepository productRepository) {
        this.inventoryMovementRepository = inventoryMovementRepository;
        this.productRepository = productRepository;
    }

    @Transactional(readOnly = true)
    public List<InventoryMovementResponse> list(
            String search,
            InventoryMovementType movementType,
            LocalDate dateFrom,
            LocalDate dateTo
    ) {
        Specification<InventoryMovement> specification = Specification.where(null);

        if (search != null && !search.isBlank()) {
            String pattern = "%" + search.trim().toLowerCase() + "%";
            specification = specification.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.or(
                            criteriaBuilder.like(criteriaBuilder.lower(root.get("product").get("name")), pattern),
                            criteriaBuilder.like(criteriaBuilder.lower(root.get("product").get("sku")), pattern),
                            criteriaBuilder.like(criteriaBuilder.lower(root.get("reference")), pattern)
                    )
            );
        }

        if (movementType != null) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("movementType"), movementType)
            );
        }

        if (dateFrom != null) {
            OffsetDateTime from = dateFrom.atStartOfDay().atOffset(OffsetDateTime.now().getOffset());
            specification = specification.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), from)
            );
        }

        if (dateTo != null) {
            OffsetDateTime until = dateTo.plusDays(1).atStartOfDay().atOffset(OffsetDateTime.now().getOffset());
            specification = specification.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.lessThan(root.get("createdAt"), until)
            );
        }

        List<InventoryMovement> movements = inventoryMovementRepository.findAll(
                specification,
                PageRequest.of(0, 200, Sort.by("createdAt").descending())
        ).getContent();

        return movements.stream().map(InventoryMovementResponse::from).toList();
    }

    @Transactional
    public InventoryMovementResponse create(InventoryMovementRequest request) {
        Product product = findProduct(request.productId());
        applyStockChange(product, request.movementType(), request.quantity());

        InventoryMovement movement = InventoryMovement.create(
                product,
                request.movementType(),
                request.quantity(),
                request.unitCost(),
                cleanText(request.reference()),
                cleanText(request.notes())
        );

        return InventoryMovementResponse.from(inventoryMovementRepository.save(movement));
    }

    @Transactional
    public void recordSale(Product product, BigDecimal quantity, String invoiceNumber) {
        InventoryMovement movement = InventoryMovement.create(
                product,
                InventoryMovementType.SALE,
                quantity,
                product.getCostPrice(),
                invoiceNumber,
                "Salida automatica por venta"
        );
        inventoryMovementRepository.save(movement);
    }

    @Transactional
    public void recordPurchase(
            Product product,
            BigDecimal quantity,
            BigDecimal unitCost,
            String documentNumber
    ) {
        InventoryMovement movement = InventoryMovement.create(
                product,
                InventoryMovementType.PURCHASE,
                quantity,
                unitCost,
                documentNumber,
                "Entrada por factura de suplidor"
        );
        inventoryMovementRepository.save(movement);
    }

    @Transactional
    public void recordCountAdjustment(
            Product product,
            BigDecimal difference,
            String countNumber
    ) {
        if (difference.signum() == 0) {
            return;
        }

        InventoryMovement movement = InventoryMovement.create(
                product,
                difference.signum() > 0
                        ? InventoryMovementType.ADJUSTMENT_IN
                        : InventoryMovementType.ADJUSTMENT_OUT,
                difference.abs(),
                product.getCostPrice(),
                countNumber,
                "Ajuste automatico por conteo fisico"
        );
        inventoryMovementRepository.save(movement);
    }

    private void applyStockChange(Product product, InventoryMovementType movementType, BigDecimal quantity) {
        if (movementType == InventoryMovementType.PURCHASE
                || movementType == InventoryMovementType.ADJUSTMENT_IN
                || movementType == InventoryMovementType.RETURN) {
            product.increaseStock(quantity);
            return;
        }

        if (movementType == InventoryMovementType.ADJUSTMENT_OUT) {
            if (!product.hasStockFor(quantity)) {
                throw new BusinessException("Stock insuficiente para " + product.getName());
            }
            product.decreaseStock(quantity);
            return;
        }

        throw new BusinessException("Ese tipo de movimiento no se registra manualmente");
    }

    private Product findProduct(java.util.UUID id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Producto no encontrado"));
    }

    private String cleanText(String value) {
        String cleanValue = value == null ? null : value.trim();
        return cleanValue == null || cleanValue.isBlank() ? null : cleanValue;
    }
}
