/*******************************************************************************
 * Copyright (c) 2014 Red Hat, Inc..
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.commonjava.aprox.model.core;

import java.net.MalformedURLException;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RemoteRepository
    extends ArtifactStore
{
    private static final long serialVersionUID = 1L;

    private static final Logger LOGGER = LoggerFactory.getLogger( RemoteRepository.class );

    public static final int DEFAULT_TIMEOUT_SECONDS = 120;

    @JsonProperty( "url" )
    private String url;

    @JsonProperty( "timeout_seconds" )
    private int timeoutSeconds;

    @JsonProperty( "nfc_timeout_seconds" )
    private int nfcTimeoutSeconds;

    private String host;

    private int port;

    private String user;

    private String password;

    @JsonProperty( "is_passthrough" )
    private boolean passthrough;

    @JsonProperty( "cache_timeout_seconds" )
    private int cacheTimeoutSeconds;

    @JsonProperty( "key_password" )
    private String keyPassword;

    @JsonProperty( "key_certificate_pem" )
    private String keyCertificatePem;

    @JsonProperty( "server_certificate_pem" )
    private String serverCertificatePem;

    @JsonProperty( "proxy_host" )
    private String proxyHost;

    @JsonProperty( "proxy_port" )
    private int proxyPort;

    @JsonProperty( "proxy_user" )
    private String proxyUser;

    @JsonProperty( "proxy_password" )
    private String proxyPassword;

    RemoteRepository()
    {
    }

    public RemoteRepository( final String name, final String remoteUrl )
    {
        super( name );
        this.url = remoteUrl;
        calculateFields();
    }

    RemoteRepository( final String name )
    {
        super( name );
    }

    public String getUrl()
    {
        calculateIfNeeded();
        return url;
    }

    public void setUrl( final String url )
    {
        this.url = url;
        calculateFields();
    }

    public String getUser()
    {
        calculateIfNeeded();
        return user;
    }

    private void calculateIfNeeded()
    {
        if ( host == null )
        {
            calculateFields();
        }
    }

    public void setUser( final String user )
    {
        this.user = user;
    }

    public String getPassword()
    {
        calculateIfNeeded();
        return password;
    }

    public void setPassword( final String password )
    {
        this.password = password;
    }

    public String getHost()
    {
        calculateIfNeeded();
        return host;
    }

    public void setHost( final String host )
    {
        this.host = host;
    }

    public int getPort()
    {
        calculateIfNeeded();
        return port;
    }

    public void setPort( final int port )
    {
        this.port = port;
    }

    public void calculateFields()
    {
        URL url = null;
        try
        {
            url = new URL( this.url );
        }
        catch ( final MalformedURLException e )
        {
            LOGGER.error( "Failed to parse repository URL: '{}'. Reason: {}", e, this.url, e.getMessage() );
        }

        if ( url == null )
        {
            return;
        }

        final String userInfo = url.getUserInfo();
        if ( userInfo != null && user == null && password == null )
        {
            user = userInfo;
            password = null;

            int idx = userInfo.indexOf( ':' );
            if ( idx > 0 )
            {
                user = userInfo.substring( 0, idx );
                password = userInfo.substring( idx + 1 );

                final StringBuilder sb = new StringBuilder();
                idx = this.url.indexOf( "://" );
                sb.append( this.url.substring( 0, idx + 3 ) );

                idx = this.url.indexOf( "@" );
                if ( idx > 0 )
                {
                    sb.append( this.url.substring( idx + 1 ) );
                }

                this.url = sb.toString();
            }
        }

        host = url.getHost();
        if ( url.getPort() < 0 )
        {
            port = url.getProtocol()
                      .equals( "https" ) ? 443 : 80;
        }
        else
        {
            port = url.getPort();
        }
    }

    public int getTimeoutSeconds()
    {
        return timeoutSeconds < 0 ? DEFAULT_TIMEOUT_SECONDS : timeoutSeconds;
    }

    public void setTimeoutSeconds( final int timeoutSeconds )
    {
        this.timeoutSeconds = timeoutSeconds;
    }

    @Override
    public String toString()
    {
        return String.format( "Repository [url=%s, timeoutSeconds=%s, host=%s, port=%s, user=%s, password=%s, getName()=%s, getKey()=%s]", url,
                              timeoutSeconds, host, port, user, password, getName(), getKey() );
    }

    public boolean isPassthrough()
    {
        return passthrough;
    }

    public void setPassthrough( final boolean passthrough )
    {
        this.passthrough = passthrough;
    }

    public int getCacheTimeoutSeconds()
    {
        return cacheTimeoutSeconds;
    }

    public void setCacheTimeoutSeconds( final int cacheTimeoutSeconds )
    {
        this.cacheTimeoutSeconds = cacheTimeoutSeconds;
    }

    public void setKeyPassword( final String keyPassword )
    {
        this.keyPassword = keyPassword;
    }

    public String getKeyPassword()
    {
        return keyPassword;
    }

    public void setKeyCertPem( final String keyCertificatePem )
    {
        this.keyCertificatePem = keyCertificatePem;
    }

    public String getKeyCertPem()
    {
        return keyCertificatePem;
    }

    public void setServerCertPem( final String serverCertificatePem )
    {
        this.serverCertificatePem = serverCertificatePem;
    }

    public String getServerCertPem()
    {
        return serverCertificatePem;
    }

    public String getProxyHost()
    {
        return proxyHost;
    }

    public int getProxyPort()
    {
        return proxyPort;
    }

    public String getProxyUser()
    {
        return proxyUser;
    }

    public String getProxyPassword()
    {
        return proxyPassword;
    }

    public void setProxyHost( final String proxyHost )
    {
        this.proxyHost = proxyHost;
    }

    public void setProxyPort( final int proxyPort )
    {
        this.proxyPort = proxyPort;
    }

    public void setProxyUser( final String proxyUser )
    {
        this.proxyUser = proxyUser;
    }

    public void setProxyPassword( final String proxyPassword )
    {
        this.proxyPassword = proxyPassword;
    }

    @Override
    protected StoreKey initKey( final String name )
    {
        return new StoreKey( StoreType.remote, name );
    }

    public int getNfcTimeoutSeconds()
    {
        return nfcTimeoutSeconds;
    }

    public void setNfcTimeoutSeconds( final int nfcTimeoutSeconds )
    {
        this.nfcTimeoutSeconds = nfcTimeoutSeconds;
    }

}