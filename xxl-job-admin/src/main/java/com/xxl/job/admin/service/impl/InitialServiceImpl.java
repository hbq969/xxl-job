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
        ThrowUtils.runWithCreateTab(() -> xxlJobUserMapper.createXxlJobUserPermission(), "h_xxl_job_user_p");
        ThrowUtils.runWithCreateTab(() -> initMapper.createJobGroup(), "xxl_job_group");
        ThrowUtils.runWithCreateTab(() -> initMapper.createJobRegistry(), "xxl_job_registry");
        ThrowUtils.runWithCreateTab(() -> initMapper.createJobInfo(), "xxl_job_info");
        ThrowUtils.runWithCreateTab(() -> initMapper.createJobLogglue(), "xxl_job_logglue");
        ThrowUtils.runWithCreateTab(() -> initMapper.createJobLog(), "xxl_job_log");
        ThrowUtils.runWithCreateTab(() -> initMapper.createJobLogReport(), "xxl_job_log_report");
        ThrowUtils.runWithCreateTab(() -> initMapper.createJobLock(), "xxl_job_lock");
        ThrowUtils.runWithCreateTab(() -> initMapper.createJobUser(), "xxl_job_user");
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
