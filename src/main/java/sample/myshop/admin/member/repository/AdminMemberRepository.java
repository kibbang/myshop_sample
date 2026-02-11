package sample.myshop.admin.member.repository;

import sample.myshop.member.domain.Member;

public interface AdminMemberRepository {
    Member findById(Long memberId);
}
