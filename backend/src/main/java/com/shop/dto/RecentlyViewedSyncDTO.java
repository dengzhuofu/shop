package com.shop.dto;

import lombok.Data;

import java.util.List;

@Data
public class RecentlyViewedSyncDTO {
  private List<Long> productIds;
}
