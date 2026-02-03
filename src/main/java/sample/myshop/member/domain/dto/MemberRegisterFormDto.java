package sample.myshop.member.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MemberRegisterFormDto {
    private String loginId;
    private String password;
    private String name;
    private String phone;
    private String zipcode;
    private String baseAddress;
    private String detailAddress;
}
