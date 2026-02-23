package sample.myshop.common.api;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ApiErrorResponse {
    private boolean isSuccess;
    private String code;
    private String message;

    public static ApiErrorResponse of(String code, String message) {
        return new ApiErrorResponse(false, code, message);
    }
}

