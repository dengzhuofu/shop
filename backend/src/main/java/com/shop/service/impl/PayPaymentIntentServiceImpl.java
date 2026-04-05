package com.shop.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shop.entity.PayPaymentIntent;
import com.shop.mapper.PayPaymentIntentMapper;
import com.shop.service.PayPaymentIntentService;
import org.springframework.stereotype.Service;

@Service
public class PayPaymentIntentServiceImpl extends ServiceImpl<PayPaymentIntentMapper, PayPaymentIntent>
    implements PayPaymentIntentService {
}
