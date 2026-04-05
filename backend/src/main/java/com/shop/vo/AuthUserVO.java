package com.shop.vo;

import com.shop.entity.SysUser;
import lombok.Data;

@Data
public class AuthUserVO {
  private Long id;
  private String email;
  private String firstName;
  private String lastName;
  private String nickname;
  private String fullName;

  public static AuthUserVO from(SysUser user) {
    AuthUserVO vo = new AuthUserVO();
    vo.setId(user.getId());
    vo.setEmail(user.getEmail());
    vo.setFirstName(user.getFirstName());
    vo.setLastName(user.getLastName());
    vo.setNickname(user.getNickname());
    vo.setFullName(((user.getFirstName() == null ? "" : user.getFirstName()) + " "
        + (user.getLastName() == null ? "" : user.getLastName())).trim());
    return vo;
  }
}
