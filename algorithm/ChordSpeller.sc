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
			var ln = asString_ln(letterName);
			var qt = asString_qt(quarterTone);
			var et = asString_et(eighthTone);
			^"%% % (%)".format(ln, qt, et, pitchClass);
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

ChordSpeller {
	var pitchClassList;
	var maxCost;
	var bestGraphs;

	*new {
		arg pitchClassList;
		^super.newCopyArgs(pitchClassList).init;
	}

	init {
		maxCost = 1000;
		bestGraphs = List[];
	}

	spell {
		this.init;
		this.prSpellFunc([], pitchClassList, 0, 0, 0);
		"best graphs:".postln;
		bestGraphs.do {
			arg graph, i;
			postln("% - %".format(i, graph));
		};
		"------------".postln;
	}

	prSpellFunc {
		arg spellingGraph, pitchClassList, totalCost, nodeEdgeCost, depth;
		var addCosts = List[];
		var nextPitchClass;
		var nextSpellings;

		// base case
		if(depth == pitchClassList) {
			case
			{totalCost < maxCost} {
				maxCost = totalCost;
				bestGraphs = [spellingGraph.deepCopy];
			}
			{totalCost == maxCost} {
				bestGraphs = bestGraphs.add(spellingGraph.deepCopy);
			};
			^nil;
		};

		// get possible spellings for the next-deeper level
		nextPitchClass = pitchClassList[depth];
		nextSpellings = possibleSpellings(nextPitchClass);

		nextSpellings.do {
			arg spelling;
			var addedNEC, graphCost, newTotalCost;

			// calculate the cost of adding this node
			// to avoid duplicating computations, we calculate this in three components
			// 1. the graph cost for the new graph
			// 2. the added node and edge costs for the new node and its edges
			// 3. the total cost (previous n/e costs + new n/e costs + graph cost)
			addedNEC = addedNodeEdgeCost(spellingGraph, spelling);
			spellingGraph = spellingGraph.add(spelling);
			graphCost = graphCost(spellingGraph);
			newTotalCost = nodeEdgeCost + graphCost + addedNEC;

			if(newTotalCost <= maxCost) {
				// only add a spelling if
				addCosts = addCosts.add((
					spelling:spelling,
					newTotalCost:newTotalCost,
					addedNEC:addedNEC));
			};

			spellingGraph.removeLast;
		};
		// sort the potential costs by increasing total cost
		addCosts = addCosts.sortBy(\newTotalCost);

		// recurse on each possible spelling with a cost <= maxCost
		// added if block because 'for' will count (0, -1) if addCosts is empty
		if(addCosts.size.isEmpty.not) {
			for(0, addCosts.size-1) {
				arg i;
				var spelling = addCosts[i].spelling;
				var newTotalCost = addCosts[i].newTotalCost;
				var addedNEC = addCosts[i].addedNEC;

				// we need to check this again because the maxCost may decrease during recursion
				if(newTotalCost <= maxCost) {
					var newNodeEdgeCost = nodeEdgeCost + addedNEC;
					spellingGraph = spellingGraph.add(spelling); // push
					this.prSpellFunc(spellingGraph, pitchClassList, newTotalCost, newNodeEdgeCost, depth+1);
					spellingGraph.removeLast; // pop
				};
			};
		};
	}

	*possibleSpellings {
		arg pc;
		// TODO: stub method
		// returns an array of SpelledPitchClass's
		^[];
	}

	*addedNodeEdgeCost {
		arg graph, node;
		// TODO: stub method
		^0;
	}

	*graphCost {
		arg graph;
		// TODO: stub method
		^0;
	}


}
