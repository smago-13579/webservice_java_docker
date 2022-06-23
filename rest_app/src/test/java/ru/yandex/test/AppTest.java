package ru.yandex.test;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import ru.yandex.app.App;
import ru.yandex.dto.*;
import ru.yandex.dto.Error;
import ru.yandex.dto.ShopUnit;
import ru.yandex.models.Unit;
import ru.yandex.repository.ShopUnitRepository;
import ru.yandex.service.ShopService;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = App.class, webEnvironment = WebEnvironment.RANDOM_PORT)
public class AppTest {

    @LocalServerPort
    private int port;

    @Autowired
    TestRestTemplate restTemplate;

    @Autowired
    ShopService shopService;

    @Autowired
    ShopUnitRepository repository;

    static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("uuuu-MM-dd'T'HH:mm:ss.SSS'Z'")
            .withZone(ZoneId.of("UTC"));

    static final java.lang.String uidOne = "2e62c221-a62b-426c-820c-6f81e24fdbcc";

    static final LocalDateTime lOne = LocalDateTime.now().withNano(3);
    static final LocalDateTime lTwo = LocalDateTime.now().minusDays(10).withNano(3);
    static final LocalDateTime lThree = LocalDateTime.now().minusDays(30).withNano(3);
    static final LocalDateTime lFour = LocalDateTime.now().minusDays(40).withNano(3);
    static final java.lang.String URL = "http://localhost:";

    @AfterEach
    void deleteAll() {
        repository.deleteAll();
    }

    @Test
    void saveSimpleEntityToDB() {
        ShopUnitImport unitImport = ShopUnitImport.builder()
                .id(uidOne)
                .type(ShopUnitType.OFFER)
                .name("TV")
                .price(900L)
                .build();

        shopService.create(unitImport, lOne);
        Unit unit = repository.findById(uidOne).get();
        assertNotNull(unit);
    }

    @Test
    void updateEntity() {
        ShopUnitImport unitImport = ShopUnitImport.builder()
                .id(uidOne)
                .type(ShopUnitType.OFFER)
                .name("newTV")
                .price(333L)
                .build();

        shopService.create(unitImport, lOne);
        Unit unit = repository.findById(uidOne).get();
        assertNotNull(unit);
        assertNotNull(unit.getId());
        assertEquals(unit.getName(), "newTV");
        assertEquals(unit.getDate().format(formatter), lOne.format(formatter));
        assertEquals(unit.getPrice(), 333L);
        assertNull(unit.getParentId());
    }

    @Test
    void createManyUnits() {
        ArrayList<ShopUnitImport> list = new ArrayList<>();

        for (int i = 0; i < 10000; i++) {
            list.add(ShopUnitImport.builder()
                    .type(ShopUnitType.OFFER)
                    .id(UUID.randomUUID().toString())
                    .name("mobile")
                    .price(1500L)
                    .build()
            );
        }
        ShopUnitImportRequest importRequest = new ShopUnitImportRequest(list, lOne.format(formatter));
        shopService.createAll(importRequest);
    }

    @Test
    void simpleImportTest() throws URISyntaxException {
        java.lang.String categoryUid = UUID.randomUUID().toString();
        java.lang.String offerUidOne = UUID.randomUUID().toString();
        java.lang.String offerUidTwo = UUID.randomUUID().toString();
        java.lang.String offerUidThree = UUID.randomUUID().toString();

        ShopUnitImport categoryOne = new ShopUnitImport(ShopUnitType.CATEGORY, categoryUid,
                "Mobile_Phones", null, null);
        ShopUnitImport offerOne = new ShopUnitImport(ShopUnitType.OFFER, offerUidOne,
                "Samsung", categoryUid, 1000L);
        ShopUnitImport offerTwo = new ShopUnitImport(ShopUnitType.OFFER, offerUidTwo,
                "IPhone", categoryUid, 1200L);
        ShopUnitImport offerThree = new ShopUnitImport(ShopUnitType.OFFER, offerUidThree,
                "Xiaomi", categoryUid, 800L);
        ArrayList<ShopUnitImport> list = new ArrayList<>(Arrays.asList(categoryOne, offerOne, offerTwo, offerThree));

        RequestEntity<ShopUnitImportRequest> request = new RequestEntity<>(
                new ShopUnitImportRequest(list, lTwo.format(formatter)), HttpMethod.POST, new URI(URL));
        ResponseEntity<java.lang.String> response = restTemplate.postForEntity(URL + port + "/imports",
                request, java.lang.String.class);
        assertEquals(response.getStatusCode(), HttpStatus.OK);
    }

    @Test
    void itemNotFoundTest() throws URISyntaxException {
        java.lang.String categoryUid = UUID.randomUUID().toString();

        RequestEntity<Object> request = new RequestEntity<>(HttpMethod.DELETE, new URI(URL));
        ResponseEntity<Error> response = restTemplate.exchange(URL + port + "/delete/" + categoryUid,
                HttpMethod.DELETE, request, Error.class);
        assertEquals(response.getStatusCode(), HttpStatus.NOT_FOUND);
    }

    @Test
    void deleteCategoryItem() throws URISyntaxException {
        java.lang.String categoryUid = UUID.randomUUID().toString();
        java.lang.String offerUidOne = UUID.randomUUID().toString();
        java.lang.String offerUidTwo = UUID.randomUUID().toString();
        java.lang.String offerUidThree = UUID.randomUUID().toString();

        ShopUnitImport categoryOne = new ShopUnitImport(ShopUnitType.CATEGORY, categoryUid,
                "Mobile_Phones", null, null);
        ShopUnitImport offerOne = new ShopUnitImport(ShopUnitType.OFFER, offerUidOne,
                "Samsung", categoryUid, 1000L);
        ShopUnitImport offerTwo = new ShopUnitImport(ShopUnitType.OFFER, offerUidTwo,
                "IPhone", categoryUid, 1200L);
        ShopUnitImport offerThree = new ShopUnitImport(ShopUnitType.OFFER, offerUidThree,
                "Xiaomi", categoryUid, 800L);
        ArrayList<ShopUnitImport> list = new ArrayList<>(Arrays.asList(categoryOne, offerOne, offerTwo, offerThree));

        RequestEntity<ShopUnitImportRequest> requestOne = new RequestEntity<>(
                new ShopUnitImportRequest(list, lTwo.format(formatter)), HttpMethod.POST, new URI(URL));
        restTemplate.postForEntity(URL + port + "/imports",
                requestOne, java.lang.String.class);

        RequestEntity<Object> requestTwo = new RequestEntity<>(HttpMethod.DELETE, new URI(URL));
        ResponseEntity<java.lang.String> response = restTemplate.exchange(URL + port + "/delete/" + categoryUid,
                HttpMethod.DELETE, requestTwo, java.lang.String.class);
        assertEquals(response.getStatusCode(), HttpStatus.OK);
    }

    @Test
    void findById() {
        ArrayList<ShopUnitImport> list = new ArrayList<>();
        list.add(new ShopUnitImport(ShopUnitType.CATEGORY, "3fa85f00",
                "Electronic_Arts", null, null));
        list.add(new ShopUnitImport(ShopUnitType.CATEGORY, "3fa85f10",
                "Mobile_Phones", "3fa85f00", null));
        list.add(new ShopUnitImport(ShopUnitType.CATEGORY, "3fa85f20",
                "Mobile_Phones", "3fa85f10", null));
        list.add(new ShopUnitImport(ShopUnitType.CATEGORY, "3fa85f30",
                "Mobile_Phones", "3fa85f10", null));

        for (int i = 0; i < 100; i++) {
            list.add(ShopUnitImport.builder()
                    .type(ShopUnitType.OFFER)
                    .id(UUID.randomUUID().toString())
                    .name("mobile")
                    .price(1500L)
                    .parentId("3fa85f00")
                    .build()
            );
        }

        for (int i = 0; i < 50; i++) {
            list.add(ShopUnitImport.builder()
                    .type(ShopUnitType.OFFER)
                    .id(UUID.randomUUID().toString())
                    .name("mobile")
                    .price(100L)
                    .parentId("3fa85f30")
                    .build()
            );
        }
        ShopUnitImportRequest importRequest = new ShopUnitImportRequest(list, lOne.format(formatter));
        shopService.createAll(importRequest);
        ShopUnit unit = shopService.getUnit("3fa85f00");

        assertNotNull(unit);
    }
}
