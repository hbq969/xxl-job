package com.xxl.job.admin.controller.biz;

import cn.hutool.core.lang.Assert;
import com.github.hbq969.code.common.restful.ReturnMessage;
import com.github.hbq969.code.common.spring.context.SpringContext;
import com.github.hbq969.code.common.utils.I18nUtils;
import com.github.hbq969.code.sm.perm.api.SMRequiresPermissions;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xxl.job.admin.mapper.XxlJobGroupMapper;
import com.xxl.job.admin.mapper.XxlJobInfoMapper;
import com.xxl.job.admin.mapper.XxlJobLogMapper;
import com.xxl.job.admin.model.XxlJobGroup;
import com.xxl.job.admin.model.XxlJobInfo;
import com.xxl.job.admin.model.XxlJobLog;
import com.xxl.job.admin.scheduler.complete.XxlJobCompleter;
import com.xxl.job.admin.scheduler.exception.XxlJobException;
import com.xxl.job.admin.scheduler.scheduler.XxlJobScheduler;
import com.xxl.job.admin.util.I18nUtil;
import com.xxl.job.admin.util.JobGroupPermissionUtil;
import com.xxl.job.core.biz.ExecutorBiz;
import com.xxl.job.core.biz.model.KillParam;
import com.xxl.job.core.biz.model.LogParam;
import com.xxl.job.core.biz.model.LogResult;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.util.DateUtil;
import com.xxl.tool.core.CollectionTool;
import com.xxl.tool.core.StringTool;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.HtmlUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * index controller
 *
 * @author xuxueli 2015-12-19 16:13:16
 */
@Tag(name = "xxl-job-日志查询")
@RestController
@RequestMapping(path = "/xxl-job-ui/joblog")
public class JobLogController {
    private static Logger logger = LoggerFactory.getLogger(JobLogController.class);

    @Resource
    private XxlJobGroupMapper xxlJobGroupMapper;
    @Resource
    public XxlJobInfoMapper xxlJobInfoMapper;
    @Resource
    public XxlJobLogMapper xxlJobLogMapper;
    @Autowired
    private SpringContext context;

    //    @RequestMapping
    public String index(HttpServletRequest request, Model model,
                        @RequestParam(value = "jobGroup", required = false, defaultValue = "0") Integer jobGroup,
                        @RequestParam(value = "jobId", required = false, defaultValue = "0") Integer jobId) {

        // find jobGroup
        List<XxlJobGroup> jobGroupListTotal = xxlJobGroupMapper.findAll();
        // filter jobGroup
        List<XxlJobGroup> jobGroupList = JobGroupPermissionUtil.filterJobGroupByPermission(request, jobGroupListTotal);
        if (CollectionTool.isEmpty(jobGroupList)) {
            throw new XxlJobException(I18nUtil.getString("jobgroup_empty"));
        }
        // write jobGroup
        model.addAttribute("JobGroupList", jobGroupList);

        // parse jobId、jobGroup
        if (jobId > 0) {
            XxlJobInfo jobInfo = xxlJobInfoMapper.loadById(jobId);
            if (jobInfo == null) {
                throw new RuntimeException(I18nUtil.getString("jobinfo_field_id") + I18nUtil.getString("system_unvalid"));
            }
            jobGroup = jobInfo.getJobGroup();
        } else if (jobGroup > 0) {
            jobId = 0;
        }
        jobGroup = jobGroup > 0 ? jobGroup : jobGroupList.get(0).getId();
        // valid permission
        JobGroupPermissionUtil.validJobGroupPermission(request, jobGroup);

        // find jobList
        List<XxlJobInfo> jobInfoList = xxlJobInfoMapper.getJobsByGroup(jobGroup);

        // write
        model.addAttribute("jobInfoList", jobInfoList);
        model.addAttribute("jobGroup", jobGroup);
        model.addAttribute("jobId", jobId);

        return "joblog/joblog.index";
    }

	/*@RequestMapping("/getJobsByGroup")
	@ResponseBody
	public ReturnT<List<XxlJobInfo>> getJobsByGroup(HttpServletRequest request, @RequestParam("jobGroup") int jobGroup){

		// valid permission
		JobInfoController.validJobGroupPermission(request, jobGroup);

		// query
		List<XxlJobInfo> list = xxlJobInfoMapper.getJobsByGroup(jobGroup);
		return ReturnT.ofSuccess(list);
	}*/

    @Operation(summary = "分页查询日志列表")
    @RequestMapping(path = "/pageList", method = RequestMethod.GET)
    @ResponseBody
    @SMRequiresPermissions(menu = "schedule_log", apiKey = "pageList", apiDesc = "分页查询日志列表")
    public ReturnMessage<PageInfo<XxlJobLog>> pageList(HttpServletRequest request,
                                                       @RequestParam(value = "start", required = false, defaultValue = "0") int start,
                                                       @RequestParam(value = "length", required = false, defaultValue = "10") int length,
                                                       @RequestParam(name = "jobGroup", required = false) Integer jobGroup,
                                                       @RequestParam(name = "jobId", required = false) Integer jobId,
                                                       @RequestParam(name = "logStatus", required = false) Integer logStatus,
                                                       @RequestParam(name = "filterTime", required = false) String filterTime) {

        // parse param
        Date triggerTimeStart = null;
        Date triggerTimeEnd = null;
        if (StringTool.isNotBlank(filterTime)) {
            String[] temp = filterTime.split(" - ");
            if (temp.length == 2) {
                triggerTimeStart = DateUtil.parseDateTime(temp[0]);
                triggerTimeEnd = DateUtil.parseDateTime(temp[1]);
            }
        }
        final Date startTime = triggerTimeStart;
        final Date endTime = triggerTimeEnd;

        PageInfo<XxlJobLog> pg = PageHelper.startPage(start, length)
                .doSelectPageInfo(() -> xxlJobLogMapper.pageList(start, length, jobGroup, jobId, startTime, endTime, logStatus));
        pg.getList().forEach(xl -> xl.convertDict(context));

        return ReturnMessage.success(pg);
    }

//    @RequestMapping(path = "/logDetailPage")
    public String logDetailPage(HttpServletRequest request, @RequestParam("id") int id, Model model) {

        // base check
        XxlJobLog jobLog = xxlJobLogMapper.load(id);
        if (jobLog == null) {
            throw new RuntimeException(I18nUtil.getString("joblog_logid_unvalid"));
        }

        // valid permission
        JobGroupPermissionUtil.validJobGroupPermission(request, jobLog.getJobGroup());

        // data
        model.addAttribute("triggerCode", jobLog.getTriggerCode());
        model.addAttribute("handleCode", jobLog.getHandleCode());
        model.addAttribute("logId", jobLog.getId());
        return "joblog/joblog.detail";
    }

    @Operation(summary = "查看实时日志")
    @RequestMapping(path = "/logDetailCat", method = RequestMethod.GET)
    @ResponseBody
    @SMRequiresPermissions(menu = "schedule_log", apiKey = "logDetailCat", apiDesc = "查看实时日志")
    public ReturnMessage<LogResult> logDetailCat(@RequestParam("logId") long logId, @RequestParam("fromLineNum") int fromLineNum) {
        try {
            // valid
            XxlJobLog jobLog = xxlJobLogMapper.load(logId);    // todo, need to improve performance
            if (jobLog == null) {
                return ReturnMessage.fail(I18nUtil.getString("joblog_logid_unvalid"));
            }

            // log cat
            ExecutorBiz executorBiz = XxlJobScheduler.getExecutorBiz(jobLog.getExecutorAddress());
            ReturnT<LogResult> logResult = executorBiz.log(new LogParam(jobLog.getTriggerTime().getTime(), logId, fromLineNum));

            // is end
            if (logResult.getContent() != null && logResult.getContent().getFromLineNum() > logResult.getContent().getToLineNum()) {
                if (jobLog.getHandleCode() > 0) {
                    logResult.getContent().setEnd(true);
                }
            }

            // fix xss
            if (logResult.getContent() != null && StringTool.isNotBlank(logResult.getContent().getLogContent())) {
                String newLogContent = filter(logResult.getContent().getLogContent());
                logResult.getContent().setLogContent(newLogContent);
            }

            return ReturnMessage.success(logResult.getContent());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return ReturnMessage.fail(e.getMessage());
        }
    }

    /**
     * filter xss tag
     *
     * @param originData
     * @return
     */
    private String filter(String originData) {

        // exclude tag
        Map<String, String> excludeTagMap = new HashMap<String, String>();
        excludeTagMap.put("<br>", "###TAG_BR###");
        excludeTagMap.put("<b>", "###TAG_BOLD###");
        excludeTagMap.put("</b>", "###TAG_BOLD_END###");

        // replace
        for (String key : excludeTagMap.keySet()) {
            String value = excludeTagMap.get(key);
            originData = originData.replaceAll(key, value);
        }

        // htmlEscape
        originData = HtmlUtils.htmlEscape(originData, "UTF-8");

        // replace back
        for (String key : excludeTagMap.keySet()) {
            String value = excludeTagMap.get(key);
            originData = originData.replaceAll(value, key);
        }

        return originData;
    }

//    @RequestMapping("/logKill")
//    @ResponseBody
    public ReturnT<String> logKill(HttpServletRequest request, @RequestParam("id") int id) {
        // base check
        XxlJobLog log = xxlJobLogMapper.load(id);
        XxlJobInfo jobInfo = xxlJobInfoMapper.loadById(log.getJobId());
        if (jobInfo == null) {
            return ReturnT.ofFail(I18nUtil.getString("jobinfo_glue_jobid_unvalid"));
        }
        if (ReturnT.SUCCESS_CODE != log.getTriggerCode()) {
            return ReturnT.ofFail(I18nUtil.getString("joblog_kill_log_limit"));
        }

        // valid JobGroup permission
        JobGroupPermissionUtil.validJobGroupPermission(request, jobInfo.getJobGroup());

        // request of kill
        ReturnT<String> runResult = null;
        try {
            ExecutorBiz executorBiz = XxlJobScheduler.getExecutorBiz(log.getExecutorAddress());
            runResult = executorBiz.kill(new KillParam(jobInfo.getId()));
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            runResult = ReturnT.ofFail(e.getMessage());
        }

        if (ReturnT.SUCCESS_CODE == runResult.getCode()) {
            log.setHandleCode(ReturnT.FAIL_CODE);
            log.setHandleMsg(I18nUtil.getString("joblog_kill_log_byman") + ":" + (runResult.getMsg() != null ? runResult.getMsg() : ""));
            log.setHandleTime(new Date());
            XxlJobCompleter.updateHandleInfoAndFinish(log);
            return ReturnT.ofSuccess(runResult.getMsg());
        } else {
            return ReturnT.ofFail(runResult.getMsg());
        }
    }

    @Operation(summary = "清理日志")
    @RequestMapping(path = "/clearLog", method = RequestMethod.GET)
    @ResponseBody
    @SMRequiresPermissions(menu = "schedule_log", apiKey = "clearLog", apiDesc = "清理日志")
    public ReturnMessage<String> clearLog(HttpServletRequest request,
                                          @RequestParam(value = "jobGroup", required = false) Integer jobGroup,
                                          @RequestParam(name = "jobId", required = false) Integer jobId,
                                          @RequestParam(name = "type", required = false) Integer type) {
        // opt
        Date clearBeforeTime = null;
        int clearBeforeNum = 0;
        if (type == 1) {
            clearBeforeTime = DateUtil.addMonths(new Date(), -1);    // 清理一个月之前日志数据
        } else if (type == 2) {
            clearBeforeTime = DateUtil.addMonths(new Date(), -3);    // 清理三个月之前日志数据
        } else if (type == 3) {
            clearBeforeTime = DateUtil.addMonths(new Date(), -6);    // 清理六个月之前日志数据
        } else if (type == 4) {
            clearBeforeTime = DateUtil.addYears(new Date(), -1);    // 清理一年之前日志数据
        } else if (type == 5) {
            clearBeforeNum = 1000;        // 清理一千条以前日志数据
        } else if (type == 6) {
            clearBeforeNum = 10000;        // 清理一万条以前日志数据
        } else if (type == 7) {
            clearBeforeNum = 30000;        // 清理三万条以前日志数据
        } else if (type == 8) {
            clearBeforeNum = 100000;    // 清理十万条以前日志数据
        } else if (type == 9) {
            clearBeforeNum = 0;            // 清理所有日志数据
        } else {
            return ReturnMessage.fail(I18nUtil.getString("joblog_clean_type_unvalid"));
        }

        List<Long> logIds = null;
        do {
            logIds = xxlJobLogMapper.findClearLogIds(jobGroup, jobId, clearBeforeTime, clearBeforeNum, 1000);
            if (logIds != null && logIds.size() > 0) {
                xxlJobLogMapper.clearLog(logIds);
            }
        } while (logIds != null && logIds.size() > 0);

        return ReturnMessage.success(I18nUtils.getMessage(context, "clear.result"));
    }

    @Operation(summary = "查询执行器下的任务")
    @RequestMapping(path = "/tasks", method = RequestMethod.GET)
    @ResponseBody
    @SMRequiresPermissions(menu = "schedule_log", apiKey = "queryTasks", apiDesc = "查询执行器下的任务")
    public ReturnMessage<List<XxlJobInfo>> queryTasks(
            @RequestParam(value = "groupId") Integer groupId) {
        Assert.notNull(groupId, "groupId must not be null");
        return ReturnMessage.success(xxlJobInfoMapper.findJobInfoByGroupId(groupId));
    }

}
