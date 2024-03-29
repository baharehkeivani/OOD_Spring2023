package io.quarkus.liquibase.deployment.devconsole;

import static io.quarkus.deployment.annotations.ExecutionTime.RUNTIME_INIT;

import io.quarkus.deployment.IsDevelopment;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.annotations.Record;
import io.quarkus.deployment.pkg.builditem.CurateOutcomeBuildItem;
import io.quarkus.devconsole.spi.DevConsoleRouteBuildItem;
import io.quarkus.devconsole.spi.DevConsoleRuntimeTemplateInfoBuildItem;
import io.quarkus.liquibase.runtime.devconsole.LiquibaseDevConsoleRecorder;
import io.quarkus.liquibase.runtime.devui.LiquibaseFactoriesSupplier;

public class LiquibaseDevConsoleProcessor {

    @BuildStep(onlyIf = IsDevelopment.class)
    public DevConsoleRuntimeTemplateInfoBuildItem collectBeanInfo(CurateOutcomeBuildItem curateOutcomeBuildItem) {
        return new DevConsoleRuntimeTemplateInfoBuildItem("liquibaseFactories", new LiquibaseFactoriesSupplier(),
                this.getClass(),
                curateOutcomeBuildItem);
    }

    @BuildStep
    @Record(value = RUNTIME_INIT, optional = true)
    DevConsoleRouteBuildItem invokeEndpoint(LiquibaseDevConsoleRecorder recorder) {
        return new DevConsoleRouteBuildItem("datasources", "POST", recorder.handler());
    }
}
