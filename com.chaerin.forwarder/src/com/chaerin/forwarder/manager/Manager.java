package com.chaerin.forwarder.manager;

import java.sql.Connection;
import java.sql.DriverManager;

import com.chaerin.forwarder.BootStrap;
import com.chaerin.forwarder.generator.Generator;
import com.chaerin.forwarder.receiver.Receiver;

public class Manager extends Thread
{
	/**
	 * Process name & Thread delay
	 */
	private final String procname 	= "MANAGER";
	private long threadDelay 		= 4000;

	
	/**
	 * Receiver, Generator 
	 */
	private int receiverCount 			= 0;
	private int generatorCount 			= 0;
	
	private Receiver[] receiverGroup 	= null;
	private Generator[] generatorGroup 	= null;
	
	
	/**
	 * MariaDB connection info
	 */
	public static String CONF_DB_IPS = null;
	public static String CONF_DB_PORTS = null;
	public static String CONF_DB_NAME = null;
	public static String CONF_DB_USER = null;
	public static String CONF_DB_PW = null;
	
	
	
	@Override
	public void run()
	{
		try
		{
			if( !this.loadProperty() )
				return;
			
			if( !this.initMariaDBConn() )
				return;
			
			while(true)
			{
				if( !DBSessionCheck() )
				{
					
				}
				
				this.runReceiver();		// kafka data receiver
				this.runGenerator();	// insert to data
			}
		}
		catch ( Exception e )
		{
			e.printStackTrace();
			return;
		}
		finally
		{

		}
	}

	
	
	private void runReceiver()
	{
		try
		{
			if ( this.receiverGroup == null )
				this.receiverGroup = new Receiver[ this.receiverCount ];

			for ( int i = 0; i < this.receiverCount; i++ )
			{
				String name = String.format( "REV-%1$03d", i );
				
				if ( this.receiverGroup[i] == null )
				{
					this.receiverGroup[i] = new Receiver( i );
					this.receiverGroup[i].start();
				}
			}

			return;
		}
		catch ( Exception e )
		{
			e.printStackTrace();
			return;
		}
	}
	
	
	
	private void runGenerator()
	{
		try
		{
			if ( this.generatorGroup == null )
				this.generatorGroup = new Generator[ this.generatorCount ];

			for ( int i = 0; i < this.generatorCount; i++ )
			{
				String name = String.format( "GEN-%1$03d", i );
				
				if ( this.generatorGroup[i] == null )
				{
					this.generatorGroup[i] = new Generator( i );
					this.generatorGroup[i].start();
				}
			}

			return;
		}
		catch ( Exception e )
		{
			e.printStackTrace();
			return;
		}
	}
	
	
	
	
	public boolean initMariaDBConn()
	{
		boolean result = false;
		
		Connection conn = null;
		String DB_URL = "jdbc:mariadb://" + CONF_DB_IPS + ":" + CONF_DB_PORTS + "/" + CONF_DB_NAME;
		
		try
		{
			System.out.println(DB_URL);
			System.out.println(CONF_DB_USER);
			System.out.println(CONF_DB_PW);
			
			Class.forName( "org.mariadb.jdbc.Driver" );
			conn = DriverManager.getConnection(DB_URL, CONF_DB_USER, CONF_DB_PW);
			
			if( conn != null )
				System.out.println("DB접속성공!");
			
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
	
	
	private boolean DBSessionCheck()
	{
		boolean result = false;

		try
		{

			result = true;
			return result;
		}
		catch ( Exception e )
		{
			return false;
		}
		finally 
		{

		}
	}
	
	
	
	private boolean loadProperty()
	{
		boolean result		= false;
		String tmpStr 		= null;

		try
		{
			
			/**
			 * Receiver, Generator thread count
			 */
			tmpStr = BootStrap.config.getProperty( "receiver.count" );
			if ( tmpStr == null )
				return result;
			this.receiverCount = Integer.parseInt(tmpStr);
			
			tmpStr = BootStrap.config.getProperty( "generator.count" );
			if ( tmpStr == null )
				return result;
			this.generatorCount = Integer.parseInt(tmpStr);
			
			
			/**
			 * Config DB connection info
			 */
			tmpStr = BootStrap.config.getProperty( "conf.db.ip" );
			if ( tmpStr == null )
				return result;
			Manager.CONF_DB_IPS = tmpStr;

			tmpStr = BootStrap.config.getProperty( "conf.db.port" );
			if ( tmpStr == null )
				return result;
			Manager.CONF_DB_PORTS = tmpStr;

			tmpStr = BootStrap.config.getProperty( "conf.db.name" );
			if ( tmpStr == null )
				return result;
			Manager.CONF_DB_NAME = tmpStr;

			tmpStr = BootStrap.config.getProperty( "conf.db.user" );
			if ( tmpStr == null )
				return result;
			Manager.CONF_DB_USER = tmpStr;

			tmpStr = BootStrap.config.getProperty( "conf.db.passwd" );
			if ( tmpStr == null )
				return result;
			Manager.CONF_DB_PW = tmpStr;


			result = true;
			return result;
		}
		catch ( Exception e )
		{
			BootStrap.logger.info( e.getMessage() );
			return false;
		}
		finally
		{

		}
	}
	
}
