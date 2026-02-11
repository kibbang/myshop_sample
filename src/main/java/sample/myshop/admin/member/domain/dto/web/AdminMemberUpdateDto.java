package sample.myshop.admin.member.domain.dto.web;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AdminMemberUpdateDto {
    private Long id;
    private String name;
    private String phone;
    private String zipCode;
    private String baseAddress;
    private String detailAddress;
    private Boolean active;
}
