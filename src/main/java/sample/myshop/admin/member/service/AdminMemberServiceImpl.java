package sample.myshop.admin.member.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sample.myshop.admin.member.domain.dto.web.AdminMemberListDto;
import sample.myshop.admin.member.domain.dto.web.AdminMemberSearchConditionDto;
import sample.myshop.admin.member.domain.dto.web.AdminMemberUpdateDto;
import sample.myshop.admin.member.repository.AdminMemberRepository;
import sample.myshop.admin.member.repository.query.AdminMemberQueryRepository;
import sample.myshop.member.domain.Address;
import sample.myshop.member.domain.Member;
import sample.myshop.shop.my.domain.dto.AdminMemberDetailDto;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AdminMemberServiceImpl implements AdminMemberService{

    private final AdminMemberQueryRepository adminMemberQueryRepository;
    private final AdminMemberRepository adminMemberRepository;

    @Override
    public List<AdminMemberListDto> searchMembers(AdminMemberSearchConditionDto condition) {
        return adminMemberQueryRepository.findMembers(condition);
    }

    @Override
    public int getTotalCount(AdminMemberSearchConditionDto condition) {
        return adminMemberQueryRepository.findMembers(condition).size();
    }

    @Override
    public AdminMemberDetailDto getMemberDetail(Long memberId) {
        return adminMemberQueryRepository.showMember(memberId);
    }

    @Override
    @Transactional
    public void updateMember(AdminMemberUpdateDto adminMemberUpdateDto) {
        Member member = adminMemberRepository.findById(adminMemberUpdateDto.getId());

        Address memberAddress = member.getAddress();

        member.changeDefaultInfo(
                adminMemberUpdateDto.getName(),
                adminMemberUpdateDto.getPhone()
        );

        member.changeActiveStatus(adminMemberUpdateDto.getActive());

        memberAddress.changeAddress(
                adminMemberUpdateDto.getZipCode(),
                adminMemberUpdateDto.getBaseAddress(),
                adminMemberUpdateDto.getDetailAddress()
        );

    }
}
