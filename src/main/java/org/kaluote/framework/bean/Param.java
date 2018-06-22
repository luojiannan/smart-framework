package org.kaluote.framework.bean;

import org.kaluote.framework.util.CastUtil;

import java.util.Map;

/**
 * @author ljn
 * @date 2018/6/5.
 * 请求参数对象
 */
public class Param {

    private Map<String,Object> map ;

    public Param(Map<String,Object> map) {
        this.map = map;
    }

    /**
     * 根据参数名获取long型参数值
     * @param name
     * @return
     */
    public long getLong(String name) {
        return CastUtil.castLong(map.get(name));
    }

    /**
     * 获取所有字段信息
     * @return
     */
    public Map<String,Object> getMap() {
        return map;
    }
}
