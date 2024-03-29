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

package org.springframework.cloud.sleuth.instrument.annotation;

import org.aopalliance.intercept.MethodInvocation;

import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.cloud.sleuth.annotation.ContinueSpan;
import org.springframework.cloud.sleuth.annotation.NewSpan;
import org.springframework.util.StringUtils;

/**
 * Method Invocation processor for non reactor apps.
 *
 * @author Marcin Grzejszczak
 * @since 2.1.0
 */
public class NonReactorSleuthMethodInvocationProcessor extends AbstractSleuthMethodInvocationProcessor {

	@Override
	public Object process(MethodInvocation invocation, NewSpan newSpan, ContinueSpan continueSpan) throws Throwable {
		return proceedUnderSynchronousSpan(invocation, newSpan, continueSpan);
	}

	private Object proceedUnderSynchronousSpan(MethodInvocation invocation, NewSpan newSpan, ContinueSpan continueSpan)
			throws Throwable {
		Span span = tracer().currentSpan();
		// in case of @ContinueSpan and no span in tracer we start new span and should
		// close it on completion
		boolean startNewSpan = newSpan != null || span == null;
		if (startNewSpan) {
			span = SleuthAnnotationSpan.ANNOTATION_NEW_OR_CONTINUE_SPAN.wrap(tracer().nextSpan());
			newSpanParser().parse(invocation, newSpan, span);
			span.start();
		}
		String log = log(continueSpan);
		boolean hasLog = StringUtils.hasText(log);
		try (Tracer.SpanInScope scope = tracer().withSpan(span)) {
			before(invocation, span, log, hasLog);
			return invocation.proceed();
		}
		catch (Exception ex) {
			onFailure(span, log, hasLog, ex);
			throw ex;
		}
		finally {
			after(span, startNewSpan, log, hasLog);
		}
	}

}
