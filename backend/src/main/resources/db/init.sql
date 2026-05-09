-- 单位管理模板系统数据库初始化脚本
-- 设置字符编码
SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;

-- 创建数据库（如果不存在）
CREATE DATABASE IF NOT EXISTS unit_management 
    CHARACTER SET utf8mb4 
    COLLATE utf8mb4_unicode_ci;

USE unit_management;

-- 设置连接字符集
SET NAMES utf8mb4;

-- 单位表
DROP TABLE IF EXISTS attendance_record;
DROP TABLE IF EXISTS sys_user_role;
DROP TABLE IF EXISTS sys_user;
DROP TABLE IF EXISTS sys_role;
DROP TABLE IF EXISTS position;
DROP TABLE IF EXISTS department;
DROP TABLE IF EXISTS unit;

CREATE TABLE unit (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL COMMENT '单位名称',
    code VARCHAR(50) UNIQUE COMMENT '单位编码',
    parent_id BIGINT COMMENT '上级单位ID',
    address VARCHAR(255) COMMENT '地址',
    phone VARCHAR(20) COMMENT '联系电话',
    email VARCHAR(100) COMMENT '电子邮箱',
    leader VARCHAR(50) COMMENT '负责人',
    sort_order INT DEFAULT 0 COMMENT '排序号',
    status INT DEFAULT 1 COMMENT '状态(0:禁用,1:启用)',
    remark TEXT COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_unit_code (code),
    INDEX idx_unit_parent_id (parent_id),
    INDEX idx_unit_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='单位表';

-- 部门表
CREATE TABLE department (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL COMMENT '部门名称',
    code VARCHAR(50) COMMENT '部门编码',
    unit_id BIGINT NOT NULL COMMENT '所属单位ID',
    parent_id BIGINT COMMENT '上级部门ID',
    leader VARCHAR(50) COMMENT '负责人',
    phone VARCHAR(20) COMMENT '联系电话',
    email VARCHAR(100) COMMENT '电子邮箱',
    sort_order INT DEFAULT 0 COMMENT '排序号',
    status INT DEFAULT 1 COMMENT '状态(0:禁用,1:启用)',
    remark TEXT COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_dept_code (code),
    INDEX idx_dept_unit_id (unit_id),
    INDEX idx_dept_parent_id (parent_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='部门表';

-- 岗位表
CREATE TABLE position (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL COMMENT '岗位名称',
    code VARCHAR(50) COMMENT '岗位编码',
    dept_id BIGINT NOT NULL COMMENT '所属部门ID',
    description VARCHAR(500) COMMENT '岗位描述',
    sort_order INT DEFAULT 0 COMMENT '排序号',
    status INT DEFAULT 1 COMMENT '状态(0:禁用,1:启用)',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_pos_code (code),
    INDEX idx_pos_dept_id (dept_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='岗位表';

-- 角色表
CREATE TABLE sys_role (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL COMMENT '角色名称',
    code VARCHAR(50) UNIQUE COMMENT '角色编码',
    description VARCHAR(500) COMMENT '角色描述',
    sort_order INT DEFAULT 0 COMMENT '排序号',
    status INT DEFAULT 1 COMMENT '状态(0:禁用,1:启用)',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_role_code (code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色表';

-- 用户表
CREATE TABLE sys_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(100) NOT NULL COMMENT '密码',
    nickname VARCHAR(50) COMMENT '昵称',
    unit_id BIGINT COMMENT '所属单位ID',
    dept_id BIGINT COMMENT '所属部门ID',
    position_id BIGINT COMMENT '所属岗位ID',
    phone VARCHAR(20) COMMENT '手机号',
    email VARCHAR(100) COMMENT '邮箱',
    avatar VARCHAR(255) COMMENT '头像',
    status INT DEFAULT 1 COMMENT '状态(0:禁用,1:启用)',
    remark VARCHAR(500) COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_user_username (username),
    INDEX idx_user_unit_id (unit_id),
    INDEX idx_user_dept_id (dept_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 用户角色关联表
CREATE TABLE sys_user_role (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    INDEX idx_ur_user_id (user_id),
    INDEX idx_ur_role_id (role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户角色关联表';

-- 插入示例单位数据
INSERT INTO unit (name, code, parent_id, address, phone, email, leader, sort_order, status) VALUES
('Head Office', 'HQ001', NULL, 'Beijing Chaoyang', '010-12345678', 'hq@company.com', 'Zhang', 1, 1),
('North Branch', 'BJ001', 1, 'Beijing Haidian', '010-23456789', 'bj@company.com', 'Li', 2, 1),
('East Branch', 'SH001', 1, 'Shanghai Pudong', '021-34567890', 'sh@company.com', 'Wang', 3, 1),
('South Branch', 'GZ001', 1, 'Guangzhou Tianhe', '020-45678901', 'gz@company.com', 'Liu', 4, 1);

-- 插入示例部门数据
INSERT INTO department (name, code, unit_id, parent_id, leader, phone, sort_order, status) VALUES
('General Office', 'DEPT001', 1, NULL, 'Zhang', '010-11111111', 1, 1),
('HR Department', 'DEPT002', 1, NULL, 'Li', '010-22222222', 2, 1),
('Finance Dept', 'DEPT003', 1, NULL, 'Wang', '010-33333333', 3, 1),
('Tech Dept', 'DEPT004', 1, NULL, 'Liu', '010-44444444', 4, 1),
('Dev Team 1', 'DEPT005', 1, 4, 'Chen', '010-55555555', 1, 1),
('Dev Team 2', 'DEPT006', 1, 4, 'Zhao', '010-66666666', 2, 1);

-- 插入示例岗位数据
INSERT INTO position (name, code, dept_id, description, sort_order, status) VALUES
('CEO', 'POS001', 1, 'Company CEO', 1, 1),
('HR Specialist', 'POS002', 2, 'HR recruitment', 1, 1),
('Accountant', 'POS003', 3, 'Finance work', 1, 1),
('Tech Director', 'POS004', 4, 'Tech team lead', 1, 1),
('Senior Engineer', 'POS005', 5, 'Core development', 1, 1),
('Junior Engineer', 'POS006', 5, 'Daily development', 2, 1);

-- 插入示例角色数据
INSERT INTO sys_role (name, code, description, sort_order, status) VALUES
('Super Admin', 'admin', 'Full permissions', 1, 1),
('Normal User', 'user', 'Basic permissions', 2, 1),
('Department Manager', 'manager', 'Department permissions', 3, 1);

-- 插入示例用户数据
INSERT INTO sys_user (username, password, nickname, unit_id, dept_id, position_id, phone, email, status) VALUES
('admin', '123456', 'Admin', 1, 1, 1, '13800138000', 'admin@company.com', 1),
('zhangsan', '123456', 'Zhang San', 1, 2, 2, '13800138001', 'zhangsan@company.com', 1),
('lisi', '123456', 'Li Si', 1, 3, 3, '13800138002', 'lisi@company.com', 1),
('wangwu', '123456', 'Wang Wu', 1, 4, 4, '13800138003', 'wangwu@company.com', 1);

-- 插入用户角色关联
INSERT INTO sys_user_role (user_id, role_id) VALUES
(1, 1),
(2, 2),
(3, 2),
(4, 3);

-- 考勤记录表
CREATE TABLE attendance_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    check_in_time DATETIME COMMENT '上班打卡时间',
    check_out_time DATETIME COMMENT '下班打卡时间',
    record_date DATE NOT NULL COMMENT '考勤日期',
    status INT DEFAULT 1 COMMENT '状态(1:正常,2:迟到,3:早退,4:缺勤)',
    remark VARCHAR(500) COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_att_user_id (user_id),
    INDEX idx_att_date (record_date),
    UNIQUE KEY uk_att_user_date (user_id, record_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='考勤记录表';
