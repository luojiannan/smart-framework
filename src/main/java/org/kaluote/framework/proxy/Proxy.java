package org.kaluote.framework.proxy;

/**
 * @author ljn
 * @date 2018/6/7.
 * 代理接口
 */
public interface Proxy {

    /**
     * 执行链式代理
     * @param proxyChain
     * @return
     * @throws Throwable
     */
    Object doProxy(ProxyChain proxyChain) throws Throwable;
}
