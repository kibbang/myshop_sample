package sample.myshop.admin.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class DashboardSummaryDto {
    private long todayOrderCount;
    private long yesterdayOrderCount;
    private long orderedCount;
    private long canceledCount;
    private long productCount;
    private long lowStockCount;
    private long memberCount;
    private long weeklyNewMemberCount;

    public long getTodayDelta() {
        return todayOrderCount - yesterdayOrderCount;
    }
}
