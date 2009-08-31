package com.eucalyptus.cluster.handlers;

import java.nio.channels.AlreadyConnectedException;

import org.apache.log4j.Logger;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;

import com.eucalyptus.cluster.Cluster;
import com.eucalyptus.event.Event;
import com.eucalyptus.ws.BindingException;

public class LogStateHandler extends AbstractClusterMessageDispatcher {
  private static Logger LOG = Logger.getLogger( NetworkStateHandler.class );
  public LogStateHandler( Cluster cluster ) throws BindingException {
    super( cluster, false );
  }

  @Override
  public void trigger( ) {}

  @Override
  public void fireEvent( Event event ) {}

  @Override
  public void downstreamMessage( ChannelHandlerContext ctx, MessageEvent e ) {
    ctx.sendDownstream( e );
  }

  @Override
  public void upstreamMessage( ChannelHandlerContext ctx, MessageEvent e ) {
    ctx.sendUpstream( e );
    ctx.getChannel( ).close( );
  }

  @Override
  public void advertiseEvent( Event event ) {}

}