---
name: backend-api-conventions
description: "Use this skill whenever you are asked to write, modify, or review Spring Boot backend API controllers, services, DTOs, or VOs in this project. It ensures that you follow the established project conventions (Result wrapper, Sa-Token auth, Swagger annotations, MyBatis-Plus pagination, avoiding CORS issues) to prevent common errors."
---

# Backend API Development Conventions

When developing or modifying backend APIs in this project, you MUST adhere to the following established conventions to ensure consistency and prevent common errors.

## 1. Response Wrapper (`Result<T>`)
**Rule:** NEVER return raw entities, strings, or standard Spring `ResponseEntity`. ALWAYS wrap the return value using `com.shop.common.Result<T>`.
- For success: `return Result.success(data);` or `return Result.success(null);`
- For errors: `return Result.error(code, "Error message");`

## 2. Authentication & Sa-Token
**Rule:** This project uses Sa-Token for authentication.
- To get the current logged-in user ID: `Long userId = StpUtil.getLoginIdAsLong();`
- **Crucial:** If you create a new API that should be publicly accessible (e.g., viewing a product, registering, logging in), you MUST add its path to the `.notMatch(...)` list in `com.shop.config.SaTokenConfigure`. Failing to do so will result in `401 User not logged in` errors.

## 3. Pagination (MyBatis-Plus)
**Rule:** Use `com.baomidou.mybatisplus.extension.plugins.pagination.Page<T>` for pagination.
- Controller parameters should default to `pageNum=1` and `pageSize=10`.
- **Conversion:** Do NOT return `Page<Entity>` directly to the frontend. Always convert the database entity to a View Object (VO).
```java
// Example conversion
Page<PmsProduct> productPage = productService.page(new Page<>(pageNum, pageSize));
Page<ProductVO> voPage = new Page<>(pageNum, pageSize);
voPage.setTotal(productPage.getTotal());
List<ProductVO> voList = productPage.getRecords().stream()
    .map(p -> ProductVO.from(p))
    .collect(Collectors.toList());
voPage.setRecords(voList);
return Result.success(voPage);
```

## 4. Swagger / OpenAPI 3 Documentation
**Rule:** All controllers and methods should be documented for Swagger.
- Use `@Tag(name = "Module Name", description = "...")` on the Controller class.
- Use `@Operation(summary = "Endpoint description")` on the mapping methods.

## 5. Cross-Origin Resource Sharing (CORS)
**Rule:** NEVER use the `@CrossOrigin` annotation on individual controllers.
- The project handles CORS globally in `WebMvcConfig.java` and through frontend proxies (Nuxt Nitro/Vite proxy).
- Adding `@CrossOrigin` locally will cause header conflicts (`Access-Control-Allow-Origin` contains multiple values or `*` when `allowCredentials` is true).

## 6. Data Transfer Objects (DTO) and View Objects (VO)
**Rule:** Strict separation of concerns for data models.
- **Input:** Use DTOs (e.g., `LoginDTO`, `OrderCreateDTO`) with `@RequestBody` and `@Validated` for incoming JSON payloads.
- **Output:** Use VOs (e.g., `ProductVO`, `OrderVO`) for returning data to the frontend.
- **Database:** Entities (e.g., `PmsProduct`, `OmsOrder`) should ONLY be used for MyBatis-Plus database operations and never exposed directly in Controller return types.
