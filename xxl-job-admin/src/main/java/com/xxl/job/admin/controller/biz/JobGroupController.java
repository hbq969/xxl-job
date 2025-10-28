package com.xxl.job.admin.controller.biz;

import com.github.hbq969.code.common.restful.ReturnMessage;
import com.github.hbq969.code.common.spring.context.SpringContext;
import com.github.hbq969.code.common.utils.I18nUtils;
import com.github.hbq969.code.sm.perm.api.SMRequiresPermissions;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xxl.job.admin.mapper.XxlJobGroupMapper;
import com.xxl.job.admin.mapper.XxlJobInfoMapper;
import com.xxl.job.admin.mapper.XxlJobRegistryMapper;
import com.xxl.job.admin.mapper.XxlJobUserMapper;
import com.xxl.job.admin.model.XxlJobGroup;
import com.xxl.job.admin.model.XxlJobRegistry;
import com.xxl.job.admin.util.I18nUtil;
import com.xxl.job.core.enums.RegistryConfig;
import com.xxl.tool.core.CollectionTool;
import com.xxl.tool.core.StringTool;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * job group controller
 *
 * @author xuxueli 2016-10-02 20:52:56
 */
@RestController
@RequestMapping(path = "/xxl-job-ui/jobgroup")
@Tag(name = "xxl-job-执行器管理")
public class JobGroupController {

    @Resource
    public XxlJobInfoMapper xxlJobInfoMapper;
    @Resource
    public XxlJobGroupMapper xxlJobGroupMapper;
    @Resource
    private XxlJobRegistryMapper xxlJobRegistryMapper;

    @Resource
    private XxlJobUserMapper xxlJobUserMapper;

    @Autowired
    private SpringContext context;

    @RequestMapping(path = "/pageList", method = RequestMethod.GET)
    @ResponseBody
    @Operation(summary = "分页查询执行器列表")
    @SMRequiresPermissions(menu = "executor_list", apiKey = "pageList", apiDesc = "分页查询执行器列表")
    public ReturnMessage<PageInfo<XxlJobGroup>> pageList(@RequestParam(value = "start", required = false, defaultValue = "0") int start,
                                                         @RequestParam(value = "length", required = false, defaultValue = "10") int length,
                                                         @RequestParam(value = "appname", required = false) String appname,
                                                         @RequestParam(value = "title", required = false) String title) {

        PageInfo<XxlJobGroup> pg = PageHelper.startPage(start, length)
                .doSelectPageInfo(() -> xxlJobGroupMapper.pageList(start, length, appname, title));
        pg.getList().forEach(g -> g.convertDict(context));
        return ReturnMessage.success(pg);
    }

    @Operation(summary = "保存执行器")
    @RequestMapping(path = "/save", method = RequestMethod.POST)
    @ResponseBody
    @SMRequiresPermissions(menu = "executor_list", apiKey = "save", apiDesc = "保存执行器")
    public ReturnMessage<String> save(@RequestBody XxlJobGroup xxlJobGroup) {

        // valid
        if (StringTool.isBlank(xxlJobGroup.getAppname())) {
            return ReturnMessage.fail((I18nUtil.getString("system_please_input") + "AppName"));
        }
        if (xxlJobGroup.getAppname().length() < 4 || xxlJobGroup.getAppname().length() > 64) {
            return ReturnMessage.fail(I18nUtil.getString("jobgroup_field_appname_length"));
        }
        if (xxlJobGroup.getAppname().contains(">") || xxlJobGroup.getAppname().contains("<")) {
            return ReturnMessage.fail("AppName" + I18nUtil.getString("system_unvalid"));
        }
        if (StringTool.isBlank(xxlJobGroup.getTitle())) {
            return ReturnMessage.fail((I18nUtil.getString("system_please_input") + I18nUtil.getString("jobgroup_field_title")));
        }
        if (xxlJobGroup.getTitle().contains(">") || xxlJobGroup.getTitle().contains("<")) {
            return ReturnMessage.fail(I18nUtil.getString("jobgroup_field_title") + I18nUtil.getString("system_unvalid"));
        }
        if (xxlJobGroup.getAddressType() != 0) {
            if (StringTool.isBlank(xxlJobGroup.getAddressList())) {
                return ReturnMessage.fail(I18nUtil.getString("jobgroup_field_addressType_limit"));
            }
            if (xxlJobGroup.getAddressList().contains(">") || xxlJobGroup.getAddressList().contains("<")) {
                return ReturnMessage.fail(I18nUtil.getString("jobgroup_field_registryList") + I18nUtil.getString("system_unvalid"));
            }

            String[] addresss = xxlJobGroup.getAddressList().split(",");
            for (String item : addresss) {
                if (StringTool.isBlank(item)) {
                    return ReturnMessage.fail(I18nUtil.getString("jobgroup_field_registryList_unvalid"));
                }
            }
        }

        // process
        xxlJobGroup.setUpdateTime(new Date());

        int ret = xxlJobGroupMapper.save(xxlJobGroup);
        return ReturnMessage.success(I18nUtils.getMessage(context, "save.result"));
    }

    @Operation(summary = "更新执行器")
    @RequestMapping(path = "/update", method = RequestMethod.POST)
    @ResponseBody
    @SMRequiresPermissions(menu = "executor_list", apiKey = "update", apiDesc = "更新执行器")
    public ReturnMessage<String> update(@RequestBody XxlJobGroup xxlJobGroup) {
        // valid
        if (StringTool.isBlank(xxlJobGroup.getAppname())) {
            return ReturnMessage.fail((I18nUtil.getString("system_please_input") + "AppName"));
        }
        if (xxlJobGroup.getAppname().length() < 4 || xxlJobGroup.getAppname().length() > 64) {
            return ReturnMessage.fail(I18nUtil.getString("jobgroup_field_appname_length"));
        }
        if (StringTool.isBlank(xxlJobGroup.getTitle())) {
            return ReturnMessage.fail((I18nUtil.getString("system_please_input") + I18nUtil.getString("jobgroup_field_title")));
        }
        if (xxlJobGroup.getAddressType() == 0) {
            // 0=自动注册
            List<String> registryList = findRegistryByAppName(xxlJobGroup.getAppname());
            String addressListStr = null;
            if (CollectionTool.isNotEmpty(registryList)) {
                Collections.sort(registryList);
                addressListStr = String.join(",", registryList);
            }
            xxlJobGroup.setAddressList(addressListStr);
        } else {
            // 1=手动录入
            if (StringTool.isBlank(xxlJobGroup.getAddressList())) {
                return ReturnMessage.fail(I18nUtil.getString("jobgroup_field_addressType_limit"));
            }
            String[] addresss = xxlJobGroup.getAddressList().split(",");
            for (String item : addresss) {
                if (StringTool.isBlank(item)) {
                    return ReturnMessage.fail(I18nUtil.getString("jobgroup_field_registryList_unvalid"));
                }
            }
        }

        // process
        xxlJobGroup.setUpdateTime(new Date());

        int ret = xxlJobGroupMapper.update(xxlJobGroup);
        return ReturnMessage.success(I18nUtils.getMessage(context, "update.result"));
    }

    private List<String> findRegistryByAppName(String appnameParam) {
        HashMap<String, List<String>> appAddressMap = new HashMap<>();
        List<XxlJobRegistry> list = xxlJobRegistryMapper.findAll(RegistryConfig.DEAD_TIMEOUT, new Date());
        if (CollectionTool.isNotEmpty(list)) {
            for (XxlJobRegistry item : list) {
                if (!RegistryConfig.RegistType.EXECUTOR.name().equals(item.getRegistryGroup())) {
                    continue;
                }

                String appname = item.getRegistryKey();
                List<String> registryList = appAddressMap.computeIfAbsent(appname, k -> new ArrayList<>());

                if (!registryList.contains(item.getRegistryValue())) {
                    registryList.add(item.getRegistryValue());
                }
            }
        }
        return appAddressMap.get(appnameParam);
    }

    @Operation(summary = "删除执行器")
    @RequestMapping(path = "/remove", method = RequestMethod.DELETE)
    @ResponseBody
    @SMRequiresPermissions(menu = "executor_list", apiKey = "remove", apiDesc = "删除执行器")
    public ReturnMessage<String> remove(@RequestParam("id") int id) {

        // valid
        int count = xxlJobInfoMapper.pageListCount(0, 10, id, -1, null, null, null);
        if (count > 0) {
            return ReturnMessage.fail(I18nUtil.getString("jobgroup_del_limit_0"));
        }

        List<XxlJobGroup> allList = xxlJobGroupMapper.findAll();
        if (allList.size() == 1) {
            return ReturnMessage.fail(I18nUtil.getString("jobgroup_del_limit_1"));
        }

        int ret = xxlJobGroupMapper.remove(id);
        return ReturnMessage.success(I18nUtils.getMessage(context, "delete.result"));
    }

    @Operation(summary = "根据ID查询执行器")
    @RequestMapping(path = "/loadById", method = RequestMethod.GET)
    @ResponseBody
    @SMRequiresPermissions(menu = "executor_list", apiKey = "loadById", apiDesc = "根据ID查询执行器")
    public ReturnMessage<XxlJobGroup> loadById(@RequestParam("id") int id) {
        XxlJobGroup jobGroup = xxlJobGroupMapper.load(id);
        return ReturnMessage.success(jobGroup);
    }

}
