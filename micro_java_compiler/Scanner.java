/* MicroJava Scanner (HM 06-12-28)
=================
 */
package MJ;

import java.io.*;

public class Scanner {

    private static final char eofCh = '\u0080';
    private static final char eol = '\n';
    private static final int // token codes
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
    private static final String key[] = { // sorted list of keywords
        "class", "else", "final", "if", "new", "print", "program", "read",
        "return", "void", "while"};
    private static final int keyVal[] = {class_, else_, final_, if_, new_,
        print_, program_, read_, return_, void_, while_};
    private static char ch; // lookahead character
    public static int col; // current column
    public static int line; // current line
    private static int pos; // current position from start of source file
    private static Reader in; // source file reader
	@SuppressWarnings("unused")
	private static char[] lex; // current lexeme (token string)

    // ----- ch = next input character
    private static void nextCh() {
        try {
            ch = (char) in.read();
            col++;
            pos++;
            if (ch == eol) {
                line++;
                col = 0;
            } else if (ch == '\uffff') {
                ch = eofCh;
            }
        } catch (IOException e) {
            ch = eofCh;
        }
    }

    // --------- Initialize scanner
    public static void init(Reader r) {
        in = new BufferedReader(r);
        lex = new char[64];
        line = 1;
        col = 0;
        nextCh();
    }

    // ---------- Read the name of a token
    public static void readName(Token t) {
        t.string = "";
        t.string += ch;
        while (true) {
            nextCh();
            if (Character.isJavaIdentifierPart(ch)) {
                t.string += ch;
            } else {
                break;
            }
        }

        t.kind = none;
        for (int i = 0; i < key.length; i++) {
            if (t.string.equals(key[i])) {
                t.kind = keyVal[i];
                break;
            }
        }
        if (t.kind == none) {
            t.kind = ident;
        }
    }

    // ---------- Read the number value of a token
    public static void readNumber(Token t) {
        t.string = "";
        t.string += ch;

        while (true) {
            nextCh();
            if (Character.isDigit(ch)) {
                t.string += ch;
            } else {
                break;
            }
        }

        t.val = Integer.parseInt(t.string);
        t.kind = number;
    }

    // ---------- Return next input token
    public static Token next() {
        while (ch <= ' ') {
            nextCh(); // skip blanks, tabs, eols
        }
        Token t = new Token();
        t.line = line;
        t.col = col;

        switch (ch) {
            case '-':
                nextCh();
                t.kind = minus;
                break;
            case '+':
                nextCh();
                t.kind = plus;
                break;
            case '*':
                nextCh();
                t.kind = times;
                break;
            case '%':
                nextCh();
                t.kind = rem;
                break;
            case '!':
                nextCh();
                if (ch == '=') {
                	nextCh();
                	t.kind = neq;
                } else {
                	Parser.error(ch + " simbolo inesperado.");
                }
                break;
            case '<':
                nextCh();
                if (ch == '=') {
                    nextCh();
                    t.kind = leq;
                } else {
                    t.kind = lss;
                }
                break;
            case '>':
                nextCh();
                if (ch == '=') {
                    nextCh();
                    t.kind = geq;
                } else {
                    t.kind = gtr;
                }
                break;
            case '\'':
                nextCh();
                // Apenas o caractere ' e reconhecido.
                // Outros caracteres que usam caractere de
                // escape nao sao reconhecidos.
                t.string = String.valueOf(ch);
                t.val = Character.getNumericValue(ch);
                nextCh();
                if (ch == '\'') {
                	nextCh();
                	t.kind = charCon;
                } else {
                	Parser.error(ch + " simbolo inesperado.");
                }
                break;
            case ',':
                nextCh();
                t.kind = comma;
                break;
            case ';':
                nextCh();
                t.kind = semicolon;
                break;
            case '.':
                nextCh();
                t.kind = period;
                break;
            case eofCh:
                t.kind = eof;
                break; // no nextCh() any more
            case '{':
                nextCh();
                t.kind = lbrace;
                break;
            case '}':
                nextCh();
                t.kind = rbrace;
                break;
            case '[':
                nextCh();
                t.kind = lbrack;
                break;
            case ']':
                nextCh();
                t.kind = rbrack;
                break;
            case '(':
                nextCh();
                t.kind = lpar;
                break;
            case ')':
                nextCh();
                t.kind = rpar;
                break;
            case '=':
                nextCh();
                if (ch == '=') {
                    nextCh();
                    t.kind = eql;
                } else {
                    t.kind = assign;
                }
                break;
            case '/':
                nextCh();
                if (ch == '/') {
                    do {
                        nextCh();
                    } while (ch != '\n' && ch != eofCh);
                    t = next(); // call scanner recursively
                } else {
                    t.kind = slash;
                }
                break;
            default:
                if (Character.isLetter(ch)) {
                    readName(t);
                    break;
                } else if (Character.isDigit(ch)) {
                    readNumber(t);
                    break;
                } else {
                    nextCh();
                    t.kind = none;
                    break;
                }
        }
        return t;
    } // ch holds the next character that is still unprocessed
}
