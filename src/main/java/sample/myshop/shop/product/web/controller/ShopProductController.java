package sample.myshop.shop.product.web.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import sample.myshop.shop.order.domain.dto.web.OrderCreateForm;
import sample.myshop.shop.product.domain.dto.web.ShopProductDetailDto;
import sample.myshop.shop.product.domain.dto.web.ShopProductListItemDto;
import sample.myshop.shop.product.domain.dto.web.ShopProductOptionDto;
import sample.myshop.shop.product.service.ShopProductService;

import java.util.List;

@Controller
@Slf4j
@RequestMapping("/products")
@RequiredArgsConstructor
public class ShopProductController {

    private final ShopProductService shopProductService;

    @GetMapping
    public String Products(Model model) {

        List<ShopProductListItemDto> productList = shopProductService.getList();

        addContentView(model, "shop/product/list :: content");
        model.addAttribute("productList", productList);

        return "shop/layout/base";
    }

    @GetMapping("/{productId}")
    public String show(@PathVariable Long productId, @ModelAttribute(name = "orderForm") OrderCreateForm orderForm, Model model) {

        ShopProductDetailDto productDetail = shopProductService.getDetail(productId);
        List<ShopProductOptionDto> options = shopProductService.getOptionsWithValuesByProductId(productId);
        Long defaultVariantId = shopProductService.getDefaultVariant(productId);

        model.addAttribute("product", productDetail);
        model.addAttribute("options", options);
        model.addAttribute("defaultVariantId", defaultVariantId);
        addContentView(model, "shop/product/detail :: content");

        return "shop/layout/base";
    }

    /**
     * 뷰에 컨텐츠 삽입
     * @param model
     * @param attributeTarget
     */
    private static void addContentView(Model model, String attributeTarget) {
        model.addAttribute("content", attributeTarget);
    }
}
