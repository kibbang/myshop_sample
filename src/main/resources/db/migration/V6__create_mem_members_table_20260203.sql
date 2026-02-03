-- members
drop table if exists mem_members;

create table mem_members (
                             id bigint not null auto_increment,

                             login_id varchar(50) not null,
                             password varchar(100) not null,

                             name varchar(100) not null,
                             phone varchar(100) not null,

                             zipcode varchar(10) not null,
                             address1 varchar(255) not null,
                             address2 varchar(255) null,

                             role varchar(20) not null,                -- USER / ADMIN
                             is_active tinyint(1) not null default 1,  -- 1: active, 0: inactive

                             created_at datetime not null,
                             updated_at datetime not null,
                             deleted_at datetime null,

                             primary key (id),
                             unique key uk_mem_members_login_id (login_id),
                             key idx_mem_members_is_active (is_active),
                             key idx_mem_members_role (role)
) engine=InnoDB default charset=utf8mb4;
