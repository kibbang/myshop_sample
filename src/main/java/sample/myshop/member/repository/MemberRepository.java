package sample.myshop.member.repository;

import sample.myshop.member.domain.Member;

public interface MemberRepository {
    Long save(Member member);
    Member findByLoginId(String loginId);
    boolean existByLoginId(String loginId);
}
