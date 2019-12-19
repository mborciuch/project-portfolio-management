package com.misiek.controller;

import com.misiek.service.IService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

public abstract class RawController<T> implements IController<T> {

    @PostMapping
    @Override
    public ResponseEntity<T> save(@RequestBody T t) {
       T result = (T) getService().save(t);
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    @GetMapping
    @Override
    public ResponseEntity<List<T>> findAll() {
        List<T> result = getService().findAll();
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @Override
    public ResponseEntity<T> find(@PathVariable Long id) {
        T result = (T) getService().find(id);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @Override
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        getService().delete(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    public abstract IService getService();
}