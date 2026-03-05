package com.cokosk.homeserve.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cokosk.homeserve.entity.ServiceItem;
import org.apache.ibatis.annotations.Mapper;

/**
 * 服务项目Mapper
 */
@Mapper
public interface ServiceItemMapper extends BaseMapper<ServiceItem> {
}