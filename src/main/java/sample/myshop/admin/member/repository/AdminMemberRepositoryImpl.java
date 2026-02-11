package sample.myshop.admin.member.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import sample.myshop.member.domain.Member;

@Repository
@RequiredArgsConstructor
public class AdminMemberRepositoryImpl implements AdminMemberRepository {
    @PersistenceContext
    private final EntityManager em;

    @Override
    public Member findById(Long memberId) {
        return em.find(Member.class, memberId);
    }
}
