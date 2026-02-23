package sample.myshop.admin.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LowStockItemDto {
    private Long productId;
    private String productName;
    private String sku;
    private Integer stockQuantity;
}
