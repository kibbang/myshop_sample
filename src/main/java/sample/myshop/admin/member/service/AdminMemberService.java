package sample.myshop.admin.member.service;

import sample.myshop.admin.member.domain.dto.web.AdminMemberListDto;
import sample.myshop.admin.member.domain.dto.web.AdminMemberSearchConditionDto;
import sample.myshop.admin.member.domain.dto.web.AdminMemberUpdateDto;
import sample.myshop.admin.member.domain.dto.web.AdminMemberUpdateRequestDto;
import sample.myshop.shop.my.domain.dto.AdminMemberDetailDto;

import java.util.List;

public interface AdminMemberService {
    List<AdminMemberListDto> searchMembers(AdminMemberSearchConditionDto condition);
    int getTotalCount(AdminMemberSearchConditionDto condition);
    AdminMemberDetailDto getMemberDetail(Long memberId);
    void updateMember(AdminMemberUpdateDto adminMemberUpdateDto);
}
