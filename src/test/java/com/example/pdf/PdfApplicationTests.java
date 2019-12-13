package com.example.pdf;

import com.example.pdf.utils.OperatingSystem;
import com.example.pdf.utils.PhantomJSExecutionResponse;
import com.example.pdf.utils.PhantomUtils;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import io.woo.htmltopdf.HtmlToPdf;
import io.woo.htmltopdf.HtmlToPdfObject;
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
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

@SpringBootTest
class PdfApplicationTests {
    static final Path TEMP_DIR = Paths.get(System.getProperty("java.io.tmpdir", "/tmp"));
    @Test
    void contextLoads() throws IOException, TemplateException {
        // 第一步：创建一个Configuration对象，直接new一个对象。构造方法的参数就是freemarker对于的版本号。
        Configuration configuration = new Configuration(Configuration.getVersion());
        ClassPathResource classPathResource = new ClassPathResource("templates");
        File tt = classPathResource.getFile();
        System.out.println(tt.getAbsolutePath());
        // 第二步：设置模板文件所在的路径。
        configuration.setDirectoryForTemplateLoading(classPathResource.getFile());
        // 第三步：设置模板文件使用的字符集。一般就是utf-8.
        configuration.setDefaultEncoding("utf-8");
        // 第四步：加载一个模板，创建一个模板对象。
        Template template = configuration.getTemplate("one.html");
        // 第五步：创建一个模板使用的数据集，可以是pojo也可以是map。一般是Map。
        Map<String, Object> dataModel = new HashMap<>();
        One one = new One();
        one.setAge("19");
        one.setName("www");
        //向数据集中添加数据
        dataModel.put("po", one);
        dataModel.put("one", "outsite");
        // 第六步：创建一个Writer对象，一般创建一FileWriter对象，指定生成的文件名。
        Writer out = new StringWriter();
        Path tempHtml = TEMP_DIR.resolve("temp.html");
        out = new FileWriter(tempHtml.toFile());
        // 第七步：调用模板对象的process方法输出文件。
        template.process(dataModel, out);
        // 第八步：关闭流。
        out.close();
//        String html = out.toString();

  /*      boolean success = HtmlToPdf.create()
                .object(HtmlToPdfObject.forUrl("https://blog.csdn.net/accountwcx/article/details/46787603"))
                .convert("D:/pdf/file.pdf");
        System.out.println(success);*/
//        System.out.println(html);

/*        System.setProperty("webdriver.chrome.driver", "D:\\driver\\chromedriver.exe");
        ChromeDriver driver = new ChromeDriver();
        File file = new File("D://temp.html");
        driver.get("file:///" + file.getAbsolutePath());
//        driver.get("https://blog.csdn.net/qq_34190023/article/details/82999702");
        new WebDriverWait(driver, 20).until(
                webDriver -> ((JavascriptExecutor) webDriver).executeScript("return document.readyState").equals("complete"));
        String source = driver.getPageSource();
        System.out.println(source);
        driver.close();
        ConverterProperties properties = new ConverterProperties();
        HtmlConverter.convertToPdf(source, new PdfWriter("D://three.pdf"), properties);*/

        OperatingSystem.OS os = OperatingSystem.get();
        String phantomPath = "bin".concat(File.separator).concat("phantomjs");
        switch (os) {
            case WINDOWS:
                phantomPath = phantomPath.concat(".exe");
                break;
            case UNIX:
                break;
            case MAC:
                throw new RuntimeException("not  support MAC");
            default:
        }
        ClassPathResource phantomjs = new ClassPathResource(phantomPath);
        File phantomjsFile = phantomjs.getFile();
        ClassPathResource html2pdfJs = new ClassPathResource("static".concat(File.separator).concat("html2pdf.js"));
        File html2pdfFile = html2pdfJs.getFile();
        CommandLine commandLine = new CommandLine(phantomjsFile);
        commandLine.addArgument("${js}");
        commandLine.addArgument("${input}");
        commandLine.addArgument("${output}");
        final Map<String, Object> args = new HashMap<>();
        args.put("js", html2pdfFile);
        args.put("input", tempHtml.toUri().toString());//暂定
        args.put("output", "暂定");
        commandLine.setSubstitutionMap(args);

        final InfoLoggerOutputStream stdOutLogger = new InfoLoggerOutputStream();
        final ErrorLoggerOutputStream stdErrLogger = new ErrorLoggerOutputStream();
        int code;
        final DefaultExecutor de = new DefaultExecutor();
        de.setStreamHandler(new PumpStreamHandler(stdOutLogger, stdErrLogger));
        try {
            //阻塞的线程
            code = de.execute(commandLine);
        } catch (final ExecuteException ex) {
            code = ex.getExitValue();
        }

        System.out.println("输出流 " + stdOutLogger.getMessageContents());
/*        String separator = File.separator;
        File file = new File("D://temp.html");
        String path = System.getProperty("user.dir");
        StringBuilder basePath = new StringBuilder(path);
        basePath.append(separator).append("src").append(separator)
                .append("main").append(separator).append("resources").append(separator);

//        String phamtomjs = path + "\\src\\main\\resources\\bin\\phantomjs.exe";
        String phamtomjs = basePath + "bin" + separator + "phantomjs.exe";
//        String html2pdfjs = path + "\\src\\main\\resources\\static\\html2pdf.js";
        String html2pdfjs = path + "\\src\\main\\resources\\static\\html2pdf.js";
        //执行phantomjs 生成js
        Runtime rt = Runtime.getRuntime();
        Process p = rt.exec(phamtomjs + " " + html2pdfjs + " file:///" + file.getAbsolutePath());
        InputStream is = p.getInputStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        StringBuffer sbf = new StringBuffer();
        String tmp = "";
        while ((tmp = br.readLine()) != null) {
            sbf.append(tmp);
        }
        String resultstr = sbf.toString();
        String[] arr = resultstr.split("\\$");
        String result = "";
        for (String s : arr) {
            if (s.endsWith("pdf")) result = s;
        }*/

    }

    @Test
    void two() throws IOException, TemplateException {
        Map<String, Object> dataModel = new HashMap<>();
        One one = new One();
        one.setAge("19");
        one.setName("www");
        //向数据集中添加数据
        dataModel.put("po", one);
        dataModel.put("one", "outsite");
        ExecutorService executorService = Executors.newScheduledThreadPool(20);
        List<Runnable> runnables = new ArrayList<>();
        for (int i = 0; i <1 ; i++) {
            runnables.add(new Runnable() {
                @Override
                public void run() {
                    PhantomJSExecutionResponse phantomJSExecutionResponse = null;
                    String out = "two" + UUID.randomUUID().toString().substring(16) + ".pdf";
                    try {
                        phantomJSExecutionResponse = PhantomUtils.html2pdf("jiankongDay.html", dataModel, Paths.get("D:", "pdf", out));
                    } catch (IOException | TemplateException e) {
                        e.printStackTrace();
                    }
                    System.out.println(out+phantomJSExecutionResponse.getStdErr());
                    System.out.println(out+phantomJSExecutionResponse.getStdOut());
                    System.out.println(out+phantomJSExecutionResponse.getExitCode());
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
        while (true){
            if (executorService.isTerminated()){
                long now = System.currentTimeMillis();
                System.out.println("结束了");
                System.out.println((now-b)/1000);
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
