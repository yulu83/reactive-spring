package fluxandmonoplayground;

import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

public class FluxAndMonoFactoryTest {

    List<String> names = Arrays.asList("adam", "anna", "jack", "jenny");

    @Test
    public void fluxUsingIterable() {
        Flux<String> namesFlux = Flux.fromIterable(names).log();

        StepVerifier.create(namesFlux)
                .expectNext("adam", "anna", "jack", "jenny")
                .verifyComplete();
    }

    @Test
    public void fluxUsingArray() {
        String[] array = (String[]) names.toArray();
        Flux<String> arrayFlux = Flux.fromArray(array).log();

        StepVerifier.create(arrayFlux)
                .expectNext("adam", "anna", "jack", "jenny")
                .verifyComplete();
    }

    @Test
    public void fluxUsingStream() {
        Flux<String> streamFlux = Flux.fromStream(names.stream());
        StepVerifier.create(streamFlux)
                .expectNext("adam", "anna", "jack", "jenny")
                .verifyComplete();
    }

    @Test
    public void monoUsingJustOrEmpty() {
        Mono<String> mono = Mono.justOrEmpty(null); // Mono.Empty

        StepVerifier.create(mono.log())
                .verifyComplete();
    }

    @Test
    public void monoUsingSupplier() {
        Mono<String> mono = Mono.fromSupplier(() -> "hello").log();

        StepVerifier.create(mono)
                .expectNext("hello")
                .verifyComplete();
    }

    @Test
    public void fluxUsingRange() {
        Flux<Integer> flux = Flux.range(1, 5);
        StepVerifier.create(flux)
                .expectNext(1, 2, 3, 4, 5)
                .verifyComplete();
    }
}
