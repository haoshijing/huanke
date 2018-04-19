Insert into t_admin(id,userName ,password ,saltPassword,insertTime,lastUpdateTime ,status)
select
 null , 'superadmin','77d3b7ed9db7d236b9eac8262d27f6a5','123',  unix_timestamp()*1000, unix_timestamp()*1000,1;

insert into  t_device_type
(

name,
icon,
funcList,
sensorList,
createTime
)
values (
'净化器',
'http://huanke.bcard.vip/deviceimg/a.png',
'',
''
)