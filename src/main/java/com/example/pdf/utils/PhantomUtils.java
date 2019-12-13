package com.example.pdf.utils;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.apache.commons.exec.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @Author: plani
 * 创建时间: 2019/11/20 11:38
 */
public class PhantomUtils {
    private final static Logger logger = LoggerFactory.getLogger(PhantomUtils.class);
    static final Path TEMP_DIR = Paths.get(System.getProperty("java.io.tmpdir", "/tmp")).resolve("html");

    /**
     * @param templateName
     * @param dataModel
     * @param outPdf
     * @return
     * @throws IOException
     * @throws TemplateException
     */
    public static PhantomJSExecutionResponse html2pdf(String templateName, Map<String, Object> dataModel, Path outPdf) throws IOException, TemplateException {
        if (!Files.exists(TEMP_DIR)) {
            Files.createDirectory(TEMP_DIR);
        }
        if (templateName == null || dataModel == null || outPdf == null) {
            throw new NullPointerException("参数为空");
        }
        // 第一步：创建一个Configuration对象，直接new一个对象。构造方法的参数就是freemarker对于的版本号。
        Configuration configuration = new Configuration(Configuration.getVersion());
        //拿到resource文件下的文件
        ClassPathResource classPathResource = new ClassPathResource("templates");
        ClassPathResource basePath = new ClassPathResource("static");
        // 第二步：设置模板文件所在的路径。
        configuration.setDirectoryForTemplateLoading(classPathResource.getFile());
        // 第三步：设置模板文件使用的字符集。一般就是utf-8.
        configuration.setDefaultEncoding("utf-8");
        // 第四步：加载一个模板，创建一个模板对象。
        Template template = configuration.getTemplate(templateName);
        // 第五步：创建一个模板使用的数据集，可以是pojo也可以是map。一般是Map。
        //pass
        // 第六步：创建一个Writer对象，一般创建一FileWriter对象，指定生成的文件名。
        final String renderId = getRenderId();
        Path tempHtml = TEMP_DIR.resolve("temp" + renderId + ".html");
        Writer out = new FileWriter(tempHtml.toFile());
        // 第七步：调用模板对象的process方法输出文件。
        dataModel.put("basePath", basePath.getFile().toURI().toString());
        template.process(dataModel, out);
        // 第八步：关闭流。
        out.close();
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
        ClassPathResource html2pdfJs = new ClassPathResource("static".concat(File.separator).concat("defaultrender.js"));
        File html2pdfFile = html2pdfJs.getFile();
        CommandLine commandLine = new CommandLine(phantomjsFile);
        commandLine.addArgument("${js}");
        //这里开始 添加 js里面的参数
        commandLine.addArgument("${sourcePath}");
        commandLine.addArgument("${targetPath}");
        commandLine.addArgument("${operatingSystem}");
        final Map<String, Object> args = new HashMap<>();
        args.put("js", html2pdfFile);
        args.put("sourcePath", tempHtml.toFile());
        args.put("targetPath", outPdf.toFile());
        args.put("operatingSystem", OperatingSystem.get().name());
        commandLine.setSubstitutionMap(args);
        //传递给js的参数 顺序 为 去掉 phantomjs 和 renderjs 后的参数，即前两个 不算参数
        logger.info("Running command :[{}]", commandLine);
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
        } finally {
            logger.info(tempHtml.toString());
            //删除临时文件
//            Files.deleteIfExists(tempHtml);
        }
        return new PhantomJSExecutionResponse(code, stdOutLogger.getMessageContents(), stdErrLogger.getMessageContents());
    }



    private static synchronized String getRenderId() {
        return UUID.randomUUID().toString();
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
