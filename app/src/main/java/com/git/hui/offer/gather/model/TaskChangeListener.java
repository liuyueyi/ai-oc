package com.git.hui.offer.gather.model;

import com.git.hui.offer.constants.gather.GatherTaskStateEnum;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * 任务变更监听器
 *
 * @author YiHui
 * @date 2025/7/18
 */
@Getter
public class TaskChangeListener extends ApplicationEvent {

    private final Long taskId;
    private GatherTaskStateEnum state;

    public TaskChangeListener(Object source, Long taskId, GatherTaskStateEnum state) {
        super(source);
        this.taskId = taskId;
        this.state = state;
    }
}
