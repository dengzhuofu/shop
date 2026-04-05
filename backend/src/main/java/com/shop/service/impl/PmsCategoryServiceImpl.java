package com.shop.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shop.entity.PmsCategory;
import com.shop.mapper.PmsCategoryMapper;
import com.shop.service.PmsCategoryService;
import org.springframework.stereotype.Service;

@Service
public class PmsCategoryServiceImpl extends ServiceImpl<PmsCategoryMapper, PmsCategory> implements PmsCategoryService {
}
