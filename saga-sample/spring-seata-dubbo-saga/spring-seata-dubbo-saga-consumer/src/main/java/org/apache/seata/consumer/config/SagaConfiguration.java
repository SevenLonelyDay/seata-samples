/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.seata.consumer.config;

import org.apache.seata.saga.engine.StateMachineConfig;
import org.apache.seata.saga.engine.StateMachineEngine;
import org.apache.seata.saga.engine.config.DbStateMachineConfig;
import org.apache.seata.saga.engine.impl.ProcessCtrlStateMachineEngine;
import org.apache.seata.saga.rm.StateMachineEngineHolder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import javax.sql.DataSource;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author wangte
 * Create At 2024/1/20
 */
@Configuration
public class SagaConfiguration {

    @Bean
    @DependsOn({"dataSource", "dataSourceInitializer"})
    public StateMachineConfig stateMachineConfig(DataSource dataSource) {
        DbStateMachineConfig dbStateMachineConfig = new DbStateMachineConfig();
        dbStateMachineConfig.setApplicationId("seata-saga-consumer");
        dbStateMachineConfig.setDataSource(dataSource);
        dbStateMachineConfig.setEnableAsync(true);
        dbStateMachineConfig.setResources(new String[]{"classpath*:statelang/*.json"});
        dbStateMachineConfig.setTxServiceGroup("my_test_tx_group");
        dbStateMachineConfig.setThreadPoolExecutor(new ThreadPoolExecutor(1, 20, 60, TimeUnit.SECONDS, new ArrayBlockingQueue<>(100)));
        return dbStateMachineConfig;
    }

    @Bean
    public StateMachineEngine stateMachineEngine(StateMachineConfig stateMachineConfig) {
        ProcessCtrlStateMachineEngine processCtrlStateMachineEngine = new ProcessCtrlStateMachineEngine();
        processCtrlStateMachineEngine.setStateMachineConfig(stateMachineConfig);
        return processCtrlStateMachineEngine;
    }

    @Bean
    public StateMachineEngineHolder stateMachineEngineHolder(StateMachineEngine stateMachineEngine) {
        StateMachineEngineHolder stateMachineEngineHolder = new StateMachineEngineHolder();
        StateMachineEngineHolder.setStateMachineEngine(stateMachineEngine);
        return stateMachineEngineHolder;
    }
}
