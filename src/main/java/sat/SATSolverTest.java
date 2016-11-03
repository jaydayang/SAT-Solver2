package sat;

/*
import static org.junit.Assert.*;

import org.junit.Test;
*/


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.EmptyStackException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.lang.model.type.ArrayType;

import immutable.EmptyImList;
import immutable.ImList;
import sat.env.*;
import sat.formula.*;

import static sat.formula.PosLiteral.make;


public class SATSolverTest {
    Literal a = make("a");
    Literal b = make("b");
    Literal c = make("c");
    Literal na = a.getNegation();
    Literal nb = b.getNegation();
    Literal nc = c.getNegation();
    static ImList<Literal> literalImList=new EmptyImList();




    // TODO: add the main method that reads the .cnf file and calls SATSolver.solve to determine the satisfiability
    public static void main(String[] args) throws IOException {
        String content=cut(readFile("/Users/chs/Documents/SUTD/intro to info/Project-2D/Project-2D-starting/sampleCNF/largeUnsat.cnf"));
        String[] clause = content.split(" 0 ");
//        for(String e:clause){
//            System.out.println(e);
//        }
        String[] literal;
        Formula fml=new Formula();
        Literal a;


        for(int i=0;i<clause.length;i++) {
            Clause cls=new Clause();
            literal=clause[i].split(" ");
//            for(String e:literal){
//                System.out.println(e);
//            }
            for(int j=0;j<literal.length;j++){
                if(!literal[j].equals("")){
                int numLiteral=Integer.parseInt(literal[j]);
                String absLiteral=Integer.toString(Math.abs(numLiteral));
                if(numLiteral>0){
                    a=PosLiteral.make(absLiteral);
                }else{
                    a=NegLiteral.make(absLiteral);
                }
                    if(!literalImList.contains(PosLiteral.make(absLiteral))){
                    literalImList=literalImList.add(PosLiteral.make(absLiteral));}
                cls=cls.add(a);
                //  System.out.println(cls.toString());
                }
            }
            fml=fml.addClause(cls);

        }
        System.out.println("SAT solver starts");
        long started=System.nanoTime();;

        Environment result=SATSolver.solve(fml);

        long time=System.nanoTime();
        long timetaken=time-started;
        System.out.println("Time:"+timetaken/1000000.0+"ms");

        if(result==null){
            System.out.println("not satisfiable");
        }else{
            String timeLog = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
            PrintWriter out = new PrintWriter("12.txt","UTF-8");
            out.println(timeLog);
            for(Literal e:literalImList){
                out.println(e.getVariable().getName()+":"+result.get(e.getVariable()).toString());
            }

            String resultStr=result.toString();
            String [] resultArr= resultStr.split(",");
//            for (String i: resultArr){
//                i=i.replace("]","");
//                i=i.replace("Environment:[","");
//                i=i.replace("->",":");
//                out.println(i);}
            out.close();
            System.out.println("satisfiable");
            System.out.println(result.toString());
        }

        //System.out.println(fml.toString());




    }

    private static String readFile (String filename) throws IOException {
        File cnfFile=new File(filename);
        BufferedReader bufferedReader = new BufferedReader(new FileReader(cnfFile));
        StringBuffer stringBuffer = new StringBuffer();
        String line = null;
        while((line = bufferedReader.readLine())!=null){
            stringBuffer.append(line).append(" ");
        }
        return stringBuffer.toString();
    }






    private static String cut(String whole){
        Pattern p = Pattern.compile("(p\\s+cnf\\s+)(\\d{1,}\\s+\\d{1,})");
        Matcher m = p.matcher(whole);

        while ( m.find() )  {
            return whole.substring(m.end()+1);
        }
        return null;

    }





    public void testSATSolver1(){
        // (a v b)
        Environment e = SATSolver.solve(makeFm(makeCl(a,b))	);
/*
    	assertTrue( "one of the literals should be set to true",
    			Bool.TRUE == e.get(a.getVariable())
    			|| Bool.TRUE == e.get(b.getVariable())	);

*/
    }


    public void testSATSolver2(){
        // (~a)
        Environment e = SATSolver.solve(makeFm(makeCl(na)));
/*
    	assertEquals( Bool.FALSE, e.get(na.getVariable()));
*/
    }

    private static Formula makeFm(Clause... e) {
        Formula f = new Formula();
        for (Clause c : e) {
            f = f.addClause(c);
        }
        return f;
    }

    private static Clause makeCl(Literal... e) {
        Clause c = new Clause();
        for (Literal l : e) {
            c = c.add(l);
        }
        return c;
    }
}