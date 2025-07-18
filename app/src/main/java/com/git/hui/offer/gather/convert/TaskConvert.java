package com.git.hui.offer.gather.convert;

import com.git.hui.offer.gather.dao.entity.GatherTaskEntity;
import com.git.hui.offer.web.model.res.TaskVo;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author YiHui
 * @date 2025/7/18
 */
public class TaskConvert {

    public static TaskVo toVo(GatherTaskEntity task, Function<GatherTaskEntity, String> func) {
        return new TaskVo()
                .setTaskId(task.getId())
                .setCnt(task.getCnt())
                .setModel(task.getModel())
                .setProcessTime(task.getProcessTime() != null ? task.getProcessTime().getTime() : null)
                .setCreateTime(task.getCreateTime().getTime())
                .setUpdateTime(task.getUpdateTime().getTime())
                .setState(task.getState())
                .setType(task.getType())
                .setContent(func.apply(task))
                .setResult(task.getResult());
    }

    public static List<TaskVo> toVo(List<GatherTaskEntity> taskList, Function<GatherTaskEntity, String> func) {
        if (taskList == null || taskList.isEmpty()) {
            return List.of();
        }
        return taskList.stream().map(s -> toVo(s, func)).collect(Collectors.toList());
    }
}
