package sample.myshop.admin.member.repository.query;

import sample.myshop.admin.member.domain.dto.web.AdminMemberListDto;
import sample.myshop.admin.member.domain.dto.web.AdminMemberSearchConditionDto;
import sample.myshop.shop.my.domain.dto.AdminMemberDetailDto;

import java.util.List;

public interface AdminMemberQueryRepository {
    List<AdminMemberListDto> findMembers(AdminMemberSearchConditionDto condition);

    AdminMemberDetailDto showMember(Long memberId);
}
