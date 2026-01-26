package sample.myshop.admin.product.web.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import sample.myshop.admin.product.domain.dto.web.*;
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

    @GetMapping("/{productId}")
    public String show(@PathVariable Long productId, @ModelAttribute(name = "inventoryForm") InventoryAdjustRequestDto inventoryAdjustRequestDto, Model model) {
        ProductDetailDto productDetailDto = productService.showProduct(productId);

        model.addAttribute("product", productDetailDto);
        addContentView(model, "admin/product/detail :: content");

        return "admin/layout/base";
    }

    @GetMapping("/{productId}/edit")
    public String edit(@PathVariable Long productId, @ModelAttribute(name = "form") ProductUpdateRequestDto productUpdateRequestDto, Model model) {
        ProductDetailDto productDetailDto = productService.showProduct(productId);

        model.addAttribute("product", productDetailDto);

        productUpdateRequestDto.setName(productDetailDto.getName());
        productUpdateRequestDto.setStatus(productDetailDto.getStatus());
        productUpdateRequestDto.setCurrency(productDetailDto.getCurrency());
        productUpdateRequestDto.setBasePrice(productDetailDto.getBasePrice());
        productUpdateRequestDto.setSlug(productDetailDto.getSlug());
        productUpdateRequestDto.setDescription(productDetailDto.getDescription());

        addContentView(model, "admin/product/edit :: content");

        return "admin/layout/base";
    }

    @PostMapping("/{productId}")
    public String update(@PathVariable Long productId, @Validated @ModelAttribute(name = "form") ProductUpdateRequestDto productUpdateRequestDto, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            // 에러로 edit 페이지를 다시 렌더링할 때도, 템플릿이 기대하는 모델을 채워줘야 함
            ProductDetailDto productDetailDto = productService.showProduct(productId);
            model.addAttribute("product", productDetailDto);
            model.addAttribute("productId", productId);

            addContentView(model, "admin/product/edit :: content");
            return "admin/layout/base";
        }

        ProductUpdateDto productUpdateDto = ProductUpdateDto.of(
                productId,
                productUpdateRequestDto.getName(),
                productUpdateRequestDto.getSlug(),
                productUpdateRequestDto.getDescription(),
                productUpdateRequestDto.getStatus(),
                productUpdateRequestDto.getBasePrice(),
                productUpdateRequestDto.getCurrency()
        );

        productService.modifyProduct(productUpdateDto);

        redirectAttributes.addFlashAttribute("flashMessage", "상품 수정이 완료되었습니다.");

        return "redirect:/admin/products/" + productId;
    }

    @PostMapping("/{productId}/inventory")
    public String updateInventoryStock(@PathVariable Long productId,
                                       @Validated @ModelAttribute(name = "inventoryForm") InventoryAdjustRequestDto inventoryAdjustRequestDto,
                                       BindingResult bindingResult,
                                       Model model,
                                       RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            ProductDetailDto product = productService.showProduct(productId);
            model.addAttribute("product", product);
            addContentView(model, "admin/product/detail :: content");

            return "admin/layout/base";
        }

        productService.modifyProductInventoryStock(productId, inventoryAdjustRequestDto.getStockQuantity());

        redirectAttributes.addFlashAttribute("flashMessage", "재고 수정이 완료되었습니다.");

        return "redirect:/admin/products/" + productId;
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
