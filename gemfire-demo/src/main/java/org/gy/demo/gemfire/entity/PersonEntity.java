package org.gy.demo.gemfire.entity;

import java.io.Serializable;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.gemfire.mapping.annotation.Region;

/**
 * 功能描述：
 *
 * @author gy
 * @version 1.0.0
 * @date 2022/1/18 11:43
 */
@Region(value = "People")
public class PersonEntity implements Serializable {

    @Id
    @Getter
    private final String name;

    @Getter
    private final int age;

    @PersistenceConstructor
    public PersonEntity(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    @Override
    public String toString() {
        return String.format("%s is %d years old", getName(), getAge());
    }
}
