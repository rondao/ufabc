/*  MicroJava Parser (HM 06-12-28)
================
 */
package MJ;

import MJ.SymTab.*;
import MJ.CodeGen.*;
import java.util.*;

public class Parser {

    public static final int // token codes
            none = 0,
            ident = 1, number = 2, charCon = 3, plus = 4, minus = 5,
            times = 6,
            slash = 7, rem = 8, eql = 9, neq = 10, lss = 11,
            leq = 12,
            gtr = 13, geq = 14, assign = 15, semicolon = 16,
            comma = 17,
            period = 18, lpar = 19, rpar = 20, lbrack = 21,
            rbrack = 22,
            lbrace = 23, rbrace = 24, class_ = 25, else_ = 26,
            final_ = 27,
            if_ = 28, new_ = 29, print_ = 30, program_ = 31,
            read_ = 32,
            return_ = 33, void_ = 34, while_ = 35, eof = 36;
    private static final String[] name = { // token names for error messages
        "none", "identifier", "number", "char constant", "+", "-", "*", "/", "%",
        "==", "!=", "<", "<=", ">", ">=", "=", ";", ",", ".", "(", ")",
        "[", "]", "{", "}", "class", "else", "final", "if", "new", "print",
        "program", "read", "return", "void", "while", "eof"};
    private static Token t; // current token (recently recognized)
    private static Token la; // lookahead token
    private static int sym; // always contains la.kind
    public static int errors; // error counter
    private static int errDist; // no. of correctly recognized tokens since last
    // error
    @SuppressWarnings("unused")
	private static BitSet exprStart, statStart, statSeqFollow, declStart,
            declFollow, syncTokens;
    // Guarda o metodo sendo analisado.
    // Como nao ha declaracao de metodo dentro de outro metodo, 
    // esta abordagem e segura.
    private static Obj curMethod;

    // ------------------- auxiliary methods ----------------------
    private static void scan() {
        t = la;
        la = Scanner.next();
        sym = la.kind;
        errDist++;
        
        /*
        System.out.print("line " + la.line + ", col " + la.col + ": " +
        name[sym]); if (sym == ident) System.out.print(" (" + la.string +
        ")"); if (sym == number || sym == charCon) System.out.print(" (" +
        la.val + ")"); System.out.println();
         */
    }

    /** Checa se o simbolo atual e o esperado.
     *  Retorna true se for, e false caso contrario. 
     */
    private static boolean check(int expected) {
        if (sym == expected) {
            scan();
            return true;
        } else {
            error(name[expected] + " expected");
            
            // Se o esperado e um token de sincronizacao.
            while (syncTokens.get(expected)) {
            	scan(); // Avancar ate sincronizar.
            	if (sym == expected) {
            		errDist = 0;
            		break;
            	}
            }
            
            return false;
        }
    }

    public static void error(String msg) { // syntactic error at token la
        if (errDist >= 3) {
            System.out.println("-- line " + la.line + " col " + la.col + ": "
                    + msg);
            errors++;
        }
        errDist = 0;
    }

    // -------------- parsing methods (in alphabetical order) -----------------
    /**
     * Program = "program" ident {ConstDecl | ClassDecl | VarDecl} 
     *           '{' {MethodDecl} '}'.
     */
    private static void Program() {
        // Inicializa a tabela com os tipos padroes.
        Tab.init();
        // Inicializa a geracao de codigo.
        Code.init();
        
        // Escopo acima contem apenas as palavras reservadas.
        Tab.openScope();

        check(program_);
        check(ident);

        declaracoes:
        while (true) {
            switch (sym) {
                case final_:
                    ConstDecl();
                    break;
                case class_:
                    ClassDecl();
                    break;
                case ident:
                    VarDecl();
                    break;
                default:
                    break declaracoes;
            }
        }

        check(lbrace);
        // Escopo acima sao as variaveis globais.
        Tab.openScope();

        declaracoesDeMetodos:
        while (true) {
            switch (sym) {
                case ident:
                case void_:
                    MethodDecl();
                    break;
                default:
                    break declaracoesDeMetodos;
            }
        }

        check(rbrace);
        Tab.closeScope();
        
        // Fecha o escopo do programa.
        Tab.closeScope();
    }

    /** ConstDecl = "final" Type ident "=" (number | charConst) ";". */
    private static void ConstDecl() {
        check(final_);

        Obj cnt = new Obj(Obj.Con);

        cnt.type = Type();

        if (check(ident)) {
            cnt.name = t.string;
        }

        check(assign);

        switch (sym) {
            case number:
                if (check(number)) {
                	if (cnt.type != Tab.intType)
                		error("Um Int nao pode ser atribuido ao tipo " + cnt.type.getTypeName());
                	else
                		cnt.val = t.val;
                }
                break;
            case charCon:
                if (check(charCon)) {
                	if (cnt.type != Tab.charType)
                		error("Um Char nao pode ser atribuido ao tipo " + cnt.type.getTypeName());
                	else
                		cnt.val = t.val;
                }
                break;
            default:
                error(name[sym] + " expected");
        }

        Tab.insert(cnt);

        // Se a variavel declarada for global, alocar espaco para ela.
        if (cnt.level == 0) {
        	Code.dataSize++;
        }
        
        check(semicolon);
    }

    /** VarDecl = Type ident {"," ident } ";". */
    private static void VarDecl() {
        Obj var = new Obj(Obj.Var);

        var.type = Type();

        if (check(ident)) {
            var.name = t.string;
            Tab.insert(var);
            
            // Se a variavel declarada for global, alocar espaco para ela.
            if (var.level == 0) {
            	Code.dataSize++;
            }
        }

        while (true) {
            if (sym == comma) {
                check(comma);
                if (check(ident)) // Kind e Type sao os mesmos do Obj anterior.
                {
                    Tab.insert(var.kind, t.string, var.type);
                    
                    // Se a variavel declarada for global, alocar espaco para ela.
                    if (var.level == 0) {
                    	Code.dataSize++;
                    }
                }
            } else {
                break;
            }
        }

        check(semicolon);
    }

    /** ClassDecl = "class" ident "{" {VarDecl} "}". */
    private static void ClassDecl() {
        check(class_);

        Obj cls = new Obj(Obj.Var);
        cls.type = new Struct(Struct.Class);

        if (check(ident)) {
            cls.name = t.string;
        }

        check(lbrace);
        Tab.openScope();

        while (true) {
            if (sym == ident) {
                VarDecl();
            } else {
                break;
            }
        }

        check(rbrace);

        // Guarda o escopo de variaveis e metodos dentro da classe.
        cls.type.nFields = Tab.curScope.nVars;
        cls.type.fields = Tab.curScope.locals;

        Tab.closeScope();

        // Insere a nova classe no escopo onde foi criado (anterior).
        Tab.insert(cls);
    }

    /** MethodDecl = (Type | "void") ident "(" [FormPars] ")" {VarDecl} Block. */
    private static void MethodDecl() {
        Obj meth = new Obj(Obj.Meth);

        if (sym == ident) { // Metodo com retorno.
            meth.type = Type();
        } else if (sym == void_) { // Metodo Void.
            check(void_);
            meth.type = Tab.voidType;
        } else {
            error(name[sym] + " expected");
        }

        if (check(ident)) {
            meth.name = t.string;
        }

        // Guarda o metodo antes de iniciar sua analise interior.
        curMethod = Tab.insert(Obj.Meth, meth.name, meth.type);
        
        check(lpar);
        // Abrindo o escopo para pegar os parametros.
        Tab.openScope();

        if (sym == ident) {
            FormPars();
        }

        check(rpar);
        // Guarda o numero de parametros.
        curMethod.nPars = Tab.curScope.nVars;

        // Analise do metodo 'main'.
        if (curMethod.name.equals("main")) {
        	// Guarda onde inicia o codigo do metodo 'main'.
        	Code.mainPc = Code.pc;
        	
        	if (curMethod.type != Tab.voidType)
        		error("O metodo 'main' dever ter retorno do tipo Void.");
        	if (curMethod.nPars != 0)
        		error("O metodo 'main' nao deve ter parametros.");
        }
        
        // Parametros e variaveis locais sao guardados juntos,
        // por isso o escopo nao fecha e re-abre.
        
        while (true) {
            if (sym == ident) {
                VarDecl();
            } else {
                break;
            }
        }

        // Geracao de codigo para o metodo corrente.
        curMethod.adr = Code.pc;
        Code.put(Code.enter);
        Code.put(curMethod.nPars);
        Code.put(Tab.curScope.nVars); // Total de espaco a ser alocado para as variaveis do metodo.

        Block();
        
        // Fechando o escopo e armazenando parametros e variaveis locais.
        curMethod.locals = Tab.curScope.locals;
        Tab.closeScope();
        
        if (curMethod.type == Tab.voidType) {
        	Code.put(Code.exit); Code.put(Code.return_);
        } else {
        	// Codigo 'trap' so sera executado se o metodo nao tiver 'return'.
        	// Caso tenha valor de retorno, o codigo 'return' sera inserido antes de 'trap'.
        	Code.put(Code.trap); Code.put(1);
        }
    }

    /** FormPars = Type ident {"," Type ident}. */
    private static void FormPars() {
        Obj var = new Obj(Obj.Var);

        var.type = Type();

        if (check(ident)) {
            var.name = t.string;
            Tab.insert(var);
        }

        while (true) {
            if (sym == comma) {
                check(comma);
                // Inicio nova variavel.
                var = new Obj(Obj.Var);
                var.type = Type();
                if (check(ident)) {
                    var.name = t.string;
                    Tab.insert(var);
                }
                // Fim nova variavel
            } else {
                break;
            }
        }
    }

    /** Type = ident ["[" "]"]. 
     *  Retorna um Struct com o tipo identificado.
     */
    private static Struct Type() {
        Struct type = new Struct();

        if (check(ident)) // Set o tipo ser for identificado.
        {
            type = Tab.find(t.string).type;
            
            if (type.kind == Struct.None)
            	error(t.kind + " nao denota um tipo valido.");
        }

        if (sym == lbrack) {
            check(lbrack);

            if (check(rbrack)) // Set o tipo do array se '[]' for identificado.
            {
                return new Struct(Struct.Arr, type); // return Type array of type
            }
        }

        return type;
    }

    /** Block = "{" {Statement} "}". */
    private static void Block() {
        check(lbrace);
        Tab.openScope();
        
        while (statStart.get(sym)) {
            Statement();
        }
        
        check(rbrace);
        Tab.closeScope();
    }

    /**
     * Statement = Designator ("=" Expr | ActPars) ";" | 
     *             "if" "(" Condition ")" Statement ["else" Statement] | 
     *             "while" "(" Condition ")" Statement |
     *             "return" [Expr] ";" | 
     *             "read" "(" Designator ")" ";" | 
     *             "print" "(" Expr ["," number] ")" ";" | 
     *             Block | 
     *             ";".
     */
    private static void Statement() {
        switch (sym) {
            case ident: // Designator ("=" Expr | ActPars) ";"
                Operand oDesig = Designator();
                switch (sym) {
                    case assign:
                        check(assign);
                        Operand oExpr = Expr();
                        
                        // Checa a atribuicao.
                        if (oExpr.type.assignableTo(oDesig.type)) {
                        	if (oDesig.kind == Operand.Con) // Se for constante, atribuicao invalida.
                        		error("Uma constante nao pode ter seu valor alterado.");
                        	else
                        		Code.assign(oDesig, oExpr); // oDesig = oExpr;
                        } else
                        	error("O tipo " + oExpr.type.getTypeName() + " nao pode ser atribuido ao tipo " + oDesig.type.getTypeName());
                        
                        break;
                    case lpar:
                    	// Primeiro deve avaliar os parametros, e carrega-los na Stack.
                    	ActPars(oDesig);
                    	// Aqui e uma chamada do metodo 'oDesig'.
                    	Code.put(Code.call); // Chamada de metodo.
                    	Code.put2(oDesig.adr); // Onde esta o metodo.
                    	// Se o metodo possui retorno, entao a execucao deve dar um 'pop' no valor.
                    	// Pois uma chamada de metodo aqui nao utiliza o retorno.
                    	if (oDesig.type != Tab.voidType)
                    		Code.put(Code.pop);
                        break;
                    default:
                        error(name[sym] + " expected");
                }
                check(semicolon);
                break;
            case if_: // "if" "(" Condition ")" Statement ["else" Statement]
                check(if_);
                
                check(lpar);
                int opI = Condition(); // Operador da condicao.
                check(rpar);
                
                // Realiza a comparacao, em caso falso ira pular para um endereco nao definido.
                Code.putFalseJump(opI, 0);
                // O endereco 0 acima precisa ser corrigido para o endereco no inicio do 'else'.
                // Se nao houver 'else', devera ir para o fim do 'if'.
                // Assim, 'adrIf' guarda este endereco, que sera setado posteriormente.
                int adrIf = Code.pc - 2;
                
                Statement();
                
                if (sym == else_) {
                	// Ao encontrar o 'else', a execucao 'true' deve pular para o fim do 'if'.
                	Code.putJump(0);
                	// O endereco 0 acima precisa ser corrigido para o endereco no final do 'if'.
                	int adrElse = Code.pc - 2;
                	
                	// Aqui e onde o primeiro Jump deve vir em caso da comparacao ser falsa.
                	Code.fixup(adrIf);
                	
                    check(else_);
                    Statement();
                    
                    // Aqui e onde a execucao 'true' deve pular. Ou seja, apos as instrucoes 'else'.
                    Code.fixup(adrElse);
                } else {
                	// Caso nao tenha 'else', a execucao 'false' deve pular todo o 'if'.
                	Code.fixup(adrIf);
                }
                
                break;
            case while_: // "while" "(" Condition ")" Statement
                check(while_);
                
                // Guarda esta posicao no codigo para retornar no inicio do 'while'.
                int top = Code.pc;
                
                check(lpar);
                int opW = Condition(); // Operador da condicao.
                check(rpar);
                
                // Realiza a comparacao, em caso falso ira pular para um endereco nao definido.
                Code.putFalseJump(opW, 0);
                // O endereco 0 acima precisa ser corrigido para o endereco no final do 'while'.
                // Assim, 'adrW' guarda este endereco, que sera setado posteriormente.
                int adrW = Code.pc - 2;
                
                Statement();
                
                // Ao terminar o 'while', retorna ao topo.
                Code.putJump(top);
                // Aqui e onde o 'while' deve pular em caso da condicao ser falsa.
                Code.fixup(adrW);
                
                break;
            case return_: // "return" [Expr] ";"
                check(return_);
                
                if (exprStart.get(sym)) { // Se ha retorno.
                    Operand oExpr = Expr();
                    
                    // Carrega o Operando de expressao que contem o retorno.
                    Code.load(oExpr);
                    
                    if (curMethod.type == Tab.voidType) // Se o metodo nao ter retorno.
                    	error("O Metodo " + curMethod.name + " possui tipo de retorno Void.");
                    else if (!oExpr.type.assignableTo(curMethod.type)) // O tipo de Expr deve ser compativel com o do retorno.
                    	error("O Metodo " + curMethod.name + " espera retorno do tipo" + curMethod.type.getTypeName());
                    	
                } else { // Se nao havia retorno.
                	if (curMethod.type != Tab.voidType)
                    	error("O Metodo " + curMethod.name + " espera retorno do tipo " + curMethod.type.getTypeName());
                }
                
                // Gerado codigo para finalizar metodo.
                Code.put(Code.exit);
                Code.put(Code.return_);
                
                check(semicolon);
                break;
            case read_: // "read" "(" Designator ")" ";"
                check(read_);
                check(lpar);
                
                Operand opdDesig = Designator();
                
                // 'read' apenas aceita Int ou Char.
                if (opdDesig.type != Tab.intType && opdDesig.type != Tab.charType)
                	error("O metodo 'read' espera um tipo Int ou Char.");
                
                // Geracao de codigo. Le o valor e guarda na variavel lida.
                Code.put(Code.read);
                Code.store(opdDesig);
                
                check(rpar);
                check(semicolon);
                break;
            case print_: // "print" "(" Expr ["," number] ")" ";"
                check(print_);
                check(lpar);
                
                Operand oExpr = Expr();
                
                // 'print' apenas aceita Int ou Char.
                if (oExpr.type != Tab.intType && oExpr.type != Tab.charType)
                	error("O metodo 'print' espera um tipo Int ou Char.");
                
                // Colocando o valor a ser impresso.
                Code.load(oExpr);
                
                if (sym == comma) {
                    check(comma);
                    check(number);
                    
                    Code.load(new Operand(t.val)); // Tamanho especificado.
                } else {
                	Code.load(new Operand(0)); // Sem especificacao.
                }

                // Gerando codigo para imprimir.
                Code.put(Code.print);
                
                check(rpar);
                check(semicolon);
                break;
            case lbrace: // Block
                Block();
                break;
            case semicolon: // ";"
                check(semicolon);
                break;
            default:
                error(name[sym] + " expected");
                break;
        }
    }

    /** ActPars = "(" [Expr {"," Expr}] ")". */
    private static void ActPars(Operand meth) {
    	if (meth.obj.kind != Obj.Meth) { // Checa se e um metodo.
        	error(meth.obj.name + " nao e um metodo.");
        	meth.obj = Tab.noObj;
    	}
    	
    	check(lpar);
    	
    	int params = 0;
    	
    	if (exprStart.get(sym)) {
    		Operand oExpr = Expr();
    		
    		// Carrega o Operando de expressao na Stack.
    		Code.load(oExpr);
    		
    		// Parametro atual.
    		Obj curParam = null;
    		
    		if (meth.obj.kind == Obj.Meth) // Checa se e um metodo.
    			curParam = meth.obj.locals;
    		
    		// Checando os tipos.
    		if (curParam != null)
    			if (!oExpr.type.assignableTo(curParam.type))
    				error("O parametro " + params + " espera um tipo " + curParam.type.getTypeName());
    		
        	while (true) {
        		params++;
        		// Avanca o parametro.
        		if (curParam != null)
        			curParam = curParam.next;
        		
            	if (sym == comma) {
                	check(comma);
                	oExpr = Expr();
                	
                	// Carrega o Operando de expressao na Stack.
            		Code.load(oExpr);
            		
                	// Checando os tipos.
            		if (curParam != null)
            			if (!oExpr.type.assignableTo(curParam.type))
            				error("O parametro " + (params+1) + " espera um tipo " + curParam.type.getTypeName());
            	} else {
                	break;
            	}
        	}
    	}
    	
        check(rpar);
        
        if (meth.obj.nPars != params) { // Checa o numero de parametros.
        	error("O metodo " + meth.obj.name + " espera " + meth.obj.nPars + " parametros.");
        }
    }

    /** Condition = Expr Relop Expr. 
     *  Retorna o operador da condicao.
     */
    private static int Condition() {
        Operand o1 = Expr();
        int op = Relop();
        Operand o2 = Expr();
        
        if (!o1.type.compatibleWith(o2.type)) // Verifica se os tipos sao compativeis.
        	error("O tipo " + o1.type.getTypeName() + " e o tipo " + o2.type.getTypeName() + " nao sao comparaveis.");
        if (o1.type.isRefType() && op != Code.eq && op != Code.ne) // Verifica se e tipo de referencia.
        	error("Tipos de referencia so podem ser comparados por Igualdade/Desigualdade");
        
        // Gerando codigo.
        Code.load(o1);
        Code.load(o2);
        
        // O operador sera carregado pela devida estrutura de controle.
        return op;
    }

    /** Relop = "==" | "!=" | ">" | ">=" | "<" | "<=". */
    private static int Relop() {
        switch (sym) {
            case eql:
                check(eql);
                return Code.eq;
            case neq:
                check(neq);
                return Code.ne;
            case lss:
                check(lss);
                return Code.lt;
            case leq:
                check(leq);
                return Code.le;
            case gtr:
                check(gtr);
                return Code.gt;
            case geq:
                check(geq);
                return Code.ge;
            default:
                error(name[sym] + " expected");
                return 0;
        }
    }

    /** Expr = ["-"] Term {Addop Term}. 
     *  Retorna um Struct com o tipo resultado da expressao.
     */
    private static Operand Expr() {
    	// Guardara se a negacao esta presente.
    	boolean neg = false;
    	// WARNING: Pode ser perigoso se 'x' nao tiver seu valor alterado.
    	Operand x = null, y;
    	
        if (sym == minus) {
            check(minus);
            // Guarda se teve o simbolo de negacao.
            neg = true;
        }

        if (exprStart.get(sym)) {
        	// O primeiro operando e o resultado de Term().
            x = Term();
            
            // Se a negacao estava presente.
            if (neg) {
            	Code.load(x); // Carrega o valor.
            	Code.put(Code.neg); // Instrucao de negacao.
            	
            	if (x.type != Tab.intType) // Apenas um inteiro pode ser negado.
            		error("O tipo " + x.type.getTypeName() + " nao pode ser negado. Um Int era esperado.");
            }

        } else {
            error(name[sym] + " expected");
        }

        termos:
        while (true) {
            switch (sym) {
                case plus:
                case minus:
                    int op = Addop();
                    
                    if (x.type != Tab.intType) { // Apenas Inteiros podem ser somados/subtraidos.
                    	error("O tipo " + x.type.getTypeName() + " nao suporta operacao de adicao/subtracao.");
                    	x.type = Tab.noType;
                    }
                    
                    // O segundo operando e o resultado do segundo Term().
                    y = Term();
                    
                    if (y.type != Tab.intType) { // Apenas Inteiros podem ser somado/subtraidos.
                    	error("O tipo " + y.type.getTypeName() + " nao suporta operacao de adicao/subtracao.");
                    	y.type = Tab.noType;
                    }
                    
                    // Gerando codigo da operacao.
                    Code.load(x); // Caso repita varias operacoes, x so sera carregado a primeira vez.
                    Code.load(y);
                    Code.put(op);
                    
                    break;
                default:
                    break termos;
            }
        }
        
        return x;
    }

    /** Term = Factor {Mulop Factor}.
     *  Retorna um Struct com o tipo resultado do termo.
     */
    private static Operand Term() {	
    	Operand x, y;
    	// O primeiro operando e o resultado de Factor().
    	x = Factor();
    	
        fatores:
        while (true) {
            switch (sym) {
                case times:
                case slash:
                case rem:
                    int op = Mulop();
                    
                    if (x.type != Tab.intType) { // Apenas Inteiros podem ser multiplicados/divididos.
                    	error("O tipo " + x.type.getTypeName() + " nao suporta operacao de multiplicacao/divisao.");
                    	x.type = Tab.noType;
                    }
                    
                    // O segundo operando e o resultado do segundo Factor().
                    y = Factor();
                    
                    if (y.type != Tab.intType) { // Apenas Inteiros podem ser multiplicados/divididos.
                    	error("O tipo " + y.type.getTypeName() + " nao suporta operacao de multiplicacao/divisao.");
                    	y.type = Tab.intType;
                    }
                    
                    // Gerando codigo da operacao.
                    Code.load(x); // Caso repita varias operacoes, x so sera carregado a primeira vez.
                    Code.load(y);
                    Code.put(op);
                    
                    break;
                default:
                    break fatores;
            }
        }
    	
    	return x;
    }

    /**
     * Factor = Designator [ActPars] | 
     *          number | charConst | 
     *          "new" ident ["[" Expr "]"] | 
     *          "(" Expr ")".
     * Retorna um Struct com o tipo resultado do fator.
     */
    private static Operand Factor() {
    	// WARNING: Pode ser perigoso se 'opd' nao tiver seu valor alterado.
    	Operand opd = null;
    	
        switch (sym) {
            case ident: // Designator [ActPars]
                Operand oDesig = Designator();
                
                if (sym == lpar) { // Chamando o metodo 'oDesig'.
                	// Aqui os metodos sao chamados para trabalhar com o retorno.
                	if (oDesig.type == Tab.voidType)
                		error("O metodo " + oDesig.obj.name + " possui valor de retorno Void.");
                	
                	// Verifica os parametros e os carrega, antes de dar 'call'.
                	ActPars(oDesig);
                	
                	// Os metodos padrao abaixo nao fazem nada.
                	if (oDesig.obj == Tab.ordObj || oDesig.obj == Tab.chrObj);
                	else if (oDesig.obj == Tab.lenObj) {
                		// Este metodo possui o seu proprio codigo no MicroJava.
                		Code.put(Code.arraylength);
                	} else {
                		// Chamada de um metodo comum.
                		Code.put(Code.call);
                		Code.put2(oDesig.adr);
                	}
                	
                	oDesig.kind = Operand.Stack; // O metodo ja foi carregado na Stack.
                	
                	// Quando o metodo for chamado, o retorno ja estara na
                	// pilha como resultado.
                	
                } else { // 'oDesig' e apenas um identificador.
                	Code.load(oDesig); // Se fosse metodo, nao seria carregado desta maneira.
                }
                
                opd = oDesig;
                break;
            case number: // number
                check(number);
                // Gerando operando, sera carregado por quem chamou.
                opd = new Operand(t.val);
                opd.type = Tab.intType;
                break;
            case charCon: // charConst
                check(charCon);
                // Gerando operando, sera carregado por quem chamou.
                opd = new Operand(t.val);
                opd.type = Tab.charType;
                break;
            case new_: // "new" ident ["[" Expr "]"]
                check(new_);
                check(ident);
                
                // Procura o tipo da Classe/Array.
                Obj obj = Tab.find(t.string);
                Struct type = obj.type;
                
                if (sym == lbrack) { // Checa se e um Array.
                	// O identificador deve ser um tipo.
                    if (obj.kind != Obj.Type)
                    	error(obj.name + " nao define um Tipo.");
                	
                    check(lbrack);
                    Operand oExpr = Expr(); // Tamanho do Array.
                    check(rbrack);
                    
                    if (oExpr.type != Tab.intType) // Tamanho do vetor deve ser Int.
                    	error("O tamanho do vetor deve ser do tipo Int.");
                    
                    // Gerando codigo para criar um Array.
                    Code.load(oExpr); // Carregando o tamanho do Array.
                    Code.put(Code.newarray);
                    
                    if (type == Tab.charType)
                    	Code.put(0);
                    else
                    	Code.put(1);
                    
                    // O Tipo encontrado e um Array, de Tipos.
                    type = new Struct(Struct.Arr, type);
                    
                } else { // Senao e do tipo Class.
                	// O identificador deve ser um tipo ou Classe.
                    if (obj.kind != Obj.Type && type.kind != Struct.Class)
                    	error(obj.name + " nao define um Tipo ou Classe.");
                    
                	// Gerando codigo para criar um Objeto.
                	Code.put(Code.new_);
                	Code.put2(type.nFields);
                }
                
                // O Operando obtido ja foi carregado na Stack, e e do tipo 'type'.
                opd = new Operand(Operand.Stack, type);
                break;
            case lpar: // "(" Expr ")"
                check(lpar);
                opd = Expr();
                check(rpar);
                break;
            default:
                error(name[sym] + " expected");
                break;
        }
        
        return opd;
    }

    /** Designator = ident {"." ident | "[" Expr "]"}. 
     *  Retorna um Struct com o tipo resultante.
     */
    private static Operand Designator() {
    	// WARNING: Pode ser perigoso de 'opd' nao tiver seu valor alterado.
    	Operand opd = null;
    	
    	// A principio, o objeto a ser retornado e este.
        if (check(ident)) {
        	opd = new Operand(Tab.find(t.string));
        }

        while (true) {
            if (sym == period) { // O Identificador deve ser uma Classe.
            	// Carregando o Operando.
            	Code.load(opd);
            	
                check(period);
                // Ponto e um operador de classes apenas.
                if (opd.type.kind != Struct.Class) {
                	error("O tipo " + opd.type.getTypeName() + " nao suporta o operador de escopo.");
                }
                // O objeto atual e o objeto do campo referenciado em 'opd'.
                if(check(ident)) {
                	if (opd.type.kind == Struct.Class) {
                		Obj fld = Tab.findField(t.string, opd.type);
                		// Atualizando o operador para o referenciado.
                		opd.kind = Operand.Fld;
                		opd.adr = fld.adr;
                		opd.type = fld.type;
                	} else {
                		error("Operador de escopo e apenas aplicavel a objetos.");
                		opd.type = Tab.noType; // Nao era o tipo desejado. Melhor limpar.
                	}
                }
            } else if (sym == lbrack) { // O Idenficador deve ser um Array.
            	// Carregando o Operando.
            	Code.load(opd);
            	
            	// Buscando um elemento do array,
            	if (opd.type.kind != Struct.Arr) {
            		error("O tipo " + opd.type.getTypeName() + " nao suporta o operador [].");
            		opd.type = Tab.noType; // Nao era o tipo desejado. Melhor limpar.
            	} else {
            		// logo o tipo final e o tipo do array.
            		opd.kind = Operand.Elem;
            		opd.type = opd.type.elemType;
            	}
            	
                check(lbrack);
                Operand oExpr = Expr(); // Indice do Array.
                Code.load(oExpr); // Carregando o Indice.
                check(rbrack);
                
                // Para acessar um vetor, o tipo deve ser Int.
                if (oExpr.type != Tab.intType)
                	error("O operador [] espera um tipo Int.");
            } else {
                break;
            }
        }
        
        return opd;
    }

    /** Addop = "+" | "-". */
    private static int Addop() {
        if (sym == plus) {
            check(plus);
            return Code.add;
        } else if (sym == minus) {
            check(minus);
            return Code.sub;
        } else {
            error(name[sym] + " expected");
            return 0;
        }
    }

    /** Mulop = "*" | "/" | "%". */
    private static int Mulop() {
        if (sym == times) {
            check(times);
            return Code.mul;
        } else if (sym == slash) {
            check(slash);
            return Code.div;
        } else if (sym == rem) {
            check(rem);
            return Code.rem;
        } else {
            error(name[sym] + " expected");
            return 0;
        }
    }

    // -------------- end parsing methods (in alphabetical order)
    // -----------------
    public static void parse() {
        // initialize symbol sets
        BitSet s;
        s = new BitSet(64);
        exprStart = s;
        s.set(ident);
        s.set(number);
        s.set(charCon);
        s.set(new_);
        s.set(lpar);
        s.set(minus);

        s = new BitSet(64);
        statStart = s;
        s.set(ident);
        s.set(if_);
        s.set(while_);
        s.set(read_);
        s.set(return_);
        s.set(print_);
        s.set(lbrace);
        s.set(semicolon);

        s = new BitSet(64);
        statSeqFollow = s;
        s.set(rbrace);
        s.set(eof);

        s = new BitSet(64);
        declStart = s;
        s.set(final_);
        s.set(ident);
        s.set(class_);

        s = new BitSet(64);
        declFollow = s;
        s.set(lbrace);
        s.set(void_);
        s.set(eof);

        s = new BitSet(64);
        syncTokens = s;
        s.set(lbrace);
        s.set(rbrace);
        s.set(lbrack);
        s.set(rbrack);
        s.set(lpar);
        s.set(rpar);
        s.set(semicolon);
        s.set(assign);
        s.set(eof);
        
        // start parsing
        errors = 0;
        errDist = 3;
        scan();
        Program();
        if (sym != eof) {
            error("end of file found before end of program");
        }
    }
}
