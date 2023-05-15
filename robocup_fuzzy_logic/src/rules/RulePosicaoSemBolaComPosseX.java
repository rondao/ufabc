package rules;

import nrc.fuzzy.FuzzyRule;
import nrc.fuzzy.FuzzyValue;
import nrc.fuzzy.FuzzyValueVector;
import nrc.fuzzy.FuzzyVariable;
import nrc.fuzzy.TriangleFuzzySet;

public class RulePosicaoSemBolaComPosseX {
	private FuzzyVariable posicao;
	private FuzzyRule[] rules;
	private FuzzyValue[] inputsFuzzy;

	public RulePosicaoSemBolaComPosseX(double a, double b, double c, double d, double e) throws Exception {
		// Step 1 (define the FuzzyVariables for posicao)

		// Variavel Posicao
		posicao = new FuzzyVariable("distancia", a-b, e+b, "metros");
		posicao.addTerm("mtoDefesa", new TriangleFuzzySet(a-b, a, b));
		posicao.addTerm("defesa", new TriangleFuzzySet(a, b, c));
		posicao.addTerm("centro", new TriangleFuzzySet(b, c, d));
		posicao.addTerm("ataque", new TriangleFuzzySet(c, d, e));
		posicao.addTerm("mtoAtaque", new TriangleFuzzySet(d, e, e+b));
		
		defineRules();
	}

	public void defineRules() throws Exception {
		// Step 2 (define our rule)
		rules = new FuzzyRule[25];

		rules[0] = new FuzzyRule();
		rules[0].addAntecedent(new FuzzyValue(posicao, "mtoDefesa"));
		rules[0].addAntecedent(new FuzzyValue(posicao, "mtoDefesa"));
		rules[0].addConclusion(new FuzzyValue(posicao, "mtoDefesa"));
		rules[1] = new FuzzyRule();
		rules[1].addAntecedent(new FuzzyValue(posicao, "mtoDefesa"));
		rules[1].addAntecedent(new FuzzyValue(posicao, "defesa"));
		rules[1].addConclusion(new FuzzyValue(posicao, "mtoDefesa"));
		rules[2] = new FuzzyRule();
		rules[2].addAntecedent(new FuzzyValue(posicao, "mtoDefesa"));
		rules[2].addAntecedent(new FuzzyValue(posicao, "centro"));
		rules[2].addConclusion(new FuzzyValue(posicao, "defesa"));
		rules[3] = new FuzzyRule();
		rules[3].addAntecedent(new FuzzyValue(posicao, "mtoDefesa"));
		rules[3].addAntecedent(new FuzzyValue(posicao, "ataque"));
		rules[3].addConclusion(new FuzzyValue(posicao, "defesa"));
		rules[4] = new FuzzyRule();
		rules[4].addAntecedent(new FuzzyValue(posicao, "mtoDefesa"));
		rules[4].addAntecedent(new FuzzyValue(posicao, "mtoAtaque"));
		rules[4].addConclusion(new FuzzyValue(posicao, "defesa"));
		rules[5] = new FuzzyRule();
		rules[5].addAntecedent(new FuzzyValue(posicao, "defesa"));
		rules[5].addAntecedent(new FuzzyValue(posicao, "mtoDefesa"));
		rules[5].addConclusion(new FuzzyValue(posicao, "defesa"));
		rules[6] = new FuzzyRule();
		rules[6].addAntecedent(new FuzzyValue(posicao, "defesa"));
		rules[6].addAntecedent(new FuzzyValue(posicao, "defesa"));
		rules[6].addConclusion(new FuzzyValue(posicao, "defesa"));
		rules[7] = new FuzzyRule();
		rules[7].addAntecedent(new FuzzyValue(posicao, "defesa"));
		rules[7].addAntecedent(new FuzzyValue(posicao, "centro"));
		rules[7].addConclusion(new FuzzyValue(posicao, "defesa"));
		rules[8] = new FuzzyRule();
		rules[8].addAntecedent(new FuzzyValue(posicao, "defesa"));
		rules[8].addAntecedent(new FuzzyValue(posicao, "ataque"));
		rules[8].addConclusion(new FuzzyValue(posicao, "centro"));
		rules[9] = new FuzzyRule();
		rules[9].addAntecedent(new FuzzyValue(posicao, "defesa"));
		rules[9].addAntecedent(new FuzzyValue(posicao, "mtoAtaque"));
		rules[9].addConclusion(new FuzzyValue(posicao, "centro"));
		rules[10] = new FuzzyRule();
		rules[10].addAntecedent(new FuzzyValue(posicao, "centro"));
		rules[10].addAntecedent(new FuzzyValue(posicao, "mtoDefesa"));
		rules[10].addConclusion(new FuzzyValue(posicao, "defesa"));
		rules[11] = new FuzzyRule();
		rules[11].addAntecedent(new FuzzyValue(posicao, "centro"));
		rules[11].addAntecedent(new FuzzyValue(posicao, "defesa"));
		rules[11].addConclusion(new FuzzyValue(posicao, "defesa"));
		rules[12] = new FuzzyRule();
		rules[12].addAntecedent(new FuzzyValue(posicao, "centro"));
		rules[12].addAntecedent(new FuzzyValue(posicao, "centro"));
		rules[12].addConclusion(new FuzzyValue(posicao, "centro"));
		rules[13] = new FuzzyRule();
		rules[13].addAntecedent(new FuzzyValue(posicao, "centro"));
		rules[13].addAntecedent(new FuzzyValue(posicao, "ataque"));
		rules[13].addConclusion(new FuzzyValue(posicao, "ataque"));
		rules[14] = new FuzzyRule();
		rules[14].addAntecedent(new FuzzyValue(posicao, "centro"));
		rules[14].addAntecedent(new FuzzyValue(posicao, "mtoAtaque"));
		rules[14].addConclusion(new FuzzyValue(posicao, "ataque"));
		rules[15] = new FuzzyRule();
		rules[15].addAntecedent(new FuzzyValue(posicao, "ataque"));
		rules[15].addAntecedent(new FuzzyValue(posicao, "mtoDefesa"));
		rules[15].addConclusion(new FuzzyValue(posicao, "centro"));
		rules[16] = new FuzzyRule();
		rules[16].addAntecedent(new FuzzyValue(posicao, "ataque"));
		rules[16].addAntecedent(new FuzzyValue(posicao, "defesa"));
		rules[16].addConclusion(new FuzzyValue(posicao, "centro"));
		rules[17] = new FuzzyRule();
		rules[17].addAntecedent(new FuzzyValue(posicao, "ataque"));
		rules[17].addAntecedent(new FuzzyValue(posicao, "centro"));
		rules[17].addConclusion(new FuzzyValue(posicao, "ataque"));
		rules[18] = new FuzzyRule();
		rules[18].addAntecedent(new FuzzyValue(posicao, "ataque"));
		rules[18].addAntecedent(new FuzzyValue(posicao, "ataque"));
		rules[18].addConclusion(new FuzzyValue(posicao, "ataque"));
		rules[19] = new FuzzyRule();
		rules[19].addAntecedent(new FuzzyValue(posicao, "ataque"));
		rules[19].addAntecedent(new FuzzyValue(posicao, "mtoAtaque"));
		rules[19].addConclusion(new FuzzyValue(posicao, "ataque"));
		rules[20] = new FuzzyRule();
		rules[20].addAntecedent(new FuzzyValue(posicao, "mtoAtaque"));
		rules[20].addAntecedent(new FuzzyValue(posicao, "mtoDefesa"));
		rules[20].addConclusion(new FuzzyValue(posicao, "ataque"));
		rules[21] = new FuzzyRule();
		rules[21].addAntecedent(new FuzzyValue(posicao, "mtoAtaque"));
		rules[21].addAntecedent(new FuzzyValue(posicao, "defesa"));
		rules[21].addConclusion(new FuzzyValue(posicao, "ataque"));
		rules[22] = new FuzzyRule();
		rules[22].addAntecedent(new FuzzyValue(posicao, "mtoAtaque"));
		rules[22].addAntecedent(new FuzzyValue(posicao, "centro"));
		rules[22].addConclusion(new FuzzyValue(posicao, "ataque"));
		rules[23] = new FuzzyRule();
		rules[23].addAntecedent(new FuzzyValue(posicao, "mtoAtaque"));
		rules[23].addAntecedent(new FuzzyValue(posicao, "ataque"));
		rules[23].addConclusion(new FuzzyValue(posicao, "mtoAtaque"));
		rules[24] = new FuzzyRule();
		rules[24].addAntecedent(new FuzzyValue(posicao, "mtoAtaque"));
		rules[24].addAntecedent(new FuzzyValue(posicao, "mtoAtaque"));
		rules[24].addConclusion(new FuzzyValue(posicao, "mtoAtaque"));
	}

	public void setInput(double[] inputsCrisp) throws Exception {
		// Step 3 (provide the fuzzified inputs for the rule)
		// create fuzzy values from the crisp values
		inputsFuzzy = new FuzzyValue[inputsCrisp.length];

		FuzzyValue.setConfineFuzzySetsToUOD(true);
		for (int i = 0; i < inputsCrisp.length; i++) {
			inputsFuzzy[i] = new FuzzyValue(posicao, new TriangleFuzzySet(
					inputsCrisp[i] - 0.05, inputsCrisp[i],
					inputsCrisp[i] + 0.05));
		}
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
