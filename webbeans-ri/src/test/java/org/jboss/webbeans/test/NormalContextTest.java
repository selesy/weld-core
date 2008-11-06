package org.jboss.webbeans.test;

import static org.jboss.webbeans.test.util.Util.createProducerModel;
import static org.jboss.webbeans.test.util.Util.createSimpleModel;

import java.lang.reflect.Method;

import javax.webbeans.ContextNotActiveException;
import javax.webbeans.manager.Bean;
import javax.webbeans.manager.Context;

import org.jboss.webbeans.contexts.AbstractContext;
import org.jboss.webbeans.contexts.RequestContext;
import org.jboss.webbeans.model.bean.ProducerMethodBeanModel;
import org.jboss.webbeans.model.bean.SimpleBeanModel;
import org.jboss.webbeans.test.beans.SpiderProducer;
import org.jboss.webbeans.test.beans.Tarantula;
import org.jboss.webbeans.test.beans.Tuna;
import org.jboss.webbeans.test.util.Util;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * 
 * @author Nicklas Karlsson (nickarls@gmail.com)
 * @author Pete Muir
 * 
 * This class tests a basic context against section 8 of the specification
 *
 */
@SpecVersion("PDR")
public class NormalContextTest extends AbstractTest
{
   Context context;
   
   @BeforeMethod
   public void initContext() {
      context = new RequestContext();
   }
   
   @Test(groups="contexts") @SpecAssertion(section="8.1")
   public void testGetWithCreateFalseReturnsNull() {
      Bean<Tuna> tunaBean = Util.createSimpleWebBean(Tuna.class, manager);      
      assert context.get(tunaBean, false) == null;
   }

   @Test(groups="contexts") @SpecAssertion(section="8.1")
   public void testGetWithCreateTrueReturnsBean() {
      Bean<Tuna> tunaBean = Util.createSimpleWebBean(Tuna.class, manager);      
      assert context.get(tunaBean, true) != null;
   }
   
   @Test(groups="contexts", expectedExceptions=ContextNotActiveException.class) @SpecAssertion(section="8.1")
   public void testInactiveContextThrowsContextNotActiveException() {
      ((AbstractContext)context).setActive(false);
      context.get(null, false);
      assert true;
   }
   
   @Test(groups="contexts") @SpecAssertion(section="8.1")
   public void testReturnsCorrectExistingBean() {
      Bean<Tuna> tunaBean = Util.createSimpleWebBean(Tuna.class, manager);      
      Tuna firstTuna = context.get(tunaBean, true);
      Tuna secondTuna = context.get(tunaBean, false);
      assert firstTuna == secondTuna;
   }

   @Test(groups={"contexts", "producerMethod"}) @SpecAssertion(section="8.1")
   public void testProducerMethodReturningNullOK() throws SecurityException, NoSuchMethodException {
      SimpleBeanModel<SpiderProducer> producer = createSimpleModel(SpiderProducer.class, manager);
      manager.getModelManager().addBeanModel(producer);
      Method nullProducer = SpiderProducer.class.getMethod("produceShelob");  
      ProducerMethodBeanModel<Tarantula> producerModel = createProducerModel(Tarantula.class, nullProducer, manager);
      //Bean<Tarantula> shelobBean = new ProducerBeanImpl<Tarantula>(producerModel, manager);
      //assert context.get(shelobBean, true) == null;
   }

   @Test(groups={"contexts", "beanDestruction"})
   public void testDestroy() {
      Bean<Tuna> tunaBean = Util.createSimpleWebBean(Tuna.class, manager);      
      assert context.get(tunaBean, true) instanceof Tuna;
      ((AbstractContext)context).destroy(manager);
      assert context.get(tunaBean, false) == null;
   }
   
}
