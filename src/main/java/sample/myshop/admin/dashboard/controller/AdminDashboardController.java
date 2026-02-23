package sample.myshop.admin.dashboard.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import sample.myshop.admin.dashboard.dto.AdminDashboardDto;
import sample.myshop.admin.dashboard.service.AdminDashBoardService;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminDashboardController {
    private final AdminDashBoardService adminDashBoardService;

    @GetMapping({"/", ""})
    public String dashboard(Model model) {
        AdminDashboardDto dashboardData = adminDashBoardService.getDashboard();
        model.addAttribute("dashboard", dashboardData);

        model.addAttribute("content", "admin/home/index :: content");
        model.addAttribute("activeMenu", "home");

        return "admin/layout/base";
    }

}
