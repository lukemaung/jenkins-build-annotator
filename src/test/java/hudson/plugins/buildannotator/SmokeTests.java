package hudson.plugins.buildannotator;

import hudson.slaves.DumbSlave;
import junit.framework.Assert;

public class SmokeTests extends BaseTest {
    /**
     * create non null entry
     *
     * @throws Exception
     */
    public void testCreateRetrieve() throws Exception {
        DumbSlave slave = createSlaveNode();
        setUpAndRunProject(slave);
        AnnotationAction annotator = build.getAction(AnnotationAction.class);
        annotator.doSet(null, null, "test", "success");
        Assert.assertEquals("success", annotator.getProperties().get("test"));
    }

    /**
     * update non null value
     *
     * @throws Exception
     */
    public void testUpdate() throws Exception {
        DumbSlave slave = createSlaveNode();
        setUpAndRunProject(slave);
        AnnotationAction annotator = build.getAction(AnnotationAction.class);
        annotator.doSet(null, null, "test", "before");
        annotator.doSet(null, null, "test", "after");
        Assert.assertEquals("after", annotator.getProperties().get("test"));
    }

    /**
     * update with null value
     *
     * @throws Exception
     */
    public void testUpdateWithNullValue() throws Exception {
        DumbSlave slave = createSlaveNode();
        setUpAndRunProject(slave);
        AnnotationAction annotator = build.getAction(AnnotationAction.class);
        annotator.doSet(null, null, "test", "before");
        annotator.doSet(null, null, "test", null);
        Assert.assertEquals("before", annotator.getProperties().get("test"));
    }

    /**
     * delete non null entry
     *
     * @throws Exception
     */
    public void testDelete() throws Exception {
        DumbSlave slave = createSlaveNode();
        setUpAndRunProject(slave);
        AnnotationAction annotator = build.getAction(AnnotationAction.class);
        annotator.doSet(null, null, "test", "success");
        Assert.assertEquals("success", annotator.getProperties().get("test"));
        annotator.doDelete(null, null, "test");
        Assert.assertNull(annotator.getProperties().get("test"));
    }

    /**
     * delete null entry - rejected
     *
     * @throws Exception
     */
    public void testDeleteNullKey() throws Exception {
        DumbSlave slave = createSlaveNode();
        setUpAndRunProject(slave);
        AnnotationAction annotator = build.getAction(AnnotationAction.class);
        annotator.doSet(null, null, "test", "success");
        Assert.assertEquals("success", annotator.getProperties().get("test"));
        annotator.doDelete(null, null, null);
        Assert.assertEquals(1, annotator.getProperties().size());
    }

    /**
     * create with null key - rejected
     *
     * @throws Exception
     */
    public void testCreateWithNullKey() throws Exception {
        DumbSlave slave = createSlaveNode();
        setUpAndRunProject(slave);
        AnnotationAction annotator = build.getAction(AnnotationAction.class);
        annotator.doSet(null, null, null, "success");
        Assert.assertEquals(0, annotator.getProperties().size());
    }

    /**
     * create with null value - rejected
     *
     * @throws Exception
     */
    public void testCreateWithNullValue() throws Exception {
        DumbSlave slave = createSlaveNode();
        setUpAndRunProject(slave);
        AnnotationAction annotator = build.getAction(AnnotationAction.class);
        annotator.doSet(null, null, "key", null);
        Assert.assertEquals(0, annotator.getProperties().size());
    }

    /**
     * create multiple entries
     *
     * @throws Exception
     */
    public void testCreateMultiple() throws Exception {
        DumbSlave slave = createSlaveNode();
        setUpAndRunProject(slave);
        AnnotationAction annotator = build.getAction(AnnotationAction.class);
        for (int i = 0; i < 100; i++) {
            annotator.doSet(null, null, "key" + i, "success" + i);
        }
        for (int i = 0; i < 100; i++) {
            Assert.assertEquals("success" + i,
                    annotator.getProperties().get("key" + i));
        }
    }
}
