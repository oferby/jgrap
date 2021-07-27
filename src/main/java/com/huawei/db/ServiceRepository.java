package com.huawei.db;

import com.huawei.model.Service;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface ServiceRepository extends Neo4jRepository<Service, Long> {

    Service getByServiceId(String serviceId);

}
