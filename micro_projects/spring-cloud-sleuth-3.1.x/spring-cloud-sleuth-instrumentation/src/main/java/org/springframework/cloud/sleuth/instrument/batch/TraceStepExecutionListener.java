/*
 * Copyright 2018-2021 the original author or authors.
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

package org.springframework.cloud.sleuth.instrument.batch;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.SpanAndScope;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.cloud.sleuth.docs.AssertingSpan;

class TraceStepExecutionListener implements StepExecutionListener {

	private final Tracer tracer;

	private static final Map<StepExecution, SpanAndScope> SPANS = new ConcurrentHashMap<>();

	TraceStepExecutionListener(Tracer tracer) {
		this.tracer = tracer;
	}

	@Override
	public void beforeStep(StepExecution stepExecution) {
		Span span = SleuthBatchSpan.BATCH_STEP_SPAN.wrap(this.tracer.nextSpan()).name(stepExecution.getStepName());
		Tracer.SpanInScope spanInScope = this.tracer.withSpan(span.start());
		SPANS.put(stepExecution, new SpanAndScope(span, spanInScope));
	}

	@Override
	public ExitStatus afterStep(StepExecution stepExecution) {
		SpanAndScope spanAndScope = SPANS.remove(stepExecution);
		List<Throwable> throwables = stepExecution.getFailureExceptions();
		// @formatter:off
		AssertingSpan span = SleuthBatchSpan.BATCH_STEP_SPAN.wrap(spanAndScope.getSpan())
		.tag(SleuthBatchSpan.StepTags.STEP_NAME, stepExecution.getStepName())
		.tag(SleuthBatchSpan.StepTags.JOB_EXECUTION_ID, String.valueOf(stepExecution.getJobExecutionId()))
		.tag(SleuthBatchSpan.StepTags.STEP_EXECUTION_ID, String.valueOf(stepExecution.getId()))
		.tag(SleuthBatchSpan.StepTags.STEP_TYPE, stepExecution.getExecutionContext().getString(Step.STEP_TYPE_KEY));
		// @formatter:on
		Tracer.SpanInScope scope = spanAndScope.getScope();
		if (!throwables.isEmpty()) {
			span.error(mergedThrowables(throwables));
		}
		span.end();
		scope.close();
		return stepExecution.getExitStatus();
	}

	private IllegalStateException mergedThrowables(List<Throwable> throwables) {
		return new IllegalStateException(
				throwables.stream().map(Throwable::toString).collect(Collectors.joining("\n")));
	}

}
