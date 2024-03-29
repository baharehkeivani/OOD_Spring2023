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

import java.io.IOException;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import brave.Span;
import brave.Tracer;
import brave.handler.MutableSpan;
import brave.handler.SpanHandler;
import brave.sampler.Sampler;
import brave.test.TestSpanHandler;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.web.server.ManagementServerProperties;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.sleuth.CurrentTraceContext;
import org.springframework.cloud.sleuth.autoconfig.instrument.web.SleuthWebProperties;
import org.springframework.cloud.sleuth.http.HttpServerHandler;
import org.springframework.cloud.sleuth.instrument.web.servlet.TracingFilter;
import org.springframework.cloud.sleuth.util.SpanUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.filter.GenericFilterBean;
import org.springframework.web.util.NestedServletException;

import static org.assertj.core.api.Assertions.fail;
import static org.assertj.core.api.BDDAssertions.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = TraceFilterIntegrationTests.Config.class)
public class TraceFilterIntegrationTests extends AbstractMvcIntegrationTest {

	static final String MVC_CONTROLLER_CLASS_KEY = "mvc.controller.class";
	static final String MVC_CONTROLLER_METHOD_KEY = "mvc.controller.method";

	private static Span span;

	@Autowired
	CurrentTraceContext currentTraceContext;

	@Autowired
	HttpServerHandler httpServerHandler;

	@Autowired
	MyFilter myFilter;

	@Autowired
	TestSpanHandler spans;

	@Autowired
	Tracer tracer;

	@BeforeEach
	@AfterEach
	public void clearSpans() {
		this.spans.clear();
	}

	@Test
	public void should_create_a_trace() throws Exception {
		whenSentPingWithoutTracingData();

		then(this.spans).hasSize(1);
		MutableSpan span = this.spans.get(0);
		then(span.tags()).containsKey(MVC_CONTROLLER_CLASS_KEY).containsKey(MVC_CONTROLLER_METHOD_KEY);
		then(this.tracer.currentSpan()).isNull();
	}

	@Test
	public void should_ignore_sampling_the_span_if_uri_matches_management_properties_context_path() throws Exception {
		MvcResult mvcResult = whenSentInfoWithTraceId(new Random().nextLong());

		// https://github.com/spring-cloud/spring-cloud-sleuth/issues/327
		// we don't want to respond with any tracing data
		then(notSampledHeaderIsPresent(mvcResult)).isEqualTo(false);
		then(this.spans).isEmpty();
		then(this.tracer.currentSpan()).isNull();
	}

	@Test
	public void when_traceId_is_sent_should_not_create_a_new_one_but_return_the_existing_one_instead()
			throws Exception {
		Long expectedTraceId = new Random().nextLong();

		whenSentPingWithTraceId(expectedTraceId);

		then(this.spans).hasSize(1);
		then(this.tracer.currentSpan()).isNull();

	}

	@Test
	public void when_message_is_sent_should_eventually_clear_mdc() throws Exception {
		Long expectedTraceId = new Random().nextLong();

		whenSentPingWithTraceId(expectedTraceId);

		then(MDC.getCopyOfContextMap()).isEmpty();
		then(this.spans).hasSize(1);
		then(this.tracer.currentSpan()).isNull();
	}

	@Test
	public void when_traceId_is_sent_to_async_endpoint_span_is_joined() throws Exception {
		Long expectedTraceId = new Random().nextLong();

		MvcResult mvcResult = whenSentFutureWithTraceId(expectedTraceId);
		this.mockMvc.perform(asyncDispatch(mvcResult)).andExpect(status().isOk()).andReturn();

		then(this.tracer.currentSpan()).isNull();
	}

	@Test
	public void should_add_a_custom_tag_to_the_span_created_in_controller() throws Exception {
		Long expectedTraceId = new Random().nextLong();

		MvcResult mvcResult = whenSentDeferredWithTraceId(expectedTraceId);
		this.mockMvc.perform(asyncDispatch(mvcResult)).andExpect(status().isOk()).andReturn();

		Optional<MutableSpan> taggedSpan = this.spans.spans().stream().filter(span -> span.tags().containsKey("tag"))
				.findFirst();
		then(taggedSpan.isPresent()).isTrue();
		then(taggedSpan.get().tags()).containsEntry("tag", "value")
				.containsEntry("mvc.controller.method", "deferredMethod")
				.containsEntry("mvc.controller.class", "TestController");
		then(this.tracer.currentSpan()).isNull();
	}

	@Test
	public void should_log_tracing_information_when_404_exception_was_thrown() throws Exception {
		Long expectedTraceId = new Random().nextLong();

		whenSentToNonExistentEndpointWithTraceId(expectedTraceId);

		// it's a span with the same ids
		then(this.spans).hasSize(1);
		MutableSpan serverSpan = this.spans.get(0);
		then(serverSpan.tags()).containsEntry("custom", "tag").containsEntry("http.status_code", "404");
		then(this.tracer.currentSpan()).isNull();
	}

	@Test
	public void should_log_tracing_information_when_500_exception_was_thrown() throws Exception {
		Long expectedTraceId = new Random().nextLong();

		try {
			whenSentToExceptionThrowingEndpoint(expectedTraceId);
			fail("Should fail");
		}
		catch (NestedServletException e) {
			then(e).hasRootCauseInstanceOf(RuntimeException.class);
		}

		// we need to dump the span cause it's not in TracingFilter since TF
		// has also error dispatch and the ErrorController would report the span
		then(this.spans).hasSize(1);
		then(this.spans.get(0).error())
				.hasMessageContaining("Request processing failed; nested exception is java.lang.RuntimeException");
	}

	@Test
	public void should_assume_that_a_request_without_span_and_with_trace_is_a_root_span() throws Exception {
		Long expectedTraceId = new Random().nextLong();

		whenSentRequestWithTraceIdAndNoSpanId(expectedTraceId);
		whenSentRequestWithTraceIdAndNoSpanId(expectedTraceId);

		then(this.spans.spans().stream().filter(span -> span.id().equals(span.traceId())).findAny().isPresent())
				.as("a root span exists").isTrue();
		then(this.tracer.currentSpan()).isNull();
	}

	@Test
	public void should_return_custom_response_headers_when_custom_trace_filter_gets_registered() throws Exception {
		Long expectedTraceId = new Random().nextLong();

		MvcResult mvcResult = whenSentPingWithTraceId(expectedTraceId);

		then(mvcResult.getResponse().getHeader("ZIPKIN-TRACE-ID")).isEqualTo(SpanUtil.idToHex(expectedTraceId));
		then(this.spans).hasSize(1);
		then(this.spans.get(0).tags()).containsEntry("custom", "tag");
	}

	@Override
	protected void configureMockMvcBuilder(DefaultMockMvcBuilder mockMvcBuilder) {
		mockMvcBuilder.addFilters(TracingFilter.create(this.currentTraceContext, this.httpServerHandler),
				this.myFilter);
	}

	private MvcResult whenSentPingWithoutTracingData() throws Exception {
		return this.mockMvc.perform(MockMvcRequestBuilders.get("/ping").accept(MediaType.TEXT_PLAIN)).andReturn();
	}

	private MvcResult whenSentPingWithTraceId(Long passedTraceId) throws Exception {
		return sendPingWithTraceId(passedTraceId);
	}

	private MvcResult whenSentInfoWithTraceId(Long passedTraceId) throws Exception {
		return sendRequestWithTraceId("/additionalContextPath/info", passedTraceId);
	}

	private MvcResult whenSentFutureWithTraceId(Long passedTraceId) throws Exception {
		return sendRequestWithTraceId("/future", passedTraceId);
	}

	private MvcResult whenSentDeferredWithTraceId(Long passedTraceId) throws Exception {
		return sendDeferredWithTraceId(passedTraceId);
	}

	private MvcResult whenSentToNonExistentEndpointWithTraceId(Long passedTraceId) throws Exception {
		return sendRequestWithTraceId("/exception/nonExistent", passedTraceId, HttpStatus.NOT_FOUND);
	}

	private MvcResult whenSentToExceptionThrowingEndpoint(Long passedTraceId) throws Exception {
		return sendRequestWithTraceId("/throwsException", passedTraceId, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	private MvcResult sendPingWithTraceId(Long traceId) throws Exception {
		return sendRequestWithTraceId("/ping", traceId);
	}

	private MvcResult sendDeferredWithTraceId(Long traceId) throws Exception {
		return sendRequestWithTraceId("/deferred", traceId);
	}

	private MvcResult sendRequestWithTraceId(String path, Long traceId) throws Exception {
		return this.mockMvc.perform(MockMvcRequestBuilders.get(path).accept(MediaType.TEXT_PLAIN).header("b3",
				SpanUtil.idToHex(traceId) + "-" + SpanUtil.idToHex(new Random().nextLong()))).andReturn();
	}

	private MvcResult whenSentRequestWithTraceIdAndNoSpanId(Long traceId) throws Exception {
		return this.mockMvc.perform(MockMvcRequestBuilders.get("/ping").accept(MediaType.TEXT_PLAIN).header("b3",
				SpanUtil.idToHex(traceId))).andReturn();
	}

	private MvcResult sendRequestWithTraceId(String path, Long traceId, HttpStatus status) throws Exception {
		return this.mockMvc
				.perform(MockMvcRequestBuilders.get(path).accept(MediaType.TEXT_PLAIN).header("b3",
						SpanUtil.idToHex(traceId) + "-" + SpanUtil.idToHex(new Random().nextLong())))
				.andExpect(status().is(status.value())).andReturn();
	}

	private boolean notSampledHeaderIsPresent(MvcResult mvcResult) {
		return "0".equals(mvcResult.getResponse().getHeader("b3"));
	}

	@EnableAutoConfiguration
	@Configuration(proxyBeanMethods = false)
	protected static class Config {

		private static final Log log = LogFactory.getLog(Config.class);

		@Bean
		SpanHandler testSpanHandler() {
			return new TestSpanHandler();
		}

		@Bean
		Sampler alwaysSampler() {
			return Sampler.ALWAYS_SAMPLE;
		}

		@Bean
		@Order(SleuthWebProperties.TRACING_FILTER_ORDER + 1)
		Filter myFilter(Tracer tracer) {
			return new MyFilter(tracer);
		}

		@RestController
		public static class TestController {

			@Autowired
			private Tracer tracer;

			@RequestMapping("/ping")
			public String ping() {
				log.info("ping");
				span = this.tracer.currentSpan();
				return "ping";
			}

			@RequestMapping("/throwsException")
			public void throwsException() {
				throw new RuntimeException();
			}

			@RequestMapping("/deferred")
			public DeferredResult<String> deferredMethod() {
				log.info("deferred");
				span = this.tracer.currentSpan();
				span.tag("tag", "value");
				DeferredResult<String> result = new DeferredResult<>();
				result.setResult("deferred");
				return result;
			}

			@RequestMapping("/future")
			public CompletableFuture<String> future() {
				log.info("future");
				return CompletableFuture.completedFuture("ping");
			}

		}

		@Configuration(proxyBeanMethods = false)
		static class ManagementServer {

			@Bean
			@Primary
			ManagementServerProperties managementServerProperties() {
				ManagementServerProperties managementServerProperties = new ManagementServerProperties();
				managementServerProperties.setBasePath("/additionalContextPath");
				return managementServerProperties;
			}

		}

	}

}

// tag::response_headers[]
@Component
@Order(SleuthWebProperties.TRACING_FILTER_ORDER + 1)
class MyFilter extends GenericFilterBean {

	private final Tracer tracer;

	MyFilter(Tracer tracer) {
		this.tracer = tracer;
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		Span currentSpan = this.tracer.currentSpan();
		if (currentSpan == null) {
			chain.doFilter(request, response);
			return;
		}
		// for readability we're returning trace id in a hex form
		((HttpServletResponse) response).addHeader("ZIPKIN-TRACE-ID", currentSpan.context().traceIdString());
		// we can also add some custom tags
		currentSpan.tag("custom", "tag");
		chain.doFilter(request, response);
	}

}
// end::response_headers[]
