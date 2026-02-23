package sample.myshop.admin.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
public class AdminDashboardDto {

    private DashboardSummaryDto summary;
    private List<RecentOrderItemDto> recentOrders;
    private List<LowStockItemDto> lowStockItems;

}
