/*
 * Copyright (c) 2017-2021 AxonIQ B.V. and/or licensed to AxonIQ B.V.
 * under one or more contributor license agreements.
 *
 *  Licensed under the AxonIQ Open Source License Agreement v1.0;
 *  you may not use this file except in compliance with the license.
 *
 */

package io.axoniq.axonserver.plugin;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository of installed plugins.
 *
 * @author Marc Gathier
 * @since 4.5
 */
public interface PluginPackageRepository extends JpaRepository<PluginPackage, Long> {

    Optional<PluginPackage> findByNameAndVersion(String extension, String version);
}
