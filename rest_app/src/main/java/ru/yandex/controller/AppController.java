package ru.yandex.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.dto.Error;
import ru.yandex.dto.ShopUnit;
import ru.yandex.dto.ShopUnitImportRequest;
import ru.yandex.exception.ItemNotFoundException;
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

            return new ResponseEntity<>("Вставка или обновление прошли успешно.",
                    getOkHeaders(), HttpStatus.OK);
        } catch (RequestErrorException e) {
            return new ResponseEntity<>(new Error(400, e.getMessage()),
                    getJsonHeaders(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/nodes/{id}")
    public ResponseEntity getData(@PathVariable("id") String id) {
        try {
            ShopUnit unit = shopService.getUnit(id);

            return new ResponseEntity<>(unit,
                    getJsonHeaders(), HttpStatus.OK);
        } catch (RequestErrorException e) {
            return new ResponseEntity<>(new Error(400, e.getMessage()),
                    getJsonHeaders(), HttpStatus.BAD_REQUEST);
        } catch (ItemNotFoundException e) {
            return new ResponseEntity<>(new Error(404, e.getMessage()),
                    getJsonHeaders(), HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity deleteData(@PathVariable("id") String id) {
        try {
            shopService.delete(id);

            return new ResponseEntity<>("Удаление прошло успешно.",
                    getOkHeaders(), HttpStatus.OK);
        } catch (RequestErrorException e) {
            return new ResponseEntity<>(new Error(400, e.getMessage()),
                    getJsonHeaders(), HttpStatus.BAD_REQUEST);
        } catch (ItemNotFoundException e) {
            return new ResponseEntity<>(new Error(404, e.getMessage()),
                    getJsonHeaders(), HttpStatus.NOT_FOUND);
        }
    }

    private HttpHeaders getOkHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "text/plain; charset=utf-8");
        return headers;
    }

    private HttpHeaders getJsonHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        return headers;
    }
}
