package com.xxl.job.admin.service;

import com.xxl.job.admin.model.XxlJobUserGroups;
import org.springframework.transaction.annotation.Transactional;

public interface XxlJobUserService {
    @Transactional(rollbackFor = Exception.class)
    void saveUserPermission(XxlJobUserGroups xxlJobUserGroups);
}
