package ru.yandex.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.dto.ShopUnitImportRequest;
import ru.yandex.service.ShopService;

@RestController
public class AppController {

    @Autowired
    private ShopService shopService;

    @PostMapping("/imports")
    public ResponseEntity importData(@RequestBody ShopUnitImportRequest body) {

        return new ResponseEntity(HttpStatus.OK);
    }
}
