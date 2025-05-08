package com.ghoul.AmanaFund.Dao;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Builder
@Getter
@AllArgsConstructor
public class ModifyPassRequest {
    private int userId;
    private String newPassword;
    private String oldPassword;
}
