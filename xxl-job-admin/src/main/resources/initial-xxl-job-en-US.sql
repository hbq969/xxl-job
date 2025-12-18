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


insert into h_icons(name,icon_desc,icon_svg,icon_size,update_at) values('timetask','任务管理','<svg t="1766054858625" class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="47494" width="20" height="20"><path d="M873.6 1.088H146.56A145.472 145.472 0 0 0 1.024 146.56V873.6a145.408 145.408 0 0 0 145.408 145.536h727.104a145.472 145.472 0 0 0 145.472-145.536V146.56A145.472 145.472 0 0 0 873.6 1.088h0.064z m72.896 872.768a72.448 72.448 0 0 1-72.704 72.64H146.752a72.448 72.448 0 0 1-51.584-21.248 72.128 72.128 0 0 1-21.44-51.392V146.56c0-40.192 32.64-72.832 72.96-72.832h727.04a72.768 72.768 0 0 1 72.768 72.832v727.296zM828.416 328.32H537.536a36.352 36.352 0 1 0 0 72.704h290.88a36.352 36.352 0 1 0 0-72.704z m0 291.008H537.536a36.288 36.288 0 1 0 0 72.64h290.88a36.288 36.288 0 1 0 0-72.64zM319.36 528.384a127.296 127.296 0 1 0 0 254.528 127.296 127.296 0 0 0 0-254.464v-0.064z m38.592 165.888a54.592 54.592 0 1 1-78.016-76.416 54.592 54.592 0 0 1 78.016 76.416z m44.8-427.968l-119.68 119.744-47.168-46.976a36.288 36.288 0 1 0-51.328 51.392L257.28 463.168a36.288 36.288 0 0 0 51.328 0l145.472-145.408a36.352 36.352 0 0 0-51.328-51.456z" fill="#5584FF" p-id="47495"></path></svg>',20,1735800456);
insert into h_icons(name,icon_desc,icon_svg,icon_size,update_at) values('route_unplug','执行器管理','<svg t="1766054880914" class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="48619" width="20" height="20"><path d="M723.309714 948.736H300.178286A226.377143 226.377143 0 0 1 74.020571 722.651429V299.52a226.377143 226.377143 0 0 1 226.157715-226.157714H723.382857a226.377143 226.377143 0 0 1 226.157714 226.157714V722.651429a226.377143 226.377143 0 0 1-226.157714 226.084571zM300.178286 128.950857a170.788571 170.788571 0 0 0-170.569143 170.569143V722.651429a170.788571 170.788571 0 0 0 170.569143 170.569142H723.382857A170.788571 170.788571 0 0 0 893.805714 722.651429V299.52a170.788571 170.788571 0 0 0-170.569143-170.569143H300.178286z" fill="#26314A" p-id="48620"></path><path d="M756.224 340.406857H269.312a27.794286 27.794286 0 0 1 0-55.588571h486.912a27.794286 27.794286 0 0 1 0 55.588571z m0 192.365714H269.312a27.794286 27.794286 0 0 1 0-55.588571h486.912a27.794286 27.794286 0 0 1 0 55.588571z m0 192.365715H269.312a27.794286 27.794286 0 0 1 0-55.588572h486.912a27.794286 27.794286 0 0 1 0 55.588572z" fill="#26314A" p-id="48621"></path><path d="M242.102857 697.344a76.580571 76.580571 0 1 0 153.161143 0 76.580571 76.580571 0 0 0-153.234286 0zM568.905143 504.978286a76.580571 76.580571 0 1 0 153.234286 0 76.580571 76.580571 0 0 0-153.234286 0zM361.252571 312.612571a76.580571 76.580571 0 1 0 153.234286 0 76.580571 76.580571 0 0 0-153.234286 0z" fill="#26314A" p-id="48622"></path></svg>',20,1735800456);
insert into h_icons(name,icon_desc,icon_svg,icon_size,update_at) values('media2','任务配置','<svg t="1766054895490" class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="49769" width="20" height="20"><path d="M256 0v96a96 96 0 0 0 86.784 95.552L352 192h320a96 96 0 0 0 95.552-86.784L768 96V0h64a128 128 0 0 1 128 128v768a128 128 0 0 1-128 128H192A128 128 0 0 1 64 896V128A128 128 0 0 1 192 0h64z m480 768H288a32 32 0 0 0-5.76 63.488L288 832h448a32 32 0 1 0 0-64z m-6.528-414.208l-4.48 3.712L476.16 606.4 363.008 493.248l-4.48-3.712a32 32 0 0 0-44.48 44.544l3.648 4.48 113.152 113.088 5.568 4.992a64 64 0 0 0 78.912 0.384l6.016-5.376 248.96-248.96 3.648-4.352a32 32 0 0 0-44.48-44.544zM704 0v64a64 64 0 0 1-64 64H384a64 64 0 0 1-64-64V0h384z" fill="#00AAA6" p-id="49770"></path></svg>',20,1735800456);
insert into h_icons(name,icon_desc,icon_svg,icon_size,update_at) values('logs','日志查询','<svg t="1766054911637" class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="50892" width="20" height="20"><path d="M404.1 847.4H183.4l-0.1-666.7 376.7-0.1L687.5 310v45.4c0 22.1 17.9 40 40 40s40-17.9 40-40v-52.7c0-16.4-6.3-31.9-17.9-43.6l-137.9-140c-11.6-11.8-27.7-18.5-44.2-18.5l-405.9 0.1c-32.1 0-58.2 26.1-58.2 58.2l0.1 706.4c0 34.2 27.9 62.1 62.1 62.1h238.6c22.1 0 40-17.9 40-40-0.1-22.1-18-40-40.1-40z" p-id="50893"></path><path d="M909.7 843.5l-86.4-86.4c13.8-23.6 21.1-50.5 21.1-78.5 0-41.7-16.2-80.9-45.7-110.4-60.9-60.9-159.9-60.9-220.8 0-29.5 29.5-45.7 68.7-45.7 110.4s16.2 80.9 45.7 110.4c30.4 30.4 70.4 45.7 110.4 45.7 27.1 0 54.3-7 78.4-21.1l86.4 86.4c15.6 15.6 40.9 15.6 56.6 0 15.7-15.6 15.7-40.9 0-56.5z m-167.6-111c-29.7 29.7-78 29.7-107.7 0-14.4-14.4-22.3-33.5-22.3-53.8s7.9-39.5 22.3-53.8c14.8-14.8 34.3-22.3 53.8-22.3s39 7.4 53.8 22.3c14.4 14.4 22.3 33.5 22.3 53.8s-7.8 39.4-22.2 53.8zM546.9 340.3c0-17.6-14.3-31.9-31.9-31.9H304.7c-17.6 0-31.9 14.3-31.9 31.9 0 17.6 14.3 31.9 31.9 31.9h210.2c17.7 0 32-14.3 32-31.9zM415.6 469.8H304.7c-17.6 0-31.9 14.3-31.9 31.9 0 17.6 14.3 31.9 31.9 31.9h110.9c17.6 0 31.9-14.3 31.9-31.9 0-17.6-14.2-31.9-31.9-31.9zM415.6 631.2H304.7c-17.6 0-31.9 14.3-31.9 31.9 0 17.6 14.3 31.9 31.9 31.9h110.9c17.6 0 31.9-14.3 31.9-31.9 0-17.6-14.2-31.9-31.9-31.9z" p-id="50894"></path></svg>',20,1735800456);
insert into h_icons(name,icon_desc,icon_svg,icon_size,update_at) values('PermissionIcon','权限配置','<svg t="1766054926255" class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="52027" width="20" height="20"><path d="M602.765646 373.163489a90.605828 90.605828 0 1 1-90.548951-92.312153A91.800256 91.800256 0 0 1 602.765646 373.163489z m-272.329383 0a186.956283 186.956283 0 0 0 127.576191 177.343988 12.968066 12.968066 0 0 1 8.531623 12.228659v213.119924a12.797433 12.797433 0 0 0 12.683678 12.968066H544.409349a12.854311 12.854311 0 0 0 12.683679-12.968066v-20.475893a12.797433 12.797433 0 0 1 12.683678-12.968066h20.248384a12.797433 12.797433 0 0 0 12.683678-12.968066v-66.489776a12.797433 12.797433 0 0 0-12.683678-12.968066H570.004216a12.797433 12.797433 0 0 1-12.683678-12.968066v-73.940726a12.854311 12.854311 0 0 1 9.271029-12.456169 184.908693 184.908693 0 1 0-235.927794-177.457743zM521.089582 1.639778l424.704156 147.198923a29.746923 29.746923 0 0 1 19.850242 28.438741v365.89284a409.517869 409.517869 0 0 1-245.198825 379.315926l-196.341067 98.739308a28.836883 28.836883 0 0 1-24.684827 0l-200.891265-93.677212A406.958382 406.958382 0 0 1 58.050003 553.408229V177.220564a30.03131 30.03131 0 0 1 19.850241-28.438741L502.035625 1.5829a29.462536 29.462536 0 0 1 19.053957 0.056878z" fill="#1296db" p-id="52028"></path></svg>',20,1735800456);
