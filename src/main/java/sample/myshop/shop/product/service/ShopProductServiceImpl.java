package sample.myshop.shop.product.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sample.myshop.shop.product.domain.dto.web.ShopProductDetailDto;
import sample.myshop.shop.product.domain.dto.web.ShopProductListItemDto;
import sample.myshop.shop.product.repository.ShopProductRepository;


import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ShopProductServiceImpl implements ShopProductService {

    private final ShopProductRepository shopProductRepository;

    @Override
    public List<ShopProductListItemDto> getList() {
        return shopProductRepository.findAll();
    }

    @Override
    public ShopProductDetailDto getDetail(Long productId) {
        return shopProductRepository.findById(productId);
    }
}
