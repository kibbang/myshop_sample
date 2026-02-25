package sample.myshop.shop.my.cart.repository;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import sample.myshop.common.exception.BadRequestException;
import sample.myshop.shop.my.cart.domain.CartItem;
import sample.myshop.shop.my.cart.domain.dto.CartItemOrderSourceDto;
import sample.myshop.shop.my.cart.domain.dto.CartListItemDto;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CartRepositoryImpl implements CartRepository {

    private final EntityManager em;

    @Override
    public void save(CartItem cartItem) {
        em.persist(cartItem);
    }

    @Override
    public Optional<CartItem> findById(Long id) {
        CartItem item = em.find(CartItem.class, id);

        return Optional.ofNullable(item);
    }

    @Override
    public Optional<CartItem> findByMemberIdAndVariantId(Long memberId, Long variantId) {
        List<CartItem> cartItemList = em.createQuery("select c" +
                " from CartItem c" +
                " where c.memberId = :memberId" +
                " and c.variantId = :variantId", CartItem.class)
                .setParameter("memberId", memberId)
                .setParameter("variantId", variantId)
                .getResultList();

        return cartItemList.stream().findFirst();
    }

    @Override
    public Optional<CartItem> findBySessionIdAndVariantId(String sessionId, Long variantId) {
        List<CartItem> result = em.createQuery("""
                select c
                from CartItem c
                where c.sessionId = :sessionId
                  and c.memberId is null
                  and c.variantId = :variantId
                  and c.deletedAt is null
                """, CartItem.class)
                .setParameter("sessionId", sessionId)
                .setParameter("variantId", variantId)
                .getResultList();

        return result.stream().findFirst();
    }

    @Override
    public List<CartItem> findAllByMemberId(Long memberId) {
        return em.createQuery("""
                select c
                from CartItem c
                where c.memberId = :memberId
                  and c.deletedAt is null
                order by c.id desc
                """, CartItem.class)
                .setParameter("memberId", memberId)
                .getResultList();
    }

    @Override
    public List<CartItem> findAllBySessionId(String sessionId) {
        return em.createQuery("""
                select c
                from CartItem c
                where c.sessionId = :sessionId
                  and c.memberId is null
                  and c.deletedAt is null
                order by c.id desc
                """, CartItem.class)
                .setParameter("sessionId", sessionId)
                .getResultList();
    }

    @Override
    public Optional<CartItem> findByIdAndMemberId(Long id, Long memberId) {
        List<CartItem> result = em.createQuery("""
                select c
                from CartItem c
                where c.id = :id
                  and c.memberId = :memberId
                  and c.deletedAt is null
                """, CartItem.class)
                .setParameter("id", id)
                .setParameter("memberId", memberId)
                .getResultList();

        return result.stream().findFirst();
    }

    @Override
    public Optional<CartItem> findByIdAndSessionId(Long id, String sessionId) {
        List<CartItem> result = em.createQuery("""
                select c
                from CartItem c
                where c.id = :id
                  and c.memberId is null
                  and c.sessionId = :sessionId
                  and c.deletedAt is null
                """, CartItem.class)
                .setParameter("id", id)
                .setParameter("sessionId", sessionId)
                .getResultList();

        return result.stream().findFirst();
    }

    @Override
    public void delete(CartItem cartItem) {
        em.remove(cartItem);
    }

    @Override
    public List<CartListItemDto> findCartListItemsByMemberId(Long memberId) {
        return em.createQuery("""
            select new sample.myshop.shop.my.cart.domain.dto.CartListItemDto(
                c.id,
                c.productId,
                c.variantId,
                p.name,
                v.sku,
                coalesce(v.price, p.basePrice),
                c.quantity,
                i.stockQuantity
            )
            from CartItem c
            join Product p on p.id = c.productId
            join Variant v on v.id = c.variantId
            join Inventory i on i.variant.id = v.id
            where c.memberId = :memberId
              and c.deletedAt is null
              and p.deletedAt is null
              and v.deletedAt is null
              and i.deletedAt is null
            order by c.id desc
            """, CartListItemDto.class)
                .setParameter("memberId", memberId)
                .getResultList();
    }

    @Override
    public List<CartListItemDto> findCartListItemsBySessionId(String sessionId) {
        return em.createQuery("""
            select new sample.myshop.shop.my.cart.domain.dto.CartListItemDto(
                c.id,
                c.productId,
                c.variantId,
                p.name,
                v.sku,
                coalesce(v.price, p.basePrice),
                c.quantity,
                i.stockQuantity
            )
            from CartItem c
            join Product p on p.id = c.productId
            join Variant v on v.id = c.variantId
            join Inventory i on i.variant.id = v.id
            where c.sessionId = :sessionId
              and c.memberId is null
              and c.deletedAt is null
              and p.deletedAt is null
              and v.deletedAt is null
              and i.deletedAt is null
            order by c.id desc
            """, CartListItemDto.class)
                .setParameter("sessionId", sessionId)
                .getResultList();
    }

    @Override
    public List<CartItemOrderSourceDto> findSelectedForOrder(Long memberId, List<Long> cartItemIds) {
        if (cartItemIds == null || cartItemIds.isEmpty()) { // 쿼리 안정성 한번 더 방어
            return List.of();
        }

        return em.createQuery(
                "select new sample.myshop.shop.my.cart.domain.dto.CartItemOrderSourceDto(ci.id, ci.productId, ci.variantId, ci.quantity)" +
                        " from CartItem ci" +
                        " where ci.memberId = :memberId" +
                        " and ci.id in :cartItemIds" +
                        " order by ci.id", CartItemOrderSourceDto.class
        )
                .setParameter("memberId", memberId)
                .setParameter("cartItemIds", cartItemIds)
                .getResultList();
    }

    @Override
    public List<CartItemOrderSourceDto> findAllForOrder(Long memberId) {
        return em.createQuery(
                        "select new sample.myshop.shop.my.cart.domain.dto.CartItemOrderSourceDto(" +
                                " ci.id, ci.productId, ci.variantId, ci.quantity)" +
                                " from CartItem ci" +
                                " where ci.memberId = :memberId" +
                                " order by ci.id asc",
                        CartItemOrderSourceDto.class
                )
                .setParameter("memberId", memberId)
                .getResultList();
    }

    @Override
    public void deleteByMemberIdAndIds(Long memberId, List<Long> cartItemIds) {
        if (memberId == null) {
            throw new BadRequestException("회원 정보가 없습니다.");
        }

        if (cartItemIds == null || cartItemIds.isEmpty()) {
            return;
        }

        em.createQuery(
                        "delete from CartItem c " +
                                " where c.memberId = :memberId " +
                                "   and c.id in :cartItemIds"
                )
                .setParameter("memberId", memberId)
                .setParameter("cartItemIds", cartItemIds)
                .executeUpdate();
    }
}
