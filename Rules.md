# General definition

Rules can be applied at the [**node-**](#node-level), [**edge-**](#edge-level), or [**graph-**](#graph-level)level:

- **Node-level rules**:
  - Applied to single `SpelledPitchClass` values
  - e.g., "avoid double flats/sharps"
- **Edge-level rules**:
  - Applied to all pairs `SpelledPitchClass` values in the graph
  - e.g., "avoid augmented unisons"
- **Graph-level rules**:
  - Applied to entire graph
  - Does not apply "double jeopardy" — penalizes only once if there are one or more instances of rule-breaking
  - e.g., "avoid eighth tone arrow direction conflicts"
  
Rules return a cost value, [0,1], which is later scaled by the program to allow rule weighting and user-controlled preferences.

<a id="node-level"></a>
# Node-level rules: 
`(SpelledPitchClass) -> Float`

__notation: RNx(A)__

###R<sub>n1</sub>: avoid double sharps or flats
* 1 if Aq is bb or x
* 0 otherwise

###R<sub>n2</sub>: avoid bad enharmonics
* 1 if (Al, Aq) = B# or Cb or E# or Fb
* 0 otherwise

_maybe this should use b, db, and bb instead of just b--this would overlap with other rules but maybe this is a good thing?_

###RN3: avoid combining quarter tones and eighth tones
* 1 if Aq is a quarter tone and Ae is not 0
* 0 otherwise

###RN4: avoid three-quarter-tone symbols
* 1 if Aq is db or #t
* 0 otherwise

<a id="edge-level"></a>
# Edge-level rules :
`(SpelledPitchClass) -> (SpelledPitchClass) -> Float`

__notation: REx(A,B)__

###RE1: avoid unisons (all unisons are augmented since we use a set of unique elements going into the problem)
* 1 if Al = Bl
* 0 otherwise

###RE2: avoid crossovers (Cb and B# for instance)
* 1 if ?
* 0 otherwise

_not sure how to implement this one, but I think you have to include checks on the quarter-tone directions_

###RE3: avoid augmented/diminished intervals
* 0.2 * the degree of augmentation/diminution of the interval (A,B)
* 0 if (A,B) is not agumented or diminished

<a id="graph-level"></a>
# Graph-level rules
`[SpelledPitchClass] -> Float`

__notation: RGx(S)__

###RG1: avoid rough spellings in opposite directions
* 1 if for any A, B in S, sign(Aq) * sign(Bq) = -1
* 0 otherwise

###RG2: avoid eighth tone direction conflict
* 1 if for any A, B in S, sign(Ae) * sign(Be) = -1
* 0 otherwise

