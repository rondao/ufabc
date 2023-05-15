package rules;

import nrc.fuzzy.*;

public class RulePosicaoSemBolaSemPosse {

	private FuzzyVariable posicao;
	private FuzzyRule[] rules;
	private FuzzyValue[] inputsFuzzy;

	public RulePosicaoSemBolaSemPosse(double a, double b, double c, double d, double e) throws Exception {
		// Step 1 (define the FuzzyVariables for posicao)

		// Variavel Posicao
		posicao = new FuzzyVariable("distancia", a-b, e+b, "metros");
		posicao.addTerm("mtoEsquerda", new TriangleFuzzySet(a-b, a, b));
		posicao.addTerm("esquerda", new TriangleFuzzySet(a, b, c));
		posicao.addTerm("centro", new TriangleFuzzySet(b, c, d));
		posicao.addTerm("direita", new TriangleFuzzySet(c, d, e));
		posicao.addTerm("mtoDireita", new TriangleFuzzySet(d, e, e+b));
		
		defineRules();
	}

	public void defineRules() throws Exception {
		// Step 2 (define our rule)
		// SE (PosicaoInicial) e (PosicaoBola) ENTAO (PosicaoJogador)
		rules = new FuzzyRule[25];

		rules[0] = new FuzzyRule();
		rules[0].addAntecedent(new FuzzyValue(posicao, "mtoEsquerda"));
		rules[0].addAntecedent(new FuzzyValue(posicao, "mtoEsquerda"));
		rules[0].addConclusion(new FuzzyValue(posicao, "mtoEsquerda"));
		rules[1] = new FuzzyRule();
		rules[1].addAntecedent(new FuzzyValue(posicao, "mtoEsquerda"));
		rules[1].addAntecedent(new FuzzyValue(posicao, "esquerda"));
		rules[1].addConclusion(new FuzzyValue(posicao, "esquerda"));
		rules[2] = new FuzzyRule();
		rules[2].addAntecedent(new FuzzyValue(posicao, "mtoEsquerda"));
		rules[2].addAntecedent(new FuzzyValue(posicao, "centro"));
		rules[2].addConclusion(new FuzzyValue(posicao, "esquerda"));
		rules[3] = new FuzzyRule();
		rules[3].addAntecedent(new FuzzyValue(posicao, "mtoEsquerda"));
		rules[3].addAntecedent(new FuzzyValue(posicao, "direita"));
		rules[3].addConclusion(new FuzzyValue(posicao, "esquerda"));
		rules[4] = new FuzzyRule();
		rules[4].addAntecedent(new FuzzyValue(posicao, "mtoEsquerda"));
		rules[4].addAntecedent(new FuzzyValue(posicao, "mtoDireita"));
		rules[4].addConclusion(new FuzzyValue(posicao, "mtoEsquerda"));
		rules[5] = new FuzzyRule();
		rules[5].addAntecedent(new FuzzyValue(posicao, "esquerda"));
		rules[5].addAntecedent(new FuzzyValue(posicao, "mtoEsquerda"));
		rules[5].addConclusion(new FuzzyValue(posicao, "mtoEsquerda"));
		rules[6] = new FuzzyRule();
		rules[6].addAntecedent(new FuzzyValue(posicao, "esquerda"));
		rules[6].addAntecedent(new FuzzyValue(posicao, "esquerda"));
		rules[6].addConclusion(new FuzzyValue(posicao, "esquerda"));
		rules[7] = new FuzzyRule();
		rules[7].addAntecedent(new FuzzyValue(posicao, "esquerda"));
		rules[7].addAntecedent(new FuzzyValue(posicao, "centro"));
		rules[7].addConclusion(new FuzzyValue(posicao, "centro"));
		rules[8] = new FuzzyRule();
		rules[8].addAntecedent(new FuzzyValue(posicao, "esquerda"));
		rules[8].addAntecedent(new FuzzyValue(posicao, "direita"));
		rules[8].addConclusion(new FuzzyValue(posicao, "esquerda"));
		rules[9] = new FuzzyRule();
		rules[9].addAntecedent(new FuzzyValue(posicao, "esquerda"));
		rules[9].addAntecedent(new FuzzyValue(posicao, "mtoDireita"));
		rules[9].addConclusion(new FuzzyValue(posicao, "esquerda"));
		rules[10] = new FuzzyRule();
		rules[10].addAntecedent(new FuzzyValue(posicao, "centro"));
		rules[10].addAntecedent(new FuzzyValue(posicao, "mtoEsquerda"));
		rules[10].addConclusion(new FuzzyValue(posicao, "esquerda"));
		rules[11] = new FuzzyRule();
		rules[11].addAntecedent(new FuzzyValue(posicao, "centro"));
		rules[11].addAntecedent(new FuzzyValue(posicao, "esquerda"));
		rules[11].addConclusion(new FuzzyValue(posicao, "esquerda"));
		rules[12] = new FuzzyRule();
		rules[12].addAntecedent(new FuzzyValue(posicao, "centro"));
		rules[12].addAntecedent(new FuzzyValue(posicao, "centro"));
		rules[12].addConclusion(new FuzzyValue(posicao, "centro"));
		rules[13] = new FuzzyRule();
		rules[13].addAntecedent(new FuzzyValue(posicao, "centro"));
		rules[13].addAntecedent(new FuzzyValue(posicao, "direita"));
		rules[13].addConclusion(new FuzzyValue(posicao, "direita"));
		rules[14] = new FuzzyRule();
		rules[14].addAntecedent(new FuzzyValue(posicao, "centro"));
		rules[14].addAntecedent(new FuzzyValue(posicao, "mtoDireita"));
		rules[14].addConclusion(new FuzzyValue(posicao, "direita"));
		rules[15] = new FuzzyRule();
		rules[15].addAntecedent(new FuzzyValue(posicao, "direita"));
		rules[15].addAntecedent(new FuzzyValue(posicao, "mtoEsquerda"));
		rules[15].addConclusion(new FuzzyValue(posicao, "direita"));
		rules[16] = new FuzzyRule();
		rules[16].addAntecedent(new FuzzyValue(posicao, "direita"));
		rules[16].addAntecedent(new FuzzyValue(posicao, "esquerda"));
		rules[16].addConclusion(new FuzzyValue(posicao, "direita"));
		rules[17] = new FuzzyRule();
		rules[17].addAntecedent(new FuzzyValue(posicao, "direita"));
		rules[17].addAntecedent(new FuzzyValue(posicao, "centro"));
		rules[17].addConclusion(new FuzzyValue(posicao, "centro"));
		rules[18] = new FuzzyRule();
		rules[18].addAntecedent(new FuzzyValue(posicao, "direita"));
		rules[18].addAntecedent(new FuzzyValue(posicao, "direita"));
		rules[18].addConclusion(new FuzzyValue(posicao, "direita"));
		rules[19] = new FuzzyRule();
		rules[19].addAntecedent(new FuzzyValue(posicao, "direita"));
		rules[19].addAntecedent(new FuzzyValue(posicao, "mtoDireita"));
		rules[19].addConclusion(new FuzzyValue(posicao, "mtoDireita"));
		rules[20] = new FuzzyRule();
		rules[20].addAntecedent(new FuzzyValue(posicao, "mtoDireita"));
		rules[20].addAntecedent(new FuzzyValue(posicao, "mtoEsquerda"));
		rules[20].addConclusion(new FuzzyValue(posicao, "mtoDireita"));
		rules[21] = new FuzzyRule();
		rules[21].addAntecedent(new FuzzyValue(posicao, "mtoDireita"));
		rules[21].addAntecedent(new FuzzyValue(posicao, "esquerda"));
		rules[21].addConclusion(new FuzzyValue(posicao, "direita"));
		rules[22] = new FuzzyRule();
		rules[22].addAntecedent(new FuzzyValue(posicao, "mtoDireita"));
		rules[22].addAntecedent(new FuzzyValue(posicao, "centro"));
		rules[22].addConclusion(new FuzzyValue(posicao, "direita"));
		rules[23] = new FuzzyRule();
		rules[23].addAntecedent(new FuzzyValue(posicao, "mtoDireita"));
		rules[23].addAntecedent(new FuzzyValue(posicao, "direita"));
		rules[23].addConclusion(new FuzzyValue(posicao, "direita"));
		rules[24] = new FuzzyRule();
		rules[24].addAntecedent(new FuzzyValue(posicao, "mtoDireita"));
		rules[24].addAntecedent(new FuzzyValue(posicao, "mtoDireita"));
		rules[24].addConclusion(new FuzzyValue(posicao, "mtoDireita"));
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
