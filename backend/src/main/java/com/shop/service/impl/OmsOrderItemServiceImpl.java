package com.shop.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shop.entity.OmsOrderItem;
import com.shop.mapper.OmsOrderItemMapper;
import com.shop.service.OmsOrderItemService;
import org.springframework.stereotype.Service;

@Service
public class OmsOrderItemServiceImpl extends ServiceImpl<OmsOrderItemMapper, OmsOrderItem>
    implements OmsOrderItemService {
}
