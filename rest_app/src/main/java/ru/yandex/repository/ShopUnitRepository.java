package ru.yandex.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.yandex.models.Unit;

import java.sql.Timestamp;

@Repository
public interface ShopUnitRepository extends CrudRepository<Unit, String> {

    @Query(value = "insert into goods.unit (uid, type, name, date, price, parent_id) \n" +
            "values(:uid, :type, :name, :date, :price, :parent_id) on conflict(uid) do update set \n" +
            "name = excluded.name, date = excluded.date, \n" +
            "price = excluded.price, parent_id = excluded.parent_id returning id;", nativeQuery = true)
    Long insertOrUpdate(@Param("uid") String uid, @Param("type") String type,
                        @Param("name") String name, @Param("date") Timestamp date, 
                        @Param("price") Long price, @Param("parent_id") String parent_id);
}
