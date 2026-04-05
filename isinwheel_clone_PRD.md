# isinwheel 官方商城前端复刻项目 PRD (产品需求文档)

## 1. 项目概述

**项目名称**: isinwheel 官方商城前端复刻
**项目目标**: 高保真复刻 [isinwheel 官方网站](https://www.isinwheel.com/) 的前端页面结构、视觉设计和交互体验。目前仅实现前端纯展示及基础交互，不包含真实后端数据交互。所有数据采用本地 Mock 数据。
**适用终端**: 响应式设计（PC 端）
**多语言**：英文站 / 中文站

## 2. 技术栈及规范要求

- **框架**: **Nuxt 4** (Vue 3 组合式 API + SSR)
- **样式方案**: **Sass (SCSS)**
  - **核心规范**: 强制要求使用 SCSS 的嵌套结构，避免冗余类名。例如：
    ```scss
    .header {
      background: #fff;
      .nav-list {
        display: flex;
        .nav-item {
          margin-right: 20px;
          &:hover {
            color: #f00;
          }
        }
      }
    }
    ```
- **状态管理**: Pinia (用于管理购物车、全局用户信息等状态)
- **路由**: Nuxt 约定式路由
- **注释规范**: 所有组件、核心函数、复杂逻辑**必须包含符合规范的中文注释**，以提升代码可读性。
- **组件化**: 高度抽取复用组件（如 ProductCard、Button、Banner等）。

## 3. 页面与功能需求

### 3.1 全局公共组件 (Layout)

- **Header (顶部导航栏)**
  - **顶部公告栏 (Top Bar)**: 滚动播放促销信息或政策（如 "Fast & Reliable Shipping"）。
  - **主导航区**:
    - 左侧/中间：Logo。
    - 导航菜单：E-Scooters, E-Bikes, E-Skateboards, Accessories, Support 等。
    - 右侧操作区：国家/地区选择 (US)、全局搜索图标、用户登录入口、购物车入口（带数字角标）。
  - **交互**: 鼠标悬浮菜单出现下拉二级菜单（Mega Menu），移动端转换为汉堡菜单 (Hamburger Menu)。
- **Footer (底部信息栏)**
  - **服务保障区**: 四大服务图标与文本 (Fast & Reliable Shipping, 1-Year Warranty Service, US-Based Support, Secure Payments)。
  - **订阅区 (Newsletter)**: 邮箱输入框及订阅按钮 ("Join Our Community")。
  - **链接区**: 快速链接、支持中心、关于我们、社交媒体图标。
  - **版权声明**: 底部 Copyright 信息及支付方式图标展示。

### 3.2 核心页面规划

#### 1. 首页 (Home Page - `/`)

- **Hero Banner (首屏大图轮播)**
  - 包含全屏/宽屏产品展示图、标语（如 "City Smart. Style Forward. Ride Free Shop Now"）及 CTA (Call to Action) 按钮。
- **分类导览 (Collections/Categories)**
  - 图片+标题的卡片形式，引导用户进入不同产品线（滑板车、电动自行车等）。
- **明星产品展示区 (Featured Products)**
  - 使用 `ProductCard` 组件展示主推产品（如 U8, V10）。
  - 卡片需包含：产品图（Hover切换图片）、型号、价格、简短核心参数（马达功率、续航、最高时速等）。
- **用户评价区 (Customer Reviews)**
  - 轮播展示来自真实用户的评价内容（"What The People Say About Isinwheel"），包括星级、用户名称、购买型号、评价内容。
- **品牌故事/特色区**
  - 图文并茂展示品牌理念、骑行生活方式。

#### 2. 产品列表页 (Collections Page - `/collections/all`)

- **页面标题与描述**: 当前分类的介绍。
- **筛选与排序 (Filter & Sort)**:
  - 支持按价格、可用性筛选。
  - 支持按推荐、价格高低、销量排序。
- **产品列表网格 (Product Grid)**:
  - 响应式网格布局（PC端一行3-4个，移动端1-2个）。
  - 每个卡片清晰展示：产品主图、标题、核心四项参数（如 `1000W Motor Capacity`, `37 Miles Max Range`, `28 MPH Top Speed`, `48V 15Ah Battery`）、价格、"Add to Cart" 按钮。

#### 3. 产品详情页 (Product Detail Page - `/products/[slug]`)

- **首屏购买区 (Buy Section)**:
  - **左侧 (图片画廊)**: 主图大图展示、缩略图列表、支持放大查看。带促销角标（如 "Ride Deals"）。
  - **右侧 (购买信息)**:
    - 产品标题（如 "isinwheel U8 Electric Bike for Adults"）。
    - 价格展示（Sale price, Regular price，带有划线价）。
    - 变体选择器 (Variants)：颜色选择（色块）、规格选择（如 "Range/Tires: 75 Miles&20Inch"）。
    - 库存提示（如 "Hurry, only 4 items left in stock!"）。
    - 增值服务选项（勾选框，如 "One Year Warranty", "Cable Lock for Ebike"）。
    - "Add to Cart" 和 "Buy it Now" 按钮。
- **核心卖点区 (Quick Know)**:
  - 列表形式展示产品核心卖点（如 ✅1000W Max Motor, ✅Professional Shimano 7 Speed 等）。
- **规格参数表 (Specification)**:
  - 清晰的表格/网格列出详细参数（Battery, Range, Max Speed, Motor, Wheel Size, Net Weight 等）。
- **包装清单 (What's in the Box)**:
  - 图文展示包装内包含的物品。
- **常见问题 (FAQs)**:
  - 手风琴组件 (Accordion) 展示关于该产品的常见问题解答。
- **相关推荐 (You may also like)**:
  - 底部推荐其他相关产品列表。

## 4. 关键组件与 Mock 数据结构设计要求

### 4.1 ProductCard 组件

- **输入 Props**: `product` (对象)
- **Mock 数据示例**:
  ```json
  {
    "id": "u8-ebike",
    "title": "isinwheel U8 Electric Bike for Adults",
    "price": 609.99,
    "compareAtPrice": 799.99,
    "image": "/images/u8-main.jpg",
    "hoverImage": "/images/u8-hover.jpg",
    "specs": [
      { "label": "Motor", "value": "1000W" },
      { "label": "Max Range", "value": "75 Miles" },
      { "label": "Top Speed", "value": "28 MPH" }
    ],
    "tags": ["Spring Sale"]
  }
  ```

## 5. 交互与动画需求

- **Hover 效果**: 所有的按钮、链接在鼠标悬停时需有平滑的颜色过渡或位移。产品卡片悬停时图片要有轻微放大或切换展示另一张图片。
- **Sticky Header**: 页面向下滚动时，Header 需要吸顶并可适当减小高度或添加阴影。
- **加载过渡 (Loading Transition)**: 页面切换时需有 Nuxt 默认的渐隐渐现过渡动画。
- **响应式适配**: 必须确保在 375px (手机) 到 1920px (大屏显示器) 之间有良好的展示效果，断点设计需合理。

## 6. 开发排期建议 (供开发参考)

1.  **Phase 1: 基础设施搭建 (1天)**
    - 初始化 Nuxt 4 项目。
    - 配置 SCSS 预处理器、全局样式变量 (主题色、字体、间距)。
    - 配置 Pinia 和基础 Layout。
2.  **Phase 2: 全局组件开发 (2天)**
    - 开发 Header、Footer。
    - 开发通用基础组件 (Button, Input, ProductCard, Accordion)。
3.  **Phase 3: 核心页面还原 (3天)**
    - 还原首页 (Home)。
    - 还原产品列表页 (Collections)。
    - 还原产品详情页 (Product Detail)。
4.  **Phase 4: 响应式适配与动效优化 (2天)**
    - 移动端样式微调。
    - 补充完善交互动效、Hover 状态。
    - 检查 SCSS 嵌套规范及中文注释是否达标。

---

**附注**: 页面涉及的图片素材若无法直接获取，请在开发时使用占位图 (Placeholder) 或从原网站审查元素截取高清素材，确保复刻的视觉高保真度。

## 7. 当前执行状态与任务清单（更新于 2026-03-29）

### 7.1 Nuxt 4 升级任务（已完成）

- [x] 依赖升级：`nuxt` 升级至 `^4.4.2`，`@pinia/nuxt` 升级至 `^0.11.3`，`pinia` 升级至 `^3.0.4`。
- [x] 锁文件同步：执行 `pnpm install` 更新 `pnpm-lock.yaml`，执行 `npm install --package-lock-only` 同步 `package-lock.json`。
- [x] 构建验证：执行 `pnpm build`，Nuxt 4 构建通过（Node Server 产物已生成）。
- [ ] 兼容性跟踪：后续每次引入新模块前，先做 Nuxt 4 兼容性检查（模块版本、SSR 行为、样式编译性能）。

### 7.2 当前复刻完成度（基于现有代码）

- [x] 已有页面：`/`（首页）、`/products/[id]`（产品详情原型）。
- [x] 已有全局组件：Header、Footer、ProductCard、QuickViewModal、CartSidebar、MegaMenu。
- [ ] 未完成页面：`/collections/all`（产品列表页）未实现。
- [ ] 路由规范未对齐：PRD 目标为 `/products/[slug]`，当前仍为 `/products/[id]`。
- [ ] 数据组织未对齐：核心数据仍分散在页面内，缺少统一 Mock 数据层与 Pinia 购物车状态流。

### 7.3 继续复刻开发任务（P0-P2）

#### P0（必须先完成，保证主链路可用）

- [ ] 新增 `pages/collections/all.vue`：完成筛选、排序、响应式网格、产品卡片复用。
- [ ] 将 `pages/products/[id].vue` 重构为 `pages/products/[slug].vue`：按 slug 读取商品数据，支持无匹配时 404。
- [ ] 建立统一 Mock 数据层：抽离首页、列表、详情数据到 `server/api` 或 `data/` + composables，避免页面硬编码。
- [ ] 引入 Pinia 购物车最小闭环：`addToCart`、数量修改、购物车角标同步、侧边栏联动。
- [ ] Header 移动端补齐：实现 Hamburger Menu、抽屉导航、二级菜单展开逻辑。

#### P1（高优先级，提升复刻保真度）

- [ ] 首页素材替换：将当前占位图/示例视频替换为与目标站一致的高保真资源。
- [ ] 产品详情页增强：补齐变体切换、库存提示、服务选项勾选、FAQ 与规格表真实映射。
- [ ] Footer 与服务保障区对齐：补齐底部服务图标、支付图标视觉与排版细节。
- [ ] 全站交互统一：按钮 hover、卡片 hover、sticky header、页面切换过渡一致化。

#### P2（优化项，提升质量与可维护性）

- [ ] 性能优化：首屏图片懒加载、关键图区预加载、减少大图布局偏移（CLS）。
- [ ] 组件注释与文档：按规范补全中文注释，补齐关键组件 props 与事件说明。
- [ ] 测试与验收：补充关键页面的基础 E2E/快照测试，建立回归检查清单。
- [ ] SEO 与基础元信息：首页、列表页、详情页的 title/description/OG 数据完善。

### 7.4 下一阶段排期建议（Nuxt 4 基线后）

1. 第 1-2 天：完成 P0（列表页、slug 路由、统一数据层、购物车闭环）。
2. 第 3-4 天：完成 P1（首页与详情页高保真复刻、全局交互统一）。
3. 第 5 天：完成 P2 核心项（性能、测试、SEO）并进行验收修正。

## 8. 支付方式调研与接入规划（更新于 2026-04-01）

### 8.1 官网当前支付方式（以官网公开信息为准）

#### A. Payment Methods 页面明确写明

- PayPal
- Credit / Debit Card（文案中包含 Visa、MasterCard、Amex，并提到 Shop Pay、Apple Pay、G Pay）
- Klarna（Buy Now, Pay Later）
- Affirm（Buy Now, Pay Later）

#### B. 官网 Footer 支付图标显示

- American Express
- Apple Pay
- Diners Club
- Discover
- Google Pay
- Mastercard
- PayPal
- Shop Pay
- Venmo
- Visa

#### C. 页面文案中的限制信息（需产品侧确认）

- PayPal Credit 文案标注“仅限美国客户（For USA customers only at the moment）”
- Klarna 文案标注“当前仅支持价格低于 $1500 的商品”

> 注：实际可用支付方式最终以 **Checkout 结算页可选网关** 与店铺后台配置为准；前端展示与后端可用网关必须保持一致。

### 8.2 复刻站支付接入目标（后期全部接入）

- 一期（主链路）：
  - 信用卡/借记卡（Visa / Mastercard / American Express / Discover）
  - PayPal
  - Shop Pay
  - Apple Pay
  - Google Pay
- 二期（分期与地区化）：
  - Klarna
  - Affirm
  - Venmo（如与 PayPal/地区配置联动）
  - Diners Club（按支付服务商能力开启）
    44

### 8.3 PRD 级实施要求（新增）

- [ ] 支付方式展示组件：在 Footer 与 Checkout 页面统一读取支付配置，不硬编码固定图标。
- [ ] 支付配置中心：新增 `payment-config`（建议放在 `server/api` 或 `data/`），按国家/币种/端类型返回可用方式。
- [ ] 结算页支付区块：支持卡支付、钱包支付、分期支付分组展示，并显示可用条件（如金额门槛、地区限制）。
- [ ] 风险与降级：若某网关不可用，前端自动隐藏该入口并给出可理解提示，不影响主结算流程。
- [ ] 埋点与监控：支付方式曝光、选择、跳转、成功/失败全链路埋点（用于后续转化分析）。

### 8.4 验收标准（支付）

- [ ] 同一国家/币种下，Footer 支付图标与 Checkout 可选支付方式一致。
- [ ] 移动端与 PC 端支付入口一致可达，Apple Pay / Google Pay 按设备条件正确显隐。
- [ ] 分期方式（Klarna / Affirm）在不满足条件时不展示或明确禁用原因。
- [ ] 支付失败回流路径完整（返回购物车/结算页，保留用户已填信息）。

## 9. 前后端联调开发准备（更新于 2026-04-04）

### 9.1 本轮目标范围

本轮准备阶段只做以下两件事：

- 明确前后端联调方案与缺失业务信息
- 搭建后续可复用的测试框架与自测基线

本轮暂不直接开发以下业务：

- 邮箱注册与登录完整闭环
- 优惠券业务逻辑
- 支付网关真实接入
- 结算税费、运费、支付回调等最终实现

### 9.2 当前前端现状

- 已有中英文切换按钮，但当前语言切换主要停留在 Cookie/Header 传递层，前端静态文案没有系统化国际化管理。
- 首页、商品详情、购物车、地址、订单页已开始对接后端部分真实接口。
- 商品详情页已经具备基于 SKU 的规格选择雏形，但路由仍是 `/products/[id]`，尚未与官网 slug 体系对齐。
- 登录页已接后端登录接口，但当前仍是“用户名/邮箱”混合输入，没有真实邮箱注册页。
- Checkout 页中的支付、折扣、积分区块仍是静态 UI，不是后端驱动。

### 9.3 已确认的业务规则（2026-04-04 已拍板）

1. 多语言范围
   - 全站全部双语：商品、Header/Footer、登录注册、购物车、结算、订单、优惠券、支付状态、错误提示都需要 `zh/en`。

2. 复刻范围
   - 第一阶段同时覆盖 `Electric Bike / Electric Skateboard / Accessories`。
   - URL 结构需要尽量对齐官网 slug。

3. 商品与规格规则
   - SKU 规格维度固定为：`颜色 / 套餐 / 版本`。
   - 每个 SKU 拥有独立：价格、划线价、库存、图片。
   - 无库存规格前端展示为禁用，不隐藏。
   - 增值服务项不算 SKU，算订单附加项。

4. 用户体系
   - 邮箱唯一，使用邮箱注册登录。
   - 第一阶段暂不做邮箱验证码、激活邮件、忘记密码、重置密码，但后续会接入，所以接口与数据结构要留扩展位。
   - 不允许游客下单。

5. 订单规则
   - 只支持 `Add to cart`，第一阶段不做 `Buy now`。
   - 创建订单前必须先走 `preview`。
   - 订单状态流转由开发按简单最佳实践定义。
   - 第一阶段地区/币种固定为 `US / USD`，税费与运费规则先固定。

6. 支付层边界
   - 虽然暂不接真实支付，但必须保留完整支付占位链路：
     - `payment intent`
     - `payment status`
     - `mock complete`
   - 固定采用：先创建订单，再创建支付单。

7. 优惠券规则
   - 仅支持“满多少减多少”的门槛优惠券。
   - 满 `0` 即减的券视为无门槛优惠券。
   - 用户先领券，再在订单预览阶段校验。
   - 第一阶段不限制用户、商品、类目、时间、次数。

8. 素材与还原优先级
   - 商品文案、图片与结构先按官网公开页面做首版。
   - 第一阶段优先级：`业务链路先跑通`，其后再做视觉精修。

### 9.4 仍保留的实现默认值（若你未额外指定）

以下内容我会按默认值直接实现；如果你想改，我再跟进：

1. 默认语言
   - 默认站点语言先使用 `en`，前端支持切到 `zh`。
   - 缺少目标语言翻译时，回退到 `en`。

2. 订单状态
   - 先按简单最佳实践实现：
     - `PENDING_PAYMENT`
     - `PAYMENT_PROCESSING`
     - `PAID`
     - `FULFILLING`
     - `SHIPPED`
     - `COMPLETED`
     - `CANCELLED`

3. 固定费用规则
   - 先使用固定 `shipping fee` 与固定 `tax rate`，都在后端集中计算并在 preview/create 阶段保持一致。

4. 优惠券使用方式
   - 默认单订单仅允许使用一张优惠券。

### 9.5 官网公开实现观察（基于 2026-04-04 官网）

- 官网当前公开主导航为：`Electric Scooter / Electric Bike / Electric Skateboard / Accessories / Support`，并带 `Login / Cart / US / USD` 入口。
- 首页当前公开结构包含：首屏轮播、分类入口、Best Sellers、用户/媒体评价、品牌说明等模块。
- 商品详情页当前公开结构包含：图片画廊、价格、颜色变体、加价购/赠品型增值服务、Quick Know、Specification、What's in the Box、Rider Reviews。
- 购物车抽屉当前公开展示了 `Order note` 与 `Discount code` 入口，且税费与运费在 checkout 阶段计算。

这部分已经和你确认完毕，因此接下来直接进入开发实现。

### 9.6 建议的前端实现方案

## 10. 2026-04-05 开发完成状态

### 10.1 已实现功能清单

- [x] 中英文双语切换
  - 已落到 Header、Footer、首页、分类页、商品详情页、登录注册页、购物车、结算页、账户页、订单页
  - 默认语言 `en`，切换后写入 `lang` Cookie 并刷新页面，后端接口同步按 `lang` 返回真实双语数据

- [x] 前端商品使用后端真实数据
  - 首页：读取 `/api/category/tree`、`/api/product/list`
  - 分类页：读取 `/api/category/{slug}`、`/api/category/{slug}/products`
  - 商品详情页：读取 `/api/product/slug/{slug}` 或 `/api/product/{id}`
  - 商品 URL 已切到官网风格 slug 访问

- [x] 商品规格功能
  - 固定支持 `color / bundle / style`
  - 由后端 `skuList + skuAttributeOptions` 驱动
  - 无库存规格在前端显示为禁用
  - 规格切换会联动价格、库存状态、图片

- [x] 创建订单功能
  - 前端只支持 `Add to cart`
  - Checkout 按 `cart -> preview -> create` 顺序调用真实接口
  - 支持已保存地址与手动地址表单

- [x] 保留支付层（暂不接真实支付）
  - 前端已接 `POST /payment/intent`
  - 前端已接 `POST /payment/mock/complete`
  - 当前为支付占位链路，不接真实第三方网关

- [x] 用户邮箱注册登录
  - 登录页改为邮箱登录
  - 新增邮箱注册页
  - 登录态通过 Cookie + `/auth/me` 恢复
  - 未登录用户加购后进入结算会被引导登录

- [x] 优惠券功能
  - 账户页与结算页可查看已领优惠券
  - 可领取后端可用优惠券
  - 订单预览阶段可选择优惠券并参与金额计算

### 10.2 本轮前端实现范围

- [x] 首页 `/`
- [x] 分类页 `/collections/[slug]`
- [x] 商品详情页 `/products/[id]`（参数兼容 slug / id）
- [x] 登录页 `/login`
- [x] 注册页 `/register`
- [x] 结算页 `/checkout`
- [x] 账户资料页 `/account/profile`
- [x] 订单页 `/account/orders`
- [x] 全局 Header / Footer / Cart Sidebar

### 10.3 当前保留项

- [ ] 忘记密码 / 重置密码
- [ ] 邮箱验证码 / 激活邮件
- [ ] 真实支付网关接入
- [ ] 视觉精修到官网级像素还原
- [ ] 更完整的 SEO / metadata / 埋点

### 10.4 前端自测结果

已通过以下测试：

- [x] `npm run test:unit`
- [x] `npm run test:integration`
- [x] `npm test`

当前前端测试覆盖：

- [x] Nuxt 运行时挂载基线
- [x] SKU 规格选择工具函数
- [x] 登录页 SSR 输出
- [x] 注册页 SSR 输出

- 使用后端真实商品数据驱动首页、详情页、购物车、结算页，不再保留页面内硬编码商品数据。
- 保留语言 Cookie，并统一通过 `useHttp` 发送 `Accept-Language` 与 `lang` 参数。
- 商品详情页按后端返回的 `skuList + skuAttributeOptions` 计算规格矩阵，前端不再写死 `style / bundle / color` 结构。
- 登录、注册、优惠券、支付方式、订单预览都通过 Pinia 或 composables 管理，避免页面内直接拼请求。
- Checkout 页中的支付方式、优惠券、订单摘要都由后端返回结果驱动，前端只负责状态展示与交互控制。

### 9.7 建议的前端页面与状态拆分

- `stores/auth.ts`
  - 登录状态
  - 用户信息
  - token 持久化

- `stores/cart.ts`
  - 购物车条目
  - 购物车数量角标
  - 加购、修改数量、删除

- `stores/checkout.ts`
  - 下单来源
  - 地址快照
  - 优惠券
  - 支付方式
  - 订单预览结果

- `composables/useLocale.ts`
  - 当前语言
  - 切换语言
  - 文案读取

- `composables/usePayment.ts`
  - 读取支付方式
  - 创建支付占位请求
  - 轮询/查询支付状态

### 9.8 前端测试与自测方案

准备采用两层测试：

- 单元/组件测试：`Vitest + @nuxt/test-utils`
- 页面集成测试：`Vitest + @nuxt/test-utils/e2e`（`browser: false`，不依赖 Chromium 下载）

当前基线已经具备：

- Nuxt 运行时冒烟测试
- 登录页 SSR 集成测试
- 后续可继续补商品列表、商品详情、购物车、下单链路的页面集成测试

后续每完成一个功能，至少需要补这些自测：

- 多语言切换是否同步影响商品页、购物车、订单页
- 商品列表和详情是否全部来自真实接口
- SKU 切换后图片、价格、库存、加购参数是否同步变化
- 注册登录后 Header、订单页、地址页是否状态一致
- 优惠券应用后 preview 与 create order 金额是否一致
- 未接入真实支付前，支付占位层是否能正确返回“待支付/模拟成功/模拟失败”

推荐测试命令：

```bash
npm test
npm run test:unit
npm run test:integration
```
