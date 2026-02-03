package sample.myshop.member.service;

import sample.myshop.member.domain.dto.MemberRegisterFormDto;

public interface MemberService {
    void join(MemberRegisterFormDto memberRegisterDto);
}
