package fun.easycode.snail.boot.util;

import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * URL处理工具类
 * @author xuzhe
 */
public class URLUtil {

    /**
     * 获取当前请求url 不包含Host和端口以及协议仅
     * /list?name=zhangsan
     * 可以过滤query
     * @param excludeQuery
     * @return
     */
    public static String getBaseURL(String... excludeQuery){
        HttpServletRequest request = RequestHolder.getRequest();
        assert request != null;
        String queryString  = request.getQueryString() != null
                ? request.getQueryString():"";

        StringBuilder newQueryStringBuilder = new StringBuilder();

        String[] params = queryString.split("&");

        List<String> excludeQueryList = Arrays.asList(excludeQuery);

        for (String param : params) {
            if(excludeQueryList.stream().noneMatch(q -> param.startsWith(q+"="))
                    && !StringUtils.isEmpty(param)){
                newQueryStringBuilder.append("&").append(param);
            }
        }

        String url = request.getContextPath() + request.getRequestURI() ;
        if(newQueryStringBuilder.length()>0){
            url+= "?" + newQueryStringBuilder.substring(1);
        }
        return url;
    }

    /**
     * 获取url上面的query参数
     *  返回会进行解码
     * @param key
     * @return
     */
    public static String getQueryParam(String key){
        HttpServletRequest request = RequestHolder.getRequest();
        assert request != null;
        String queryString  = request.getQueryString() != null
                ? request.getQueryString():"";
        String[] params = queryString.split("&");
        for (String param : params) {
            String[] keyValue = param.split("=");
            if(keyValue.length > 1 && Objects.equals(key, keyValue[0])){
                return decodeUrl(keyValue[1]);
            }
        }
        return null;
    }

    /**
     * 获取当前请求完整URL
     */
    public static String getURL() {
        HttpServletRequest request = RequestHolder.getRequest();
        assert request != null;
        return request.getScheme() + "://"
                + request.getServerName()
                + (request.getServerPort() == 80 ? "" : ":" + request.getServerPort())
                + request.getContextPath()
                + request.getRequestURI()
                + ("".equals(request.getQueryString()) ? "" : "?" + request.getQueryString());
    }

    /**
     * url编码
     * @param url url
     * @return encode url
     */
    public static String encodeUrl(String url){
        try {
            return URLEncoder.encode(url, "UTF-8");
        }catch (UnsupportedEncodingException e){
            throw new RuntimeException(e);
        }
    }

    /**
     * url解码
     * @param url encode url
     * @return String
     */
    public static String decodeUrl(String url){
        try {
            return URLDecoder.decode( url, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

}
