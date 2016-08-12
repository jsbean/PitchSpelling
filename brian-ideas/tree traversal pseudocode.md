> exploreTree(
>    Array<SpelledPitchClass> spcs,
>    int nodeAndEdgeCost, 
>    Array<SpelledPitchClass> solution, 
>    int thresh,
>    Array<PitchClass> pcs
> )
      
      
      // NB I'm using "cost" right now to refer to the overall cost. obviously further optimization
      
      // would involve breaking that down into non-graph- and graph-level costs and keeping the
      
      // values in memory rather than recalculating each time. it's just good for now to see when
      
      // the cost has to be calculated
      
      // "index" = stack size = number of spelled pitch classes in the current function call
      
      i <= spcs.size
      
      pc <= pcs[i]
      
      
      // if we're at the base case (i = pcs.size) and under the threshold
      
      // then set the solution to be the current graph (=spcs)
      
      if (i == pcs.size)
      
         if cost(spcs) < thresh
      
            thresh = cost(spcs)
      
            solution = copy(spcs)
      
         end if
      
         return // end here
      
      end if
      
      
      
      [otherwise...]
      
      spellings <= possibleSpellings(pc)
      
      costs <= Array<(SpelledPitchClass, int, int)>
      
      
      for each spelling in spellings
      
        calculate cost of (spcs + spelling) for nodeAndEdgeCost and graphCost
      
        costs <= (spelling, nodeAndEdgeCost, graphCost)
      
      sort costs by (nodeAndEdgeCost + graphCost)
      
      for each (spelling, nodeAndEdgeCost, graphCost) in costs
        ...
      
        [if we're still under the threshold, recurse]
        
      end
