package org.kaluote.framework;

import org.kaluote.framework.helper.*;
import org.kaluote.framework.util.ClassUtil;

/**
 * @author ljn
 * @date 2018/6/5.
 * 加载相应的helper类
 */
public class HelperLoader {

    public static void init() {
        Class<?>[] classList = {ClassHelper.class, BeanHelper.class, AopHelper.class, IocHelper.class, ControllerHelper.class};
        for (Class<?> cls : classList) {
            ClassUtil.loadClass(cls.getName(),true);
        }
    }
}
