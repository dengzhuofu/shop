package com.shop.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterEmailDTO {
  @Email(message = "Email is invalid")
  @NotBlank(message = "Email cannot be empty")
  private String email;

  @NotBlank(message = "Password cannot be empty")
  @Size(min = 6, max = 32, message = "Password length must be between 6 and 32")
  private String password;

  @NotBlank(message = "First name cannot be empty")
  private String firstName;

  @NotBlank(message = "Last name cannot be empty")
  private String lastName;
}
