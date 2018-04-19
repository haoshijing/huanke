Insert into t_admin(id,userName ,password ,saltPassword,insertTime,lastUpdateTime ,status)
select
 null , 'superadmin','77d3b7ed9db7d236b9eac8262d27f6a5','123',  unix_timestamp()*1000, unix_timestamp()*1000,1;

insert into  t_device_type
(
id,
name,
icon,
funcList,
sensorList
)
values (
3,
'净化器',
'http://huanke.bcard.vip/deviceimg/a.png',
'210,220,230,240,250,270,280,290,310,320,330,340,350',
'110,120,130,140,150,160'
);



insert into  t_device_type
(
id,
name,
icon,
funcList,
sensorList
)
values (
4,
'清风机',
'http://huanke.bcard.vip/deviceimg/b.png',
'210,220,230,240,250,270,280,290,310,320,330,340,350',
'110,120,130,140,150,160'
);
