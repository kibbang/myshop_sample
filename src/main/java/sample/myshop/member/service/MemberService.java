package sample.myshop.member.service;

import sample.myshop.auth.SessionUser;
import sample.myshop.member.domain.dto.MemberRegisterFormDto;

public interface MemberService {
    Long join(MemberRegisterFormDto memberRegisterDto);

    SessionUser login(String loginId, String password);
}
