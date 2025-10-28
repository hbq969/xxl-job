package com.xxl.job.admin.model;

import lombok.Data;

import java.util.List;

@Data
public class XxlJobUserGroups {
    private String userName;
    private List<Integer> groupIds;
}
