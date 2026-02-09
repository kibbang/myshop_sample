package sample.myshop.member.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sample.myshop.auth.SessionUser;
import sample.myshop.member.domain.Address;
import sample.myshop.member.domain.Member;
import sample.myshop.member.domain.dto.MemberRegisterFormDto;
import sample.myshop.member.enums.Role;
import sample.myshop.member.repository.MemberRepository;
import sample.myshop.utils.EncryptHelper;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService{

    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final MemberRepository memberRepository;

    @Override
    @Transactional
    public Long join(MemberRegisterFormDto memberRegisterFormDto) {
        // 중복 체크
        boolean memberExists = memberRepository.existByLoginId(memberRegisterFormDto.getLoginId());

        if (memberExists) {
            throw new IllegalArgumentException("이미 존재하는 아이디입니다.");
        }

        // member 생성
        Member member = Member.createMember(
                memberRegisterFormDto.getLoginId(),
                bCryptPasswordEncoder.encode(memberRegisterFormDto.getPassword()),
                EncryptHelper.encrypt(memberRegisterFormDto.getName()),
                EncryptHelper.encrypt(memberRegisterFormDto.getPhone()),
                Address.createAddress(
                        memberRegisterFormDto.getZipcode(),
                        memberRegisterFormDto.getBaseAddress(),
                        memberRegisterFormDto.getDetailAddress()
                ),
                Role.USER
        );

        return memberRepository.save(member);
    }

    @Override
    public SessionUser login(String loginId, String password) {
        Member foundMember = memberRepository.findByLoginId(loginId);

        if (foundMember == null) {
            throw new IllegalArgumentException("존재하지 않는 아이디입니다.");
        }

        if (!foundMember.isActive()) {
            throw new IllegalArgumentException("비활성화된 계정입니다. 관리자에게 문의하세요.");
        }

        if (!bCryptPasswordEncoder.matches(password, foundMember.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        return new SessionUser(foundMember.getId(), foundMember.getLoginId(), foundMember.getRole());
    }
}
