# how i understand the problem.

### general solution:

1. process inputs
2. assign costs to graphs (explore output space)
3. select lowest-cost graph

### strategy for verticalities:

1. generate intervals from input pcs
2. sort intervals by decreasing expected loss variance (james's interval sorting thing)
3. find low-cost graphs through recursive tree traversal
4. select lowest-cost graph

# longer-term ideas

* find rules by trying out dyads, triads, etc (verticalities)
* then see how dyad to dyad changes things and could point toward adaptation of the framework from vertical to 2D
* wait until the end to fine-tune the optimizations


# other thoughts
* there are 189 different spellings possible (7 letter names * 9 qt symbols * 3 et states) [175 if you don't allow x-up or bb-down]
* this means it's computationally reasonable to calculate node cost rules on every spelling (assuming cost is a 16-bit int, that's less than 1 kb memory + a few additions/multiplications for the indexing function)
* it might also be computationally reasonable to calculate edge cost rules on every possible edge
  * this would be an upper triangular matrix (189x189) with unique elements (189 * 190) / 2 = **17955**
  * it could be easiliy stored in memory as 1D or 2D array
