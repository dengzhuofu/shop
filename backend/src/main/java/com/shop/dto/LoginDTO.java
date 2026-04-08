package com.shop.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Email;

@Data
public class LoginDTO {
  @Email(message = "Email is invalid")
  @NotBlank(message = "Email cannot be empty")
  private String email;

  @NotBlank(message = "Password cannot be empty")
  private String password;

  public void setEmail(String email) {
    this.email = email == null ? null : email.trim().toLowerCase();
  }
}
