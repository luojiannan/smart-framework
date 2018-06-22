package org.kaluote.framework.bean;

/**
 * @author ljn
 * @date 2018/6/5.
 * 返回数据对象
 */
public class Data {
    /**
     * 模型数据
     */
    private Object model;

    public Data(Object model) {
        this.model = model;
    }

    public Object getModel() {
        return model;
    }
}
