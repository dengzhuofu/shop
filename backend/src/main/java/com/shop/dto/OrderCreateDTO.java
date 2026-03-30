package com.shop.dto;

import lombok.Data;
import java.util.List;

@Data
public class OrderCreateDTO {
  private List<Long> cartItemIds;
}
