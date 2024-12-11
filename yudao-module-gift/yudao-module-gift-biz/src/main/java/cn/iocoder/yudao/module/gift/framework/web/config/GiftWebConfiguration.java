package cn.iocoder.yudao.module.gift.framework.web.config;

import cn.iocoder.yudao.framework.swagger.config.YudaoSwaggerAutoConfiguration;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * gift 模块的 web 组件的 Configuration
 *
 * @author reprejudicer2014
 */
@Configuration(proxyBeanMethods = false)
public class GiftWebConfiguration {

    /**
     * gift 模块的 API 分组
     */
    @Bean
    public GroupedOpenApi giftGroupedOpenApi() {
        return YudaoSwaggerAutoConfiguration.buildGroupedOpenApi("gift");
    }

}
