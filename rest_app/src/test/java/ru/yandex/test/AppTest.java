package ru.yandex.test;

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
import ru.yandex.dto.ShopUnitImport;
import ru.yandex.dto.ShopUnitImportRequest;
import ru.yandex.dto.ShopUnitType;
import ru.yandex.models.Unit;
import ru.yandex.repository.ShopUnitRepository;
import ru.yandex.service.ShopService;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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


    static final String uidOne = "2e62c221-a62b-426c-820c-6f81e24fdbcc";
    static final Timestamp tOne = Timestamp.valueOf(LocalDateTime.now());
    static final Timestamp tTwo = Timestamp.valueOf(LocalDateTime.now().minusHours(1));
    static final Timestamp tThree = Timestamp.valueOf(LocalDateTime.now().minusDays(10));
    static final LocalDateTime lOne = LocalDateTime.now().minusDays(10);
    static final LocalDateTime lTwo = LocalDateTime.now().minusDays(30);
    static final String URL = "http://localhost:";


    @Test
    void saveSimpleEntityToDB() {
        ShopUnitImport unitImport = ShopUnitImport.builder()
                .id(uidOne)
                .type(ShopUnitType.OFFER)
                .name("TV")
                .price(900L)
                .build();

        shopService.create(unitImport, tOne);
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

        shopService.create(unitImport, tTwo);
        Unit unit = repository.findById(uidOne).get();
        assertNotNull(unit);
        assertNotNull(unit.getId());
        assertEquals(unit.getName(), "newTV");
        assertEquals(unit.getDate(), tTwo);
        assertEquals(unit.getPrice(), 333L);
        assertNull(unit.getParentId());
    }

    @Test
    void findAllUnits() {
        List<Unit> units = shopService.findAll();
        assertTrue(units.size() > 0);
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
        ShopUnitImportRequest importRequest = new ShopUnitImportRequest(list, lOne.toString());
        shopService.createAll(importRequest);
    }

    @Test
    void simpleImportTest() throws URISyntaxException {
        String categoryUid = UUID.randomUUID().toString();
        String offerUidOne = UUID.randomUUID().toString();
        String offerUidTwo = UUID.randomUUID().toString();
        String offerUidThree = UUID.randomUUID().toString();
        ShopUnitImport categoryOne = ShopUnitImport.builder()
                .type(ShopUnitType.CATEGORY)
                .id(categoryUid)
                .name("Mobile_Phones")
                .build();

        ShopUnitImport offerOne = new ShopUnitImport(ShopUnitType.OFFER, offerUidOne,
                "Samsung", categoryUid, 1000L);
        ShopUnitImport offerTwo = new ShopUnitImport(ShopUnitType.OFFER, offerUidTwo,
                "IPhone", categoryUid, 1200L);
        ShopUnitImport offerThree = new ShopUnitImport(ShopUnitType.OFFER, offerUidThree,
                "Xiaomi", categoryUid, 800L);
        ArrayList<ShopUnitImport> list = new ArrayList<>(Arrays.asList(categoryOne, offerOne, offerTwo, offerThree));

        RequestEntity<ShopUnitImportRequest> request = new RequestEntity<>(
                new ShopUnitImportRequest(list, lTwo.toString()), HttpMethod.POST, new URI(URL));
        ResponseEntity<String> response = restTemplate.postForEntity(URL + port + "/imports",
                request, String.class);
        assertEquals(response.getStatusCode(), HttpStatus.OK);
    }
}
