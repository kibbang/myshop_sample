package sample.myshop.member.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sample.myshop.member.domain.Address;
import sample.myshop.member.domain.Member;
import sample.myshop.member.domain.dto.MemberRegisterFormDto;
import sample.myshop.member.enums.Role;
import sample.myshop.member.repository.MemberRepository;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService{

    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final MemberRepository memberRepository;

    @Override
    @Transactional
    public void join(MemberRegisterFormDto memberRegisterFormDto) {
        Member member = Member.createMember(
                memberRegisterFormDto.getLoginId(),
                bCryptPasswordEncoder.encode(memberRegisterFormDto.getPassword()),
                memberRegisterFormDto.getName(),
                memberRegisterFormDto.getPhone(),
                Address.createAddress(
                        memberRegisterFormDto.getZipcode(),
                        memberRegisterFormDto.getBaseAddress(),
                        memberRegisterFormDto.getDetailAddress()
                ),
                Role.USER
        );

        memberRepository.save(member);
    }
}
