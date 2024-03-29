package io.quarkus.qute;

import java.util.Map;
import java.util.concurrent.CompletionStage;

/**
 * Defines the logic of a section node.
 *
 * @see SectionHelperFactory
 */
@FunctionalInterface
public interface SectionHelper {

    /**
     *
     * @param context
     * @return the result node
     */
    CompletionStage<ResultNode> resolve(SectionResolutionContext context);

    /**
     * A new instance is created for every invocation of {@link SectionHelper#resolve(SectionResolutionContext)}.
     */
    public interface SectionResolutionContext {

        /**
         *
         * @return the current resolution context
         */
        ResolutionContext resolutionContext();

        /**
         *
         * @param data
         * @param extendingBlocks
         * @return a new resolution context
         */
        ResolutionContext newResolutionContext(Object data, Map<String, SectionBlock> extendingBlocks);

        /**
         * Execute the main block with the current resolution context.
         *
         * @return the result node
         */
        default CompletionStage<ResultNode> execute() {
            return execute(null, resolutionContext());
        }

        /**
         * Execute the main block with the specified {@link ResolutionContext}.
         *
         * @param context
         * @return the result node
         */
        default CompletionStage<ResultNode> execute(ResolutionContext context) {
            return execute(null, context);
        }

        /**
         * Execute the specified block with the specified {@link ResolutionContext}.
         *
         * @param block
         * @param context
         * @return the result node
         */
        CompletionStage<ResultNode> execute(SectionBlock block, ResolutionContext context);

        /**
         * Parameters for a specific resolution.
         *
         * @return the immutable map of parameters, never {@code null}
         */
        Map<String, Object> getParameters();

    }

}
