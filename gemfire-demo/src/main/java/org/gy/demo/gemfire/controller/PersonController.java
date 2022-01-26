package org.gy.demo.gemfire.controller;

import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.ClientCacheFactory;
import org.apache.geode.cache.client.ClientRegionShortcut;
import org.gy.demo.gemfire.entity.PersonEntity;
import org.gy.demo.gemfire.listener.CustomCacheSubscribeListener;
import org.gy.demo.gemfire.repository.PersonRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 功能描述：
 *
 * @author gy
 * @version 1.0.0
 * @date 2022/1/18 17:36
 */
@Slf4j
@RestController
@RequestMapping("/api/person")
public class PersonController {

    @Resource
    private PersonRepository personRepository;

    @PostMapping("/save")
    public ResponseEntity save(PersonEntity entity) {
        return ResponseEntity.ok(personRepository.save(entity));
    }

    @GetMapping("/findAll")
    public ResponseEntity findAll() {
        return ResponseEntity.ok(personRepository.findAll());
    }

    @GetMapping("/findByName")
    public ResponseEntity findByName(String name) {
        return ResponseEntity.ok(personRepository.findByName(name));
    }

    public static void main(String[] args) throws InterruptedException {
        ClientCache cache = new ClientCacheFactory()
            .addPoolLocator("127.0.0.1", 10334)
            .setPoolSubscriptionEnabled(true)
            .create();

        Region<String, Object> region = cache
            .<String, Object>createClientRegionFactory(ClientRegionShortcut.PROXY)
            .addCacheListener(new CustomCacheSubscribeListener())
            .create("test1");

        region.registerInterestRegex("key*");

        String value = (String) region.get("key3");
        System.out.println("region get value = " + value);

        cache.close();
    }
}
