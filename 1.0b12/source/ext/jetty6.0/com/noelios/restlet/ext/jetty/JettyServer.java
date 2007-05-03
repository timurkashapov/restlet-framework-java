/*
 * Copyright 2005-2006 Noelios Consulting.
 *
 * The contents of this file are subject to the terms
 * of the Common Development and Distribution License
 * (the "License").  You may not use this file except
 * in compliance with the License.
 *
 * You can obtain a copy of the license at
 * http://www.opensource.org/licenses/cddl1.txt
 * See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL
 * HEADER in each file and include the License file at
 * http://www.opensource.org/licenses/cddl1.txt
 * If applicable, add the following below this CDDL
 * HEADER, with the fields enclosed by brackets "[]"
 * replaced with your own identifying information:
 * Portions Copyright [yyyy] [name of copyright owner]
 */

package com.noelios.restlet.ext.jetty;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.List;

import javax.servlet.ServletException;

import org.mortbay.jetty.Connector;
import org.mortbay.jetty.HttpConnection;
import org.mortbay.jetty.nio.SelectChannelConnector;
import org.restlet.Call;
import org.restlet.Restlet;
import org.restlet.component.Component;
import org.restlet.component.RestletContainer;
import org.restlet.connector.Server;
import org.restlet.connector.ServerCall;
import org.restlet.data.Protocol;
import org.restlet.data.Protocols;

/**
 * Jetty connector acting as a HTTP server.
 * @see <a href="http://jetty.mortbay.com/">Jetty home page</a>
 */
public class JettyServer extends org.mortbay.jetty.Server implements Server
{
   /** Serial version identifier. */
   private static final long serialVersionUID = 1L;

   /** The name of this REST connector. */
   protected String name;
	
   /** The delegate Server. */
   protected Server delegate;

   /** The target Restlet. */
   protected Restlet target;

   /** The owner component. */
   protected Component owner;

   /**
    * Constructor.
    * @param protocol The connector protocol.
    * @param name The unique connector name.
    * @param delegate The delegate Server.
    * @param address The optional listening IP address (local host used if null).
    * @param port The listening port.
    */
   public JettyServer(Protocol protocol, String name, Server delegate, String address, int port)
   {
      // Create and configure the Jetty HTTP connector
      Connector connector = new SelectChannelConnector(); // Uses non-blocking NIO
      
      if(address != null)
      {
      	connector.setHost(address);
      }
      
      connector.setPort(port);
      Connector[] connectors = new Connector[]{connector};
      setConnectors(connectors);

      this.name = name;
      this.delegate = delegate;
      this.target = null;
   }
   
   /**
    * Constructor.
    * @param protocol The connector protocol.
    * @param name The unique connector name.
    * @param delegate The delegate Server.
    * @param address The IP address to listen to.
    */
   public JettyServer(Protocol protocol, String name, Server delegate, InetSocketAddress address)
   {
   	this(protocol, name, delegate, address.getHostName(), address.getPort());
   }

   /**
    * Constructor.
    * @param protocol The connector protocol.
    * @param name The unique connector name.
    * @param delegate The delegate Server.
    * @param port The HTTP port number.
    */
   public JettyServer(Protocol protocol, String name, Server delegate, int port)
   {
   	this(protocol, name, delegate, null, port);
   }

   /**
    * Returns the delegate server.
    * @return The delegate server.
    */
   public Server getDelegate()
   {
   	return this.delegate;
   }

   /**
    * Sets the delegate server.
    * @param delegate The delegate server.
    */
   public void setDelegate(Server delegate)
   {
   	this.delegate = delegate;
   }
   
   /**
    * Returns the supported protocols. 
    * @return The supported protocols.
    */
   public static List<Protocol> getProtocols()
   {
   	return Arrays.asList(new Protocol[]{Protocols.HTTP});
   }

   /**
    * @param keystorePath The path of the keystore file.
    * @param keystorePassword The keystore password.
    * @param keyPassword The password of the server key .
    */
   public void configureSsl(String keystorePath, String keystorePassword, String keyPassword)
   {
      throw new IllegalArgumentException("SSL not currently supported by Jetty 6 connector");
   }

   /**
    * Handles a HTTP connection.
    * @param connection The connection to handle.
    */
   public void handle(HttpConnection connection) throws IOException, ServletException
   {
      handle(new JettyCall(connection));
      connection.completeResponse();
   }

   /**
    * Handles a uniform call.
    * The default behavior is to as the attached Restlet to handle the call.
    * @param call The call to handle.
    */
   public void handle(Call call)
   {
      getTarget().handle(call);
   }

   /**
    * Indicates if the connector is stopped.
    * @return True if the connector is stopped.
    */
   public boolean isStopped()
   {
      return !isStarted();
   }

   /**
    * Returns the target Restlet.
    * @return The target Restlet.
    */
   public Restlet getTarget()
   {
      return this.target;
   }

   /**
    * Sets the target Restlet.
    * @param target The target Restlet.
    */
   public void setTarget(Restlet target)
   {
      this.target = target;
   }

   /**
    * Handles the HTTP protocol call.<br/>
    * The default behavior is to create an UniformCall and invoke the "handle(UniformCall)" method.
    * @param call The HTTP protocol call.
    */
   public void handle(ServerCall call) throws IOException
   {
   	getDelegate().handle(call);
   }

   /**
    * Returns the connector's protocol.
    * @return The connector's protocol.
    */
   public Protocol getProtocol()
   {
      return Protocols.HTTP;
   }

   /**
    * Returns the owner component.
    * @return The owner component.
    */
   public Component getOwner()
   {
      return this.owner;
   }

   /**
    * Sets the owner component.
    * @param owner The owner component.
    */
   public void setOwner(Component owner)
   {
      this.owner = owner;
   }

   /**
    * Returns the name of this connector.
    * @return The name of this connector.
    */
   public String getName()
   {
      return this.name;
   }

   /**
    * Sets the name of this REST element.
    * @param name The name of this REST element.
    */
   public void setName(String name)
   {
   	this.name = name;
   }
}
