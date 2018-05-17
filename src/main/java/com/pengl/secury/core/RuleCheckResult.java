package com.pengl.secury.core;

import java.io.Serializable;
/**
 * @ClassName: RuleCheckResult
 * @Author: pengl
 * @Date: 2018/3/9 0017 14:29
 * @Description: 校验结果模型
 * @Version: 1.0
 **/
public class RuleCheckResult implements Serializable{
    private boolean flag;
    private String ruleid;
    private String message;
    private String returnValue;
    private String groupstr;

    public RuleCheckResult(){

    }

    public RuleCheckResult(boolean flag, String message) {
        this.flag = flag;
        this.message = message;
    }

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public String getRuleid() {
        return ruleid;
    }

    public void setRuleid(String ruleid) {
        this.ruleid = ruleid;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getReturnValue() {
        return returnValue;
    }

    public void setReturnValue(String returnValue) {
        this.returnValue = returnValue;
    }

    public String getGroupstr() {
        return groupstr;
    }

    public void setGroupstr(String groupstr) {
        this.groupstr = groupstr;
    }
}
