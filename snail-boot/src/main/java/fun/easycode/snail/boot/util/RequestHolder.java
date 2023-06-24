package fun.easycode.snail.boot.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * RequestHolder
 * @author xuzhe
 */
@Slf4j
public class RequestHolder {

    /**
     * 获取request
     *
     * @return HttpServletRequest
     */
    public static HttpServletRequest getRequest() {
        if(log.isDebugEnabled()){
            log.debug("getRequest -- Thread id :{}, name : {}"
                    , Thread.currentThread().getId(), Thread.currentThread().getName());
        }
        ServletRequestAttributes servletRequestAttributes = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes());
        if (null == servletRequestAttributes) {
            return null;
        }
        return servletRequestAttributes.getRequest();
    }

    /**
     * 获取Response
     *
     * @return HttpServletRequest
     */
    public static HttpServletResponse getResponse() {
        if(log.isDebugEnabled()){
            log.debug("getResponse -- Thread id :{}, name : {}"
                    , Thread.currentThread().getId(), Thread.currentThread().getName());
        }
        ServletRequestAttributes servletRequestAttributes = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes());
        if (null == servletRequestAttributes) {
            return null;
        }
        return servletRequestAttributes.getResponse();
    }

    /**
     * 获取session
     *
     * @return HttpSession
     */
    public static HttpSession getSession() {
        if(log.isDebugEnabled()){
            log.debug("getSession -- Thread id :{}, name : {}"
                    , Thread.currentThread().getId(), Thread.currentThread().getName());
        }
        HttpServletRequest request = null;
        if (null == (request = getRequest())) {
            return null;
        }
        return request.getSession();
    }

    /**
     * 获取session的Attribute
     *
     * @param name session的key
     * @return Object
     */
    public static Object getSession(String name) {
        if(log.isDebugEnabled()){
            log.debug("getSession -- Thread id :{}, name : {}"
                    , Thread.currentThread().getId(), Thread.currentThread().getName());
        }
        ServletRequestAttributes servletRequestAttributes = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes());
        if (null == servletRequestAttributes) {
            return null;
        }
        return servletRequestAttributes.getAttribute(name, RequestAttributes.SCOPE_SESSION);
    }

    /**
     * 添加session
     *
     * @param name String
     * @param value Object
     */
    public static void setSession(String name, Object value) {
        if(log.isDebugEnabled()){
            log.debug("setSession -- Thread id :{}, name : {}"
                    , Thread.currentThread().getId(), Thread.currentThread().getName());
        }
        ServletRequestAttributes servletRequestAttributes = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes());
        if (null == servletRequestAttributes) {
            return;
        }
        servletRequestAttributes.setAttribute(name, value, RequestAttributes.SCOPE_SESSION);
    }

    /**
     * 清除指定session
     *
     * @param name String
     */
    public static void removeSession(String name) {
        if(log.isDebugEnabled()){
            log.debug("removeSession -- Thread id :{}, name : {}"
                    , Thread.currentThread().getId(), Thread.currentThread().getName());
        }
        ServletRequestAttributes servletRequestAttributes = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes());
        if (null == servletRequestAttributes) {
            return;
        }
        servletRequestAttributes.removeAttribute(name, RequestAttributes.SCOPE_SESSION);
    }

    /**
     * 获取所有session key
     *
     * @return String[]
     */
    public static String[] getSessionKeys() {
        if(log.isDebugEnabled()){
            log.debug("getSessionKeys -- Thread id :{}, name : {}"
                    , Thread.currentThread().getId(), Thread.currentThread().getName());
        }
        ServletRequestAttributes servletRequestAttributes = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes());
        if (null == servletRequestAttributes) {
            return null;
        }
        return servletRequestAttributes.getAttributeNames(RequestAttributes.SCOPE_SESSION);
    }
}
