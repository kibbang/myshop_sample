package sample.myshop;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import sample.myshop.member.domain.Address;
import sample.myshop.member.domain.Member;
import sample.myshop.member.enums.Role;
import sample.myshop.member.repository.MemberRepository;
import sample.myshop.member.service.MemberService;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;

@RequiredArgsConstructor
@Profile({"local","prod"})
@Component
@Transactional
public class AdminCreateSeeder implements CommandLineRunner {

    private final MemberRepository memberRepository;
    private final EntityManager em;
    private final BCryptPasswordEncoder passwordEncoder;

    @Value("${myshop.seed.admin:false}")
    private boolean seedAdmin;

    @Override
    public void run(String... args) throws Exception {
        Long count = em.createQuery("select count(m) from Member m where m.role = 'ADMIN'", Long.class)
                .getSingleResult();

        if (count > 0) return;

        Member member = Member.createMember(
                "admin",
                passwordEncoder.encode("admin"),
                "admin@example.com",
                "010-1234-5678",
                Address.createAddress(
                        "123-456",
                        "서울시 강남구",
                        "오잉빌딩"
                ),
                Role.ADMIN
        );

        memberRepository.save(member);
    }
}
