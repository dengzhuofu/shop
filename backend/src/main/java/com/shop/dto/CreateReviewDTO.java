package com.shop.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class CreateReviewDTO {

  @NotNull
  @Min(1)
  @Max(5)
  private Integer rating;

  @Size(max = 255)
  private String title;

  @NotBlank
  @Size(max = 2000)
  private String content;

  @Size(max = 4)
  private List<@Size(max = 600) String> images = new ArrayList<>();
}
