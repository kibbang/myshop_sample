package sample.myshop.admin.order.domain.dto.web;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import sample.myshop.common.entity.PaginatorEntity;
import sample.myshop.enums.order.OrderStatus;

import java.time.LocalDate;

@Getter
@Setter
public class OrderSearchConditionDto extends PaginatorEntity {
    private  String keyword; // like
    private  OrderStatus status;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate fromDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate toDate;

    public OrderSearchConditionDto() {
        this.fromDate = LocalDate.now().minusMonths(1);
        this.toDate = LocalDate.now();
    }
}
