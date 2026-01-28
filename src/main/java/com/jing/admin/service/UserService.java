package com.jing.admin.service;

import com.jing.admin.core.PageResult;
import com.jing.admin.model.api.UserRequest;
import com.jing.admin.model.api.UserQueryRequest;
import com.jing.admin.model.dto.UserDTO;

public interface UserService {
    PageResult<UserDTO> getUserPage(UserQueryRequest queryRequest);

    UserDTO getUserById(String id);

    UserDTO createUser(UserRequest request);
}
