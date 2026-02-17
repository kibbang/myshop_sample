package sample.myshop.admin.product.repository;

import sample.myshop.admin.product.domain.Option;
import sample.myshop.admin.product.domain.OptionValue;

import java.util.List;

public interface OptionRepository {
    List<Option> findByProductId(Long productId);

    boolean checkDuplicate(Long productId, String nameTrim);

    Integer getOptionMaxSortOrder(Long productId);

    void save(Option option);

    Option findById(Long optionId);

    Integer getOptionValueMaxOrder(Long optionId);

    void saveOptionValue(OptionValue optionValue);

    List<OptionValue> findByOptionIds(List<Long> optionIds);

    Long countOption(Long productId);

    List<OptionValue> validateOptionFromProduct(Long productId, List<Long> optionValueIds);

    boolean checkOptionExists(Long productId, List<Long> normalizedOptionValueIds);

    List<OptionValue> findAllOptionValuesByIds(List<Long> optionValueIds);
}
