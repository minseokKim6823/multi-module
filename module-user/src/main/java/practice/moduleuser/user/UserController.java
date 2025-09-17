package practice.moduleuser.user;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
import practice.moduleuser.user.dto.UserCreateRequest;
import practice.moduleuser.user.dto.UserResponse;
import practice.moduleuser.user.dto.UserUpdateRequest;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserCrudService userCrudService;

    @PostMapping
    public UserResponse create(@RequestBody UserCreateRequest req) {
        return UserResponse.from(userCrudService.create(req));
    }

    @GetMapping("/{id}")
    public UserResponse get(@PathVariable Long id) {
        return UserResponse.from(userCrudService.get(id));
    }

    @GetMapping
    public Page<UserResponse> list(@RequestParam(defaultValue = "0") int page,
                                   @RequestParam(defaultValue = "10") int size) {
        var p = userCrudService.list(PageRequest.of(page, size));
        List<UserResponse> content = p.getContent().stream().map(UserResponse::from).toList();
        return new PageImpl<>(content, p.getPageable(), p.getTotalElements());
    }

    @PatchMapping("/{id}")
    public UserResponse update(@PathVariable Long id, @RequestBody UserUpdateRequest req) {
        return UserResponse.from(userCrudService.update(id, req));
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        userCrudService.delete(id);
    }
}
