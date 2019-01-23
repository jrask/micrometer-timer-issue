package se.flapsdown.micrometer.boot;

import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.distribution.DistributionStatisticConfig;
import io.micrometer.core.instrument.distribution.HistogramGauges;
import io.micrometer.core.instrument.distribution.pause.PauseDetector;
import io.micrometer.core.instrument.step.StepMeterRegistry;
import se.flapsdown.micrometer.StepTimer;

import java.time.Duration;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class RunUpdatedCorrectHistogram {

    /*
      This runs new Timer with modified Histogram that only (forced) rotates when takeSnapshot() is invoked
      With this knowledge, we can correctly use timers in publish() method without having them
      suddenly rotated.
     */


    static StepMeterRegistry simpleRegistry = new Registry();

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



    static class Registry extends PrintHistogramStepMeterRegistry {


        @Override
        protected Timer newTimer(Meter.Id id, DistributionStatisticConfig distributionStatisticConfig, PauseDetector pauseDetector) {
            Timer timer = new StepTimer(id, clock, distributionStatisticConfig, pauseDetector, getBaseTimeUnit(),
                config.step().toMillis(), false);
            HistogramGauges.registerWithCommonFormat(timer, this);
            return timer;
        }

    }

}
