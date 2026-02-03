package sample.myshop.order.session;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderDeliveryRequestDto {
    @NotBlank
    private String receiverName;
    @NotBlank
    private String receiverPhone;
    @NotBlank
    private String zipcode;
    @NotBlank
    private String baseAddress;
    @NotBlank
    private String detailAddress;
    @NotBlank
    private String deliveryMemo;
}
