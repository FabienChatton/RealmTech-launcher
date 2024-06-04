package ch.realmtech.launcher.wrk;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;

public class ApplicationProcess implements Closeable {

    private final Process process;
    private final Thread applicationProcessThread;
    private volatile boolean applicationProcessRun;
    private final Runnable onProcessClose;

    public ApplicationProcess(Process process, Runnable onProcessClose) {
        this.process = process;
        this.onProcessClose = onProcessClose;
        applicationProcessThread = new Thread(applicationProcessRunnable());
        applicationProcessThread.start();
        applicationProcessRun = true;

        this.process.onExit().thenApply((processFuture) -> {
            applicationProcessRun = false;
            onProcessClose.run();
            return processFuture;
        });
    }

    public static ApplicationProcess launchVersionFile(File versionFile, Runnable onProcessClose) throws Exception {
        if (!RealmTechData.isRealmTechVersion(versionFile.getName())) {
            throw new IllegalArgumentException("Try to execute a non RealmTech version");
        }

        ProcessBuilder processBuilder = new ProcessBuilder("java", "-jar", versionFile.toString());
        processBuilder.inheritIO();
        return new ApplicationProcess(processBuilder.start(), onProcessClose);
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
