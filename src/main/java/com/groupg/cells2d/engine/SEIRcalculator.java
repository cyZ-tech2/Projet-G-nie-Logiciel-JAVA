package com.groupg.cells2d.engine;

import com.groupg.cells2d.model.board.SEIRData;

/**
 * formules beta (β) gamma (γ) sigma (σ)
 * << Les 4 compartiments du modèle SEIR
 * S — Susceptible (population saine exposable)
 * dS/dt = - β × S × I / N
 * E — Exposed (exposé, en incubation, pas encore contagieux)
 * dE/dt = + β × S × I / N - σ × E
 * I — Infected (infecté ET contagieux)
 * dI/dt = + σ × E - γ × I
 * R — Recovered (guéri ou décédé, hors circulation)
 * dR/dt = + γ × I
 * modèle SEIR spatialisé ou métapopulation.
 * Concrètement ça veut dire qu'à chaque pas, une fraction de la population d'un arrondissement "visite" ses voisins et peut transmettre ou recevoir l'infection.
 * Le paramètre s'appelle généralement mobilityRate (ou mu dans la littérature). Il représente la proportion de la population qui se déplace vers les arrondissements voisins à chaque pas.
 * Ce que ça change dans ton architecture :
 * SimulationParams gagne un attribut mobilityRate : double entre 0 et 1. Zéro = personne ne bouge, les arrondissements évoluent indépendamment. Un = tout le monde se déplace en permanence, ce qui n'a pas de sens épidémiologiquement. En pratique on reste entre 0.01 et 0.15.
 * La formule de dS/dt pour un district donné devient :
 * dS/dt = - β × S × I / N
 *         - mobilityRate × S        (gens qui partent)
 *         + mobilityRate × Σ S_voisin / nb_voisins   (gens qui arrivent)
 * La même logique s'applique à E, I et R.>>
 */

public class SEIRcalculator {
    public static SEIRData compute(SEIRData seirData, double beta, double sigma, double gamma, double mortalityRate, double propagationRate, double avgNeighborInfected, int population){
        double s=seirData.getSusceptible();
        double e=seirData.getExposed();
        double i=seirData.getInfected();
        double r=seirData.getRecovered();
        double d=seirData.getDead();

        if(population==0){return seirData;}

        double dS=((-beta*s*i)/population)-(propagationRate*s)+(propagationRate*avgNeighborInfected);
        double dE=(beta*s*i)/population-sigma*e+(propagationRate*avgNeighborInfected*beta);
        double dI=sigma*e-gamma*i;
        double dR=(1-mortalityRate)*gamma*i;
        double dD=mortalityRate*gamma*i;

        return new SEIRData(s+dS, e+dE, i+dI, r+dR,d+dD);
    }
}
