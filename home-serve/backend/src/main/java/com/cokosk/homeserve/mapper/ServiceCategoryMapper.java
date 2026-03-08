package com.cokosk.homeserve.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cokosk.homeserve.entity.ServiceCategory;
import org.apache.ibatis.annotations.Mapper;

/**
 * 服务分类Mapper
 */
@Mapper
public interface ServiceCategoryMapper extends BaseMapper<ServiceCategory> {
}