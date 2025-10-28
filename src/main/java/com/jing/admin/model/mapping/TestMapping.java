package com.jing.admin.model.mapping;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.UUID;

//@Mapper(imports = {UUID.class, System.class})
public interface TestMapping {
    TestMapping INSTANCE = Mappers.getMapper(TestMapping.class);

    Object toEntity(Object knowledgeFile);
}
