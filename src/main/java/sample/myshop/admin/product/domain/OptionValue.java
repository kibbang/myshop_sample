package sample.myshop.admin.product.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.FetchType.*;
import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@Table(name = "prd_option_values")
@NoArgsConstructor
public class OptionValue {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;
    private String value;
    private int sortOrder;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "option_id")
    private Option option;

}
