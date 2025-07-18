package com.git.hui.offer.gather.model;

import com.git.hui.offer.constants.gather.GatherTargetTypeEnum;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author YiHui
 * @date 2025/7/18
 */
public record GatherTaskSaveBo(GatherTargetTypeEnum type, String model, String content, MultipartFile file) {
}
