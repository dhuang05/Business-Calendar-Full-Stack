/**
 * copyright © Nusino Technologies Inc, 2021, All rights reserved.
 * dhuang05@gmail.com
 */
package com.nusino.microservices.service.buscalendar.expr.stdexpr.func;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.RegexPatternTypeFilter;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.regex.Pattern;

@Component
public class AddOnPackagesScanner {
    @Value("${addon.function.packages: }")
    private String addOnPackages;

    public void addOnAllFunctions() {
        if (addOnPackages != null) {
            String[] packagzes = addOnPackages.split("[;]{1,}[\\s]{0,}");
            for (String packagze : packagzes) {
                packagze = packagze.trim();
                if (packagze.isEmpty()) {
                    continue;
                }
                final ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(false);
                provider.addIncludeFilter(new RegexPatternTypeFilter(Pattern.compile(".*")));
                final Set<BeanDefinition> classes = provider.findCandidateComponents(packagze);
                for (BeanDefinition bean : classes) {
                    try {
                        Class clazz = Class.forName(bean.getBeanClassName());
                        if (AddOnExprHandler.class.isAssignableFrom(clazz)) {
                            AddOnFuncExprInterpretor.addOn((AddOnExprHandler) clazz.getDeclaredConstructor().newInstance());
                        }
                    } catch (Exception ex) {
                        //
                    }
                }
            }

        }
    }

}
