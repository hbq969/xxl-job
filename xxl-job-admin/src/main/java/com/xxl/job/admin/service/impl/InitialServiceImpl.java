package com.xxl.job.admin.service.impl;

import cn.hutool.core.util.StrUtil;
import com.github.hbq969.code.common.initial.AbstractScriptInitialAware;
import com.github.hbq969.code.common.spring.context.SpringContext;
import com.github.hbq969.code.common.spring.i18n.LangInfo;
import com.github.hbq969.code.common.spring.i18n.LanguageEvent;
import com.github.hbq969.code.common.utils.InitScriptUtils;
import com.github.hbq969.code.common.utils.StrUtils;
import com.github.hbq969.code.common.utils.ThrowUtils;
import com.github.hbq969.code.dict.service.api.impl.MapDictHelperImpl;
import com.github.hbq969.code.sm.config.LoginConfig;
import com.github.hbq969.code.sm.login.service.LoginService;
import com.github.hbq969.code.sm.login.utils.I18nUtils;
import com.google.common.collect.ImmutableMap;
import com.xxl.job.admin.mapper.XxlJobInitMapper;
import com.xxl.job.admin.mapper.XxlJobUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Map;

@Component
public class InitialServiceImpl extends AbstractScriptInitialAware {

    @Autowired
    private SpringContext context;

    @Autowired
    private LoginService loginService;

    @Autowired
    private XxlJobUserMapper xxlJobUserMapper;

    @Autowired
    private XxlJobInitMapper initMapper;

    @Override
    protected void tableCreate0() {
        ThrowUtils.call("创建h_xxl_job_user_p表成功", "h_xxl_job_user_p表已存在", () -> xxlJobUserMapper.createXxlJobUserPermission());
        ThrowUtils.call("创建xxl_job_group表成功", "xxl_job_group表已存在", () -> initMapper.createJobGroup());
        ThrowUtils.call("创建xxl_job_registry表成功", "xxl_job_registry表已存在", () -> initMapper.createJobRegistry());
        ThrowUtils.call("创建xxl_job_info表成功", "xxl_job_info表已存在", () -> initMapper.createJobInfo());
        ThrowUtils.call("创建xxl_job_logglue表成功", "xxl_job_logglue表已存在", () -> initMapper.createJobLogglue());
        ThrowUtils.call("创建xxl_job_log表成功", "xxl_job_log表已存在", () -> initMapper.createJobLog());
        ThrowUtils.call("创建xxl_job_log_report表成功", "xxl_job_log_report表已存在", () -> initMapper.createJobLogReport());
        ThrowUtils.call("创建xxl_job_lock表成功", "xxl_job_lock表已存在", () -> initMapper.createJobLock());
        ThrowUtils.call("创建xxl_job_user表成功", "xxl_job_user表已存在", () -> initMapper.createJobUser());
    }

    @Override
    protected void scriptInitial0() {
        String lang = I18nUtils.getFullLanguage(context);
        Map map = ImmutableMap.of("menuPrefix", context.getBean(LoginConfig.class).getMenu().getPrefix());
        InitScriptUtils.initial(context, StrUtil.format("initial-xxl-job-{}.sql", lang), StandardCharsets.UTF_8,
                (sql) -> StrUtils.replacePlaceHolders(sql, map),
                () -> {
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
        LangInfo info = (LangInfo) event.getSource();
        Map map = ImmutableMap.of("menuPrefix", context.getBean(LoginConfig.class).getMenu().getPrefix());
        InitScriptUtils.initial(context, info.filename("initial-xxl-job", "sql"), StandardCharsets.UTF_8,
                (sql) -> StrUtils.replacePlaceHolders(sql, map),
                () -> {
                    loginService.loadSMInfo();
                    context.getBean(MapDictHelperImpl.class).reloadImmediately();
                });
    }
}
