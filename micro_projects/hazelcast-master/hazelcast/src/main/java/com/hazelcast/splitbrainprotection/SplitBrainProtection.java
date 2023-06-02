/*
 * Copyright (c) 2008-2023, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hazelcast.splitbrainprotection;

/**
 * {@link SplitBrainProtection} provides access to the current status of a split brain protection.
 */
public interface SplitBrainProtection {
    /**
     * Returns true if the minimum cluster size is satisfied, otherwise false.
     *
     * @return boolean whether the minimum cluster size property is satisfied
     */
    boolean hasMinimumSize();

}