package sample.myshop.member.service;

import sample.myshop.auth.SessionUser;
import sample.myshop.member.domain.Member;
import sample.myshop.member.domain.dto.MemberRegisterFormDto;
import sample.myshop.shop.my.domain.dto.MyProfileUpdateDto;

public interface MemberService {
    Long join(MemberRegisterFormDto memberRegisterDto);

    SessionUser login(String loginId, String password);

    Member findMemberById(Long memberId);

    void updateInfo(MyProfileUpdateDto myProfileUpdateDto);
}
