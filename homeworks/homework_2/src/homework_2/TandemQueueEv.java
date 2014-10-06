package homework_2;

import java.util.LinkedList;

import umontreal.iro.lecuyer.simevents.*;
import umontreal.iro.lecuyer.probdist.ExponentialDist;
import umontreal.iro.lecuyer.rng.RandomStream;
import umontreal.iro.lecuyer.randvar.*;

/**
 * tandem queue class for homework 2 - 3
 * 
 * @author gab
 *
 */

/*
 * Vous allez resimuler une file en tandem comme a ` la question 4 du devoir 1,
 * mais cette fois il y aura trois serveurs `a chaque station au lieu d’un seul,
 * et dans les exp ́eriences num ́eriques, le taux de service de chaque serveur
 * sera μ 1 = 0.5 a ` la station 1, et μ 2 = μ 3 = 0.4 aux deux autres stations.
 * Les dur ́ees de service suivent donc une loi exponentielle, avec la mˆeme
 * moyenne pour tous les serveurs d’une mˆeme station. Dans ce cas-ci, les r
 * ́ecurrences donn ́ees dans l’exemple 1.7 des notes ne tiennent plus, et il
 * est plus difficile d’ ́ecrire des r ́ecurrences, parce que les clients
 * peuvent se d ́epasser aux stations. On vous demande d’implanter une classe
 * TandemQueueEv qui va effectuer la simulation en utilisant des ́ev ́enements.
 * Votre classe devra pouvoir prendre un nombre arbitraire (≥ 1) de serveurs a `
 * chaque station. Elle doit ˆetre implant ́ee de mani`ere simple et ́el ́egante
 * (autant que possible). Le reste sera comme dans la question 4 du devoir 1,
 * sauf qu’il suffit de fournit une m ́ethode simulateFixedTime pour simuler le
 * syst`eme sur un horizon de temps T fix ́e (pas besoin d’implanter une m
 * ́ethode pour un nombre de clients fix ́e). Faites les mˆemes exp ́eriences
 * que dans la question 4 du devoir 1 et comparez vos r ́esultats.
 */
public class TandemQueueEv {

	int numberOfStations;
	int[] numberOfServers;
	double[] serviceRates;
	int[] queueSizes;
	Station[] stations;

	RandomVariateGen arrivalsRNG;
	RandomStream serviceRNG;

	/**
	 * constructor for tandem queue simulated with event and multiple servers
	 * @param arrivalRate
	 * @param numberOfStations
	 * @param numberOfServers
	 * @param serviceRates
	 * @param queueSizes
	 * @param arrivalsRNG
	 * @param serviceRNG
	 */
	public TandemQueueEv(double arrivalRate, int numberOfStations,
			int[] numberOfServers, double[] serviceRates, int[] queueSizes,
			RandomStream arrivalsRNG, RandomStream serviceRNG) {
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
		for (int i = 0; i < numberOfStations; i++) {
			this.stations[i] = new Station(numberOfServers[i], serviceRates[i],
					queueSizes[i], i);
		}
	}

	
	class Station {
		LinkedList<Client> queue;
		Server[] servers;
		int id;
		int maxQueueSize;
		LinkedList<Server> blocked;  // blocked servers in the previous station (if any)

		/**
		 * constructor for the station
		 * 
		 * @param numberOfServers
		 *            number of servers in the station
		 * @param serviceRate
		 *            service rate of the servers in the station (homogenous)
		 * @param maxQueueSize
		 *            max size of the waiting queue
		 * @param stationId
		 *            id of the station in the TandemQueueEv object
		 */
		public Station(int numberOfServers, double serviceRate,
				int maxQueueSize, int stationId) {
			this.servers = new Server[numberOfServers];
			for (int i = 0; i < numberOfServers; i++) {
				this.servers[i] = new Server(serviceRate);
			}
			this.id = stationId;
			this.queue = new LinkedList<Client>();
			this.maxQueueSize = maxQueueSize;
			this.blocked = new LinkedList<Server>();
		}
		
		public boolean isBlocked()
		{
			return this.queue.size() == this.maxQueueSize;
		}
		
		public void addClient(Client c)
		{
			// add a client to the waiting list
		}
		
		
		
	}

	
	class Client {
		double[] waitTime;
		double[] serveTime;
		double[] blockTime;
		
		/**
		 * the clients will store their information on the traversal
		 * of the system and offload them when they go out
		 * @param numberOfServers
		 */
		public Client(int numberOfServers)
		{
			this.waitTime = new double[numberOfServers];
			this.serveTime = new double[numberOfServers];
			this.blockTime = new double[numberOfServers];
		}
	}

	
	class Server {
		double serviceRate;
		public Server(double serviceRate) {
			this.serviceRate = serviceRate;
		}
		
		public double generateServiceTime(double serviceRate, RandomStream prng) {
			return ExponentialDist.inverseF(serviceRate, prng.nextDouble());
		}
		
	}


	class Arrival extends Event {
		public Arrival(Station s, Client c)
		{
			
		}
		public void actions() {
			new Arrival().schedule(arrivalsRNG.nextDouble());
		}
	}

	class EndOfSim extends Event {
		public void actions() {
			Sim.stop();
		}
	}

	

	class Service extends Event {

		public void actions() {

		}
	}

	/**
	 * Departure of the client c from the server S
	 * 
	 * @author gab
	 *
	 */
	class Departure extends Event {
		public Departure(Client c, Server s) {
			// so client c departs from server s, find out which station is next
			// (could be the end)
			int nextStation = s.station + 1;
			if (nextStation >= numberOfStations) {
				s.nextClient();
			} else {

			}

		}

		public void actions() {
			// log the service time

			//

		}
	}

	/**
	 * main simulation interface
	 * 
	 * @param timeHorizon
	 */
	private void simulateOneRun(double timeHorizon) {
		Sim.init();
		new EndOfSim().schedule(timeHorizon);
		new Arrival().schedule(arrivalsRNG.nextDouble());
		Sim.start();
	}

	/**
	 * main simulation interface
	 * 
	 * @param timeHorizon
	 */
	public void simulateFixedTime(double timeHorizon) {
		simulateOneRun(timeHorizon);
	}

}
