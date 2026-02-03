package sample.myshop.shop.main.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sample.myshop.shop.main.dto.MainProductCardDto;
import sample.myshop.shop.product.repository.ShopProductRepository;

import java.util.List;


@Service
@RequiredArgsConstructor
public class MainServiceImpl implements MainService {

    private final ShopProductRepository productRepository;

    @Override
    public List<MainProductCardDto> getProductCards(int limit) {
        return productRepository.findMainProductCards(limit);
    }
}
