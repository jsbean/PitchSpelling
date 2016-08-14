#general definition

rules can be either node-, edge-, or graph-level

* node-level rules
  * applied to single SpelledPitchClasses
  * ex. "avoid double flats/sharps"
  * can probably be pre-calculated to avoid repeated computation
* edge-level rules
  * applied to all SpelledPitchClass-pairs in the graph
  *ex. "avoid augmented unisons"
  * could be pre-calculated for large problems (only 189*190/2 = 17955 combinations)
* graph-level rules
  *applied to entire graph, way to avoid "double jeopardy" and implement costs that only care whether at least one instance of a thing exists
  *ex. "avoid eighth tone arrow direction conflicts"
  
rules return a cost value, [0,1], which is later scaled by the program to allow rule weighting and user-controlled preferences
  
#node-level rules : SpelledPitchClass -> float

notation: RNx(A)

###RN1: avoid double sharps or flats
1 if Aq is bb or x
0 otherwise

###RN2: avoid bad enharmonics
1 if (Al, Aq) = B# or Cb or E# or Fb
0 otherwise

_maybe this should use b, db, and bb instead of just b--this would overlap with other rules but maybe this is a good thing?_

###RN3: avoid combining quarter tones and eighth tones
1 if Aq is a quarter tone and Ae is not 0
0 otherwise

###RN4: avoid three-quarter-tone symbols
1 if Aq is db or #t
0 otherwise

#edge-level rules : SpelledPitchClass -> SpelledPitchClass -> float

notation: REx(A,B)

###RE1: avoid unisons (all unisons are augmented since we use a set of unique elements going into the problem)
1 if Al = Bl
0 otherwise

###RE2: avoid crossovers (Cb and B# for instance)
1 if ?
0 otherwise

_not sure how to implement this one, but I think you have to include checks on the quarter-tone directions_

###RE3: avoid augmented/diminished intervals
0.2 * the degree of augmentation/diminution of the interval (A,B)
0 if (A,B) is not agumented or diminished

#graph-level rules : [SpelledPitchClass] -> float

notation: RGx(S)

###RG1: avoid rough spellings in opposite directions
1 if for any A, B in S, sign(Aq) * sign(Bq) = -1
0 otherwise

###RG2: avoid eighth tone direction conflict
1 if for any A, B in S, sign(Ae) * sign(Be) = -1
0 otherwise

