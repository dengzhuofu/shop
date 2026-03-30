package com.shop.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shop.common.Result;
import com.shop.entity.PmsReview;
import com.shop.service.PmsReviewService;
import com.shop.vo.ReviewSummaryVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/review")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class ReviewController {

    private final PmsReviewService reviewService;

    @GetMapping("/product/{productId}")
    public Result<Page<PmsReview>> getProductReviews(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        
        Page<PmsReview> page = new Page<>(pageNum, pageSize);
        QueryWrapper<PmsReview> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("product_id", productId).orderByDesc("create_time");
        
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
}