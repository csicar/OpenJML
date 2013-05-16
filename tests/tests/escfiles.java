package tests;

import static org.junit.Assert.fail;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

/** These tests check running ESC on files in the file system, comparing the
 * output against expected files. These tests are a bit easier to create, since 
 * the file and output do not have to be converted into Strings; however, they
 * are not as easily read, since the content is tucked away in files, rather 
 * than immediately there in the test class.
 * <P>
 * To add a new test:
 * <UL>
 * <LI> create a directory containing the test files as a subdirectory of 
 * 'testfiles'
 * <LI> add a test to this class - typically named similarly to the folder
 * containing the source data
 * </UL>
 */

@RunWith(Parameterized.class)
public class escfiles extends EscBase {

    public escfiles(String option, String solver) {
        super(option,solver);
    }
    
    String[] rac = null;
    
    /** The command-line to use to run ESC on a program */
    String[] sysrac = new String[]{jdk, "-classpath","bin"+z+"bin-runtime",null};

    @Override
    public void setUp() throws Exception {
        rac = sysrac;
        super.setUp();
    }

    /** This method does the running of a RAC test.  No output is
     * expected from running openjml to produce the RACed program;
     * the number of expected diagnostics is set by 'expectedErrors'.
     * @param dirname The directory containing the test sources, a relative path
     * from the project folder
     * @param classname The fully-qualified classname for the test class (where main is)
     * @param list any expected diagnostics from openjml, followed by the error messages from the RACed program, line by line
     */
    public void helpTCF(String sourceDirname, String outDir, String ... opts) {
        boolean print = false;
        try {
            new File(outDir).mkdirs();
            String actCompile = outDir + "/actual";
            new File(actCompile).delete();
            List<String> args = new LinkedList<String>();
            args.add("-esc");
            args.add("-noPurityCheck");
            args.add("-dir");
            args.add(sourceDirname);
            args.addAll(Arrays.asList(opts));
            
            PrintWriter pw = new PrintWriter(actCompile);
            int ex = org.jmlspecs.openjml.Main.execute(pw,null,null,args.toArray(new String[args.size()]));
            pw.close();
            
            String diffs = compareFiles(outDir + "/expected", actCompile);
            int n = 1;
            while (diffs != null) {
                n++;
                String name = outDir + "/expected" + n;
                if (!new File(name).exists()) break;
                diffs = compareFiles(name, actCompile);
            }
            if (diffs != null) {
                System.out.println(diffs);
                fail("Files differ: " + diffs);
            }  
            new File(actCompile).delete();
            if (ex != expectedExit) fail("Compile ended with exit code " + ex);

        } catch (Exception e) {
            e.printStackTrace(System.out);
            fail("Exception thrown while processing test: " + e);
        } catch (AssertionError e) {
            throw e;
        } finally {
            // Should close open objects
        }
    }


    @Test
    public void testDemo1() {
        expectedExit = 1;
        helpTCF("../OpenJMLDemo/src/openjml/clock/TickTockClock.java","testfiles/escDemo1","-noInternalSpecs","-subexpressions","-progress");
    }

    @Test
    public void testDemo2() {
        expectedExit = 1;
        helpTCF("../OpenJMLDemo/src/openjml/clock/TickTockClockA.java","testfiles/escDemo2","-noInternalSpecs","-subexpressions","-progress","-show","-method=tick");
    }

    @Test
    public void testDemo3() {
        expectedExit = 0;
        helpTCF("../OpenJMLDemo/src/openjml/clock/TickTockClockB.java","testfiles/escDemo3","-noInternalSpecs","-subexpressions","-progress");
    }

    @Test
    public void testDemoPaths() {
        expectedExit = 0;
        helpTCF("../OpenJMLDemo/src/openjml/demo/Paths.java","testfiles/escDemoPaths","-noInternalSpecs","-subexpressions","-progress");
    }

    @Test @Ignore // until other integer types are implemented
    public void testDemoPaths2() {
        expectedExit = 0;
        helpTCF("../OpenJMLDemo/src/openjml/test/Path2.java","testfiles/escDemoPaths2","-noInternalSpecs","-subexpressions","-progress");
    }

    @Test
    public void testDemoTypes() {
        expectedExit = 0;
        helpTCF("../OpenJMLDemo/src/openjml/demo/Types.java","testfiles/escDemoTypes","-noInternalSpecs","-subexpressions","-progress");
    }


    @Test
    public void testTrace() {
        expectedExit = 0;
        helpTCF("testfiles/escTrace","testfiles/escTrace","-subexpressions","-method=m","-escMaxWarnings=1");
    }

    @Test
    public void testTrace2() {
        expectedExit = 0;
        helpTCF("testfiles/escTrace2","testfiles/escTrace2","-subexpressions","-method=m");
    }

    @Test
    public void testTrace3() {
        expectedExit = 0;
        helpTCF("testfiles/escTrace3","testfiles/escTrace3","-subexpressions","-progress");
    }

    @Test
    public void testTrace4() {
        expectedExit = 0;
        helpTCF("testfiles/escTrace4","testfiles/escTrace4","-method=m","-subexpressions","-progress");
    }

    @Test
    public void testTrace5() {
        expectedExit = 0;
        helpTCF("testfiles/escTrace5","testfiles/escTrace5","-method=m","-subexpressions","-progress");
    }

    @Test
    public void testTrace6() {
        expectedExit = 0;
        helpTCF("testfiles/escTrace6","testfiles/escTrace6","-subexpressions","-progress");
    }

    @Test
    public void testTraceloops() {
        expectedExit = 0;
        helpTCF("testfiles/escTraceLoops","testfiles/escTraceLoops","-method=mgood","-subexpressions","-progress");
    }

    @Test
    public void testTraceloops1() {
        expectedExit = 0;
        helpTCF("testfiles/escTraceLoops","testfiles/escTraceLoops1","-method=m1","-subexpressions","-progress");
    }

    @Test
    public void testTraceloops2() {
        expectedExit = 0;
        helpTCF("testfiles/escTraceLoops","testfiles/escTraceLoops2","-method=m2","-subexpressions","-progress");
    }

    @Test
    public void testTraceloops3() {
        expectedExit = 0;
        helpTCF("testfiles/escTraceLoops","testfiles/escTraceLoops3","-method=m3","-subexpressions","-progress");
    }

    @Test
    public void testTraceloops4() {
        expectedExit = 0;
        helpTCF("testfiles/escTraceLoops","testfiles/escTraceLoops4","-method=m4","-subexpressions","-progress");
    }

    @Test
    public void testTraceloops5() {
        expectedExit = 0;
        helpTCF("testfiles/escTraceLoops","testfiles/escTraceLoops5","-method=m5","-subexpressions","-progress");
    }

    @Test
    public void testTraceloops6() {
        expectedExit = 0;
        helpTCF("testfiles/escTraceLoops","testfiles/escTraceLoops6","-method=m6","-subexpressions","-progress");
    }

    @Test
    public void testTraceWhile() {
        expectedExit = 0;
        helpTCF("testfiles/escTraceLoops","testfiles/escTraceWhile","-method=mwhile","-subexpressions","-progress");
    }

    @Test
    public void testTraceWhile1() {
        expectedExit = 0;
        helpTCF("testfiles/escTraceLoops","testfiles/escTraceWhile1","-method=mwhile1","-subexpressions","-progress");
    }

    @Test
    public void testTraceWhile2() {
        expectedExit = 0;
        helpTCF("testfiles/escTraceLoops","testfiles/escTraceWhile2","-method=mwhile2","-subexpressions","-progress");
    }

    @Test
    public void testTraceDo() {
        expectedExit = 0;
        helpTCF("testfiles/escTraceLoops","testfiles/escTraceDo","-method=mdo","-subexpressions","-progress");
    }

    @Test
    public void testTraceDo1() {
        expectedExit = 0;
        helpTCF("testfiles/escTraceLoops","testfiles/escTraceDo1","-method=mdo1","-subexpressions","-progress");
    }

    @Test
    public void testTraceDo2() {
        expectedExit = 0;
        helpTCF("testfiles/escTraceLoops","testfiles/escTraceDo2","-method=mdo2","-subexpressions","-progress");
    }

    @Test
    public void testTraceForeach() {
        expectedExit = 0;
        helpTCF("testfiles/escTraceLoops","testfiles/escTraceForeach","-method=mforeach","-subexpressions","-progress");
    }

    @Test
    public void testTraceForeach1() {
        expectedExit = 0;
        helpTCF("testfiles/escTraceLoops","testfiles/escTraceForeach1","-method=mforeach1","-subexpressions","-progress");
    }

    @Test
    public void testTraceForeach2() {
        expectedExit = 0;
        helpTCF("testfiles/escTraceLoops","testfiles/escTraceForeach2","-show","-method=mforeach2","-subexpressions","-progress");
    }

    @Test
    public void testTraceForeach3() {
        expectedExit = 0;
        helpTCF("testfiles/escTraceLoops","testfiles/escTraceForeach3","-method=mforeach3","-subexpressions","-progress");
    }

    @Test
    public void testTraceForeach4() {
        expectedExit = 0;
        helpTCF("testfiles/escTraceLoops","testfiles/escTraceForeach4","-method=mforeach4","-subexpressions","-progress");
    }

    @Test
    public void testTraceForeach5() {
        expectedExit = 0;
        helpTCF("testfiles/escTraceLoops","testfiles/escTraceForeach5","-method=mforeach5","-subexpressions","-progress");
    }


}