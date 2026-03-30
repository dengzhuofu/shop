package com.shop.vo;

import lombok.Data;

import java.util.Map;

@Data
public class ReviewSummaryVO {
    private Double averageRating;
    private Long totalReviews;
    // Map with key as star rating (1-5) and value as count of reviews
    private Map<Integer, Long> ratingDistribution;
}