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

package org.springframework.cloud.sleuth.internal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.beans.factory.BeanFactory;

/**
 * Utility class that verifies that context is in creation. Do not use.
 *
 * @author Marcin Grzejszczak
 * @since 2.1.0
 */
public final class ContextUtil {

	private ContextUtil() {
		throw new IllegalStateException("Can't instantiate a utility class");
	}

	private static final Log log = LogFactory.getLog(ContextUtil.class);

	/**
	 * @param beanFactory bean factory
	 * @return {@code true} when context is not ready to be used
	 */
	public static boolean isContextUnusable(BeanFactory beanFactory) {
		org.springframework.cloud.sleuth.internal.SleuthContextListener listener = org.springframework.cloud.sleuth.internal.SleuthContextListener
				.getBean(beanFactory);
		boolean contextUnusable = listener.isUnusable();
		if (contextUnusable && log.isDebugEnabled()) {
			log.debug("Context [" + Integer.toHexString(beanFactory.hashCode())
					+ "] is either not refreshed or is closed");
		}
		return contextUnusable;
	}

}
