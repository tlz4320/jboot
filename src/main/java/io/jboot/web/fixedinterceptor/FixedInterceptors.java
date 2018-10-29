/**
 * Copyright (c) 2015-2018, Michael Yang 杨福海 (fuhai999@gmail.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.jboot.web.fixedinterceptor;

import io.jboot.Jboot;
import io.jboot.component.jwt.JwtInterceptor;
import io.jboot.component.metric.JbootMetricInterceptor;
import io.jboot.component.opentracing.OpentracingInterceptor;
import io.jboot.component.shiro.JbootShiroInterceptor;
import io.jboot.web.controller.validate.ParaValidateInterceptor;
import io.jboot.web.cors.CORSInterceptor;
import io.jboot.web.limitation.LimitationInterceptor;

import java.util.*;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Package io.jboot.web.fixedinterceptor
 */
public class FixedInterceptors {

    private static final FixedInterceptors me = new FixedInterceptors();

    public static FixedInterceptors me() {
        return me;
    }


    /**
     * 默认的 Jboot 系统拦截器
     */
    private FixedInterceptorWapper[] defaultInters = new FixedInterceptorWapper[]{
            new FixedInterceptorWapper(new CORSInterceptor(), 10) ,
            new FixedInterceptorWapper(new LimitationInterceptor(), 20),
            new FixedInterceptorWapper(new ParaValidateInterceptor(), 30),
            new FixedInterceptorWapper(new JwtInterceptor(), 40),
            new FixedInterceptorWapper(new JbootShiroInterceptor(), 50),
            new FixedInterceptorWapper(new OpentracingInterceptor(), 60),
            new FixedInterceptorWapper(new JbootMetricInterceptor(), 70)};

    private List<FixedInterceptorWapper> userInters = new ArrayList<>();

    private FixedInterceptor[] allInters = null;

    private List<FixedInterceptorWapper> inters;

    FixedInterceptor[] all() {
        if (allInters == null) {
            synchronized (this) {
                if (allInters == null) {
                    initInters();
                }
            }
        }
        return allInters;
    }


    private void initInters() {

        FixedInterceptor[] interceptors = new FixedInterceptor[defaultInters.length + userInters.size()];
        inters = new ArrayList<>();
        inters.addAll(Arrays.asList(defaultInters));
        inters.addAll(userInters);
        inters.sort(new Comparator<FixedInterceptorWapper>() {
            @Override
            public int compare(FixedInterceptorWapper f1, FixedInterceptorWapper f2) {
                return Integer.compare(f1.getOrderNo(), f2.getOrderNo());
            }
        });

        int i = 0;
        for (FixedInterceptorWapper interceptor : inters) {
            Jboot.injectMembers(interceptor);
            interceptors[i++] = interceptor.getFixedInterceptor();
        }

        allInters = interceptors;
    }


    public void add(FixedInterceptor interceptor) {
        userInters.add(new FixedInterceptorWapper(interceptor));
    }

    public void add(FixedInterceptor interceptor, int orderNo) {
        if (orderNo < 0) {
            orderNo = 0;
        }

        userInters.add(new FixedInterceptorWapper(interceptor, orderNo));
    }

    public List<FixedInterceptorWapper> list() {
        return inters;
    }
}
