package com.shop.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shop.entity.PmsProduct;
import com.shop.mapper.PmsProductMapper;
import com.shop.service.PmsProductService;
import org.springframework.stereotype.Service;

@Service
public class PmsProductServiceImpl extends ServiceImpl<PmsProductMapper, PmsProduct> implements PmsProductService {
}
