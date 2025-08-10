package org.gy.demo.mybatisplus.controller;

import org.gy.demo.mybatisplus.entity.TestInventory;
import org.gy.demo.mybatisplus.service.ITestInventoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/test")
public class TestInventoryController {

    @Resource
    private ITestInventoryService inventoryService;

    @PostMapping("/batchUpsertSelective")
    public ResponseEntity<Object> batchUpsertSelective(@RequestBody List<TestInventory> list) {
        Map<String, Object> result = new HashMap<>(2);
        int update = inventoryService.batchUpdateQty(list);
        result.put("update", update);
        result.put("list", list);
        return ResponseEntity.ok(result);
    }

}
