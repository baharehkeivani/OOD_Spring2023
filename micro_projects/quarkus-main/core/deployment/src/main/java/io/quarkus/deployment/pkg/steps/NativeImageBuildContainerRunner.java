package io.quarkus.deployment.pkg.steps;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Stream;

import org.apache.commons.lang3.RandomStringUtils;
import org.jboss.logging.Logger;

import io.quarkus.deployment.pkg.NativeConfig;
import io.quarkus.deployment.util.ProcessUtil;
import io.quarkus.runtime.util.ContainerRuntimeUtil;

public abstract class NativeImageBuildContainerRunner extends NativeImageBuildRunner {

    private static final Logger log = Logger.getLogger(NativeImageBuildContainerRunner.class);

    final NativeConfig nativeConfig;
    protected final ContainerRuntimeUtil.ContainerRuntime containerRuntime;
    String[] baseContainerRuntimeArgs;
    private final String containerName;

    protected NativeImageBuildContainerRunner(NativeConfig nativeConfig) {
        this.nativeConfig = nativeConfig;
        containerRuntime = nativeConfig.containerRuntime().orElseGet(ContainerRuntimeUtil::detectContainerRuntime);

        this.baseContainerRuntimeArgs = new String[] { "--env", "LANG=C", "--rm" };

        containerName = "build-native-" + RandomStringUtils.random(5, true, false);
    }

    @Override
    public boolean isContainer() {
        return true;
    }

    @Override
    public void setup(boolean processInheritIODisabled) {
        if (containerRuntime != ContainerRuntimeUtil.ContainerRuntime.UNAVAILABLE) {
            log.infof("Using %s to run the native image builder", containerRuntime.getExecutableName());
            // we pull the docker image in order to give users an indication of which step the process is at
            // it's not strictly necessary we do this, however if we don't the subsequent version command
            // will appear to block and no output will be shown
            String effectiveBuilderImage = nativeConfig.getEffectiveBuilderImage();
            log.info("Checking image status " + effectiveBuilderImage);
            Process pullProcess = null;
            try {
                final ProcessBuilder pb = new ProcessBuilder(
                        Arrays.asList(containerRuntime.getExecutableName(), "pull", effectiveBuilderImage));
                pullProcess = ProcessUtil.launchProcess(pb, processInheritIODisabled);
                pullProcess.waitFor();
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException("Failed to pull builder image " + effectiveBuilderImage, e);
            } finally {
                if (pullProcess != null) {
                    pullProcess.destroy();
                }
            }
        }
    }

    @Override
    protected String[] getGraalVMVersionCommand(List<String> args) {
        return buildCommand("run", Collections.singletonList("--rm"), args);
    }

    @Override
    protected String[] getBuildCommand(Path outputDir, List<String> args) {
        List<String> containerRuntimeBuildArgs = getContainerRuntimeBuildArgs(outputDir);
        List<String> effectiveContainerRuntimeBuildArgs = new ArrayList<>(containerRuntimeBuildArgs.size() + 2);
        effectiveContainerRuntimeBuildArgs.addAll(containerRuntimeBuildArgs);
        effectiveContainerRuntimeBuildArgs.add("--name");
        effectiveContainerRuntimeBuildArgs.add(containerName);
        return buildCommand("run", effectiveContainerRuntimeBuildArgs, args);
    }

    @Override
    protected void objcopy(Path outputDir, String... args) {
        final List<String> containerRuntimeBuildArgs = getContainerRuntimeBuildArgs(outputDir);
        Collections.addAll(containerRuntimeBuildArgs, "--entrypoint", "/bin/bash");
        final ArrayList<String> objcopyCommand = new ArrayList<>(2);
        objcopyCommand.add("-c");
        objcopyCommand.add("objcopy " + String.join(" ", args));
        final String[] command = buildCommand("run", containerRuntimeBuildArgs, objcopyCommand);
        runCommand(command, null, null);
    }

    @Override
    public void addShutdownHook(Process process) {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (process.isAlive()) {
                try {
                    Process removeProcess = new ProcessBuilder(
                            List.of(containerRuntime.getExecutableName(), "rm", "-f", containerName))
                            .redirectOutput(ProcessBuilder.Redirect.DISCARD)
                            .redirectError(ProcessBuilder.Redirect.DISCARD)
                            .start();
                    removeProcess.waitFor(2, TimeUnit.SECONDS);
                } catch (IOException | InterruptedException e) {
                    log.debug("Unable to stop running container", e);
                }
            }
        }));
    }

    protected List<String> getContainerRuntimeBuildArgs(Path outputDir) {
        List<String> containerRuntimeArgs = new ArrayList<>();
        nativeConfig.containerRuntimeOptions().ifPresent(containerRuntimeArgs::addAll);
        if (nativeConfig.debugBuildProcess() && nativeConfig.publishDebugBuildProcessPort()) {
            // publish the debug port onto the host if asked for
            containerRuntimeArgs.add("--publish=" + NativeImageBuildStep.DEBUG_BUILD_PROCESS_PORT + ":"
                    + NativeImageBuildStep.DEBUG_BUILD_PROCESS_PORT);
        }
        return containerRuntimeArgs;
    }

    protected String[] buildCommand(String dockerCmd, List<String> containerRuntimeArgs, List<String> command) {
        return Stream
                .of(Stream.of(containerRuntime.getExecutableName()), Stream.of(dockerCmd), Stream.of(baseContainerRuntimeArgs),
                        containerRuntimeArgs.stream(), Stream.of(nativeConfig.getEffectiveBuilderImage()), command.stream())
                .flatMap(Function.identity()).toArray(String[]::new);
    }

}
