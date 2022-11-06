package org.gy.demo.vertx.service.dto;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;
import java.io.Serializable;

/**
 * 功能描述：
 *
 * @author gy
 * @version 1.0.0
 * @date 2022/11/5 23:18
 */
@DataObject(generateConverter = true)
public class User implements Serializable {

    private static final long serialVersionUID = 3220597525614784582L;

    private String name;

    private Integer age;

    public User() {
    }

    public User(JsonObject jsonObject) {
        UserConverter.fromJson(jsonObject, this);
    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        UserConverter.toJson(this, json);
        return json;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }
}
