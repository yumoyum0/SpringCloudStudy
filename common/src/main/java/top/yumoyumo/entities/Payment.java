package top.yumoyumo.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: yumo
 * @Description: TODO
 * @DateTime: 2023/5/2 20:01
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Payment{
    private Long id;
    private String serial;

}
