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
