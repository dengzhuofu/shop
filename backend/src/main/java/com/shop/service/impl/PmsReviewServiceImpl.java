package com.shop.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shop.entity.PmsReview;
import com.shop.mapper.PmsReviewMapper;
import com.shop.service.PmsReviewService;
import org.springframework.stereotype.Service;

@Service
public class PmsReviewServiceImpl extends ServiceImpl<PmsReviewMapper, PmsReview> implements PmsReviewService {
}