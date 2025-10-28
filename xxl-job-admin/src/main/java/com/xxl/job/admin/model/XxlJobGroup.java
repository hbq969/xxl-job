package com.xxl.job.admin.model;

import com.github.hbq969.code.common.spring.context.SpringContext;
import com.github.hbq969.code.common.utils.FormatTime;
import com.github.hbq969.code.dict.service.api.DictAware;
import com.github.hbq969.code.dict.service.api.DictModel;
import com.github.hbq969.code.dict.service.api.Td;
import com.xxl.tool.core.StringTool;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by xuxueli on 16/9/30.
 */
public class XxlJobGroup implements DictModel, DictAware {

    private int id;
    private String appname;
    private String title;
    @Td(fmtFieldName = "fmtAddressType", dictName = "xxl-job-admin,addressType")
    private int addressType;        // 执行器地址类型：0=自动注册、1=手动录入
    @Setter
    @Getter
    private String fmtAddressType;
    private String addressList;     // 执行器地址列表，多地址逗号分隔(手动录入)
    private Date updateTime;
    @Setter
    @Getter
    private String fmtUpdateTime;

    // registry list
    private List<String> registryList;  // 执行器地址列表(系统注册)

    @Override
    public void convertDict(SpringContext context) {
        DictAware.super.convertDict(context);
        if (updateTime != null)
            this.fmtUpdateTime = FormatTime.YYYYMMDDHHMISS.withMills(this.updateTime.getTime());
    }

    public List<String> getRegistryList() {
        if (StringTool.isNotBlank(addressList)) {
            registryList = new ArrayList<>(Arrays.asList(addressList.split(",")));
        }
        return registryList;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAppname() {
        return appname;
    }

    public void setAppname(String appname) {
        this.appname = appname;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getAddressType() {
        return addressType;
    }

    public void setAddressType(int addressType) {
        this.addressType = addressType;
    }

    public String getAddressList() {
        return addressList;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public void setAddressList(String addressList) {
        this.addressList = addressList;
    }

}
