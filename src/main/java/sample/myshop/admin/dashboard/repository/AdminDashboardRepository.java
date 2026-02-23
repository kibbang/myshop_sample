package sample.myshop.admin.dashboard.repository;

import sample.myshop.admin.dashboard.dto.DashboardSummaryDto;
import sample.myshop.admin.dashboard.dto.LowStockItemDto;
import sample.myshop.admin.dashboard.dto.RecentOrderItemDto;

import java.util.List;

public interface AdminDashboardRepository {
    DashboardSummaryDto getDashboardSummary();
    List<RecentOrderItemDto> findRecentOrders(int limit);
    List<LowStockItemDto> findLowStockItems(int limit, int threshold);
}
