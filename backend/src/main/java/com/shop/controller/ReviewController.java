package com.shop.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shop.common.Result;
import com.shop.dto.CreateReviewDTO;
import com.shop.entity.OmsOrder;
import com.shop.entity.OmsOrderItem;
import com.shop.entity.PmsProduct;
import com.shop.entity.PmsReview;
import com.shop.entity.SysUser;
import com.shop.service.OmsOrderItemService;
import com.shop.service.OmsOrderService;
import com.shop.service.PmsProductService;
import com.shop.service.PmsReviewService;
import com.shop.service.SysUserService;
import com.shop.vo.ReviewSummaryVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/review")
@RequiredArgsConstructor
@CrossOrigin(originPatterns = "*", maxAge = 3600)
public class ReviewController {

    private final PmsReviewService reviewService;
    private final PmsProductService productService;
    private final SysUserService sysUserService;
    private final OmsOrderService orderService;
    private final OmsOrderItemService orderItemService;

    @GetMapping("/product/{productId}")
    public Result<Page<PmsReview>> getProductReviews(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        
        Page<PmsReview> page = new Page<>(pageNum, pageSize);
        QueryWrapper<PmsReview> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("product_id", productId).orderByDesc("create_time").orderByDesc("id");
        
        Page<PmsReview> resultPage = reviewService.page(page, queryWrapper);
        return Result.success(resultPage);
    }

    @GetMapping("/product/{productId}/summary")
    public Result<ReviewSummaryVO> getReviewSummary(@PathVariable Long productId) {
        QueryWrapper<PmsReview> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("product_id", productId);
        List<PmsReview> reviews = reviewService.list(queryWrapper);

        ReviewSummaryVO summary = new ReviewSummaryVO();
        long totalReviews = reviews.size();
        summary.setTotalReviews(totalReviews);

        if (totalReviews == 0) {
            summary.setAverageRating(0.0);
            summary.setRatingDistribution(new HashMap<>());
            return Result.success(summary);
        }

        double sum = reviews.stream().mapToInt(PmsReview::getRating).sum();
        double average = sum / totalReviews;
        // Keep 2 decimal places
        BigDecimal bd = new BigDecimal(average).setScale(2, RoundingMode.HALF_UP);
        summary.setAverageRating(bd.doubleValue());

        Map<Integer, Long> distribution = reviews.stream()
                .collect(Collectors.groupingBy(PmsReview::getRating, Collectors.counting()));
        
        // Initialize all ratings 1-5 with 0 if not present
        for (int i = 1; i <= 5; i++) {
            distribution.putIfAbsent(i, 0L);
        }
        
        summary.setRatingDistribution(distribution);

        return Result.success(summary);
    }

    @PostMapping("/product/{productId}")
    public Result<PmsReview> createProductReview(
            @PathVariable Long productId,
            @RequestBody @Valid CreateReviewDTO dto) {
        if (!StpUtil.isLogin()) {
            return Result.error(401, "Please sign in to write a review");
        }

        PmsProduct product = productService.getById(productId);
        if (product == null || !Boolean.TRUE.equals(product.getPublished())) {
            return Result.error(404, "Product not found");
        }

        Long userId = StpUtil.getLoginIdAsLong();
        SysUser user = sysUserService.getById(userId);
        if (user == null) {
            return Result.error(404, "User not found");
        }

        PmsReview review = new PmsReview();
        review.setProductId(productId);
        review.setUserId(userId);
        review.setUserName(resolveReviewerName(user));
        review.setRating(dto.getRating());
        review.setTitle(normalizeOptionalText(dto.getTitle()));
        review.setContent(dto.getContent().trim());
        review.setImages(normalizeImages(dto.getImages()));
        review.setVerifiedPurchase(hasVerifiedPurchase(userId, productId));
        review.setCreateTime(LocalDateTime.now());

        reviewService.save(review);
        return Result.success(review);
    }

    private String resolveReviewerName(SysUser user) {
        if (user.getNickname() != null && !user.getNickname().isBlank()) {
            return user.getNickname().trim();
        }

        String fullName = String.format("%s %s",
                user.getFirstName() == null ? "" : user.getFirstName().trim(),
                user.getLastName() == null ? "" : user.getLastName().trim()).trim();
        if (!fullName.isBlank()) {
            return fullName;
        }

        if (user.getEmail() == null || user.getEmail().isBlank()) {
            return "Verified Rider";
        }
        return user.getEmail().split("@")[0];
    }

    private String normalizeOptionalText(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private List<String> normalizeImages(List<String> images) {
        if (images == null || images.isEmpty()) {
            return new ArrayList<>();
        }

        return images.stream()
                .filter(url -> url != null && !url.isBlank())
                .map(String::trim)
                .distinct()
                .limit(4)
                .collect(Collectors.toList());
    }

    private boolean hasVerifiedPurchase(Long userId, Long productId) {
        List<OmsOrderItem> orderItems = orderItemService.list(new QueryWrapper<OmsOrderItem>()
                .eq("product_id", productId)
                .orderByDesc("id"));
        if (orderItems.isEmpty()) {
            return false;
        }

        List<Long> orderIds = orderItems.stream()
                .map(OmsOrderItem::getOrderId)
                .distinct()
                .collect(Collectors.toList());
        if (orderIds.isEmpty()) {
            return false;
        }

        return orderService.count(new QueryWrapper<OmsOrder>()
                .eq("user_id", userId)
                .in("id", orderIds)
                .and(wrapper -> wrapper
                        .eq("payment_status", "PAID")
                        .or()
                        .eq("status", "PAID")
                        .or()
                        .eq("status", "COMPLETED"))) > 0;
    }
}
