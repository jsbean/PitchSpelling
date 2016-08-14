#data structure

##SpelledPitchClass = (n, l, q, e)

* n: pitch-class number
  * float mod 12
* l: note letter 
  * C - B => int mod 7
* q: quarter-tone spelling
  * __rough tuning__
  * bb db b d 0 t # #t x => int mod 9 or [-4, +4]
* e: eighth-tone arrow 
  * __fine tuning__
  * dn 0 up => int mod 3 or [-1, 1]
  
The rough vs fine tuning parameters could be generalized, but let's solve one problem at a time.

#Interval naming algorithm (formalized)

###normalizeInputs :: SpelledPitchClass -> SpelledPitchClass -> (SpelledPitchClass, SpelledPitchClass)


for a SpelledPitchClass X, let Xn, Xl, Xq, Xe be the pc-num, note letter, q-tone spelling, and e-tone spelling respectively of X
let A and B be two SpelledPitchClasses

if Bl - Al mod 7 > Al - Bl mod 7
  return (B, A)
else
  return (A, B)
end


###nameInterval :: SpelledPitchClass -> SpelledPitchClass -> (Ordinal, Quality)


let *A* and *B* be two SpelledPitchClasses
then let the SpelledPitchClass pair (*A'*, *B'*) be *normalizeInputs(A, B)*


let the ordered set *O* be *(unison, second, third, fourth)*

define the ordinal-to-interval-class mapping, the function *M*(x), as
  0 -> 0
  1 -> 1.5
  2 -> 3.5
  3 -> 5
where x is an integer and 0 <= x <= 3
  
define the perfect interval test, the function *P*(x), as
  0 -> 1
  1 -> 0
  2 -> 0
  3 -> 1
where x is an integer and 0 <= x <= 3
  
define the function *N*(x, y, z) as
  (y - x - z + 18) mod 12 - 6
where x and y are pitch classes and z is an interval class (0 <= x, y < 12, 0 <= z < 6)
  
define the quality assignment function, *Q*(x, y), as
  if y = 0
    __neutral__ if x is in (-0.5, 0.5)
    __major__ if x is in [0.5, 1.5)
    __minor__ if x is in (-1.5, -0.5]
    __augmented__ if x >= 1.5
    __diminished__ if x <= -1.5
  if y = 1
    __perfect__ if x is in (-0.5, 0.5)
    __augmented__ if x >= 0.5
    __diminished__ if x <= -0.5
where x is in [-6, 6) and y is 0 or 1

define the diminished/augmented ordinal function, *ADO*(x), as
  2 -> __double__
  3 -> __triple__
  4 -> __quadruple__
  5 -> __quintuple__
  and null for all other values
where x is an integer and 0 <= x <= 5
  
THEN
let *delta_l* = *B'l* - *A'l* mod 7
let *o* be the element of *O* at index *delta_l*
let *m* be *M*(*delta_l*)
let *delta_n*, the difference between the actual interval class and its 'perfect' or 'neutral' size according to its ordinal, be N(A'n, B'n, m)
let *delta_n'* be |*delta_n*| if *delta_l* = 0 and *delta_n* otherwise
let *q* be the concatenation of *ADO*(floor|*n*|) and *Q*(*delta_n'*, *P*(*delta_l*))

then the result is (*o*, *q*)

