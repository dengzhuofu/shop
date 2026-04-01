package com.shop.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shop.entity.UmsUserAddress;
import com.shop.mapper.UmsUserAddressMapper;
import com.shop.service.UmsUserAddressService;
import org.springframework.stereotype.Service;

@Service
public class UmsUserAddressServiceImpl extends ServiceImpl<UmsUserAddressMapper, UmsUserAddress> implements UmsUserAddressService {
}
