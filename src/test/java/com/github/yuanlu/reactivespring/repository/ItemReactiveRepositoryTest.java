package com.github.yuanlu.reactivespring.repository;

import com.github.yuanlu.reactivespring.document.Item;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

@DataMongoTest
@RunWith(SpringRunner.class)
@DirtiesContext
public class ItemReactiveRepositoryTest {
    @Autowired
    ItemReactiveRepository itemReactiveRepository;

    List<Item> itemList = Arrays.asList(
            new Item(null, "Sumsung TV", 400.0),
            new Item(null, "LG TV", 420.0),
            new Item(null, "Apple Watch", 299.99),
            new Item(null, "Beats Headphones", 149.99),
            new Item("ABC", "Bose Headphones", 149.99)
    );

    @Before
    public void setUp() {
        itemReactiveRepository.deleteAll()
                .thenMany(Flux.fromIterable(itemList))
                .flatMap(itemReactiveRepository::save)
                .doOnNext((item) -> {
                    System.out.println("Inserted Item is :" + item);
                }).blockLast();
    }

    @Test
    public void testGetAllItems() {
        Flux<Item> flux = itemReactiveRepository.findAll();
        StepVerifier.create(flux)
                .expectSubscription()
                .expectNextCount(5)
                .verifyComplete();
    }

    @Test
    public void getItemById() {
        Mono<Item> mono = itemReactiveRepository.findById("ABC");

        StepVerifier.create(mono)
                .expectSubscription()
                .expectNextMatches(item -> item.getDescription().equals("Bose Headphones"))
                .verifyComplete();
    }

    @Test
    public void testFindItemByDescription() {
        Flux<Item> flux = itemReactiveRepository.findByDescription("Bose Headphones");

        StepVerifier.create(flux)
                .expectSubscription()
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    public void testSaveItem() {
        Item item = new Item(null, "Google Home Mini", 30.00);
        Mono<Item> mono = itemReactiveRepository.save(item);

        StepVerifier.create(mono)
                .expectSubscription()
                .expectNextMatches(i -> i.getId() != null && i.getDescription().equals("Google Home Mini"))
                .verifyComplete();
    }

    @Test
    public void updateItem() {
        Mono<Item> flux = itemReactiveRepository.findById("ABC")
                .map(item -> {
                    item.setPrice(520.0);
                    return item;
                }).flatMap(item -> {
                    return itemReactiveRepository.save(item);
        });

        StepVerifier.create(flux)
                .expectSubscription()
                .expectNextMatches(item -> item.getPrice() == 520.0)
                .verifyComplete();
    }

    @Test
    public void testDelete() {
        Mono<Void> deletedItem = itemReactiveRepository.findById("ABC")
                .map(Item::getId)
                .flatMap((id) -> itemReactiveRepository.deleteById(id));

        StepVerifier.create(deletedItem)
                .expectSubscription()
                .verifyComplete();

        StepVerifier.create(itemReactiveRepository.findAll())
                .expectSubscription()
                .expectNextCount(4)
                .verifyComplete();
    }
}