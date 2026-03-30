# 电商后端项目运行保姆级教程

本项目基于以下技术栈构建：
- **后端主框架**: Spring Boot 3.2.4 (需 JDK 17)
- **认证鉴权**: Sa-Token (最新稳定版)
- **ORM 增强**: MyBatis-Plus (最新稳定版)
- **数据库**: PostgreSQL 17
- **JSON 处理**: Jackson
- **容器化**: Docker Engine 24.0 (可选，用于部署)
- **反向代理**: Nginx (可选，用于部署)

---

## 第一部分：检查并安装必备环境

要运行这个后端，我们需要 **JDK 17**、**Maven** 和 **PostgreSQL 17**。如果您不确定电脑上是否已经安装，请按照以下步骤检查。

### 1. 检查并安装 JDK 17
**【如何检查】**
1. 按下 `Win + R`，输入 `cmd` 并回车，打开命令行窗口。
2. 输入命令：`java -version`
3. **结果判断**：
   - 如果提示 `java version "17.x.x"` 或类似包含 `17` 的字样，说明已安装，**跳过此步**。
   - 如果提示“不是内部或外部命令”，或者版本不是 17（如 1.8 或 11），则需要安装。

**【如何安装】**
1. 点击下载：[JDK 17 Windows 安装包](https://download.oracle.com/java/17/latest/jdk-17_windows-x64_bin.exe)。
2. 双击下载的 `.exe` 文件，一直点击“下一步”完成安装。
3. **配置环境变量**：
   - 按下 `Win + S` 搜索“环境变量”，选择**编辑系统环境变量**。
   - 点击右下角的**环境变量**按钮。
   - 在“系统变量”下，点击**新建**，变量名：`JAVA_HOME`，变量值填入您的安装路径（通常是 `C:\Program Files\Java\jdk-17`）。
   - 找到名为 `Path` 的系统变量，双击，点击**新建**，输入 `%JAVA_HOME%\bin`，点击确定。

### 2. 检查并安装 Maven
**【如何检查】**
1. 在刚才的 `cmd` 窗口中，输入命令：`mvn -v`
2. **结果判断**：
   - 如果输出了 Maven 的版本信息，说明已安装，**跳过此步**。
   - 如果提示“不是内部或外部命令”，则需要安装。

**【如何安装】**
1. 点击下载：[Apache Maven 3.9.6](https://dlcdn.apache.org/maven/maven-3/3.9.6/binaries/apache-maven-3.9.6-bin.zip)（或最新稳定版）。
2. 将 zip 文件解压到一个固定目录，例如 `C:\maven\apache-maven-3.9.6`。
3. **配置环境变量**：
   - 再次打开**环境变量**设置。
   - 在“系统变量”下，点击**新建**，变量名：`MAVEN_HOME`，变量值填入解压路径：`C:\maven\apache-maven-3.9.6`。
   - 找到 `Path` 变量，双击，点击**新建**，输入 `%MAVEN_HOME%\bin`，点击确定。
4. **重新打开一个 cmd 窗口**，输入 `mvn -v` 确认安装成功。

### 3. 检查并安装 PostgreSQL 17
**【如何检查】**
1. 在 Windows 开始菜单中搜索 `pgAdmin` 或 `psql`。
2. 或者在 `cmd` 中输入 `psql -V`。
3. **结果判断**：
   - 如果能看到 `psql (PostgreSQL) 17.x`，说明已安装，**跳过此步**。
   - 如果没有，则需要安装。

**【如何安装】**
1. 点击下载：[PostgreSQL 17 for Windows](https://www.enterprisedb.com/downloads/postgres-postgresql-downloads)。
2. 运行安装程序，**重要提示**：在安装过程中会要求设置超级用户（postgres）的密码，**请务必设置一个您能记住的密码（比如 `postgres` 或 `123456`）**。
3. 端口保持默认的 `5432` 即可。一直下一步直到安装完成。

*(注：Docker Engine 24.0 和 Nginx 主要用于后续的线上部署，本地开发和运行后端代码不需要强制安装 Docker，直接在宿主机运行 Java 和 PG 数据库即可)*

---

## 第二部分：初始化数据库表和数据

我们需要在 PostgreSQL 17 中创建一个名为 `shop` 的数据库，并导入相关的表结构。

1. 在 Windows 开始菜单搜索并打开 **pgAdmin 4**（安装 PostgreSQL 17 时自带的图形化工具）。
2. 在左侧树状图中展开 `Servers`，输入您安装时设置的密码。
3. 右键点击 `Databases` -> `Create` -> `Database...`。
4. 在 Database 栏输入名字：`shop`，点击 Save。
5. 左侧展开刚才创建的 `shop` 数据库，点击顶部的 **Query Tool（查询工具，一个像闪电/数据库带播放键的图标）**。
6. 回到 Trae IDE（或您的代码编辑器），找到并打开文件：`e:\project\shop\backend\src\main\resources\schema.sql`。
7. **全选并复制** `schema.sql` 里的所有内容。
8. 粘贴到 pgAdmin 4 的 Query Tool 窗口中，点击上方绿色的**播放按钮（Execute/F5）**。
9. 底部显示 `Query returned successfully`，说明表结构和测试数据已经成功导入！

---

## 第三部分：配置项目密码并启动后端

### 1. 修改数据库密码配置
1. 在 Trae IDE 中，打开后端配置文件：`e:\project\shop\backend\src\main\resources\application.yml`
2. 找到第 11 行和 12 行，将密码修改为您在安装 PostgreSQL 时设置的真实密码：
   ```yaml
   spring:
     datasource:
       url: jdbc:postgresql://localhost:5432/shop
       username: postgres
       password: postgres # <--- 把这里改成您真实的数据库密码
   ```

### 2. 一键启动后端
现在一切就绪，我们可以直接启动 Spring Boot 项目了！

**最简单的方法（推荐）：**
1. 在 Trae 左侧的扩展（Extensions）面板中，搜索并安装 `Extension Pack for Java`（如果没装的话）。
2. 在左侧文件树找到启动类：`e:\project\shop\backend\src\main\java\com\shop\BackendApplication.java`。
3. 打开该文件，在 `public class BackendApplication` 这一行上面，会出现一个蓝色的 **Run** 或 **Debug** 按钮。
4. 点击 **Run**。
5. 观察底部的终端/控制台输出，当看到类似以下的字样时，说明后端成功运行在 8080 端口了！
   ```text
   Tomcat started on port 8080 (http) with context path ''
   Started BackendApplication in 3.567 seconds (process running for 4.123)
   ```

**备选方法（命令行启动）：**
如果您喜欢用命令行，可以在 Trae 下方打开终端，输入：
```powershell
cd e:\project\shop\backend
mvn spring-boot:run
```

---

## 第四部分：测试接口是否正常

项目启动后，我们可以通过浏览器来验证一下：

打开您的浏览器，在地址栏输入以下链接并回车：
```
http://localhost:8080/product/1?lang=zh
```

如果网页上显示了一大串 JSON 数据（包含了 `"title": "iPhone 15 Pro"`、`"price": 9999.0` 等字样），说明：
1. 您的 Java 和 Spring Boot 运行正常。
2. 您的 PostgreSQL 数据库连接正常。
3. 您的多语言（中文 `lang=zh`）和商品/SKU 接口逻辑正常！

现在，您的后端已经处于完美待命状态，您可以去启动前端项目（`npm run dev`）进行联调了！
