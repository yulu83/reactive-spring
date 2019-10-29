package fluxandmonoplayground;

import org.junit.Test;
import org.springframework.data.mongodb.core.aggregation.ArrayOperators;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;

import java.sql.SQLOutput;
import java.time.Duration;

public class ColdAndHotTest {
    @Test
    public void coldPublisherTest() throws InterruptedException {
        Flux numFlux = Flux.just(1, 2, 3, 4, 5, 6, 7).delayElements(Duration.ofSeconds(1));

        numFlux.subscribe(i -> System.out.println("Sub1: " + i)); // emits value from beginning

        Thread.sleep(2000);

        numFlux.subscribe(i -> System.out.println("Sub2: " + i));

        Thread.sleep(4000);
    }

    @Test
    public void hotPublisher() throws InterruptedException {
        Flux numFlux = Flux.just(1, 2, 3, 4, 5, 6, 7).delayElements(Duration.ofSeconds(1));

        ConnectableFlux<Integer> cf = numFlux.publish();
        cf.connect();

        cf.subscribe(i -> System.out.println("Sub1: " + i));

        Thread.sleep(3000);

        cf.subscribe(i -> System.out.println("Sub2: " + i));

        Thread.sleep(4000);
    }
}
