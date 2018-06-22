package org.kaluote.framework.util;

import org.apache.commons.lang3.StringUtils;

/**
 * @author ljn
 * @date 2018/6/5.
 */
public class CastUtil {

    /**
     * 转为String型
     * @param obj
     * @return
     */
    public static String castString(Object obj){
        return CastUtil.castString(obj,"");
    }

    /**
     * 转为String型(提供默认值)
     * @param obj
     * @param defaultValue
     * @return
     */
    public static String castString(Object obj,String defaultValue){
        return obj!=null?String .valueOf(obj):defaultValue;
    }

    /**
     * 转为double型
     * @param obj
     * @return
     */
    public static double castDouble(Object obj){
        return CastUtil.castDouble(obj,0);
    }

    /**
     * 转为Double型(提供默认值)
     * @param obj
     * @param defaultValue
     * @return
     */
    public static double castDouble(Object obj,double defaultValue){
        double douvleValue=defaultValue;
        if (obj!=null){
            String strValue=castString(obj);
            if (StringUtils.isNotBlank(strValue)){
                try{
                    douvleValue=Double.parseDouble(strValue);
                }catch (NumberFormatException e){
                    douvleValue=defaultValue;
                }
            }
        }
        return douvleValue;
    }

    /**
     * 转为long型
     * @param obj
     * @return
     */
    public static long castLong(Object obj){
        return CastUtil.castLong(obj,0);
    }

    /**
     * 转为long型(提供默认值)
     * @param obj
     * @param defaultValue
     * @return
     */
    public static long castLong(Object obj,long defaultValue){
        long longValue=defaultValue;
        if(obj!=null){
            String strValue=castString(obj);
            if (StringUtils.isNotBlank(strValue)){
                try {
                    longValue=Long.parseLong(strValue);
                }catch (NumberFormatException e){
                    longValue=defaultValue;
                }
            }
        }
        return longValue;
    }

    /**
     * 转为int型
     * @param obj
     * @return
     */
    public static int castInt(Object obj){
        return CastUtil.castInt(obj,0);
    }

    /**
     * 转为int型(提供默认值)
     * @param obj
     * @param defaultValue
     * @return
     */
    public static int castInt(Object obj,int defaultValue){
        int intValue=defaultValue;
        if(obj!=null){
            String strValue=castString(obj);
            if (StringUtils.isNotBlank(strValue)){
                try {
                    intValue=Integer.parseInt(strValue);
                }catch (NumberFormatException e){
                    intValue=defaultValue;
                }
            }
        }
        return  intValue;
    }

    /**
     * 转为boolean型
     * @param obj
     * @return
     */
    public static  boolean castBoolean(Object obj){
        return CastUtil.castBoolean(obj,false);
    }

    /**
     * 转为boolean型(提供默认值
     * @param obj
     * @param defaultValue
     * @return
     */
    public static boolean castBoolean(Object obj,boolean defaultValue){
        boolean booleanValue=defaultValue;
        if (obj!=null){
            booleanValue=Boolean.parseBoolean(castString(obj));
        }
        return booleanValue;
    }
}
