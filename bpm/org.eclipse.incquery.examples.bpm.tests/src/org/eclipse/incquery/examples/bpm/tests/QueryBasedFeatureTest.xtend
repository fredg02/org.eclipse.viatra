package org.eclipse.incquery.examples.bpm.tests

import org.junit.runner.RunWith
import org.eclipse.xtext.junit4.XtextRunner
import org.eclipse.xtext.junit4.InjectWith
import org.eclipse.incquery.testing.core.injector.EMFPatternLanguageInjectorProvider
import com.google.inject.Inject
import org.eclipse.incquery.testing.core.TestExecutor
import org.eclipse.incquery.testing.core.ModelLoadHelper
import org.eclipse.incquery.testing.core.SnapshotHelper
import org.junit.Test
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl
import org.eclipse.incquery.runtime.api.IncQueryEngineManager
import org.eclipse.incquery.runtime.api.AdvancedIncQueryEngine
import static org.junit.Assert.*
import org.eclipse.incquery.runtime.api.IncQueryEngine
import system.Job
import system.Data
import process.Task
import org.eclipse.incquery.examples.bpm.queries.ProcessTasksMatcher
import operation.Checklist
import org.apache.log4j.Appender
import org.eclipse.incquery.examples.bpm.queries.JobTasksMatcher
import org.eclipse.incquery.querybasedfeatures.runtime.handler.QueryBasedFeatures
import operation.OperationPackage
import org.eclipse.incquery.querybasedfeatures.runtime.QueryBasedFeatureHelper
import org.eclipse.incquery.querybasedfeatures.runtime.QueryBasedFeatureKind

@RunWith(typeof(XtextRunner))
@InjectWith(typeof(EMFPatternLanguageInjectorProvider))
class QueryBasedFeatureTest {
 
  @Inject extension TestExecutor
  @Inject extension ModelLoadHelper
  @Inject extension SnapshotHelper

  @Test
  def simpleGetterTest(){
    
    val rs = new ResourceSetImpl
    val resource = loadAdditionalResourceFromUri(rs,"org.eclipse.incquery.examples.bpm.tests/model/Simple.system")
    
    val sys = resource.contents.get(0) as system.System
    sys.contains.forEach[
      assertTrue(it.tasks.empty)
    ]
    sys.data.forEach[
        assertTrue(it.readingTask.empty)
        assertTrue(it.writingTask.empty)
    ]

    val resource2 = loadAdditionalResourceFromUri(rs,"org.eclipse.incquery.examples.bpm.tests/model/Simple.process")
    sys.contains.forEach[
      val job = it as Job
      assertTrue(job.taskIds.size == job.tasks.size)
      // all elements resolved
      assertTrue(job.taskIds.filter[tid |
        job.tasks.filter[
          val task = it as Task
          task.id == tid
        ].empty
      ].empty)
      assertTrue(job.tasks.filter[
        val task = it as Task
        !job.taskIds.contains(task.id)
      ].empty)
    ]
    sys.data.forEach[
      val data = it as Data
      assertTrue(data.readingTaskIds.size == data.readingTask.size)
      // all elements resolved
      assertTrue(data.readingTask.filter[
        val task = it as Task
        !data.readingTaskIds.contains(task.id)
      ].empty)
      assertTrue(data.writingTaskIds.size == data.writingTask.size)
      // all elements resolved
      assertTrue(data.writingTask.filter[
        val task = it as Task
        !data.writingTaskIds.contains(task.id)
      ].empty)
    ]
        
  }
  
  @Test
  def simpleModifyTest(){
    val rs = new ResourceSetImpl
    val resource = loadAdditionalResourceFromUri(rs,"org.eclipse.incquery.examples.bpm.tests/model/Simple.system")
    loadAdditionalResourceFromUri(rs,"org.eclipse.incquery.examples.bpm.tests/model/Simple.process")
    
    val sys = resource.contents.get(0) as system.System
    sys.contains.forEach[
      assertTrue(it.tasks.size == it.taskIds.size)
    ]
    
    val job = sys.contains.get(0)
    
    val engine = AdvancedIncQueryEngine::on(rs)
    val matcher = ProcessTasksMatcher::on(engine)
    matcher.forEachMatch[
      if(!job.tasks.contains(task)){
        job.taskIds += task.id
      }
      assertTrue(job.tasks.contains(task))
    ]
    
    val data = sys.data.get(0)
    data.readingTaskIds.clear
    assertTrue(data.readingTask.empty)
  }
  
  @Test
  def singleFeatureTest(){
    val rs = new ResourceSetImpl
    val resource = loadAdditionalResourceFromUri(rs,"org.eclipse.incquery.examples.bpm.tests/model/Simple.operation")
    
    val checklist = resource.contents.get(0) as Checklist
    
    checklist.entries.forEach[
      assertNull(it.task)
    ]
    
    val resource2 = loadAdditionalResourceFromUri(rs,"org.eclipse.incquery.examples.bpm.tests/model/Simple.process")
    val engine = AdvancedIncQueryEngine::on(rs)
    val procMatcher = ProcessTasksMatcher::on(engine)
    
    checklist.entries.forEach[
      if(it.taskId != null){
        assertTrue(it.task.id == it.taskId)
      }
      
      val t = it.task
      t.id = "changedID"
      assertNull(it.task)
      
      it.taskId = "changedID"
      assertTrue(it.task  == t)
      
      it.taskId = null
      assertNull(it.task)
      
      val entry = it
      
      procMatcher.forEachMatch[
        entry.taskId = task.id
      ]
    ]
  }
  
  @Test
  def violateSingleFeatureTest(){
    
    val rs = new ResourceSetImpl
    val resource = loadAdditionalResourceFromUri(rs,"org.eclipse.incquery.examples.bpm.tests/model/Simple.operation")
    val resource2 = loadAdditionalResourceFromUri(rs,"org.eclipse.incquery.examples.bpm.tests/model/Simple.process")
    
    val checklist = resource.contents.get(0) as Checklist
    val proc = resource2.contents.get(0) as process.Process
    val entry  =  checklist.entries.get(0)
    val tid = entry.taskId
    proc.contents.forEach[
      it.id = tid
    ]
    
    val engine = IncQueryEngine::on(rs)
    engine.registerLogger
    
    val task = entry.task
    
    val logOut = engine.retrieveLoggerOutput
    assertTrue(logOut.contains("[IncqueryFeatureHandler] Space-time continuum breached (should never happen): update memory already set for given source"));
  }
  
  @Test
  def initDuringMatcherBuilding(){
    
    val rs = new ResourceSetImpl
    loadAdditionalResourceFromUri(rs,"org.eclipse.incquery.examples.bpm.tests/model/Simple.system")
    loadAdditionalResourceFromUri(rs,"org.eclipse.incquery.examples.bpm.tests/model/Simple.process")
    
    val engine = AdvancedIncQueryEngine::on(rs)
    engine.registerLogger
    
    val matcher = JobTasksMatcher::on(engine)
    
    assertTrue(matcher.countMatches > 0);
    matcher.forEachMatch[
      job.taskIds.clear
      assertTrue(job.tasks.empty)
    ]
  }
  
  @Test
  def uncachedSingleFeatureTest() {
    
    val rs = new ResourceSetImpl
    loadAdditionalResourceFromUri(rs,"org.eclipse.incquery.examples.bpm.tests/model/Simple.system")
    loadAdditionalResourceFromUri(rs,"org.eclipse.incquery.examples.bpm.tests/model/Simple.process")
    val resource = loadAdditionalResourceFromUri(rs,"org.eclipse.incquery.examples.bpm.tests/model/Simple.operation")
    
    val singleqbf = QueryBasedFeatureHelper::getQueryBasedFeatureHandler(rs,
       OperationPackage::eINSTANCE.checklistEntry_Task,
       "operation.queries.ChecklistEntryTaskCorrespondence",
       "CLE", "Task",
       QueryBasedFeatureKind::SINGLE_REFERENCE, false);
    
    
    val checklist = resource.contents.get(0) as Checklist
    
    checklist.entries.forEach[
      assertEquals(singleqbf.getValue(it),it.task)
    ]
  }
  
  @Test
  def uncachedManyFeatureTest() {
    val rs = new ResourceSetImpl
    loadAdditionalResourceFromUri(rs,"org.eclipse.incquery.examples.bpm.tests/model/Simple.system")
    loadAdditionalResourceFromUri(rs,"org.eclipse.incquery.examples.bpm.tests/model/Simple.process")
    val resource = loadAdditionalResourceFromUri(rs,"org.eclipse.incquery.examples.bpm.tests/model/Simple.operation")
    
    val manyqbf = QueryBasedFeatureHelper::getQueryBasedFeatureHandler(
          rs, OperationPackage::eINSTANCE.checklistEntry_Jobs,
          "operation.queries.ChecklistEntryJobCorrespondence", "CLE",
          "Job", QueryBasedFeatureKind::MANY_REFERENCE, false);
    
    val checklist = resource.contents.get(0) as Checklist
    
    checklist.entries.forEach[
      assertEquals(manyqbf.getValue(it),it.jobs)
    ]
  }
}
