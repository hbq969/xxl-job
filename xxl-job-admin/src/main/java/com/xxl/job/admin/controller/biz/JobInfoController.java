package com.xxl.job.admin.controller.biz;

import com.github.hbq969.code.common.restful.ReturnMessage;
import com.github.hbq969.code.common.spring.context.SpringContext;
import com.github.hbq969.code.dict.model.Pair;
import com.github.hbq969.code.dict.service.api.impl.MapDictHelperImpl;
import com.github.hbq969.code.sm.perm.api.SMRequiresPermissions;
import com.github.pagehelper.PageInfo;
import com.xxl.job.admin.mapper.XxlJobGroupMapper;
import com.xxl.job.admin.model.XxlJobGroup;
import com.xxl.job.admin.model.XxlJobInfo;
import com.xxl.job.admin.scheduler.exception.XxlJobException;
import com.xxl.job.admin.scheduler.route.ExecutorRouteStrategyEnum;
import com.xxl.job.admin.scheduler.scheduler.MisfireStrategyEnum;
import com.xxl.job.admin.scheduler.scheduler.ScheduleTypeEnum;
import com.xxl.job.admin.scheduler.thread.JobScheduleHelper;
import com.xxl.job.admin.service.XxlJobService;
import com.xxl.job.admin.util.I18nUtil;
import com.xxl.job.admin.util.JobGroupPermissionUtil;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.enums.ExecutorBlockStrategyEnum;
import com.xxl.job.core.glue.GlueTypeEnum;
import com.xxl.job.core.util.DateUtil;
import com.xxl.sso.core.helper.XxlSsoHelper;
import com.xxl.sso.core.model.LoginInfo;
import com.xxl.tool.core.CollectionTool;
import com.xxl.tool.response.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * index controller
 *
 * @author xuxueli 2015-12-19 16:13:16
 */
@RestController
@RequestMapping(path = "/xxl-job-ui/jobinfo")
@Tag(name = "xxl-job-任务管理")
public class JobInfoController {
    private static Logger logger = LoggerFactory.getLogger(JobInfoController.class);

    @Resource
    private XxlJobGroupMapper xxlJobGroupMapper;
    @Resource
    private XxlJobService xxlJobService;

    @Autowired
    private SpringContext context;

    @RequestMapping
    public String index(HttpServletRequest request, Model model, @RequestParam(value = "jobGroup", required = false, defaultValue = "-1") int jobGroup) {

        // 枚举-字典
        model.addAttribute("ExecutorRouteStrategyEnum", ExecutorRouteStrategyEnum.values());        // 路由策略-列表
        model.addAttribute("GlueTypeEnum", GlueTypeEnum.values());                                // Glue类型-字典
        model.addAttribute("ExecutorBlockStrategyEnum", ExecutorBlockStrategyEnum.values());        // 阻塞处理策略-字典
        model.addAttribute("ScheduleTypeEnum", ScheduleTypeEnum.values());                        // 调度类型
        model.addAttribute("MisfireStrategyEnum", MisfireStrategyEnum.values());                    // 调度过期策略

        // 执行器列表
        List<XxlJobGroup> jobGroupListTotal = xxlJobGroupMapper.findAll();

        // filter group
        List<XxlJobGroup> jobGroupList = JobGroupPermissionUtil.filterJobGroupByPermission(request, jobGroupListTotal);
        if (CollectionTool.isEmpty(jobGroupList)) {
            throw new XxlJobException(I18nUtil.getString("jobgroup_empty"));
        }

        model.addAttribute("JobGroupList", jobGroupList);
        model.addAttribute("jobGroup", jobGroup);

        return "jobinfo/jobinfo.index";
    }

    @Operation(summary = "分页查询任务列表")
    @RequestMapping(path = "/pageList", method = RequestMethod.GET)
    @ResponseBody
    @SMRequiresPermissions(menu = "task_list", apiKey = "pageList", apiDesc = "分页查询任务列表")
    public ReturnMessage<PageInfo<XxlJobInfo>> pageList(@RequestParam(name = "start", required = false, defaultValue = "1") int start,
                                                        @RequestParam(name = "length", required = false, defaultValue = "10") int length,
                                                        @RequestParam(name = "jobGroup", required = false) Integer jobGroup,
                                                        @RequestParam(name = "triggerStatus", required = false) Integer triggerStatus,
                                                        @RequestParam(name = "jobDesc", required = false) String jobDesc,
                                                        @RequestParam(name = "executorHandler", required = false) String executorHandler,
                                                        @RequestParam(name = "author", required = false) String author) {

        return ReturnMessage.success(xxlJobService.pageList(start, length, jobGroup, triggerStatus, jobDesc, executorHandler, author));
    }

    @Operation(summary = "添加任务")
    @RequestMapping(path = "/add", method = RequestMethod.POST)
    @ResponseBody
    @SMRequiresPermissions(menu = "task_list", apiKey = "add", apiDesc = "添加任务")
    public ReturnMessage<String> add(HttpServletRequest request, @RequestBody XxlJobInfo jobInfo) {
        return xxlJobService.add(jobInfo, null);
    }

    @Operation(summary = "更新任务")
    @RequestMapping(path = "/update", method = RequestMethod.POST)
    @ResponseBody
    @SMRequiresPermissions(menu = "task_list", apiKey = "update", apiDesc = "更新任务")
    public ReturnMessage<String> update(HttpServletRequest request, @RequestBody XxlJobInfo jobInfo) {
        return xxlJobService.update(jobInfo, null);
    }

    @Operation(summary = "删除任务")
    @RequestMapping(path = "/remove", method = RequestMethod.DELETE)
    @ResponseBody
    @SMRequiresPermissions(menu = "task_list", apiKey = "remove", apiDesc = "删除任务")
    public ReturnMessage<String> remove(HttpServletRequest request, @RequestParam("id") int id) {
        return xxlJobService.remove(id, null);
    }

    @Operation(summary = "停止任务")
    @RequestMapping(path = "/stop", method = RequestMethod.POST)
    @ResponseBody
    @SMRequiresPermissions(menu = "task_list", apiKey = "pause", apiDesc = "停止任务")
    public ReturnMessage<String> pause(HttpServletRequest request, @RequestParam("id") int id) {
        return xxlJobService.stop(id, null);
    }

    @Operation(summary = "启动任务")
    @RequestMapping(path = "/start", method = RequestMethod.POST)
    @ResponseBody
    @SMRequiresPermissions(menu = "task_list", apiKey = "start", apiDesc = "启动任务")
    public ReturnMessage<String> start(HttpServletRequest request, @RequestParam("id") int id) {
        return xxlJobService.start(id, null);
    }

    @Operation(summary = "触发任务")
    @RequestMapping(path = "/trigger", method = RequestMethod.POST)
    @ResponseBody
    @SMRequiresPermissions(menu = "task_list", apiKey = "triggerJob", apiDesc = "触发任务")
    public ReturnMessage<String> triggerJob(HttpServletRequest request,
                                            @RequestParam("id") int id,
                                            @RequestParam("executorParam") String executorParam,
                                            @RequestParam("addressList") String addressList) {
        return xxlJobService.trigger(null, id, executorParam, addressList);
    }

    @Operation(summary = "查询任务下次触发时间")
    @RequestMapping("/nextTriggerTime")
    @ResponseBody
    @SMRequiresPermissions(menu = "task_list", apiKey = "nextTriggerTime", apiDesc = "查询任务下次触发时间")
    public ReturnMessage<List<String>> nextTriggerTime(@RequestParam("scheduleType") String scheduleType,
                                                       @RequestParam("scheduleConf") String scheduleConf) {

        XxlJobInfo paramXxlJobInfo = new XxlJobInfo();
        paramXxlJobInfo.setScheduleType(scheduleType);
        paramXxlJobInfo.setScheduleConf(scheduleConf);

        List<String> result = new ArrayList<>();
        try {
            Date lastTime = new Date();
            for (int i = 0; i < 5; i++) {
                lastTime = JobScheduleHelper.generateNextValidTime(paramXxlJobInfo, lastTime);
                if (lastTime != null) {
                    result.add(DateUtil.formatDateTime(lastTime));
                } else {
                    break;
                }
            }
        } catch (Exception e) {
            logger.error("nextTriggerTime error. scheduleType = {}, scheduleConf= {}", scheduleType, scheduleConf, e);
            return ReturnMessage.fail((I18nUtil.getString("schedule_type") + I18nUtil.getString("system_unvalid")) + e.getMessage());
        }
        return ReturnMessage.success(result);

    }

    @Operation(summary = "查询字典数据")
    @RequestMapping(path = "/dict", method = RequestMethod.GET)
    @ResponseBody
    @SMRequiresPermissions(menu = "task_list", apiKey = "queryDict", apiDesc = "查询字典数据")
    public ReturnMessage<List<Pair>> queryDict(
            @RequestParam(value = "dn") String dn) {
        return ReturnMessage.success(context.getBean(MapDictHelperImpl.class).queryPairList(dn));
    }

    @Operation(summary = "根据id查询任务详情")
    @RequestMapping(path = "/info", method = RequestMethod.GET)
    @ResponseBody
    public ReturnMessage<XxlJobInfo> queryJobInfo(
            @RequestParam(value = "id") Integer id) {
        return ReturnMessage.success(xxlJobService.queryJobInfo(id));
    }

}
