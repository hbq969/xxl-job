package com.xxl.job.admin.service.impl;

import cn.hutool.core.lang.Assert;
import com.github.hbq969.code.common.restful.ReturnMessage;
import com.github.hbq969.code.common.spring.context.SpringContext;
import com.github.hbq969.code.common.utils.I18nUtils;
import com.github.hbq969.code.sm.login.session.UserContext;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xxl.job.admin.mapper.*;
import com.xxl.job.admin.model.XxlJobGroup;
import com.xxl.job.admin.model.XxlJobInfo;
import com.xxl.job.admin.model.XxlJobLogReport;
import com.xxl.job.admin.scheduler.cron.CronExpression;
import com.xxl.job.admin.scheduler.route.ExecutorRouteStrategyEnum;
import com.xxl.job.admin.scheduler.scheduler.MisfireStrategyEnum;
import com.xxl.job.admin.scheduler.scheduler.ScheduleTypeEnum;
import com.xxl.job.admin.scheduler.thread.JobScheduleHelper;
import com.xxl.job.admin.scheduler.thread.JobTriggerPoolHelper;
import com.xxl.job.admin.scheduler.trigger.TriggerTypeEnum;
import com.xxl.job.admin.service.XxlJobService;
import com.xxl.job.admin.util.I18nUtil;
import com.xxl.job.admin.util.JobGroupPermissionUtil;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.enums.ExecutorBlockStrategyEnum;
import com.xxl.job.core.glue.GlueTypeEnum;
import com.xxl.job.core.util.DateUtil;
import com.xxl.sso.core.model.LoginInfo;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.*;

/**
 * core job action for xxl-job
 *
 * @author xuxueli 2016-5-28 15:30:33
 */
@Service
public class XxlJobServiceImpl implements XxlJobService {
    private static Logger logger = LoggerFactory.getLogger(XxlJobServiceImpl.class);

    @Resource
    private XxlJobGroupMapper xxlJobGroupMapper;
    @Resource
    private XxlJobInfoMapper xxlJobInfoMapper;
    @Resource
    public XxlJobLogMapper xxlJobLogMapper;
    @Resource
    private XxlJobLogGlueMapper xxlJobLogGlueMapper;
    @Resource
    private XxlJobLogReportMapper xxlJobLogReportMapper;

    @Override
    public PageInfo<XxlJobInfo> pageList(int start, int length, Integer jobGroup, Integer triggerStatus, String jobDesc, String executorHandler, String author) {

        logger.info("+++++++>>>> ");
        PageInfo<XxlJobInfo> pg = PageHelper.startPage(start, length)
                .doSelectPageInfo(() -> xxlJobInfoMapper.pageList(start, length, jobGroup, triggerStatus, jobDesc, executorHandler, author));
        pg.getList().forEach(xi -> xi.convertDict(context));
        return pg;
    }

    @Override
    public XxlJobInfo queryJobInfo(Integer id) {
        Assert.notNull(id, "id must not be null");
        XxlJobInfo jobInfo = xxlJobInfoMapper.loadById(id);
        if (jobInfo != null)
            jobInfo.convertDict(context);
        return jobInfo;
    }

    @Override
    public ReturnMessage<String> add(XxlJobInfo jobInfo, LoginInfo loginInfo) {
        jobInfo.setAuthor(UserContext.get().getUserName());
        return doAdd(jobInfo);
    }

    private ReturnMessage<String> doAdd(XxlJobInfo jobInfo) {
        // valid base
        XxlJobGroup group = xxlJobGroupMapper.load(jobInfo.getJobGroup());
        if (group == null) {
            return ReturnMessage.fail(I18nUtil.getString("system_please_choose") + I18nUtil.getString("jobinfo_field_jobgroup"));
        }
        if (jobInfo.getJobDesc() == null || jobInfo.getJobDesc().trim().length() == 0) {
            return ReturnMessage.fail((I18nUtil.getString("system_please_input") + I18nUtil.getString("jobinfo_field_jobdesc")));
        }
        if (jobInfo.getAuthor() == null || jobInfo.getAuthor().trim().length() == 0) {
            return ReturnMessage.fail((I18nUtil.getString("system_please_input") + I18nUtil.getString("jobinfo_field_author")));
        }

        // valid trigger
        ScheduleTypeEnum scheduleTypeEnum = ScheduleTypeEnum.match(jobInfo.getScheduleType(), null);
        if (scheduleTypeEnum == null) {
            return ReturnMessage.fail((I18nUtil.getString("schedule_type") + I18nUtil.getString("system_unvalid")));
        }
        if (scheduleTypeEnum == ScheduleTypeEnum.CRON) {
            if (jobInfo.getScheduleConf() == null || !CronExpression.isValidExpression(jobInfo.getScheduleConf())) {
                return ReturnMessage.fail("Cron" + I18nUtil.getString("system_unvalid"));
            }
        } else if (scheduleTypeEnum == ScheduleTypeEnum.FIX_RATE/* || scheduleTypeEnum == ScheduleTypeEnum.FIX_DELAY*/) {
            if (jobInfo.getScheduleConf() == null) {
                return ReturnMessage.fail((I18nUtil.getString("schedule_type")));
            }
            try {
                int fixSecond = Integer.valueOf(jobInfo.getScheduleConf());
                if (fixSecond < 1) {
                    return ReturnMessage.fail((I18nUtil.getString("schedule_type") + I18nUtil.getString("system_unvalid")));
                }
            } catch (Exception e) {
                return ReturnMessage.fail((I18nUtil.getString("schedule_type") + I18nUtil.getString("system_unvalid")));
            }
        }

        // valid job
        if (GlueTypeEnum.match(jobInfo.getGlueType()) == null) {
            return ReturnMessage.fail((I18nUtil.getString("jobinfo_field_gluetype") + I18nUtil.getString("system_unvalid")));
        }
        if (GlueTypeEnum.BEAN == GlueTypeEnum.match(jobInfo.getGlueType()) && (jobInfo.getExecutorHandler() == null || jobInfo.getExecutorHandler().trim().length() == 0)) {
            return ReturnMessage.fail((I18nUtil.getString("system_please_input") + "JobHandler"));
        }
        // 》fix "\r" in shell
        if (GlueTypeEnum.GLUE_SHELL == GlueTypeEnum.match(jobInfo.getGlueType()) && jobInfo.getGlueSource() != null) {
            jobInfo.setGlueSource(jobInfo.getGlueSource().replaceAll("\r", ""));
        }

        // valid advanced
        if (ExecutorRouteStrategyEnum.match(jobInfo.getExecutorRouteStrategy(), null) == null) {
            return ReturnMessage.fail((I18nUtil.getString("jobinfo_field_executorRouteStrategy") + I18nUtil.getString("system_unvalid")));
        }
        if (MisfireStrategyEnum.match(jobInfo.getMisfireStrategy(), null) == null) {
            return ReturnMessage.fail((I18nUtil.getString("misfire_strategy") + I18nUtil.getString("system_unvalid")));
        }
        if (ExecutorBlockStrategyEnum.match(jobInfo.getExecutorBlockStrategy(), null) == null) {
            return ReturnMessage.fail((I18nUtil.getString("jobinfo_field_executorBlockStrategy") + I18nUtil.getString("system_unvalid")));
        }

        // 》ChildJobId valid
        if (jobInfo.getChildJobId() != null && jobInfo.getChildJobId().trim().length() > 0) {
            String[] childJobIds = jobInfo.getChildJobId().split(",");
            for (String childJobIdItem : childJobIds) {
                if (childJobIdItem != null && childJobIdItem.trim().length() > 0 && isNumeric(childJobIdItem)) {
                    XxlJobInfo childJobInfo = xxlJobInfoMapper.loadById(Integer.parseInt(childJobIdItem));
                    if (childJobInfo == null) {
                        return ReturnMessage.fail(
                                MessageFormat.format((I18nUtil.getString("jobinfo_field_childJobId") + "({0})" + I18nUtil.getString("system_not_found")), childJobIdItem));
                    }
                } else {
                    return ReturnMessage.fail(
                            MessageFormat.format((I18nUtil.getString("jobinfo_field_childJobId") + "({0})" + I18nUtil.getString("system_unvalid")), childJobIdItem));
                }
            }

            // join , avoid "xxx,,"
            String temp = "";
            for (String item : childJobIds) {
                temp += item + ",";
            }
            temp = temp.substring(0, temp.length() - 1);

            jobInfo.setChildJobId(temp);
        }

        // add in db
        jobInfo.setAddTime(new Date());
        jobInfo.setUpdateTime(new Date());
        jobInfo.setGlueUpdatetime(new Date());
        // remove the whitespace
        jobInfo.setExecutorHandler(jobInfo.getExecutorHandler().trim());
        xxlJobInfoMapper.save(jobInfo);
        if (jobInfo.getId() < 1) {
            return ReturnMessage.fail((I18nUtil.getString("jobinfo_field_add") + I18nUtil.getString("system_fail")));
        }

        String successMsg = I18nUtils.getMessage(context, "save.result");
        if (successMsg == null) {
            successMsg = "保存成功";
            logger.warn("save.result 未获取到国际化消息, 使用默认消息, context={}", context);
        }
        return ReturnMessage.success(successMsg);
    }

    private boolean isNumeric(String str) {
        try {
            int result = Integer.valueOf(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @Override
    public ReturnMessage<String> update(XxlJobInfo jobInfo, LoginInfo loginInfo) {

        // valid base
        if (jobInfo.getJobDesc() == null || jobInfo.getJobDesc().trim().length() == 0) {
            return ReturnMessage.fail((I18nUtil.getString("system_please_input") + I18nUtil.getString("jobinfo_field_jobdesc")));
        }
        if (jobInfo.getAuthor() == null || jobInfo.getAuthor().trim().length() == 0) {
            return ReturnMessage.fail((I18nUtil.getString("system_please_input") + I18nUtil.getString("jobinfo_field_author")));
        }

        // valid trigger
        ScheduleTypeEnum scheduleTypeEnum = ScheduleTypeEnum.match(jobInfo.getScheduleType(), null);
        if (scheduleTypeEnum == null) {
            return ReturnMessage.fail((I18nUtil.getString("schedule_type") + I18nUtil.getString("system_unvalid")));
        }
        if (scheduleTypeEnum == ScheduleTypeEnum.CRON) {
            if (jobInfo.getScheduleConf() == null || !CronExpression.isValidExpression(jobInfo.getScheduleConf())) {
                return ReturnMessage.fail("Cron" + I18nUtil.getString("system_unvalid"));
            }
        } else if (scheduleTypeEnum == ScheduleTypeEnum.FIX_RATE /*|| scheduleTypeEnum == ScheduleTypeEnum.FIX_DELAY*/) {
            if (jobInfo.getScheduleConf() == null) {
                return ReturnMessage.fail((I18nUtil.getString("schedule_type") + I18nUtil.getString("system_unvalid")));
            }
            try {
                int fixSecond = Integer.valueOf(jobInfo.getScheduleConf());
                if (fixSecond < 1) {
                    return ReturnMessage.fail((I18nUtil.getString("schedule_type") + I18nUtil.getString("system_unvalid")));
                }
            } catch (Exception e) {
                return ReturnMessage.fail((I18nUtil.getString("schedule_type") + I18nUtil.getString("system_unvalid")));
            }
        }

        // valid advanced
        if (ExecutorRouteStrategyEnum.match(jobInfo.getExecutorRouteStrategy(), null) == null) {
            return ReturnMessage.fail((I18nUtil.getString("jobinfo_field_executorRouteStrategy") + I18nUtil.getString("system_unvalid")));
        }
        if (MisfireStrategyEnum.match(jobInfo.getMisfireStrategy(), null) == null) {
            return ReturnMessage.fail((I18nUtil.getString("misfire_strategy") + I18nUtil.getString("system_unvalid")));
        }
        if (ExecutorBlockStrategyEnum.match(jobInfo.getExecutorBlockStrategy(), null) == null) {
            return ReturnMessage.fail((I18nUtil.getString("jobinfo_field_executorBlockStrategy") + I18nUtil.getString("system_unvalid")));
        }

        // 》ChildJobId valid
        if (jobInfo.getChildJobId() != null && jobInfo.getChildJobId().trim().length() > 0) {
            String[] childJobIds = jobInfo.getChildJobId().split(",");
            for (String childJobIdItem : childJobIds) {
                if (childJobIdItem != null && childJobIdItem.trim().length() > 0 && isNumeric(childJobIdItem)) {
                    // parse child
                    int childJobId = Integer.parseInt(childJobIdItem);
                    if (childJobId == jobInfo.getId()) {
                        return ReturnMessage.fail((I18nUtil.getString("jobinfo_field_childJobId") + "(" + childJobId + ")" + I18nUtil.getString("system_unvalid")));
                    }

                    // valid child
                    XxlJobInfo childJobInfo = xxlJobInfoMapper.loadById(childJobId);
                    if (childJobInfo == null) {
                        return ReturnMessage.fail(
                                MessageFormat.format((I18nUtil.getString("jobinfo_field_childJobId") + "({0})" + I18nUtil.getString("system_not_found")), childJobIdItem));
                    }
                } else {
                    return ReturnMessage.fail(
                            MessageFormat.format((I18nUtil.getString("jobinfo_field_childJobId") + "({0})" + I18nUtil.getString("system_unvalid")), childJobIdItem));
                }
            }

            // join , avoid "xxx,,"
            String temp = "";
            for (String item : childJobIds) {
                temp += item + ",";
            }
            temp = temp.substring(0, temp.length() - 1);

            jobInfo.setChildJobId(temp);
        }

        // group valid
        XxlJobGroup jobGroup = xxlJobGroupMapper.load(jobInfo.getJobGroup());
        if (jobGroup == null) {
            return ReturnMessage.fail((I18nUtil.getString("jobinfo_field_jobgroup") + I18nUtil.getString("system_unvalid")));
        }

        // stage job info
        XxlJobInfo exists_jobInfo = xxlJobInfoMapper.loadById(jobInfo.getId());
        if (exists_jobInfo == null) {
            return ReturnMessage.fail((I18nUtil.getString("jobinfo_field_id") + I18nUtil.getString("system_not_found")));
        }

        // next trigger time (5s后生效，避开预读周期)
        long nextTriggerTime = exists_jobInfo.getTriggerNextTime();
        boolean scheduleDataNotChanged = jobInfo.getScheduleType().equals(exists_jobInfo.getScheduleType()) && jobInfo.getScheduleConf().equals(exists_jobInfo.getScheduleConf());
        if (exists_jobInfo.getTriggerStatus() == 1 && !scheduleDataNotChanged) {
            try {
                Date nextValidTime = JobScheduleHelper.generateNextValidTime(jobInfo, new Date(System.currentTimeMillis() + JobScheduleHelper.PRE_READ_MS));
                if (nextValidTime == null) {
                    return ReturnMessage.fail((I18nUtil.getString("schedule_type") + I18nUtil.getString("system_unvalid")));
                }
                nextTriggerTime = nextValidTime.getTime();
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                return ReturnMessage.fail((I18nUtil.getString("schedule_type") + I18nUtil.getString("system_unvalid")));
            }
        }

        exists_jobInfo.setJobGroup(jobInfo.getJobGroup());
        exists_jobInfo.setJobDesc(jobInfo.getJobDesc());
        exists_jobInfo.setAuthor(jobInfo.getAuthor());
        exists_jobInfo.setAlarmEmail(jobInfo.getAlarmEmail());
        exists_jobInfo.setScheduleType(jobInfo.getScheduleType());
        exists_jobInfo.setScheduleConf(jobInfo.getScheduleConf());
        exists_jobInfo.setMisfireStrategy(jobInfo.getMisfireStrategy());
        exists_jobInfo.setExecutorRouteStrategy(jobInfo.getExecutorRouteStrategy());
        // remove the whitespace
        exists_jobInfo.setExecutorHandler(jobInfo.getExecutorHandler().trim());
        exists_jobInfo.setExecutorParam(jobInfo.getExecutorParam());
        exists_jobInfo.setExecutorBlockStrategy(jobInfo.getExecutorBlockStrategy());
        exists_jobInfo.setExecutorTimeout(jobInfo.getExecutorTimeout());
        exists_jobInfo.setExecutorFailRetryCount(jobInfo.getExecutorFailRetryCount());
        exists_jobInfo.setChildJobId(jobInfo.getChildJobId());
        exists_jobInfo.setTriggerNextTime(nextTriggerTime);

        exists_jobInfo.setUpdateTime(new Date());
        xxlJobInfoMapper.update(exists_jobInfo);


        return ReturnMessage.success(I18nUtils.getMessage(context, "update.result"));
    }

    @Autowired
    private SpringContext context;

    @Override
    public ReturnMessage<String> remove(int id, LoginInfo loginInfo) {
        // valid job
        XxlJobInfo xxlJobInfo = xxlJobInfoMapper.loadById(id);
        if (xxlJobInfo == null) {
            return ReturnMessage.success("任务不存在");
        }

        xxlJobInfoMapper.delete(id);
        xxlJobLogMapper.delete(id);
        xxlJobLogGlueMapper.deleteByJobId(id);
        return ReturnMessage.success(I18nUtils.getMessage(context, "delete.result"));
    }

    @Override
    public ReturnMessage<String> start(int id, LoginInfo loginInfo) {
        // load and valid
        XxlJobInfo xxlJobInfo = xxlJobInfoMapper.loadById(id);
        if (xxlJobInfo == null) {
            return ReturnMessage.fail(I18nUtil.getString("jobinfo_glue_jobid_unvalid"));
        }

        // valid
        ScheduleTypeEnum scheduleTypeEnum = ScheduleTypeEnum.match(xxlJobInfo.getScheduleType(), ScheduleTypeEnum.NONE);
        if (ScheduleTypeEnum.NONE == scheduleTypeEnum) {
            return ReturnMessage.fail(I18nUtil.getString("schedule_type_none_limit_start"));
        }

        // next trigger time (5s后生效，避开预读周期)
        long nextTriggerTime = 0;
        try {
            Date nextValidTime = JobScheduleHelper.generateNextValidTime(xxlJobInfo, new Date(System.currentTimeMillis() + JobScheduleHelper.PRE_READ_MS));
            if (nextValidTime == null) {
                return ReturnMessage.fail((I18nUtil.getString("schedule_type") + I18nUtil.getString("system_unvalid")));
            }
            nextTriggerTime = nextValidTime.getTime();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return ReturnMessage.fail((I18nUtil.getString("schedule_type") + I18nUtil.getString("system_unvalid")));
        }

        xxlJobInfo.setTriggerStatus(1);
        xxlJobInfo.setTriggerLastTime(0);
        xxlJobInfo.setTriggerNextTime(nextTriggerTime);

        xxlJobInfo.setUpdateTime(new Date());
        xxlJobInfoMapper.update(xxlJobInfo);
        return ReturnMessage.success("启动成功");
    }

    @Override
    public ReturnMessage<String> stop(int id, LoginInfo loginInfo) {
        // load and valid
        XxlJobInfo xxlJobInfo = xxlJobInfoMapper.loadById(id);
        if (xxlJobInfo == null) {
            return ReturnMessage.fail(I18nUtil.getString("jobinfo_glue_jobid_unvalid"));
        }

        // stop
        xxlJobInfo.setTriggerStatus(0);
        xxlJobInfo.setTriggerLastTime(0);
        xxlJobInfo.setTriggerNextTime(0);

        xxlJobInfo.setUpdateTime(new Date());
        xxlJobInfoMapper.update(xxlJobInfo);
        return ReturnMessage.success("停止成功");
    }


    @Override
    public ReturnMessage<String> trigger(LoginInfo loginInfo, int jobId, String executorParam, String addressList) {
        // valid job
        XxlJobInfo xxlJobInfo = xxlJobInfoMapper.loadById(jobId);
        if (xxlJobInfo == null) {
            return ReturnMessage.fail(I18nUtil.getString("jobinfo_glue_jobid_unvalid"));
        }

        // force cover job param
        if (executorParam == null) {
            executorParam = "";
        }

        JobTriggerPoolHelper.trigger(jobId, TriggerTypeEnum.MANUAL, -1, null, executorParam, addressList);
        return ReturnMessage.success("触发成功");
    }

    @Override
    public Map<String, Object> dashboardInfo() {

        int jobInfoCount = xxlJobInfoMapper.findAllCount();
        int jobLogCount = 0;
        int jobLogSuccessCount = 0;
        XxlJobLogReport xxlJobLogReport = xxlJobLogReportMapper.queryLogReportTotal();
        if (xxlJobLogReport != null) {
            jobLogCount = xxlJobLogReport.getRunningCount() + xxlJobLogReport.getSucCount() + xxlJobLogReport.getFailCount();
            jobLogSuccessCount = xxlJobLogReport.getSucCount();
        }

        // executor count
        Set<String> executorAddressSet = new HashSet<String>();
        List<XxlJobGroup> groupList = xxlJobGroupMapper.findAll();

        if (groupList != null && !groupList.isEmpty()) {
            for (XxlJobGroup group : groupList) {
                if (group.getRegistryList() != null && !group.getRegistryList().isEmpty()) {
                    executorAddressSet.addAll(group.getRegistryList());
                }
            }
        }

        int executorCount = executorAddressSet.size();

        Map<String, Object> dashboardMap = new HashMap<String, Object>();
        dashboardMap.put("jobInfoCount", jobInfoCount);
        dashboardMap.put("jobLogCount", jobLogCount);
        dashboardMap.put("jobLogSuccessCount", jobLogSuccessCount);
        dashboardMap.put("executorCount", executorCount);
        return dashboardMap;
    }

    @Override
    public ReturnT<Map<String, Object>> chartInfo(Date startDate, Date endDate) {

        // process
        List<String> triggerDayList = new ArrayList<String>();
        List<Integer> triggerDayCountRunningList = new ArrayList<Integer>();
        List<Integer> triggerDayCountSucList = new ArrayList<Integer>();
        List<Integer> triggerDayCountFailList = new ArrayList<Integer>();
        int triggerCountRunningTotal = 0;
        int triggerCountSucTotal = 0;
        int triggerCountFailTotal = 0;

        List<XxlJobLogReport> logReportList = xxlJobLogReportMapper.queryLogReport(startDate, endDate);

        if (logReportList != null && logReportList.size() > 0) {
            for (XxlJobLogReport item : logReportList) {
                String day = DateUtil.formatDate(item.getTriggerDay());
                int triggerDayCountRunning = item.getRunningCount();
                int triggerDayCountSuc = item.getSucCount();
                int triggerDayCountFail = item.getFailCount();

                triggerDayList.add(day);
                triggerDayCountRunningList.add(triggerDayCountRunning);
                triggerDayCountSucList.add(triggerDayCountSuc);
                triggerDayCountFailList.add(triggerDayCountFail);

                triggerCountRunningTotal += triggerDayCountRunning;
                triggerCountSucTotal += triggerDayCountSuc;
                triggerCountFailTotal += triggerDayCountFail;
            }
        } else {
            for (int i = -6; i <= 0; i++) {
                triggerDayList.add(DateUtil.formatDate(DateUtil.addDays(new Date(), i)));
                triggerDayCountRunningList.add(0);
                triggerDayCountSucList.add(0);
                triggerDayCountFailList.add(0);
            }
        }

        Map<String, Object> result = new HashMap<String, Object>();
        result.put("triggerDayList", triggerDayList);
        result.put("triggerDayCountRunningList", triggerDayCountRunningList);
        result.put("triggerDayCountSucList", triggerDayCountSucList);
        result.put("triggerDayCountFailList", triggerDayCountFailList);

        result.put("triggerCountRunningTotal", triggerCountRunningTotal);
        result.put("triggerCountSucTotal", triggerCountSucTotal);
        result.put("triggerCountFailTotal", triggerCountFailTotal);

        return ReturnT.ofSuccess(result);
    }

    @Override
    public ReturnMessage<String> batchStart(List<Integer> ids, LoginInfo loginInfo) {
        int failCount = 0;
        for (Integer id : ids) {
            ReturnMessage<String> result = start(id, loginInfo);
            if (!"OK".equals(result.getState())) {
                logger.warn("批量启动任务失败, id: {}", id);
                failCount++;
            }
        }
        return ReturnMessage.success("批量启动完成，成功" + (ids.size() - failCount) + "个");
    }

    @Override
    public ReturnMessage<String> batchStop(List<Integer> ids, LoginInfo loginInfo) {
        int failCount = 0;
        for (Integer id : ids) {
            ReturnMessage<String> result = stop(id, loginInfo);
            if (!"OK".equals(result.getState())) {
                logger.warn("批量停止任务失败, id: {}", id);
                failCount++;
            }
        }
        return ReturnMessage.success("批量停止完成，成功" + (ids.size() - failCount) + "个");
    }

    @Override
    public ReturnMessage<String> batchDelete(List<Integer> ids, LoginInfo loginInfo) {
        int failCount = 0;
        for (Integer id : ids) {
            ReturnMessage<String> result = remove(id, loginInfo);
            if (!"OK".equals(result.getState())) {
                logger.warn("批量删除任务失败, id: {}", id);
                failCount++;
            }
        }
        return ReturnMessage.success("批量删除完成，成功" + (ids.size() - failCount) + "个");
    }

    @Override
    public ReturnMessage<String> batchUpdateJobGroup(List<Integer> ids, Integer jobGroup) {
        XxlJobGroup group = xxlJobGroupMapper.load(jobGroup);
        if (group == null) {
            return ReturnMessage.fail("执行器不存在");
        }
        xxlJobInfoMapper.batchUpdateJobGroup(ids, jobGroup, new Date());
        return ReturnMessage.success("批量修改执行器成功");
    }

    private static final String[] CSV_HEADERS = {
            "jobGroup", "jobDesc", "author", "alarmEmail", "scheduleType", "scheduleConf",
            "misfireStrategy", "executorRouteStrategy", "executorHandler", "executorParam",
            "executorBlockStrategy", "executorTimeout", "executorFailRetryCount",
            "glueType", "glueSource", "glueRemark", "childJobId"
    };

    private static final String CSV_CONTENT_TYPE = "text/csv;charset=UTF-8";

    private XxlJobInfo parseCsvRow(String[] headers, String[] fields) {
        XxlJobInfo jobInfo = new XxlJobInfo();
        for (int i = 0; i < headers.length && i < fields.length; i++) {
            String value = fields[i].trim();
            if (value.isEmpty()) continue;
            switch (headers[i]) {
                case "jobGroup": jobInfo.setJobGroup(Integer.parseInt(value)); break;
                case "jobDesc": jobInfo.setJobDesc(value); break;
                case "author": jobInfo.setAuthor(value); break;
                case "alarmEmail": jobInfo.setAlarmEmail(value); break;
                case "scheduleType": jobInfo.setScheduleType(value); break;
                case "scheduleConf": jobInfo.setScheduleConf(value); break;
                case "misfireStrategy": jobInfo.setMisfireStrategy(value); break;
                case "executorRouteStrategy": jobInfo.setExecutorRouteStrategy(value); break;
                case "executorHandler": jobInfo.setExecutorHandler(value); break;
                case "executorParam": jobInfo.setExecutorParam(value); break;
                case "executorBlockStrategy": jobInfo.setExecutorBlockStrategy(value); break;
                case "executorTimeout": jobInfo.setExecutorTimeout(Integer.parseInt(value)); break;
                case "executorFailRetryCount": jobInfo.setExecutorFailRetryCount(Integer.parseInt(value)); break;
                case "glueType": jobInfo.setGlueType(value); break;
                case "glueSource": jobInfo.setGlueSource(value); break;
                case "glueRemark": jobInfo.setGlueRemark(value); break;
                case "childJobId": jobInfo.setChildJobId(value); break;
            }
        }
        return jobInfo;
    }

    private String[] splitCsvLine(String line) {
        List<String> fields = new ArrayList<>();
        boolean inQuotes = false;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (inQuotes) {
                if (c == '"') {
                    if (i + 1 < line.length() && line.charAt(i + 1) == '"') {
                        sb.append('"');
                        i++;
                    } else {
                        inQuotes = false;
                    }
                } else {
                    sb.append(c);
                }
            } else {
                if (c == '"') {
                    inQuotes = true;
                } else if (c == ',') {
                    fields.add(sb.toString());
                    sb = new StringBuilder();
                } else {
                    sb.append(c);
                }
            }
        }
        fields.add(sb.toString());
        return fields.toArray(new String[0]);
    }

    private String escapeCsvField(String field) {
        if (field == null) return "";
        if (field.contains(",") || field.contains("\"") || field.contains("\n") || field.contains("\r")) {
            return "\"" + field.replace("\"", "\"\"") + "\"";
        }
        return field;
    }

    private String buildCsvLine(String[] fields) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < fields.length; i++) {
            if (i > 0) sb.append(",");
            sb.append(escapeCsvField(fields[i]));
        }
        return sb.toString();
    }

    private String getFieldValue(XxlJobInfo ji, String fieldName) {
        switch (fieldName) {
            case "jobGroup": return String.valueOf(ji.getJobGroup());
            case "jobDesc": return ji.getJobDesc();
            case "author": return ji.getAuthor();
            case "alarmEmail": return ji.getAlarmEmail();
            case "scheduleType": return ji.getScheduleType();
            case "scheduleConf": return ji.getScheduleConf();
            case "misfireStrategy": return ji.getMisfireStrategy();
            case "executorRouteStrategy": return ji.getExecutorRouteStrategy();
            case "executorHandler": return ji.getExecutorHandler();
            case "executorParam": return ji.getExecutorParam();
            case "executorBlockStrategy": return ji.getExecutorBlockStrategy();
            case "executorTimeout": return String.valueOf(ji.getExecutorTimeout());
            case "executorFailRetryCount": return String.valueOf(ji.getExecutorFailRetryCount());
            case "glueType": return ji.getGlueType();
            case "glueSource": return ji.getGlueSource();
            case "glueRemark": return ji.getGlueRemark();
            case "childJobId": return ji.getChildJobId();
            default: return "";
        }
    }

    @Override
    public ReturnMessage<String> importJobs(MultipartFile file, LoginInfo loginInfo) {
        if (file.isEmpty()) {
            return ReturnMessage.fail("导入文件为空");
        }
        int successCount = 0;
        int failCount = 0;
        StringBuilder errors = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            String headerLine = br.readLine();
            if (headerLine == null) {
                return ReturnMessage.fail("导入文件无内容");
            }
            String[] headers = splitCsvLine(headerLine);
            String line;
            int rowNum = 1;
            while ((line = br.readLine()) != null) {
                rowNum++;
                if (line.trim().isEmpty()) continue;
                try {
                    String[] fields = splitCsvLine(line);
                    XxlJobInfo jobInfo = parseCsvRow(headers, fields);
                    if (jobInfo.getJobGroup() < 1) {
                        failCount++;
                        errors.append(String.format("第%d行: 执行器ID不能为空; ", rowNum));
                        continue;
                    }
                    if (jobInfo.getJobDesc() == null || jobInfo.getJobDesc().trim().isEmpty()) {
                        failCount++;
                        errors.append(String.format("第%d行: 任务描述不能为空; ", rowNum));
                        continue;
                    }
                    if (jobInfo.getScheduleType() == null || jobInfo.getScheduleType().trim().isEmpty()) {
                        failCount++;
                        errors.append(String.format("第%d行: 调度类型不能为空; ", rowNum));
                        continue;
                    }
                    ReturnMessage<String> result = doAdd(jobInfo);
                    String errMsg = result.getErrorMessage();
                    if (errMsg == null || errMsg.isEmpty()) {
                        successCount++;
                    } else {
                        failCount++;
                        errors.append(String.format("第%d行: %s; ", rowNum, errMsg));
                    }
                } catch (Exception e) {
                    failCount++;
                    String errMsg = e.getMessage();
                    if (errMsg == null || errMsg.isEmpty()) {
                        errMsg = e.getClass().getSimpleName();
                    }
                    errors.append(String.format("第%d行: %s; ", rowNum, errMsg));
                }
            }
        } catch (Exception e) {
            logger.error("导入任务数据失败", e);
            return ReturnMessage.fail("导入失败: " + e.getMessage());
        }
        String msg = String.format("导入完成，成功%d条，失败%d条", successCount, failCount);
        if (failCount > 0) {
            msg += "。失败详情: " + errors.toString();
        }
        return ReturnMessage.success(msg);
    }

    @Override
    public void exportJobs(XxlJobInfo query, HttpServletResponse response) {
        List<XxlJobInfo> list = xxlJobInfoMapper.pageList(0, Integer.MAX_VALUE,
                query.getJobGroup(),
                query.getTriggerStatus(),
                query.getJobDesc(),
                query.getExecutorHandler(),
                query.getAuthor());
        try (OutputStreamWriter osw = new OutputStreamWriter(response.getOutputStream(), StandardCharsets.UTF_8)) {
            response.setContentType(CSV_CONTENT_TYPE);
            response.setHeader("Content-Disposition", "attachment;filename=" +
                    URLEncoder.encode("xxl-job任务数据.csv", StandardCharsets.UTF_8));
            // BOM for Excel UTF-8 recognition
            osw.write('\ufeff');
            osw.write(buildCsvLine(CSV_HEADERS) + "\n");
            for (XxlJobInfo ji : list) {
                String[] fields = new String[CSV_HEADERS.length];
                for (int i = 0; i < CSV_HEADERS.length; i++) {
                    fields[i] = getFieldValue(ji, CSV_HEADERS[i]);
                }
                osw.write(buildCsvLine(fields) + "\n");
            }
            osw.flush();
        } catch (IOException e) {
            logger.error("导出任务数据失败", e);
        }
    }

    @Override
    public void downloadTemplate(HttpServletResponse response) {
        try (OutputStreamWriter osw = new OutputStreamWriter(response.getOutputStream(), StandardCharsets.UTF_8)) {
            response.setContentType(CSV_CONTENT_TYPE);
            response.setHeader("Content-Disposition", "attachment;filename=" +
                    URLEncoder.encode("xxl-job导入模版.csv", StandardCharsets.UTF_8));
            osw.write('\ufeff');
            osw.write(buildCsvLine(CSV_HEADERS) + "\n");
            osw.flush();
        } catch (IOException e) {
            logger.error("下载导入模版失败", e);
        }
    }

}
