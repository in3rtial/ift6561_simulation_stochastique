package homework_3;

import umontreal.iro.lecuyer.probdist.*;
import umontreal.iro.lecuyer.rng.*;
import umontreal.iro.lecuyer.stat.*;


/*
 * Ceci est une variante de l’exercice 2.43 des notes, ou le mouvement Brownien est remplace
par un processus VG dans le GBM. On veut estimer la valeur d’une option asiatique dont
les param`etres sont K = 101 (le prix d’exercice), T = 1 (horizon d’un an), and ζ j = j/16
for j = 1, . . . , 16 (les d = 16 points d’observation).
Le processus VG est d ́efini par Y (t) = X(G(t)) o`
u le subordinateur G est un processus
gamma de param`etres 1 et ν = 0.3 et X est un mouvement Brownien de param`tres μ =
0.1436 et σ = 0.12136. Le taux d’int ́erˆet est r = 0.1 (10%). Le sous-jacent (asset price) S(t)
 ́evolue exactement comme dans l’exemple 6.52 des notes. SSJ offre des outils pour simuler
ce genre de processus.
 ́
Ecrivez
un programme permettant d’estimer la valeur de cette option en simulant le processus
VG selon chacune des trois m ́ethodes mentionn ́es dans l’exemple 6.52: BGSS, BGBS, et
DGBS. Utilisez votre programme pour estimer la valeur par Monte Carlo avec n = 10 6
replications. Dans chaque cas, calculer un intervalle de confiance `a 95% sur cette valeur.
 */

public class Exercise7 {

}
