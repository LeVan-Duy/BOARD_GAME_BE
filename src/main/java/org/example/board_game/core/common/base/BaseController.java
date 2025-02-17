package org.example.board_game.core.common.base;


import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.example.board_game.core.common.PageableObject;
import org.example.board_game.utils.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FieldDefaults(level = AccessLevel.PRIVATE)
public abstract class BaseController<T, K, R> {

    final BaseService<T, K, R> service;

    public BaseController(BaseService<T, K, R> service) {
        this.service = service;
    }

    @GetMapping("/get-all")
    public ResponseEntity<Response<PageableObject<T>>> findAll(R request) {
        Response<PageableObject<T>> response = service.findAll(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/create")
    public ResponseEntity<Response<Object>> create(@RequestBody @Valid R request) {
        return ResponseEntity.ok(service.create(request));
    }

    @PostMapping("/update/{id}")
    public ResponseEntity<Response<Object>> update(@PathVariable K id, @RequestBody @Valid R request) {
        return ResponseEntity.ok(service.update(request,id));
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<Response<T>> findById(@PathVariable K id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PostMapping("/delete/{id}")
    public ResponseEntity<Response<Object>> delete(@PathVariable K id) {
        return ResponseEntity.ok(service.delete(id));
    }
}
