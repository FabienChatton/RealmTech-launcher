package ch.realmtech.launcher.wrk;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;

public class VersionApplicationProcess implements Closeable {

    private final Process process;
    private final Thread applicationProcessThread;
    private volatile boolean applicationProcessRun;

    public VersionApplicationProcess(Process process) {
        this.process = process;
        applicationProcessThread = new Thread(applicationProcessRunnable());
        applicationProcessThread.start();
        applicationProcessRun = true;

        this.process.onExit().thenApply((processFuture) -> {
            applicationProcessRun = false;
            return processFuture;
        });
    }

    public static VersionApplicationProcess launchVersionFile(File versionFile) throws Exception {
        if (!RealmTechData.isRealmTechVersion(versionFile.getName())) {
            throw new IllegalArgumentException("Try to execute a non RealmTech version");
        }

        ProcessBuilder processBuilder = new ProcessBuilder("java", "-jar", versionFile.toString());
        return new VersionApplicationProcess(processBuilder.start());
    }


    public Runnable applicationProcessRunnable() {
        return () -> {
            while (applicationProcessRun) {
                Thread.onSpinWait();
            }
            process.destroy();
        };
    }

    @Override
    public void close() throws IOException {
        applicationProcessRun = false;
        try {
            applicationProcessThread.join();
        } catch (InterruptedException e) {
            throw new IOException(e);
        }
    }
}
