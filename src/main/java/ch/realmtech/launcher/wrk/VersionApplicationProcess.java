package ch.realmtech.launcher.wrk;

import ch.realmtech.launcher.wrk.RealmTechData;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class VersionApplicationProcess {

    private final List<Process> processes;

    public VersionApplicationProcess() {
        this.processes = new ArrayList<>();
    }

    public void launchVersionFile(File versionFile) throws Exception {
        boolean realmTechVersion = RealmTechData.isRealmTechVersion(versionFile.getName());
        if (!realmTechVersion) {
            throw new IllegalArgumentException("Try to execute a non RealmTech version");
        }

        ProcessBuilder processBuilder = new ProcessBuilder("java", "-jar", versionFile.toString());
        Process process = processBuilder.start();
        processes.add(process);
    }
}
