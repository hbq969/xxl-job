package com.xxl.job.admin.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import com.xxl.job.admin.mapper.XxlJobUserMapper;
import com.xxl.job.admin.model.XxlJobUserGroups;
import com.xxl.job.admin.service.XxlJobUserService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class XxlJobUserServiceImpl implements XxlJobUserService {

    @Resource
    private XxlJobUserMapper xxlJobUserMapper;

    @Override
    public void saveUserPermission(XxlJobUserGroups xxlJobUserGroups) {
        Assert.notNull(xxlJobUserGroups.getUserName(), "userName is null");
        if (CollectionUtil.isEmpty(xxlJobUserGroups.getGroupIds()))
            throw new IllegalArgumentException("groupIds is empty");
        xxlJobUserMapper.deleteUserPermission(xxlJobUserGroups.getUserName());
        for (Integer groupId : xxlJobUserGroups.getGroupIds()) {
            if (groupId != null && StrUtil.isNotEmpty(xxlJobUserGroups.getUserName()))
                xxlJobUserMapper.saveUserPermission(xxlJobUserGroups.getUserName(), groupId);
        }
    }
}
