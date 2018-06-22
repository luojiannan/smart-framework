package org.kaluote.framework.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @author ljn
 * @date 2018/6/6.
 * 流操作工具
 */
public final class StreamUtil {

    private static final Logger logger = LoggerFactory.getLogger(StreamUtil.class);

    public static String getString(InputStream is) {
        StringBuilder sb = new StringBuilder();
        try{
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        }catch (Exception e) {
            logger.error("get string failure",e);
            throw new RuntimeException(e);
        }
        return sb.toString();
    }


}
