package com.pengl.secury.core;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @ClassName: RuleCheck
 * @Author: pengl
 * @Date: 2018/3/9 0017 14:29
 * @Description: 规则校验
 * @Version: 1.0
 **/
public class RuleCheck {
    private static final Logger LOGGER = LoggerFactory.getLogger(RuleCheck.class);
    public static XssConfig xssConfig;
    /**
     * Method_name: checkHost
     * Param: HttpServletRequest req, XssConfig xssConfig
     * Describe: HOST验证
     * Creat_user: pengl
     * Creat_date: 2018/5/9 0009
     * Creat_time: 14:33
     **/
    public static RuleCheckResult checkHost(HttpServletRequest req){
        Enumeration hostEnumeration = req.getHeaders("HOST");
        while(hostEnumeration.hasMoreElements()){
            String h = hostEnumeration.nextElement() + "";
            RuleConfig hostWiteRule = xssConfig.getRuleConfig("host_white");
            if(!RuleCheck.checkContains(hostWiteRule, h)){
                RuleCheckResult ret = new RuleCheckResult();
                ret.setFlag(true);
                ret.setRuleid("host_white");
                ret.setMessage("HOST验证不通过，匹配规则：host_white" + "，匹配字符：" + h);
                ret.setGroupstr(h);
                return ret;
            }
        }
        return new RuleCheckResult(false , "HOST验证通过");
    }
    
    /**
     * Method_name: checkMethod
     * Param: HttpServletRequest req, XssConfig xssConfig
     * Describe: 检查请求类型
     * Creat_user: pengl
     * Creat_date: 2018/5/9 0009
     * Creat_time: 14:43
     **/
    public static RuleCheckResult checkMethod(HttpServletRequest req){
        String reqMethod = req.getMethod();
        RuleConfig reqTypeRule = xssConfig.getRuleConfig("req_method");
        if(!RuleCheck.checkContains(reqTypeRule, reqMethod)){
            RuleCheckResult ret = new RuleCheckResult();
            ret.setFlag(true);
            ret.setRuleid("req_method");
            ret.setMessage("Http请求方法验证不通过，匹配规则：req_method" + "，匹配字符：" + reqMethod);
            ret.setGroupstr(reqMethod);
            return ret;
        }
        return new RuleCheckResult(false , "REQ_METHOD验证通过");
    }

    public static RuleCheckResult checkURL(HttpServletRequest req){
        String reqUrl = req.getRequestURL().toString();
        RuleCheckResult ruleCheckResult = checkRegexMatch(xssConfig.getRuleConfig("xss_svn"), reqUrl);
        if(ruleCheckResult.isFlag()){
            return ruleCheckResult;
        }
        return new RuleCheckResult(false , "URL验证通过");
    }

    /**
     * Method_name: checkNotFilterURL
     * Param: 
     * Describe: 判断当前URL是否不需要过滤
     * Creat_user: pengl
     * Creat_date: 2018/5/9 0009
     * Creat_time: 15:10
     **/
    public static boolean checkNotFilterURL(HttpServletRequest req){
        String uri = req.getRequestURI();
        String ext = null;
        int idx = uri.lastIndexOf(".");
        if (idx > 0) {
            ext = uri.substring(idx + 1);
        }
        String[] includeExts = xssConfig.getIncludeExt().split(",");
        /**
         * 有后缀，且后缀不属于配置文件中指定的，则放行
         */
        if(StringUtils.isNotBlank(ext) && !Arrays.asList(includeExts).contains(ext)){
            return true;
        }
        String path = req.getServletPath();
        List<String> excludeUrls = xssConfig.getExcludeUrls();
        if(ArrayUtils.isNotEmpty(excludeUrls.toArray())){
            for (String url : excludeUrls) {
                if (StringUtils.isNotBlank(url)) {
                    Pattern pattern = Pattern.compile(url, 2);
                    Matcher matcher = pattern.matcher(path);
                    if (matcher.matches()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    /**
     * Method_name: checkHeader
     * Param: 
     * Describe: Header头部检测
     * Creat_user: pengl
     * Creat_date: 2018/5/10 0010
     * Creat_time: 11:46
     **/
    public static RuleCheckResult checkHeader(HttpServletRequest req){
        RuleCheckResult ruleCheckResult = new RuleCheckResult(true, "Header验证通过");
        Enumeration headers = req.getHeaderNames();
        while (headers.hasMoreElements()) {
            String h = (String) headers.nextElement();
            if (StringUtils.isBlank(h)) {
                break;
            }
            String v = req.getHeader(h);
            ruleCheckResult = xssCheck(xssConfig, v);
        }

        return ruleCheckResult;
    }
    
    /**
     * @MethodName: checkParamers
     * @Author: pengl
     * @Date: 2018/5/14 0014 9:55
     * @Description: 检查请求参数
     * @Version: 1.0
     * @Param: HttpServletRequest req, XssConfig xssConfig
     * @Return: RuleCheckResult
     **/
    public static RuleCheckResult checkParamers(HttpServletRequest req){
        RuleCheckResult ruleCheckResult = new RuleCheckResult(true, "Paramers验证通过");
        if ("GET".equalsIgnoreCase(req.getMethod())) {
            ruleCheckResult = xssCheck(xssConfig, req.getQueryString());
            if(ruleCheckResult.isFlag()){
                return ruleCheckResult;
            }
        }else{
            Enumeration en = req.getParameterNames();
            String name = null;
            String value = null;
            while (en.hasMoreElements()) {
                name = (String) en.nextElement();
                if (StringUtils.isBlank(name)) {
                    break;
                }
                value = req.getParameter(name);
                name = name.toLowerCase();
                ruleCheckResult = xssCheck(xssConfig, name);
                if(ruleCheckResult.isFlag()){
                    return ruleCheckResult;
                }
                ruleCheckResult = xssCheck(xssConfig, value);
                if(ruleCheckResult.isFlag()){
                    return ruleCheckResult;
                }
            }
        }
        return ruleCheckResult;
    }

    /**
     * @MethodName: xssCheck
     * @Author: pengl
     * @Date: 2018/5/14 0014 10:01
     * @Description: 跨站脚本攻击检查
     * @Version: 1.0
     * @Param: 
     * @Return: 
     **/
    private static RuleCheckResult xssCheck(XssConfig xssConfig, String v){
        RuleCheckResult ruleCheckResult;
        ruleCheckResult = checkRegexMatch(xssConfig.getRuleConfig("xss_script"), v);
        if(ruleCheckResult.isFlag()){
            return ruleCheckResult;
        }
        ruleCheckResult = checkRegexMatch(xssConfig.getRuleConfig("xss_event"), v);
        if(ruleCheckResult.isFlag()){
            return ruleCheckResult;
        }
        ruleCheckResult = checkRegexMatch(xssConfig.getRuleConfig("xss_sql"), v);
        if(ruleCheckResult.isFlag()){
            return ruleCheckResult;
        }
        ruleCheckResult = checkRegexMatch(xssConfig.getRuleConfig("xss_crlf"), v);
        if(ruleCheckResult.isFlag()){
            return ruleCheckResult;
        }

        return ruleCheckResult;
    }

    /**
     * @MethodName: checkContains
     * @Author: pengl
     * @Date: 2018/5/14 0014 9:50
     * @Description: 检查是否包含某些规则
     * @Version: 1.0
     * @Param: RuleConfig ruleConfig, String str
     * @Return: boolean
     **/
    private static boolean checkContains(RuleConfig ruleConfig, String str){
        boolean flag = false;
        if("true".equals(ruleConfig.getFlag())){
            Iterator<RegexConfig> iterator = ruleConfig.getRegexs().iterator();
            while (iterator.hasNext()) {
                RegexConfig r = iterator.next();
                if("true".equalsIgnoreCase(r.getFlag()) && str.equalsIgnoreCase(r.getValue())){
                    flag = true;
                    break;
                }
            }
        }else{
            //不开启不拦截
            flag = true;
        }
        return flag;
    }

    private static RuleCheckResult checkRegexMatch(RuleConfig ruleConfig, String str){
        if(StringUtils.isBlank(str) || ruleConfig == null || ruleConfig.getRegexs().size() == 0){
            return new RuleCheckResult(false, "验证参数为空或规则为空");
        }

        String c = null;
        if (StringUtils.isNotBlank(ruleConfig.getEncode())) {
            try {
                c = new String(str.getBytes(ruleConfig.getEncode()));
            } catch (UnsupportedEncodingException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }

        if (StringUtils.isBlank(c)) {
            c = str;
        }

        boolean bCheck = true;
        if (StringUtils.isNotBlank(ruleConfig.getFlag())) {
            Pattern p = Pattern.compile(ruleConfig.getFlag(), 66);
            Matcher m = p.matcher(c);
            if (!m.find()) {
                bCheck = false;
            }
        }

        if (bCheck) {
            Iterator<RegexConfig> regex = ruleConfig.getRegexs().iterator();
            while (regex.hasNext()) {
                RegexConfig r =  regex.next();
                if (r == null) {
                    break;
                }
                Pattern p = Pattern.compile(r.getValue(), 66);
                Matcher m = p.matcher(c);
                if (m.find()) {
                    RuleCheckResult ret = new RuleCheckResult();
                    ret.setFlag(true);
                    ret.setRuleid(r.getId());
                    ret.setMessage("Invalid String:" + c + "，匹配规则：" + r.getId() + "，匹配字符：" + m.group());
                    ret.setGroupstr(m.group());
                    return ret;
                }
            }
        }

        return new RuleCheckResult(false, "不匹配主体规则，跳过验证");
    }
}
