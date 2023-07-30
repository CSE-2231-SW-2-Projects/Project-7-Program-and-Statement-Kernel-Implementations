import static org.junit.Assert.assertEquals;

import org.junit.Test;

import components.map.Map;
import components.map.Map.Pair;
import components.program.Program;
import components.simplereader.SimpleReader;
import components.simplereader.SimpleReader1L;
import components.statement.Statement;

/**
 * JUnit test fixture for {@code Program}'s constructor and kernel methods.
 *
 * @author Wayne Heym
 * @author Zheyuan Gao
 * @author Cedric Fausey
 *
 */
public abstract class ProgramTest {

    /**
     * The name of a file containing a BL program.
     */
    private static final String FILE_NAME_1 = "data/program-sample.bl";
    private static final String FILE_NAME_NO_INST = "data/program-NoInstruction.bl";
    private static final String FILE_NAME_ONE_INST = "data/program-OneInstruction.bl";
    private static final String FILE_NAME_TWO_INST = "data/program-TwoInstructions.bl";
    private static final String FILE_NAME_THREE_INST = "data/program-ThreeInstructions.bl";
    private static final String FILE_NAME_FOUR_INST = "data/program-FourInstructions.bl";
    private static final String FILE_NAME_MESSY = "data/program-TestPrettyPrint.bl";

    /**
     * Invokes the {@code Program} constructor for the implementation under test
     * and returns the result.
     *
     * @return the new program
     * @ensures constructor = ("Unnamed", {}, compose((BLOCK, ?, ?), <>))
     */
    protected abstract Program constructorTest();

    /**
     * Invokes the {@code Program} constructor for the reference implementation
     * and returns the result.
     *
     * @return the new program
     * @ensures constructor = ("Unnamed", {}, compose((BLOCK, ?, ?), <>))
     */
    protected abstract Program constructorRef();

    /**
     *
     * Creates and returns a {@code Program}, of the type of the implementation
     * under test, from the file with the given name.
     *
     * @param filename
     *            the name of the file to be parsed to create the program
     * @return the constructed program
     * @ensures createFromFile = [the program as parsed from the file]
     */
    private Program createFromFileTest(String filename) {
        Program p = this.constructorTest();
        SimpleReader file = new SimpleReader1L(filename);
        p.parse(file);
        file.close();
        return p;
    }

    /**
     *
     * Creates and returns a {@code Program}, of the reference implementation
     * type, from the file with the given name.
     *
     * @param filename
     *            the name of the file to be parsed to create the program
     * @return the constructed program
     * @ensures createFromFile = [the program as parsed from the file]
     */
    private Program createFromFileRef(String filename) {
        Program p = this.constructorRef();
        SimpleReader file = new SimpleReader1L(filename);
        p.parse(file);
        file.close();
        return p;
    }

    /**
     * Test constructor.
     */
    @Test
    public final void testConstructor() {
        /*
         * Setup
         */
        Program pRef = this.constructorRef();

        /*
         * The call
         */
        Program pTest = this.constructorTest();

        /*
         * Evaluation
         */
        assertEquals(pRef, pTest);
    }

    /**
     * Test name.
     */
    @Test
    public final void testName() {
        /*
         * Setup
         */
        Program pTest = this.createFromFileTest(FILE_NAME_1);
        Program pRef = this.createFromFileRef(FILE_NAME_1);

        /*
         * The call
         */
        String result = pTest.name();

        /*
         * Evaluation
         */
        assertEquals(pRef, pTest);
        assertEquals("Test", result);
    }

    /**
     * Test setName.
     */
    @Test
    public final void testSetName() {
        /*
         * Setup
         */
        Program pTest = this.createFromFileTest(FILE_NAME_1);
        Program pRef = this.createFromFileRef(FILE_NAME_1);
        String newName = "Replacement";
        pRef.setName(newName);

        /*
         * The call
         */
        pTest.setName(newName);

        /*
         * Evaluation
         */
        assertEquals(pRef, pTest);
    }

    /**
     * Test newContext.
     */
    @Test
    public final void testNewContext() {
        /*
         * Setup
         */
        Program pTest = this.createFromFileTest(FILE_NAME_1);
        Program pRef = this.createFromFileRef(FILE_NAME_1);
        Map<String, Statement> cRef = pRef.newContext();

        /*
         * The call
         */
        Map<String, Statement> cTest = pTest.newContext();

        /*
         * Evaluation
         */
        assertEquals(pRef, pTest);
        assertEquals(cRef, cTest);
    }

    /**
     * Test swapContext.
     */
    @Test
    public final void testSwapContext() {
        /*
         * Setup
         */
        Program pTest = this.createFromFileTest(FILE_NAME_1);
        Program pRef = this.createFromFileRef(FILE_NAME_1);
        Map<String, Statement> contextRef = pRef.newContext();
        Map<String, Statement> contextTest = pTest.newContext();
        String oneName = "one";
        pRef.swapContext(contextRef);
        Pair<String, Statement> oneRef = contextRef.remove(oneName);
        /* contextRef now has just "two" */
        pRef.swapContext(contextRef);
        /* pRef's context now has just "two" */
        contextRef.add(oneRef.key(), oneRef.value());
        /* contextRef now has just "one" */

        /* Make the reference call, replacing, in pRef, "one" with "two": */
        pRef.swapContext(contextRef);

        pTest.swapContext(contextTest);
        Pair<String, Statement> oneTest = contextTest.remove(oneName);
        /* contextTest now has just "two" */
        pTest.swapContext(contextTest);
        /* pTest's context now has just "two" */
        contextTest.add(oneTest.key(), oneTest.value());
        /* contextTest now has just "one" */

        /*
         * The call
         */
        pTest.swapContext(contextTest);

        /*
         * Evaluation
         */
        assertEquals(pRef, pTest);
        assertEquals(contextRef, contextTest);
    }

    /**
     * Test newBody.
     */
    @Test
    public final void testNewBody() {
        /*
         * Setup
         */
        Program pTest = this.createFromFileTest(FILE_NAME_1);
        Program pRef = this.createFromFileRef(FILE_NAME_1);
        Statement bRef = pRef.newBody();

        /*
         * The call
         */
        Statement bTest = pTest.newBody();

        /*
         * Evaluation
         */
        assertEquals(pRef, pTest);
        assertEquals(bRef, bTest);
    }

    /**
     * Test swapBody.
     */
    @Test
    public final void testSwapBody() {
        /*
         * Setup
         */
        Program pTest = this.createFromFileTest(FILE_NAME_1);
        Program pRef = this.createFromFileRef(FILE_NAME_1);
        Statement bodyRef = pRef.newBody();
        Statement bodyTest = pTest.newBody();
        pRef.swapBody(bodyRef);
        Statement firstRef = bodyRef.removeFromBlock(0);
        /* bodyRef now lacks the first statement */
        pRef.swapBody(bodyRef);
        /* pRef's body now lacks the first statement */
        bodyRef.addToBlock(0, firstRef);
        /* bodyRef now has just the first statement */

        /* Make the reference call, replacing, in pRef, remaining with first: */
        pRef.swapBody(bodyRef);

        pTest.swapBody(bodyTest);
        Statement firstTest = bodyTest.removeFromBlock(0);
        /* bodyTest now lacks the first statement */
        pTest.swapBody(bodyTest);
        /* pTest's body now lacks the first statement */
        bodyTest.addToBlock(0, firstTest);
        /* bodyTest now has just the first statement */

        /*
         * The call
         */
        pTest.swapBody(bodyTest);

        /*
         * Evaluation
         */
        assertEquals(pRef, pTest);
        assertEquals(bodyRef, bodyTest);
    }

    // TODO - provide additional test cases to thoroughly test ProgramKernel

    /**
     * Test name2.
     */
    @Test
    public final void testName2() {
        /*
         * Setup
         */
        Program pTest = this.createFromFileTest(FILE_NAME_NO_INST);
        Program pRef = this.createFromFileRef(FILE_NAME_NO_INST);

        /*
         * The call
         */
        String result = pTest.name();

        /*
         * Evaluation
         */
        assertEquals(pRef, pTest);
        assertEquals("Test-NoInstruction", result);
    }

    /**
     * Test name3.
     */
    @Test
    public final void testName3() {
        /*
         * Setup
         */
        Program pTest = this.createFromFileTest(FILE_NAME_ONE_INST);
        Program pRef = this.createFromFileRef(FILE_NAME_ONE_INST);

        /*
         * The call
         */
        String result = pTest.name();

        /*
         * Evaluation
         */
        assertEquals(pRef, pTest);
        assertEquals("Test-OneInstruction", result);
    }

    /**
     * Test name4.
     */
    @Test
    public final void testName4() {
        /*
         * Setup
         */
        Program pTest = this.createFromFileTest(FILE_NAME_TWO_INST);
        Program pRef = this.createFromFileRef(FILE_NAME_TWO_INST);

        /*
         * The call
         */
        String result = pTest.name();

        /*
         * Evaluation
         */
        assertEquals(pRef, pTest);
        assertEquals("Test-TwoInstructions", result);
    }

    /**
     * Test name5.
     */
    @Test
    public final void testName5() {
        /*
         * Setup
         */
        Program pTest = this.createFromFileTest(FILE_NAME_THREE_INST);
        Program pRef = this.createFromFileRef(FILE_NAME_THREE_INST);

        /*
         * The call
         */
        String result = pTest.name();

        /*
         * Evaluation
         */
        assertEquals(pRef, pTest);
        assertEquals("Test-ThreeInstructions", result);
    }

    /**
     * Test name6.
     */
    @Test
    public final void testName6() {
        /*
         * Setup
         */
        Program pTest = this.createFromFileTest(FILE_NAME_FOUR_INST);
        Program pRef = this.createFromFileRef(FILE_NAME_FOUR_INST);

        /*
         * The call
         */
        String result = pTest.name();

        /*
         * Evaluation
         */
        assertEquals(pRef, pTest);
        assertEquals("Test-FourInstructions", result);
    }

    /**
     * Test name7.
     */
    @Test
    public final void testName7() {
        /*
         * Setup
         */
        Program pTest = this.createFromFileTest(FILE_NAME_MESSY);
        Program pRef = this.createFromFileRef(FILE_NAME_MESSY);

        /*
         * The call
         */
        String result = pTest.name();

        /*
         * Evaluation
         */
        assertEquals(pRef, pTest);
        assertEquals("TestPrettyPrint", result);
    }

    /**
     * Test setName2.
     */
    @Test
    public final void testSetName2() {
        /*
         * Setup
         */
        Program pTest = this.createFromFileTest(FILE_NAME_NO_INST);
        Program pRef = this.createFromFileRef(FILE_NAME_NO_INST);
        String newName = "BadProgram";
        pRef.setName(newName);

        /*
         * The call
         */
        pTest.setName(newName);

        /*
         * Evaluation
         */
        assertEquals(pRef, pTest);
    }

    /**
     * Test setName3.
     */
    @Test
    public final void testSetName3() {
        /*
         * Setup
         */
        Program pTest = this.createFromFileTest(FILE_NAME_ONE_INST);
        Program pRef = this.createFromFileRef(FILE_NAME_ONE_INST);
        String newName = "StillBadProgram";
        pRef.setName(newName);

        /*
         * The call
         */
        pTest.setName(newName);

        /*
         * Evaluation
         */
        assertEquals(pRef, pTest);
    }

    /**
     * Test setName4.
     */
    @Test
    public final void testSetName4() {
        /*
         * Setup
         */
        Program pTest = this.createFromFileTest(FILE_NAME_TWO_INST);
        Program pRef = this.createFromFileRef(FILE_NAME_TWO_INST);
        String newName = "WhatABadProgram";
        pRef.setName(newName);

        /*
         * The call
         */
        pTest.setName(newName);

        /*
         * Evaluation
         */
        assertEquals(pRef, pTest);
    }

    /**
     * Test setName5.
     */
    @Test
    public final void testSetName5() {
        /*
         * Setup
         */
        Program pTest = this.createFromFileTest(FILE_NAME_THREE_INST);
        Program pRef = this.createFromFileRef(FILE_NAME_THREE_INST);
        String newName = "WorstProgram";
        pRef.setName(newName);

        /*
         * The call
         */
        pTest.setName(newName);

        /*
         * Evaluation
         */
        assertEquals(pRef, pTest);
    }

    /**
     * Test setName6.
     */
    @Test
    public final void testSetName6() {
        /*
         * Setup
         */
        Program pTest = this.createFromFileTest(FILE_NAME_FOUR_INST);
        Program pRef = this.createFromFileRef(FILE_NAME_FOUR_INST);
        String newName = "MightBeAFineProgram";
        pRef.setName(newName);

        /*
         * The call
         */
        pTest.setName(newName);

        /*
         * Evaluation
         */
        assertEquals(pRef, pTest);
    }

    /**
     * Test setName7.
     */
    @Test
    public final void testSetName7() {
        /*
         * Setup
         */
        Program pTest = this.createFromFileTest(FILE_NAME_MESSY);
        Program pRef = this.createFromFileRef(FILE_NAME_MESSY);
        String newName = "MessyProgram";
        pRef.setName(newName);

        /*
         * The call
         */
        pTest.setName(newName);

        /*
         * Evaluation
         */
        assertEquals(pRef, pTest);
    }

    /**
     * Test newContext2.
     */
    @Test
    public final void testNewContext2() {
        /*
         * Setup
         */
        Program pTest = this.createFromFileTest(FILE_NAME_NO_INST);
        Program pRef = this.createFromFileRef(FILE_NAME_NO_INST);
        Map<String, Statement> cRef = pRef.newContext();

        /*
         * The call
         */
        Map<String, Statement> cTest = pTest.newContext();

        /*
         * Evaluation
         */
        assertEquals(pRef, pTest);
        assertEquals(cRef, cTest);
    }

    /**
     * Test newContext3.
     */
    @Test
    public final void testNewContext3() {
        /*
         * Setup
         */
        Program pTest = this.createFromFileTest(FILE_NAME_ONE_INST);
        Program pRef = this.createFromFileRef(FILE_NAME_ONE_INST);
        Map<String, Statement> cRef = pRef.newContext();

        /*
         * The call
         */
        Map<String, Statement> cTest = pTest.newContext();

        /*
         * Evaluation
         */
        assertEquals(pRef, pTest);
        assertEquals(cRef, cTest);
    }

    /**
     * Test newContext4.
     */
    @Test
    public final void testNewContext4() {
        /*
         * Setup
         */
        Program pTest = this.createFromFileTest(FILE_NAME_TWO_INST);
        Program pRef = this.createFromFileRef(FILE_NAME_TWO_INST);
        Map<String, Statement> cRef = pRef.newContext();

        /*
         * The call
         */
        Map<String, Statement> cTest = pTest.newContext();

        /*
         * Evaluation
         */
        assertEquals(pRef, pTest);
        assertEquals(cRef, cTest);
    }

    /**
     * Test newContext5.
     */
    @Test
    public final void testNewContext5() {
        /*
         * Setup
         */
        Program pTest = this.createFromFileTest(FILE_NAME_THREE_INST);
        Program pRef = this.createFromFileRef(FILE_NAME_THREE_INST);
        Map<String, Statement> cRef = pRef.newContext();

        /*
         * The call
         */
        Map<String, Statement> cTest = pTest.newContext();

        /*
         * Evaluation
         */
        assertEquals(pRef, pTest);
        assertEquals(cRef, cTest);
    }

    /**
     * Test newContext6.
     */
    @Test
    public final void testNewContext6() {
        /*
         * Setup
         */
        Program pTest = this.createFromFileTest(FILE_NAME_FOUR_INST);
        Program pRef = this.createFromFileRef(FILE_NAME_FOUR_INST);
        Map<String, Statement> cRef = pRef.newContext();

        /*
         * The call
         */
        Map<String, Statement> cTest = pTest.newContext();

        /*
         * Evaluation
         */
        assertEquals(pRef, pTest);
        assertEquals(cRef, cTest);
    }

    /**
     * Test newContext7.
     */
    @Test
    public final void testNewContext7() {
        /*
         * Setup
         */
        Program pTest = this.createFromFileTest(FILE_NAME_MESSY);
        Program pRef = this.createFromFileRef(FILE_NAME_MESSY);
        Map<String, Statement> cRef = pRef.newContext();

        /*
         * The call
         */
        Map<String, Statement> cTest = pTest.newContext();

        /*
         * Evaluation
         */
        assertEquals(pRef, pTest);
        assertEquals(cRef, cTest);
    }

    /**
     * Test swapContext2.
     */
    @Test
    public final void testSwapContext2() {
        /*
         * Setup
         */
        Program pTest = this.createFromFileTest(FILE_NAME_ONE_INST);
        Program pRef = this.createFromFileRef(FILE_NAME_ONE_INST);
        Map<String, Statement> contextRef = pRef.newContext();
        Map<String, Statement> contextTest = pTest.newContext();
        String oneName = "AttackEnemy";
        pRef.swapContext(contextRef);
        Pair<String, Statement> oneRef = contextRef.remove(oneName);
        /* contextRef now has no context */
        pRef.swapContext(contextRef);
        /* pRef's context now has has no context */
        contextRef.add(oneRef.key(), oneRef.value());
        /* contextRef now has just "AttackEnemy" */

        /*
         * Make the reference call, replacing, in pRef, empty with
         * "AttackEnemy":
         */
        pRef.swapContext(contextRef);

        pTest.swapContext(contextTest);
        Pair<String, Statement> oneTest = contextTest.remove(oneName);
        /* contextTest now has no context */
        pTest.swapContext(contextTest);
        /* pTest's context now has no context */
        contextTest.add(oneTest.key(), oneTest.value());
        /* contextTest now has "AttackEnemy" */

        /*
         * The call
         */
        pTest.swapContext(contextTest);

        /*
         * Evaluation
         */
        assertEquals(pRef, pTest);
        assertEquals(contextRef, contextTest);
    }

    /**
     * Test swapContext3.
     */
    @Test
    public final void testSwapContext3() {
        /*
         * Setup
         */
        Program pTest = this.createFromFileTest(FILE_NAME_TWO_INST);
        Program pRef = this.createFromFileRef(FILE_NAME_TWO_INST);
        Map<String, Statement> contextRef = pRef.newContext();
        Map<String, Statement> contextTest = pTest.newContext();
        String oneName = "FindWay";
        pRef.swapContext(contextRef);
        Pair<String, Statement> oneRef = contextRef.remove(oneName);
        /* contextRef now has only "AttackEnemy" */
        pRef.swapContext(contextRef);
        /* pRef's context now has has "AttackEnemy" */
        contextRef.add(oneRef.key(), oneRef.value());
        /* contextRef now has just "FindWay" */

        /*
         * Make the reference call, replacing, in pRef, "AttackEnemy" with
         * "FindWay":
         */
        pRef.swapContext(contextRef);

        pTest.swapContext(contextTest);
        Pair<String, Statement> oneTest = contextTest.remove(oneName);
        /* contextTest now has "AttackEnemy" */
        pTest.swapContext(contextTest);
        /* pTest's context now has "AttackEnemy" */
        contextTest.add(oneTest.key(), oneTest.value());
        /* contextTest now has "FindWay" */

        /*
         * The call
         */
        pTest.swapContext(contextTest);

        /*
         * Evaluation
         */
        assertEquals(pRef, pTest);
        assertEquals(contextRef, contextTest);
    }

    /**
     * Test swapContext4.
     */
    @Test
    public final void testSwapContext4() {
        /*
         * Setup
         */
        Program pTest = this.createFromFileTest(FILE_NAME_THREE_INST);
        Program pRef = this.createFromFileRef(FILE_NAME_THREE_INST);
        Map<String, Statement> contextRef = pRef.newContext();
        Map<String, Statement> contextTest = pTest.newContext();
        String oneName = "FindObstacle";
        String secondName = "AttackEnemy";
        pRef.swapContext(contextRef);
        Pair<String, Statement> oneRef = contextRef.remove(oneName);
        Pair<String, Statement> secondRef = contextRef.remove(secondName);
        /* contextRef now has only "FindWay" */
        pRef.swapContext(contextRef);
        /* pRef's context now has has "FindWay" */
        contextRef.add(oneRef.key(), oneRef.value());
        contextRef.add(secondRef.key(), secondRef.value());
        /* contextRef now has "FindObstacle" and "AttackEnemy" */

        /*
         * Make the reference call, replacing, in pRef, "FindWay" with
         * "FindObstacle" and "AttackEnemy".
         */
        pRef.swapContext(contextRef);

        pTest.swapContext(contextTest);
        Pair<String, Statement> oneTest = contextTest.remove(oneName);
        Pair<String, Statement> secondTest = contextTest.remove(secondName);
        /* contextTest now has "FindWay" */
        pTest.swapContext(contextTest);
        /* pTest's context now has "FindWay" */
        contextTest.add(oneTest.key(), oneTest.value());
        contextTest.add(secondTest.key(), secondTest.value());
        /* contextTest now has "FindObstacle" and "AttackEnemy" */

        /*
         * The call
         */
        pTest.swapContext(contextTest);

        /*
         * Evaluation
         */
        assertEquals(pRef, pTest);
        assertEquals(contextRef, contextTest);
    }

    /**
     * Test swapContext5.
     */
    @Test
    public final void testSwapContext5() {
        /*
         * Setup
         */
        Program pTest = this.createFromFileTest(FILE_NAME_FOUR_INST);
        Program pRef = this.createFromFileRef(FILE_NAME_FOUR_INST);
        Map<String, Statement> contextRef = pRef.newContext();
        Map<String, Statement> contextTest = pTest.newContext();
        String oneName = "AvoidFriend";
        String secondName = "AttackEnemy";
        pRef.swapContext(contextRef);
        Pair<String, Statement> oneRef = contextRef.remove(oneName);
        Pair<String, Statement> secondRef = contextRef.remove(secondName);
        /* contextRef now has "FindWay" and "FindObstacle" */
        pRef.swapContext(contextRef);
        /* pRef's context now has has "FindWay" and "FindObstacle" */
        contextRef.add(oneRef.key(), oneRef.value());
        contextRef.add(secondRef.key(), secondRef.value());
        /* contextRef now has "AvoidFriend" and "AttackEnemy" */

        /*
         * Make the reference call, replacing, in pRef, "FindWay" and
         * "FindObstacle" with "AvoidFriend" and "AttackEnemy".
         */
        pRef.swapContext(contextRef);

        pTest.swapContext(contextTest);
        Pair<String, Statement> oneTest = contextTest.remove(oneName);
        Pair<String, Statement> secondTest = contextTest.remove(secondName);
        /* contextTest now has "FindWay" and "FindObstacle" */
        pTest.swapContext(contextTest);
        /* pTest's context now has "FindWay" and "FindObstacle" */
        contextTest.add(oneTest.key(), oneTest.value());
        contextTest.add(secondTest.key(), secondTest.value());
        /* contextTest now has "AvoidFriend" and "AttackEnemy" */

        /*
         * The call
         */
        pTest.swapContext(contextTest);

        /*
         * Evaluation
         */
        assertEquals(pRef, pTest);
        assertEquals(contextRef, contextTest);
    }

    /**
     * Test swapContext6.
     */
    @Test
    public final void testSwapContext6() {
        /*
         * Setup
         */
        Program pTest = this.createFromFileTest(FILE_NAME_MESSY);
        Program pRef = this.createFromFileRef(FILE_NAME_MESSY);
        Map<String, Statement> contextRef = pRef.newContext();
        Map<String, Statement> contextTest = pTest.newContext();
        String oneName = "AvoidFriend";
        String secondName = "AttackEnemy";
        pRef.swapContext(contextRef);
        Pair<String, Statement> oneRef = contextRef.remove(oneName);
        Pair<String, Statement> secondRef = contextRef.remove(secondName);
        /* contextRef now has "FindWay" and "FindObstacle" */
        pRef.swapContext(contextRef);
        /* pRef's context now has has "FindWay" and "FindObstacle" */
        contextRef.add(oneRef.key(), oneRef.value());
        contextRef.add(secondRef.key(), secondRef.value());
        /* contextRef now has "AvoidFriend" and "AttackEnemy" */

        /*
         * Make the reference call, replacing, in pRef, "FindWay" and
         * "FindObstacle" with "AvoidFriend" and "AttackEnemy".
         */
        pRef.swapContext(contextRef);

        pTest.swapContext(contextTest);
        Pair<String, Statement> oneTest = contextTest.remove(oneName);
        Pair<String, Statement> secondTest = contextTest.remove(secondName);
        /* contextTest now has "FindWay" and "FindObstacle" */
        pTest.swapContext(contextTest);
        /* pTest's context now has "FindWay" and "FindObstacle" */
        contextTest.add(oneTest.key(), oneTest.value());
        contextTest.add(secondTest.key(), secondTest.value());
        /* contextTest now has "AvoidFriend" and "AttackEnemy" */

        /*
         * The call
         */
        pTest.swapContext(contextTest);

        /*
         * Evaluation
         */
        assertEquals(pRef, pTest);
        assertEquals(contextRef, contextTest);
    }

    /**
     * Test newBody2.
     */
    @Test
    public final void testNewBody2() {
        /*
         * Setup
         */
        Program pTest = this.createFromFileTest(FILE_NAME_NO_INST);
        Program pRef = this.createFromFileRef(FILE_NAME_NO_INST);
        Statement bRef = pRef.newBody();

        /*
         * The call
         */
        Statement bTest = pTest.newBody();

        /*
         * Evaluation
         */
        assertEquals(pRef, pTest);
        assertEquals(bRef, bTest);
    }

    /**
     * Test newBody3.
     */
    @Test
    public final void testNewBody3() {
        /*
         * Setup
         */
        Program pTest = this.createFromFileTest(FILE_NAME_ONE_INST);
        Program pRef = this.createFromFileRef(FILE_NAME_ONE_INST);
        Statement bRef = pRef.newBody();

        /*
         * The call
         */
        Statement bTest = pTest.newBody();

        /*
         * Evaluation
         */
        assertEquals(pRef, pTest);
        assertEquals(bRef, bTest);
    }

    /**
     * Test newBody4.
     */
    @Test
    public final void testNewBody4() {
        /*
         * Setup
         */
        Program pTest = this.createFromFileTest(FILE_NAME_TWO_INST);
        Program pRef = this.createFromFileRef(FILE_NAME_TWO_INST);
        Statement bRef = pRef.newBody();

        /*
         * The call
         */
        Statement bTest = pTest.newBody();

        /*
         * Evaluation
         */
        assertEquals(pRef, pTest);
        assertEquals(bRef, bTest);
    }

    /**
     * Test newBody5.
     */
    @Test
    public final void testNewBody5() {
        /*
         * Setup
         */
        Program pTest = this.createFromFileTest(FILE_NAME_THREE_INST);
        Program pRef = this.createFromFileRef(FILE_NAME_THREE_INST);
        Statement bRef = pRef.newBody();

        /*
         * The call
         */
        Statement bTest = pTest.newBody();

        /*
         * Evaluation
         */
        assertEquals(pRef, pTest);
        assertEquals(bRef, bTest);
    }

    /**
     * Test newBody6.
     */
    @Test
    public final void testNewBody6() {
        /*
         * Setup
         */
        Program pTest = this.createFromFileTest(FILE_NAME_FOUR_INST);
        Program pRef = this.createFromFileRef(FILE_NAME_FOUR_INST);
        Statement bRef = pRef.newBody();

        /*
         * The call
         */
        Statement bTest = pTest.newBody();

        /*
         * Evaluation
         */
        assertEquals(pRef, pTest);
        assertEquals(bRef, bTest);
    }

    /**
     * Test newBody7.
     */
    @Test
    public final void testNewBody7() {
        /*
         * Setup
         */
        Program pTest = this.createFromFileTest(FILE_NAME_MESSY);
        Program pRef = this.createFromFileRef(FILE_NAME_MESSY);
        Statement bRef = pRef.newBody();

        /*
         * The call
         */
        Statement bTest = pTest.newBody();

        /*
         * Evaluation
         */
        assertEquals(pRef, pTest);
        assertEquals(bRef, bTest);
    }

    /**
     * Test swapBody2.
     */
    @Test
    public final void testSwapBody2() {
        /*
         * Setup
         */
        Program pTest = this.createFromFileTest(FILE_NAME_NO_INST);
        Program pRef = this.createFromFileRef(FILE_NAME_NO_INST);
        Statement bodyRef = pRef.newBody();
        Statement bodyTest = pTest.newBody();
        pRef.swapBody(bodyRef);
        Statement firstRef = bodyRef.removeFromBlock(0);
        /* bodyRef now have no statement */
        pRef.swapBody(bodyRef);
        /* pRef's body now have no statement */
        bodyRef.addToBlock(0, firstRef);
        /* bodyRef now has just the first statement */

        /* Make the reference call, replacing, in pRef, remaining with first: */
        pRef.swapBody(bodyRef);

        pTest.swapBody(bodyTest);
        Statement firstTest = bodyTest.removeFromBlock(0);
        /* bodyTest now has no statement */
        pTest.swapBody(bodyTest);
        /* pTest's body now has no statement */
        bodyTest.addToBlock(0, firstTest);
        /* bodyTest now has just the first statement */

        /*
         * The call
         */
        pTest.swapBody(bodyTest);

        /*
         * Evaluation
         */
        assertEquals(pRef, pTest);
        assertEquals(bodyRef, bodyTest);
    }

    /**
     * Test swapBody3.
     */
    @Test
    public final void testSwapBody3() {
        /*
         * Setup
         */
        Program pTest = this.createFromFileTest(FILE_NAME_ONE_INST);
        Program pRef = this.createFromFileRef(FILE_NAME_ONE_INST);
        Statement bodyRef = pRef.newBody();
        Statement bodyTest = pTest.newBody();
        pRef.swapBody(bodyRef);
        Statement firstRef = bodyRef.removeFromBlock(0);
        /* bodyRef now has no statement */
        pRef.swapBody(bodyRef);
        /* pRef's body now has no statement */
        bodyRef.addToBlock(0, firstRef);
        /* bodyRef now has just the first statement */

        /* Make the reference call, replacing, in pRef, remaining with first: */
        pRef.swapBody(bodyRef);

        pTest.swapBody(bodyTest);
        Statement firstTest = bodyTest.removeFromBlock(0);
        /* bodyTest now has no statement */
        pTest.swapBody(bodyTest);
        /* pTest's body has no statement */
        bodyTest.addToBlock(0, firstTest);
        /* bodyTest now has just the first statement */

        /*
         * The call
         */
        pTest.swapBody(bodyTest);

        /*
         * Evaluation
         */
        assertEquals(pRef, pTest);
        assertEquals(bodyRef, bodyTest);
    }

    /**
     * Test swapBody4.
     */
    @Test
    public final void testSwapBody4() {
        /*
         * Setup
         */
        Program pTest = this.createFromFileTest(FILE_NAME_TWO_INST);
        Program pRef = this.createFromFileRef(FILE_NAME_TWO_INST);
        Statement bodyRef = pRef.newBody();
        Statement bodyTest = pTest.newBody();
        pRef.swapBody(bodyRef);
        Statement firstRef = bodyRef.removeFromBlock(0);
        /* bodyRef now lacks the first statement */
        pRef.swapBody(bodyRef);
        /* pRef's body now lacks the first statement */
        bodyRef.addToBlock(0, firstRef);
        /* bodyRef now has just the first statement */

        /* Make the reference call, replacing, in pRef, remaining with first: */
        pRef.swapBody(bodyRef);

        pTest.swapBody(bodyTest);
        Statement firstTest = bodyTest.removeFromBlock(0);
        /* bodyTest now lacks the first statement */
        pTest.swapBody(bodyTest);
        /* pTest's body now lacks the first statement */
        bodyTest.addToBlock(0, firstTest);
        /* bodyTest now has just the first statement */

        /*
         * The call
         */
        pTest.swapBody(bodyTest);

        /*
         * Evaluation
         */
        assertEquals(pRef, pTest);
        assertEquals(bodyRef, bodyTest);
    }

    /**
     * Test swapBody5.
     */
    @Test
    public final void testSwapBody5() {
        /*
         * Setup
         */
        Program pTest = this.createFromFileTest(FILE_NAME_THREE_INST);
        Program pRef = this.createFromFileRef(FILE_NAME_THREE_INST);
        Statement bodyRef = pRef.newBody();
        Statement bodyTest = pTest.newBody();
        pRef.swapBody(bodyRef);
        Statement firstRef = bodyRef.removeFromBlock(0);

        /* bodyRef now lacks the first statement */
        pRef.swapBody(bodyRef);
        /* pRef's body now lacks the first statement */
        bodyRef.addToBlock(0, firstRef);

        /* bodyRef now has just the first statement */

        /*
         * Make the reference call, replacing, in pRef, remaining with first:
         */
        pRef.swapBody(bodyRef);

        pTest.swapBody(bodyTest);
        Statement firstTest = bodyTest.removeFromBlock(0);

        /* bodyTest now lacks the first statement */
        pTest.swapBody(bodyTest);
        /* pTest's body now lacks the first statement */
        bodyTest.addToBlock(0, firstTest);

        /* bodyTest now has the first statement */

        /*
         * The call
         */
        pTest.swapBody(bodyTest);

        /*
         * Evaluation
         */
        assertEquals(pRef, pTest);
        assertEquals(bodyRef, bodyTest);
    }

    /**
     * Test swapBody6.
     */
    @Test
    public final void testSwapBody6() {
        /*
         * Setup
         */
        Program pTest = this.createFromFileTest(FILE_NAME_FOUR_INST);
        Program pRef = this.createFromFileRef(FILE_NAME_FOUR_INST);
        Statement bodyRef = pRef.newBody();
        Statement bodyTest = pTest.newBody();
        pRef.swapBody(bodyRef);
        Statement firstRef = bodyRef.removeFromBlock(0);

        /* bodyRef now lacks the first statement */
        pRef.swapBody(bodyRef);
        /* pRef's body now lacks the first statement */
        bodyRef.addToBlock(0, firstRef);

        /* bodyRef now has just the first statement */

        /*
         * Make the reference call, replacing, in pRef, remaining with first:
         */
        pRef.swapBody(bodyRef);

        pTest.swapBody(bodyTest);
        Statement firstTest = bodyTest.removeFromBlock(0);

        /* bodyTest now lacks the first statement */
        pTest.swapBody(bodyTest);
        /* pTest's body now lacks the first statement */
        bodyTest.addToBlock(0, firstTest);

        /* bodyTest now has the first statement */

        /*
         * The call
         */
        pTest.swapBody(bodyTest);

        /*
         * Evaluation
         */
        assertEquals(pRef, pTest);
        assertEquals(bodyRef, bodyTest);
    }

    /**
     * Test swapBody7.
     */
    @Test
    public final void testSwapBody7() {
        /*
         * Setup
         */
        Program pTest = this.createFromFileTest(FILE_NAME_MESSY);
        Program pRef = this.createFromFileRef(FILE_NAME_MESSY);
        Statement bodyRef = pRef.newBody();
        Statement bodyTest = pTest.newBody();
        pRef.swapBody(bodyRef);
        Statement firstRef = bodyRef.removeFromBlock(0);

        /* bodyRef now lacks the first statement */
        pRef.swapBody(bodyRef);
        /* pRef's body now lacks the first statement */
        bodyRef.addToBlock(0, firstRef);

        /* bodyRef now has just the first statement */

        /*
         * Make the reference call, replacing, in pRef, remaining with first:
         */
        pRef.swapBody(bodyRef);

        pTest.swapBody(bodyTest);
        Statement firstTest = bodyTest.removeFromBlock(0);

        /* bodyTest now lacks the first statement */
        pTest.swapBody(bodyTest);
        /* pTest's body now lacks the first statement */
        bodyTest.addToBlock(0, firstTest);

        /* bodyTest now has the first statement */

        /*
         * The call
         */
        pTest.swapBody(bodyTest);

        /*
         * Evaluation
         */
        assertEquals(pRef, pTest);
        assertEquals(bodyRef, bodyTest);
    }
}
