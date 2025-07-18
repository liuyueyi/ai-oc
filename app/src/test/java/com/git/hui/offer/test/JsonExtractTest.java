package com.git.hui.offer.test;

import com.git.hui.offer.gather.service.helper.GatherResFormat;
import com.git.hui.offer.gather.service.helper.PartialJsonExtractor;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * @author YiHui
 * @date 2025/7/18
 */
public class JsonExtractTest {
    public static final String TXT = """
            ,
              {
                "companyName": "天阳科技",
                "companyType": "私企",
                "deadline": "",
                "deliveryProgress": "",
                "internalReferralCode": "",
                "jobAnnouncement": "2025金融AI未来计划：金融大模型开发工程师云校招启动",
                "jobLocation": "长沙",
                "lastUpdatedTime": "7月15日"{
              "companyName": "天阳科技",
              "companyType": "私企",
              "deadline": "",
              "deliveryProgress": "",
              "internalReferralCode": "",
              "jobAnnouncement": "2025金融AI未来计划：金融大模型开发工程师云校招启动",
              "jobLocation": "长沙",
              "lastUpdatedTime": "7月15日",
              "position": "互联网/电子商务/计算机软件/",
              "recruitmentType": "2026校招",
              "relatedLink": "https://mp.weixin.qq.com/s/gpJZGuas3ktNb4hMFxwAA?m=shars_1_80800_180000_37110_40130_5_51a178shar",
              "remarks": "",
              "requirementTarget": "尽快申请"
            }
            ```""";

    String text1 = """
            [
               {
                   "id": 14,
                   "companyName": "新浪&微博-管培生",
                   "companyType": "民企",
                   "jobLocation": "北京",
                   "recruitmentType": "秋招提前批",
                   "recruitmentTarget": "2026年毕业生",
                   "position": "产品管培生、商业管培生、运营管培生、研发管培生(算法方向)",
                   "deliveryProgress": "未投递",
                   "lastUpdatedTime": "2025-07-16",
                   "deadline": "招满为止",
                   "relatedLink": "https://app.mokahr.com/campus-recruitment/sina/43536?locale=zh-CN#/jobs?project0=100102761&page=1&anchorName=jobsList",
                   "jobAnnouncement": "https://mp.weixin.qq.com/s/y2DFOwvLmmsLNxWFwrqMJQ",
                   "internalReferralCode": "-",
                   "remarks": "-",
                   "state": 0,
                   "toProcess": 0,
                   "createTime": "2025-07-16T10:09:31.496+00:00",
                   "updateTime": "2025-07-16T10:09:31.496+00:00"
               },
               {
                   "id": 13,
                   "companyName": "中望软件",
                   "companyType": "民企",
                   "jobLocation": "广州 武汉 上海 北京西安",
                   "recruitmentType": "秋招",
                   "recruitmentTarget": "2026年毕业生",""";


    String text2 = """
             "position": "算法研究员(博士专项)、图形算法工程师、CAM算法工程师、C++研发工程师、销售管培生海外销售管培生、海外市场营销管培生",
                    "deliveryProgress": "未投递",
                    "lastUpdatedTime": "2025-07-16",
                    "deadline": "招满为止",
                    "relatedLink": "https://www.zwsoft.cn/job/campus",
                    "jobAnnouncement": "https://mp.weixin.qq.com/s/KTW2Fq3aaQ0J-TZCLCOIuA",
                    "internalReferralCode": "-",
                    "remarks": "-",
                    "state": 0,
                    "toProcess": 0,
                    "createTime": "2025-07-16T10:09:31.495+00:00",
                    "updateTime": "2025-07-16T10:09:31.495+00:00"
                },
                {
                    "id": 12,
                    "companyName": "极客未来",
                    "companyType": "民企",
                    "jobLocation": "成都市,重庆市",
                    "recruitmentType": "秋招提前批",
                    "recruitmentTarget": "2026年毕业生",
                    "position": "班课主讲教师,A计划-数学竞赛教师,学科校长管培生,运营校长管培生",
                    "deliveryProgress": "未投递",
                    "lastUpdatedTime": "2025-07-16",
                    "deadline": "招满为止",
                    "relatedLink": "https://jsj.top/f/N4enYy",
                    "jobAnnouncement": "https://mp.weixin.qq.com/s/rGGn4IPR1gvhAzUJNEBUNQ",
                    "internalReferralCode": "-",
                    "remarks": "-",
                    "state": 0,
                    "toProcess": 0,
                    "createTime": "2025-07-16T10:09:31.492+00:00",
                    "updateTime": "2025-07-16T10:09:31.492+00:00"
                },
                {
                    "id": 11,
                    "companyName": "友芝友集团",
                    "companyType": "民企",
                    "jobLocation": "武汉市",
                    "recruitmentType": "秋招提前批",
                    "recruitmentTarget": "2026年毕业生",
                    "position": "管培生",
                    "deliveryProgress": "未投递",
                    "lastUpdatedTime": "2025-07-16",
            """;

    String text3 = """
            "deadline": "招满为止",
                    "relatedLink": "https://mp.weixin.qq.com/s/W4lxjTd9KQDdC9LETSrwpw",
                    "jobAnnouncement": "https://mp.weixin.qq.com/s/W4lxjTd9KQDdC9LETSrwpw",
                    "internalReferralCode": "-",
                    "remarks": "-",
                    "state": 0,
                    "toProcess": 0,
                    "createTime": "2025-07-16T10:09:31.490+00:00",
                    "updateTime": "2025-07-16T10:09:31.490+00:00"
                },
                {
                    "id": 10,
                    "companyName": "中兴终端",
                    "companyType": "央国企",
                    "jobLocation": "上海市,深圳市,西安市,成都市",
                    "recruitmentType": "秋招提前批",
                    "recruitmentTarget": "2026年毕业生",
                    "position": "AI算法工程师,软件开发工程师射频开发工程师,结构设计工程师",
                    "deliveryProgress": "未投递",
                    "lastUpdatedTime": "2025-07-16",
                    "deadline": "招满为止",
                    "relatedLink": "https://job.zte.com.cn/cn/",
                    "jobAnnouncement": "https://mp.weixin.qq.com/s/WB8AzWth5B5ZChCFJPOdKw",
                    "internalReferralCode": "-",
                    "remarks": "-",
                    "state": 0,
                    "toProcess": 0,
                    "createTime": "2025-07-16T10:09:31.428+00:00",
                    "updateTime": "2025-07-16T10:09:31.428+00:00"
                },
                {
                    "id": 9,
                    "companyName": "Example Company",
                    "companyType": "Private",
                    "jobLocation": "New York, NY",
                    "recruitmentType": "Full-time",
                    "recruitmentTarget": "5",
                    "position": "Software Engineer",
                    "deliveryProgress": "50%",
                    "lastUpdatedTime": "2023-11-15T12:34:56Z",
                    "deadline": "2023-12-31",
                    "relatedLink": "https://www.example.com
            """;

    String text4 = """
            "relatedLink": "https://www.example.com/job/12345",
                    "jobAnnouncement": "Software Engineer",
                    "internalReferralCode": "REF12345",
                    "remarks": "Experience with Python and JavaScript required.",
                    "state": 0,
                    "toProcess": 0,
                    "createTime": "2025-07-16T09:54:36.816+00:00",
                    "updateTime": "2025-07-16T09:54:36.816+00:00"
                },
                {
                    "id": 6,
                    "companyName": "迅雷",
                    "companyType": "民企",
                    "jobLocation": "全国",
                    "recruitmentType": "暑假实习",
                    "recruitmentTarget": "2026年毕业生",
                    "position": "校园猎手",
                    "deliveryProgress": "未投递",
                    "lastUpdatedTime": "2025-07-14",
                    "deadline": "招满为止",
                    "relatedLink": "https://mp.weixin.qq.com/s/vMxuTOZ-evTzlLIkp5OTSg",
                    "jobAnnouncement": "https://mp.weixin.qq.com/s/vMxuTOZ-evTzlLIkp5OTSg",
                    "internalReferralCode": "1234",
                    "remarks": "dddasdfasdf",
                    "state": 1,
                    "toProcess": 1,
                    "createTime": "2025-07-16T02:54:17.034+00:00",
                    "updateTime": "2025-07-16T03:46:40.925+00:00"
                },
            """;


    @Test
    public void test() {
        StringBuilder builder = new StringBuilder(text1);
        List<String> ans = PartialJsonExtractor.extractCompleteElements(builder);
        System.out.println(ans);

        builder.append(text2);
        ans.addAll(PartialJsonExtractor.extractCompleteElements(builder));

        builder.append(text3);
        ans.addAll(PartialJsonExtractor.extractCompleteElements(builder));

        builder.append(text4);
        ans.addAll(PartialJsonExtractor.extractCompleteElements(builder));

        System.out.println(ans);
    }

    @Test
    public void testExact() {
        List<String> ans = GatherResFormat.extact(TXT);
        System.out.println(ans);
    }
}
