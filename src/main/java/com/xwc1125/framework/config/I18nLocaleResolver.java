package com.xwc1125.framework.config;

import com.xwc1125.common.util.string.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.LocaleResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Locale;

/**
 * @Description:
 * @Author: xwc1125
 * @Date: 2019-04-18 18:51
 * @Copyright Copyright@2019
 */
@Slf4j
public class I18nLocaleResolver implements LocaleResolver {

    private static final String I18N_LANGUAGE = "lang";
    private static final String I18N_LANGUAGE_SESSION = "i18n_language_session";
    private static final String LANGUAGE_ZH_HANS = "zh-Hans";
    private static final String LANGUAGE_ZH_HANT = "zh-Hant";

    @Override
    public Locale resolveLocale(HttpServletRequest httpServletRequest) {
        String i18n_language = httpServletRequest.getHeader(I18N_LANGUAGE);
        if (StringUtils.isEmpty(i18n_language)) {
            i18n_language = httpServletRequest.getParameter(I18N_LANGUAGE);
        }
        Locale locale = Locale.getDefault();
        if (StringUtils.isNotEmpty(i18n_language)) {
            if (LANGUAGE_ZH_HANS.equals(i18n_language)) {
                i18n_language = "zh";
            } else if (LANGUAGE_ZH_HANT.equals(i18n_language)) {
                i18n_language = "zh_TW";
            }
            locale = new Locale(i18n_language);

            //将国际化语言保存到session
            HttpSession session = httpServletRequest.getSession();
            session.setAttribute(I18N_LANGUAGE_SESSION, locale);
        } else {
            //如果没有带国际化参数，则判断session有没有保存，有保存，则使用保存的，也就是之前设置的，避免之后的请求不带国际化参数造成语言显示不对
            HttpSession session = httpServletRequest.getSession();
            Locale localeInSession = (Locale) session.getAttribute(I18N_LANGUAGE_SESSION);
            if (localeInSession != null) {
                locale = localeInSession;
            }
        }
        return locale;
    }

    @Override
    public void setLocale(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Locale locale) {

    }
}
