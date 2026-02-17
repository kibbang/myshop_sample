package sample.myshop.admin.product.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sample.myshop.common.entity.CommonEntity;

@Entity
@Getter
@Table(name = "prd_variant_option_values")
@NoArgsConstructor
public class VariantOptionValue extends CommonEntity {
    @EmbeddedId
    private VariantOptionValueId variantOptionValueId;

    @MapsId("variantId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "variant_id", nullable = false)
    private Variant variant;

    @MapsId("optionValueId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "option_value_id", nullable = false)
    private OptionValue optionValue;

    private VariantOptionValue(Variant variant, OptionValue optionValue) {
        this.variantOptionValueId = new VariantOptionValueId(variant.getId(), optionValue.getId());
        this.variant = variant;
        this.optionValue = optionValue;
    }

    public static VariantOptionValue create(Variant variant, OptionValue optionValue) {
        return new VariantOptionValue(variant, optionValue);
    }
}

