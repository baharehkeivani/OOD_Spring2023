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

package org.springframework.cloud.sleuth.autoconfig.instrument.circuitbreaker;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.sleuth.CurrentTraceContext;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.cloud.sleuth.autoconfig.brave.BraveAutoConfiguration;
import org.springframework.cloud.sleuth.instrument.circuitbreaker.TraceCircuitBreakerFactoryAspect;
import org.springframework.cloud.sleuth.instrument.circuitbreaker.TraceReactiveCircuitBreakerFactoryAspect;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * {@link org.springframework.boot.autoconfigure.EnableAutoConfiguration
 * Auto-configuration} that registers instrumentation for circuit breakers.
 *
 * @author Marcin Grzejszczak
 * @since 2.2.1
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnBean(Tracer.class)
@ConditionalOnProperty(value = "spring.sleuth.circuitbreaker.enabled", matchIfMissing = true)
@EnableConfigurationProperties(SleuthCircuitBreakerProperties.class)
@AutoConfigureAfter(BraveAutoConfiguration.class)
public class TraceCircuitBreakerAutoConfiguration {

	@Bean
	@ConditionalOnClass(name = "org.springframework.cloud.client.circuitbreaker.CircuitBreaker")
	TraceCircuitBreakerFactoryAspect traceCircuitBreakerFactoryAspect(Tracer tracer) {
		return new TraceCircuitBreakerFactoryAspect(tracer);
	}

	@Bean
	@ConditionalOnClass(name = { "reactor.core.publisher.Mono",
			"org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreaker" })
	TraceReactiveCircuitBreakerFactoryAspect traceReactiveCircuitBreakerFactoryAspect(Tracer tracer,
			CurrentTraceContext currentTraceContext) {
		return new TraceReactiveCircuitBreakerFactoryAspect(tracer, currentTraceContext);
	}

}
