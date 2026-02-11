package sample.myshop.admin.member.domain.dto.web;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sample.myshop.common.entity.PaginatorEntity;
import sample.myshop.member.enums.Role;

@Getter
@Setter
@NoArgsConstructor
public class AdminMemberSearchConditionDto extends PaginatorEntity {
    private String keyword;
    private Role role;
}
