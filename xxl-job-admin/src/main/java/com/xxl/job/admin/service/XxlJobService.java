package com.xxl.job.admin.service;


import com.github.hbq969.code.common.restful.ReturnMessage;
import com.github.pagehelper.PageInfo;
import com.xxl.job.admin.model.XxlJobInfo;
import com.xxl.job.admin.model.XxlJobUser;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.sso.core.model.LoginInfo;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * core job action for xxl-job
 * 
 * @author xuxueli 2016-5-28 15:30:33
 */
public interface XxlJobService {

	/**
	 * page list
	 *
	 * @param start
	 * @param length
	 * @param jobGroup
	 * @param jobDesc
	 * @param executorHandler
	 * @param author
	 * @return
	 */
	public PageInfo<XxlJobInfo> pageList(int start, int length, Integer jobGroup, Integer triggerStatus, String jobDesc, String executorHandler, String author);

	XxlJobInfo queryJobInfo(Integer id);

	/**
	 * add job
	 *
	 * @param jobInfo
	 * @return
	 */
	public ReturnMessage<String> add(XxlJobInfo jobInfo, LoginInfo loginInfo);

	/**
	 * update job
	 *
	 * @param jobInfo
	 * @return
	 */
	public ReturnMessage<String> update(XxlJobInfo jobInfo, LoginInfo loginInfo);

	/**
	 * remove job
	 * 	 *
	 * @param id
	 * @return
	 */
	public ReturnMessage<String> remove(int id, LoginInfo loginInfo);

	/**
	 * start job
	 *
	 * @param id
	 * @return
	 */
	public ReturnMessage<String> start(int id, LoginInfo loginInfo);

	/**
	 * stop job
	 *
	 * @param id
	 * @return
	 */
	public ReturnMessage<String> stop(int id, LoginInfo loginInfo);

	/**
	 * trigger
	 *
	 * @param loginInfo
	 * @param jobId
	 * @param executorParam
	 * @param addressList
	 * @return
	 */
	public ReturnMessage<String> trigger(LoginInfo loginInfo, int jobId, String executorParam, String addressList);

	/**
	 * dashboard info
	 *
	 * @return
	 */
	public Map<String,Object> dashboardInfo();

	/**
	 * chart info
	 *
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public ReturnT<Map<String,Object>> chartInfo(Date startDate, Date endDate);

	ReturnMessage<String> batchStart(List<Integer> ids, LoginInfo loginInfo);

	ReturnMessage<String> batchStop(List<Integer> ids, LoginInfo loginInfo);

	ReturnMessage<String> batchUpdateJobGroup(List<Integer> ids, Integer jobGroup);

}
