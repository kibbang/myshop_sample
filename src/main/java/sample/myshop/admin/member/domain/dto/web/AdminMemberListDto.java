package sample.myshop.admin.member.domain.dto.web;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import sample.myshop.member.enums.Role;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class AdminMemberListDto {
    private Long id;
    private String loginId;
    private String name;
    private String phone;
    private Role role;
    private boolean isActive;
    private LocalDateTime createdAt;
}
