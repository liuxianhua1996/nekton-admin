package com.jing.admin.model.mapping;

import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

public interface TestMapping {
    TestMapping INSTANCE = Mappers.getMapper(TestMapping.class);

    @Mapping(target = "createTime", expression = "java(System.currentTimeMillis())")
    @Mapping(target = "updateTime", expression = "java(System.currentTimeMillis())")
    Object toEntity(Object knowledgeFile);
}
