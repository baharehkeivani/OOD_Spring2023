import io.dekorate.utils.Serialization
import io.fabric8.kubernetes.api.model.KubernetesList
import io.fabric8.kubernetes.api.model.Container
import io.fabric8.kubernetes.api.model.Service
import io.fabric8.kubernetes.api.model.apps.Deployment;

//Check that file exits
String base = basedir
File kubernetesYml = new File(base, "target/kubernetes/kubernetes.yml")
assert kubernetesYml.exists()
kubernetesYml.withInputStream { stream -> 
    //Check that its parse-able
    KubernetesList list = Serialization.unmarshalAsList(stream)
    assert list != null

    //Most probably this (the handling of QUARKUS_CONTAINER_IMAGE_TAG) is something that we need to change, but until then this is needed.
    String version = System.getenv("QUARKUS_CONTAINER_IMAGE_TAG") != null ? System.getenv("QUARKUS_CONTAINER_IMAGE_TAG") : "0.1-SNAPSHOT"

    Deployment deployment = list.items.find{r -> r.kind == "Deployment" && r.metadata.name == "kubernetes-with-existing-selectorless-manifest"}
    assert deployment != null
    assert deployment.metadata.labels.get("app.kubernetes.io/name") == "kubernetes-with-existing-selectorless-manifest"
    assert deployment.metadata.labels.get("app.kubernetes.io/version") == version
    assert deployment.spec.replicas == 3

    assert deployment.spec.selector.matchLabels.get("app.kubernetes.io/name") == "kubernetes-with-existing-selectorless-manifest"
    assert deployment.spec.selector.matchLabels.get("app.kubernetes.io/version") == version
}
