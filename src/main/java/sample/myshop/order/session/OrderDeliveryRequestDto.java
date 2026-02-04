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
    private String receiverZipcode;
    @NotBlank
    private String receiverBaseAddress;
    @NotBlank
    private String receiverDetailAddress;
    @NotBlank
    private String deliveryMemo;
}
