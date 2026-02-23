package sample.myshop.admin.dashboard.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sample.myshop.admin.dashboard.dto.AdminDashboardDto;
import sample.myshop.admin.dashboard.dto.DashboardSummaryDto;
import sample.myshop.admin.dashboard.dto.LowStockItemDto;
import sample.myshop.admin.dashboard.dto.RecentOrderItemDto;
import sample.myshop.admin.dashboard.repository.AdminDashboardRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminDashBoardServiceImpl implements AdminDashBoardService{

    private final AdminDashboardRepository adminDashboardRepository;

    @Override
    public AdminDashboardDto getDashboard() {
        DashboardSummaryDto dashboardSummary = adminDashboardRepository.getDashboardSummary();
        List<RecentOrderItemDto> recentOrders = adminDashboardRepository.findRecentOrders(5);
        List<LowStockItemDto> lowStockItems = adminDashboardRepository.findLowStockItems(5, 3); // limit=5, threshold=3

        return new AdminDashboardDto(dashboardSummary, recentOrders, lowStockItems);
    }
}
