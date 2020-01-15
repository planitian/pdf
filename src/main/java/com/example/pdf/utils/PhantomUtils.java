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
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @Author: plani
 * 创建时间: 2019/11/20 11:38
 */
public class PhantomUtils {

    private final static Logger logger = LoggerFactory.getLogger(PhantomUtils.class);
    static final Path TEMP_DIR = Paths.get(System.getProperty("java.io.tmpdir", "tmp")).resolve("html");
    static final Path PHA = phatomBinPath();
    static final Path DEFAULTREBDERJS = TEMP_DIR.resolve("defaultrender.js");
    static final Path EMAILJS = TEMP_DIR.resolve("defaultrenderEmail.js");

    static {
        try {
            initializeBinaries();
        } catch (IOException e) {
            e.printStackTrace();
        }
        initializeShutDownHook();
    }

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
            Files.createDirectories(TEMP_DIR);
        }
        if (templateName == null || dataModel == null || outPdf == null) {
            throw new NullPointerException("参数为空");
        }
        // 第一步：创建一个Configuration对象，直接new一个对象。构造方法的参数就是freemarker对于的版本号。
        Configuration configuration = new Configuration(Configuration.getVersion());
        // 第二步：设置模板文件所在的路径。
        configuration.setClassForTemplateLoading(PhantomUtils.class, "/templates");
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
        template.process(dataModel, out);
        // 第八步：关闭流。
        out.close();
        File phantomjsFile = PHA.toFile();
        //js 文件
        File html2pdfFile = DEFAULTREBDERJS.toFile();
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
            logger.error("删除临时文件 " + tempHtml);
            //删除临时文件
            Files.deleteIfExists(tempHtml);
            logger.error("删除临时文件 成功 " + tempHtml);
            logger.error("设置 输出文件 权限");
            FileUtils.changePermission(outPdf);
        }
        return new PhantomJSExecutionResponse(code, stdOutLogger.getMessageContents(), stdErrLogger.getMessageContents());
    }

    public static PhantomJSExecutionResponse htmlEmail(String templateName, Map<String, Object> dataModel, Path reportPath) throws IOException, TemplateException {
        if (!Files.exists(TEMP_DIR)) {
            Files.createDirectories(TEMP_DIR);
        }
        if (templateName == null || dataModel == null || reportPath == null) {
            throw new NullPointerException("参数为空");
        }
        // 第一步：创建一个Configuration对象，直接new一个对象。构造方法的参数就是freemarker对于的版本号。
        Configuration configuration = new Configuration(Configuration.getVersion());
        // 第二步：设置模板文件所在的路径。
        configuration.setClassForTemplateLoading(PhantomUtils.class, "/templates");
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

        template.process(dataModel, out);
        // 第八步：关闭流。
        out.close();

        File phantomjsFile = PHA.toFile();
        File html2pdfFile = EMAILJS.toFile();
        logger.error("phantomjsFile Path" + phantomjsFile.getAbsolutePath());
        CommandLine commandLine = new CommandLine(phantomjsFile);
        commandLine.addArgument(" --web-security=false");

        commandLine.addArgument("${js}");
        //这里开始 添加 js里面的参数
        commandLine.addArgument("${sourcePath}");
        commandLine.addArgument("${operatingSystem}");
        commandLine.addArgument("${reportPath}");
        final Map<String, Object> args = new HashMap<>();
        args.put("js", html2pdfFile);
        args.put("sourcePath", tempHtml.toFile());
        args.put("operatingSystem", OperatingSystem.get().name());
        args.put("reportPath", reportPath.toFile());
        commandLine.setSubstitutionMap(args);
        //传递给js的参数 顺序 为 去掉 phantomjs 和 renderjs 后的参数，即前两个 不算参数
        logger.error("Running command :[{}]", commandLine);
        logger.error("executable " + commandLine.toString());
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
            logger.error("删除临时文件 " + tempHtml);
            //删除临时文件
            Files.deleteIfExists(tempHtml);
            logger.error("删除临时文件 成功 " + tempHtml);
            logger.error("设置 输出文件 权限");
            FileUtils.changePermission(reportPath);
        }
        return new PhantomJSExecutionResponse(code, stdOutLogger.getMessageContents(), stdErrLogger.getMessageContents());
    }

    private static synchronized String getRenderId() {
        return UUID.randomUUID().toString();
    }

    private static Path phatomBinPath() {
        OperatingSystem.OS os = OperatingSystem.get();
        String phantomPath = "phantomjs";
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
        Path path = TEMP_DIR.resolve(phantomPath);
        return path;

    }

    private static void initializeBinaries() throws IllegalStateException, IOException {
        OperatingSystem.OS os = OperatingSystem.get();
        String phantomPath = "bin".concat(File.separator).concat("phantomjs");
        switch (os) {
            case WINDOWS:
                phantomPath = phantomPath.concat(".exe");
                break;
            case UNIX:
                phantomPath = phantomPath.concat(".bin");
                break;
            case MAC:
                throw new RuntimeException("not  support MAC");
            default:
        }
        logger.error("pha path " + PHA);
        logger.error("phantomPath " + phantomPath);
        //创建父目录目录
        Files.createDirectories(TEMP_DIR);
        logger.error("创建父目录目录 ");

        ClassPathResource phantomjs = new ClassPathResource(phantomPath);
        logger.error("复制文件 " + PHA);
        try {
            Files.copy(phantomjs.getInputStream(), PHA, StandardCopyOption.REPLACE_EXISTING);
            FileUtils.changePermission(PHA);//设置权限
        } catch (Exception e) {
            logger.error("复制 PHA 出错" + e.toString());
        }
        ClassPathResource emailjs = new ClassPathResource("static".concat(File.separator).concat("defaultrenderEmail.js"));
        logger.error("复制文件 " + EMAILJS);
        try {
            Files.copy(emailjs.getInputStream(), EMAILJS, StandardCopyOption.REPLACE_EXISTING);
            FileUtils.changePermission(EMAILJS);//设置权限
        } catch (Exception e) {
            logger.error("复制 EMAILJS 出错" + e.toString());
        }

        ClassPathResource html2pdfJs = new ClassPathResource("static".concat(File.separator).concat("defaultrender.js"));
        logger.error("复制文件 " + DEFAULTREBDERJS);
        try {
            Files.copy(html2pdfJs.getInputStream(), DEFAULTREBDERJS, StandardCopyOption.REPLACE_EXISTING);
            FileUtils.changePermission(DEFAULTREBDERJS);//设置权限
        } catch (Exception e) {
            logger.error("复制 DEFAULTREBDERJS 出错" + e.toString());
        }


    }

    /**
     * Shutdown hook in charge of cleaning of JVM specific folders during JVM
     * shutdown.
     * This hook needs to be added during initialization of the class.
     */
    private static void initializeShutDownHook() {
        final Runtime runtime = Runtime.getRuntime();

        final Thread shutdownThread = new Thread("phantomjs") {

            @Override
            public void run() {
                deleteTempDir(DEFAULTREBDERJS);
                deleteTempDir(EMAILJS);
                deleteTempDir(PHA);
            }

            private void deleteTempDir(Path path) {
                try {
                    Files.deleteIfExists(path);
                } catch (final Exception e) {
                    logger.warn("PhantomJSSetup was unable to clean up temporary directory [{}]. Caused by: ", path, e);
                }
            }
        };

        runtime.addShutdownHook(shutdownThread);
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
