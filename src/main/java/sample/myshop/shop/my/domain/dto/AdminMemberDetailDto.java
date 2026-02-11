package sample.myshop.shop.my.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import sample.myshop.member.enums.Role;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class AdminMemberDetailDto {
    private Long id;

    private String loginId;

    private String name;

    private String phone;

    private String zipCode;

    private String baseAddress;

    private String detailAddress;

    private Role role;

    private Boolean active;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
