package homework_2;

import java.util.LinkedList;

public class Station {
	
	double serviceRate;
	int queueCapacity;
	LinkedList<Client> clients;
	
	/**
	 * homogeneous station, has a number of servers with same service rate
	 * 
	 * @param serviceRate service rate of all the servers at the station
	 * @param queueCapacity capacity of the station's queue
	 * @param numServers number of servers at the station
	 */
	public Station(double serviceRate, int queueCapacity, int numServers)
	{
		this.serviceRate = serviceRate;
		this.queueCapacity = queueCapacity;
		this.clients = new LinkedList<Client>();
	}
	
	
	public void add(Client c) throws Exception
	{
		if(this.clients.size() == this.queueCapacity)
		{
			throw new Exception("client was added to full queue");
		}
		else
		{
			this.clients.addLast(c);
		}
	}
	
	public Client dequeue() throws Exception
	{
		if(this.clients.size() == 0)
		{
			throw new Exception("tried to dequeue from an empty queue");
		}
		else
		{
			return this.clients.getFirst();
		}
	}
	
	public boolean isFull()
	{
		return this.clients.size() >= this.queueCapacity;
	}
	
	

}
