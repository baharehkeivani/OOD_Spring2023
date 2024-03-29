/*
 * Copyright 2023 Hazelcast Inc.
 *
 * Licensed under the Hazelcast Community License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://hazelcast.com/hazelcast-community-license
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hazelcast.jet.sql.impl.connector.kafka;

import com.hazelcast.jet.kafka.impl.KafkaTestSupport;
import com.hazelcast.jet.sql.SqlTestSupport;
import com.hazelcast.sql.SqlResult;
import com.hazelcast.sql.SqlService;
import org.apache.kafka.common.serialization.IntegerDeserializer;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.AfterClass;
import org.junit.BeforeClass;

import static com.hazelcast.jet.sql.impl.connector.SqlConnector.OPTION_KEY_FORMAT;
import static com.hazelcast.jet.sql.impl.connector.SqlConnector.OPTION_VALUE_FORMAT;
import static org.assertj.core.api.Assertions.assertThat;

public abstract class KafkaSqlTestSupport extends SqlTestSupport {
    protected static final KafkaTestSupport kafkaTestSupport = KafkaTestSupport.create();
    protected static SqlService sqlService;

    @BeforeClass
    public static void beforeClass() throws Exception {
        kafkaTestSupport.createKafkaCluster();
        initialize(1, null);
        sqlService = instance().getSql();
    }

    @AfterClass
    public static void afterClass() {
        kafkaTestSupport.shutdownKafkaCluster();
    }

    protected static String createRandomTopic(int partitionCount) {
        String topicName = randomName();
        kafkaTestSupport.createTopic(topicName, partitionCount);
        return topicName;
    }

    protected static void createSqlKafkaDataConnection(String dlName, boolean isShared, String options) {
        try (SqlResult result = instance().getSql().execute(
                "CREATE DATA CONNECTION " + dlName + " TYPE Kafka "
                        + (isShared ? " SHARED " : " NOT SHARED ") + options
        )) {
            assertThat(result.updateCount()).isZero();
        }

    }

    protected static void createSqlKafkaDataConnection(String dlName, boolean isShared) {
        createSqlKafkaDataConnection(dlName, isShared, isShared ? defaultSharedOptions() : defaultNotSharedOptions());
    }

    protected static void createKafkaMappingUsingDataConnection(String name, String dataConnection, String sqlOptions) {
        try (SqlResult result = instance().getSql().execute("CREATE OR REPLACE MAPPING " + name
                + " DATA CONNECTION " + quoteName(dataConnection) + "\n" + sqlOptions
        )) {
            assertThat(result.updateCount()).isZero();
        }
    }

    protected static String defaultSharedOptions() {
        return String.format("OPTIONS ( " +
                        "'bootstrap.servers' = '%s', " +
                        "'key.serializer' = '%s', " +
                        "'key.deserializer' = '%s', " +
                        "'value.serializer' = '%s', " +
                        "'value.deserializer' = '%s', " +
                        "'auto.offset.reset' = 'earliest') ",
                kafkaTestSupport.getBrokerConnectionString(),
                IntegerSerializer.class.getCanonicalName(),
                IntegerDeserializer.class.getCanonicalName(),
                StringSerializer.class.getCanonicalName(),
                StringDeserializer.class.getCanonicalName());
    }

    protected static String defaultNotSharedOptions() {
        return String.format("OPTIONS ( "
                        + "'bootstrap.servers' = '%s', "
                        + "'auto.offset.reset' = 'earliest') ",
                kafkaTestSupport.getBrokerConnectionString());
    }

    protected static String constructDataConnectionOptions(
            Class<?> keySerializerClazz,
            Class<?> keyDeserializerClazz,
            Class<?> valueSerializerClazz,
            Class<?> valueDeserializerClazz) {
        return String.format("OPTIONS ( " +
                        "'bootstrap.servers' = '%s', " +
                        "'key.serializer' = '%s', " +
                        "'key.deserializer' = '%s', " +
                        "'value.serializer' = '%s', " +
                        "'value.deserializer' = '%s', " +
                        "'auto.offset.reset' = 'earliest') ",
                kafkaTestSupport.getBrokerConnectionString(),
                keySerializerClazz.getCanonicalName(),
                keyDeserializerClazz.getCanonicalName(),
                valueSerializerClazz.getCanonicalName(),
                valueDeserializerClazz.getCanonicalName());
    }

    protected static String constructMappingOptions(String keyFormat, String valueFormat) {
        return "OPTIONS ('" + OPTION_KEY_FORMAT + "'='" + keyFormat + "'"
                + ", '" + OPTION_VALUE_FORMAT + "'='" + valueFormat + "')";
    }
}
