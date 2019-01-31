insert into person(name,age,address) values('汪云飞',32,'合肥');
insert into person(name,age,address) values('xx',31,'北京');
insert into person(name,age,address) values('yy',30,'上海');
insert into person(name,age,address) values('zz',29,'南京');
insert into person(name,age,address) values('aa',28,'武汉');
insert into person(name,age,address) values('bb',27,'合肥');

insert into sys_user (id,username, password) values (1,'wyf', 'wyf');
insert into sys_user (id,username, password) values (2,'wisely', 'wisely');

insert into sys_role(id,name) values(1,'ROLE_ADMIN');
insert into sys_role(id,name) values(2,'ROLE_USER');

insert into sys_user_roles(SYS_USER_ID,ROLES_ID) values(1,1);
insert into sys_user_roles(SYS_USER_ID,ROLES_ID) values(2,2);