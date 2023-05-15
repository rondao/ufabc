package rules;

import nrc.fuzzy.*;

public class RuleChuteGol {
	private FuzzyVariable posGoleiro;
	private FuzzyVariable posJogador;
	private FuzzyVariable posChute;
	private FuzzyRule[] rules;
	private FuzzyValue[] inputsFuzzy;

	public RuleChuteGol() throws Exception {
		// Step 1 (define the FuzzyVariables)

		// Variavel posGoleiro
		posGoleiro = new FuzzyVariable("posicao no gol", -32, 32, "metros");
		posGoleiro.addTerm("mtoDireita", new TrapezoidFuzzySet(-32, -32, -28, -20));
		posGoleiro.addTerm("direita", new TrapezoidFuzzySet(-28, -20, -12, -4));
		posGoleiro.addTerm("centro", new TrapezoidFuzzySet(-12, -4, 4, 12));
		posGoleiro.addTerm("esquerda", new TrapezoidFuzzySet(4, 12, 20, 28));
		posGoleiro.addTerm("mtoEsquerda", new TrapezoidFuzzySet(20, 28, 32, 32));
		
		// Variavel posJogador
		posJogador = new FuzzyVariable("posicao no gol", -128, 128, "metros");
		posJogador.addTerm("mtoDireita", new TrapezoidFuzzySet(-128, -128, -112, -80));
		posJogador.addTerm("direita", new TrapezoidFuzzySet(-112, -80, -48, -16));
		posJogador.addTerm("centro", new TrapezoidFuzzySet(-48, -16, 16, 48));
		posJogador.addTerm("esquerda", new TrapezoidFuzzySet(16, 48, 80, 112));
		posJogador.addTerm("mtoEsquerda", new TrapezoidFuzzySet(80, 112, 128, 128));
		
		// Variavel posChute
		posChute = new FuzzyVariable("posicao no gol", -32, 32, "metros");
		posChute.addTerm("mtoDireita", new TrapezoidFuzzySet(-32, -32, -28, -20));
		posChute.addTerm("direita", new TrapezoidFuzzySet(-28, -20, -12, -4));
		posChute.addTerm("centro", new TrapezoidFuzzySet(-12, -4, 4, 12));
		posChute.addTerm("esquerda", new TrapezoidFuzzySet(4, 12, 20, 28));
		posChute.addTerm("mtoEsquerda", new TrapezoidFuzzySet(20, 28, 32, 32));
		
		defineRules();
	}

	public void defineRules() throws Exception {
		// Step 2 (define our rule)
		rules = new FuzzyRule[25];

		rules[0] = new FuzzyRule();
		rules[0].addAntecedent(new FuzzyValue(posGoleiro, "mtoEsquerda"));
		rules[0].addAntecedent(new FuzzyValue(posJogador, "mtoEsquerda"));
		rules[0].addConclusion(new FuzzyValue(posChute, "direita"));
		rules[1] = new FuzzyRule();
		rules[1].addAntecedent(new FuzzyValue(posGoleiro, "mtoEsquerda"));
		rules[1].addAntecedent(new FuzzyValue(posJogador, "esquerda"));
		rules[1].addConclusion(new FuzzyValue(posChute, "direita"));
		rules[2] = new FuzzyRule();
		rules[2].addAntecedent(new FuzzyValue(posGoleiro, "mtoEsquerda"));
		rules[2].addAntecedent(new FuzzyValue(posJogador, "centro"));
		rules[2].addConclusion(new FuzzyValue(posChute, "direita"));
		rules[3] = new FuzzyRule();
		rules[3].addAntecedent(new FuzzyValue(posGoleiro, "mtoEsquerda"));
		rules[3].addAntecedent(new FuzzyValue(posJogador, "direita"));
		rules[3].addConclusion(new FuzzyValue(posChute, "direita"));
		rules[4] = new FuzzyRule();
		rules[4].addAntecedent(new FuzzyValue(posGoleiro, "mtoEsquerda"));
		rules[4].addAntecedent(new FuzzyValue(posJogador, "mtoDireita"));
		rules[4].addConclusion(new FuzzyValue(posChute, "centro"));
		rules[5] = new FuzzyRule();
		rules[5].addAntecedent(new FuzzyValue(posGoleiro, "esquerda"));
		rules[5].addAntecedent(new FuzzyValue(posJogador, "mtoEsquerda"));
		rules[5].addConclusion(new FuzzyValue(posChute, "mtoDireita"));
		rules[6] = new FuzzyRule();
		rules[6].addAntecedent(new FuzzyValue(posGoleiro, "esquerda"));
		rules[6].addAntecedent(new FuzzyValue(posJogador, "esquerda"));
		rules[6].addConclusion(new FuzzyValue(posChute, "mtoDireita"));
		rules[7] = new FuzzyRule();
		rules[7].addAntecedent(new FuzzyValue(posGoleiro, "esquerda"));
		rules[7].addAntecedent(new FuzzyValue(posJogador, "centro"));
		rules[7].addConclusion(new FuzzyValue(posChute, "direita"));
		rules[8] = new FuzzyRule();
		rules[8].addAntecedent(new FuzzyValue(posGoleiro, "esquerda"));
		rules[8].addAntecedent(new FuzzyValue(posJogador, "direita"));
		rules[8].addConclusion(new FuzzyValue(posChute, "direita"));
		rules[9] = new FuzzyRule();
		rules[9].addAntecedent(new FuzzyValue(posGoleiro, "esquerda"));
		rules[9].addAntecedent(new FuzzyValue(posJogador, "mtoDireita"));
		rules[9].addConclusion(new FuzzyValue(posChute, "direita"));
		rules[10] = new FuzzyRule();
		rules[10].addAntecedent(new FuzzyValue(posGoleiro, "centro"));
		rules[10].addAntecedent(new FuzzyValue(posJogador, "mtoEsquerda"));
		rules[10].addConclusion(new FuzzyValue(posChute, "mtoEsquerda"));
		rules[11] = new FuzzyRule();
		rules[11].addAntecedent(new FuzzyValue(posGoleiro, "centro"));
		rules[11].addAntecedent(new FuzzyValue(posJogador, "esquerda"));
		rules[11].addConclusion(new FuzzyValue(posChute, "mtoEsquerda"));
		rules[12] = new FuzzyRule();
		rules[12].addAntecedent(new FuzzyValue(posGoleiro, "centro"));
		rules[12].addAntecedent(new FuzzyValue(posJogador, "centro"));
		rules[12].addConclusion(new FuzzyValue(posChute, "mtoEsquerda"));
		rules[13] = new FuzzyRule();
		rules[13].addAntecedent(new FuzzyValue(posGoleiro, "centro"));
		rules[13].addAntecedent(new FuzzyValue(posJogador, "direita"));
		rules[13].addConclusion(new FuzzyValue(posChute, "mtoDireita"));
		rules[14] = new FuzzyRule();
		rules[14].addAntecedent(new FuzzyValue(posGoleiro, "centro"));
		rules[14].addAntecedent(new FuzzyValue(posJogador, "mtoDireita"));
		rules[14].addConclusion(new FuzzyValue(posChute, "mtoDireita"));
		rules[15] = new FuzzyRule();
		rules[15].addAntecedent(new FuzzyValue(posGoleiro, "direita"));
		rules[15].addAntecedent(new FuzzyValue(posJogador, "mtoEsquerda"));
		rules[15].addConclusion(new FuzzyValue(posChute, "esquerda"));
		rules[16] = new FuzzyRule();
		rules[16].addAntecedent(new FuzzyValue(posGoleiro, "direita"));
		rules[16].addAntecedent(new FuzzyValue(posJogador, "esquerda"));
		rules[16].addConclusion(new FuzzyValue(posChute, "esquerda"));
		rules[17] = new FuzzyRule();
		rules[17].addAntecedent(new FuzzyValue(posGoleiro, "direita"));
		rules[17].addAntecedent(new FuzzyValue(posJogador, "centro"));
		rules[17].addConclusion(new FuzzyValue(posChute, "esquerda"));
		rules[18] = new FuzzyRule();
		rules[18].addAntecedent(new FuzzyValue(posGoleiro, "direita"));
		rules[18].addAntecedent(new FuzzyValue(posJogador, "direita"));
		rules[18].addConclusion(new FuzzyValue(posChute, "mtoEsquerda"));
		rules[19] = new FuzzyRule();
		rules[19].addAntecedent(new FuzzyValue(posGoleiro, "direita"));
		rules[19].addAntecedent(new FuzzyValue(posJogador, "mtoDireita"));
		rules[19].addConclusion(new FuzzyValue(posChute, "mtoEsquerda"));
		rules[20] = new FuzzyRule();
		rules[20].addAntecedent(new FuzzyValue(posGoleiro, "mtoDireita"));
		rules[20].addAntecedent(new FuzzyValue(posJogador, "mtoEsquerda"));
		rules[20].addConclusion(new FuzzyValue(posChute, "centro"));
		rules[21] = new FuzzyRule();
		rules[21].addAntecedent(new FuzzyValue(posGoleiro, "mtoDireita"));
		rules[21].addAntecedent(new FuzzyValue(posJogador, "esquerda"));
		rules[21].addConclusion(new FuzzyValue(posChute, "esquerda"));
		rules[22] = new FuzzyRule();
		rules[22].addAntecedent(new FuzzyValue(posGoleiro, "mtoDireita"));
		rules[22].addAntecedent(new FuzzyValue(posJogador, "centro"));
		rules[22].addConclusion(new FuzzyValue(posChute, "esquerda"));
		rules[23] = new FuzzyRule();
		rules[23].addAntecedent(new FuzzyValue(posGoleiro, "mtoDireita"));
		rules[23].addAntecedent(new FuzzyValue(posJogador, "direita"));
		rules[23].addConclusion(new FuzzyValue(posChute, "esquerda"));
		rules[24] = new FuzzyRule();
		rules[24].addAntecedent(new FuzzyValue(posGoleiro, "mtoDireita"));
		rules[24].addAntecedent(new FuzzyValue(posJogador, "mtoDireita"));
		rules[24].addConclusion(new FuzzyValue(posChute, "esquerda"));
	}

	public void setInput(double[] inputsCrisp) throws Exception {
		// Step 3 (provide the fuzzified inputs for the rule)
		// create fuzzy values from the crisp values
		inputsFuzzy = new FuzzyValue[inputsCrisp.length];

		FuzzyValue.setConfineFuzzySetsToUOD(true);
		inputsFuzzy[0] = new FuzzyValue(posGoleiro, new TriangleFuzzySet(
				inputsCrisp[0] - 0.05, inputsCrisp[0],
				inputsCrisp[0] + 0.05));
		inputsFuzzy[1] = new FuzzyValue(posJogador, new TriangleFuzzySet(
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
		return (outputFuzzy == null) ? -10.0 : outputFuzzy.momentDefuzzify();
	}
}
