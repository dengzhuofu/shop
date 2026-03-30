# Backend 测试说明

## 1. 准备工作

1. 安装并启动 PostgreSQL 17 数据库。
2. 创建数据库 `shop_db`。
3. 执行 `src/main/resources/schema.sql` 脚本，初始化表结构及测试数据。

## 2. 启动服务

使用 Maven 命令或在 IDE 中运行 `BackendApplication.java` 启动 Spring Boot 服务。默认端口为 8080。

## 3. 接口测试步骤

可以使用 Postman 或 cURL 进行接口测试。

### 3.1 登录获取 Token

```bash
curl -X POST http://localhost:8080/auth/login \
     -H "Content-Type: application/json" \
     -d '{"username":"admin","password":"123456"}'
```
响应中会包含 `tokenName` (Authorization) 和 `tokenValue`，后续请求需要在 Header 中携带该 Token。

### 3.2 获取商品列表（免登录）

```bash
curl -X GET "http://localhost:8080/product/list?pageNum=1&pageSize=10"
```

### 3.3 获取商品详情及 SKU（免登录）

```bash
# 假设返回的商品 ID 为 1
curl -X GET http://localhost:8080/product/1
```

### 3.4 加入购物车（需登录）

```bash
curl -X POST http://localhost:8080/cart/add \
     -H "Content-Type: application/json" \
     -H "Authorization: <Your_Token_Value>" \
     -d '{"productId":1, "skuId":1, "quantity":1}'
```

### 3.5 获取购物车列表（需登录）

```bash
curl -X GET http://localhost:8080/cart/list \
     -H "Authorization: <Your_Token_Value>"
```

### 3.6 提交订单（需登录）

```bash
# 假设返回的购物车记录 ID 为 1
curl -X POST http://localhost:8080/order/create \
     -H "Content-Type: application/json" \
     -H "Authorization: <Your_Token_Value>" \
     -d '{"cartItemIds":[1]}'
```
