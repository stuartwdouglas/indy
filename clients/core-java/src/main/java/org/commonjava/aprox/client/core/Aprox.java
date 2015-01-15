package org.commonjava.aprox.client.core;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.commonjava.aprox.client.core.module.AproxContentClientModule;
import org.commonjava.aprox.client.core.module.AproxStoresClientModule;
import org.commonjava.aprox.model.core.io.AproxObjectMapper;
import org.commonjava.aprox.stats.AProxVersioning;

public class Aprox
{

    private final AproxClientHttp http;

    private final Set<AproxClientModule> moduleRegistry;

    public Aprox( final String baseUrl, final AproxClientModule... modules )
    {
        this( baseUrl, null, modules );
    }

    private void setupStandardModules()
    {
        final Set<AproxClientModule> standardModules = new HashSet<>();
        standardModules.add( new AproxStoresClientModule() );
        standardModules.add( new AproxContentClientModule() );

        for ( final AproxClientModule module : standardModules )
        {
            module.setup( http );
            moduleRegistry.add( module );
        }
    }

    public Aprox( final String baseUrl, final AproxObjectMapper mapper, final AproxClientModule... modules )
    {
        this.http = new AproxClientHttp( baseUrl, mapper == null ? new AproxObjectMapper( true ) : mapper );
        this.moduleRegistry = new HashSet<>();

        setupStandardModules();
        for ( final AproxClientModule module : modules )
        {
            module.setup( http );
            moduleRegistry.add( module );
        }
    }

    public Aprox( final String baseUrl, final Collection<AproxClientModule> modules )
    {
        this( baseUrl, null, modules );
    }

    public Aprox( final String baseUrl, final AproxObjectMapper mapper, final Collection<AproxClientModule> modules )
    {
        this.http = new AproxClientHttp( baseUrl, mapper == null ? new AproxObjectMapper( true ) : mapper );
        this.moduleRegistry = new HashSet<>();

        setupStandardModules();
        for ( final AproxClientModule module : modules )
        {
            module.setup( http );
            moduleRegistry.add( module );
        }
    }

    public void setupExternal( final AproxClientModule module )
    {
        module.setup( http );
    }

    public Aprox connect()
    {
        http.connect();
        return this;
    }

    public void close()
        throws IOException
    {
        http.close();
    }

    public AProxVersioning getVersionInfo()
        throws AproxClientException
    {
        return http.get( "/stats/version-info", AProxVersioning.class );
    }

    public AproxStoresClientModule stores()
        throws AproxClientException
    {
        return module( AproxStoresClientModule.class );
    }

    public AproxContentClientModule content()
        throws AproxClientException
    {
        return module( AproxContentClientModule.class );
    }

    public <T extends AproxClientModule> T module( final Class<T> type )
        throws AproxClientException
    {
        for ( final AproxClientModule module : moduleRegistry )
        {
            if ( type.isInstance( module ) )
            {
                return type.cast( module );
            }
        }

        throw new AproxClientException( "Module not found: %s.", type.getName() );
    }

    public String getBaseUrl()
    {
        return http.getBaseUrl();
    }

}