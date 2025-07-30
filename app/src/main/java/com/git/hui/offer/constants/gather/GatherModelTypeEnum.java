package com.git.hui.offer.constants.gather;

import com.git.hui.offer.util.json.StringBaseEnum;
import lombok.Getter;

/**
 * 业界内的模型的类型
 *
 * @author YiHui
 * @date 2025/7/30
 */
@Getter
public enum GatherModelTypeEnum implements StringBaseEnum {
    CHAT_MODEL("ChatModel", "聊天模型"),
    EMBEDDING_MODEL("EmbeddingModel", "嵌入模型"),
    IMAGE_MODEL("ImageModel", "图片模型"),
    AUDIO_MODEL("AudioModel", "音频模型"),
    MODERATION_MODEL("ModerationModel", "内容审核模型"),
    VECTOR_MODEL("VectorModel", "向量模型");
    private final String value;
    private final String desc;

    GatherModelTypeEnum(String value, String desc) {
        this.value = value;
        this.desc = desc;
    }
}
