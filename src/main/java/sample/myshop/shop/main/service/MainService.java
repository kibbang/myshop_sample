package sample.myshop.shop.main.service;

import sample.myshop.shop.main.dto.MainProductCardDto;

import java.util.List;

public interface MainService {
    List<MainProductCardDto> getProductCards(int limit);
}
