public class Part2 {

	/*
	 * STATEMENT
	 * 
	 * Vous allez simuler le meme modele VG qu’a l’exemple 6.52 des notes de
	 * cours (voir aussi la section 2.16.6), avec les memes valeurs des
	 * parametres. On a aussi t j = jT /d pour j = 0, . . . , d, et d = 16. SSJ
	 * fournit les outils requis pour simuler de tels processus. Vous pouvez
	 * utiliser les valeurs exactes donnees dans l’exemple pour verifier votre
	 * programme.
	 * 
	 * Pour generer les processus gamma via “bridge sampling”, utilisez la
	 * classe GammaProcess- SymmetricalBridge, qui est beaucoup plus rapide que
	 * GammaProcessBridge.
	 */

	/*
	 * (a) Ecrivez un programme permettant de reproduire a peu pres les r
	 * esultats du tableau 6.12 des notes et refaites les experiences pour la
	 * colonne n = 2^14 , avec m = 32 repetitions, pour les quatre methodes RQMC
	 * indiques dans le tableau. Bien sˆ ur, vous n’obtiendrez pas exactement
	 * les memes facteurs, car ces valeurs sont des estimations bruitees.
	 */

	/*
	 * (b) Supposons maintenant que d = 1, de sorte que S ̄ = S(t 1 ) = S(1).
	 * Supposons aussi que K 100, de sorte que le “payoff” de l’option sera
	 * rarement positif. On voudra alors utiliser l’importance sampling (IS). Si
	 * on simule le processus par DGBS, il semble raisonnable d’augmenter la
	 * moyenne du processus G + et de diminuer celle du processus G − , de
	 * maniere a augmenter la valeur de G + (1) − G − (1).
	 * 
	 * Pour cela, on peut utiliser la strategie heuristique suivante. On
	 * applique une torsion expo- nentielle (“exponential twisting”) a la
	 * densite de chacune des deux v.a. gamma: on multiplie la densite g + (x)
	 * de G + (1) par e θx et la densite g − (y) de G − (1) par e −θy , pour une
	 * constante θ ≥ 0 qui reste a choisir, puis on normalise les densites. Pour
	 * θ ≥ 0 donne, quelles seront les nouvelles densites? Comment peut-on
	 * generer des variables aleatoires selon ces nouvelles densites? Et quel
	 * sera le rapport de vraisemblance associe?
	 */

	/*
	 * (c) Notons que l’option commence a payer lorsque S(1) ≥ K, i.e., lorsque
	 * r + ω + G + (1) − G − (1) = ln[S(1)/S(0)] ≥ ln(K/S(0)). Il apparait alors
	 * raisonnable de choisir θ tel que l’on ait E[G + (1)−G − (1)] =
	 * ln(K/S(0))−r−ω sous les nouvelles densites. Enecrivant l’esperance en
	 * fonction de θ, obtenez une fonction monotone de θ dont la racine est
	 * precisement cette valeur de θ. On pourra alors trouver cette racine en
	 * utilisant une methode telle que celle de Brent-Dekker, disponible dans
	 * SSJ et ailleurs. Implantez cette strategie IS, et utilisez-la pour
	 * estimer la valeur de l’option avec une erreur relative d’au plus 1% pour
	 * K = 140 et pour K = 180. Comparez la valeur et la variance de votre
	 * estimateur IS avec celui obtenu sans IS, dans les deux cas.
	 */

	/*
	 * (d) Une seconde approche pourrait consister a trouver θ qui minimise la
	 * valeur maximale du rapport de vraisemblance lorsque l’estimateur est non
	 * negatif, qui est en fait atteinte lorsque S(1) = K. Cela minimisera la
	 * borne sur la variance donnee par la proposition 6.26 des notes. Trouvez
	 * une expression pour ce θ et comparez-le a celui utilise en (c).
	 */

	/*
	 * (e) On peut essayer d’ameliorer l’estimateur defini en (c) en s’inspirant
	 * de l’exemple 1.42, comme suit. On genere d’abord G − (1) via IS avec le θ
	 * choisi. Ensuite, on genere G + (1) selon sa loi gamma originale, mais
	 * conditionnelle a ce que G + (1) ≥ G − (1) + ln(K/S(0)) − r − ω (une loi
	 * gamma tronquee). De cette maniere, le revenu observe ne sera jamais nul.
	 * Implantez cet algorithme, faites le meme type d’experience qu’en (c), et
	 * comparez les resultats. Essayez ensuite de l’ameliorer davantage en
	 * essayant d’optimiser θ empiriquement.
	 */

	/*
	 * (f) Combinez votre estimateur IS obtenu en (e) avec une methode RQMC qui
	 * utilise un “Sobol’ net” constitue des n = 2 14 premiers points de la
	 * suite de Sobol’, randomise par un “left matrix scramble” suivi d’un
	 * decalage aleatoire digital (LMScrambleShift dans SSJ). Estimez la valeur
	 * de l’option avec ce nouvel estimateur pour les valeurs de K donnees en
	 * (c), donnez un intervalle de confiance approximatif, et estimez aussi le
	 * facteur additionnel de reduction de variance obtenu en appliquant IS avec
	 * RQMC par rapport a celui ou on utilise IS seulement
	 */
}
