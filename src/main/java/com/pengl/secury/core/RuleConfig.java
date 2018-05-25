package com.pengl.secury.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName: RuleConfig
 * @Author: pengl
 * @Date: 2018/3/9 0017 14:29
 * @Description: 规则模型
 * @Version: 1.0
 * 我就是来加个注释 骚年不要紧张 哈哈
 **/
public class RuleConfig implements Serializable{

    private String id;
    private String flag;
    private String encode;
    private String desc;
    private List<RegexConfig> regexs = new ArrayList<>();

    public RuleConfig(String id, String flag, String encode, String desc) {
        this.id = id;
        this.flag = flag;
        this.encode = encode;
        this.desc = desc;
    }

    public void addRegex(RegexConfig regexConfig){
        this.regexs.add(regexConfig);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public String getEncode() {
        return encode;
    }

    public void setEncode(String encode) {
        this.encode = encode;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public List<RegexConfig> getRegexs() {
        return regexs;
    }

    public void setRegexs(List<RegexConfig> regexs) {
        this.regexs = regexs;
    }
}
