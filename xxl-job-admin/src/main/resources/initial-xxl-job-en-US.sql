delete from h_sm_info where app='xxl-job-admin';
insert into h_sm_info(app,info_content) values('xxl-job-admin','{"title":"XXL-JOB"}');

delete from h_dict_base where dict_name in ('xxl-job-admin,addressType','xxl-job-admin,triggerStatus','xxl-job-admin,scheduleType','xxl-job-admin,glueType','xxl-job-admin,executorRouteStrategy','xxl-job-admin,misfireStrategy','xxl-job-admin,executorBlockStrategy','xxl-job-admin,logStatus','xxl-job-admin,code','xxl-job-admin,logClearType') and app='xxl-job-admin';
delete from h_dict_pairs where dict_name in ('xxl-job-admin,addressType','xxl-job-admin,triggerStatus','xxl-job-admin,scheduleType','xxl-job-admin,glueType','xxl-job-admin,executorRouteStrategy','xxl-job-admin,misfireStrategy','xxl-job-admin,executorBlockStrategy','xxl-job-admin,logStatus','xxl-job-admin,code','xxl-job-admin,logClearType');
insert into h_dict_base(dict_name,dict_desc,dict_source,key_column,val_column,app) values('xxl-job-admin,addressType','Registration method',1,'key','value','xxl-job-admin');
insert into h_dict_pairs(dict_name,pair_key,pair_value) values('xxl-job-admin,addressType','0','Automatic logon');
insert into h_dict_pairs(dict_name,pair_key,pair_value) values('xxl-job-admin,addressType','1','Manual registration');
insert into h_dict_base(dict_name,dict_desc,dict_source,key_column,val_column,app) values('xxl-job-admin,triggerStatus','Task status',1,'key','value','xxl-job-admin');
insert into h_dict_pairs(dict_name,pair_key,pair_value) values('xxl-job-admin,triggerStatus','-1','All');
insert into h_dict_pairs(dict_name,pair_key,pair_value) values('xxl-job-admin,triggerStatus','0','Stop');
insert into h_dict_pairs(dict_name,pair_key,pair_value) values('xxl-job-admin,triggerStatus','1','Start');
insert into h_dict_base(dict_name,dict_desc,dict_source,key_column,val_column,app) values('xxl-job-admin,scheduleType','Scheduling type',1,'key','value','xxl-job-admin');
insert into h_dict_pairs(dict_name,pair_key,pair_value) values('xxl-job-admin,scheduleType','NONE','None');
insert into h_dict_pairs(dict_name,pair_key,pair_value) values('xxl-job-admin,scheduleType','CRON','CRON');
insert into h_dict_pairs(dict_name,pair_key,pair_value) values('xxl-job-admin,scheduleType','FIX_RATE','FIX_RATE');
insert into h_dict_base(dict_name,dict_desc,dict_source,key_column,val_column,app) values('xxl-job-admin,glueType','Operation mode',1,'key','value','xxl-job-admin');
insert into h_dict_pairs(dict_name,pair_key,pair_value) values('xxl-job-admin,glueType','BEAN','BEAN');
insert into h_dict_pairs(dict_name,pair_key,pair_value) values('xxl-job-admin,glueType','GLUE_GROOVY','GLUE(Java)');
insert into h_dict_pairs(dict_name,pair_key,pair_value) values('xxl-job-admin,glueType','GLUE_SHELL','GLUE(Shell)');
insert into h_dict_pairs(dict_name,pair_key,pair_value) values('xxl-job-admin,glueType','GLUE_PYTHON','GLUE(Python3)');
insert into h_dict_pairs(dict_name,pair_key,pair_value) values('xxl-job-admin,glueType','GLUE_PYTHON2','GLUE(Python2)');
insert into h_dict_pairs(dict_name,pair_key,pair_value) values('xxl-job-admin,glueType','GLUE_NODEJS','GLUE(Nodejs)');
insert into h_dict_pairs(dict_name,pair_key,pair_value) values('xxl-job-admin,glueType','GLUE_POWERSHELL','GLUE(PowerShell)');
insert into h_dict_pairs(dict_name,pair_key,pair_value) values('xxl-job-admin,glueType','GLUE_PHP','GLUE(PHP)');
insert into h_dict_base(dict_name,dict_desc,dict_source,key_column,val_column,app) values('xxl-job-admin,executorRouteStrategy','Executor Route Strategy',1,'key','value','xxl-job-admin');
insert into h_dict_pairs(dict_name,pair_key,pair_value) values('xxl-job-admin,executorRouteStrategy','FIRST','First');
insert into h_dict_pairs(dict_name,pair_key,pair_value) values('xxl-job-admin,executorRouteStrategy','LAST','Last');
insert into h_dict_pairs(dict_name,pair_key,pair_value) values('xxl-job-admin,executorRouteStrategy','ROUND','Round');
insert into h_dict_pairs(dict_name,pair_key,pair_value) values('xxl-job-admin,executorRouteStrategy','RANDOM','Random');
insert into h_dict_pairs(dict_name,pair_key,pair_value) values('xxl-job-admin,executorRouteStrategy','CONSISTENT_HASH','Consistent Hash');
insert into h_dict_pairs(dict_name,pair_key,pair_value) values('xxl-job-admin,executorRouteStrategy','LEAST_FREQUENTLY_USED','Least Frequently Used');
insert into h_dict_pairs(dict_name,pair_key,pair_value) values('xxl-job-admin,executorRouteStrategy','LEAST_RECENTLY_USED','Least Recently Used');
insert into h_dict_pairs(dict_name,pair_key,pair_value) values('xxl-job-admin,executorRouteStrategy','FAILOVER','Failover');
insert into h_dict_pairs(dict_name,pair_key,pair_value) values('xxl-job-admin,executorRouteStrategy','BUSYOVER','Busy Over');
insert into h_dict_pairs(dict_name,pair_key,pair_value) values('xxl-job-admin,executorRouteStrategy','SHARDING_BROADCAST','Sharding Broadcast');
insert into h_dict_base(dict_name,dict_desc,dict_source,key_column,val_column,app) values('xxl-job-admin,misfireStrategy','Misfire Strategy',1,'key','value','xxl-job-admin');
insert into h_dict_pairs(dict_name,pair_key,pair_value) values('xxl-job-admin,misfireStrategy','DO_NOTHING','Ignore');
insert into h_dict_pairs(dict_name,pair_key,pair_value) values('xxl-job-admin,misfireStrategy','FIRE_ONCE_NOW','Fire once now');
insert into h_dict_base(dict_name,dict_desc,dict_source,key_column,val_column,app) values('xxl-job-admin,executorBlockStrategy','Executor Block Strategy',1,'key','value','xxl-job-admin');
insert into h_dict_pairs(dict_name,pair_key,pair_value) values('xxl-job-admin,executorBlockStrategy','SERIAL_EXECUTION','Serial Execution');
insert into h_dict_pairs(dict_name,pair_key,pair_value) values('xxl-job-admin,executorBlockStrategy','DISCARD_LATER','Discard Later');
insert into h_dict_pairs(dict_name,pair_key,pair_value) values('xxl-job-admin,executorBlockStrategy','COVER_EARLY','Cover Early');
insert into h_dict_base(dict_name,dict_desc,dict_source,key_column,val_column,app) values('xxl-job-admin,logStatus','Log status',1,'key','value','xxl-job-admin');
insert into h_dict_pairs(dict_name,pair_key,pair_value) values('xxl-job-admin,logStatus','-1','All');
insert into h_dict_pairs(dict_name,pair_key,pair_value) values('xxl-job-admin,logStatus','1','Success');
insert into h_dict_pairs(dict_name,pair_key,pair_value) values('xxl-job-admin,logStatus','2','Failure');
insert into h_dict_pairs(dict_name,pair_key,pair_value) values('xxl-job-admin,logStatus','3','Running');
insert into h_dict_base(dict_name,dict_desc,dict_source,key_column,val_column,app) values('xxl-job-admin,code','Result status',1,'key','value','xxl-job-admin');
insert into h_dict_pairs(dict_name,pair_key,pair_value) values('xxl-job-admin,code','200','Success');
insert into h_dict_pairs(dict_name,pair_key,pair_value) values('xxl-job-admin,code','500','Failure');
insert into h_dict_base(dict_name,dict_desc,dict_source,key_column,val_column,app) values('xxl-job-admin,logClearType','Log clear type',1,'key','value','xxl-job-admin');
insert into h_dict_pairs(dict_name,pair_key,pair_value) values('xxl-job-admin,logClearType','1','Clear logs one month ago');
insert into h_dict_pairs(dict_name,pair_key,pair_value) values('xxl-job-admin,logClearType','2','Clear logs three months ago');
insert into h_dict_pairs(dict_name,pair_key,pair_value) values('xxl-job-admin,logClearType','3','Clear logs six months ago');
insert into h_dict_pairs(dict_name,pair_key,pair_value) values('xxl-job-admin,logClearType','4','Clear logs one year ago');
insert into h_dict_pairs(dict_name,pair_key,pair_value) values('xxl-job-admin,logClearType','5','Clear logs one thousand ago');
insert into h_dict_pairs(dict_name,pair_key,pair_value) values('xxl-job-admin,logClearType','6','Clear logs one ten thousand ago');
insert into h_dict_pairs(dict_name,pair_key,pair_value) values('xxl-job-admin,logClearType','7','Clear logs one thousand thousand ago');
insert into h_dict_pairs(dict_name,pair_key,pair_value) values('xxl-job-admin,logClearType','8','Clear logs one ten thousand ago');
insert into h_dict_pairs(dict_name,pair_key,pair_value) values('xxl-job-admin,logClearType','9','Clear all logs');

delete from h_menus where app='xxl-job-admin' and name in ('xxl-job','executor_list','task_list','schedule_log','xxl_user_list');
insert into h_menus(app,name,menu_desc,url,parent_key,order_index,menu_level,icon_name,created_at) values('xxl-job-admin','xxl-job','Task Management','/xxl-job','-',0,1,'timetask',1735800456);
insert into h_menus(app,name,menu_desc,url,parent_key,order_index,menu_level,icon_name,created_at) values('xxl-job-admin','executor_list','Executor Management','inner:${menuPrefix}/xxl-job-admin/xxl-job-ui/index.html#/executor/list','xxl-job',0,2,'route_unplug',1735800456);
insert into h_menus(app,name,menu_desc,url,parent_key,order_index,menu_level,icon_name,created_at) values('xxl-job-admin','task_list','Task configuration','inner:${menuPrefix}/xxl-job-admin/xxl-job-ui/index.html#/task/list','xxl-job',1,2,'media2',1735800456);
insert into h_menus(app,name,menu_desc,url,parent_key,order_index,menu_level,icon_name,created_at) values('xxl-job-admin','schedule_log','Log query','inner:${menuPrefix}/xxl-job-admin/xxl-job-ui/index.html#/log/list','xxl-job',2,2,'logs',1735800456);
insert into h_menus(app,name,menu_desc,url,parent_key,order_index,menu_level,icon_name,created_at) values('xxl-job-admin','xxl_user_list','Permission Configuration','inner:${menuPrefix}/xxl-job-admin/xxl-job-ui/index.html#/user/perm','xxl-job',3,2,'PermissionIcon',1735800456);

INSERT INTO `xxl_job_group`(`id`, `app_name`, `title`, `address_type`, `address_list`, `update_time`) VALUES (1, 'xxl-job-executor-sample', 'Universal executor sample', 0, NULL, now()),
       (2, 'xxl-job-executor-sample-ai', 'AI executor sample', 0, NULL, now());

INSERT INTO `xxl_job_info`(`id`, `job_group`, `job_desc`, `add_time`, `update_time`, `author`, `alarm_email`,
                           `schedule_type`, `schedule_conf`, `misfire_strategy`, `executor_route_strategy`,
                           `executor_handler`, `executor_param`, `executor_block_strategy`, `executor_timeout`,
                           `executor_fail_retry_count`, `glue_type`, `glue_source`, `glue_remark`, `glue_updatetime`,
                           `child_jobid`)
VALUES (1, 1, 'Example Task 01', now(), now(), 'XXL', '', 'CRON', '0 0 0 * * ? *',
        'DO_NOTHING', 'FIRST', 'demoJobHandler', '', 'SERIAL_EXECUTION', 0, 0, 'BEAN', '', 'GLUE代码初始化',
        now(), ''),
       (2, 2, 'Olama Example Task 01', now(), now(), 'XXL', '', 'NONE', '',
        'DO_NOTHING', 'FIRST', 'ollamaJobHandler', '{
    "input": "Analysis ideas for slow SQL problems",
    "prompt": "You are a research and development engineer, skilled in solving technical problems.",
    "model": "qwen3:0.6b"
}', 'SERIAL_EXECUTION', 0, 0, 'BEAN', '', 'GLUE代码初始化',
        now(), ''),
       (3, 2, 'Dify Example Task', now(), now(), 'XXL', '', 'NONE', '',
        'DO_NOTHING', 'FIRST', 'difyWorkflowJobHandler', '{
    "inputs":{
        "input":"Search for the top three students in each subject of the class"
    },
    "user": "xxl-job",
    "baseUrl": "http://localhost/v1",
    "apiKey": "app-OUVgNUOQRIMokfmuJvBJoUTN"
}', 'SERIAL_EXECUTION', 0, 0, 'BEAN', '', 'GLUE代码初始化',
        now(), '');

INSERT INTO `xxl_job_user`(`id`, `username`, `password`, `role`, `permission`) VALUES (1, 'admin', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 1, NULL);

INSERT INTO `xxl_job_lock` (`lock_name`) VALUES ('schedule_lock');
