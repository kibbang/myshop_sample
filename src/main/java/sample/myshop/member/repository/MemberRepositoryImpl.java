package sample.myshop.member.repository;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import sample.myshop.member.domain.Member;

@Repository
@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepository {

    private final EntityManager em;

    @Override
    public void save(Member member) {
        em.persist(member);
    }

    @Override
    public Member findByLoginId(String loginId) {
        return null;
    }

    @Override
    public boolean existByLoginId(String loginId) {
        return false;
    }
}
