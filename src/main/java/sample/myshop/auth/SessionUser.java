package sample.myshop.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import sample.myshop.member.enums.Role;

@Getter
@Setter
@AllArgsConstructor
public class SessionUser {
    private Long memberId;
    private String loginId;
    private Role role;
}
