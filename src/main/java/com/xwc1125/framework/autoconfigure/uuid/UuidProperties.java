package com.xwc1125.framework.autoconfigure.uuid;

import com.xwc1125.common.util.uuid.IdWorker;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @Description: Uuid属性配置类
 * @Author: xwc1125
 * @Date: 2019-02-21 11:21
 * @Copyright Copyright@2019
 */
@Data
@ConfigurationProperties(prefix = "xwc1125.uuid")
public class UuidProperties {

    private long workerId = 0L;
    private long datacenterId = 0L;
    private long sequence = 0L;
    private long idepoch = 1344322705519L;

    private static IdWorker idWorker;

    /**
     * Description: 生成Uuid
     * </p>
     *
     * @param
     * @return long
     * @Author: xwc1125
     * @Date: 2019-02-21 11:35:16
     */
    public long generateUuid() {
        if (idWorker == null) {
            idWorker = new IdWorker(workerId, datacenterId, sequence, idepoch);
        }
        return idWorker.getId();
    }
}
