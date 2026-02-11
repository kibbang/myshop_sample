package sample.myshop.member.domain;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.*;

@Embeddable
@Getter
@NoArgsConstructor(access = PROTECTED)
public class Address {
    private String zipcode;
    private String baseAddress;
    private String detailAddress;

    private Address(String zipcode, String baseAddress, String detailAddress) {
        this.zipcode = zipcode;
        this.baseAddress = baseAddress;
        this.detailAddress = detailAddress;
    }

    /**
     * 생성
     * @param zipcode
     * @param baseAddress
     * @param detailAddress
     * @return
     */
    public static Address createAddress(String zipcode, String baseAddress, String detailAddress) {
        return new Address(zipcode, baseAddress, detailAddress);
    }

    /**
     * 변경
     * @param zipcode
     * @param baseAddress
     * @param detailAddress
     */
    public void changeAddress(String zipcode, String baseAddress, String detailAddress) {
        this.zipcode = zipcode;
        this.baseAddress = baseAddress;
        this.detailAddress = detailAddress;
    }

}
