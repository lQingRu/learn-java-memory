package com.qingru.springbootmemory.controller;

import com.qingru.springbootmemory.service.SingleIndexerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IndexController {

    @Autowired
    private SingleIndexerService singleIndexerService;

    @GetMapping("/single-index-all")
    public void singleIndexAllCollections() {
        singleIndexerService.runWhole();
    }

    @GetMapping("/single-index-blocks")
    public void singleIndexBlocksCollections() {
        singleIndexerService.runBlocks();
    }
}
