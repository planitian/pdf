package com.example.pdf;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: plani
 * 创建时间: 2019/11/15 15:52
 */
public class LocalTest {

    public static void main(String[] args) throws IOException, TemplateException {
        Long l = 99L;
        BigDecimal bigDecimal = new BigDecimal(l);
        bigDecimal = bigDecimal.divide(new BigDecimal("10000"), 2, RoundingMode.DOWN);
        System.out.println(bigDecimal);

    }
}
