package com.shop.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shop.entity.OmsCartItem;
import com.shop.mapper.OmsCartItemMapper;
import com.shop.service.OmsCartItemService;
import org.springframework.stereotype.Service;

@Service
public class OmsCartItemServiceImpl extends ServiceImpl<OmsCartItemMapper, OmsCartItem> implements OmsCartItemService {
}
