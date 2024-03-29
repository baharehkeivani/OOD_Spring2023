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

package org.springframework.cloud.sleuth.brave.instrument.web;

import java.util.regex.Pattern;

import brave.http.HttpRequest;
import brave.sampler.SamplerFunction;

/**
 * Doesn't sample a span if skip pattern is matched.
 *
 * @author Marcin Grzejszczak
 * @since 2.0.0
 */
abstract class SkipPatternSampler implements SamplerFunction<HttpRequest> {

	private Pattern pattern;

	@Override
	public final Boolean trySample(HttpRequest request) {
		String url = request.path();
		if (url == null) {
			return null;
		}

		boolean shouldSkip = pattern().matcher(url).matches();
		if (shouldSkip) {
			return false;
		}
		return null;
	}

	abstract Pattern getPattern();

	private Pattern pattern() {
		if (this.pattern == null) {
			this.pattern = getPattern();
		}
		return this.pattern;
	}

}
