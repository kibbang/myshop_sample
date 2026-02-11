package sample.myshop.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminHomeController {

    @GetMapping({"/", ""})
    public String home(Model model) {
        model.addAttribute("content", "admin/home/index :: content");
        model.addAttribute("activeMenu", "home");
        return "admin/layout/base";
    }
}
