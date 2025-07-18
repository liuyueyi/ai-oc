package com.git.hui.offer.gather.service.helper;

import com.git.hui.offer.components.bizexception.BizException;
import com.git.hui.offer.components.bizexception.StatusEnum;
import com.git.hui.offer.web.config.ImgConfig;
import com.github.hui.quick.plugin.base.OSUtil;
import com.github.hui.quick.plugin.base.file.FileReadUtil;
import com.github.hui.quick.plugin.base.file.FileWriteUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

/**
 * @author YiHui
 * @date 2025/7/18
 */
@Slf4j
@Component
public class LocalStorageHelper {
    private static final Random random = new Random();

    @Autowired
    private ImgConfig imgConfig;


    /**
     * 获取文件临时名称
     *
     * @return
     */
    private String genTmpFileName() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddhhmmssSSS")) + "_" + random.nextInt(100);
    }

    /**
     * 将附件保存到本地文件夹中
     *
     * @param input
     * @param fileType
     * @return 相对路径
     */
    public String saveFile(InputStream input, String fileType) {
        String fileName = genTmpFileName();
        String path = imgConfig.getAbsTmpPath() + imgConfig.getWebImgPath();
        FileWriteUtil.FileInfo fileInfo = null;
        try {
            fileInfo = FileWriteUtil.saveFileByStream(input, path, fileName, fileType);
        } catch (FileNotFoundException e) {
            log.error("failed to save file to {}", path, e);
            throw new BizException(StatusEnum.UNEXPECT_ERROR, "附件存储异常!");
        }
        return imgConfig.getWebImgPath() + fileInfo.getFilename() + "." + fileInfo.getFileType();
    }

    /**
     * 读取资源内容
     *
     * @param path
     * @return
     */
    public InputStream loadFile(String path) {
        if (imgConfig.getCdnHost() != null && path.startsWith(imgConfig.getCdnHost())) {
            // 提取本地路径
            path = imgConfig.getAbsTmpPath() + path.substring(imgConfig.getCdnHost().length());
            if (OSUtil.isWinOS() && !path.toLowerCase().startsWith("d:")) {
                // window 操作系统，补齐硬盘前缀
                path = "d:" + path;
            }
        } else if (path.startsWith("/") && OSUtil.isWinOS()) {
            // window 操作系统，补齐硬盘前缀
            path = "d:" + imgConfig.getAbsTmpPath() + path;
        }

        try {
            return FileReadUtil.createByteRead(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String buildFileHttpUrl(String file) {
        return imgConfig.buildImgUrl(file);
    }
}
