import umontreal.iro.lecuyer.stat.TallyStore;
import umontreal.iro.lecuyer.rng.MRG32k3a;
import umontreal.iro.lecuyer.charts.HistogramChart;

;

/**
 * exercise 4 Le but de cet exercice est de vous familiariser avec la librairie
 * SSJ et son utilisation. On vous demande d’ecrire une classe TandemQueue
 * permettant de simuler le systeme de files en tandem de l’exemple 1.7 des
 * notes, avec “production blocking”, en utilisant les recurrences donnees. Pour
 * simplifier, vous pouvez supposer que les durees inter-arrives A i suivent une
 * loi exponentielle de taux λ (moyenne 1/λ), que les dures de service a la
 * station j suivent une loi exponentielle de taux μj , et que toutes ces
 * variables aleatoires sont independantes. (Dans la vraie vie il faudra bien
 * sur faire une analyse statistique des donnees pour identifier des lois de
 * probabilite appropriees et estimer leurs parametres.) Votre classe devra
 * avoir un constructeur qui prend en entree les parametres du modele, soit m,
 * λ, le vecteur des μ j , et le vecteur des c j . Il y aura aussi une methode
 * simulateFixedNumber qui simule un nombre fixe N c the clients et une methode
 * simulateFixedTime qui simule le systeme pour un horizon de temps T fixe
 * (apres T , il n’y a plus d’arrivees, mais tous les clients dej`a arrives
 * doivent poursuivre leur progression dans le systeme et comptent dans les
 * statistiques). Dans les deux cas, on suppose que le systeme est initialement
 * vide. Chacune de ces methodes prendra en entree deux RandomStream’s, l’un qui
 * serviraa generer les A i et l’autre pour les S i,j . Ces methodes prendront
 * aussi en parametres des collecteurs statistiqus de type TallyStore pour
 * recueillir le temps total d’attente et le temps total de blocage a chaque
 * station j. Pour tester cette classe et faire vos experiences, vous invoquerez
 * le constructeur et les methodes de votre classe TandemQueue a partir d’une
 * autre classe (s eparee, dans un autre fichier). L’idee est qu’une fois le
 * “simulateur” TandemQueue implante et teste, on ne doit plus jamais modifier
 * son code lors de son utilisation. On n’utilise que les methodes de son
 * interface. On vous demande ensuite de faire les experiences suivantes avec
 * votre simulateur. Construisez un modele avec λ = 1, m = 3, μ 1 = 1.5, μ 2 = μ
 * 3 = 1.2, c 1 = ∞, c 2 = 4, et c 3 = 8. Simulez ce modele pour un horizon de
 * temps T = 1000 et calculez l’attente totale et le temps total de blocage a
 * chacune des trois stations (sauf a la derniere, o` u il n’y a pas de
 * blocage). Notez que ce ne sont pas les mˆemes mesures que dans l’algorithme
 * des notes. Repetez cette simulation n = 1000 fois, puis calculez la valeur
 * estimee et un intervalle de confiance `a 95% pour l’esperance de chacune des
 * 5 quantites calcules (temps total d’attente et temps de blocage `a chaque
 * station). Faites aussi tracer des histogrammes des 1000 valeurs observes pour
 * chacune de ces 5 quantites. Discutez ce que vous observez; par exemple o` u
 * observe-t-on davantage d’attente ou de blocage? Important: vous devez bien
 * expliquer et documenter vos programmes. Pour cela, vous pouvez prendre
 * exemple sur la documentation des classes de SSJ.
 */

class Exercise4 {
	TallyStore[] waitingTimes;
	TallyStore[] blockedTimes;
	HistogramChart[] waitingCharts;
	HistogramChart[] blockingCharts;

	public Exercise4() {
		// use Integer.MAX_VALUE to symbolize positive infinity
		TandemQueue queue = new TandemQueue(3, 1, new int[] {
				Integer.MAX_VALUE, 4, 8 }, new double[] { 1.5, 1.2, 1.2 });

		// initialize the TallyStore objects
		TallyStore w1 = new TallyStore("Waiting time station 1");
		TallyStore w2 = new TallyStore("Waiting time station 2");
		TallyStore w3 = new TallyStore("Waiting time station 3");
		TallyStore b1 = new TallyStore("Blocked time station 1");
		TallyStore b2 = new TallyStore("Blocked time station 2");

		this.waitingTimes = new TallyStore[] { w1, w2, w3 };
		this.blockedTimes = new TallyStore[] { b1, b2 };

		// prngs
		MRG32k3a gen1 = new MRG32k3a();
		MRG32k3a gen2 = new MRG32k3a();

		// run the simulation 1000 times
		final int numberOfSimulations = 1;
		for (int i = 0; i < numberOfSimulations; i++) {
			queue.simulateFixedTime(gen1, gen2, 1000, waitingTimes,
					blockedTimes);
		}

		waitingCharts = new HistogramChart[queue.getNumberOfServers()];
		blockingCharts = new HistogramChart[queue.getNumberOfServers()-1];

		// add the collected data to the histogram charts
		for (int j = 0; j < numberOfSimulations; j++) {
			// create the chart for the average waiting times
			waitingCharts[j] = new HistogramChart(
					"Average waiting times for server " + j, "Average time",
					"Number of occurrences", waitingTimes[j].getArray());
			// create the chart for the blocked times
			// (no blocking for last server so we ignore)
			if (j != numberOfSimulations - 1) {
				blockingCharts[j] = new HistogramChart(
						"Average blocking time for server " + j,
						"Average time", "Number of occurrences",
						blockedTimes[j].getArray());
			}

		}

	}

}
