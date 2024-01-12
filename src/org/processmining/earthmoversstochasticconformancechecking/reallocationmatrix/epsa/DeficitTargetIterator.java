package org.processmining.earthmoversstochasticconformancechecking.reallocationmatrix.epsa;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;

/**
 * A class used for iterating over possible deficit targets.
 * As iterating over the targets has to be done frequently in every iteration, possible
 * targets are stored. Thus it is not necessary to iterate over
 * the whole tree again during the second calculation of the reducing
 * costs for a new source.
 * @author brockhoff
 *
 */
public class DeficitTargetIterator implements Iterator<Integer>
{

	/**
	 * Iterator to iterate over the subtrees
	 */
	TreeIterator treeIt;
	
	/**
	 * First target index (id)
	 */
	private final int firstTar;
	/**
	 * Handle to the surplus trees
	 */
	private List<Integer> deficitTrees;
	/**
	 * Iterator of deficit Trees
	 */
	private Iterator<Integer> defTreesIt;
	/**
	 * internal array storing targets
	 */
	private int[] targets;
    /**
     * Current index in {@link #targets} where the next
     * targets key {@link #nextT} is stored
     */
	private int curArInd;
	/**
	 * Next target
	 */
	private int nextT;
	/**
	 * length of target array {@link #targets}
	 */
	private int lengthTarArray;
	/**
	 * Use tree or internal list {@link #targets}?
	 */
	private boolean useArray;

    /**
     * Instantiates a new TreeIterator from the root of a subtree
     *
     * @param root the root vertex of the subtree.
     */
    public DeficitTargetIterator(TreeIterator treeIt,
    		List<Integer> deficitTrees, int cTar, int firstTar)
    {
        this.treeIt = treeIt;
    	this.targets = new int[cTar];
        this.useArray = false;
        this.firstTar = firstTar;
        this.deficitTrees = deficitTrees;
        lengthTarArray = 0;
        
        defTreesIt = deficitTrees.iterator();

        //Existence is assured before
        treeIt.setRoot(defTreesIt.next());

        //Each subtree contains at least one target
        //Get first target in subtree
        int nTar;
        while((nTar = treeIt.next()) < firstTar) {
        	;
        }
        this.nextT = nTar;

    }
    
    /**
     * Resets the iterator for the next iteration.
     */
    public void nextIt() {
		curArInd = 0;
        this.useArray = false;
        lengthTarArray = 0;
        
        //Reset tree iterator
        defTreesIt = deficitTrees.iterator();

        //Existence is assured before
        treeIt.setRoot(defTreesIt.next());

        //Each subtree contains at least one target
        //Get first target in subtree
        int nTar;
        while((nTar = treeIt.next()) < firstTar) {
        	;
        }
        this.nextT = nTar;
    }

    /**
     * Resets the iterator for next pass on the same targets
     */
    public void resetToStart()
    {
    	if(useArray) {
    		curArInd = 0;
    		nextT = targets[curArInd];
    	}
    	else {
    		//Add remaining targets
    		while(this.hasNext()) {
    			this.next();
    		}

    		useArray = true;
    		curArInd = 0;
    		nextT = targets[curArInd];
    	}
    }

 
    @Override
    public boolean hasNext()
    {
        return nextT != -1;
    }

    @Override
    public Integer next()
    {
        int result = nextT;
        nextT = -1;

        //Get next target from array
        if(useArray) {
        	if(++curArInd < lengthTarArray) {
        		nextT = targets[curArInd];
        	}
        	else {
        		nextT = -1;
        	}
        }
        else {
        	while(treeIt.hasNext() && (nextT = treeIt.next()) < firstTar) ;

        	//No new target in the current subtree found
        	if(nextT < firstTar) {
        		//Check if there is a next subtree
        		if(!defTreesIt.hasNext()) {
        			nextT = -1;
        		}
        		//Set tree iterator on next subtree and find the first target
        		else {
        			try {
        				treeIt.setRoot(defTreesIt.next());
        			}
        			catch(ConcurrentModificationException conE) {
        				System.out.println("Why concurrent? :(");
        			}
        			
                    //Each subtree contains at least one target
                    //Get first target in subtree
        			int nTar;
        	        while((nTar = treeIt.next()) < firstTar) {
        	        	;
        	        }
        	        this.nextT = nTar;
        		}
        	}
        	targets[curArInd++] = result;
        	lengthTarArray++;
        }

        return result;
    }

    /*
     * Not supported.
     */
    @Override
    public void remove()
    {
        throw new UnsupportedOperationException("Not supported.");
    }
    
    public int defTreeCount() {
    	return deficitTrees.size();
    }

}