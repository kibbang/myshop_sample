package sample.myshop.admin.member.domain.dto.web;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

@Getter
@Setter
@AllArgsConstructor
public class AdminMemberUpdateRequestDto {
    @NotBlank
    private String name;
    @NotBlank
    private String phone;
    @NotBlank
    private String zipCode;
    @NotBlank
    private String baseAddress;
    @NotBlank
    private String detailAddress;
    @NotNull
    private Boolean active;
}
