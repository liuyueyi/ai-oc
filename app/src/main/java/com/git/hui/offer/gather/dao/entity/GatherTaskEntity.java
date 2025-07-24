package com.git.hui.offer.gather.dao.entity;

import com.git.hui.offer.constants.gather.GatherTargetTypeEnum;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.annotations.DynamicUpdate;

import java.util.Date;

/**
 * ai数据录入采用异步方案
 *
 * @author YiHui
 * @date 2025/7/18
 */
@Data
@Accessors(chain = true)
// 动态更新字段
@DynamicUpdate
@Entity(name = "gather_task")
public class GatherTaskEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 抓取类型
     *
     * @see GatherTargetTypeEnum#getValue()
     */
    @Column(name = "type")
    private Integer type;

    /**
     * 大语言模型
     */
    @Column(name = "model")
    private String model;

    /**
     * 任务处理状态： 0 未处理 1 处理中 2 处理完成 3 处理失败
     */
    @Column(name = "state")
    private Integer state;

    /**
     * 传入的数据，添加 @Lob 注解，解决h2database时，传入数据太大的报错
     * - 如果是文件，则这里存储文件转存到oss后的地址
     * - 如果不是文件，则直接存储用户传入的文本内容
     */
    @Lob
    @Column(name = "content")
    private String content;

    /**
     * 处理计数
     */
    @Column(name = "cnt")
    private Integer cnt;

    /**
     * 处理的结果，json格式，对应的value是 draft_oc 表的主键
     * 形如:
     * {
     * "insert": [1, 2, 3],
     * "update": [4, 5, 6]
     * }
     */
    @Column(name = "result")
    private String result;

    /**
     * 开始处理的时间
     */
    @Column(name = "process_time")
    private Date processTime;

    /**
     * 创建时间
     */
    @Column(name = "create_time")
    private Date createTime;

    /**
     * 更新时间
     */
    @Column(name = "update_time")
    private Date updateTime;
}
