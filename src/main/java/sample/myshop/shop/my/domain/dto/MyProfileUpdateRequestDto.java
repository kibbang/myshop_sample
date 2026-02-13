package sample.myshop.shop.my.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class MyProfileUpdateRequestDto {
    private String name;
    private String phone;
    private String zipCode;
    private String baseAddress;
    private String detailAddress;
}
