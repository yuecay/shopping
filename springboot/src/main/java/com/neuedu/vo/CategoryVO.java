package com.neuedu.vo;

import lombok.Data;

/**
 * @BelongsProject: springboot
 * @BelongsPackage: com.neuedu.vo
 * @Author: 郝毓才
 * @CreateTime: 2019-11-18 18:54
 * @Description: 将创建时间，修改时间format
 */
@Data
public class CategoryVO {
    private Integer id;
    private Integer parentId;
    private String mainImage;
    private String name;
    private Boolean status;
    private Integer sortOrder;
    private String createTime;
    private String updateTime;
}
