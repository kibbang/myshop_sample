package sample.myshop.member.repository;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import sample.myshop.member.domain.Member;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepository {

    private final EntityManager em;

    @Override
    public Long save(Member member) {
        em.persist(member);

        return member.getId();
    }

    @Override
    public Member findByLoginId(String loginId) {
        return em.createQuery("select m from Member m where m.loginId = :loginId ", Member.class)
                .setParameter("loginId", loginId)
                .getSingleResult();
    }

    @Override
    public boolean existByLoginId(String loginId) {
        List<Member> resultList = em.createQuery("select m from Member m where m.loginId = :loginId ", Member.class)
                .setParameter("loginId", loginId)
                .getResultList();

        return !resultList.isEmpty();
    }
}
