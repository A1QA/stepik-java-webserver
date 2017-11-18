package main;

/**
 * Created by esin on 06.12.2014.
 */
public interface ThreadSettings {
    int CLIENT_SLEEP_TIME = 25;
    int SERVICE_SLEEP_TIME = 100;

    static void serviceSleep() {
        try {
            Thread.sleep(ThreadSettings.SERVICE_SLEEP_TIME);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
