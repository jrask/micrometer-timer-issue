package se.flapsdown.micrometer.boot;

import io.micrometer.core.instrument.Timer;

import java.time.Duration;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class RunFailingHistogram {

    /*

      This runs original histograms without any modifications

     */

    static PrintHistogramStepMeterRegistry simpleRegistry = new PrintHistogramStepMeterRegistry();


    public static void main(String args[]) throws InterruptedException {

        simpleRegistry.start(Executors.defaultThreadFactory());

        Random durationRandom = new Random();

        while (true) {

            Timer.builder("test__no__tag_")
                    .sla(Duration.ofMillis(100), Duration.ofMillis(200), Duration.ofMillis(500))
                    .publishPercentileHistogram(false)
                    .register(simpleRegistry)
                    .record(Duration.ofMillis(durationRandom.nextInt(490)));

            TimeUnit.MILLISECONDS.sleep(100);

        }

    }

}
