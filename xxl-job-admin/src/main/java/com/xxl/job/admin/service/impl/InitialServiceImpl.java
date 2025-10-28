package com.xxl.job.admin.service.impl;

import com.github.hbq969.code.common.initial.AbstractScriptInitialAware;
import com.github.hbq969.code.common.spring.context.SpringContext;
import com.github.hbq969.code.common.spring.i18n.LanguageEvent;
import com.github.hbq969.code.common.utils.InitScriptUtils;
import com.github.hbq969.code.common.utils.ThrowUtils;
import com.github.hbq969.code.dict.service.api.impl.MapDictHelperImpl;
import com.github.hbq969.code.sm.login.service.LoginService;
import com.xxl.job.admin.mapper.XxlJobUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
public class InitialServiceImpl extends AbstractScriptInitialAware {

    @Autowired
    private SpringContext context;

    @Autowired
    private LoginService loginService;

    @Autowired
    private XxlJobUserMapper xxlJobUserMapper;

    @Override
    protected void tableCreate0() {
        ThrowUtils.call("创建h_xxl_job_user_p表成功", "h_xxl_job_user_p表已存在", () -> xxlJobUserMapper.createXxlJobUserPermission());
    }

    @Override
    protected void scriptInitial0() {
        InitScriptUtils.initial(context, "initial-xxl-job.sql", StandardCharsets.UTF_8, null, () -> {
            loginService.loadSMInfo();
            context.getBean(MapDictHelperImpl.class).reloadImmediately();
        });
    }

    @Override
    public String nameOfScriptInitialAware() {
        return "xxl-job-admin";
    }

    @Override
    public void onApplicationEvent(LanguageEvent event) {
        throw new UnsupportedOperationException();
    }
}
