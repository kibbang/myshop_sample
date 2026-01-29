package sample.myshop.common.advice;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import sample.myshop.enums.product.Currency;
import sample.myshop.enums.product.SaleStatus;

@ControllerAdvice
public class EnumModelAdvice {
    @ModelAttribute("productCurrencies")
    public Currency[] productCurrencies() {
        return Currency.values();
    }

    @ModelAttribute("productSaleStatus")
    public SaleStatus[] productSaleStatus() {
        return SaleStatus.values();
    }
}
