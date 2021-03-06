package org.kaluote.framework.util;


import org.apache.commons.lang3.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author ljn
 * @date 2018/6/1.
 */
public class PropsUtil {

    private static final Logger logger = LoggerFactory.getLogger(PropsUtil.class);

    /**
     * 加载属性文件
     * @param fileName
     * @return
     */
    public static Properties loadProps(String fileName) {
        Properties props = null;
        InputStream is = null;
        try {
            is = Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName);
            if (is == null) {
                throw new FileNotFoundException(fileName + "file is not found");
            }
            props = new Properties();
            props.load(is);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("load properties file failure",e);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    logger.error("close input stream failure",e);
                }
            }
        }
        return props;
    }

    /**
     * 获取字符型属性(默认值为空字符串)
     * @param props
     * @param key
     * @return
     */
    public static String getString (Properties props,String key) {
        return getString(props,key,"");
    }

    public static String getString(Properties props,String key,String defaultValue) {
        String value = defaultValue;
        if (props.containsKey(key)) {
            value = props.getProperty(key);
        }
        return value;
    }

    /**
     * 获取整数型属性
     * @param props
     * @param key
     * @param defaultValue
     * @return
     */
    public static int getInt(Properties props,String key,int defaultValue) {
        int value = defaultValue;
        if (props.containsKey(key)) {
            value = Integer.parseInt(props.getProperty(key));
        }
        return value;
    }

    public static boolean getBoolean(Properties props,String key,Boolean defaultValue) {
        boolean value = defaultValue;
        if (props.containsKey(key)) {
            value = BooleanUtils.toBoolean(props.getProperty(key));
        }
        return value;
    }

}
