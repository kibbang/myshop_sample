package sample.myshop.member.domain.dto;

import jakarta.validation.constraints.NotBlank;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MemberRegisterFormDto {
    @NotBlank
    @Size(max = 50)
    private String loginId;
    @NotBlank
    @Size(min = 8, max = 16)
    private String password;
    @NotBlank
    private String name;
    @NotBlank
    private String phone;
    @NotBlank
    private String zipcode;
    @NotBlank
    private String baseAddress;
    @NotBlank
    private String detailAddress;
}
