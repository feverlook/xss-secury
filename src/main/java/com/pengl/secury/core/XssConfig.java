package com.pengl.secury.core;

import com.pengl.secury.tools.XmlParse;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
/**
 * @ClassName: XssConfig
 * @Author: pengl
 * @Date: 2018/3/9 0017 14:30
 * @Description: 配置模型
 * @Version: 1.0
 **/
public class XssConfig implements Serializable{
    private static final Logger LOGGER = LoggerFactory.getLogger(XssConfig.class);
    private List<String> excludeUrls = new ArrayList<>();
    private List<RuleConfig> rules = new ArrayList<>();
    private String includeExt;

    /**
     * Method_name: initConfig
     * Param: 
     * Describe: 初始化XSS-CONFIG配置
     * Creat_user: pengl
     * Creat_date: 2018/5/9 0009
     * Creat_time: 10:35
     **/
    public static boolean initConfig(String xssConfigPath){
        LOGGER.error("======>>>开始读取XSS-CONFIG配置文件，路径：{}", xssConfigPath);
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        InputStream is = cl.getResourceAsStream(xssConfigPath);
        if(is == null){
            LOGGER.error("======>>>初始化XSS-CONFIG配置文件失败，加载DOC输入流为null....");
            return false;
        }
        XssConfig xssConfig = new XssConfig();
        try {
            Document doc = XmlParse.loadXmlStream(is);
            if (doc == null) {
                LOGGER.error("======>>>初始化XSS-CONFIG配置文件失败，加载DOC为null....");
                return false;
            }
            Element root = doc.getRootElement();
            List rules = root.elements("rule");
            int length;
            if(rules == null || ((length=rules.size()) == 0)){
                LOGGER.error("======>>>XSS-CONFIG配置文件中未配置规则....");
                return false;
            }
            for (int i = 0; i < length; i++) {
                Element r = (Element) rules.get(i);
                String id = r.attributeValue("id");
                String flag = r.attributeValue("flag");
                String encoding = r.attributeValue("encoding");
                String desc = r.attributeValue("desc");
                RuleConfig ruleConfig = new RuleConfig(id, flag, encoding, desc);

                List regexs = r.elements();
                for (int j = 0; j < regexs.size(); j++) {
                    Element e = (Element) regexs.get(j);
                    String eid = e.attributeValue("id");
                    String evalue = e.attributeValue("value");
                    String edesc = e.attributeValue("desc");
                    String eflag = e.attributeValue("flag");
                    RegexConfig regex = new RegexConfig(eid, evalue, edesc, eflag);
                    ruleConfig.addRegex(regex);
                }
                xssConfig.addRule(ruleConfig);
            }

            List excludeUrls = root.elements("exclude-url");
            if(excludeUrls != null || excludeUrls.size() > 0){
                for (int i = 0; i < length; i++) {
                    Element r = (Element) rules.get(i);
                    xssConfig.addExcludeUrl(r.getTextTrim());
                }
            }

            String includeExt = root.elementTextTrim("include-ext");
            xssConfig.setIncludeExt(includeExt);

        } catch (Exception e) {
            LOGGER.error("======>>>初始化XSS-CONFIG配置文件失败，异常信息：" + e.getMessage(), e);
            return false;
        }
        RuleCheck.xssConfig = xssConfig;
        return true;
    }

    public static void destroyConfig(){
        RuleCheck.xssConfig = null;
    }

    public RuleConfig getRuleConfig(String id){
        if(StringUtils.isBlank(id)){
            return null;
        }
        if(this.rules.size() == 0){
            return null;
        }
        for(RuleConfig r : this.rules){
            if(id.equalsIgnoreCase(r.getId())){
                return r;
            }
        }
        return null;
    }

    public void addRule(RuleConfig ruleConfig){
        this.rules.add(ruleConfig);
    }

    public void addExcludeUrl(String url){
        this.excludeUrls.add(url);
    }

    public List<String> getExcludeUrls() {
        return excludeUrls;
    }

    public void setExcludeUrls(List<String> excludeUrls) {
        this.excludeUrls = excludeUrls;
    }

    public List<RuleConfig> getRules() {
        return rules;
    }

    public void setRules(List<RuleConfig> rules) {
        this.rules = rules;
    }

    public String getIncludeExt() {
        return includeExt;
    }

    public void setIncludeExt(String includeExt) {
        this.includeExt = includeExt;
    }
}
