package sample.myshop.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sample.myshop.enums.auth.UserType;

@Getter
@Setter
@AllArgsConstructor
public class SessionUser {
    private String loginId;
    private UserType role;
}
