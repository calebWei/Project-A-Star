package org.softeng306.scheduler;

import org.junit.Assert;
import org.junit.Test;
import org.softeng306.graph.Task;

import java.util.ArrayList;
import java.util.List;

public class ScheduleTest {

    @Test
    public void scheduleCreateEmptyTest(){
        Schedule schedule = new Schedule(new ArrayList<>(), 1);
        Assert.assertNotNull(schedule);
        Assert.assertEquals(0, schedule.getUnscheduledTasks().size());
        Assert.assertEquals(0, schedule.getScheduledTasks().size());
        Assert.assertEquals(1, schedule.getNumProcessors());
    }

    @Test
    public void scheduleCreateWithTaskTest(){
        Task task = new Task("a", 5);
        List<Task> tasks = new ArrayList<>();
        tasks.add(task);
        Schedule schedule = new Schedule(tasks, 1);
        Assert.assertEquals(1, schedule.getUnscheduledTasks().size());
    }

    @Test
    public void scheduleTaskTest(){
        Task task = new Task("a", 5);
        List<Task> tasks = new ArrayList<>();
        tasks.add(task);
        Schedule schedule = new Schedule(tasks, 1);
        task.setStartTime(0);
        task.setProcessorNumber(0);
        schedule.scheduleTask(task);
        Assert.assertEquals(0, schedule.getUnscheduledTasks().size());
        Assert.assertEquals(1, schedule.getScheduledTasks().size());
        Assert.assertTrue(schedule.getScheduledTasks().contains(task));
    }

    @Test
    public void scheduleGetNextTasksTest(){
        Task taskA = new Task("a", 5);
        Task taskB = new Task("b", 2);
        Task taskC = new Task("c", 3);
        Task taskD = new Task("d", 1);

        List<Task> prerequisiteA = new ArrayList<>();
        prerequisiteA.add(taskA);
        taskB.setPrerequisites(prerequisiteA);
        taskC.setPrerequisites(prerequisiteA);

        List<Task> prerequisiteBC = new ArrayList<>();
        prerequisiteBC.add(taskB);
        prerequisiteBC.add(taskC);
        taskD.setPrerequisites(prerequisiteBC);

        List<Task> tasks = new ArrayList<>();
        tasks.add(taskA);
        tasks.add(taskB);
        tasks.add(taskC);
        tasks.add(taskD);

        Schedule schedule = new Schedule(tasks, 2);
        List<Task> nextTasks = schedule.getNextTasks();
        Assert.assertEquals(1, nextTasks.size());
        Assert.assertEquals(taskA, nextTasks.get(0));
        taskA.setStartTime(0);
        taskA.setProcessorNumber(0);
        schedule.scheduleTask(taskA);
        List<Task> expectedNextTasks = new ArrayList<>();
        expectedNextTasks.add(taskB);
        expectedNextTasks.add(taskC);
        Assert.assertTrue(schedule.getNextTasks().containsAll(expectedNextTasks));
    }

    @Test
    public void scheduleMakespanTest(){
        Task task = new Task("a", 5);
        List<Task> tasks = new ArrayList<>();
        tasks.add(task);
        Schedule schedule = new Schedule(tasks, 1);
        task.setProcessorNumber(0);
        task.setStartTime(1);
        schedule.scheduleTask(task);
        Assert.assertEquals(6, schedule.getMakeSpan());
    }

    @Test
    public void scheduleProcFreeTimeTest(){
        Task task = new Task("a", 5);
        List<Task> tasks = new ArrayList<>();
        tasks.add(task);
        Schedule schedule = new Schedule(tasks, 2);
        task.setProcessorNumber(0);
        task.setStartTime(1);
        schedule.scheduleTask(task);
        Assert.assertEquals(6, schedule.getProcFreeTime(0));
        Assert.assertEquals(0, schedule.getProcFreeTime(1));
    }

    @Test
    public void ScheduleCompleteTest(){
        Task taskA = new Task("a", 5);
        Task taskB = new Task("b", 2);
        Task taskC = new Task("c", 3);
        Task taskD = new Task("d", 1);

        List<Task> prerequisiteA = new ArrayList<>();
        prerequisiteA.add(taskA);
        taskB.setPrerequisites(prerequisiteA);
        taskC.setPrerequisites(prerequisiteA);

        List<Task> prerequisiteBC = new ArrayList<>();
        prerequisiteBC.add(taskB);
        prerequisiteBC.add(taskC);
        taskD.setPrerequisites(prerequisiteBC);

        List<Task> tasks = new ArrayList<>();
        tasks.add(taskA);
        tasks.add(taskB);
        tasks.add(taskC);
        tasks.add(taskD);

        Schedule schedule = new Schedule(tasks, 2);
        // Schedule Task A
        taskA.setStartTime(0);
        taskA.setProcessorNumber(0);
        schedule.scheduleTask(taskA);
        // Schedule Task B
        taskB.setStartTime(6);
        taskB.setProcessorNumber(1);
        schedule.scheduleTask(taskB);
        // Schedule Task C
        taskC.setStartTime(5);
        taskC.setProcessorNumber(0);
        schedule.scheduleTask(taskC);
        // Schedule Task D
        taskD.setStartTime(9);
        taskD.setProcessorNumber(0);
        schedule.scheduleTask(taskD);

        Assert.assertTrue(schedule.isComplete());
    }

    // Schedule equivalence is value-based, using task start times for all tasks scheduled on both schedules
    // If both schedules have the same scheduled tasks scheduled at the same times, then they are considered equivalent
    // and their equals() method should return true.
    @Test
    public void scheduleEqualityTest(){
        Task taskA = new Task("a", 5);
        Task taskB = new Task("b", 2);
        Task taskC = new Task("c", 3);
        Task taskD = new Task("d", 1);

        Task taskAA = new Task("a", 5);
        Task taskBB = new Task("b", 2);
        Task taskCC = new Task("c", 3);
        Task taskDD = new Task("d", 1);

        List<Task> prerequisiteA = new ArrayList<>();
        prerequisiteA.add(taskA);
        taskB.setPrerequisites(prerequisiteA);
        taskC.setPrerequisites(prerequisiteA);

        List<Task> prerequisiteBC = new ArrayList<>();
        prerequisiteBC.add(taskB);
        prerequisiteBC.add(taskC);
        taskD.setPrerequisites(prerequisiteBC);

        List<Task> tasks = new ArrayList<>();
        tasks.add(taskA);
        tasks.add(taskB);
        tasks.add(taskC);
        tasks.add(taskD);

        Schedule schedule = new Schedule(tasks, 2);

        List<Task> equivalentTasks = new ArrayList<>();
        tasks.add(taskAA);
        tasks.add(taskBB);
        tasks.add(taskCC);
        tasks.add(taskDD);

        Schedule equivalentSchedule = new Schedule(equivalentTasks, 2);

        Assert.assertEquals(schedule, equivalentSchedule);

        // Schedule Task A
        taskA.setStartTime(0);
        taskA.setProcessorNumber(0);
        schedule.scheduleTask(taskA);
        // Schedule Task B
        taskB.setStartTime(6);
        taskB.setProcessorNumber(1);
        schedule.scheduleTask(taskB);
        // Schedule Task C
        taskC.setStartTime(5);
        taskC.setProcessorNumber(0);
        schedule.scheduleTask(taskC);
        // Schedule Task D
        taskD.setStartTime(9);
        taskD.setProcessorNumber(0);
        schedule.scheduleTask(taskD);

        // Schedule Task A
        taskAA.setStartTime(0);
        taskAA.setProcessorNumber(0);
        equivalentSchedule.scheduleTask(taskAA);
        // Schedule Task B
        taskBB.setStartTime(6);
        taskBB.setProcessorNumber(0);
        equivalentSchedule.scheduleTask(taskBB);
        // Schedule Task C
        taskCC.setStartTime(5);
        taskCC.setProcessorNumber(1);
        equivalentSchedule.scheduleTask(taskCC);
        // Schedule Task D
        taskDD.setStartTime(9);
        taskDD.setProcessorNumber(1);
        equivalentSchedule.scheduleTask(taskDD);

        Assert.assertEquals(schedule, equivalentSchedule);
    }

    @Test
    public void equivalentScheduleHashCodeTest(){
        Task taskA = new Task("a", 5);
        Task taskB = new Task("b", 2);
        Task taskC = new Task("c", 3);
        Task taskD = new Task("d", 1);

        Task taskAA = new Task("a", 5);
        Task taskBB = new Task("b", 2);
        Task taskCC = new Task("c", 3);
        Task taskDD = new Task("d", 1);

        List<Task> prerequisiteA = new ArrayList<>();
        prerequisiteA.add(taskA);
        taskB.setPrerequisites(prerequisiteA);
        taskC.setPrerequisites(prerequisiteA);

        List<Task> prerequisiteBC = new ArrayList<>();
        prerequisiteBC.add(taskB);
        prerequisiteBC.add(taskC);
        taskD.setPrerequisites(prerequisiteBC);

        List<Task> tasks = new ArrayList<>();
        tasks.add(taskA);
        tasks.add(taskB);
        tasks.add(taskC);
        tasks.add(taskD);

        Schedule schedule = new Schedule(tasks, 2);

        List<Task> equivalentTasks = new ArrayList<>();
        tasks.add(taskAA);
        tasks.add(taskBB);
        tasks.add(taskCC);
        tasks.add(taskDD);

        Schedule equivalentSchedule = new Schedule(equivalentTasks, 2);

        Assert.assertEquals(schedule, equivalentSchedule);

        // Schedule Task A
        taskA.setStartTime(0);
        taskA.setProcessorNumber(0);
        schedule.scheduleTask(taskA);
        // Schedule Task B
        taskB.setStartTime(6);
        taskB.setProcessorNumber(1);
        schedule.scheduleTask(taskB);
        // Schedule Task C
        taskC.setStartTime(5);
        taskC.setProcessorNumber(0);
        schedule.scheduleTask(taskC);
        // Schedule Task D
        taskD.setStartTime(9);
        taskD.setProcessorNumber(0);
        schedule.scheduleTask(taskD);

        // Schedule Task A
        taskAA.setStartTime(0);
        taskAA.setProcessorNumber(0);
        equivalentSchedule.scheduleTask(taskAA);
        // Schedule Task B
        taskBB.setStartTime(6);
        taskBB.setProcessorNumber(0);
        equivalentSchedule.scheduleTask(taskBB);
        // Schedule Task C
        taskCC.setStartTime(5);
        taskCC.setProcessorNumber(1);
        equivalentSchedule.scheduleTask(taskCC);
        // Schedule Task D
        taskDD.setStartTime(9);
        taskDD.setProcessorNumber(1);
        equivalentSchedule.scheduleTask(taskDD);

        Assert.assertEquals(schedule.hashCode(), equivalentSchedule.hashCode());
    }

}
