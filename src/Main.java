import java.io.FileReader;

public class Main {
    public static void main(String[] args) throws Exception {
        BizLangLexer lexer = new BizLangLexer(new FileReader("programa.biz"));
        Parser p = new Parser(lexer);

    }
}