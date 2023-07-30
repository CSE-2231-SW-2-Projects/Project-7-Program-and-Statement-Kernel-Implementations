import static org.junit.Assert.assertEquals;

import org.junit.Test;

import components.queue.Queue;
import components.simplereader.SimpleReader;
import components.simplereader.SimpleReader1L;
import components.statement.Statement;
import components.statement.StatementKernel.Condition;
import components.statement.StatementKernel.Kind;
import components.utilities.Tokenizer;

/**
 * JUnit test fixture for {@code Statement}'s constructor and kernel methods.
 *
 * @author Wayne Heym
 * @author Zheyuan Gao
 * @author Cedric Fausey
 *
 */
public abstract class StatementTest {

    /**
     * The name of a file containing a sequence of BL statements.
     */
    private static final String FILE_NAME_1 = "data/statement-sample.bl";

    private static final String FILE_NAME_EMPTY = "data/statement-NoChild.bl";

    private static final String FILE_NAME_IF = "data/statement-TestIf.bl";

    private static final String FILE_NAME_IF_ELSE = "data/statement-TestIfElse.bl";

    private static final String FILE_NAME_WHILE = "data/statement-TestWhile.bl";

    private static final String FILE_NAME_CALL = "data/statement-TestCALL.bl";

    private static final String FILE_NAME_MIX = "data/statement-TestMix.bl";

    /**
     * Invokes the {@code Statement} constructor for the implementation under
     * test and returns the result.
     *
     * @return the new statement
     * @ensures constructor = compose((BLOCK, ?, ?), <>)
     */
    protected abstract Statement constructorTest();

    /**
     * Invokes the {@code Statement} constructor for the reference
     * implementation and returns the result.
     *
     * @return the new statement
     * @ensures constructor = compose((BLOCK, ?, ?), <>)
     */
    protected abstract Statement constructorRef();

    /**
     *
     * Creates and returns a block {@code Statement}, of the type of the
     * implementation under test, from the file with the given name.
     *
     * @param filename
     *            the name of the file to be parsed for the sequence of
     *            statements to go in the block statement
     * @return the constructed block statement
     * @ensures <pre>
     * createFromFile = [the block statement containing the statements
     * parsed from the file]
     * </pre>
     */
    private Statement createFromFileTest(String filename) {
        Statement s = this.constructorTest();
        SimpleReader file = new SimpleReader1L(filename);
        Queue<String> tokens = Tokenizer.tokens(file);
        s.parseBlock(tokens);
        file.close();
        return s;
    }

    /**
     *
     * Creates and returns a block {@code Statement}, of the reference
     * implementation type, from the file with the given name.
     *
     * @param filename
     *            the name of the file to be parsed for the sequence of
     *            statements to go in the block statement
     * @return the constructed block statement
     * @ensures <pre>
     * createFromFile = [the block statement containing the statements
     * parsed from the file]
     * </pre>
     */
    private Statement createFromFileRef(String filename) {
        Statement s = this.constructorRef();
        SimpleReader file = new SimpleReader1L(filename);
        Queue<String> tokens = Tokenizer.tokens(file);
        s.parseBlock(tokens);
        file.close();
        return s;
    }

    /**
     * Test constructor.
     */
    @Test
    public final void testConstructor() {
        /*
         * Setup
         */
        Statement sRef = this.constructorRef();

        /*
         * The call
         */
        Statement sTest = this.constructorTest();

        /*
         * Evaluation
         */
        assertEquals(sRef, sTest);
    }

    /**
     * Test kind of a WHILE statement.
     */
    @Test
    public final void testKindWhile() {
        /*
         * Setup
         */
        final int whilePos = 3;
        Statement sourceTest = this.createFromFileTest(FILE_NAME_1);
        Statement sourceRef = this.createFromFileRef(FILE_NAME_1);
        Statement sTest = sourceTest.removeFromBlock(whilePos);
        Statement sRef = sourceRef.removeFromBlock(whilePos);
        Kind kRef = sRef.kind();

        /*
         * The call
         */
        Kind kTest = sTest.kind();

        /*
         * Evaluation
         */
        assertEquals(kRef, kTest);
        assertEquals(sRef, sTest);
    }

    /**
     * Test addToBlock at an interior position.
     */
    @Test
    public final void testAddToBlockInterior() {
        /*
         * Setup
         */
        Statement sTest = this.createFromFileTest(FILE_NAME_1);
        Statement sRef = this.createFromFileRef(FILE_NAME_1);
        Statement emptyBlock = sRef.newInstance();
        Statement nestedTest = sTest.removeFromBlock(1);
        Statement nestedRef = sRef.removeFromBlock(1);
        sRef.addToBlock(2, nestedRef);

        /*
         * The call
         */
        sTest.addToBlock(2, nestedTest);

        /*
         * Evaluation
         */
        assertEquals(emptyBlock, nestedTest);
        assertEquals(sRef, sTest);
    }

    /**
     * Test removeFromBlock at the front leaving a non-empty block behind.
     */
    @Test
    public final void testRemoveFromBlockFrontLeavingNonEmpty() {
        /*
         * Setup
         */
        Statement sTest = this.createFromFileTest(FILE_NAME_1);
        Statement sRef = this.createFromFileRef(FILE_NAME_1);
        Statement nestedRef = sRef.removeFromBlock(0);

        /*
         * The call
         */
        Statement nestedTest = sTest.removeFromBlock(0);

        /*
         * Evaluation
         */
        assertEquals(sRef, sTest);
        assertEquals(nestedRef, nestedTest);
    }

    /**
     * Test lengthOfBlock, greater than zero.
     */
    @Test
    public final void testLengthOfBlockNonEmpty() {
        /*
         * Setup
         */
        Statement sTest = this.createFromFileTest(FILE_NAME_1);
        Statement sRef = this.createFromFileRef(FILE_NAME_1);
        int lengthRef = sRef.lengthOfBlock();

        /*
         * The call
         */
        int lengthTest = sTest.lengthOfBlock();

        /*
         * Evaluation
         */
        assertEquals(lengthRef, lengthTest);
        assertEquals(sRef, sTest);
    }

    /**
     * Test assembleIf.
     */
    @Test
    public final void testAssembleIf() {
        /*
         * Setup
         */
        Statement blockTest = this.createFromFileTest(FILE_NAME_1);
        Statement blockRef = this.createFromFileRef(FILE_NAME_1);
        Statement emptyBlock = blockRef.newInstance();
        Statement sourceTest = blockTest.removeFromBlock(1);
        Statement sRef = blockRef.removeFromBlock(1);
        Statement nestedTest = sourceTest.newInstance();
        Condition c = sourceTest.disassembleIf(nestedTest);
        Statement sTest = sourceTest.newInstance();

        /*
         * The call
         */
        sTest.assembleIf(c, nestedTest);

        /*
         * Evaluation
         */
        assertEquals(emptyBlock, nestedTest);
        assertEquals(sRef, sTest);
    }

    /**
     * Test disassembleIf.
     */
    @Test
    public final void testDisassembleIf() {
        /*
         * Setup
         */
        Statement blockTest = this.createFromFileTest(FILE_NAME_1);
        Statement blockRef = this.createFromFileRef(FILE_NAME_1);
        Statement sTest = blockTest.removeFromBlock(1);
        Statement sRef = blockRef.removeFromBlock(1);
        Statement nestedTest = sTest.newInstance();
        Statement nestedRef = sRef.newInstance();
        Condition cRef = sRef.disassembleIf(nestedRef);

        /*
         * The call
         */
        Condition cTest = sTest.disassembleIf(nestedTest);

        /*
         * Evaluation
         */
        assertEquals(nestedRef, nestedTest);
        assertEquals(sRef, sTest);
        assertEquals(cRef, cTest);
    }

    /**
     * Test assembleIfElse.
     */
    @Test
    public final void testAssembleIfElse() {
        /*
         * Setup
         */
        final int ifElsePos = 2;
        Statement blockTest = this.createFromFileTest(FILE_NAME_1);
        Statement blockRef = this.createFromFileRef(FILE_NAME_1);
        Statement emptyBlock = blockRef.newInstance();
        Statement sourceTest = blockTest.removeFromBlock(ifElsePos);
        Statement sRef = blockRef.removeFromBlock(ifElsePos);
        Statement thenBlockTest = sourceTest.newInstance();
        Statement elseBlockTest = sourceTest.newInstance();
        Condition cTest = sourceTest.disassembleIfElse(thenBlockTest,
                elseBlockTest);
        Statement sTest = blockTest.newInstance();

        /*
         * The call
         */
        sTest.assembleIfElse(cTest, thenBlockTest, elseBlockTest);

        /*
         * Evaluation
         */
        assertEquals(emptyBlock, thenBlockTest);
        assertEquals(emptyBlock, elseBlockTest);
        assertEquals(sRef, sTest);
    }

    /**
     * Test disassembleIfElse.
     */
    @Test
    public final void testDisassembleIfElse() {
        /*
         * Setup
         */
        final int ifElsePos = 2;
        Statement blockTest = this.createFromFileTest(FILE_NAME_1);
        Statement blockRef = this.createFromFileRef(FILE_NAME_1);
        Statement sTest = blockTest.removeFromBlock(ifElsePos);
        Statement sRef = blockRef.removeFromBlock(ifElsePos);
        Statement thenBlockTest = sTest.newInstance();
        Statement elseBlockTest = sTest.newInstance();
        Statement thenBlockRef = sRef.newInstance();
        Statement elseBlockRef = sRef.newInstance();
        Condition cRef = sRef.disassembleIfElse(thenBlockRef, elseBlockRef);

        /*
         * The call
         */
        Condition cTest = sTest.disassembleIfElse(thenBlockTest, elseBlockTest);

        /*
         * Evaluation
         */
        assertEquals(cRef, cTest);
        assertEquals(thenBlockRef, thenBlockTest);
        assertEquals(elseBlockRef, elseBlockTest);
        assertEquals(sRef, sTest);
    }

    /**
     * Test assembleWhile.
     */
    @Test
    public final void testAssembleWhile() {
        /*
         * Setup
         */
        Statement blockTest = this.createFromFileTest(FILE_NAME_1);
        Statement blockRef = this.createFromFileRef(FILE_NAME_1);
        Statement emptyBlock = blockRef.newInstance();
        Statement sourceTest = blockTest.removeFromBlock(1);
        Statement sourceRef = blockRef.removeFromBlock(1);
        Statement nestedTest = sourceTest.newInstance();
        Statement nestedRef = sourceRef.newInstance();
        Condition cTest = sourceTest.disassembleIf(nestedTest);
        Condition cRef = sourceRef.disassembleIf(nestedRef);
        Statement sRef = sourceRef.newInstance();
        sRef.assembleWhile(cRef, nestedRef);
        Statement sTest = sourceTest.newInstance();

        /*
         * The call
         */
        sTest.assembleWhile(cTest, nestedTest);

        /*
         * Evaluation
         */
        assertEquals(emptyBlock, nestedTest);
        assertEquals(sRef, sTest);
    }

    /**
     * Test disassembleWhile.
     */
    @Test
    public final void testDisassembleWhile() {
        /*
         * Setup
         */
        final int whilePos = 3;
        Statement blockTest = this.createFromFileTest(FILE_NAME_1);
        Statement blockRef = this.createFromFileRef(FILE_NAME_1);
        Statement sTest = blockTest.removeFromBlock(whilePos);
        Statement sRef = blockRef.removeFromBlock(whilePos);
        Statement nestedTest = sTest.newInstance();
        Statement nestedRef = sRef.newInstance();
        Condition cRef = sRef.disassembleWhile(nestedRef);

        /*
         * The call
         */
        Condition cTest = sTest.disassembleWhile(nestedTest);

        /*
         * Evaluation
         */
        assertEquals(nestedRef, nestedTest);
        assertEquals(sRef, sTest);
        assertEquals(cRef, cTest);
    }

    /**
     * Test assembleCall.
     */
    @Test
    public final void testAssembleCall() {
        /*
         * Setup
         */
        Statement sRef = this.constructorRef().newInstance();
        Statement sTest = this.constructorTest().newInstance();

        String name = "look-for-something";
        sRef.assembleCall(name);

        /*
         * The call
         */
        sTest.assembleCall(name);

        /*
         * Evaluation
         */
        assertEquals(sRef, sTest);
    }

    /**
     * Test disassembleCall.
     */
    @Test
    public final void testDisassembleCall() {
        /*
         * Setup
         */
        Statement blockTest = this.createFromFileTest(FILE_NAME_1);
        Statement blockRef = this.createFromFileRef(FILE_NAME_1);
        Statement sTest = blockTest.removeFromBlock(0);
        Statement sRef = blockRef.removeFromBlock(0);
        String nRef = sRef.disassembleCall();

        /*
         * The call
         */
        String nTest = sTest.disassembleCall();

        /*
         * Evaluation
         */
        assertEquals(sRef, sTest);
        assertEquals(nRef, nTest);
    }

    // TODO - provide additional test cases to thoroughly test StatementKernel

    /**
     * Test kind of a IF statement.
     */
    @Test
    public final void testKindIF() {
        /*
         * Setup
         */
        final int ifPos = 0;
        Statement sourceTest = this.createFromFileTest(FILE_NAME_IF);
        Statement sourceRef = this.createFromFileRef(FILE_NAME_IF);
        Statement sTest = sourceTest.removeFromBlock(ifPos);
        Statement sRef = sourceRef.removeFromBlock(ifPos);
        Kind kRef = sRef.kind();

        /*
         * The call
         */
        Kind kTest = sTest.kind();

        /*
         * Evaluation
         */
        assertEquals(kRef, kTest);
        assertEquals(sRef, sTest);
    }

    /**
     * Test kind of a IF_ELSE statement.
     */
    @Test
    public final void testKindIFELSE() {
        /*
         * Setup
         */
        final int ifPos = 0;
        Statement sourceTest = this.createFromFileTest(FILE_NAME_IF_ELSE);
        Statement sourceRef = this.createFromFileRef(FILE_NAME_IF_ELSE);
        Statement sTest = sourceTest.removeFromBlock(ifPos);
        Statement sRef = sourceRef.removeFromBlock(ifPos);
        Kind kRef = sRef.kind();

        /*
         * The call
         */
        Kind kTest = sTest.kind();

        /*
         * Evaluation
         */
        assertEquals(kRef, kTest);
        assertEquals(sRef, sTest);
    }

    /**
     * Test kind of a CALL statement.
     */
    @Test
    public final void testKindCALL() {
        /*
         * Setup
         */
        final int callPos = 0;
        Statement sourceTest = this.createFromFileTest(FILE_NAME_CALL);
        Statement sourceRef = this.createFromFileRef(FILE_NAME_CALL);
        Statement sTest = sourceTest.removeFromBlock(callPos);
        Statement sRef = sourceRef.removeFromBlock(callPos);
        Kind kRef = sRef.kind();

        /*
         * The call
         */
        Kind kTest = sTest.kind();

        /*
         * Evaluation
         */
        assertEquals(kRef, kTest);
        assertEquals(sRef, sTest);
    }

    /**
     * Test addToBlock to an empty file.
     */
    @Test
    public final void testAddToBlockEmpty() {
        /*
         * Setup
         */
        Statement s = this.createFromFileTest(FILE_NAME_EMPTY);
        Statement sExpected = this.createFromFileRef(FILE_NAME_EMPTY);
        Statement st = s.newInstance();
        Statement stExpected = sExpected.newInstance();
        st.assembleCall("skip");
        stExpected.assembleCall("skip");
        sExpected.addToBlock(0, stExpected);

        /*
         * The call
         */
        s.addToBlock(0, st);

        /*
         * Evaluation
         */

        assertEquals(sExpected, s);
    }

    /**
     * Test addToBlock at an interior position.
     */
    @Test
    public final void testAddToBlockInterior2() {
        /*
         * Setup
         */
        Statement sTest = this.createFromFileTest(FILE_NAME_IF);
        Statement sRef = this.createFromFileRef(FILE_NAME_IF);
        Statement emptyBlock = sRef.newInstance();
        Statement nestedTest = sTest.removeFromBlock(0);
        Statement nestedRef = sRef.removeFromBlock(0);
        sRef.addToBlock(2, nestedRef);

        /*
         * The call
         */
        sTest.addToBlock(2, nestedTest);

        /*
         * Evaluation
         */
        assertEquals(emptyBlock, nestedTest);
        assertEquals(sRef, sTest);
    }

    /**
     * Test addToBlock at an interior position.
     */
    @Test
    public final void testAddToBlockInterior3() {
        /*
         * Setup
         */
        Statement sTest = this.createFromFileTest(FILE_NAME_CALL);
        Statement sRef = this.createFromFileRef(FILE_NAME_CALL);
        Statement nestedTest = sTest.removeFromBlock(0);
        Statement nestedRef = sRef.removeFromBlock(0);
        sRef.addToBlock(2, nestedRef);

        /*
         * The call
         */
        sTest.addToBlock(2, nestedTest);

        /*
         * Evaluation
         */

        assertEquals(sRef, sTest);
    }

    /**
     * Test addToBlock by another block created by author.
     */
    @Test
    public final void testAddToBlockMyBlock() {
        /*
         * Setup
         */
        Statement s = this.createFromFileTest(FILE_NAME_CALL);
        Statement sExpected = this.createFromFileRef(FILE_NAME_CALL);
        Statement st = s.newInstance();
        Statement stExpected = sExpected.newInstance();
        st.assembleCall("move");
        stExpected.assembleCall("move");

        /*
         * The call
         */
        s.addToBlock(2, st);
        sExpected.addToBlock(2, stExpected);

        /*
         * Evaluation
         */

        assertEquals(sExpected, s);
    }

    /**
     * Test removeFromBlock to remove an IF statement.
     */
    @Test
    public final void testRemoveFromBlockIF() {
        /*
         * Setup
         */
        Statement s = this.createFromFileTest(FILE_NAME_IF);
        Statement sExpected = this.createFromFileRef(FILE_NAME_IF);
        Statement stExpected = sExpected.removeFromBlock(0);

        /*
         * The call
         */
        Statement st = s.removeFromBlock(0);

        /*
         * Evaluation
         */
        assertEquals(sExpected, s);
        assertEquals(stExpected, st);
    }

    /**
     * Test removeFromBlock to remove an IF_ELSE statement.
     */
    @Test
    public final void testRemoveFromBlockIfElse() {
        /*
         * Setup
         */
        Statement s = this.createFromFileTest(FILE_NAME_IF_ELSE);
        Statement sExpected = this.createFromFileRef(FILE_NAME_IF_ELSE);
        Statement stExpected = sExpected.removeFromBlock(1);

        /*
         * The call
         */
        Statement st = s.removeFromBlock(1);

        /*
         * Evaluation
         */
        assertEquals(sExpected, s);
        assertEquals(stExpected, st);
    }

    /**
     * Test removeFromBlock to remove an WHILE statement.
     */
    @Test
    public final void testRemoveFromBlockIfWhile() {
        /*
         * Setup
         */
        Statement s = this.createFromFileTest(FILE_NAME_WHILE);
        Statement sExpected = this.createFromFileRef(FILE_NAME_WHILE);
        Statement stExpected = sExpected.removeFromBlock(2);

        /*
         * The call
         */
        Statement st = s.removeFromBlock(2);

        /*
         * Evaluation
         */
        assertEquals(sExpected, s);
        assertEquals(stExpected, st);
    }

    /**
     * Test removeFromBlock to remove an CALL statement.
     */
    @Test
    public final void testRemoveFromBlockIfCall() {
        /*
         * Setup
         */
        Statement s = this.createFromFileTest(FILE_NAME_CALL);
        Statement sExpected = this.createFromFileRef(FILE_NAME_CALL);
        Statement stExpected = sExpected.removeFromBlock(2);

        /*
         * The call
         */
        Statement st = s.removeFromBlock(2);

        /*
         * Evaluation
         */
        assertEquals(sExpected, s);
        assertEquals(stExpected, st);
    }

    /**
     * Test lengthOfBlock of empty statement.
     */
    @Test
    public final void testLengthOfBlockEmpty() {
        /*
         * Setup
         */
        Statement s = this.createFromFileTest(FILE_NAME_EMPTY);
        Statement sExpected = this.createFromFileRef(FILE_NAME_EMPTY);
        int lengthExpected = sExpected.lengthOfBlock();

        /*
         * The call
         */
        int length = s.lengthOfBlock();

        /*
         * Evaluation
         */
        assertEquals(lengthExpected, length);
        assertEquals(sExpected, s);
    }

    /**
     * Test lengthOfBlock of If statements file.
     */
    @Test
    public final void testLengthOfBlockIfFile() {
        /*
         * Setup
         */
        Statement s = this.createFromFileTest(FILE_NAME_IF);
        Statement sExpected = this.createFromFileRef(FILE_NAME_IF);
        int lengthExpected = sExpected.lengthOfBlock();

        /*
         * The call
         */
        int length = s.lengthOfBlock();

        /*
         * Evaluation
         */
        assertEquals(lengthExpected, length);
        assertEquals(sExpected, s);
    }

    /**
     * Test lengthOfBlock of If-Else statements file.
     */
    @Test
    public final void testLengthOfBlockIfElse() {
        /*
         * Setup
         */
        Statement s = this.createFromFileTest(FILE_NAME_IF_ELSE);
        Statement sExpected = this.createFromFileRef(FILE_NAME_IF_ELSE);
        int lengthExpected = sExpected.lengthOfBlock();

        /*
         * The call
         */
        int length = s.lengthOfBlock();

        /*
         * Evaluation
         */
        assertEquals(lengthExpected, length);
        assertEquals(sExpected, s);
    }

    /**
     * Test lengthOfBlock of While statements file.
     */
    @Test
    public final void testLengthOfBlockWhileFile() {
        /*
         * Setup
         */
        Statement s = this.createFromFileTest(FILE_NAME_WHILE);
        Statement sExpected = this.createFromFileRef(FILE_NAME_WHILE);
        int lengthExpected = sExpected.lengthOfBlock();

        /*
         * The call
         */
        int length = s.lengthOfBlock();

        /*
         * Evaluation
         */
        assertEquals(lengthExpected, length);
        assertEquals(sExpected, s);
    }

    /**
     * Test lengthOfBlock of Mix statements file.
     */
    @Test
    public final void testLengthOfBlockMixFile() {
        /*
         * Setup
         */
        Statement s = this.createFromFileTest(FILE_NAME_MIX);
        Statement sExpected = this.createFromFileRef(FILE_NAME_MIX);
        int lengthExpected = sExpected.lengthOfBlock();

        /*
         * The call
         */
        int length = s.lengthOfBlock();

        /*
         * Evaluation
         */
        assertEquals(lengthExpected, length);
        assertEquals(sExpected, s);
    }

    /**
     * Test assembleIf to an if statements file.
     */
    @Test
    public final void testAssembleIf2() {
        /*
         * Setup
         */
        Statement s = this.createFromFileTest(FILE_NAME_IF);
        Statement sExpected = this.createFromFileRef(FILE_NAME_IF);
        Statement emptyBlock = sExpected.newInstance();
        Statement sourceTest = s.removeFromBlock(2);
        Statement sRef = sExpected.removeFromBlock(2);
        Statement nestedTest = sourceTest.newInstance();
        Condition c = sourceTest.disassembleIf(nestedTest);
        Statement sTest = sourceTest.newInstance();

        /*
         * The call
         */
        sTest.assembleIf(c, nestedTest);

        /*
         * Evaluation
         */
        assertEquals(emptyBlock, nestedTest);
        assertEquals(sRef, sTest);
    }

    /**
     * Test assembleIf to an mix statements file.
     */
    @Test
    public final void testAssembleIf3() {
        /*
         * Setup
         */
        Statement s = this.createFromFileTest(FILE_NAME_MIX);
        Statement sExpected = this.createFromFileRef(FILE_NAME_MIX);
        Statement emptyBlock = sExpected.newInstance();
        Statement sourceTest = s.removeFromBlock(3);
        Statement sRef = sExpected.removeFromBlock(3);
        Statement nestedTest = sourceTest.newInstance();
        Condition c = sourceTest.disassembleIf(nestedTest);
        Statement sTest = sourceTest.newInstance();

        /*
         * The call
         */
        sTest.assembleIf(c, nestedTest);

        /*
         * Evaluation
         */
        assertEquals(emptyBlock, nestedTest);
        assertEquals(sRef, sTest);
    }

    /**
     * Test disassembleIf to an if-statements file.
     */
    @Test
    public final void testDisassembleIf2() {
        /*
         * Setup
         */
        Statement blockTest = this.createFromFileTest(FILE_NAME_IF);
        Statement blockRef = this.createFromFileRef(FILE_NAME_IF);
        Statement sTest = blockTest.removeFromBlock(2);
        Statement sRef = blockRef.removeFromBlock(2);
        Statement nestedTest = sTest.newInstance();
        Statement nestedRef = sRef.newInstance();
        Condition cRef = sRef.disassembleIf(nestedRef);

        /*
         * The call
         */
        Condition cTest = sTest.disassembleIf(nestedTest);

        /*
         * Evaluation
         */
        assertEquals(nestedRef, nestedTest);
        assertEquals(sRef, sTest);
        assertEquals(cRef, cTest);
    }

    /**
     * Test disassembleIf to an mix-statements file.
     */
    @Test
    public final void testDisassembleIf3() {
        /*
         * Setup
         */
        Statement blockTest = this.createFromFileTest(FILE_NAME_MIX);
        Statement blockRef = this.createFromFileRef(FILE_NAME_MIX);
        Statement sTest = blockTest.removeFromBlock(3);
        Statement sRef = blockRef.removeFromBlock(3);
        Statement nestedTest = sTest.newInstance();
        Statement nestedRef = sRef.newInstance();
        Condition cRef = sRef.disassembleIf(nestedRef);

        /*
         * The call
         */
        Condition cTest = sTest.disassembleIf(nestedTest);

        /*
         * Evaluation
         */
        assertEquals(nestedRef, nestedTest);
        assertEquals(sRef, sTest);
        assertEquals(cRef, cTest);
    }

    /**
     * Test assembleIfElse to an if-else-statements file(position 0).
     */
    @Test
    public final void testAssembleIfElse2() {
        /*
         * Setup
         */
        final int ifElsePos = 0;
        Statement blockTest = this.createFromFileTest(FILE_NAME_IF_ELSE);
        Statement blockRef = this.createFromFileRef(FILE_NAME_IF_ELSE);
        Statement emptyBlock = blockRef.newInstance();
        Statement sourceTest = blockTest.removeFromBlock(ifElsePos);
        Statement sRef = blockRef.removeFromBlock(ifElsePos);
        Statement thenBlockTest = sourceTest.newInstance();
        Statement elseBlockTest = sourceTest.newInstance();
        Condition cTest = sourceTest.disassembleIfElse(thenBlockTest,
                elseBlockTest);
        Statement sTest = blockTest.newInstance();

        /*
         * The call
         */
        sTest.assembleIfElse(cTest, thenBlockTest, elseBlockTest);

        /*
         * Evaluation
         */
        assertEquals(emptyBlock, thenBlockTest);
        assertEquals(emptyBlock, elseBlockTest);
        assertEquals(sRef, sTest);
    }

    /**
     * Test assembleIfElse to an if-else-statements file(position 2).
     */
    @Test
    public final void testAssembleIfElse3() {
        /*
         * Setup
         */
        final int ifElsePos = 2;
        Statement blockTest = this.createFromFileTest(FILE_NAME_IF_ELSE);
        Statement blockRef = this.createFromFileRef(FILE_NAME_IF_ELSE);
        Statement emptyBlock = blockRef.newInstance();
        Statement sourceTest = blockTest.removeFromBlock(ifElsePos);
        Statement sRef = blockRef.removeFromBlock(ifElsePos);
        Statement thenBlockTest = sourceTest.newInstance();
        Statement elseBlockTest = sourceTest.newInstance();
        Condition cTest = sourceTest.disassembleIfElse(thenBlockTest,
                elseBlockTest);
        Statement sTest = blockTest.newInstance();

        /*
         * The call
         */
        sTest.assembleIfElse(cTest, thenBlockTest, elseBlockTest);

        /*
         * Evaluation
         */
        assertEquals(emptyBlock, thenBlockTest);
        assertEquals(emptyBlock, elseBlockTest);
        assertEquals(sRef, sTest);
    }

    /**
     * Test assembleIfElse to an mix-statements file(position 2).
     */
    @Test
    public final void testAssembleIfElse4() {
        /*
         * Setup
         */
        final int ifElsePos = 6;
        Statement blockTest = this.createFromFileTest(FILE_NAME_MIX);
        Statement blockRef = this.createFromFileRef(FILE_NAME_MIX);
        Statement emptyBlock = blockRef.newInstance();
        Statement sourceTest = blockTest.removeFromBlock(ifElsePos);
        Statement sRef = blockRef.removeFromBlock(ifElsePos);
        Statement thenBlockTest = sourceTest.newInstance();
        Statement elseBlockTest = sourceTest.newInstance();
        Condition cTest = sourceTest.disassembleIfElse(thenBlockTest,
                elseBlockTest);
        Statement sTest = blockTest.newInstance();

        /*
         * The call
         */
        sTest.assembleIfElse(cTest, thenBlockTest, elseBlockTest);

        /*
         * Evaluation
         */
        assertEquals(emptyBlock, thenBlockTest);
        assertEquals(emptyBlock, elseBlockTest);
        assertEquals(sRef, sTest);
    }

    /**
     * Test disassembleIfElse to an if-else file(Position 3).
     */
    @Test
    public final void testDisassembleIfElse2() {
        /*
         * Setup
         */
        final int ifElsePos = 3;
        Statement blockTest = this.createFromFileTest(FILE_NAME_IF_ELSE);
        Statement blockRef = this.createFromFileRef(FILE_NAME_IF_ELSE);
        Statement sTest = blockTest.removeFromBlock(ifElsePos);
        Statement sRef = blockRef.removeFromBlock(ifElsePos);
        Statement thenBlockTest = sTest.newInstance();
        Statement elseBlockTest = sTest.newInstance();
        Statement thenBlockRef = sRef.newInstance();
        Statement elseBlockRef = sRef.newInstance();
        Condition cRef = sRef.disassembleIfElse(thenBlockRef, elseBlockRef);

        /*
         * The call
         */
        Condition cTest = sTest.disassembleIfElse(thenBlockTest, elseBlockTest);

        /*
         * Evaluation
         */
        assertEquals(cRef, cTest);
        assertEquals(thenBlockRef, thenBlockTest);
        assertEquals(elseBlockRef, elseBlockTest);
        assertEquals(sRef, sTest);
    }

    /**
     * Test disassembleIfElse to an if-else file(Position 0).
     */
    @Test
    public final void testDisassembleIfElse3() {
        /*
         * Setup
         */
        final int ifElsePos = 0;
        Statement blockTest = this.createFromFileTest(FILE_NAME_IF_ELSE);
        Statement blockRef = this.createFromFileRef(FILE_NAME_IF_ELSE);
        Statement sTest = blockTest.removeFromBlock(ifElsePos);
        Statement sRef = blockRef.removeFromBlock(ifElsePos);
        Statement thenBlockTest = sTest.newInstance();
        Statement elseBlockTest = sTest.newInstance();
        Statement thenBlockRef = sRef.newInstance();
        Statement elseBlockRef = sRef.newInstance();
        Condition cRef = sRef.disassembleIfElse(thenBlockRef, elseBlockRef);

        /*
         * The call
         */
        Condition cTest = sTest.disassembleIfElse(thenBlockTest, elseBlockTest);

        /*
         * Evaluation
         */
        assertEquals(cRef, cTest);
        assertEquals(thenBlockRef, thenBlockTest);
        assertEquals(elseBlockRef, elseBlockTest);
        assertEquals(sRef, sTest);
    }

    /**
     * Test disassembleIfElse to an mix file(Position 6).
     */
    @Test
    public final void testDisassembleIfElse4() {
        /*
         * Setup
         */
        final int ifElsePos = 6;
        Statement blockTest = this.createFromFileTest(FILE_NAME_MIX);
        Statement blockRef = this.createFromFileRef(FILE_NAME_MIX);
        Statement sTest = blockTest.removeFromBlock(ifElsePos);
        Statement sRef = blockRef.removeFromBlock(ifElsePos);
        Statement thenBlockTest = sTest.newInstance();
        Statement elseBlockTest = sTest.newInstance();
        Statement thenBlockRef = sRef.newInstance();
        Statement elseBlockRef = sRef.newInstance();
        Condition cRef = sRef.disassembleIfElse(thenBlockRef, elseBlockRef);

        /*
         * The call
         */
        Condition cTest = sTest.disassembleIfElse(thenBlockTest, elseBlockTest);

        /*
         * Evaluation
         */
        assertEquals(cRef, cTest);
        assertEquals(thenBlockRef, thenBlockTest);
        assertEquals(elseBlockRef, elseBlockTest);
        assertEquals(sRef, sTest);
    }

    /**
     * Test assembleWhile to an mix-statements file.
     */
    @Test
    public final void testAssembleWhile2() {
        /*
         * Setup
         */
        Statement blockTest = this.createFromFileTest(FILE_NAME_MIX);
        Statement blockRef = this.createFromFileRef(FILE_NAME_MIX);
        Statement emptyBlock = blockRef.newInstance();
        Statement sourceTest = blockTest.removeFromBlock(3);
        Statement sourceRef = blockRef.removeFromBlock(3);
        Statement nestedTest = sourceTest.newInstance();
        Statement nestedRef = sourceRef.newInstance();
        Condition cTest = sourceTest.disassembleIf(nestedTest);
        Condition cRef = sourceRef.disassembleIf(nestedRef);
        Statement sRef = sourceRef.newInstance();
        sRef.assembleWhile(cRef, nestedRef);
        Statement sTest = sourceTest.newInstance();

        /*
         * The call
         */
        sTest.assembleWhile(cTest, nestedTest);

        /*
         * Evaluation
         */
        assertEquals(emptyBlock, nestedTest);
        assertEquals(sRef, sTest);
    }

    /**
     * Test assembleWhile to an if-statements file.
     */
    @Test
    public final void testAssembleWhile3() {
        /*
         * Setup
         */
        Statement blockTest = this.createFromFileTest(FILE_NAME_IF);
        Statement blockRef = this.createFromFileRef(FILE_NAME_IF);
        Statement emptyBlock = blockRef.newInstance();
        Statement sourceTest = blockTest.removeFromBlock(2);
        Statement sourceRef = blockRef.removeFromBlock(2);
        Statement nestedTest = sourceTest.newInstance();
        Statement nestedRef = sourceRef.newInstance();
        Condition cTest = sourceTest.disassembleIf(nestedTest);
        Condition cRef = sourceRef.disassembleIf(nestedRef);
        Statement sRef = sourceRef.newInstance();
        sRef.assembleWhile(cRef, nestedRef);
        Statement sTest = sourceTest.newInstance();

        /*
         * The call
         */
        sTest.assembleWhile(cTest, nestedTest);

        /*
         * Evaluation
         */
        assertEquals(emptyBlock, nestedTest);
        assertEquals(sRef, sTest);
    }

    /**
     * Test disassembleWhile to a while-statements file.
     */
    @Test
    public final void testDisassembleWhile2() {
        /*
         * Setup
         */
        final int whilePos = 2;
        Statement blockTest = this.createFromFileTest(FILE_NAME_WHILE);
        Statement blockRef = this.createFromFileRef(FILE_NAME_WHILE);
        Statement sTest = blockTest.removeFromBlock(whilePos);
        Statement sRef = blockRef.removeFromBlock(whilePos);
        Statement nestedTest = sTest.newInstance();
        Statement nestedRef = sRef.newInstance();
        Condition cRef = sRef.disassembleWhile(nestedRef);

        /*
         * The call
         */
        Condition cTest = sTest.disassembleWhile(nestedTest);

        /*
         * Evaluation
         */
        assertEquals(nestedRef, nestedTest);
        assertEquals(sRef, sTest);
        assertEquals(cRef, cTest);
    }

    /**
     * Test disassembleWhile to a mix-statements file.
     */
    @Test
    public final void testDisassembleWhile3() {
        /*
         * Setup
         */
        final int whilePos = 2;
        Statement blockTest = this.createFromFileTest(FILE_NAME_MIX);
        Statement blockRef = this.createFromFileRef(FILE_NAME_MIX);
        Statement sTest = blockTest.removeFromBlock(whilePos);
        Statement sRef = blockRef.removeFromBlock(whilePos);
        Statement nestedTest = sTest.newInstance();
        Statement nestedRef = sRef.newInstance();
        Condition cRef = sRef.disassembleWhile(nestedRef);

        /*
         * The call
         */
        Condition cTest = sTest.disassembleWhile(nestedTest);

        /*
         * Evaluation
         */
        assertEquals(nestedRef, nestedTest);
        assertEquals(sRef, sTest);
        assertEquals(cRef, cTest);
    }

    /**
     * Test assembleCall to an empty file.
     */
    @Test
    public final void testAssembleCallEmpty() {
        /*
         * Setup
         */
        Statement sRef = this.createFromFileRef(FILE_NAME_EMPTY);
        Statement sTest = this.createFromFileRef(FILE_NAME_EMPTY);

        String name = "move";
        sRef.assembleCall(name);

        /*
         * The call
         */
        sTest.assembleCall(name);

        /*
         * Evaluation
         */
        assertEquals(sRef, sTest);
    }

    /**
     * Test assembleCall to an call statements file.
     */
    @Test
    public final void testAssembleCall3() {
        /*
         * Setup
         */
        Statement sRef = this.createFromFileRef(FILE_NAME_CALL);
        Statement sTest = this.createFromFileRef(FILE_NAME_CALL);

        String name = "move";
        sRef.assembleCall(name);

        /*
         * The call
         */
        sTest.assembleCall(name);

        /*
         * Evaluation
         */
        assertEquals(sRef, sTest);
    }

    /**
     * Test assembleCall to an mix statements file.
     */
    @Test
    public final void testAssembleCall4() {
        /*
         * Setup
         */
        Statement sRef = this.createFromFileRef(FILE_NAME_MIX);
        Statement sTest = this.createFromFileRef(FILE_NAME_MIX);

        String name = "skip";
        sRef.assembleCall(name);

        /*
         * The call
         */
        sTest.assembleCall(name);

        /*
         * Evaluation
         */
        assertEquals(sRef, sTest);
    }

    /**
     * Test disassembleCall to a call statements file.
     */
    @Test
    public final void testDisassembleCall2() {
        /*
         * Setup
         */
        Statement blockTest = this.createFromFileTest(FILE_NAME_CALL);
        Statement blockRef = this.createFromFileRef(FILE_NAME_CALL);
        Statement sTest = blockTest.removeFromBlock(5);
        Statement sRef = blockRef.removeFromBlock(5);
        String nRef = sRef.disassembleCall();

        /*
         * The call
         */
        String nTest = sTest.disassembleCall();

        /*
         * Evaluation
         */
        assertEquals(sRef, sTest);
        assertEquals(nRef, nTest);
    }

    /**
     * Test disassembleCall to a mix statements file.
     */
    @Test
    public final void testDisassembleCall3() {
        /*
         * Setup
         */
        Statement blockTest = this.createFromFileTest(FILE_NAME_MIX);
        Statement blockRef = this.createFromFileRef(FILE_NAME_MIX);
        Statement sTest = blockTest.removeFromBlock(4);
        Statement sRef = blockRef.removeFromBlock(4);
        String nRef = sRef.disassembleCall();

        /*
         * The call
         */
        String nTest = sTest.disassembleCall();

        /*
         * Evaluation
         */
        assertEquals(sRef, sTest);
        assertEquals(nRef, nTest);
    }

    /**
     * Test disassembleCall to a mix statements file.
     */
    @Test
    public final void testDisassembleCall4() {
        /*
         * Setup
         */
        Statement blockTest = this.createFromFileTest(FILE_NAME_MIX);
        Statement blockRef = this.createFromFileRef(FILE_NAME_MIX);
        Statement sTest = blockTest.removeFromBlock(0);
        Statement sRef = blockRef.removeFromBlock(0);
        String nRef = sRef.disassembleCall();

        /*
         * The call
         */
        String nTest = sTest.disassembleCall();

        /*
         * Evaluation
         */
        assertEquals(sRef, sTest);
        assertEquals(nRef, nTest);
    }

}
