package com.pengl.secury.core;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @ClassName: XSSFilter
 * @Author: pengl
 * @Date: 2018/3/9 0014 10:30
 * @Description: XSS安全过滤器
 * @Version: 1.0
 **/
public class XSSFilter implements Filter {
	private static final Logger LOGGER = LoggerFactory.getLogger(XSSFilter.class);

	/**
	 * @MethodName: init
	 * @Author: pengl
	 * @Date: 2018/5/14 0014 10:29
	 * @Description: 初始化读取配置文件
	 * @Version: 1.0
	 * @Param: 
	 * @Return: 
	 **/
	@Override
	public void init(FilterConfig arg0) throws ServletException {
		String xss_config = arg0.getInitParameter("xss-config");
		if (StringUtils.isBlank(xss_config)){
			xss_config = "com/doone/ah/secury/config/xss-config.xml";
		}
		if(XssConfig.initConfig(xss_config)){
			LOGGER.info("========>>>XSSFilter加载配置文件完成...");
		}
	}

	@Override
	public void destroy() {
		XssConfig.destroyConfig();
	}

	/**
	 * @MethodName: doFilter
	 * @Author: pengl
	 * @Date: 2018/5/14 0014 10:30
	 * @Description: 安全过滤主体方法
	 * @Version: 1.0
	 * @Param: 
	 * @Return: 
	 **/
	@Override
	public void doFilter(ServletRequest arg0, ServletResponse arg1,
			FilterChain arg2) throws IOException, ServletException {

		HttpServletRequest req = (HttpServletRequest) arg0;
		HttpServletResponse res = (HttpServletResponse) arg1;

		/**
		 * 读取不到配置文件，直接放行
		 */
		if (RuleCheck.xssConfig == null) {
			arg2.doFilter(req, res);
			return;
		}
		
		/**
		 * host验证
		 */
		RuleCheckResult ruleCheckResult = RuleCheck.checkHost(req);
		if(ruleCheckResult.isFlag()){
			LOGGER.error(ruleCheckResult.getMessage());
			res.sendError(403);
			return;
		}
		/**
		 * 请求方式过滤
		 */
		ruleCheckResult = RuleCheck.checkMethod(req);
		if(ruleCheckResult.isFlag()){
			LOGGER.error(ruleCheckResult.getMessage());
			res.sendError(405);
			return;
		}

		/**
		 *  URL检查（svn文件泄漏）
		 */
		ruleCheckResult = RuleCheck.checkURL(req);
		if(ruleCheckResult.isFlag()){
			LOGGER.error(ruleCheckResult.getMessage());
			res.sendError(404);
			return;
		}

		/**
		 * 验证当前请求的地址是否为不进行XSS过滤的地址
		 */
		if(RuleCheck.checkNotFilterURL(req)){
			arg2.doFilter(req, res);
			return;
		}


		/**
		 * Http Header验证
		 */
		ruleCheckResult = RuleCheck.checkHeader(req);
		if(ruleCheckResult.isFlag()){
			LOGGER.error(ruleCheckResult.getMessage());
			res.sendError(400);
			return;
		}

		/**
		 * Paramer 请求参数验证
		 */
		ruleCheckResult = RuleCheck.checkParamers(req);
		if(ruleCheckResult.isFlag()){
			LOGGER.error(ruleCheckResult.getMessage());
			res.sendError(400);
			return;
		}

		arg2.doFilter(req, res);
	}

}