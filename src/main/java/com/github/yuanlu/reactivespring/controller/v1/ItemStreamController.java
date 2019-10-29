package com.github.yuanlu.reactivespring.controller.v1;

import com.github.yuanlu.reactivespring.constants.ItemConstants;
import com.github.yuanlu.reactivespring.document.ItemCapped;
import com.github.yuanlu.reactivespring.repository.ItemReactiveCappedRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
public class ItemStreamController {

    @Autowired
    ItemReactiveCappedRepository itemReactiveCappedRepository;

    @GetMapping(value = ItemConstants.ITEM_STREAM_END_POINT_V1, produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
    public Flux<ItemCapped> getItemStream() {
        return itemReactiveCappedRepository.findItemsBy();
    }
}
