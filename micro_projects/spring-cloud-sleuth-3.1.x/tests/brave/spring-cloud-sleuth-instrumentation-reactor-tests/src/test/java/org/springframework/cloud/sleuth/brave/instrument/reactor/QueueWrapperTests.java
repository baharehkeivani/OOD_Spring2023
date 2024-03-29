/*
 * Copyright 2013-2023 the original author or authors.
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

package org.springframework.cloud.sleuth.brave.instrument.reactor;

import org.springframework.cloud.sleuth.CurrentTraceContext;
import org.springframework.cloud.sleuth.TraceContext;
import org.springframework.cloud.sleuth.brave.bridge.BraveAccessor;

/**
 * @author Oleh Dokuka
 */
public class QueueWrapperTests extends org.springframework.cloud.sleuth.instrument.reactor.QueueWrapperTests {

	brave.propagation.CurrentTraceContext traceContext = brave.propagation.CurrentTraceContext.Default.create();

	CurrentTraceContext currentTraceContext = BraveAccessor.currentTraceContext(traceContext);

	TraceContext context = BraveAccessor
			.traceContext(brave.propagation.TraceContext.newBuilder().traceId(1).spanId(1).sampled(true).build());

	@Override
	protected CurrentTraceContext currentTraceContext() {
		return this.currentTraceContext;
	}

	@Override
	protected TraceContext context() {
		return this.context;
	}

}
