package sample.myshop.admin.product.service;

import sample.myshop.admin.product.domain.dto.web.OptionCreateFormDto;
import sample.myshop.admin.product.domain.dto.web.OptionDto;
import sample.myshop.admin.product.domain.dto.web.OptionValueCreateFormDto;

import java.util.List;

public interface OptionService {
    void createOption(Long productId, OptionCreateFormDto optionCreateFormDto);
    void createOptionValues(Long variantId, Long optionId, OptionValueCreateFormDto optionValueCreateFormDto);
    List<OptionDto> getOptions(Long productId);
}
