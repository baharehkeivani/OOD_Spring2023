package io.quarkus.hibernate.orm.deployment;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import jakarta.persistence.Entity;

import org.jboss.jandex.DotName;

import io.quarkus.builder.item.SimpleBuildItem;
import io.quarkus.hibernate.orm.runtime.boot.xml.RecordableXmlMapping;

/**
 * Internal model to represent which objects are likely needing enhancement
 * via HibernateEntityEnhancer.
 */
public final class JpaModelBuildItem extends SimpleBuildItem {

    private final Set<String> allModelPackageNames = new TreeSet<>();
    private final Set<String> entityClassNames = new TreeSet<>();
    private final Set<DotName> potentialCdiBeanClassNames = new TreeSet<>();
    private final Set<String> allModelClassNames = new TreeSet<>();
    private final Map<String, List<RecordableXmlMapping>> xmlMappingsByPU = new HashMap<>();

    public JpaModelBuildItem(Set<String> allModelPackageNames, Set<String> entityClassNames,
            Set<DotName> potentialCdiBeanClassNames, Set<String> allModelClassNames,
            Map<String, List<RecordableXmlMapping>> xmlMappingsByPU) {
        this.allModelPackageNames.addAll(allModelPackageNames);
        this.entityClassNames.addAll(entityClassNames);
        this.potentialCdiBeanClassNames.addAll(potentialCdiBeanClassNames);
        this.allModelClassNames.addAll(allModelClassNames);
        this.xmlMappingsByPU.putAll(xmlMappingsByPU);
    }

    /**
     * @return the list of packages annotated with a JPA annotation.
     */
    public Set<String> getAllModelPackageNames() {
        return allModelPackageNames;
    }

    /**
     * @return the list of entities (i.e. classes marked with {@link Entity})
     */
    public Set<String> getEntityClassNames() {
        return entityClassNames;
    }

    /**
     * @return the list of classes that might be retrieved by Hibernate ORM as CDI beans,
     *         e.g. converters, listeners, ...
     */
    public Set<DotName> getPotentialCdiBeanClassNames() {
        return potentialCdiBeanClassNames;
    }

    /**
     * @return the list of all model class names: entities, mapped super classes, {@link #getPotentialCdiBeanClassNames()}...
     */
    public Set<String> getAllModelClassNames() {
        return allModelClassNames;
    }

    /**
     * @return the list of all XML mappings for the given persistence unit.
     */
    public List<RecordableXmlMapping> getXmlMappings(String puName) {
        return xmlMappingsByPU.getOrDefault(puName, Collections.emptyList());
    }
}
