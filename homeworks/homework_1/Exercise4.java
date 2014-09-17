/*
  Exercise 4
  Le but de cet exercice est de vous familiariser avec la librairie SSJ et son utilisation. On
  vous demande d’ ́ecrire une classe TandemQueue permettant de simuler le syst`eme de files en
  tandem de l’exemple 1.7 des notes, avec “production blocking”, en utilisant les r ́ecurrences
  donn ́ees. Pour simplifier, vous pouvez supposer que les dur ́ees inter-arriv ́es A i suivent une loi
  exponentielle de taux λ (moyenne 1/λ), que les dur ́es de service a ` la station j suivent une loi
  exponentielle de taux μ j , et que toutes ces variables al ́eatoires sont ind ́ependantes. (Dans la
  vraie vie il faudra bien sˆ
  ur faire une analyse statistique des donn ́ees pour identifier des lois de
  probabilit ́e appropri ́ees et estimer leurs param`etres.)
  Votre classe devra avoir un constructeur qui prend en entr ́ee les param`etres du mod`ele, soit m,
  λ, le vecteur des μ j , et le vecteur des c j . Il y aura aussi une m ́ethode simulateFixedNumber
  qui simule un nombre fixe N c the clients et une m ́ethode simulateFixedTime qui simule le
  syst`eme pour un horizon de temps T fix ́e (apr`es T , il n’y a plus d’arrivees, mais tous les
  clients d ́ej`a arriv ́es doivent poursuivre leur progression dans le syst`eme et comptent dans les
  statistiques). Dans les deux cas, on suppose que le syst`eme est initialement vide. Chacune
  de ces m ́ethodes prendra en entr ́ee deux RandomStream’s, l’un qui servira `a g ́en ́erer les A i et
  l’autre pour les S i,j . Ces m ́ethodes prendront aussi en param`etres des collecteurs statistiqus
  de type Tally pour recueillir le temps total d’attente et le temps total de blocage a ` chaque
  station j.
  Pour tester cette classe et faire vos exp ́eriences, vous invoquerez le constructeur et les m ́ethodes
  de votre classe TandemQueue a ` partir d’une autre classe (s ́epar ́ee, dans un autre fichier). L’id ́ee
  est qu’une fois le “simulateur” TandemQueue implant ́e et test ́e, on ne doit plus jamais modifier
  son code lors de son utilisation. On n’utilise que les m ́ethodes de son interface.
  On vous demande ensuite de faire les exp ́eriences suivantes avec votre simulateur. Construisez
  un mod`ele avec λ = 1, m = 3, μ 1 = 1.5, μ 2 = μ 3 = 1.2, c 1 = ∞, c 2 = 4, et c 3 = 8.
  Simulez ce mod`ele pour un horizon de temps T = 1000 et calculez l’attente totale et le temps
  total de blocage a ` chacune des trois stations (sauf a ` la derni`ere, o`
  u il n’y a pas de blocage).
  Notez que ce ne sont pas les mˆemes mesures que dans l’algorithme des notes. R ́ep ́etez cette
  simulation n = 1000 fois, puis calculez la valeur estim ́ee et un intervalle de confiance `a
  95% pour l’esp ́erance de chacune des 5 quantit ́es calcul ́es (temps total d’attente et temps de
  blocage `a chaque station). Faites aussi tracer des histogrammes des 1000 valeurs observ ́es
  pour chacune de ces 5 quantit ́es. Discutez ce que vous observez; par exemple o`
  u observe-t-on
  davantage d’attente ou de blocage?
  Important: vous devez bien expliquer et documenter vos programmes. Pour cela, vous pouvez
  prendre exemple sur la documentation des classes de SSJ.

*/