package top.yumoyumo.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result<T> {
    private Boolean success;
    private String errMsg;
    private Integer errCode;
    private T data;


    public static <T> Result success(T data) {
        return new Result(true, null, null, data);
    }

    public static <Void> Result failure(String errMsg) {
        return new Result(false, errMsg, 500, null);
    }
}
