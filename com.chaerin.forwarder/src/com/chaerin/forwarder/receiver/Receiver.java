package com.chaerin.forwarder.receiver;

import java.util.LinkedList;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import com.chaerin.forwarder.BootStrap;


public class Receiver extends Thread{

	/**
	 * Kafka Settings
	 */
	private KafkaConsumer<String, String> consumer	= null;;
	private LinkedList<String> topicList 			= null;
	private String bootstrapServers 				= null;
	private String sessionTimeoutMs 				= null;
	private String zookeeperSyncTimeMs 				= null;
	private String groupId 							= null;
	private String keyDeserializer 					= null;
	private String valueDeserializer 				= null;
	private final int pollingIntervalMs				= 500;
	private String topic							= null;
	
	
	public Receiver( int id )
	{
		System.out.println( id + " - receiver");
	}
	
	
	@Override
	public void run()
	{
		try
		{
			if( !this.settingConfig() )
				return;
			
			if ( !this.initBroker() )
				return;
			
			while( true )
			{
				try
				{
					this.processing();
				}
				catch( Exception e )
				{
					e.printStackTrace();
					return;
				}
				finally
				{
					
				}
			}
			
		}
		catch ( Exception e )
		{
			return;
		}
		finally
		{

		}
	}
	
	
	
	private boolean processing()
	{
		boolean result = false;
		ConsumerRecords<String, String> records = null;
		
		try
		{
			records = this.consumer.poll( this.pollingIntervalMs );
			
			if ( records == null )
				return result;
			
			for( ConsumerRecord<String, String> record : records )
			{
				String readTopic = record.topic();
				
				if( this.topicList.contains( readTopic ) )
				{
					System.out.println( record.value() );
				}
			}
			
			result = true;
			return result;
		}
		catch( Exception e )
		{
			e.printStackTrace();
			return result;
		}
	}

	
	
	private boolean initBroker()
	{
		boolean result = false;
		
		Properties configs = null;
		
		try
		{
			configs = new Properties();
			configs.put( "bootstrap.servers"		, this.bootstrapServers );
			configs.put( "session.timeout.ms"		, this.sessionTimeoutMs );
			configs.put( "zookeeper.sync.time.ms"	, this.zookeeperSyncTimeMs );
			configs.put( "group.id"					, this.groupId );
			configs.put( "key.deserializer"			, this.keyDeserializer );
			configs.put( "value.deserializer"		, this.valueDeserializer );

			if ( this.consumer == null )
				this.consumer = new KafkaConsumer<>( configs );
			
			this.topicList = new LinkedList<String>(); 
			this.topicList.add( this.topic );
			
			this.consumer.subscribe( this.topicList );
			
			result = true;
			return result;
		}
		catch ( Exception e )
		{
			e.printStackTrace();
			return false;
		}
		finally
		{
			
		}
	}
	
	
	
	
	private boolean settingConfig()
	{
		boolean result	= false;
		String tmpStr 	= null;
		
		try
		{
			/**
			 * Kafka consumer properties
			 */
			tmpStr =BootStrap.config.getProperty( "receiver.bootstrap.servers" );
			if ( tmpStr == null )
				return result;
			this.bootstrapServers = tmpStr;
			
			tmpStr = BootStrap.config.getProperty( "receiver.session.timeout.ms" );
			if ( tmpStr == null )
				return result;
			this.sessionTimeoutMs = tmpStr;
			
			tmpStr = BootStrap.config.getProperty( "receiver.group.id" );
			if ( tmpStr == null )
				return result;
			this.groupId = tmpStr;
			
			tmpStr = BootStrap.config.getProperty( "receiver.key.deserializer" );
			if ( tmpStr == null )
				return result;
			this.keyDeserializer = tmpStr;

			tmpStr = BootStrap.config.getProperty( "receiver.value.deserializer" );
			if ( tmpStr == null )
				return result;
			this.valueDeserializer = tmpStr;
			
			tmpStr = BootStrap.config.getProperty( "receiver.zookeeper.sync.time.ms" );
			if ( tmpStr == null )
				return result;
			this.zookeeperSyncTimeMs = tmpStr;
			
			tmpStr = BootStrap.config.getProperty( "receiver.topic" );
			if ( tmpStr == null )
				return result;
			this.topic = tmpStr;
			

			result = true;
			return result;
		}
		catch ( Exception e )
		{
			e.printStackTrace();
			return false;
		}
		finally
		{

		}
	}

}
