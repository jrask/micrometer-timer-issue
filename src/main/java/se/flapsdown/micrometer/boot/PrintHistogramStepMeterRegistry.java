package se.flapsdown.micrometer.boot;

import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.distribution.HistogramSnapshot;
import io.micrometer.core.instrument.step.StepMeterRegistry;
import io.micrometer.core.instrument.step.StepRegistryConfig;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

public class PrintHistogramStepMeterRegistry extends StepMeterRegistry {

    static StepRegistryConfig sc = new StepRegistryConfig() {

        @Override
        public Duration step() {
            return Duration.ofSeconds(3);
        }

        @Override
        public String prefix() {
            return null;
        }

        @Override
        public String get(String s) {
            return null;
        }

    };


    protected StepRegistryConfig config;

    public PrintHistogramStepMeterRegistry(StepRegistryConfig config, Clock clock) {
        super(config, clock);
        this.config = config;
    }


    public PrintHistogramStepMeterRegistry() {
        this(sc, Clock.SYSTEM);
    }

    @Override
    protected void publish() {

        for (Meter m : getMeters()) {
            if (m instanceof Timer) {
                Timer t = (Timer)m;

                // When running FailureMain, 20ms sleep will cause incorrect result due to histogram rotate.
                // When running UpdatedMain, this will not matter since rotate is not done until takeSnapshot
                // is invoked
                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                HistogramSnapshot histogramSnapshot = t.takeSnapshot();
                System.out.println(t.count() + ", " + histogramSnapshot);
            }
        }
        System.out.println("###### ");
    }


    @Override
    protected TimeUnit getBaseTimeUnit() {
        return TimeUnit.MILLISECONDS;
    }
}
