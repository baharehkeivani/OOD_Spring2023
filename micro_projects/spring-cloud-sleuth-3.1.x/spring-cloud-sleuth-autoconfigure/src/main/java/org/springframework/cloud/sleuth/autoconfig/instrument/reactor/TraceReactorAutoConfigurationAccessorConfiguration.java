/*
 * Copyright 2013-2021 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.cloud.sleuth.autoconfig.instrument.reactor;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.context.ConfigurableApplicationContext;

/**
 * @author Marcin Grzejszczak
 */
public final class TraceReactorAutoConfigurationAccessorConfiguration {

	private TraceReactorAutoConfigurationAccessorConfiguration() {
		throw new IllegalStateException("Can't instantiate a utility class");
	}

	private static final Log log = LogFactory.getLog(TraceReactorAutoConfigurationAccessorConfiguration.class);

	public static void close() {
		if (log.isTraceEnabled()) {
			log.trace("Cleaning up hooks");
		}
		try {
			new HookRegisteringBeanFactoryPostProcessor().close();
		}
		catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

	public static void setup(ConfigurableApplicationContext context) {
		if (log.isTraceEnabled()) {
			log.trace("Setting up hooks");
		}
		HookRegisteringBeanFactoryPostProcessor.setupHooks(context);
	}

}
