package org.kaluote.framework;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.kaluote.framework.bean.Data;
import org.kaluote.framework.bean.Handler;
import org.kaluote.framework.bean.Param;
import org.kaluote.framework.bean.View;
import org.kaluote.framework.helper.BeanHelper;
import org.kaluote.framework.helper.ConfigHelper;
import org.kaluote.framework.helper.ControllerHelper;
import org.kaluote.framework.util.CodecUtil;
import org.kaluote.framework.util.JsonUtil;
import org.kaluote.framework.util.ReflectionUtil;
import org.kaluote.framework.util.StreamUtil;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * @author ljn
 * @date 2018/6/5.
 * 请求转发器
 */
@WebServlet(urlPatterns = "/*",loadOnStartup = 0)
public class DispatcherServlet extends HttpServlet {

    @Override
    public void init(ServletConfig config) throws ServletException {
        //初始化Helper类
        HelperLoader.init();
        //获取servletContext对象（用于注册servlet）
        ServletContext servletContext = config.getServletContext();
        //注册处理JSP的Servlet
        ServletRegistration jspServlet = servletContext.getServletRegistration("jsp");
        jspServlet.addMapping(ConfigHelper.getAppJspPath() + "*");
        //注册处理静态资源的默认servlet
        ServletRegistration defaultServlet = servletContext.getServletRegistration("default");
        defaultServlet.addMapping(ConfigHelper.getAppAssetPath() + "*");
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //获取请求方法与请求路径
        String requestMethod = req.getMethod().toLowerCase();
        String requestPath = req.getPathInfo();
        //获取Action处理器
        Handler handler = ControllerHelper.getHandler(requestMethod, requestPath);
        if (handler != null) {
            //获取Controller类及其bean实例
            Class<?> controllerClass = handler.getControllerClass();
            Object controllerBean = BeanHelper.getBean(controllerClass);
            //创建请求参数对象
            HashMap<String, Object> map = new HashMap<String, Object>();
            Enumeration<String> parameterNames = req.getParameterNames();
            while(parameterNames.hasMoreElements()) {
                String paramName = parameterNames.nextElement();
                String paramValue = req.getParameter(paramName);
                map.put(paramName,paramValue);
            }
            String body = CodecUtil.decodeURL(StreamUtil.getString(req.getInputStream()));
            if (StringUtils.isNotEmpty(body)) {
                String[] array = body.split("=");
                if (ArrayUtils.isNotEmpty(array)) {
                    String paramName = array[0];
                    String paramValue = array[1];
                    map.put(paramName,paramValue);
                }
            }
            Param param = new Param(map);
            //调用Action方法
            Method actionMethod = handler.getActionMethod();
            Object result;
            if (MapUtils.isEmpty(map)) {
                result = ReflectionUtil.invokeMethod(controllerBean, actionMethod);
            } else {
                result = ReflectionUtil.invokeMethod(controllerBean, actionMethod, param);
            }
            //处理action方法返回值
            if (result instanceof View) {
                //返回JSP页面
                View view = (View)result;
                String path = view.getPath();
                if (StringUtils.isNotBlank(path)) {
                    if (path.startsWith("/")) {
                        //重定向
                        resp.sendRedirect(req.getContextPath() + path);
                    }else {
                        Map<String, Object> model = view.getModel();
                        for (Map.Entry<String,Object> entry : model.entrySet()) {
                            req.setAttribute(entry.getKey(),entry.getValue());
                        }
                        req.getRequestDispatcher(ConfigHelper.getAppJspPath() + path).forward(req,resp);
                    }
                }
            } else if (result instanceof Data) {
                //返回JSON数据
                Data data = (Data)result;
                Object model = data.getModel();
                if (model != null) {
                    resp.setContentType("application/json");
                    resp.setCharacterEncoding("UTF-8");
                    PrintWriter writer = resp.getWriter();
                    String json = JsonUtil.toJson(model);
                    writer.write(json);
                    writer.flush();
                    writer.close();
                }
            }

        }
    }
}
