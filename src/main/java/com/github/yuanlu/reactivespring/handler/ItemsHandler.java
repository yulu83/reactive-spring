package com.github.yuanlu.reactivespring.handler;

import com.github.yuanlu.reactivespring.document.Item;
import com.github.yuanlu.reactivespring.document.ItemCapped;
import com.github.yuanlu.reactivespring.repository.ItemReactiveCappedRepository;
import com.github.yuanlu.reactivespring.repository.ItemReactiveRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static org.springframework.web.reactive.function.BodyInserters.fromObject;

@Component
public class ItemsHandler {
    @Autowired
    ItemReactiveRepository itemReactiveRepository;
    @Autowired
    ItemReactiveCappedRepository itemReactiveCappedRepository;

    static Mono<ServerResponse> notFound = ServerResponse.notFound().build();

    public Mono<ServerResponse> getAllItems(ServerRequest serverRequest) {
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(itemReactiveRepository.findAll(), Item.class);
    }

    public Mono<ServerResponse> getOneItem(ServerRequest serverRequest) {
        String id = serverRequest.pathVariable("id");
        Mono<Item> mono = itemReactiveRepository.findById(id);

        return mono.flatMap(item ->
                ServerResponse.ok()
        .contentType(MediaType.APPLICATION_JSON)
        .body(fromObject(item))).switchIfEmpty(notFound);
    }

    public Mono<ServerResponse> createItem(ServerRequest serverRequest) {
        Mono<Item> mono = serverRequest.bodyToMono(Item.class);
        return mono.flatMap(item -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
        .body(itemReactiveRepository.save(item), Item.class));
    }

    public Mono<ServerResponse> deleteItem(ServerRequest serverRequest) {
        String id = serverRequest.pathVariable("id");
        Mono<Void> mono = itemReactiveRepository.deleteById(id);

        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(mono, Void.class);
    }

    public Mono<ServerResponse> updateItem(ServerRequest serverRequest) {
        String id = serverRequest.pathVariable("id");
        Mono<Item> updatedItem = serverRequest.bodyToMono(Item.class)
                .flatMap((item) -> {
                    Mono<Item> itemMono = itemReactiveRepository.findById(id)
                            .flatMap(currentItem -> {
                                currentItem.setDescription(item.getDescription());
                                currentItem.setPrice(item.getPrice());
                                return itemReactiveRepository.save(currentItem);
                            });
                    return itemMono;
                });

        return updatedItem.flatMap(item -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
        .body(updatedItem, Item.class)).switchIfEmpty(notFound);

    }

    public Mono<ServerResponse> itemsException(ServerRequest serverRequest) {
        throw new RuntimeException("RuntimeException Occurred");
    }

    public Mono<ServerResponse> itemStream(ServerRequest serverRequest) {
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_STREAM_JSON)
                .body(itemReactiveCappedRepository.findItemsBy(), ItemCapped.class);
    }
}
