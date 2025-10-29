delete from h_sm_info where app='xxl-job-admin';
insert into h_sm_info(app,info_content) values('xxl-job-admin','{"title":"XXL-任务中心"}');

delete from h_dict_base where dict_name in ('xxl-job-admin,addressType','xxl-job-admin,triggerStatus','xxl-job-admin,scheduleType','xxl-job-admin,glueType','xxl-job-admin,executorRouteStrategy','xxl-job-admin,misfireStrategy','xxl-job-admin,executorBlockStrategy','xxl-job-admin,logStatus','xxl-job-admin,code','xxl-job-admin,logClearType') and app='xxl-job-admin';
delete from h_dict_pairs where dict_name in ('xxl-job-admin,addressType','xxl-job-admin,triggerStatus','xxl-job-admin,scheduleType','xxl-job-admin,glueType','xxl-job-admin,executorRouteStrategy','xxl-job-admin,misfireStrategy','xxl-job-admin,executorBlockStrategy','xxl-job-admin,logStatus','xxl-job-admin,code','xxl-job-admin,logClearType');
insert into h_dict_base(dict_name,dict_desc,dict_source,key_column,val_column,app) values('xxl-job-admin,addressType','注册方式',1,'key','value','xxl-job-admin');
insert into h_dict_pairs(dict_name,pair_key,pair_value) values('xxl-job-admin,addressType','0','自动注册');
insert into h_dict_pairs(dict_name,pair_key,pair_value) values('xxl-job-admin,addressType','1','手工注册');
insert into h_dict_base(dict_name,dict_desc,dict_source,key_column,val_column,app) values('xxl-job-admin,triggerStatus','任务状态',1,'key','value','xxl-job-admin');
insert into h_dict_pairs(dict_name,pair_key,pair_value) values('xxl-job-admin,triggerStatus','-1','全部');
insert into h_dict_pairs(dict_name,pair_key,pair_value) values('xxl-job-admin,triggerStatus','0','停止');
insert into h_dict_pairs(dict_name,pair_key,pair_value) values('xxl-job-admin,triggerStatus','1','启动');
insert into h_dict_base(dict_name,dict_desc,dict_source,key_column,val_column,app) values('xxl-job-admin,scheduleType','调度类型',1,'key','value','xxl-job-admin');
insert into h_dict_pairs(dict_name,pair_key,pair_value) values('xxl-job-admin,scheduleType','NONE','无');
insert into h_dict_pairs(dict_name,pair_key,pair_value) values('xxl-job-admin,scheduleType','CRON','CRON');
insert into h_dict_pairs(dict_name,pair_key,pair_value) values('xxl-job-admin,scheduleType','FIX_RATE','固定速度');
insert into h_dict_base(dict_name,dict_desc,dict_source,key_column,val_column,app) values('xxl-job-admin,glueType','运行模式',1,'key','value','xxl-job-admin');
insert into h_dict_pairs(dict_name,pair_key,pair_value) values('xxl-job-admin,glueType','BEAN','BEAN');
insert into h_dict_pairs(dict_name,pair_key,pair_value) values('xxl-job-admin,glueType','GLUE_GROOVY','GLUE(Java)');
insert into h_dict_pairs(dict_name,pair_key,pair_value) values('xxl-job-admin,glueType','GLUE_SHELL','GLUE(Shell)');
insert into h_dict_pairs(dict_name,pair_key,pair_value) values('xxl-job-admin,glueType','GLUE_PYTHON','GLUE(Python3)');
insert into h_dict_pairs(dict_name,pair_key,pair_value) values('xxl-job-admin,glueType','GLUE_PYTHON2','GLUE(Python2)');
insert into h_dict_pairs(dict_name,pair_key,pair_value) values('xxl-job-admin,glueType','GLUE_NODEJS','GLUE(Nodejs)');
insert into h_dict_pairs(dict_name,pair_key,pair_value) values('xxl-job-admin,glueType','GLUE_POWERSHELL','GLUE(PowerShell)');
insert into h_dict_pairs(dict_name,pair_key,pair_value) values('xxl-job-admin,glueType','GLUE_PHP','GLUE(PHP)');
insert into h_dict_base(dict_name,dict_desc,dict_source,key_column,val_column,app) values('xxl-job-admin,executorRouteStrategy','执行器路由策略',1,'key','value','xxl-job-admin');
insert into h_dict_pairs(dict_name,pair_key,pair_value) values('xxl-job-admin,executorRouteStrategy','FIRST','第一个');
insert into h_dict_pairs(dict_name,pair_key,pair_value) values('xxl-job-admin,executorRouteStrategy','LAST','最后一个');
insert into h_dict_pairs(dict_name,pair_key,pair_value) values('xxl-job-admin,executorRouteStrategy','ROUND','轮询');
insert into h_dict_pairs(dict_name,pair_key,pair_value) values('xxl-job-admin,executorRouteStrategy','RANDOM','随机');
insert into h_dict_pairs(dict_name,pair_key,pair_value) values('xxl-job-admin,executorRouteStrategy','CONSISTENT_HASH','一致性HASH');
insert into h_dict_pairs(dict_name,pair_key,pair_value) values('xxl-job-admin,executorRouteStrategy','LEAST_FREQUENTLY_USED','最不经常使用');
insert into h_dict_pairs(dict_name,pair_key,pair_value) values('xxl-job-admin,executorRouteStrategy','LEAST_RECENTLY_USED','最近最久未使用');
insert into h_dict_pairs(dict_name,pair_key,pair_value) values('xxl-job-admin,executorRouteStrategy','FAILOVER','故障转移');
insert into h_dict_pairs(dict_name,pair_key,pair_value) values('xxl-job-admin,executorRouteStrategy','BUSYOVER','忙碌转移');
insert into h_dict_pairs(dict_name,pair_key,pair_value) values('xxl-job-admin,executorRouteStrategy','SHARDING_BROADCAST','分片广播');
insert into h_dict_base(dict_name,dict_desc,dict_source,key_column,val_column,app) values('xxl-job-admin,misfireStrategy','调度过期策略',1,'key','value','xxl-job-admin');
insert into h_dict_pairs(dict_name,pair_key,pair_value) values('xxl-job-admin,misfireStrategy','DO_NOTHING','忽略');
insert into h_dict_pairs(dict_name,pair_key,pair_value) values('xxl-job-admin,misfireStrategy','FIRE_ONCE_NOW','立即执行一次');
insert into h_dict_base(dict_name,dict_desc,dict_source,key_column,val_column,app) values('xxl-job-admin,executorBlockStrategy','阻塞处理策略',1,'key','value','xxl-job-admin');
insert into h_dict_pairs(dict_name,pair_key,pair_value) values('xxl-job-admin,executorBlockStrategy','SERIAL_EXECUTION','单机串行');
insert into h_dict_pairs(dict_name,pair_key,pair_value) values('xxl-job-admin,executorBlockStrategy','DISCARD_LATER','丢弃后续调度');
insert into h_dict_pairs(dict_name,pair_key,pair_value) values('xxl-job-admin,executorBlockStrategy','COVER_EARLY','覆盖之前调度');
insert into h_dict_base(dict_name,dict_desc,dict_source,key_column,val_column,app) values('xxl-job-admin,logStatus','日志状态',1,'key','value','xxl-job-admin');
insert into h_dict_pairs(dict_name,pair_key,pair_value) values('xxl-job-admin,logStatus','-1','全部');
insert into h_dict_pairs(dict_name,pair_key,pair_value) values('xxl-job-admin,logStatus','1','成功');
insert into h_dict_pairs(dict_name,pair_key,pair_value) values('xxl-job-admin,logStatus','2','失败');
insert into h_dict_pairs(dict_name,pair_key,pair_value) values('xxl-job-admin,logStatus','3','运行中');
insert into h_dict_base(dict_name,dict_desc,dict_source,key_column,val_column,app) values('xxl-job-admin,code','结果状态',1,'key','value','xxl-job-admin');
insert into h_dict_pairs(dict_name,pair_key,pair_value) values('xxl-job-admin,code','200','成功');
insert into h_dict_pairs(dict_name,pair_key,pair_value) values('xxl-job-admin,code','500','失败');
insert into h_dict_base(dict_name,dict_desc,dict_source,key_column,val_column,app) values('xxl-job-admin,logClearType','结果状态',1,'key','value','xxl-job-admin');
insert into h_dict_pairs(dict_name,pair_key,pair_value) values('xxl-job-admin,logClearType','1','清理一个月之前日志数据');
insert into h_dict_pairs(dict_name,pair_key,pair_value) values('xxl-job-admin,logClearType','2','清理三个月之前日志数据');
insert into h_dict_pairs(dict_name,pair_key,pair_value) values('xxl-job-admin,logClearType','3','清理六个月之前日志数据');
insert into h_dict_pairs(dict_name,pair_key,pair_value) values('xxl-job-admin,logClearType','4','清理一年之前日志数据');
insert into h_dict_pairs(dict_name,pair_key,pair_value) values('xxl-job-admin,logClearType','5','清理一千条以前日志数据');
insert into h_dict_pairs(dict_name,pair_key,pair_value) values('xxl-job-admin,logClearType','6','清理一万条以前日志数据');
insert into h_dict_pairs(dict_name,pair_key,pair_value) values('xxl-job-admin,logClearType','7','清理三万条以前日志数据');
insert into h_dict_pairs(dict_name,pair_key,pair_value) values('xxl-job-admin,logClearType','8','清理十万条以前日志数据');
insert into h_dict_pairs(dict_name,pair_key,pair_value) values('xxl-job-admin,logClearType','9','清理所有日志数据');

delete from h_menus where app='xxl-job-admin' and name in ('xxl-job','executor_list','task_list','schedule_log','xxl_user_list');
insert into h_menus(app,name,menu_desc,url,parent_key,order_index,menu_level,icon_name,created_at) values('xxl-job-admin','xxl-job','任务管理','/xxl-job','-',0,1,'timetask',1735800456);
insert into h_menus(app,name,menu_desc,url,parent_key,order_index,menu_level,icon_name,created_at) values('xxl-job-admin','executor_list','执行器管理','inner:${menuPrefix}/xxl-job-admin/xxl-job-ui/index.html#/executor/list','xxl-job',0,2,'route_unplug',1735800456);
insert into h_menus(app,name,menu_desc,url,parent_key,order_index,menu_level,icon_name,created_at) values('xxl-job-admin','task_list','任务配置','inner:${menuPrefix}/xxl-job-admin/xxl-job-ui/index.html#/task/list','xxl-job',1,2,'media2',1735800456);
insert into h_menus(app,name,menu_desc,url,parent_key,order_index,menu_level,icon_name,created_at) values('xxl-job-admin','schedule_log','日志查询','inner:${menuPrefix}/xxl-job-admin/xxl-job-ui/index.html#/log/list','xxl-job',2,2,'logs',1735800456);
insert into h_menus(app,name,menu_desc,url,parent_key,order_index,menu_level,icon_name,created_at) values('xxl-job-admin','xxl_user_list','权限配置','inner:${menuPrefix}/xxl-job-admin/xxl-job-ui/index.html#/user/perm','xxl-job',3,2,'PermissionIcon',1735800456);

INSERT INTO `xxl_job_group`(`id`, `app_name`, `title`, `address_type`, `address_list`, `update_time`) VALUES (1, 'xxl-job-executor-sample', '通用执行器Sample', 0, NULL, now()),
       (2, 'xxl-job-executor-sample-ai', 'AI执行器Sample', 0, NULL, now());

INSERT INTO `xxl_job_info`(`id`, `job_group`, `job_desc`, `add_time`, `update_time`, `author`, `alarm_email`,
                           `schedule_type`, `schedule_conf`, `misfire_strategy`, `executor_route_strategy`,
                           `executor_handler`, `executor_param`, `executor_block_strategy`, `executor_timeout`,
                           `executor_fail_retry_count`, `glue_type`, `glue_source`, `glue_remark`, `glue_updatetime`,
                           `child_jobid`)
VALUES (1, 1, '示例任务01', now(), now(), 'XXL', '', 'CRON', '0 0 0 * * ? *',
        'DO_NOTHING', 'FIRST', 'demoJobHandler', '', 'SERIAL_EXECUTION', 0, 0, 'BEAN', '', 'GLUE代码初始化',
        now(), ''),
       (2, 2, 'Ollama示例任务01', now(), now(), 'XXL', '', 'NONE', '',
        'DO_NOTHING', 'FIRST', 'ollamaJobHandler', '{
    "input": "慢SQL问题分析思路",
    "prompt": "你是一个研发工程师，擅长解决技术类问题。",
    "model": "qwen3:0.6b"
}', 'SERIAL_EXECUTION', 0, 0, 'BEAN', '', 'GLUE代码初始化',
        now(), ''),
       (3, 2, 'Dify示例任务', now(), now(), 'XXL', '', 'NONE', '',
        'DO_NOTHING', 'FIRST', 'difyWorkflowJobHandler', '{
    "inputs":{
        "input":"查询班级各学科前三名"
    },
    "user": "xxl-job",
    "baseUrl": "http://localhost/v1",
    "apiKey": "app-OUVgNUOQRIMokfmuJvBJoUTN"
}', 'SERIAL_EXECUTION', 0, 0, 'BEAN', '', 'GLUE代码初始化',
        now(), '');

INSERT INTO `xxl_job_user`(`id`, `username`, `password`, `role`, `permission`) VALUES (1, 'admin', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 1, NULL);

INSERT INTO `xxl_job_lock` (`lock_name`) VALUES ('schedule_lock');
