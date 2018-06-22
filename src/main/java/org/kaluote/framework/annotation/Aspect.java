package org.kaluote.framework.annotation;

import java.lang.annotation.*;

/**
 * @author ljn
 * @date 2018/6/7.
 * 切面注解
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Aspect {

    /**
     * 注解
     * @return
     */
    Class<? extends Annotation> value();


}
