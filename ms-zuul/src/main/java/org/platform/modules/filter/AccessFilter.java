package org.platform.modules.filter;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.platform.modules.oauth.IOAuthService;
import org.platform.utils.json.GsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;

public class AccessFilter extends ZuulFilter  {
	
    private static Logger LOG = LoggerFactory.getLogger(AccessFilter.class);
    
    @Autowired
    private IOAuthService oauthService = null;
    
    /**
     * 返回一个字符串代表过滤器的类型，在zuul中定义了四种不同生命周期的过滤器类型，具体如下：
	   pre：可以在请求被路由之前调用
       routing：在路由请求时候被调用
       post：在routing和error过滤器之后被调用
       error：处理请求时发生错误时被调用
     */
    @Override
    public String filterType() {
        return "pre";
    }
    
    /** 
     * 通过int值来定义过滤器的执行顺序
     */
    @Override
    public int filterOrder() {
        return 0;
    }
    
    /**
     * 返回一个boolean类型来判断该过滤器是否要执行，所以通过此函数可实现过滤器的开关。
     */
    @Override
    public boolean shouldFilter() {
        return false;
    }
    
    /**
     * 过滤器的具体逻辑。
     * 通过ctx.setSendZuulResponse(false)令zuul过滤该请求，不对其进行路由，
     * 然后通过ctx.setResponseStatusCode(401)设置了其返回的错误码，
     * 当然也可以进一步优化返回，比如通过ctx.setResponseBody(body)对返回body内容进行编辑等。
     */
    @Override
    public Object run() {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        String accessToken = request.getHeader("accessToken");
        if(null != accessToken && !"".equals(accessToken)) {
        	LOG.info(String.format("request %s token %s", request.getRequestURL().toString(), accessToken));
        	//Authorization:Basic Y2lzaW9uZGF0YTpjaXNpb25kYXRh
        	Map<String, Object> result = oauthService.checkToken("Basic Y2lzaW9uZGF0YTpjaXNpb25kYXRh", accessToken);
            if (result.containsKey("error")) {
            	ctx.setSendZuulResponse(false);
            	ctx.setResponseStatusCode(401);
            	ctx.setResponseBody(GsonUtils.fromMapToJson(result));
            }
        }
        return null;
    }

}
