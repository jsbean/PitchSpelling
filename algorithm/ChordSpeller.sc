SpelledPitchClass {
	var <>pitchClass, <>letterName, <>quarterTone, <>eighthTone;
	classvar letterNames, quarterTones, eighthTones;

	*new {
		arg pitchClass, letterName, quarterTone, eighthTone;
		^super.newCopyArgs(pitchClass, letterName, quarterTone, eighthTone);
	}

	asString {
		if(letterName.isNil || quarterTone.isNil || eighthTone.isNil) {
			^"unspelled %".format(pitchClass);
		} {
			var ln = SpelledPitchClass.asString_ln(letterName);
			var qt = SpelledPitchClass.asString_qt(quarterTone);
			var et = SpelledPitchClass.asString_et(eighthTone);
			^"%% % (%)".format(ln, qt, et, pitchClass).replace("  ", " ");
		}
	}

	// string conversion for letter names
	*asString_ln {
		arg letterName;
		^switch(letterName)
		{0} {"C"}
		{1} {"D"}
		{2} {"E"}
		{3} {"F"}
		{4} {"G"}
		{5} {"A"}
		{6} {"B"}
		{Error("invalid letter name: %".format(letterName)).throw};
	}

	// string conversion for quarter tones
	*asString_qt {
		arg quarterTone;
		^switch(quarterTone)
		{-4} {"bb"}
		{-3} {"db"}
		{-2} {"b"}
		{-1} {"d"}
		{0} {""}
		{1} {"t"}
		{2} {"#"}
		{3} {"#t"}
		{4} {"x"}
		{Error("invalid quarter tone: %".format(quarterTone)).throw};
	}

	// string conversion for eighth tones
	*asString_et {
		arg eighthTone;
		^switch(eighthTone)
		{-1} {"dn"}
		{0} {""}
		{1} {"up"}
		{Error("invalid eighth tone: %".format(eighthTone)).throw};
	}

}

ChordSpellerRule {
	*type {^thisMethod.shouldNotImplement}
	*apply {^thisMethod.shouldNotImplement}
	*prApplyRule {^thisMethod.shouldNotImplement}
}

ChordSpellerNodeRule : ChordSpellerRule {
	*type {^\node}
	*prApplyRule {
		arg node;
		^thisMethod.shouldNotImplement;
	}
}

ChordSpellerEdgeRule : ChordSpellerRule {
	*type {^\edge}
	*prApplyRule {
		arg edge;
		^thisMethod.shouldNotImplement;
	}
}

ChordSpellerGraphRule : ChordSpellerRule {
	*type {^\graph}
	*prApplyRule {
		arg graph;
		^thisMethod.shouldNotImplement;
	}
}

// node rules
ChordSpellerNodeRule_DoubleSharpFlat : ChordSpellerNodeRule {
	classvar <>costMul = 1;
	*prApplyRule {arg node; ^if(node.quarterTone.abs == 4) {1} {0}}
	*apply {arg in; ^this.prApplyRule(in) * costMul;}
}

ChordSpellerNodeRule_BadEnharmonic : ChordSpellerNodeRule {
	classvar <>costMul = 1;
	*apply {arg in; ^this.prApplyRule(in) * costMul;}
	*prApplyRule {
		arg node;
		^switch(node.quarterTone)
		{2} {
			((node.letterName == 2) || (node.letterName == 6)).binaryValue;
		}
		{-2} {
			((node.letterName == 3) || (node.letterName == 0)).binaryValue;
		}
		{0}
	}
}

ChordSpellerNodeRule_ThreeQuarterSharpFlat : ChordSpellerNodeRule {
	classvar <>costMul = 1;
	*apply {arg in; ^this.prApplyRule(in) * costMul;}
	*prApplyRule {arg node; ^if(node.quarterTone.abs == 3) {1} {0}}
}

ChordSpellerNodeRule_QuarterAndEighth : ChordSpellerNodeRule {
	classvar <>costMul = 1;
	*apply {arg in; ^this.prApplyRule(in) * costMul;}
	*prApplyRule {arg node; ^if(node.quarterTone.odd && node.eighthTone.odd) {1} {0}}
}

ChordSpellerEdgeRule_Unison : ChordSpellerEdgeRule {
	classvar <>costMul = 1;
	*apply {arg in; ^this.prApplyRule(in) * costMul;}
	*prApplyRule {
		arg nodes;
		var lhs = nodes[0];
		var rhs = nodes[1];
		^(lhs.letterName == rhs.letterName).binaryValue;
	}
}

ChordSpellerEdgeRule_AugDim : ChordSpellerEdgeRule {
	classvar <>costMul = 1;
	*apply {arg in; ^this.prApplyRule(in) * costMul;}
	*prApplyRule {
		arg nodes;
		var lhs, rhs;
		var dl, m, n, q;
		nodes = this.switchFunc(nodes);
		lhs = nodes[0];
		rhs = nodes[1];


		dl = (rhs.letterName - lhs.letterName) % 7;
		m = [0, 1.5, 3.5, 5]@dl;
		n = (rhs.pitchClass - m - lhs.pitchClass) + 6 % 12 - 6;
		n = n.abs;
		if(m % 1 != 0) {n = excess(n, 0.5)};
		^n;
	}

	*switchFunc {
		arg in;
		var lhs = in[0];
		var rhs = in[1];
		^if((rhs.letterName - lhs.letterName % 7) > (lhs.letterName - rhs.letterName % 7))
		{[rhs, lhs]}
		{[lhs, rhs]}
	}
}

ChordSpellerEdgeRule_Crossover : ChordSpellerEdgeRule {
	classvar <>costMul = 1;
	*apply {arg in; ^this.prApplyRule(in) * costMul;}
	*prApplyRule {
		arg nodes;
		var lhs, rhs, qtdir, pcross, res;
		nodes = ChordSpellerEdgeRule_AugDim.switchFunc(nodes);
		lhs = nodes[0];
		rhs = nodes[1];

		qtdir = (rhs.quarterTone - lhs.quarterTone).sign; // should be negative
		pcross = (rhs.pitchClass - lhs.pitchClass % 12) >= 6; // should be true

		res = (qtdir == (-1)) && (pcross) && (lhs.letterName != rhs.letterName);
		res = res.binaryValue;
		// if((res == 1)) {"% %\n".postf(lhs, rhs)};
		// ^res;
		^res;
	}
}

ChordSpellerGraphRule_FlatsSharps : ChordSpellerGraphRule {
	classvar <>costMul = 1;
	*apply {arg in; ^this.prApplyRule(in) * costMul;}
	*prApplyRule {
		arg graph;
		var prevqt = 0;
		graph.do {
			|node, i|
			if(node.quarterTone != 0) {
				if(prevqt == 0) {
					prevqt = node.quarterTone.sign
				} {
					if(node.quarterTone.sign != prevqt) {^1}
				}
			}
		};
		^0
	}
}

ChordSpellerGraphRule_UpDown : ChordSpellerGraphRule {
	classvar <>costMul = 1;
	*apply {arg in; ^this.prApplyRule(in) * costMul;}
	*prApplyRule {
		arg graph;
		var prevet = 0;
		graph.do {
			|node, i|
			if(node.eighthTone != 0) {
				if(prevet == 0) {
					prevet = node.eighthTone.sign
				} {
					if(node.eighthTone.sign != prevet) {^1}
				}
			}
		};
		^0
	}
}

ChordSpeller {
	var pitchClassList;
	var maxCost;
	var bestGraphs;
	var <>bDebug = false;
	var <>iDebugDepth = 5;

	classvar spellingsList;

	*new {
		arg pitchClassList;
		^super.newCopyArgs(pitchClassList).init;
	}

	init {
		maxCost = 1000;
		bestGraphs = List[];
		ChordSpellerNodeRule_DoubleSharpFlat.costMul_(100);
		ChordSpellerNodeRule_BadEnharmonic.costMul_(10);
		ChordSpellerEdgeRule_Unison.costMul_(2);
		ChordSpellerEdgeRule_AugDim.costMul_(4);
		ChordSpellerEdgeRule_Crossover.costMul_(1000);

	}

	spell {
		this.init;
		this.prSpellFunc([], pitchClassList, 0, 0, 0);
		"best graphs (cost %):".format(maxCost).postln;
		bestGraphs.do {
			arg graph, i;
			postln("% - %".format(i+1, graph.collect(_.asString)));
		};
		"------------".postln;
	}

	prSpellFunc {
		arg spellingGraph, pitchClassList, totalCost, nodeEdgeCost, depth;
		var addCosts = List[];
		var nextPitchClass;
		var nextSpellings;

		// base case
		if(depth == pitchClassList.size) {
			case
			{totalCost < maxCost} {
				maxCost = totalCost;
				bestGraphs = [spellingGraph.deepCopy];
				this.debugOut("bestGraphs size: % | Cmax: %\n".format(bestGraphs.size, maxCost));
			}
			{totalCost == maxCost} {
				bestGraphs = bestGraphs.add(spellingGraph.deepCopy);
				this.debugOut("bestGraphs size: %\n".format(bestGraphs.size));
			};
			^nil;
		};

		// get possible spellings for the next-deeper level
		nextPitchClass = pitchClassList[depth];
		nextSpellings = ChordSpeller.possibleSpellings(nextPitchClass);

		nextSpellings.do {
			arg spelling;
			var addedNEC, graphCost, newTotalCost;

			// calculate the cost of adding this node
			// to avoid duplicating computations, we calculate this in three components
			// 1. the graph cost for the new graph
			// 2. the added node and edge costs for the new node and its edges
			// 3. the total cost (previous n/e costs + new n/e costs + graph cost)
			addedNEC = ChordSpeller.addedNodeEdgeCost(spellingGraph, spelling);
			spellingGraph = spellingGraph.add(spelling);
			graphCost = ChordSpeller.graphCost(spellingGraph);
			newTotalCost = nodeEdgeCost + graphCost + addedNEC;

			if(newTotalCost <= maxCost) {
				// only add a spelling if
				addCosts = addCosts.add((
					spelling:spelling,
					newTotalCost:newTotalCost,
					addedNEC:addedNEC));
			};

			spellingGraph.pop;
		};
		// sort the potential costs by increasing total cost
		addCosts = addCosts.sortBy(\newTotalCost);

		// recurse on each possible spelling with a cost <= maxCost
		// added if block because 'for' will count (0, -1) if addCosts is empty
		if(addCosts.isEmpty.not) {
			for(0, addCosts.size-1) {
				arg i;
				var spelling = addCosts[i].spelling;
				var newTotalCost = addCosts[i].newTotalCost;
				var addedNEC = addCosts[i].addedNEC;

				// we need to check this again because the maxCost may decrease during recursion
				if(newTotalCost <= maxCost) {
					var newNodeEdgeCost = nodeEdgeCost + addedNEC;
					spellingGraph = spellingGraph.add(spelling); // push
					if(depth <= iDebugDepth) {
						this.debugOut("depth: %\t | spelling: %\t | cost: %\n".format(depth, spelling, newTotalCost));
					};
					this.prSpellFunc(spellingGraph, pitchClassList, newTotalCost, newNodeEdgeCost, depth+1);
					spellingGraph.pop; // pop
				};
			};
		};
	}

	debugOut {
		arg string;
		if(bDebug) {string.post};
	}

	*possibleSpellings {
		arg pc;
		if(spellingsList.isNil) {
			// populate list of complete possible spellings
			// cycle through every combination and register a spelled pitch class
			spellingsList = List[];
			for(0,6) {
				|l|
				for(-4,4) {
					|q|
					for(-1,1) {
						|e|
						var base = [0,2,4,5,7,9,11]@l;
						var qt = q / 2;
						var et = e / 4;
						spellingsList = spellingsList.add(SpelledPitchClass(base+qt+et mod:12, l, q, e));
					}
				}
			};

			// sort by PC
			spellingsList = spellingsList.sort({|a,b| a.pitchClass < b.pitchClass});

			// separate into arrays by common PC
			spellingsList = spellingsList.separate({|a,b| b.pitchClass - a.pitchClass > 0});
		};

		// index = pc * 4 (0.25 -> 1, 0.5 -> 2)
		^spellingsList[pc*4];
	}

	*addedNodeEdgeCost {
		arg graph, node;

		var cost = 0;

		ChordSpellerNodeRule.subclasses.do {
			|class|
			cost = cost + class.apply(node);
		};
		ChordSpellerEdgeRule.subclasses.do {
			|class|
			graph.do {
				|node2|
				cost = cost + class.apply([node, node2]);
			}
		};

		^cost
	}

	*graphCost {
		arg graph;
		var cost = 0;

		ChordSpellerGraphRule.subclasses.do {
			|class|
			cost = cost + class.apply(graph);
		};

		^cost
	}


}
