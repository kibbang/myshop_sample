package sample.myshop.admin.product.web.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import sample.myshop.admin.product.domain.OptionValue;
import sample.myshop.admin.product.domain.Product;
import sample.myshop.admin.product.domain.Variant;
import sample.myshop.admin.product.domain.VariantOptionValue;
import sample.myshop.admin.product.domain.dto.web.*;
import sample.myshop.admin.product.service.OptionService;
import sample.myshop.admin.product.service.ProductService;
import sample.myshop.admin.product.service.VariantService;
import sample.myshop.common.exception.ProductNotFoundException;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import static java.lang.Math.*;
import static java.util.stream.Collectors.*;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/admin/products")
public class ProductController {

    private final ProductService productService;
    private final OptionService optionService;
    private final VariantService variantService;

    /**
     * 뷰 페이지
     * @param model
     * @return
     */
    @GetMapping
    public String products(@ModelAttribute(name = "searchForm") ProductSearchConditionDto searchForm, Model model) {
        List<ProductListItemDto> productList = productService.searchProducts(searchForm, max(searchForm.getPage(), 1), searchForm.getSize());
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
     * @param images
     * @param bindingResult
     * @param model
     * @return
     */
    @PostMapping("/create")
    public String save(
            @Validated @ModelAttribute(name = "form") ProductCreateRequestDto form,
            @RequestParam(required = false) MultipartFile[] images,
            BindingResult bindingResult, Model model
    ) {

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

        productService.createProduct(productCreateDto, images);

        return "redirect:/admin/products";
    }

    @GetMapping("/{productId}")
    public String show(@PathVariable Long productId, @ModelAttribute(name = "inventoryForm") InventoryAdjustRequestDto inventoryAdjustRequestDto, Model model) {
        ProductDetailDto productDetailDto = productService.showProduct(productId);

        List<Variant> variants = variantService.getVariants(productId);

        List<Long> variantIds = variants.stream().map(Variant::getId).toList();

        List<VariantOptionValue> variantOptionValueList = variantService.getVariantOptionValuesByIds(variantIds);

        Map<Long, List<VariantOptionValue>> grouped = variantOptionValueList.stream()
                .collect(groupingBy(
                        variantOptionValue -> variantOptionValue.getVariant().getId()
                ));

        List<VariantInfoDto> variantInfoList = variantService.getVariantInfoList(variants, variantOptionValueList);

        model.addAttribute("product", productDetailDto);
        model.addAttribute("variants", variantInfoList);

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
    public String update(
            @PathVariable Long productId,
            @Validated @ModelAttribute(name = "form") ProductUpdateRequestDto productUpdateRequestDto,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes,
            @RequestParam(required = false) MultipartFile[] images,
            @RequestParam(required = false, defaultValue = "false") boolean resetImages
    ) {
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

        productService.modifyProduct(productUpdateDto, images, resetImages);

        redirectAttributes.addFlashAttribute("flashMessage", "상품 수정이 완료되었습니다.");

        return "redirect:/admin/products/" + productId;
    }

    @PostMapping("/{productId}/variants/{variantId}/inventory/edit")
    public String updateVariantInventoryStock(
            @PathVariable Long productId,
            @PathVariable Long variantId,
            @RequestParam int stockQuantity,
            RedirectAttributes redirectAttributes
    ) {
        variantService.changeVariantStock(productId, variantId, stockQuantity);

        redirectAttributes.addFlashAttribute("flashMessage", "재고 수정이 완료되었습니다.");
        return "redirect:/admin/products/" + productId;
    }


    @GetMapping("/{productId}/options")
    public String options(@PathVariable Long productId, @ModelAttribute("form") OptionCreateFormDto optionCreateFormDto, Model model) {

        // 상품 헤더 주입
        ProductHeaderDto productHeader = productService.showProductHeader(productId);

        if (productHeader == null) {
            throw new ProductNotFoundException("상품을 찾을 수 없습니다.");
        }

        // 옵션
        List<OptionDto> options = optionService.getOptions(productId);


        model.addAttribute("product", productHeader);
        model.addAttribute("options", options);
        model.addAttribute("optionValueForm", new OptionValueCreateFormDto());

        addContentView(model, "admin/product/options/index :: content");
        return "admin/layout/base";
    }

    @PostMapping("/{productId}/options")
    public String createOption(@PathVariable Long productId, @Validated @ModelAttribute("form") OptionCreateFormDto optionCreateFormDto, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return options(productId, optionCreateFormDto, model);
        }

        optionService.createOption(productId, optionCreateFormDto);

        redirectAttributes.addFlashAttribute("flashMessage", "옵션 생성이 완료되었습니다.");

        return "redirect:/admin/products/" + productId + "/options";
    }

    @PostMapping("/{productId}/options/{optionId}/values")
    public String createOptionValue(
            @PathVariable Long productId,
            @PathVariable Long optionId,
            @Validated @ModelAttribute("optionValueForm") OptionValueCreateFormDto optionValueCreateFormDto,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        optionService.createOptionValues(productId, optionId, optionValueCreateFormDto);

        redirectAttributes.addFlashAttribute("flashMessage","옵션 값이 입력되었습니다.");

        return "redirect:/admin/products/" + productId + "/options";
    }

    /**
     * 옵션 조합 페이지
     * @param productId
     * @param model
     * @return
     */
    @GetMapping("/{productId}/variants/create")
    public String createVariant(@PathVariable Long productId, Model model) {

        // 옵션
        List<OptionDto> options = optionService.getOptions(productId);

        model.addAttribute("options", options);
        addContentView(model, "admin/product/variants/create :: content");

        return "admin/layout/base";
    }

    @PostMapping("/{productId}/variants/create")
    public String saveVariant(
            @PathVariable Long productId,
            @Validated @ModelAttribute(name = "form") AdminProductVariantCreateRequestDto form,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("errors", bindingResult.getFieldError());
            return "redirect:/admin/products/" + productId + "/variants/create";
        }

        Long optionsCount = optionService.optionCount(productId);
        int requestOptionSize = form.getOptionValueIds().size();

        // 옵션이 없을 경우
        if (optionsCount == 0) {
            redirectAttributes.addFlashAttribute("error", "옵션이 없습니다. 옵션/옵션값을 먼저 추가해주세요.");
            return "redirect:/admin/products/" + productId + "/variants/create";
        }

        // 모든 옵션을 1개씩 선택
        if (optionsCount != requestOptionSize) {
            redirectAttributes.addFlashAttribute("error", "각 옵션에서 1개씩 선택해주세요. (옵션 " + optionsCount + "개 / 선택 " + requestOptionSize + "개)");
            return "redirect:/admin/products/" + productId + "/variants/create";
        }

        // 리퀘스트로 들어온 생성 요청 옵션값 정규화 처리
        List<Long> normalizedOptionValueIds = optionService.normalizeOptionValueIds(form.getOptionValueIds());

        //  product 소속 검증
        optionService.validateOptionFromProduct(productId, normalizedOptionValueIds);

        // 이후 생성된 옵션 조합 있는지 확인
        optionService.checkOptionExists(productId, normalizedOptionValueIds);

        // 옵션 Variant 우선 저장
        VariantCreateDto newVariant = new VariantCreateDto(
                productId,
                form.getSku(),
                form.getCustomPrice()
        );

        Long newVariantId = variantService.createVariant(newVariant, normalizedOptionValueIds);

        redirectAttributes.addFlashAttribute("success","생성된 옵션조합 ID:" + newVariantId);

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
