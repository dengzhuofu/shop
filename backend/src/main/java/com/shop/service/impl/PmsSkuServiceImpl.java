package com.shop.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shop.entity.PmsSku;
import com.shop.mapper.PmsSkuMapper;
import com.shop.service.PmsSkuService;
import org.springframework.stereotype.Service;

@Service
public class PmsSkuServiceImpl extends ServiceImpl<PmsSkuMapper, PmsSku> implements PmsSkuService {
}
