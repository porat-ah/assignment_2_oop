package gameClient;

import java.util.*;

import api.*;

public class Pokemon {
    public static HashMap<CL_Pokemon,Pokemon> pokemon_map = new HashMap<>();
    private  CL_Pokemon pokemon;
    private Queue<pok_pri> q ;

    public Pokemon(CL_Pokemon pokemon) {
        this.pokemon = pokemon;
        pokemon_map.put(pokemon,this);
        q = new PriorityQueue<>( Comparator.comparing(pok_pri :: getDist));
    }
    public CL_Pokemon getPokemon(){
        CL_Pokemon p;
        while(!q.isEmpty()){
            p = q.poll().getPokemon();
            if (!p.isTarget())
                return p;
        }
        return null;
    }
    public void setQ(dw_graph_algorithms ga, List<CL_Pokemon> pokemons){
       // ga.one_to_all(pokemon.get_edge().getDest());
        directed_weighted_graph g = ga.getGraph();
        double dist;
        for (CL_Pokemon pok:pokemons) {
            dist = g.getNode(pok.get_edge().getSrc()).getWeight();
            if (dist >=0)
                q.add(new pok_pri(pok,dist));
        }
    }
    private class pok_pri{
        private CL_Pokemon pokemon;
        private double dist;
        public pok_pri(CL_Pokemon pokemon, double dist) {
            this.pokemon = pokemon;
            this.dist = dist;
        }
        public CL_Pokemon getPokemon() {
            return pokemon;
        }

        public double getDist() {
            return dist;
        }
    }
}