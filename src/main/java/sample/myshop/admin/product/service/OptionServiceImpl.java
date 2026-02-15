package sample.myshop.admin.product.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sample.myshop.admin.product.domain.Option;
import sample.myshop.admin.product.domain.OptionValue;
import sample.myshop.admin.product.domain.Product;
import sample.myshop.admin.product.domain.dto.web.OptionCreateFormDto;
import sample.myshop.admin.product.domain.dto.web.OptionDto;
import sample.myshop.admin.product.domain.dto.web.OptionValueCreateFormDto;
import sample.myshop.admin.product.domain.dto.web.OptionValueDto;
import sample.myshop.admin.product.repository.OptionRepository;
import sample.myshop.admin.product.repository.ProductRepository;
import sample.myshop.common.exception.ProductNotFoundException;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OptionServiceImpl implements OptionService{

    private final ProductRepository productRepository;
    private final OptionRepository optionRepository;

    /**
     * 옵션 생성
     * @param productId
     * @param optionCreateFormDto
     */
    @Override
    @Transactional
    public void createOption(Long productId, OptionCreateFormDto optionCreateFormDto) {

        Product product = productRepository.findById(productId);

        if (product == null) {
            throw new ProductNotFoundException("상품을 찾을 수 없습니다.");
        }

        String optionName = optionCreateFormDto.getName();

        if (optionName == null) {
            throw new IllegalArgumentException("옵션명 입력");
        }

        String nameTrim = optionName.trim();

        // 옵션 명 trim 체크
        if (nameTrim.isEmpty()) {
            throw new IllegalArgumentException("옵션 명이 유효하지 않습니다.");
        }

        // 중복체크
        boolean optionAlreadyExists = optionRepository.checkDuplicate(productId, nameTrim);

        if (optionAlreadyExists) {
            throw new IllegalArgumentException("옵션 명이 중복입니다.");
        }

        Integer optionMaxSortOrder = optionRepository.getOptionMaxSortOrder(productId);

        int currentMaxSortOrder = optionMaxSortOrder == null
                ? 0
                : optionMaxSortOrder;

        Option option = Option.createOption(
                nameTrim,
                currentMaxSortOrder + 1,
                product
        );

        optionRepository.save(option);
    }

    /**
     * 옵션 조회
     * @param productId
     * @return
     */
    @Override
    public List<OptionDto> getOptions(Long productId) {
        List<Option> optionList = optionRepository.findByProductId(productId);

        List<Long> optionIds = optionList.stream().map(Option::getId).toList();

        List<OptionValue> foundOptionValues = optionRepository.findByOptionIds(optionIds);

        Map<Long, List<OptionValueDto>> groupedByOptionId = foundOptionValues.stream()
                .collect(groupingBy( // map으로 그룹핑
                        optionValue -> optionValue.getOption().getId(), // 키는 뭐로 잡을 것인가?
                        LinkedHashMap::new, // Map은 어떤 구현체로?
                        mapping(
                                optionValue -> new OptionValueDto(optionValue.getId(), optionValue.getValue(), optionValue.getSortOrder()),
                                toList()
                        ) // 어떤형태로?
                ));

        return optionList.stream().map(option -> new OptionDto(
                option.getId(),
                option.getName(),
                option.getSortOrder(),
                groupedByOptionId.getOrDefault(
                        option.getId(), //option.getId()(Long)로 맵을 조회해서 그 리스트를 달라
                        Collections.emptyList()  // 없으면 빈거
                )
        ))
                .toList();
    }

    /**
     * 옵션 값 생성
     * @param productId
     * @param optionId
     * @param optionValueCreateFormDto
     */
    @Override
    @Transactional
    public void createOptionValues(Long productId, Long optionId, OptionValueCreateFormDto optionValueCreateFormDto) {
        Product product = productRepository.findById(productId);

        if (product == null) {
            throw new ProductNotFoundException("상품을 찾을 수 없습니다.");
        }

        Option option = optionRepository.findById(optionId);

        if (option == null) {
            throw new ProductNotFoundException("옵션을 찾을 수 없습니다."); // 일단 상품 익셉션으로
        }

        boolean productIdMatches = option.getProduct().getId().equals(productId);

        if (!productIdMatches) {
            throw new IllegalArgumentException("옵션 대상 상품이 일치 하지 않습니다.");
        }

        String optionValueName = optionValueCreateFormDto.getValueName();

        if (optionValueName == null) {
            throw new IllegalArgumentException("옵션 값이 없습니다.");
        }

        String trim = optionValueName.trim();

        if (trim.isEmpty()) {
            throw new IllegalArgumentException("옵션 명이 유효하지 않습니다.");
        }

        Integer optionValueMaxOrder = optionRepository.getOptionValueMaxOrder(optionId);

        int currentSortOrder = optionValueMaxOrder == null ? 0 : optionValueMaxOrder;

        OptionValue optionValue = OptionValue.createOptionValue(trim, currentSortOrder + 1, option);

        optionRepository.saveOptionValue(optionValue);
    }
}
