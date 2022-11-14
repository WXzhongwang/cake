package com.cake.framework.log.appender;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy;
import ch.qos.logback.core.util.FileSize;
import org.slf4j.LoggerFactory;

/**
 * TODO
 *
 * @author zhongshengwang
 * @description TODO
 * @date 2022/10/28 23:28
 * @email 18668485565163.com
 */
public class CakeLogBackAppenderBuilder {
    /**
     * 文件大小
     */
    private static final FileSize FILE_SIZE = FileSize.valueOf("5gb");

    public static void initRollingFileAppender(String name, String filePath, String fileNamePattern,
                                               String encodePattern, FileSize fileSize, int maxHistory) {
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        Logger logger = context.getLogger(name);

        // 不会集成ROOT
        logger.setAdditive(false);

        RollingFileAppender rollingFile = new RollingFileAppender();
        rollingFile.setContext(context);
        rollingFile.setName(name);

        rollingFile.setFile(filePath);
        rollingFile.setAppend(true);

        SizeAndTimeBasedRollingPolicy sizeAndTimeBasedRollingPolicy = new SizeAndTimeBasedRollingPolicy();
        sizeAndTimeBasedRollingPolicy.setMaxFileSize(fileSize);
        sizeAndTimeBasedRollingPolicy.setTotalSizeCap(FILE_SIZE);
        sizeAndTimeBasedRollingPolicy.setFileNamePattern(fileNamePattern);
        sizeAndTimeBasedRollingPolicy.setParent(rollingFile);
        sizeAndTimeBasedRollingPolicy.setContext(context);
        sizeAndTimeBasedRollingPolicy.setMaxHistory(maxHistory);
        sizeAndTimeBasedRollingPolicy.start();

        PatternLayoutEncoder encoder = new PatternLayoutEncoder();
        encoder.setContext(context);
        encoder.setPattern(encodePattern);
        encoder.start();

        rollingFile.setRollingPolicy(sizeAndTimeBasedRollingPolicy);
        rollingFile.setEncoder(encoder);
        rollingFile.start();

        logger.addAppender(rollingFile);


    }
}
