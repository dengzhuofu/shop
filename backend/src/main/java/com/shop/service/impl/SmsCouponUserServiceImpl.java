package com.shop.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shop.entity.SmsCouponUser;
import com.shop.mapper.SmsCouponUserMapper;
import com.shop.service.SmsCouponUserService;
import org.springframework.stereotype.Service;

@Service
public class SmsCouponUserServiceImpl extends ServiceImpl<SmsCouponUserMapper, SmsCouponUser> implements SmsCouponUserService {
}
