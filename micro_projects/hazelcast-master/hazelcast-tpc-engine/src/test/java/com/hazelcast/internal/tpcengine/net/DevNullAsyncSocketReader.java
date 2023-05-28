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

package com.hazelcast.internal.tpcengine.net;

import java.nio.ByteBuffer;

import static com.hazelcast.internal.tpcengine.util.BufferUtil.upcast;


/**
 * A {@link AsyncSocketReader} that disposes any bytes on the receive buffer.
 */
public class DevNullAsyncSocketReader extends AsyncSocketReader {
    @Override
    public void onRead(ByteBuffer src) {
        upcast(src).position(src.limit());
    }
}
