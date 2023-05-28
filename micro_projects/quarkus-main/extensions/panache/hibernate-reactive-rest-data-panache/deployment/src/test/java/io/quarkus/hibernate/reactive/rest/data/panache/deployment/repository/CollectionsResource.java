package io.quarkus.hibernate.reactive.rest.data.panache.deployment.repository;

import io.quarkus.hibernate.reactive.rest.data.panache.PanacheRepositoryResource;
import io.quarkus.rest.data.panache.MethodProperties;
import io.quarkus.rest.data.panache.ResourceProperties;
import io.smallrye.mutiny.Uni;

@ResourceProperties(hal = true, paged = false, halCollectionName = "item-collections", rolesAllowed = "user")
public interface CollectionsResource extends PanacheRepositoryResource<CollectionsRepository, Collection, String> {

    @MethodProperties(rolesAllowed = "admin")
    Uni<Boolean> delete(String name);
}
