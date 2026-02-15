package sample.myshop.admin.product.domain.dto.web;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
@AllArgsConstructor
public class OptionCreateFormDto {
    @NotBlank
    @Length(max = 100)
    private String name;
}
