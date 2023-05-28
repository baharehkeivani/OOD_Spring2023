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

package com.hazelcast.internal.jmx;

import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelJVMTest;
import com.hazelcast.test.annotation.QuickTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import java.util.logging.Level;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@RunWith(HazelcastParallelClassRunner.class)
@Category({QuickTest.class, ParallelJVMTest.class})
public class LoggingServiceMBeanTest extends HazelcastTestSupport {

    public static final String TYPE_NAME = "HazelcastInstance.LoggingService";

    private MBeanDataHolder holder;
    private String hzName;

    @Before
    public void setUp() throws Exception {
        holder = new MBeanDataHolder(createHazelcastInstanceFactory(1));
        hzName = holder.getHz().getName();
    }

    @Test
    public void test() throws Exception {
        assertNull(holder.getMBeanAttribute(TYPE_NAME, hzName, "level"));

        holder.invokeMBeanOperation(TYPE_NAME, hzName, "setLevel", new Object[]{Level.FINEST.getName()}, null);
        assertEquals(Level.FINEST.getName(), holder.getMBeanAttribute(TYPE_NAME, hzName, "level"));

        holder.invokeMBeanOperation(TYPE_NAME, hzName, "resetLevel", null, null);
        assertNull(holder.getMBeanAttribute(TYPE_NAME, hzName, "level"));
    }

}
