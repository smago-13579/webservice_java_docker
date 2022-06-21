package ru.yandex.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.dto.Error;
import ru.yandex.dto.ShopUnitImportRequest;
import ru.yandex.exception.RequestErrorException;
import ru.yandex.service.ShopService;

@RestController
public class AppController {

    @Autowired
    private ShopService shopService;

    @PostMapping("/imports")
    public ResponseEntity importData(@RequestBody ShopUnitImportRequest body) {
        try {
            shopService.createAll(body);

            return new ResponseEntity<>("Вставка или обновление прошли успешно.", getOkHeaders(), HttpStatus.OK);
        } catch (RequestErrorException e) {
            return new ResponseEntity<>(new Error(400, e.getMessage()), getErrorHeaders(), HttpStatus.BAD_REQUEST);
        }
    }

    private HttpHeaders getOkHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "text/plain; charset=utf-8");
        return headers;
    }

    private HttpHeaders getErrorHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        return headers;
    }
}
