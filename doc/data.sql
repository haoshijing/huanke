Insert into t_admin(id,userName ,password ,saltPassword,insertTime,lastUpdateTime ,status)
select
 null , 'admin','74cb1c732d55c3aa93bad590ffe1ee8b','123',  unix_timestamp()*1000, unix_timestamp()*1000,1;

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
'净化设备',
'http://huanke.bcard.vip/deviceimg/xinfeng.png',
'210,220,230,240,250,270,280,2A0,310,320,330,340,350',
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
'新风设备',
'http://huanke.bcard.vip/deviceimg/xinfeng.png',
'210,220,230,240,250,270,280,281,290,310,320,330,340,350',
'110,111,120,121,130,131,140,141,150,160'
);
