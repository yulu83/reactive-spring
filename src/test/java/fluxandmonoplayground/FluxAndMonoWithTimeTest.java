package fluxandmonoplayground;


import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;

public class FluxAndMonoWithTimeTest {

//    @Test
//    public void infiniteSequence() {
//        Flux<Long> infiniteFlux = Flux.interval(Duration.ofMillis(200)).log();
//
//        infiniteFlux.subscribe(i -> System.out.println(i));
//    }

    @Test
    public void testInfiniteSequence() {
        Flux<Long> infiniteFlux = Flux.interval(Duration.ofMillis(100)).take(3).log();

        StepVerifier.create(infiniteFlux)
                .expectSubscription()
                .expectNext(0L, 1L, 2L)
                .verifyComplete();
    }

    @Test
    public void testInfiniteSequenceMap() {
        Flux<Integer> infiniteFlux = Flux.interval(Duration.ofMillis(100))
                .map(l -> new Integer(l.intValue()))
                .take(3).log();

        StepVerifier.create(infiniteFlux)
                .expectSubscription()
                .expectNext(0, 1, 2)
                .verifyComplete();
    }


}
