package com.git.hui.offer.test;

import cn.hutool.core.io.resource.Resource;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.idev.excel.ExcelReader;
import cn.idev.excel.FastExcel;
import cn.idev.excel.context.AnalysisContext;
import cn.idev.excel.read.listener.ReadListener;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author YiHui
 * @date 2025/7/17
 */
@Slf4j
public class ExcelLoadTest {

    @Test
    public void testLoadExcel() {
        Resource resource = ResourceUtil.getResourceObj("data/oc.xlsx");
        ExcelReader reader = FastExcel.read(resource.getStream(), new ReadListener<LinkedHashMap>() {
            @Override
            public void invokeHead(Map headMap, AnalysisContext context) {
                log.info("获取表头: {}", headMap);
            }


            @Override
            public void invoke(LinkedHashMap strings, AnalysisContext analysisContext) {
                log.info("读取的数据 {}", strings);
            }

            @Override
            public void doAfterAllAnalysed(AnalysisContext analysisContext) {
                log.info("读取完毕");
            }
        }).build();
        reader.readAll();
        reader.close();
        log.info("结束");
    }
}
