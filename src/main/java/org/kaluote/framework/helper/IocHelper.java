package org.kaluote.framework.helper;


import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.kaluote.framework.annotation.Inject;
import org.kaluote.framework.util.ReflectionUtil;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * @author ljn
 * @date 2018/6/5.
 * 依赖注入助手类
 */
public class IocHelper {

    static {
        Map<Class<?>, Object> beanMap = BeanHelper.getBeanMap();
        if (MapUtils.isNotEmpty(beanMap)) {
            for (Map.Entry<Class<?>,Object> entry : beanMap.entrySet()) {
                Class<?> beanClass = entry.getKey();
                Object beanInstance = entry.getValue();
                //获取Bean类定义的所有成员变量
                Field[] beanFields = beanClass.getDeclaredFields();
                if (ArrayUtils.isNotEmpty(beanFields)) {
                    for (Field field : beanFields) {
                        //判断当前BeanField是否带有@Inject注解
                        if (field.isAnnotationPresent(Inject.class)) {
                            //在beanMap中获取BeanField对应的实例
                            Class<?> fieldClass = field.getType();
                            Object fieldInstance = beanMap.get(fieldClass);
                            if (fieldInstance != null) {
                                //通过反射初始化field的值
                                ReflectionUtil.setField(beanInstance,field,fieldInstance);
                            }
                        }
                    }
                }
            }
        }
    }
}
