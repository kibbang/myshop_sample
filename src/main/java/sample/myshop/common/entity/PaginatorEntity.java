package sample.myshop.common.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaginatorEntity {
    private int page = 1;
    private int size = 20;
}
