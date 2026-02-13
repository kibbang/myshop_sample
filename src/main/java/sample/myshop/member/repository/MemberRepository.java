package sample.myshop.member.repository;

import sample.myshop.member.domain.Member;

public interface MemberRepository {
    Long save(Member member);
    Member findByLoginId(String loginId);
    Member findById(Long memberId);
    boolean existByLoginId(String loginId);
}
