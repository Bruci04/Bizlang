package Bizlang;
import java_cup.runtime.Symbol;
%%
%class BizLangLexer
%unicode
%cup  // Tokens compatibles con CUP
/* Declaración de patrones regulares */
%{
    // Método auxiliar para devolver tokens
    private Symbol symbol(int type) {
        return new Symbol(type, yyline, yycolumn);
    }

    private Symbol symbol(int type, Object value) {
        return new Symbol(type, yyline, yycolumn, value);
    }
%}

/*Expresiones regulares y comandos */
%%
"agregar"           { return symbol(sym.COMANDO, "agregar"); }
"vender"            { return symbol(sym.COMANDO, "vender"); }
"mostrar_inventario" { return symbol(sym.COMANDO, "mostrar_inventario"); }
"generar_reporte"   { return symbol(sym.COMANDO, "generar_reporte"); }

\"[^\"]*\"          { return symbol(sym.TEXTO, yytext().replace("\"", "")); }  // Texto entre comillas
[0-9]+              { return symbol(sym.NUMERO, Integer.parseInt(yytext())); }  // Números enteros

[ \t\r\n\f]+        { /* Ignorar espacios en blanco */ }
.                   { System.err.println("Caracter no reconocido: " + yytext()); }