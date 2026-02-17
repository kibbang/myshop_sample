package sample.myshop.admin.product.service;

import jakarta.validation.constraints.NotEmpty;
import sample.myshop.admin.product.domain.dto.web.OptionCreateFormDto;
import sample.myshop.admin.product.domain.dto.web.OptionDto;
import sample.myshop.admin.product.domain.dto.web.OptionValueCreateFormDto;

import java.util.List;

public interface OptionService {
    void createOption(Long productId, OptionCreateFormDto optionCreateFormDto);
    void createOptionValues(Long variantId, Long optionId, OptionValueCreateFormDto optionValueCreateFormDto);
    List<OptionDto> getOptions(Long productId);
    Long optionCount(Long productId);

    void validateOptionFromProduct(Long productId, List<Long> normalizedOptionValueIds);

    List<Long> normalizeOptionValueIds(List<Long> optionValueIds);

    void checkOptionExists(Long productId, List<Long> normalizedOptionValueIds);
}
