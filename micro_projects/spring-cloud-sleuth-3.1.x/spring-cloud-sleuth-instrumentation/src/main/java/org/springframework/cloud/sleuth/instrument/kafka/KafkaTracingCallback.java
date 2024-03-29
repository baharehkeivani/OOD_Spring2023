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

package org.springframework.cloud.sleuth.instrument.kafka;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.RecordMetadata;

import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.Tracer;

/**
 * This decorates a Kafka {@link Callback} and completes the {@link Span.Kind#PRODUCER}
 * span created for the record when {@code onCompletion()} is invoked (i.e. the broker has
 * acknowledged or an {@link Exception}) was thrown.
 *
 * @author Anders Clausen
 * @author Flaviu Muresan
 * @since 3.1.0
 */
public class KafkaTracingCallback implements Callback {

	private static final Log log = LogFactory.getLog(KafkaTracingCallback.class);

	private final Callback callback;

	private final Tracer tracer;

	private final Span span;

	public KafkaTracingCallback(Callback callback, Tracer tracer, Span span) {
		this.callback = callback;
		this.tracer = tracer;
		this.span = span;
	}

	@Override
	public void onCompletion(RecordMetadata recordMetadata, Exception e) {
		try (Tracer.SpanInScope spanInScope = tracer.withSpan(this.span)) {
			if (this.callback != null) {
				this.callback.onCompletion(recordMetadata, e);
			}
		}
		finally {
			if (e != null) {
				this.span.error(e);
			}
			this.span.end();
			if (log.isDebugEnabled()) {
				log.debug("Finished producer span " + span);
			}
		}
	}

}
