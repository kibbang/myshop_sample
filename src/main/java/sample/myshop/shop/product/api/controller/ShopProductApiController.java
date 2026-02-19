package sample.myshop.shop.product.api.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import sample.myshop.shop.product.api.dto.ProductVariantMatchResponse;
import sample.myshop.shop.product.service.ShopProductService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ShopProductApiController {

    private final ShopProductService shopProductService;

    @GetMapping("/{productId}/variant")
    public ProductVariantMatchResponse getProductVariant(@PathVariable Long productId, @RequestParam(name = "optionValueIds") List<Long> optionValueIds) {
        return shopProductService.getMatchedVariant(productId, optionValueIds);
    }

}
