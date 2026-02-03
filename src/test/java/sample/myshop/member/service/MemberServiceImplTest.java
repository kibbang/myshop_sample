package sample.myshop.member.service;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import sample.myshop.member.domain.Member;
import sample.myshop.member.domain.dto.MemberRegisterFormDto;
import sample.myshop.member.enums.Role;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class MemberServiceImplTest {

    @Autowired
    private MemberService memberService;
    @Autowired
    private EntityManager em;

    @Test
    void 회원가입() {
        // given
        MemberRegisterFormDto memberRegisterFormDto = new MemberRegisterFormDto();
        memberRegisterFormDto.setLoginId("tester");
        memberRegisterFormDto.setPassword("12qwaszxas");
        memberRegisterFormDto.setName("테스터");
        memberRegisterFormDto.setPhone("01012345678");
        memberRegisterFormDto.setZipcode("123-123");
        memberRegisterFormDto.setBaseAddress("성남시");
        memberRegisterFormDto.setDetailAddress("분당구");

        // when
        Long savedMemberId = memberService.join(memberRegisterFormDto);
        em.flush();
        em.clear();


        // then
        Member member = em.find(Member.class, savedMemberId);

        assertEquals("tester", member.getLoginId());
        assertEquals(Role.USER, member.getRole());
    }

    @Test
    void 중복_체크() {
        // given
        MemberRegisterFormDto memberRegisterFormDto = new MemberRegisterFormDto();
        memberRegisterFormDto.setLoginId("tester");
        memberRegisterFormDto.setPassword("12qwaszxas");
        memberRegisterFormDto.setName("테스터");
        memberRegisterFormDto.setPhone("01012345678");
        memberRegisterFormDto.setZipcode("123-123");
        memberRegisterFormDto.setBaseAddress("성남시");
        memberRegisterFormDto.setDetailAddress("분당구");

        memberService.join(memberRegisterFormDto);

        em.flush();
        em.clear();

        MemberRegisterFormDto memberRegisterFormDto2 = new MemberRegisterFormDto();
        memberRegisterFormDto2.setLoginId("tester");
        memberRegisterFormDto2.setPassword("12qwaszxas");
        memberRegisterFormDto2.setName("테스터2");
        memberRegisterFormDto2.setPhone("01012345678");
        memberRegisterFormDto2.setZipcode("123-123");
        memberRegisterFormDto2.setBaseAddress("성남시");
        memberRegisterFormDto2.setDetailAddress("분당구");

        assertThrows(IllegalArgumentException.class, () -> memberService.join(memberRegisterFormDto2));

    }

}