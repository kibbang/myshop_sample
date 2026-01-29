package sample.myshop.admin.product.domain.dto.web;

import jakarta.annotation.Nullable;
import lombok.Getter;
import sample.myshop.enums.product.SaleStatus;
import sample.myshop.common.entity.PaginatorEntity;

@Getter
public class ProductSearchConditionDto extends PaginatorEntity {
    private final String keyword; // like
    private final SaleStatus status;

    private ProductSearchConditionDto(@Nullable String keyword, @Nullable SaleStatus status) {
        this.keyword = keyword;
        this.status = status;
    }

    /**
     * 생성 메소드
     * @param keyword
     * @param status
     * @return
     */
    public static ProductSearchConditionDto of(String keyword, SaleStatus status) {
        return new ProductSearchConditionDto(keyword, status);
    }
}
