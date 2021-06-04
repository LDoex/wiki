# 电子书表
drop table if exists `ebook`;
create table `ebook` (
                         `id` bigint not null comment 'id',
                         `name` varchar(50) comment '名称',
                         `category1_id` bigint comment '分类1',
                         `category2_id` bigint comment '分类2',
                         `description` varchar(200) comment '描述',
                         `cover` varchar(200) comment '封面',
                         `doc_count` int comment '文档数',
                         `view_count` int comment '阅读数',
                         `vote_count` int comment '点赞数',
                         primary key (`id`)
) engine=innodb default charset=utf8mb4 comment='电子书';

insert into `ebook` (id, name, description, cover) values (1, 'Spring Boot 入门教程', '零基础入门 Java 开发，企业级应用开发最佳首选框架', '/image/cover2.png');
insert into `ebook` (id, name, description, cover) values (2, 'Vue 入门教程', '零基础入门 Vue 开发，企业级应用开发最佳首选框架', '/image/cover1.png');
insert into `ebook` (id, name, description) values (3, 'Python 入门教程', '零基础入门 Python 开发，企业级应用开发最佳首选框架');
insert into `ebook` (id, name, description) values (4, 'Mysql 入门教程', '零基础入门 Mysql 开发，企业级应用开发最佳首选框架');
insert into `ebook` (id, name, description) values (5, 'Oracle 入门教程', '零基础入门 Oracle 开发，企业级应用开发最佳首选框架');


drop table if exists `test`;
create table `test`(
    `id` bigint not null comment 'id',
    `name` varchar(50) comment '名称',
    `password` varchar(50) comment '密码',
    primary key (`id`)
) engine = innodb default charset =utf8mb4 comment ='测试';
select * from test;

insert into `test` (id, name, password) values (1, '测试', 'password');


drop table if exists `demo`;
create table `demo`(
                       `id` bigint not null comment 'id',
                       `name` varchar(50) comment '名称',
                        primary key (`id`)
) engine = innodb default charset =utf8mb4 comment ='测试';

insert into `demo` (id, name) values (1, '测试');

UPDATE wikidev.ebook item SET item.category1_id = 1, item.category2_id = 2, item.doc_count = 1, item.view_count = 2, item.vote_count = 111 WHERE item.id = 1