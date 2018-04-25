package bwapiMath.graph;

import java.util.HashMap;

/**
 * IInstanceMapper.java --- Interface defining a mapping function for generating
 * a HashMap containing instances mapped to unique indices.
 * 
 * @author P H - 08.03.2018
 *
 */
public interface IInstanceMapper<T> {

	/**
	 * Function used for mapping instances (of the currently played map) to an
	 * index / Integer. This HashMap functions as a key for the graph that is
	 * being created since the graph uses Integers and not objects as its
	 * vertices representation. Therefore a way of "encoding" and "decoding" the
	 * information is necessary.
	 * 
	 * @return a HashMap with each T instance of the map mapped to an Integer.
	 */
	public HashMap<T, Integer> map();
}
