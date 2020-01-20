/**
 * Copyright (C) 2011-2020 Red Hat, Inc. (https://github.com/Commonjava/indy)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.commonjava.indy.subsys.honeycomb.interceptor;

import io.honeycomb.beeline.tracing.Span;
import org.commonjava.indy.measure.annotation.Measure;
import org.commonjava.indy.subsys.honeycomb.HoneycombManager;
import org.commonjava.indy.subsys.honeycomb.config.HoneycombConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import java.lang.reflect.Method;
import java.util.stream.Stream;

import static org.commonjava.indy.metrics.IndyMetricsConstants.getDefaultName;
import static org.commonjava.indy.metrics.RequestContextHelper.CONTENT_TRACKING_ID;
import static org.commonjava.indy.metrics.RequestContextHelper.HTTP_METHOD;
import static org.commonjava.indy.metrics.RequestContextHelper.HTTP_STATUS;
import static org.commonjava.indy.metrics.RequestContextHelper.PREFERRED_ID;
import static org.commonjava.indy.metrics.RequestContextHelper.X_FORWARDED_FOR;
import static org.commonjava.indy.metrics.RequestContextHelper.getContext;

@Interceptor
@Measure
public class HoneycombInterceptor
{
    private final Logger logger = LoggerFactory.getLogger( getClass() );

    @Inject
    private HoneycombConfiguration config;

    @Inject
    private HoneycombManager honeycombManager;

    @AroundInvoke
    public Object operation( InvocationContext context ) throws Exception
    {
        if ( !config.isEnabled() )
        {
            return context.proceed();
        }

        Method method = context.getMethod();
        Measure measure = method.getAnnotation( Measure.class );
        if ( measure == null )
        {
            measure = method.getDeclaringClass().getAnnotation( Measure.class );
        }

        if ( measure == null )
        {
            return context.proceed();
        }

        Class<?> cls = context.getMethod().getDeclaringClass();

        String defaultName = getDefaultName( cls, context.getMethod().getName() );

        Span span = null;
        try
        {
            span = honeycombManager.startChildSpan( defaultName );
            if ( span != null )
            {
                span.markStart();
            }

            logger.trace( "startChildSpan, span: {}, defaultName: {}", span, defaultName );
            return context.proceed();
        }
        finally
        {
            if ( span != null )
            {
                Span theSpan = span;
                Stream.of( config.getFields()).forEach( field->{
                    Object value = getContext( field );
                    if ( value != null )
                    {
                        theSpan.addField( field, value );
                    }
                });

                logger.trace( "closeSpan, {}", span );
                span.close();
            }
        }
    }

}