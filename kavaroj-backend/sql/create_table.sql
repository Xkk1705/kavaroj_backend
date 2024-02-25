#数据库初始化


-- 创建库
create
    database if not exists karvaroj;

-- 切换库
use karvaroj;

-- 用户表
create table if not exists user
(
    id
                 bigint
        auto_increment
        comment
            'id'
        primary
            key,
    userAccount
                 varchar(256)                           not null comment '账号',
    userPassword varchar(512)                           not null comment '密码',
    unionId      varchar(256)                           null comment '微信开放平台id',
    mpOpenId     varchar(256)                           null comment '公众号openId',
    userName     varchar(256)                           null comment '用户昵称',
    userAvatar   varchar(1024)                          null comment '用户头像',
    userProfile  varchar(512)                           null comment '用户简介',
    userRole     varchar(256) default 'user'            not null comment '用户角色：user/admin/ban',
    createTime   datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime   datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete     tinyint      default 0                 not null comment '是否删除',
    index idx_unionId
        (
         unionId
            )
) comment '用户' collate = utf8mb4_unicode_ci;

-- 帖子表
create table if not exists post
(
    id
               bigint
        auto_increment
        comment
            'id'
        primary
            key,
    title
               varchar(512)                       null comment '标题',
    content    text                               null comment '内容',
    tags       varchar(1024)                      null comment '标签列表（json 数组）',
    thumbNum   int      default 0                 not null comment '点赞数',
    favourNum  int      default 0                 not null comment '收藏数',
    userId     bigint                             not null comment '创建用户 id',
    createTime datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete   tinyint  default 0                 not null comment '是否删除',
    index idx_userId
        (
         userId
            )
) comment '帖子' collate = utf8mb4_unicode_ci;

-- 帖子点赞表（硬删除）
create table if not exists post_thumb
(
    id         bigint auto_increment comment 'id' primary key,
    postId     bigint                             not null comment '帖子 id',
    userId     bigint                             not null comment '创建用户 id',
    createTime datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    index idx_postId (postId),
    index idx_userId (userId)
) comment '帖子点赞';

-- 帖子收藏表（硬删除）
create table if not exists post_favour
(
    id
        bigint
        auto_increment
        comment
            'id'
        primary
            key,
    postId
        bigint
        not
            null
        comment
            '帖子 id',
    userId
        bigint
        not
            null
        comment
            '创建用户 id',
    createTime
        datetime
        default
            CURRENT_TIMESTAMP
        not
            null
        comment
            '创建时间',
    updateTime
        datetime
        default
            CURRENT_TIMESTAMP
        not
            null
        on
            update
            CURRENT_TIMESTAMP
        comment
            '更新时间',
    index
        idx_postId
        (
         postId
            ),
    index idx_userId
        (
         userId
            )
) comment '帖子收藏';

-- 问题表
create table if not exists question
(
    id          bigint auto_increment primary key comment 'id',
    title       varchar(512)                       null comment '标题',
    content     text                               null comment '内容',
    answer      text                               null comment '答案',
    tags        varchar(1024)                      null comment '题目标签（json 数组）',
    submitNum   int      default 0 comment '提交数',
    judgeConfig varchar(1024)                      null comment '判题配置（json 对象）',
    judgeCase   varchar(1024)                      null comment '判题用例（json 数组）',
    thumbNum    int      default 0                 not null comment '点赞数',
    favourNum   int      default 0                 not null comment '收藏数',
    userId      bigint                             not null comment '创建用户 id',
    createTime  datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime  datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete    tinyint  default 0                 not null comment '是否删除',
    index idx_userId (userId)
) comment '问题' collate = utf8mb4_unicode_ci;

-- 问题提交表
create table if not exists question_submit
(
    id         bigint auto_increment comment 'id' primary key,
    questionId bigint                             not null comment '问题 id',
    userId     bigint                             not null comment '答题用户 id',
    code       text                               not null comment '提交答案',
    language   varchar(128)                       not null comment '编程语言',
    status     int      default 0 comment '提交状态 0-待提交 1_带判题 2_成功 3_失败 4_系统异常',
    judgeInfo varchar(1024)                      null comment '判题日志（json 对象）',
    createTime datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete   tinyint  default 0                 not null comment '是否删除',
    index idx_postId (questionId),
    index idx_userId (userId)
) comment '题目提交表';
