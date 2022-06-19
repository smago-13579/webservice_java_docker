package ru.yandex.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.yandex.models.Unit;

@Repository
public interface ShopUnitRepository extends CrudRepository<Unit, Long> {
}
