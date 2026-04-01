# SKU主流最佳方案全链路改造计划

## Summary
- 目标：将当前 SKU 选择流程升级为主流电商可售矩阵方案，覆盖 **PDP详情页 + QuickView + 购物车 + 结算 + 订单**，并统一中英文输出与库存并发安全。
- 已确认决策：
  - 纳入 QuickView 一起改造；
  - 下单库存升级为数据库原子扣减；
  - SKU 规格从固定三维升级为通用多维模型。
- 成功标准：
  - 前端任意入口选择规格时，均只允许可售组合，禁选/售罄状态正确；
  - 后端严校验 `skuId` 与 `productId` 归属关系，防参数篡改；
  - 高并发下不超卖（原子扣减失败即返回库存不足）；
  - 中英文下规格展示一致，购物车/订单快照可回放。

## Current State Analysis

### 1) 前端现状
- 商品详情页已有本地 `selectedSku` 计算，但是“匹配失败回退首个 SKU”，存在误选风险。  
  文件：[pages/products/[id].vue](file:///e:/project/shop/pages/products/[id].vue)
- QuickView 组件仍是静态交互，未接真实 SKU 数据与可售联动。  
  文件：[components/QuickViewModal.vue](file:///e:/project/shop/components/QuickViewModal.vue)
- checkout 规格展示虽做了容错，但仍未基于统一“可展示快照协议”定义类型。  
  文件：[pages/checkout.vue](file:///e:/project/shop/pages/checkout.vue)

### 2) 后端现状
- SKU 规格在 `pms_sku.specs(JSONB)`，当前存在多种数据形态（扁平 / zh-en 嵌套）。  
  文件：[backend/src/main/resources/schema.sql](file:///e:/project/shop/backend/src/main/resources/schema.sql)
- `SkuVO/ProductVO` 已有归一化基础，但选项聚合与快照协议仍偏“兼容式”，不是完整矩阵模型。  
  文件：[SkuVO.java](file:///e:/project/shop/backend/src/main/java/com/shop/vo/SkuVO.java)、[ProductVO.java](file:///e:/project/shop/backend/src/main/java/com/shop/vo/ProductVO.java)
- 下单库存当前是先查后改循环更新，缺少数据库层原子扣减。  
  文件：[OmsOrderServiceImpl.java](file:///e:/project/shop/backend/src/main/java/com/shop/service/impl/OmsOrderServiceImpl.java)
- 加购与直购链路对 `productId + skuId` 归属校验不足。  
  文件：[CartController.java](file:///e:/project/shop/backend/src/main/java/com/shop/controller/CartController.java)、[OmsOrderServiceImpl.java](file:///e:/project/shop/backend/src/main/java/com/shop/service/impl/OmsOrderServiceImpl.java)

### 3) 多语言现状
- 已有 `LanguageInterceptor + LanguageContext`，但请求入口与历史快照在中英文一致性上仍需统一。  
  文件：[LanguageInterceptor.java](file:///e:/project/shop/backend/src/main/java/com/shop/config/LanguageInterceptor.java)、[composables/useHttp.ts](file:///e:/project/shop/composables/useHttp.ts)

## Proposed Changes

### A. 规格模型升级为“通用多维 + 可售矩阵”
- 目标文件：
  - [SkuVO.java](file:///e:/project/shop/backend/src/main/java/com/shop/vo/SkuVO.java)
  - [ProductVO.java](file:///e:/project/shop/backend/src/main/java/com/shop/vo/ProductVO.java)
  - 新增 VO（建议）：`SkuDimensionVO`、`SkuSaleMatrixVO`
- 改造内容：
  - 后端详情返回统一结构：
    - `dimensions`: 动态规格维度列表（key + label + values）
    - `saleMatrix`: 可售组合与状态（available/out_of_stock）
    - `defaultSkuId`: 默认可售 SKU
  - 保留 `attributes/attributeDisplay` 作为兼容字段，但前端主逻辑切到矩阵驱动。

### B. PDP 与 QuickView 共用同一 SKU 选择引擎
- 目标文件：
  - [pages/products/[id].vue](file:///e:/project/shop/pages/products/[id].vue)
  - [components/QuickViewModal.vue](file:///e:/project/shop/components/QuickViewModal.vue)
  - 新增 composable（建议）：`composables/useSkuSelector.ts`
- 改造内容：
  - 抽离通用选择器：输入 `dimensions + saleMatrix + selectedMap`，输出：
    - 当前命中 `skuId`
    - 每个选项的状态（enabled/disabled/out_of_stock）
    - 当前价格/库存
  - PDP/QuickView 共用逻辑，避免双实现偏差。

### C. 服务端安全校验与反篡改
- 目标文件：
  - [CartController.java](file:///e:/project/shop/backend/src/main/java/com/shop/controller/CartController.java)
  - [OmsOrderServiceImpl.java](file:///e:/project/shop/backend/src/main/java/com/shop/service/impl/OmsOrderServiceImpl.java)
  - DTO/VO：`CartAddDTO`、`OrderCreateDTO`（按现有类扩展）
- 改造内容：
  - 加购时校验：
    - `skuId` 存在；
    - `sku.productId == dto.productId`；
    - `quantity > 0` 且不超过库存策略上限；
  - 下单预览/创建对 cart/direct 两链路都做同样归属校验。

### D. 库存原子扣减（防超卖）
- 目标文件：
  - `backend/src/main/java/com/shop/mapper/PmsSkuMapper.java`（新增原子扣减 SQL）
  - [OmsOrderServiceImpl.java](file:///e:/project/shop/backend/src/main/java/com/shop/service/impl/OmsOrderServiceImpl.java)
- 改造内容：
  - 使用条件更新：`UPDATE pms_sku SET stock = stock - ? WHERE id = ? AND stock >= ?`
  - 返回影响行数，若为 0 则抛出“库存不足”，回滚事务。
  - 取消订单回补库存保留（可继续使用加库存更新）。

### E. 快照协议统一（购物车/订单）
- 目标文件：
  - [CartController.java](file:///e:/project/shop/backend/src/main/java/com/shop/controller/CartController.java)
  - [OmsOrderServiceImpl.java](file:///e:/project/shop/backend/src/main/java/com/shop/service/impl/OmsOrderServiceImpl.java)
  - [OrderItemVO.java](file:///e:/project/shop/backend/src/main/java/com/shop/vo/OrderItemVO.java)
- 改造内容：
  - 快照结构统一为：
    - `attributes`（机器值）
    - `attributeDisplay`（当前语言展示）
    - `lang`（快照语言）
  - `/cart/list`、`/order/preview`、`/order/{id}` 返回同形态，前端不再猜测结构。

### F. 数据迁移脚本（一次性）
- 目标文件：
  - [backend/src/main/resources/sku_i18n_migration.sql](file:///e:/project/shop/backend/src/main/resources/sku_i18n_migration.sql)
  - [backend/src/main/resources/mock_data_insert.sql](file:///e:/project/shop/backend/src/main/resources/mock_data_insert.sql)
- 改造内容：
  - 将历史 `specs` 标准化为可解析的多维结构；
  - 回填购物车与订单项历史快照；
  - 输出异常数据（缺失关键维度）供人工补录。

### G. 语言链路统一（中英）
- 目标文件：
  - [composables/useHttp.ts](file:///e:/project/shop/composables/useHttp.ts)
  - [pages/index.vue](file:///e:/project/shop/pages/index.vue)
  - [pages/products/[id].vue](file:///e:/project/shop/pages/products/[id].vue)
  - [pages/checkout.vue](file:///e:/project/shop/pages/checkout.vue)
- 改造内容：
  - 请求统一传递当前语言（header/query）；
  - 后端返回业务多语言内容，前端只渲染，不做业务翻译。

## Assumptions & Decisions
- 仅中英文（zh/en），不扩展第三语言。
- 采用后端主导业务多语言输出。
- 新模型主路径为“通用维度 + 可售矩阵”，固定三维仅作兼容过渡。
- 发布策略：先后端协议 + 前端双读兼容，再切主读矩阵字段。

## Verification Steps

### 1) 接口校验
- `GET /product/{id}`：
  - `dimensions/saleMatrix/defaultSkuId` 完整；
  - 中英文切换时 `attributeDisplay` 正确；
  - 兼容字段仍可返回（过渡期）。
- `POST /cart/add`、`POST /order/preview`、`POST /order/create`：
  - 非法 `sku-product` 组合被拒绝。

### 2) 前端行为校验
- PDP 与 QuickView：
  - 相同商品下选择行为一致；
  - 不可售组合不可点/置灰；
  - 选中 SKU 后价格、库存实时联动。
- checkout/订单页：
  - 规格展示来源统一，不出现空值或 `[object Object]`。

### 3) 并发与库存校验
- 压测同一 SKU 并发下单：
  - 不超卖；
  - 库存不足时稳定返回错误并回滚。

### 4) 构建与回归
- 后端：`mvn test` 通过。
- 前端：`npm run build` 通过。
- 全链路：浏览 → 选规格 → 加购/直购 → 结算 → 下单 → 支付 → 订单回显。
