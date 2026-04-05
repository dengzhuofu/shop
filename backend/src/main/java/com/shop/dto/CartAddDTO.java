package com.shop.dto;

import lombok.Data;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;

import java.util.List;

@Data
public class CartAddDTO {
  @NotNull(message = "Product ID cannot be null")
  private Long productId;

  @NotNull(message = "SKU ID cannot be null")
  private Long skuId;

  @NotNull(message = "Quantity cannot be null")
  @Min(value = 1, message = "Quantity must be at least 1")
  private Integer quantity;

  private List<String> addonCodes;
}
