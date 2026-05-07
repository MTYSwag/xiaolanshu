package com.smart.community.gateway.config;

import com.alibaba.csp.sentinel.adapter.gateway.common.rule.GatewayFlowRule;
import com.alibaba.csp.sentinel.adapter.gateway.common.rule.GatewayRuleManager;
import com.alibaba.csp.sentinel.datasource.ReadableDataSource;
import com.alibaba.csp.sentinel.datasource.nacos.NacosDataSource;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class SentinelNacosDataSourceRunner implements CommandLineRunner {

    @Override
    public void run(String... args) {
        String serverAddr = "localhost:8848";
        String groupId = "DEFAULT_GROUP";
        String dataId = "gateway-gw-flow-rules";

        ReadableDataSource<String, Set<GatewayFlowRule>> dataSource = new NacosDataSource<>(
                serverAddr, groupId, dataId,
                source -> {
                    try {
                        ObjectMapper mapper = new ObjectMapper();
                        List<GatewayFlowRule> list = mapper.readValue(
                                source,
                                mapper.getTypeFactory().constructCollectionType(List.class, GatewayFlowRule.class));
                        return new HashSet<>(list);
                    } catch (Exception e) {
                        e.printStackTrace();
                        return new HashSet<>();
                    }
                }
        );

        GatewayRuleManager.register2Property(dataSource.getProperty());
        System.out.println("=== Sentinel Nacos DataSource Registered Successfully ===");
        System.out.println("Current rules: " + GatewayRuleManager.getRules());
    }
}