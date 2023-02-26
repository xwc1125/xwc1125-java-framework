package com.xwc1125.framework.autoconfigure.uuid.service.impl;

import com.xwc1125.framework.autoconfigure.uuid.UuidProperties;
import com.xwc1125.framework.autoconfigure.uuid.service.UuidService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Description:
 * @Author: xwc1125
 * @Date: 2019-02-21 11:31
 * @Copyright Copyright@2019
 */
@Component
public class UuidServiceImpl implements UuidService {
    @Autowired
    private UuidProperties uuidProperties;

    @Override
    public long generateUuid() {
        return uuidProperties.generateUuid();
    }
}
