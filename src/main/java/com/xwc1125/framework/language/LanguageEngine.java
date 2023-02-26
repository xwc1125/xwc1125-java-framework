package com.xwc1125.framework.language;

import com.xwc1125.common.util.i18n.LanguageUtils;
import com.xwc1125.framework.autoconfigure.redis.service.RedisHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @Description:
 * @Author: xwc1125
 * @Date: 2019-05-06 11:03
 * @Copyright Copyright@2019
 */
@Slf4j
@Service("frameworkLanguageEngine")
public class LanguageEngine {

    @Autowired
    protected RedisHelper redisHelper;

    /**
     * Description: 从redis里读取语言信息，存入内存
     * </p>
     *
     * @param
     * @return void
     * @Author: xwc1125
     * @Date: 2019-05-06 10:57:53
     */
    public void initLanguage() {
        try {
            Map<String, String> map = redisHelper.getRedisTemplate().opsForHash().entries(LanguageUtils.KEY_LANGUAGE_DIC);
            if (map != null && map.size() > 0) {
                LanguageUtils.init();
                for (Map.Entry<String, String> entry : map.entrySet()) {
                    String key = entry.getKey();
                    String value = entry.getValue();
                    LanguageUtils.put(key, value);
                }
                log.debug("initLanguage number :" + map.size());
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
