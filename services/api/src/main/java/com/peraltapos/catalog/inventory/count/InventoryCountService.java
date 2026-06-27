package com.peraltapos.catalog.inventory.count;

import com.peraltapos.catalog.inventory.InventoryMovementService;
import com.peraltapos.catalog.product.Product;
import com.peraltapos.catalog.product.ProductRepository;
import com.peraltapos.common.web.BusinessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class InventoryCountService {

    private static final DateTimeFormatter COUNT_NUMBER_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");

    private final InventoryCountRepository inventoryCountRepository;
    private final ProductRepository productRepository;
    private final InventoryMovementService inventoryMovementService;

    public InventoryCountService(
            InventoryCountRepository inventoryCountRepository,
            ProductRepository productRepository,
            InventoryMovementService inventoryMovementService
    ) {
        this.inventoryCountRepository = inventoryCountRepository;
        this.productRepository = productRepository;
        this.inventoryMovementService = inventoryMovementService;
    }

    @Transactional(readOnly = true)
    public List<InventoryCountResponse> list() {
        return inventoryCountRepository.findTop20ByOrderByCountedAtDesc()
                .stream()
                .map(InventoryCountResponse::from)
                .toList();
    }

    @Transactional
    public InventoryCountResponse create(InventoryCountRequest request) {
        Set<UUID> productIds = new HashSet<>();
        List<InventoryCountItem> items = new ArrayList<>();

        for (InventoryCountItemRequest itemRequest : request.items()) {
            if (!productIds.add(itemRequest.productId())) {
                throw new BusinessException("Un producto no puede repetirse en el mismo conteo");
            }

            Product product = productRepository.findById(itemRequest.productId())
                    .orElseThrow(() -> new BusinessException("Producto no encontrado"));

            if (!product.isActive()) {
                throw new BusinessException("El producto " + product.getName() + " esta inactivo");
            }

            items.add(InventoryCountItem.from(product, itemRequest.countedStock()));
        }

        String countNumber = generateCountNumber();
        InventoryCount count = new InventoryCount();
        count.post(countNumber, request.notes(), items);
        InventoryCount savedCount = inventoryCountRepository.save(count);

        for (InventoryCountItem item : savedCount.getItems()) {
            applyDifference(item.getProduct(), item.getDifference());
            inventoryMovementService.recordCountAdjustment(
                    item.getProduct(),
                    item.getDifference(),
                    countNumber
            );
        }

        return InventoryCountResponse.from(savedCount);
    }

    private void applyDifference(Product product, BigDecimal difference) {
        if (difference.signum() > 0) {
            product.increaseStock(difference);
        } else if (difference.signum() < 0) {
            product.decreaseStock(difference.abs());
        }
    }

    private String generateCountNumber() {
        return "CNT-"
                + LocalDateTime.now().format(COUNT_NUMBER_FORMAT)
                + "-"
                + UUID.randomUUID().toString().substring(0, 4).toUpperCase();
    }
}
