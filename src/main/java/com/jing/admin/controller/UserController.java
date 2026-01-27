package com.jing.admin.controller;

import com.jing.admin.core.HttpResult;
import com.jing.admin.core.PageResult;
import com.jing.admin.model.api.UserQueryRequest;
import com.jing.admin.model.dto.UserDTO;
import com.jing.admin.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/page")
    public HttpResult<PageResult<UserDTO>> getUserPage(UserQueryRequest queryRequest) {
        return HttpResult.success(userService.getUserPage(queryRequest));
    }

    @GetMapping("/{id}")
    public HttpResult<UserDTO> getUserById(@PathVariable String id) {
        UserDTO user = userService.getUserById(id);
        if (user == null) {
            return HttpResult.error("用户不存在");
        }
        return HttpResult.success(user);
    }
}
