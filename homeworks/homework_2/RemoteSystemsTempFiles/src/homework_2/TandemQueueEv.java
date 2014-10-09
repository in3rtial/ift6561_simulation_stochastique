package homework_2;

import java.util.LinkedList;
import java.util.ArrayList;

import umontreal.iro.lecuyer.simevents.*;
import umontreal.iro.lecuyer.probdist.ExponentialDist;
import umontreal.iro.lecuyer.rng.RandomStream;
import umontreal.iro.lecuyer.randvar.*;
import umontreal.iro.lecuyer.rng.MRG32k3a;


public class TandemQueueEv {
	static int clientID = 0;

	int numberOfStations;
	int[] numberOfServers;
	double[] serviceRates;
	int[] queueSizes;
	Station[] stations;
	RandomVariateGen arrivalsRNG;
	RandomStream serviceRNG;

	double[] waitingTimes;
	double[] blockedTimes;
	double[] serviceTimes;
	int numberOfClients;
	
	/**
	 * constructor for tandem queue simulated with event and multiple servers
	 * 
	 * @param arrivalRate arrival rate at station 0
	 * @param numberOfStations number of stations in the system
	 * @param numberOfServers number of servers per station in the server (vector)
	 * @param serviceRates service rate at each station (same for every servers of a station)
	 * @param queueSizes size of the waiting queues at each station (station[0] = Inf)
	 * @param arrivalsRNG prng that will generate variables for the arrival
	 * @param serviceRNG prng that will generate the service times for the servers
	 */
	public TandemQueueEv(double arrivalRate,
						 int numberOfStations,
						 int[] numberOfServers,
						 double[] serviceRates,
						 int[] queueSizes,
						 RandomStream arrivalsRNG,
						 RandomStream serviceRNG)
	{
		assert numberOfServers.length == serviceRates.length;
		assert numberOfServers.length == queueSizes.length;
		assert numberOfServers.length == numberOfStations;

		this.numberOfStations = numberOfStations;
		this.numberOfServers = numberOfServers;
		this.serviceRates = serviceRates;
		this.arrivalsRNG = new RandomVariateGen(arrivalsRNG,
				new ExponentialDist(arrivalRate));
		this.serviceRNG = serviceRNG;
		this.stations = new Station[numberOfStations];
		this.queueSizes = queueSizes;


	}

	private void initSystem()
	{
		// initialize the stations
		
		for (int i = 0; i < numberOfStations; i++) {
			this.stations[i] = new Station(this.numberOfServers[i], this.serviceRates[i], this.queueSizes[i], i);
		}
		
		this.waitingTimes = new double[numberOfStations];
		this.blockedTimes = new double[numberOfStations];
		this.serviceTimes = new double[numberOfStations];
		this.numberOfClients = 0;
	}
	class Station {
		LinkedList<Client> queue;
		Server[] servers;
		int stationId;
		int maxQueueSize;
		LinkedList<BlockedState> blockedServers;
		LinkedList<Server> availableServers;
		/**
		 * constructor for the station
		 * 
		 * @param numberOfServers
		 *            number of servers in the station
		 * @param serviceRate
		 *            service rate of the servers in the station (homogeneous)
		 * @param maxQueueSize
		 *            max size of the waiting queue
		 * @param stationId
		 *            id of the station in the TandemQueueEv object
		 */
		public Station(int numberOfServers,
					   double serviceRate,
					   int maxQueueSize,
					   int stationId)
		{
			this.servers = new Server[numberOfServers];
			for (int i = 0; i < numberOfServers; i++) {
				this.servers[i] = new Server(serviceRate, this);
			}
			this.stationId = stationId;
			this.queue = new LinkedList<Client>();
			this.maxQueueSize = maxQueueSize;
			// fill the available servers
			this.availableServers = new LinkedList<Server>();
			for(int i = 0; i < this.servers.length; i++)
			{
				this.availableServers.addLast(this.servers[i]);
			}
			
			this.blockedServers = new LinkedList<BlockedState>();
		}

		public boolean queueFull()
		{
			return this.queue.size() >= this.maxQueueSize;
		}
	}

	class BlockedState
	{
		Server server;
		Client client;
		public BlockedState(Server server, Client client)
		{
			this.server = server;
			this.client = client;
		}
	}

	class Server {
		double serviceRate;
		Station station;
		public Server(double serviceRate,
					  Station s)
		{
			this.serviceRate = serviceRate;
			this.station = s;
		}
		
		public double getServiceTime(RandomStream prng) {
			return ExponentialDist.inverseF(this.serviceRate, prng.nextDouble());
		}
	}
	
	class Client {
		double[] waitTime;
		double[] serveTime;
		double[] blockTime;

		public Client(int numberOfServers)
		{
			this.waitTime = new double[numberOfServers];
			this.serveTime = new double[numberOfServers];
			this.blockTime = new double[numberOfServers];
		}
	}

	// arrival in the tandem queue system (station 0)
	class Arrival extends Event {
		public void actions() {
			// create a new client
			Client client = new Client(numberOfStations);

			// add the new client to the first station and start its wait time
			stations[0].queue.addLast(client);
			client.waitTime[0] = Sim.time();

			// alert the station of a new client
			new CheckState(numberOfStations - 1).schedule(0);

			// schedule the next arrival (schedule is based on the current time
			// + interval)
			new Arrival().schedule(arrivalsRNG.nextDouble());

		}
	}

	class StartService extends Event {
		int stationId;
		Server server;
		Client client;
		public StartService(int stationId, Server server, Client client)
		{
			this.stationId = stationId;
			this.server = server;
			this.client = client;
		}
		
		public void actions()
		{
			// mark the end of waiting
			client.waitTime[stationId] = Sim.time() - client.waitTime[stationId];
			
			// put back the server into available list after service
			double serviceTime = server.getServiceTime(serviceRNG);
			client.serveTime[stationId] = serviceTime;
			new FinishService(stationId, server, client).schedule(serviceTime);
			new CheckState(numberOfStations - 1).schedule(0);
		}
	}


	class FinishService extends Event {
		int stationId;
		Server server;
		Client client;
		
		public FinishService(int stationId, Server server, Client client)
		{
			this.stationId = stationId;
			this.server = server;
			this.client = client;
		}
		
		public void actions()
		{
			// GOING OUT
			if(this.stationId == (numberOfStations - 1))
			{
				// dump the stats
				for(int i = 0; i < numberOfStations; i++)
				{
					blockedTimes[i] += client.blockTime[i];
					waitingTimes[i] += client.waitTime[i];
					serviceTimes[i] += client.serveTime[i];
				}
				numberOfClients += 1;
				client = null; // garbage collect
				stations[stationId].availableServers.addLast(server);
			}
			// BLOCKED
			else if (stations[stationId + 1].queueFull())
			{
				//TODO System.out.println("Blocked @ " + stationId);
				// start counting the blocking time
				client.blockTime[stationId] = Sim.time();

				// save the state for when it will be unblocked
				stations[stationId].blockedServers.addLast(new BlockedState(server, client));
			}
			// NOT BLOCKED
			else
			{
				// since the client is going to the next station, we must fix its wait time
				client.waitTime[stationId + 1] = Sim.time();
				
				// we affect the next station
				stations[stationId + 1].queue.addLast(client);

				// we affect this station
				stations[stationId].availableServers.addLast(server);
			}
			new CheckState(numberOfStations - 1).schedule(0);
			return;

		}
	}


	class CheckState extends Event{
		// used to alert the station of a state change (get them serving...)
		// will also unblock servers
		int stationId;
		public CheckState(int stationId)
		{
			this.stationId = stationId;
		}
		
		public void actions()
		{
			//TODO System.out.println("check " + stationId);
	
			//TODO System.out.println("Check Station " + stationId + " q : " + stations[stationId].queue.size() + " av " + stations[stationId].availableServers.size());
	
			// liberate blocked states if the next station is not full
			if(stations[stationId].blockedServers.size() != 0) // last station cannot be blocked
			{
				if(!(stations[stationId + 1].queueFull()))
				{
					
					int toLiberate = Math.min(stations[stationId + 1].maxQueueSize - stations[stationId +1].queue.size(), stations[stationId].blockedServers.size());
					for(int i = 0; i < toLiberate; i++)
					{
						BlockedState blockedState = stations[stationId].blockedServers.removeFirst();
						Client client = blockedState.client;
						Server server = blockedState.server;
						// assign the blocking time (based on the difference)
						client.blockTime[stationId] = Sim.time() - client.blockTime[stationId];
						// put the server back into active duty
						stations[stationId].availableServers.addLast(server);
						// put the client in the next waiting line
						client.waitTime[stationId+1] = Sim.time();
						stations[stationId+1].queue.addLast(client);
						
					}
				}
			}
			
			// process the new clients
			int toProcess = Math.min(stations[stationId].availableServers.size(), stations[stationId].queue.size());
			for(int i = 0; i < toProcess; i++)
			{
				// spawn service starts
				Server server = stations[stationId].availableServers.removeFirst();
				Client client = stations[stationId].queue.removeFirst();
				new StartService(stationId, server, client).schedule(0);
			}
			

			// repercussions happen backward in the system
			if(stationId != 0)
			{
				new CheckState(stationId-1).schedule(0);
			}
		}
	}





	class EndOfTime extends Event {
		public void actions() {
			Sim.stop();
		}
	}


	public void simulateOneRun(double timeHorizon) {
		// initialize the collectors
		initSystem();
		Sim.init();
		new EndOfTime().schedule(timeHorizon);
		new Arrival().schedule(arrivalsRNG.nextDouble());
		Sim.start();
	}


	public void simulateFixedTime(double timeHorizon) {
		simulateOneRun(timeHorizon);
	}


	public static void test1()
	{
		double[] serviceRates = {100.0};
		double arrivalRate = 1.0;
		int numberOfStations= 1;
		int[] numberOfServers = {3};
		int[] queueSizes = {10000000};
		MRG32k3a prng1 = new MRG32k3a();
		MRG32k3a prng2 = new MRG32k3a();
		
		/*
		 * double arrivalRate, int numberOfStations, int[] numberOfServers,
		 * double[] serviceRates, int[] queueSizes, RandomStream arrivalsRNG,
		 * RandomStream serviceRNG)
		 */
		TandemQueueEv queue = new TandemQueueEv(arrivalRate,
												numberOfStations,
												numberOfServers,
												serviceRates,
												queueSizes,
												prng1,
												prng2);
		queue.simulateOneRun(10.0);
	}


	public static void test2()
	{
		double[] serviceRates = {5.0, 10.0};
		double arrivalRate = 1.0;
		int numberOfStations= 2;
		int[] numberOfServers = {1, 1};
		int[] queueSizes = {10000000, 10};
		MRG32k3a prng1 = new MRG32k3a();
		MRG32k3a prng2 = new MRG32k3a();
		
		/*
		 * double arrivalRate, int numberOfStations, int[] numberOfServers,
		 * double[] serviceRates, int[] queueSizes, RandomStream arrivalsRNG,
		 * RandomStream serviceRNG)
		 */
		// initialize the collectors

		TandemQueueEv queue = new TandemQueueEv(arrivalRate,
												numberOfStations,
												numberOfServers,
												serviceRates,
												queueSizes,
												prng1,
												prng2);
		queue.simulateOneRun(10.0);
		
		
		
	}
	public static void test3()
	{
		double[] serviceRates = {0.5, 0.4, 0.4};
		double arrivalRate = 1.0;
		int numberOfStations= 3;
		int[] numberOfServers = {3,3,3};
		int[] queueSizes = {Integer.MAX_VALUE, 4, 8};
		MRG32k3a prng1 = new MRG32k3a();
		MRG32k3a prng2 = new MRG32k3a();
		
		/*
		 * double arrivalRate, int numberOfStations, int[] numberOfServers,
		 * double[] serviceRates, int[] queueSizes, RandomStream arrivalsRNG,
		 * RandomStream serviceRNG)
		 */
		// initialize the collectors

		TandemQueueEv queue = new TandemQueueEv(arrivalRate,
												numberOfStations,
												numberOfServers,
												serviceRates,
												queueSizes,
												prng1,
												prng2);
		queue.simulateOneRun(50.0);
	
		
	}
	
	
	public double[] summarize(ArrayList<double[]> list)
	{
		double[] sum = new double[list.get(0).length];
		for(int i = 0; i < list.size(); i++)
		{
			for(int j = 0; j < list.get(0).length; j++)
			{
				sum[j] += list.get(i)[j];
			}
		}
		
		return sum;
	}
	
	
	
	public static void printDouble(double[] toPrint)
	{
		System.out.print("[ ");
		for(int j = 0; j < toPrint.length; j++)
		{
			System.out.print(toPrint[j]+" ");
		}
		System.out.print("]\n");
	}
	
	
	public static void printArrayDouble(ArrayList<double[]> list)
	{
		for (int i = 0; i < list.size(); i++)
		{
			System.out.print("[ ");
			for(int j = 0; j < list.get(i).length; j++)
			{
				System.out.print(list.get(i)[j]+" ");
			}
			System.out.print("]\n");
		}
	}
	
	public static void main(String[] args) {
		test3();
		test3();
		System.out.println("done");
	}
}
