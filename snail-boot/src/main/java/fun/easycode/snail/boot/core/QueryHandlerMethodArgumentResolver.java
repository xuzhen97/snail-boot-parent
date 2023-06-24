package fun.easycode.snail.boot.core;

import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * 查询参数解析器
 * @author xuzhen97
 */
public class QueryHandlerMethodArgumentResolver implements HandlerMethodArgumentResolver {
    @Override
    public boolean supportsParameter(MethodParameter methodParameter) {
        return methodParameter.hasParameterAnnotation(QueryParam.class);
    }

    @Override
    public Object resolveArgument(MethodParameter methodParameter, ModelAndViewContainer modelAndViewContainer, NativeWebRequest nativeWebRequest, WebDataBinderFactory webDataBinderFactory) throws Exception {
        QueryParam queryParam = methodParameter.getParameterAnnotation(QueryParam.class);

        String name = "".equalsIgnoreCase(queryParam.name()) ? queryParam.value() : queryParam.name();

        if ("".equalsIgnoreCase(name)) {
            name = methodParameter.getParameter().getName();
        }

        String value = nativeWebRequest.getParameter(name);

        if (value == null) {
            return null;
        }

        return QueryDslParser.parser(value, queryParam.root()).build();
    }
}