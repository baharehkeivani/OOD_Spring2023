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

package org.springframework.cloud.sleuth.autoconfig.instrument.quartz;

import org.quartz.Scheduler;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.quartz.QuartzAutoConfiguration;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.cloud.sleuth.autoconfig.brave.BraveAutoConfiguration;
import org.springframework.cloud.sleuth.instrument.quartz.TracingJobListener;
import org.springframework.cloud.sleuth.propagation.Propagator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * {@link org.springframework.boot.autoconfigure.EnableAutoConfiguration
 * Auto-configuration} enables Quartz span information propagation.
 *
 * @author Branden Cash
 * @since 2.2.0
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnBean({ Tracer.class, Scheduler.class })
@ConditionalOnProperty(value = "spring.sleuth.quartz.enabled", matchIfMissing = true)
@AutoConfigureAfter({ BraveAutoConfiguration.class, QuartzAutoConfiguration.class })
public class TraceQuartzAutoConfiguration implements InitializingBean {

	private final Scheduler scheduler;

	private final BeanFactory beanFactory;

	@Deprecated
	public TraceQuartzAutoConfiguration(Scheduler scheduler, Tracer tracer, Propagator propagator) {
		this(scheduler, null);
	}

	@Autowired
	public TraceQuartzAutoConfiguration(Scheduler scheduler, BeanFactory beanFactory) {
		this.scheduler = scheduler;
		this.beanFactory = beanFactory;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		TracingJobListener tracingJobListener = this.beanFactory.getBean(TracingJobListener.class);
		this.scheduler.getListenerManager().addTriggerListener(tracingJobListener);
		this.scheduler.getListenerManager().addJobListener(tracingJobListener);
	}

	@Configuration(proxyBeanMethods = false)
	static class Config {

		@Bean
		TracingJobListener tracingJobListener(Tracer tracer, Propagator propagator) {
			return new TracingJobListener(tracer, propagator);
		}

	}

}
