package com.xwc1125.framework.dataMasking;

import com.xwc1125.common.util.json.JSON;
import org.junit.Test;

/**
 * @Description:
 * @Author: xwc1125
 * @Date: 2022/8/28 12:26
 * @Copyright Copyright@2022
 */
public class UserTest {

    @Test
    public void marshalJson() {
        User user = new User();
        user.setId(1l);
        user.setName("小明");
        user.setAge(10);
        user.setEmail("xiaoming@qq.com");

        try {
            String jsonStr = JSON.marshal(user);
            System.out.println(jsonStr);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
