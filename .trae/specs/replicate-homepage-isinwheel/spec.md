# 还原 isinwheel 首页 Spec

## Why
项目已经完成初期搭建，现在需要进一步还原 isinwheel 独立站的首页 UI 和功能。通过高质量的页面还原，提供优质的电商展示体验，并确保在 768、1024、1440 三个主流分辨率下的适配。

## What Changes
- 新增首页（满铺视窗）模块
- 新增探索（Explore）模块
- 新增商品（Best Sellers）模块
- 新增视频介绍模块
- 新增达人视频模块
- 引入 SCSS/SASS 作为样式预处理器，使用嵌套结构编写样式
- 根据 frontend-design 的要求，实现高质量的排版、动画、空间布局和细节

## Impact
- Affected specs: 首页浏览、响应式布局适配
- Affected code: 首页相关组件（Hero、Explore、BestSellers、VideoIntro、InfluencerVideos）、全局样式文件

## ADDED Requirements
### Requirement: 首页（满铺视窗）模块
系统 SHALL 提供一个全屏满铺的首页头部展示。

#### Scenario: Success case
- **WHEN** 用户访问首页
- **THEN** 看到全屏的背景图/轮播，带有品牌大标题、标语和 CTA 按钮（Find Your Ride），并自适应 768/1024/1440 屏幕宽度。

### Requirement: 探索模块
系统 SHALL 提供探索不同品类的入口模块。

#### Scenario: Success case
- **WHEN** 用户向下滚动
- **THEN** 看到 "Explore isinwheel" 标题，以及 Electric Scooter, Electric Bike, Electric Skateboard, Accessories 的图文卡片，悬浮时有交互动效。

### Requirement: 商品模块
系统 SHALL 提供热门商品展示。

#### Scenario: Success case
- **WHEN** 用户浏览到商品区
- **THEN** 看到 "Best Sellers" 分类切换和商品卡片（包含标签、价格、折扣、关键参数如功率/续航/速度等）。

### Requirement: 视频介绍模块
系统 SHALL 提供大尺寸的视频播放区域。

#### Scenario: Success case
- **WHEN** 用户浏览到视频区
- **THEN** 可以播放/暂停展示产品的宣传视频。

### Requirement: 达人视频模块
系统 SHALL 提供社交媒体达人的短视频展示墙。

#### Scenario: Success case
- **WHEN** 用户浏览到底部
- **THEN** 看到多列垂直的达人视频卡片（类似短视频流），包含达人信息，点击可查看达人评测。
