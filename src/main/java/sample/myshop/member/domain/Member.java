package sample.myshop.member.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sample.myshop.common.entity.CommonEntity;
import sample.myshop.member.enums.Role;

import static jakarta.persistence.EnumType.*;
import static jakarta.persistence.GenerationType.*;

@Entity
@Getter
@Table(name = "mem_members")
@NoArgsConstructor
public class Member extends CommonEntity {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(unique = true, name = "login_id")
    private String loginId;
    @Column(name = "password")
    private String password;
    private String name;
    private String phone;
    @Embedded
    private Address address;

    @Enumerated(STRING)
    private Role role;
    private boolean isActive;


    private Member(String loginId, String password, String name, String phone, Address address, Role role) {
        this.loginId = loginId;
        this.password = password;
        this.name = name;
        this.phone = phone;
        this.address = address;
        this.role = role;
        this.isActive = true;
    }

    public static Member createMember(String loginId, String password, String name, String phone, Address address, Role role) {
        return new Member(loginId, password, name, phone, address, role);
    }

    public void changeActiveStatus(boolean isActive) {
        this.isActive = isActive;
    }

    public void changeDefaultInfo(String name, String phone) {
        this.name = name;
        this.phone = phone;
    }
}
