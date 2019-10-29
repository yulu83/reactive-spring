package fluxandmonoplayground;

import org.junit.Test;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

public class FluxAndMonoBackpressureTest {

    @Test
    public void backPressureTest() {
        Flux<Integer> finiteFlux = Flux.range(1, 10).log();

        StepVerifier.create(finiteFlux)
                .expectSubscription()
                .thenRequest(1)
                .expectNext(1)
                .thenRequest(1)
                .expectNext(2)
                .thenCancel()
                .verify();
    }

    @Test
    public void backPressure() {
        Flux<Integer> finiteFlux = Flux.range(1, 10).log();

        finiteFlux.subscribe((elem) -> {
            System.out.println("Element is: " + elem);
        }, (e) -> System.err.println("Exception is: " + e),
                () -> System.out.println("Completed"),
                (subscription -> subscription.request(2)));
    }

    @Test
    public void backPressureCancel() {
        Flux<Integer> finiteFlux = Flux.range(1, 10).log();

        finiteFlux.subscribe((elem) -> {
                    System.out.println("Element is: " + elem);
                }, (e) -> System.err.println("Exception is: " + e),
                () -> System.out.println("Completed"),
                (subscription -> subscription.cancel()));
    }

    @Test
    public void backPressureCustomized() {
        Flux<Integer> finiteFlux = Flux.range(1, 10).log();

        finiteFlux.subscribe(new BaseSubscriber<Integer>() {
            @Override
            protected void hookOnNext(Integer value) {
                request(1);
                System.out.println("Value is :" + value);
                if (value == 4) {
                    cancel();
                }
            }
        });
    }
}
