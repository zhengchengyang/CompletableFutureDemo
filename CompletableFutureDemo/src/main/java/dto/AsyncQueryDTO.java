package dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @description 异步查询结果封装类
 * @author zcyang
 * @date 2022/04/03 18:40
 */
@Data
@AllArgsConstructor
public class AsyncQueryDTO<T> {

    /**
     * 数据
     */
    private T data;

    /**
     * 数据类型
     */
    private Integer dataType;
}
