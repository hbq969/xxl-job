delete from h_sm_info where app='xxl-job-admin';
insert into h_sm_info(app,info_content) values('xxl-job-admin','{"title":"XXL-タスクセンター"}');

delete from h_dict_base where dict_name in ('xxl-job-admin,addressType','xxl-job-admin,triggerStatus','xxl-job-admin,scheduleType','xxl-job-admin,glueType','xxl-job-admin,executorRouteStrategy','xxl-job-admin,misfireStrategy','xxl-job-admin,executorBlockStrategy','xxl-job-admin,logStatus','xxl-job-admin,code','xxl-job-admin,logClearType') and app='xxl-job-admin';
delete from h_dict_pairs where dict_name in ('xxl-job-admin,addressType','xxl-job-admin,triggerStatus','xxl-job-admin,scheduleType','xxl-job-admin,glueType','xxl-job-admin,executorRouteStrategy','xxl-job-admin,misfireStrategy','xxl-job-admin,executorBlockStrategy','xxl-job-admin,logStatus','xxl-job-admin,code','xxl-job-admin,logClearType');
insert into h_dict_base(dict_name,dict_desc,dict_source,key_column,val_column,app) values('xxl-job-admin,addressType','登録方法',1,'key','value','xxl-job-admin');
insert into h_dict_pairs(dict_name,pair_key,pair_value) values('xxl-job-admin,addressType','0','自動登録');
insert into h_dict_pairs(dict_name,pair_key,pair_value) values('xxl-job-admin,addressType','1','手動登録');
insert into h_dict_base(dict_name,dict_desc,dict_source,key_column,val_column,app) values('xxl-job-admin,triggerStatus','タスクステータス',1,'key','value','xxl-job-admin');
insert into h_dict_pairs(dict_name,pair_key,pair_value) values('xxl-job-admin,triggerStatus','-1','全て');
insert into h_dict_pairs(dict_name,pair_key,pair_value) values('xxl-job-admin,triggerStatus','0','停止');
insert into h_dict_pairs(dict_name,pair_key,pair_value) values('xxl-job-admin,triggerStatus','1','起動');
insert into h_dict_base(dict_name,dict_desc,dict_source,key_column,val_column,app) values('xxl-job-admin,scheduleType','スケジュール・タイプ',1,'key','value','xxl-job-admin');
insert into h_dict_pairs(dict_name,pair_key,pair_value) values('xxl-job-admin,scheduleType','NONE','無');
insert into h_dict_pairs(dict_name,pair_key,pair_value) values('xxl-job-admin,scheduleType','CRON','CRON');
insert into h_dict_pairs(dict_name,pair_key,pair_value) values('xxl-job-admin,scheduleType','FIX_RATE','こていそくど');
insert into h_dict_base(dict_name,dict_desc,dict_source,key_column,val_column,app) values('xxl-job-admin,glueType','運転モード',1,'key','value','xxl-job-admin');
insert into h_dict_pairs(dict_name,pair_key,pair_value) values('xxl-job-admin,glueType','BEAN','BEAN');
insert into h_dict_pairs(dict_name,pair_key,pair_value) values('xxl-job-admin,glueType','GLUE_GROOVY','GLUE(Java)');
insert into h_dict_pairs(dict_name,pair_key,pair_value) values('xxl-job-admin,glueType','GLUE_SHELL','GLUE(Shell)');
insert into h_dict_pairs(dict_name,pair_key,pair_value) values('xxl-job-admin,glueType','GLUE_PYTHON','GLUE(Python3)');
insert into h_dict_pairs(dict_name,pair_key,pair_value) values('xxl-job-admin,glueType','GLUE_PYTHON2','GLUE(Python2)');
insert into h_dict_pairs(dict_name,pair_key,pair_value) values('xxl-job-admin,glueType','GLUE_NODEJS','GLUE(Nodejs)');
insert into h_dict_pairs(dict_name,pair_key,pair_value) values('xxl-job-admin,glueType','GLUE_POWERSHELL','GLUE(PowerShell)');
insert into h_dict_pairs(dict_name,pair_key,pair_value) values('xxl-job-admin,glueType','GLUE_PHP','GLUE(PHP)');
insert into h_dict_base(dict_name,dict_desc,dict_source,key_column,val_column,app) values('xxl-job-admin,executorRouteStrategy','アクチュエータルーティングポリシー',1,'key','value','xxl-job-admin');
insert into h_dict_pairs(dict_name,pair_key,pair_value) values('xxl-job-admin,executorRouteStrategy','FIRST','最初の');
insert into h_dict_pairs(dict_name,pair_key,pair_value) values('xxl-job-admin,executorRouteStrategy','LAST','最後の');
insert into h_dict_pairs(dict_name,pair_key,pair_value) values('xxl-job-admin,executorRouteStrategy','ROUND','ポーリング');
insert into h_dict_pairs(dict_name,pair_key,pair_value) values('xxl-job-admin,executorRouteStrategy','RANDOM','ランダム');
insert into h_dict_pairs(dict_name,pair_key,pair_value) values('xxl-job-admin,executorRouteStrategy','CONSISTENT_HASH','一貫性HASH');
insert into h_dict_pairs(dict_name,pair_key,pair_value) values('xxl-job-admin,executorRouteStrategy','LEAST_FREQUENTLY_USED','最も頻繁に使用しない');
insert into h_dict_pairs(dict_name,pair_key,pair_value) values('xxl-job-admin,executorRouteStrategy','LEAST_RECENTLY_USED','最近最も長く使用されていない');
insert into h_dict_pairs(dict_name,pair_key,pair_value) values('xxl-job-admin,executorRouteStrategy','FAILOVER','フェイルオーバ');
insert into h_dict_pairs(dict_name,pair_key,pair_value) values('xxl-job-admin,executorRouteStrategy','BUSYOVER','ビジー転送');
insert into h_dict_pairs(dict_name,pair_key,pair_value) values('xxl-job-admin,executorRouteStrategy','SHARDING_BROADCAST','スライス放送');
insert into h_dict_base(dict_name,dict_desc,dict_source,key_column,val_column,app) values('xxl-job-admin,misfireStrategy','スケジュールの有効期限ポリシー',1,'key','value','xxl-job-admin');
insert into h_dict_pairs(dict_name,pair_key,pair_value) values('xxl-job-admin,misfireStrategy','DO_NOTHING','無視');
insert into h_dict_pairs(dict_name,pair_key,pair_value) values('xxl-job-admin,misfireStrategy','FIRE_ONCE_NOW','今すぐ実行');
insert into h_dict_base(dict_name,dict_desc,dict_source,key_column,val_column,app) values('xxl-job-admin,executorBlockStrategy','ブロッキング処理ポリシー',1,'key','value','xxl-job-admin');
insert into h_dict_pairs(dict_name,pair_key,pair_value) values('xxl-job-admin,executorBlockStrategy','SERIAL_EXECUTION','スタンドアロンシリアル');
insert into h_dict_pairs(dict_name,pair_key,pair_value) values('xxl-job-admin,executorBlockStrategy','DISCARD_LATER','次のスケジュールを破棄');
insert into h_dict_pairs(dict_name,pair_key,pair_value) values('xxl-job-admin,executorBlockStrategy','COVER_EARLY','上書き前のスケジュール');
insert into h_dict_base(dict_name,dict_desc,dict_source,key_column,val_column,app) values('xxl-job-admin,logStatus','ログのステータス',1,'key','value','xxl-job-admin');
insert into h_dict_pairs(dict_name,pair_key,pair_value) values('xxl-job-admin,logStatus','-1','全て');
insert into h_dict_pairs(dict_name,pair_key,pair_value) values('xxl-job-admin,logStatus','1','成功');
insert into h_dict_pairs(dict_name,pair_key,pair_value) values('xxl-job-admin,logStatus','2','失敗');
insert into h_dict_pairs(dict_name,pair_key,pair_value) values('xxl-job-admin,logStatus','3','実行中');
insert into h_dict_base(dict_name,dict_desc,dict_source,key_column,val_column,app) values('xxl-job-admin,code','結果ステータス',1,'key','value','xxl-job-admin');
insert into h_dict_pairs(dict_name,pair_key,pair_value) values('xxl-job-admin,code','200','成功');
insert into h_dict_pairs(dict_name,pair_key,pair_value) values('xxl-job-admin,code','500','失敗');
insert into h_dict_base(dict_name,dict_desc,dict_source,key_column,val_column,app) values('xxl-job-admin,logClearType','結果ステータス',1,'key','value','xxl-job-admin');
insert into h_dict_pairs(dict_name,pair_key,pair_value) values('xxl-job-admin,logClearType','1','1か月前のログデータのクリーンアップ');
insert into h_dict_pairs(dict_name,pair_key,pair_value) values('xxl-job-admin,logClearType','2','3ヶ月前のログデータのクリーンアップ');
insert into h_dict_pairs(dict_name,pair_key,pair_value) values('xxl-job-admin,logClearType','3','6ヶ月前のログデータのクリーンアップ');
insert into h_dict_pairs(dict_name,pair_key,pair_value) values('xxl-job-admin,logClearType','4','1年前のログデータのクリーンアップ');
insert into h_dict_pairs(dict_name,pair_key,pair_value) values('xxl-job-admin,logClearType','5','1000件前のログデータを消去');
insert into h_dict_pairs(dict_name,pair_key,pair_value) values('xxl-job-admin,logClearType','6','1万件前のログデータを消去');
insert into h_dict_pairs(dict_name,pair_key,pair_value) values('xxl-job-admin,logClearType','7','3万件の過去ログデータを消去');
insert into h_dict_pairs(dict_name,pair_key,pair_value) values('xxl-job-admin,logClearType','8','10万件前のログデータを消去');
insert into h_dict_pairs(dict_name,pair_key,pair_value) values('xxl-job-admin,logClearType','9','ログデータをすべて消去');

delete from h_menus where app='xxl-job-admin' and name in ('xxl-job','executor_list','task_list','schedule_log','xxl_user_list');
insert into h_menus(app,name,menu_desc,url,parent_key,order_index,menu_level,icon_name,created_at) values('xxl-job-admin','xxl-job','タスク管理','/xxl-job','-',0,1,'timetask',1735800456);
insert into h_menus(app,name,menu_desc,url,parent_key,order_index,menu_level,icon_name,created_at) values('xxl-job-admin','executor_list','アクチュエータ管理','inner:${menuPrefix}/xxl-job-admin/xxl-job-ui/index.html#/executor/list','xxl-job',0,2,'route_unplug',1735800456);
insert into h_menus(app,name,menu_desc,url,parent_key,order_index,menu_level,icon_name,created_at) values('xxl-job-admin','task_list','タスク構成','inner:${menuPrefix}/xxl-job-admin/xxl-job-ui/index.html#/task/list','xxl-job',1,2,'media2',1735800456);
insert into h_menus(app,name,menu_desc,url,parent_key,order_index,menu_level,icon_name,created_at) values('xxl-job-admin','schedule_log','ログクエリー','inner:${menuPrefix}/xxl-job-admin/xxl-job-ui/index.html#/log/list','xxl-job',2,2,'logs',1735800456);
insert into h_menus(app,name,menu_desc,url,parent_key,order_index,menu_level,icon_name,created_at) values('xxl-job-admin','xxl_user_list','権限設定','inner:${menuPrefix}/xxl-job-admin/xxl-job-ui/index.html#/user/perm','xxl-job',3,2,'PermissionIcon',1735800456);

INSERT INTO `xxl_job_group`(`id`, `app_name`, `title`, `address_type`, `address_list`, `update_time`) VALUES (1, 'xxl-job-executor-sample', '汎用アクチュエータSample', 0, NULL, now()),
       (2, 'xxl-job-executor-sample-ai', 'AIアクチュエータSample', 0, NULL, now());

INSERT INTO `xxl_job_info`(`id`, `job_group`, `job_desc`, `add_time`, `update_time`, `author`, `alarm_email`,
                           `schedule_type`, `schedule_conf`, `misfire_strategy`, `executor_route_strategy`,
                           `executor_handler`, `executor_param`, `executor_block_strategy`, `executor_timeout`,
                           `executor_fail_retry_count`, `glue_type`, `glue_source`, `glue_remark`, `glue_updatetime`,
                           `child_jobid`)
VALUES (1, 1, 'サンプルタスク01', now(), now(), 'XXL', '', 'CRON', '0 0 0 * * ? *',
        'DO_NOTHING', 'FIRST', 'demoJobHandler', '', 'SERIAL_EXECUTION', 0, 0, 'BEAN', '', 'GLUE代码初始化',
        now(), ''),
       (2, 2, 'Ollamaサンプルタスク01', now(), now(), 'XXL', '', 'NONE', '',
        'DO_NOTHING', 'FIRST', 'ollamaJobHandler', '{
    "input": "遅いSQL問題分析の考え方",
    "prompt": "あなたは研究開発エンジニアで、技術的な問題を解決するのが得意です。",
    "model": "qwen3:0.6b"
}', 'SERIAL_EXECUTION', 0, 0, 'BEAN', '', 'GLUE代码初始化',
        now(), ''),
       (3, 2, 'Difyサンプルタスク', now(), now(), 'XXL', '', 'NONE', '',
        'DO_NOTHING', 'FIRST', 'difyWorkflowJobHandler', '{
    "inputs":{
        "input":"クラスの各学科のトップ3を検索"
    },
    "user": "xxl-job",
    "baseUrl": "http://localhost/v1",
    "apiKey": "app-OUVgNUOQRIMokfmuJvBJoUTN"
}', 'SERIAL_EXECUTION', 0, 0, 'BEAN', '', 'GLUE代码初始化',
        now(), '');

INSERT INTO `xxl_job_user`(`id`, `username`, `password`, `role`, `permission`) VALUES (1, 'admin', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 1, NULL);

INSERT INTO `xxl_job_lock` (`lock_name`) VALUES ('schedule_lock');
