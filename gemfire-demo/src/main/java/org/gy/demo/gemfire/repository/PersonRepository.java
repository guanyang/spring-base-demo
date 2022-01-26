package org.gy.demo.gemfire.repository;

import java.util.Collection;
import org.gy.demo.gemfire.entity.PersonEntity;
import org.springframework.data.gemfire.repository.query.annotation.Trace;
import org.springframework.data.repository.CrudRepository;

/**
 * 功能描述：
 *
 * @author gy
 * @version 1.0.0
 * @date 2022/1/18 11:46
 */
public interface PersonRepository extends CrudRepository<PersonEntity, String> {

    @Trace
    PersonEntity findByName(String name);

    @Trace
    Collection<PersonEntity> findByAgeGreaterThan(int age);

    @Trace
    Collection<PersonEntity> findByAgeLessThan(int age);

    @Trace
    Collection<PersonEntity> findByAgeGreaterThanAndAgeLessThan(int greaterThanAge, int lessThanAge);

}
