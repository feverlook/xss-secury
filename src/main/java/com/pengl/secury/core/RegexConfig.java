package com.pengl.secury.core;

import org.apache.commons.lang3.StringUtils;
import java.io.Serializable;
/**
 * @ClassName: RegexConfig
 * @Author: pengl
 * @Date: 2018/3/9 0017 14:29
 * @Description: 正则匹配模型
 * @Version: 1.0
 * 骚年 加行注释 哈哈
 **/
public class RegexConfig implements Serializable{

    private String id;
    private String value;
    private String desc;
    private String flag;

    public RegexConfig(String id, String value, String desc, String flag) {
        this.id = id;
        this.value = value;
        this.desc = desc;
        this.flag = flag;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getFlag() {
        return StringUtils.isBlank(flag)? "true" : flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }
}
