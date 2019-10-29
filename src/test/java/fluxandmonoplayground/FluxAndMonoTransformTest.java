package fluxandmonoplayground;

import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

import static reactor.core.scheduler.Schedulers.parallel;

public class FluxAndMonoTransformTest {

    List<String> names = Arrays.asList("adam", "anna", "jack", "jenny");

    @Test
    public void transformUsingMap() {
        Flux<String> namesFlux = Flux.fromIterable(names)
                .map(s -> s.toUpperCase())
                .log();

        StepVerifier.create(namesFlux)
                .expectNext("ADAM", "ANNA", "JACK", "JENNY")
                .verifyComplete();
    }

    @Test
    public void transformUsingMapLength() {
        Flux<Integer> namesFlux = Flux.fromIterable(names)
                .map(s -> s.length())
                .log();

        StepVerifier.create(namesFlux)
                .expectNext(4, 4, 4, 5)
                .verifyComplete();
    }

    @Test
    public void transformUsingMapLength_repeat() {
        Flux<Integer> namesFlux = Flux.fromIterable(names)
                .map(s -> s.length())
                .repeat(1)
                .log();

        StepVerifier.create(namesFlux)
                .expectNext(4, 4, 4, 5, 4, 4, 4, 5)
                .verifyComplete();
    }

    @Test
    public void transformUsingFlatMap() {
        Flux<String> names = Flux.fromIterable(Arrays.asList("A", "B", "C", "D", "E", "F"))
                .flatMap(s -> {
                    return Flux.fromIterable(convertToList(s));
                }).log();

        StepVerifier.create(names)
                .expectNextCount(12)
                .verifyComplete();
    }

    private List<String> convertToList(String s) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            //
        }
        return Arrays.asList(s, "newValue");
    }

    @Test
    public void transformUsingFlatMapUsingParallel() {
        Flux<String> names = Flux.fromIterable(Arrays.asList("A", "B", "C", "D", "E", "F"))
                .window(2) // Flux<Flux<String>>
                .flatMap(s ->
                    s.map(this::convertToList).subscribeOn(parallel())
                .flatMap(t -> Flux.fromIterable(t))).log();

        StepVerifier.create(names)
                .expectNextCount(12)
                .verifyComplete();
    }

    @Test
    public void transformUsingFlatMapUsingParallelSequential() {
        Flux<String> names = Flux.fromIterable(Arrays.asList("A", "B", "C", "D", "E", "F"))
                .window(2) // Flux<Flux<String>>
                .flatMapSequential(s ->
                        s.map(this::convertToList).subscribeOn(parallel())
                                .flatMap(t -> Flux.fromIterable(t))).log();

        StepVerifier.create(names)
                .expectNextCount(12)
                .verifyComplete();
    }
}
