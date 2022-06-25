package ru.yandex.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.yandex.models.Statistic;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StatisticRepository extends CrudRepository<Statistic, Long> {
    List<Statistic> findAllByUid(String uid);

    @Query(value = "select * from goods.statistic where uid = :uid and date between :start and :end",
            nativeQuery = true)
    List<Statistic> findAllByUidWithDate(@Param("uid")String uid,
                                         @Param("start") LocalDateTime start,
                                         @Param("end") LocalDateTime end);

    @Query(value = "insert into goods.statistic (uid, type, name, date, price, parent_id) \n" +
            "values(:uid, :type, :name, :date, :price, :parent_id)  returning id;", nativeQuery = true)
    Long insert(@Param("uid") String uid, @Param("type") String type,
                        @Param("name") String name, @Param("date") LocalDateTime date,
                        @Param("price") Long price, @Param("parent_id") String parent_id);
}
