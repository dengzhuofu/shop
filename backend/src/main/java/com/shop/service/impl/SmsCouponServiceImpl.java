package com.shop.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shop.entity.SmsCoupon;
import com.shop.mapper.SmsCouponMapper;
import com.shop.service.SmsCouponService;
import org.springframework.stereotype.Service;

@Service
public class SmsCouponServiceImpl extends ServiceImpl<SmsCouponMapper, SmsCoupon> implements SmsCouponService {
}
