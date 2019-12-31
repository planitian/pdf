package com.example.pdf;

import com.example.pdf.utils.OperatingSystem;
import com.example.pdf.utils.PhantomJSExecutionResponse;
import com.example.pdf.utils.PhantomUtils;
import com.google.gson.Gson;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.apache.commons.exec.*;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

@SpringBootTest
class PdfApplicationTests {
    static final Path TEMP_DIR = Paths.get(System.getProperty("java.io.tmpdir", "/tmp"));


    @Test
    void two() throws IOException, TemplateException {
        Map<String, Object> dataModel = new HashMap<>();
        One one = new One();
        one.setAge("19");
        one.setName("www");
        //向数据集中添加数据
        dataModel.put("po", one);
        dataModel.put("one", "outsite");

        String json = "{\"type\":1,\"name\":\"李涵\",\"startTime\":\"2019.12.23\",\"endTime\":\"2019.12.29\",\"orgCounts\":3,\"overAllChange\":[[4,4,0],[0,0,0]],\"riskAll\":{\"华为技术有限公司\":[0,3,0],\"安徽华星化工有限公司\":[0,10,0],\"海航集团有限公司\":[0,77,0]},\"longListMap\":{\"114748436\":[{\"id\":\"q0201\",\"x315OrgId\":\"114748436\",\"x315OrgName\":\"海航集团有限公司\",\"counts\":3,\"detailsIds\":\"8689344764248065,8689346412609537,8689349101158401\",\"advice\":\"新增3条被执行人信息，金额较大，可能增加企业的资金压力，提请注意把控付款风险\"},{\"id\":\"q0203\",\"x315OrgId\":\"114748436\",\"x315OrgName\":\"海航集团有限公司\",\"counts\":16,\"detailsIds\":\"8689341362667521,8689355577163777,8689361113645057,8689360585162753,8689367925194753,8689378465480705,8689342998446081,8689346681044993,8689369917489153,8689361491132417,8689368852135937,8689345691189249,8689349558337537,8689362040586241,8689362875252737,8689365442166785\",\"advice\":\"新增16条裁判文书案件，可能会影响到企业经营效率，提请注意把控风险\"},{\"id\":\"q0204\",\"x315OrgId\":\"114748436\",\"x315OrgName\":\"海航集团有限公司\",\"counts\":9,\"detailsIds\":\"8689358307655681,8689347536683009,8689356277612545,8689364041269249,8689340989374465,8689347259858945,8689351097647105,8689347977084929,8689356051120129\",\"advice\":\"新增9条法院公告，可能会影响到企业的经营效率，提请注意把控风险\"},{\"id\":\"q0304\",\"x315OrgId\":\"114748436\",\"x315OrgName\":\"海航集团有限公司\",\"counts\":4,\"detailsIds\":\"8689342264442881,8689369493864449,8689342595792897,8689345045266433\",\"advice\":\"涉及4条经营异常，企业存在信用风险，可能在融资、招投标等多方面受影响\"}],\"131179779\":[{\"id\":\"q0203\",\"x315OrgId\":\"131179779\",\"x315OrgName\":\"安徽华星化工有限公司\",\"counts\":11,\"detailsIds\":\"8689371024785409,8689366490742785,8689368294293505,8689371800731649,8689371981086721,8689368634032129,8689371494547457,8689370269810689,8689365723185153,8689367350575105,8689380713627649\",\"advice\":\"新增11条裁判文书案件，可能会影响到企业经营效率，提请注意把控风险\"},{\"id\":\"q0402\",\"x315OrgId\":\"131179779\",\"x315OrgName\":\"安徽华星化工有限公司\",\"counts\":1,\"detailsIds\":\"8689369321897985\",\"advice\":\"新增(减少)对外投资，提请注意把控风险\"},{\"id\":\"q0405\",\"x315OrgId\":\"131179779\",\"x315OrgName\":\"安徽华星化工有限公司\",\"counts\":2,\"detailsIds\":\"8689370739572737,8689372857696257\",\"advice\":\"发生动产抵押，提请注意避免交易或担保风险\"},{\"id\":\"q0601\",\"x315OrgId\":\"131179779\",\"x315OrgName\":\"安徽华星化工有限公司\",\"counts\":2,\"detailsIds\":\"8690768336191489,8690754583068673\",\"advice\":\"新增多起新闻舆情\"}],\"113074920\":[{\"id\":\"q0203\",\"x315OrgId\":\"113074920\",\"x315OrgName\":\"华为技术有限公司\",\"counts\":126,\"detailsIds\":\"8689459985973249,8689427006160897,8689604165173249,8689377639202817,8689386598236161,8689422161739777,8689608317534209,8689606719504385,8689379044294657,8689380491329537,8689462032793601,8689598125375489,8689418445586433,8689387302879233,8689465824444417,8689374963236865,8689381472796673,8689464255774721,8689592156880897,8689372639592449,8689382240354305,8689466931740673,8689411109748737,8689374438948865,8689384207482881,8689390066925569,8689597936631809,8689378784247809,8689385558048769,8689391115501569,8689600327385089,8689374757715969,8689387479040001,8689392453484545,8689503296356353,8689388699582465,8689458627018753,8689462301229057,8689543020609537,8689596732866561,8689417199878145,8689375839846401,8689385839067137,8689408828047361,8689410036006913,8689541842010113,8689614948728833,8689378083799041,8689387839750145,8689379337895937,8689422581170177,8689430160277505,8689613539442689,8689383616086017,8689463366582273,8689605868060673,8689430810394625,8689390956118017,8689466508115969,8689375361695745,8689\",\"advice\":\"新增126条裁判文书案件，可能会影响到企业经营效率，提请注意把控风险\"},{\"id\":\"q0204\",\"x315OrgId\":\"113074920\",\"x315OrgName\":\"华为技术有限公司\",\"counts\":2,\"detailsIds\":\"8689593650053121,8689606128107521\",\"advice\":\"新增2条法院公告，可能会影响到企业的经营效率，提请注意把控风险\"}]},\"cautions\":[{\"name\":\"海航集团有限公司\",\"id\":114748436,\"gqlz\":0,\"gqyz\":81},{\"name\":\"安徽华星化工有限公司\",\"id\":131179779,\"gqlz\":0,\"gqyz\":10},{\"name\":\"华为技术有限公司\",\"id\":113074920,\"gqlz\":0,\"gqyz\":3}],\"hints\":[{\"name\":\"安徽华星化工有限公司\",\"orgId\":131179779,\"zcbg\":2,\"flss\":0,\"xzjg\":0,\"jyzk\":0,\"zscq\":0,\"xwyq\":0},{\"name\":\"青岛蓝海汇网络科技有限公司\",\"orgId\":131891038,\"zcbg\":1,\"flss\":0,\"xzjg\":0,\"jyzk\":0,\"zscq\":0,\"xwyq\":0}],\"negativeCounts\":0,\"nagativeInfos\":[]}";
        json = "[{value:2,name:\"警示风险的企业\"},{value:3,name:\"提示信息的企业\"},{value:4,name:\"无变动的企业\"}]";
        List<Map<String, Object>> date = new ArrayList<>();
        Map<String, Object> temp = new HashMap<>();
        temp.put("value", 2);
        temp.put("name", "警示风险的企业");
        date.add(temp);
        Map<String, Object> temp1 = new HashMap<>();
        temp1.put("value", 3);
        temp1.put("name", "提示信息的企业");
        date.add(temp1);
        Map<String, Object> temp2 = new HashMap<>();
        temp2.put("value", 4);
        temp2.put("name", "无变动的企业");
        date.add(temp2);
        Gson gson = new Gson();
        json = gson.toJson(date);
        System.out.println(json);
        json = "{\"type\":1,\"name\":\"李涵888\",\"startTime\":\"2019.12.16\",\"endTime\":\"2019.12.29\",\"orgCounts\":2,\"overAllChange\":[[1,1,1],[0,0,0]],\"riskAll\":{\"青岛格兰德信用管理咨询有限公司\":[2,0,0]},\"longListMap\":{\"100888670\":[{\"id\":\"g0407\",\"x315OrgId\":\"100888670\",\"x315OrgName\":\"青岛格兰德信用管理咨询有限公司\",\"counts\":1,\"detailsIds\":\"8691347095617537\",\"advice\":\"债券逾期，可能存在资金链紧张等风险\"},{\"id\":\"q0402\",\"x315OrgId\":\"100888670\",\"x315OrgName\":\"青岛格兰德信用管理咨询有限公司\",\"counts\":1,\"detailsIds\":\"8690257574821889\",\"advice\":\"新增(减少)对外投资，提请注意把控风险\"}]},\"cautions\":[{\"name\":\"青岛格兰德信用管理咨询有限公司\",\"id\":100888670,\"gqlz\":0,\"gqyz\":2}],\"hints\":[{\"name\":\"青岛格兰德信用管理咨询有限公司\",\"orgId\":100888670,\"zcbg\":7,\"flss\":0,\"xzjg\":0,\"jyzk\":0,\"zscq\":0,\"xwyq\":0}],\"negativeCounts\":0,\"nagativeInfos\":[]}";
        dataModel.put("dar", json);
        ExecutorService executorService = Executors.newScheduledThreadPool(10);
        List<Runnable> runnables = new ArrayList<>();
        for (int i = 0; i < 1; i++) {
            runnables.add(new Runnable() {
                @Override
                public void run() {
                    PhantomJSExecutionResponse phantomJSExecutionResponse = null;
                    String out = "two" + UUID.randomUUID().toString().substring(16) + ".pdf";
                    Path reportDate = Paths.get("D:", "pdf", "report.html");
                    try {
                        Path pdf = Paths.get("D:", "pdf", out);
                        //转成pdf
                        phantomJSExecutionResponse = PhantomUtils.html2pdf("weekMonthPDF.html", dataModel, pdf);
                        //获取渲染后的 网页源码
//                        phantomJSExecutionResponse = PhantomUtils.htmlEmail("weekMonthPDF.html", dataModel,reportDate);
                    } catch (IOException | TemplateException e) {
                        e.printStackTrace();
                    }
                    System.out.println(out + phantomJSExecutionResponse.getStdErr());
                    System.out.println(out + phantomJSExecutionResponse.getStdOut());
                    System.out.println(out + phantomJSExecutionResponse.getExitCode());
                }
            });
        }
        long b = System.currentTimeMillis();
        runnables.forEach(new Consumer<Runnable>() {
            @Override
            public void accept(Runnable runnable) {
                executorService.execute(runnable);
            }
        });
        executorService.shutdown();
        while (true) {
            if (executorService.isTerminated()) {
                long now = System.currentTimeMillis();
                System.out.println("结束了");
                System.out.println((now - b) / 1000);
                break;
            }
        }


    }

    /**
     * Abstract logging class.
     */
    private static abstract class LoggerOutputStream extends LogOutputStream {

        protected final StringBuffer messageContents;


        LoggerOutputStream() {
            messageContents = new StringBuffer();
        }


        String getMessageContents() {
            return messageContents.toString();
        }
    }

    /**
     * Info level logger.
     */
    private static class InfoLoggerOutputStream extends LoggerOutputStream {

        @Override
        protected void processLine(final String s, final int i) {
            messageContents.append(s).append(System.lineSeparator());
        }
    }

    /**
     * Error level logger.
     */
    private static class ErrorLoggerOutputStream extends LoggerOutputStream {

        @Override
        protected void processLine(final String s, final int i) {
            messageContents.append(s).append(System.lineSeparator());
        }
    }

}
