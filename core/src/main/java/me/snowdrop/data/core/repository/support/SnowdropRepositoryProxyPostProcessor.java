/*
 * Copyright 2017 Red Hat, Inc, and individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.snowdrop.data.core.repository.support;

import org.aopalliance.aop.Advice;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.data.repository.core.support.RepositoryProxyPostProcessor;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public class SnowdropRepositoryProxyPostProcessor implements RepositoryProxyPostProcessor {
    private static final Advice INFO_ADVICE = new MethodInfo();

    @Override
    public void postProcess(ProxyFactory factory, RepositoryInformation repositoryInformation) {
        factory.addAdvice(INFO_ADVICE);
    }

    public static final ThreadLocal<MethodInvocation> INFO = new ThreadLocal<>();

    private static class MethodInfo implements MethodInterceptor {
        @Override
        public Object invoke(@SuppressWarnings("null") MethodInvocation invocation) throws Throwable {
            INFO.set(invocation);
            try {
                return invocation.proceed();
            } finally {
                INFO.remove();
            }
        }

    }
}
