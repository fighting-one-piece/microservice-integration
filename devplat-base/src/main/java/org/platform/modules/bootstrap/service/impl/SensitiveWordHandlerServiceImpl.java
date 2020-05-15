package org.platform.modules.bootstrap.service.impl;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.platform.modules.abstr.entity.ResultCode;
import org.platform.utils.exception.BusinessException;
import org.platform.utils.redis.RedisClusterUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

@Order(1)
@Service("sensitiveWordHandlerService")
public class SensitiveWordHandlerServiceImpl extends AbstractHandlerChainServiceImpl {

	@Override
	public Object[] preHandle(HttpServletRequest request) throws BusinessException {
		String path = request.getServletPath();
		if (path.startsWith("/api/v")) path = path.replaceAll("/api/v.", "");
		try {
			judgeSensitiveWord(request.getParameterMap());
		} catch (BusinessException be) {
			LOG.error(be.getMessage(), be);
			return new Object[]{false, be};
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			return new Object[]{false, new BusinessException(e.getMessage())};
		}
		return new Object[]{ true };
	}
	
	@SuppressWarnings("unchecked")
	private void judgeSensitiveWord(Map<String, String[]> paramMap) throws BusinessException {
    	for (Map.Entry<String, String[]> entry : paramMap.entrySet()) {
    		Object arg = entry.getValue()[0];
			if (arg instanceof Map) {
				Map<String, Object> argMap = (Map<String, Object>) arg;
				for (Map.Entry<String, Object> argEntry : argMap.entrySet()) {
					judgeSensitiveWord(argEntry.getValue());
				}
			} else {
				judgeSensitiveWord(arg);
			}
    	}
	}
    
    @SuppressWarnings({ "unchecked", "unused" })
	private void judgeSensitiveWord(Object[] args) throws BusinessException {
		if (null != args && args.length != 0) {
			for (int i = 0, len = args.length; i < len; i++) {
				Object arg = args[i];
				if (arg instanceof Map) {
					Map<String, Object> map = (Map<String, Object>) arg;
					for (Map.Entry<String, Object> entry : map.entrySet()) {
						judgeSensitiveWord(entry.getValue());
					}
				} else {
					judgeSensitiveWord(arg);
				}
			}
		}
	}
	
	private void judgeSensitiveWord(Object arg) throws BusinessException {
		if (arg instanceof String) {
			String queryTxt = String.valueOf(arg);
			if(queryTxt.indexOf(":") != -1) {
				String[] args = queryTxt.split(",");
				for(String word : args){
					String[] keywords = word.split(":");
					for (int i = 0, len = keywords.length; i < len; i++) {
						if (RedisClusterUtils.getInstance().sismember("sensitive_word", keywords[i])) {
							throw new BusinessException(ResultCode.PARAM_ERROR);
						}
					}
				}
			} else {
				String[] keywords = queryTxt.indexOf(" ") == -1 ? new String[]{queryTxt} : queryTxt.split(" ");
				for (int i = 0, len = keywords.length; i < len; i++) {
					if (RedisClusterUtils.getInstance().sismember("sensitive_word", keywords[i])) {
						throw new BusinessException(ResultCode.PARAM_ERROR);
					}
				}
			}
			
		}
	}
}
