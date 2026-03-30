# 电商核心后端模块及登录页开发 Spec

## Why
项目目前已经实现了前端商品模块的部分页面，但缺乏真实的后端数据支撑和完整的交易链路。需要基于既定的技术栈实现商品浏览到下单的全链路后端接口，以及为了支持鉴权而需要的登录页面。

## What Changes
- 初始化 Spring Boot 3.2.4 后端项目。
- 引入 MyBatis-Plus (适配 Spring Boot 3), PostgreSQL 17, Sa-Token 等核心依赖。
- 设计并创建用户、商品、商品规格(SKU)、购物车、订单相关的数据表。
- 实现后端接口：商品列表、商品详情（含规格选择）、加入购物车、购物车列表、立即购买（下单）、用户登录。
- 在前端项目中设计并实现登录页面，保持与现有黑绿色调 (`#000000`, `#4CAF50`) 及 `Montserrat` 字体风格一致。

## Impact
- Affected specs: 商品展示、用户鉴权、购物车管理、订单生成。
- Affected code: 后端整个工程目录（新建），前端页面 `pages/login.vue` 及相关的 API 封装。

## ADDED Requirements
### Requirement: 后端核心业务接口
系统 SHALL 提供商品及购物车的相关 RESTful 接口。

#### Scenario: 用户浏览与加购
- **WHEN** 用户访问商品列表或详情页
- **THEN** 系统返回商品基础信息及 SKU 规格信息。
- **WHEN** 用户选择规格并加入购物车
- **THEN** 系统鉴权通过后，将商品加入该用户的购物车。

### Requirement: 登录接口及页面
系统 SHALL 提供用户鉴权能力及前端登录页面。

#### Scenario: 用户登录
- **WHEN** 用户访问 `/login`
- **THEN** 看到符合现有 UI 风格（黑绿主题，Montserrat字体）的登录表单。
- **WHEN** 用户提交账号密码
- **THEN** 后端通过 Sa-Token 进行登录签发，并返回 Token 给前端保存。

### Requirement: 立即购买（下单）接口
系统 SHALL 提供从购物车或直接下单的能力。

#### Scenario: 用户下单
- **WHEN** 用户在购物车或商品详情点击购买并提交
- **THEN** 系统生成订单，扣减库存，并清空对应的购物车记录。