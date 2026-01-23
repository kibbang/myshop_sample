package sample.myshop.admin.product.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Embeddable
@NoArgsConstructor
@Getter
@EqualsAndHashCode
public class VariantOptionValueId implements Serializable {

    @Column(name = "variant_id")
    private Long variantId;

    @Column(name = "option_value_id")
    private Long optionValueId;

    public VariantOptionValueId(Long variantId, Long optionValueId) {
        this.variantId = variantId;
        this.optionValueId = optionValueId;
    }
}
