package sample.myshop.admin.member.repository.query;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import sample.myshop.admin.member.domain.dto.web.AdminMemberListDto;
import sample.myshop.admin.member.domain.dto.web.AdminMemberSearchConditionDto;
import sample.myshop.common.exception.NotFoundException;
import sample.myshop.member.domain.Address;
import sample.myshop.member.domain.Member;
import sample.myshop.member.enums.Role;
import sample.myshop.shop.my.domain.dto.AdminMemberDetailDto;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class AdminMemberQueryRepositoryImpl implements AdminMemberQueryRepository {

    @PersistenceContext
    private final EntityManager em;

    @Override
    public List<AdminMemberListDto> findMembers(AdminMemberSearchConditionDto condition) {
        int size = condition.getSize();
        int page = condition.getPage();
        String keyword = condition.getKeyword();
        Role role = condition.getRole();

        String baseJpql = "select m from Member m";

        boolean isFirstCondition = true;

        if (keyword != null && !keyword.isBlank()) {
            if (isFirstCondition) {
                baseJpql += " where m.name like :keyword";
                isFirstCondition = false;
            } else {
                baseJpql += " and m.name like :keyword";
            }
        }

        if (role != null) {
            if (isFirstCondition) {
                baseJpql += " where m.role = :role";
                isFirstCondition = false;
            } else {
                baseJpql += " and m.role = :role";
            }
        }

        baseJpql += " order by m.id desc";

        TypedQuery<Member> query = em.createQuery(baseJpql, Member.class).setFirstResult((page - 1) * size).setMaxResults(size);

        if (keyword != null && !keyword.isBlank()) {
            query.setParameter("keyword", "%" + keyword + "%");
        }

        if (role != null) {
            query.setParameter("status", role);
        }

        List<Member> fetchedMembers = query.getResultList();

        return fetchedMembers.stream()
                .map(member -> new AdminMemberListDto(
                member.getId(),
                member.getLoginId(),
                member.getName(),
                member.getPhone(),
                member.getRole(),
                member.isActive(),
                member.getCreatedAt()
        ))
                .toList();
    }

    @Override
    public AdminMemberDetailDto showMember(Long memberId) {
        List<Member> foundMembers = em.createQuery("select m from Member m where m.id = :memberId", Member.class)
                .setParameter("memberId", memberId)
                .getResultList();

        if (foundMembers.isEmpty()) {
            throw new NotFoundException("회원이 없습니다.: " + memberId);
        }

        Member member = foundMembers.get(0);
        Address address = member.getAddress();

        return new AdminMemberDetailDto(
                member.getId(),
                member.getLoginId(),
                member.getName(),
                member.getPhone(),
                address.getZipcode(),
                address.getBaseAddress(),
                address.getDetailAddress(),
                member.getRole(),
                member.isActive(),
                member.getCreatedAt(),
                member.getUpdatedAt()
        );
    }
}

