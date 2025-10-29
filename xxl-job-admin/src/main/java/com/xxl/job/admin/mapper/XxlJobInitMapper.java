package com.xxl.job.admin.mapper;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface XxlJobInitMapper {
    void createJobGroup();

    void createJobRegistry();

    void createJobInfo();

    void createJobLogglue();

    void createJobLog();

    void createJobLogReport();

    void createJobLock();

    void createJobUser();
}
