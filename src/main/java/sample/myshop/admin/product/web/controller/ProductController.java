package sample.myshop.admin.product.web.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import sample.myshop.admin.product.domain.dto.web.ProductCreateDto;
import sample.myshop.admin.product.domain.dto.web.ProductCreateRequestDto;
import sample.myshop.admin.product.domain.dto.web.ProductListItemDto;
import sample.myshop.admin.product.domain.dto.web.ProductSearchConditionDto;
import sample.myshop.admin.product.service.ProductService;

import java.util.List;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/admin/products")
public class ProductController {

    private final ProductService productService;

    /**
     * 뷰 페이지
     * @param model
     * @return
     */
    @GetMapping
    public String products(@ModelAttribute(name = "searchForm") ProductSearchConditionDto searchForm, Model model) {
        List<ProductListItemDto> productList = productService.searchProducts(searchForm, Math.max(searchForm.getPage(), 1), searchForm.getSize());
        Long totalCount = productService.getTotalProductCount(searchForm);

        model.addAttribute("productList", productList);
        model.addAttribute("totalCount", totalCount);

        addContentView(model, "admin/product/list :: content");

        return "admin/layout/base";
    }

    /**
     * 등록 페이지
     * @param form
     * @param model
     * @return
     */
    @GetMapping("/create")
    public String create(@ModelAttribute(name = "form") ProductCreateRequestDto form, Model model) {
        addContentView(model, "admin/product/create :: content");

        return "admin/layout/base";
    }

    /**
     * 등록
     * @param form
     * @param bindingResult
     * @param model
     * @return
     */
    @PostMapping("/create")
    public String save(@Validated @ModelAttribute(name = "form") ProductCreateRequestDto form, BindingResult bindingResult, Model model) {

        if (bindingResult.hasErrors()) {
            addContentView(model, "admin/product/create :: content");
            return "admin/layout/base";
        }

        ProductCreateDto productCreateDto = ProductCreateDto.of(
                form.getCode(),
                form.getName(),
                form.getSlug(),
                form.getDescription(),
                form.getStatus(),
                form.getBasePrice(),
                form.getCurrency()
        );

        productService.createProduct(productCreateDto);

        return "redirect:/admin/products";
    }

    /**
     * 뷰에 컨텐츠 삽입
     * @param model
     * @param attributeTarget
     */
    private static void addContentView(Model model, String attributeTarget) {
        model.addAttribute("content", attributeTarget);
        model.addAttribute("activeMenu", "products");
    }
}
