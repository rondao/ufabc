package rules;

import nrc.fuzzy.*;

public class RulePassar {
	private FuzzyVariable distancia;
	private FuzzyVariable pressao;
	private FuzzyVariable possibilidade;
	private FuzzyRule[] rules;
	private FuzzyValue[] inputsFuzzy;

	public RulePassar() throws Exception {
		// Step 1 (define the FuzzyVariables)

		// Variavel distancia
		distancia = new FuzzyVariable("distancia", 0, 38, "metros");
		distancia.addTerm("perto", new TrapezoidFuzzySet(0, 0, 5, 12));
		distancia.addTerm("pertoMedio", new TriangleFuzzySet(6, 10, 16));
		distancia.addTerm("medioLonge", new TriangleFuzzySet(12, 16, 20));
		distancia.addTerm("longe", new TrapezoidFuzzySet(18, 20, 38, 38));
		// Variavel pressao
		pressao = new FuzzyVariable("pressao", 0, 11, "pessoas no raio");
		pressao.addTerm("baixa", new TriangleFuzzySet(0, 0, 2));
		pressao.addTerm("mediaBaixa", new TriangleFuzzySet(1, 2, 3));
		pressao.addTerm("mediaAlta", new TriangleFuzzySet(2, 3, 4));
		pressao.addTerm("alta", new TrapezoidFuzzySet(3, 4, 11, 11));
		
		// Variavel possibilidade
		possibilidade = new FuzzyVariable("possibilidade", 0, 10, "");
		possibilidade.addTerm("baixa", new TriangleFuzzySet(0, 0, 3.3));
		possibilidade.addTerm("mediaBaixa", new TriangleFuzzySet(0, 3.3, 6.6));
		possibilidade.addTerm("mediaAlta", new TriangleFuzzySet(3.3, 6.6, 10));
		possibilidade.addTerm("alta", new TriangleFuzzySet(6.6, 10, 10));
		
		defineRules();
	}

	public void defineRules() throws Exception {
		// Step 2 (define our rule)
		rules = new FuzzyRule[16];

		rules[0] = new FuzzyRule();
		rules[0].addAntecedent(new FuzzyValue(distancia, "perto"));
		rules[0].addAntecedent(new FuzzyValue(pressao, "baixa"));
		rules[0].addConclusion(new FuzzyValue(possibilidade, "mediaAlta"));
		rules[1] = new FuzzyRule();
		rules[1].addAntecedent(new FuzzyValue(distancia, "perto"));
		rules[1].addAntecedent(new FuzzyValue(pressao, "mediaBaixa"));
		rules[1].addConclusion(new FuzzyValue(possibilidade, "mediaBaixa"));
		rules[2] = new FuzzyRule();
		rules[2].addAntecedent(new FuzzyValue(distancia, "perto"));
		rules[2].addAntecedent(new FuzzyValue(pressao, "mediaAlta"));
		rules[2].addConclusion(new FuzzyValue(possibilidade, "baixa"));
		rules[3] = new FuzzyRule();
		rules[3].addAntecedent(new FuzzyValue(distancia, "perto"));
		rules[3].addAntecedent(new FuzzyValue(pressao, "alta"));
		rules[3].addConclusion(new FuzzyValue(possibilidade, "baixa"));
		rules[4] = new FuzzyRule();
		rules[4].addAntecedent(new FuzzyValue(distancia, "pertoMedio"));
		rules[4].addAntecedent(new FuzzyValue(pressao, "baixa"));
		rules[4].addConclusion(new FuzzyValue(possibilidade, "alta"));
		rules[5] = new FuzzyRule();
		rules[5].addAntecedent(new FuzzyValue(distancia, "pertoMedio"));
		rules[5].addAntecedent(new FuzzyValue(pressao, "mediaBaixa"));
		rules[5].addConclusion(new FuzzyValue(possibilidade, "alta"));
		rules[6] = new FuzzyRule();
		rules[6].addAntecedent(new FuzzyValue(distancia, "pertoMedio"));
		rules[6].addAntecedent(new FuzzyValue(pressao, "mediaAlta"));
		rules[6].addConclusion(new FuzzyValue(possibilidade, "mediaBaixa"));
		rules[7] = new FuzzyRule();
		rules[7].addAntecedent(new FuzzyValue(distancia, "pertoMedio"));
		rules[7].addAntecedent(new FuzzyValue(pressao, "alta"));
		rules[7].addConclusion(new FuzzyValue(possibilidade, "baixa"));
		rules[8] = new FuzzyRule();
		rules[8].addAntecedent(new FuzzyValue(distancia, "medioLonge"));
		rules[8].addAntecedent(new FuzzyValue(pressao, "baixa"));
		rules[8].addConclusion(new FuzzyValue(possibilidade, "alta"));
		rules[9] = new FuzzyRule();
		rules[9].addAntecedent(new FuzzyValue(distancia, "medioLonge"));
		rules[9].addAntecedent(new FuzzyValue(pressao, "mediaBaixa"));
		rules[9].addConclusion(new FuzzyValue(possibilidade, "mediaAlta"));
		rules[10] = new FuzzyRule();
		rules[10].addAntecedent(new FuzzyValue(distancia, "medioLonge"));
		rules[10].addAntecedent(new FuzzyValue(pressao, "mediaAlta"));
		rules[10].addConclusion(new FuzzyValue(possibilidade, "mediaBaixa"));
		rules[11] = new FuzzyRule();
		rules[11].addAntecedent(new FuzzyValue(distancia, "medioLonge"));
		rules[11].addAntecedent(new FuzzyValue(pressao, "alta"));
		rules[11].addConclusion(new FuzzyValue(possibilidade, "baixa"));
		rules[12] = new FuzzyRule();
		rules[12].addAntecedent(new FuzzyValue(distancia, "longe"));
		rules[12].addAntecedent(new FuzzyValue(pressao, "baixa"));
		rules[12].addConclusion(new FuzzyValue(possibilidade, "mediaAlta"));
		rules[13] = new FuzzyRule();
		rules[13].addAntecedent(new FuzzyValue(distancia, "longe"));
		rules[13].addAntecedent(new FuzzyValue(pressao, "mediaBaixa"));
		rules[13].addConclusion(new FuzzyValue(possibilidade, "mediaBaixa"));
		rules[14] = new FuzzyRule();
		rules[14].addAntecedent(new FuzzyValue(distancia, "longe"));
		rules[14].addAntecedent(new FuzzyValue(pressao, "mediaAlta"));
		rules[14].addConclusion(new FuzzyValue(possibilidade, "baixa"));
		rules[15] = new FuzzyRule();
		rules[15].addAntecedent(new FuzzyValue(distancia, "longe"));
		rules[15].addAntecedent(new FuzzyValue(pressao, "alta"));
		rules[15].addConclusion(new FuzzyValue(possibilidade, "baixa"));
	}

	public void setInput(double[] inputsCrisp) throws Exception {
		// Step 3 (provide the fuzzified inputs for the rule)
		// create fuzzy values from the crisp values
		inputsFuzzy = new FuzzyValue[inputsCrisp.length];

		FuzzyValue.setConfineFuzzySetsToUOD(true);
		inputsFuzzy[0] = new FuzzyValue(distancia, new TriangleFuzzySet(
				inputsCrisp[0] - 0.05, inputsCrisp[0],
				inputsCrisp[0] + 0.05));
		inputsFuzzy[1] = new FuzzyValue(pressao, new TriangleFuzzySet(
				inputsCrisp[1] - 0.05, inputsCrisp[1],
				inputsCrisp[1] + 0.05));
		FuzzyValue.setConfineFuzzySetsToUOD(false);
	}

	public double inference() throws Exception {
		// Step 4 (execute the rule with these inputs)
		FuzzyValueVector fvv;
		FuzzyValue outputFuzzy = null;
		
		for (FuzzyRule rule : rules) {
			// remove any inputs associated with the rule, then add the new
			// inputs to the rule
			rule.removeAllInputs();
			for (FuzzyValue FV : inputsFuzzy) {
				rule.addInput(FV);
			}

			if (rule.testRuleMatching()) {
				// fire the rule, the result of firing is a vector of
				// FuzzyValues that represent the outputs
				fvv = rule.execute();

				// if it isnt the first output, it needs to be united with the
				// previous output
				if (outputFuzzy == null) {
					outputFuzzy = fvv.fuzzyValueAt(0);
				} else {
					outputFuzzy = outputFuzzy.fuzzyUnion(fvv.fuzzyValueAt(0));
				}
			}
		}

		// Step 5 (defuzzify the outputs to get crisp values)
		// calculate the deffuzified value
		return (outputFuzzy == null) ? -1.0 : outputFuzzy.momentDefuzzify();
	}
}
