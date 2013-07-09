package tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;

import javax.tools.JavaFileObject;

import org.jmlspecs.openjml.JmlOption;
import org.jmlspecs.openjml.JmlSpecs;
import org.jmlspecs.openjml.Utils;
import org.junit.runners.Parameterized.Parameters;

import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.Log;


public abstract class EscBase extends JmlTestCase {

    static public java.util.List<String> solvers = java.util.Arrays.asList(new String[]{ 
        "z3_4_3", 
        "cvc4",
        // "yices2" , // FIXME - needs quantifiers
        // "yices", 
        // "simplify" 
        });
    
    static public  Collection<String[]> datax() {
        Collection<String[]> data = new ArrayList<String[]>(10);
        data.addAll(makeData(solvers));
        data.addAll(makeData((String)null)); // FIXME - Boogie twice?
        return data;
    }
    
    @Parameters
    static public  Collection<String[]> nonnulldatax() {
        return (makeData(solvers));
    }
    
    static public  Collection<String[]> makeData(java.util.List<String> solvers) {
        Collection<String[]> data = new ArrayList<String[]>(10);
        for (String s: solvers) data.add(new String[]{"-newesc",s});
        // FIXME: data.add(new String[]{"-boogie",null}); 
        return data;
    }

    static public  Collection<String[]> makeData(String... solvers) {
        Collection<String[]> data = new ArrayList<String[]>(10);
        for (String s: solvers) data.add(new String[]{"-newesc",s});
        // FIXME: data.add(new String[]{"-boogie",null}); 
        return data;
    }

    String option;
    String solver;
    
    public EscBase(String option, String solver) {
        this.option = option;
        this.solver = solver;
    }
    
    public void printDiagnostics() {
        System.out.println("SOLVER: " + solver + " " + option);
        super.printDiagnostics();
    }

    static String z = java.io.File.pathSeparator;
    static String testspecpath1 = "$A"+z+"$B";
    static String testspecpath;
    int expectedExit = 0;
    int expectedErrors = 0;
    boolean noAssociatedDeclaration;
    String[] args;

    @Override
    public void setUp() throws Exception {
        testspecpath = testspecpath1;
        collector = new FilteredDiagnosticCollector<JavaFileObject>(true);
        super.setUp();
        main.addOptions("-specspath",   testspecpath);
        main.addOptions("-command","esc");
        main.addOptions("-noPurityCheck");
        setOption(option,solver);
        //main.setupOptions();
        specs = JmlSpecs.instance(context);
        expectedExit = 0;
        expectedErrors = 0;
        noAssociatedDeclaration = false;
        print = false;
        args = new String[]{};
    }
    

    protected void setOption(String option) {
        if (option == null) {
            // nothing set
        } else if (option.equals("-boogie")) {
            main.addUncheckedOption(JmlOption.NEWESC.optionName());
            main.addUncheckedOption(JmlOption.BOOGIE.optionName());
//        } else if (option.equals("-custom")) {
//            main.addUncheckedOption(JmlOption.CUSTOM.optionName());
//            options.put(JmlOption.NEWESC.optionName(),null);
//            main.addUncheckedOption("openjml.defaultProver=yices");
        } else {
            main.addUncheckedOption(JmlOption.NEWESC.optionName());
            main.addUncheckedOption("openjml.defaultProver=z3_4_3");
        }
    }

    protected void setOption(String option, String solver) {
        if (option == null) {
            // nothing set
        } else if (option.equals("-boogie")) {
            main.addUncheckedOption(JmlOption.NEWESC.optionName());
            main.addUncheckedOption(JmlOption.BOOGIE.optionName());
//        } else if (option.equals("-custom")) {
//            main.addUncheckedOption(JmlOption.CUSTOM.optionName());
//            options.put(JmlOption.NEWESC.optionName(),null);
//            main.addUncheckedOption("openjml.defaultProver=yices");
        } else {
            main.addUncheckedOption(JmlOption.NEWESC.optionName());
            main.addUncheckedOption("openjml.defaultProver=z3_4_3");
        }
        // solver == null means use the default
        if (solver != null) main.addOptions(JmlOption.PROVER.optionName(),solver);
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
        specs = null;
    }

    
    protected void helpTCX2(String classname, String s, String classname2, String s2, Object... list) {
        try {
            String filename = classname.replace(".","/")+".java";
            JavaFileObject f = new TestJavaFileObject(filename,s);
            String filename2 = classname2.replace(".","/")+".java";
            JavaFileObject f2 = new TestJavaFileObject(filename2,s2);
            Log.instance(context).useSource(f);
            helpTCXB(List.of(f,f2),list);
        } catch (Exception e) {
            e.printStackTrace(System.out);
            fail("Exception thrown while processing test: " + e);
        }
    }

    protected void helpTCX(String classname, String s, Object... list) {
        try {
            String filename = classname.replace(".","/")+".java";
            JavaFileObject f = new TestJavaFileObject(filename,s);
            Log.instance(context).useSource(f);
            helpTCXB(List.of(f),list);
        } catch (Exception e) {
            e.printStackTrace(System.out);
            fail("Exception thrown while processing test: " + e);
        }
    }

    protected void helpTCXB(List<JavaFileObject> files, Object... list) {
        try {
            for (JavaFileObject f: mockFiles) files = files.append(f);
            
            int ex = main.compile(args, null, context, files, null);
            
            if (print) printDiagnostics();
            int j = 0; // counts the errors in list, accounting for optional or null entries
            int i = 0;
            while (i < list.length) {
                if (list[i] == null) { i+=2; continue; }
                int col = ((Integer)list[i+1]).intValue();
                if (col < 0) {
                    // allowed to be optional
                    if (j >= collector.getDiagnostics().size()) {
                        // OK - just skip
                    } else if (list[i].toString().equals(noSource(collector.getDiagnostics().get(j))) &&
                            -col == Math.abs(collector.getDiagnostics().get(j).getColumnNumber())) {
                        j++;
                    } else {
                        // Not equal and the expected error is optional so just skip
                    }
                } else {
                    if (noAssociatedDeclaration && list[i].toString().contains("Associated declaration")) {
                        // OK - skip
                    } else {
                        if (j < collector.getDiagnostics().size()) {
                            assertEquals("Error " + j, list[i].toString(), noSource(collector.getDiagnostics().get(j)));
                            assertEquals("Error " + j, col, collector.getDiagnostics().get(j).getColumnNumber());
                        }
                        j++;
                    }
                }
                i += 2;
            }
            expectedErrors = j;
            assertEquals("Errors seen",expectedErrors,collector.getDiagnostics().size());
            if (ex != expectedExit) fail("Compile ended with exit code " + ex);

        } catch (Exception e) {
            e.printStackTrace(System.out);
            fail("Exception thrown while processing test: " + e);
        } catch (AssertionError e) {
            if (!print && !noExtraPrinting) printDiagnostics();
            throw e;
        }
    }

    /** Used to add a pseudo file to the file system. Note that for testing, a 
     * typical filename given here might be #B/A.java, where #B denotes a 
     * mock directory on the specification path
     * @param filename the name of the file, including leading directory components 
     * @param content the String constituting the content of the pseudo-file
     */
    protected void addMockFile(/*@ non_null */ String filename, /*@ non_null */String content) {
        try {
            addMockFile(filename,new TestJavaFileObject(new URI("file:///" + filename),content));
        } catch (Exception e) {
            fail("Exception in creating a URI: " + e);
        }
    }

    /** Used to add a pseudo file to the file system. Note that for testing, a 
     * typical filename given here might be #B/A.java, where #B denotes a 
     * mock directory on the specification path
     * @param filename the name of the file, including leading directory components 
     * @param file the JavaFileObject to be associated with this name
     */
    protected void addMockJavaFile(String filename, JavaFileObject file) {
        mockFiles.add(file);
    }


}
