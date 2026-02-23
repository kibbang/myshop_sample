package sample.myshop.admin.dashboard.repository;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import sample.myshop.admin.dashboard.dto.DashboardSummaryDto;
import sample.myshop.admin.dashboard.dto.LowStockItemDto;
import sample.myshop.admin.dashboard.dto.RecentOrderItemDto;
import sample.myshop.enums.order.OrderStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class AdminDashboardRepositoryImpl implements AdminDashboardRepository{

    private final EntityManager em;

    @Override
    public DashboardSummaryDto getDashboardSummary() {

        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);
        LocalDate weekStart = today.minusDays(6);

        LocalDateTime todayStart = today.atStartOfDay();
        LocalDateTime tomorrowStart = today.plusDays(1).atStartOfDay();
        LocalDateTime yesterdayStart = yesterday.atStartOfDay();
        LocalDateTime todayStartForYesterday = today.atStartOfDay();
        LocalDateTime weekStartDateTime = weekStart.atStartOfDay();

        long todayOrderCount = countOrdersBetween(todayStart, tomorrowStart);
        long yesterdayOrderCount = countOrdersBetween(yesterdayStart, todayStartForYesterday);
        long orderedCount = countOrdersByStatus(OrderStatus.ORDERED);
        long canceledCount = countOrdersByStatus(OrderStatus.CANCELED);

        long productCount = countProducts();
        long lowStockCount = countLowStockProducts(3);
        long memberCount = countMembers();
        long weeklyNewMemberCount = countMembersCreatedAfter(weekStartDateTime);

        return new DashboardSummaryDto(
                todayOrderCount,
                yesterdayOrderCount,
                orderedCount,
                canceledCount,
                productCount,
                lowStockCount,
                memberCount,
                weeklyNewMemberCount
        );
    }

    @Override
    public List<RecentOrderItemDto> findRecentOrders(int limit) {
        return em.createQuery(
                    "select new sample.myshop.admin.dashboard.dto.RecentOrderItemDto(" +
                            "o.id," +
                            "o.orderNo," +
                            " coalesce(m.name, o.buyerLoginId)," +
                            " o.status," +
                            " o.totalAmount," +
                            " o.createdAt" +
                            ")" +
                            " from Order o left join Member m on m.id = o.memberId" +
                            " order by o.createdAt desc", RecentOrderItemDto.class
                )
                .setMaxResults(limit)
                .getResultList();
    }

    @Override
    public List<LowStockItemDto> findLowStockItems(int limit, int threshold) {
        return em.createQuery("select new sample.myshop.admin.dashboard.dto.LowStockItemDto(p.id, p.name, v.sku, i.stockQuantity)" +
                " from Inventory i" +
                " join i.variant v" +
                " join v.product p" +
                " where i.stockQuantity <= :threshold" +
                " and p.deletedAt is null" +
                " order by i.stockQuantity asc, p.id desc", LowStockItemDto.class)
                .setParameter("threshold", threshold)
                .setMaxResults(limit)
                .getResultList();
    }

    private long countOrdersBetween(LocalDateTime from, LocalDateTime to) {
        return em.createQuery(
                        "select count(o) from Order o where o.createdAt >= :from and o.createdAt < :to",
                        Long.class
                ).setParameter("from", from)
                .setParameter("to", to)
                .getSingleResult();
    }

    private long countOrdersByStatus(OrderStatus status) {
        return em.createQuery(
                        "select count(o) from Order o where o.status = :status",
                        Long.class
                ).setParameter("status", status)
                .getSingleResult();
    }

    private long countProducts() {
        return em.createQuery(
                "select count(p) from Product p where p.deletedAt is null",
                Long.class
        ).getSingleResult();
    }

    private long countLowStockProducts(int threshold) {
        return em.createQuery(
                        "select count(i) from Inventory i join i.variant v join v.product p " +
                                "where i.stockQuantity <= :threshold and p.deletedAt is null",
                        Long.class
                ).setParameter("threshold", threshold)
                .getSingleResult();
    }

    private long countMembers() {
        return em.createQuery(
                "select count(m) from Member m where m.deletedAt is null",
                Long.class
        ).getSingleResult();
    }

    private long countMembersCreatedAfter(LocalDateTime from) {
        return em.createQuery(
                        "select count(m) from Member m where m.createdAt >= :from and m.deletedAt is null",
                        Long.class
                ).setParameter("from", from)
                .getSingleResult();
    }
}
