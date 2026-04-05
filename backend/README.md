# Backend README

## 技术栈

- Spring Boot 3.2.4
- JDK 21.0.10 LTS
- Maven 3.9.x
- PostgreSQL 17
- MyBatis-Plus
- Sa-Token

说明：
- 本项目代码版本固定为 Spring Boot 3.2.4。
- 查官方资料时，优先参考 3.2.4 同代文档，避免把更新版本的配置差异误用到当前项目。

## 当前默认配置

- 服务端口：`8081`
- 数据库主机：`localhost`
- 数据库端口：`5433`
- 数据库名：`shop`
- 数据库用户名：`postgres`
- 数据库密码：`postgres`

对应配置文件：`src/main/resources/application.yml`

项目支持通过环境变量覆盖数据库配置：

```powershell
DB_HOST
DB_PORT
DB_NAME
DB_USERNAME
DB_PASSWORD
```

## 本机已安装环境

如果你使用当前这台机器，后端环境已经装在 `E:`：

- JDK：`E:\tools\java\jdk-21.0.10`
- Maven：`E:\tools\maven\apache-maven-3.9.14`
- PostgreSQL 17：`E:\tools\postgresql\17.9`
- 环境加载脚本：`E:\tools\backend-env.ps1`
- PostgreSQL 启动脚本：`E:\tools\postgresql\17.9\start-pg17.ps1`
- PostgreSQL 停止脚本：`E:\tools\postgresql\17.9\stop-pg17.ps1`

加载环境：

```powershell
. E:\tools\backend-env.ps1
```

启动 PostgreSQL 17：

```powershell
powershell -ExecutionPolicy Bypass -File E:\tools\postgresql\17.9\start-pg17.ps1
```

停止 PostgreSQL 17：

```powershell
powershell -ExecutionPolicy Bypass -File E:\tools\postgresql\17.9\stop-pg17.ps1
```

## 从零启动后端

### 1. 准备数据库

如果数据库还没初始化，执行：

```powershell
$env:PGPASSWORD='postgres'
E:\tools\postgresql\17.9\bin\createdb.exe -h localhost -p 5433 -U postgres shop
E:\tools\postgresql\17.9\bin\psql.exe -h localhost -p 5433 -U postgres -d shop -f E:\project\shop\backend\src\main\resources\schema.sql
```

### 2. 启动后端

```powershell
. E:\tools\backend-env.ps1
cd E:\project\shop\backend
mvn spring-boot:run
```

启动成功后，后端地址为：

```text
http://localhost:8081
```

## 快速验证

### 1. 商品详情接口

浏览器打开：

```text
http://localhost:8081/product/1?lang=zh
```

### 2. 登录接口

默认测试账号：

- 邮箱：`admin@isinwheel.local`
- 密码：`123456`

请求示例：

```bash
curl -X POST http://localhost:8081/auth/login \
     -H "Content-Type: application/json" \
     -d '{"email":"admin@isinwheel.local","password":"123456"}'
```

### 3. 其他常用接口

获取商品列表：

```bash
curl -X GET "http://localhost:8081/product/list?pageNum=1&pageSize=10"
```

获取购物车列表：

```bash
curl -X GET http://localhost:8081/cart/list \
     -H "Authorization: <Your_Token_Value>"
```

提交订单：

```bash
curl -X POST http://localhost:8081/order/create \
     -H "Content-Type: application/json" \
     -H "Authorization: <Your_Token_Value>" \
     -d '{"cartItemIds":[1]}'
```

## 目录结构

```text
src/main/java/com/shop
├─ config       配置类
├─ controller   接口入口
├─ dto          请求参数对象
├─ entity       数据库实体
├─ mapper       MyBatis-Plus Mapper
├─ service      服务层接口
├─ service/impl 服务层实现
├─ vo           返回视图对象
└─ common       通用返回与上下文
```

## 建议阅读顺序

1. `src/main/resources/application.yml`
2. `src/main/java/com/shop/BackendApplication.java`
3. `src/main/resources/schema.sql`
4. `src/main/java/com/shop/controller/ProductController.java`
5. `src/main/java/com/shop/controller/AuthController.java`
6. `src/main/java/com/shop/controller/CartController.java`
7. `src/main/java/com/shop/controller/OrderController.java`

## 当前后端现状评估（更新于 2026-04-04）

- 已有多语言上下文：通过 `Accept-Language` 或 `lang` 参数切换 `zh/en`。
- 已有商品、评论、购物车、地址、订单接口基础能力。
- 已有订单预览、创建订单、模拟支付接口，但支付仍是业务内联逻辑，不是独立支付层。
- 当前登录仅支持 `username + password`，用户表没有邮箱字段，也没有注册接口。
- 当前没有优惠券表、优惠券接口、优惠券金额校验链路。

## 官网对齐观察（基于 2026-04-04 官网公开页面）

- 官网当前公开主站默认地区与币种表现为 `United States (USD $)`。
- 官网公开购物车抽屉包含 `Discount code` 入口，说明优惠码至少在购物车/结算前阶段可输入。
- 官网公开商品详情页存在颜色变体与附加服务项，说明商品主 SKU 与增值服务项需要分层建模，不能都混成同一种商品规格。
- 官网存在 `Login`、`Track Your Order`、`Financing`、`Exclusive Discount` 等入口，后续用户体系、订单查询、优惠能力需要与站点信息架构对齐。

这部分是我根据官网公开页面做的对齐推断，真正开发前仍需要你拍板哪些部分第一期做、哪些只保留接口层。

## 本轮开发已确认的业务规则（2026-04-04）

### 1. 多语言

- 全站全部双语：商品、分类、Header/Footer、登录注册、购物车、订单、优惠券、支付状态、错误提示都需要 `zh/en`。
- 开发默认值：
  - 默认语言 `en`
  - 缺失翻译时回退到 `en`

### 2. 商品与 URL

- 第一阶段同时覆盖：`Electric Bike / Electric Skateboard / Accessories`
- URL slug 结构尽量对齐官网

### 3. 商品规格与附加项

- SKU 维度固定为：`颜色 / 套餐 / 版本`
- 每个 SKU 有独立：
  - `price`
  - `compareAtPrice`
  - `stock`
  - `images`
- 无库存规格前端展示为禁用，不隐藏
- 增值服务项不纳入 SKU，而作为订单附加项

### 4. 用户与认证

- 使用邮箱注册登录
- 邮箱唯一
- 第一阶段暂不做：
  - 邮箱验证码
  - 激活邮件
  - 忘记密码
  - 重置密码
- 后端数据模型和接口保留未来扩展位
- 不允许游客下单

### 5. 订单规则

- 第一阶段仅支持 `Add to cart`
- 下单前必须先走 `order preview`
- 地区/币种固定为 `US / USD`
- 税费、运费先使用固定规则，由后端统一计算
- 订单状态先按简单最佳实践实现：
  - `PENDING_PAYMENT`
  - `PAYMENT_PROCESSING`
  - `PAID`
  - `FULFILLING`
  - `SHIPPED`
  - `COMPLETED`
  - `CANCELLED`

### 6. 支付层边界

- 暂不接真实支付网关
- 但必须保留完整支付链路：
  - `payment intent`
  - `payment status`
  - `mock complete`
- 订单与支付关系固定为：
  - 先创建订单
  - 再创建支付单

### 7. 优惠券

- 仅支持“满多少减多少”门槛券
- 满 `0` 可减的券视为无门槛券
- 采用“先领券，再在订单预览阶段校验”的模式
- 第一阶段默认：
  - 不限制用户
  - 不限制商品
  - 不限制类目
  - 不限制时间
  - 不限制使用次数
- 默认单订单仅允许使用一张优惠券

### 8. 复刻与交付优先级

- 首版商品文案、图片与结构先按官网公开页面完成
- 第一阶段优先：`业务链路先跑通`
- 视觉精修放在主链路打通之后

## 建议的后端接口草案

### 1. 认证与用户

- `POST /auth/register/email`
- `POST /auth/login`
- `POST /auth/logout`
- `GET /auth/me`
- `POST /auth/password/forgot`
- `POST /auth/password/reset`

### 2. 商品与类目

- `GET /product/list`
- `GET /product/{id}`
- `GET /product/slug/{slug}`
- `GET /category/tree`
- `GET /category/{slug}/products`

### 3. 购物车

- `POST /cart/add`
- `GET /cart/list`
- `PUT /cart/{id}`
- `DELETE /cart/{id}`

### 4. 优惠券

- `POST /coupon/validate`
- `GET /coupon/available`
- `POST /coupon/{couponId}/claim`
- `GET /coupon/my`

### 5. 订单

- `POST /order/preview`
- `POST /order/create`
- `GET /order/list`
- `GET /order/{id}`
- `PUT /order/{id}/cancel`

### 6. 支付占位层

- `GET /payment/methods`
- `POST /payment/intent`
- `GET /payment/intent/{id}`
- `POST /payment/mock/complete`

说明：

- 第一阶段不接真实支付网关，但订单与支付的边界要先拆开。
- `order` 负责订单业务，`payment` 负责支付意图与支付状态。
- 这样后续接入 PayPal / Stripe / Shop Pay / Apple Pay 时，不需要重做订单主链路。

## 建议的数据模型补充

当前后端至少还需要补这些模型或字段：

- `sys_user`
  - `email`
  - `email_verified`
  - `password_hash`
  - `status`
  - `last_login_time`

- `pms_product`
  - `slug`
  - `category_id`
  - `published`

- `pms_sku`
  - `attributes_json`
  - `attribute_display_json`
  - `compare_at_price`
  - `status`

- `oms_order`
  - `coupon_code`
  - `coupon_discount_amount`
  - `currency`
  - `payment_status`
  - `payment_intent_id`

- `sms_coupon`
  - 优惠券主表

- `sms_coupon_user`
  - 用户领券/用券记录

- `pay_payment_intent`
  - 支付意图占位表

## 后端测试与自测方案

后端测试基线建议如下：

## 2026-04-05 后端实现状态

### 已实现功能

- [x] 中英文双语接口
  - 商品、分类、优惠券、支付方式、订单详情都支持 `lang / Accept-Language`
  - 默认语言 `en`，缺失翻译回退 `en`

- [x] 真实商品与分类接口
  - `GET /category/tree`
  - `GET /category/{slug}`
  - `GET /category/{slug}/products`
  - `GET /product/list`
  - `GET /product/{id}`
  - `GET /product/slug/{slug}`

- [x] 商品规格能力
  - SKU 维度固定为 `color / bundle / style`
  - 每个 SKU 拥有独立价格、划线价、库存、图片、状态
  - 订单附加项与 SKU 分层建模

- [x] 邮箱注册登录
  - `POST /auth/register/email`
  - `POST /auth/login`
  - `GET /auth/me`
  - `POST /auth/logout`
  - 用户表已切到邮箱唯一模型

- [x] 购物车与创建订单
  - `POST /cart/add`
  - `GET /cart/list`
  - `PUT /cart/{id}`
  - `DELETE /cart/{id}`
  - `POST /order/preview`
  - `POST /order/create`
  - `GET /order/list`
  - `GET /order/{id}`
  - `PUT /order/{id}/cancel`

- [x] 支付占位层
  - `GET /payment/methods`
  - `POST /payment/intent`
  - `GET /payment/intent/{id}`
  - `POST /payment/mock/complete`
  - 订单与支付已拆层：先订单，再支付意图

- [x] 优惠券
  - `GET /coupon/available`
  - `POST /coupon/{couponId}/claim`
  - `GET /coupon/my`
  - `POST /coupon/validate`
  - 预览阶段参与优惠金额计算

### 数据与种子

- [x] `schema.sql` 已重建为真实双语种子数据
- [x] 覆盖品类：`Electric Scooters / Electric Bike / Electric Skateboard / Accessories`
- [x] 覆盖种子：用户、分类、商品、SKU、评论、地址、优惠券、订单、支付意图

### 已通过的后端测试

- [x] `mvn test`

当前后端测试覆盖：

- [x] 商品详情多语言返回
- [x] 邮箱注册 + 登录 + `/auth/me`
- [x] 分类树与商品 slug 详情
- [x] 购物车 -> 订单预览 -> 创建订单 -> 支付意图 -> mock 完成支付

### 当前保留项

- [ ] 真实第三方支付网关
- [ ] 忘记密码 / 重置密码
- [ ] 邮箱验证码 / 激活邮件
- [ ] 更完整的履约、发货、物流回传链路

- `Spring Boot Test`
- `MockMvc`
- `JSONPath` 断言
- 前端联调自测建议配合 `Vitest + @nuxt/test-utils/e2e` 的无浏览器集成测试一起跑

当前测试基线已经具备：

- 前端：Nuxt 冒烟测试、登录页无浏览器集成测试
- 后端：`ProductController` 的多语言 MockMvc 测试

后续每个接口至少需要覆盖：

- 正常流程
- 参数错误
- 未登录
- 越权访问
- 多语言返回
- 金额计算正确性
- 库存/优惠券/支付状态边界

推荐命令：

```bash
mvn test
```
