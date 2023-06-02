package org.jboss.resteasy.reactive.common.model;

import java.util.function.Function;

import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.ext.ReaderInterceptor;
import jakarta.ws.rs.ext.WriterInterceptor;

import org.jboss.resteasy.reactive.spi.BeanFactory;

public class ResourceInterceptors {

    private InterceptorContainer<ContainerResponseFilter> containerResponseFilters = new InterceptorContainer.Reversed<>();
    private PreMatchInterceptorContainer<ContainerRequestFilter> containerRequestFilters = new PreMatchInterceptorContainer<>();
    private InterceptorContainer<WriterInterceptor> writerInterceptors = new InterceptorContainer<>();
    private InterceptorContainer<ReaderInterceptor> readerInterceptors = new InterceptorContainer<>();

    public InterceptorContainer<ContainerResponseFilter> getContainerResponseFilters() {
        return containerResponseFilters;
    }

    public ResourceInterceptors setContainerResponseFilters(
            InterceptorContainer<ContainerResponseFilter> containerResponseFilters) {
        this.containerResponseFilters = containerResponseFilters;
        return this;
    }

    public PreMatchInterceptorContainer<ContainerRequestFilter> getContainerRequestFilters() {
        return containerRequestFilters;
    }

    public ResourceInterceptors setContainerRequestFilters(
            PreMatchInterceptorContainer<ContainerRequestFilter> containerRequestFilters) {
        this.containerRequestFilters = containerRequestFilters;
        return this;
    }

    public InterceptorContainer<WriterInterceptor> getWriterInterceptors() {
        return writerInterceptors;
    }

    public ResourceInterceptors setWriterInterceptors(InterceptorContainer<WriterInterceptor> writerInterceptors) {
        this.writerInterceptors = writerInterceptors;
        return this;
    }

    public InterceptorContainer<ReaderInterceptor> getReaderInterceptors() {
        return readerInterceptors;
    }

    public ResourceInterceptors setReaderInterceptors(InterceptorContainer<ReaderInterceptor> readerInterceptors) {
        this.readerInterceptors = readerInterceptors;
        return this;
    }

    public void initializeDefaultFactories(Function<String, BeanFactory<?>> factoryCreator) {
        containerRequestFilters.initializeDefaultFactories(factoryCreator);
        containerResponseFilters.initializeDefaultFactories(factoryCreator);
        readerInterceptors.initializeDefaultFactories(factoryCreator);
        writerInterceptors.initializeDefaultFactories(factoryCreator);
    }

    // we sort this at build time as the order of the elements in the lists is retained in generated bytecode
    // therefore at runtime the elements are already properly sorted
    public ResourceInterceptors sort() {
        containerRequestFilters.sort();
        containerResponseFilters.sort();
        writerInterceptors.sort();
        readerInterceptors.sort();
        return this;
    }

    public void visitFilters(FiltersVisitor visitor) {
        for (var f : containerRequestFilters.getPreMatchInterceptors()) {
            var visitResult = visitor.visitPreMatchRequestFilter(f);
            if (visitResult == FiltersVisitor.VisitResult.ABORT) {
                return;
            }
        }
        for (var f : containerRequestFilters.getGlobalResourceInterceptors()) {
            var visitResult = visitor.visitGlobalRequestFilter(f);
            if (visitResult == FiltersVisitor.VisitResult.ABORT) {
                return;
            }

        }
        for (var f : containerRequestFilters.getNameResourceInterceptors()) {
            var visitResult = visitor.visitNamedRequestFilter(f);
            if (visitResult == FiltersVisitor.VisitResult.ABORT) {
                return;
            }
        }

        for (var f : containerResponseFilters.getGlobalResourceInterceptors()) {
            var visitResult = visitor.visitGlobalResponseFilter(f);
            if (visitResult == FiltersVisitor.VisitResult.ABORT) {
                return;
            }
        }
        for (var f : containerResponseFilters.getNameResourceInterceptors()) {
            var visitResult = visitor.visitNamedResponseFilter(f);
            if (visitResult == FiltersVisitor.VisitResult.ABORT) {
                return;
            }
        }
    }

    public interface FiltersVisitor {

        VisitResult visitPreMatchRequestFilter(ResourceInterceptor<ContainerRequestFilter> interceptor);

        VisitResult visitGlobalRequestFilter(ResourceInterceptor<ContainerRequestFilter> interceptor);

        VisitResult visitNamedRequestFilter(ResourceInterceptor<ContainerRequestFilter> interceptor);

        VisitResult visitGlobalResponseFilter(ResourceInterceptor<ContainerResponseFilter> interceptor);

        VisitResult visitNamedResponseFilter(ResourceInterceptor<ContainerResponseFilter> interceptor);

        enum VisitResult {
            CONTINUE,
            ABORT
        }
    }

}