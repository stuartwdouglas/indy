/*******************************************************************************
 * Copyright 2011 John Casey
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package org.commonjava.aprox.change.event;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import org.commonjava.aprox.model.StoreType;

public class ProxyManagerDeleteEvent
    implements Iterable<String>, AproxEvent
{

    private final StoreType type;

    private final Collection<String> names;

    public ProxyManagerDeleteEvent( final StoreType type, final Collection<String> names )
    {
        this.type = type;
        this.names = Collections.unmodifiableCollection( names );
    }

    public ProxyManagerDeleteEvent( final StoreType type, final String... names )
    {
        this.names = Collections.unmodifiableCollection( Arrays.asList( names ) );
        this.type = type;
    }

    public StoreType getType()
    {
        return type;
    }

    @Override
    public Iterator<String> iterator()
    {
        return names.iterator();
    }

    public Collection<String> getNames()
    {
        return names;
    }

    @Override
    public String toString()
    {
        return String.format( "ProxyManagerDeleteEvent [type=%s, names=%s]", type, names );
    }

}
