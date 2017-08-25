package org.jboss.data.hibernatesearch;

import org.hibernate.search.backend.spi.Work;
import org.hibernate.search.backend.spi.WorkType;
import org.hibernate.search.backend.spi.Worker;
import org.hibernate.search.spi.SearchIntegrator;
import org.hibernate.search.spi.SearchIntegratorBuilder;

/**
 * @author Ales Justin
 */
public class TestUtils {

  private static void println(String line) {
    System.out.println(line);
  }

  public static SearchIntegrator createSearchIntegrator(Class<?>... classes) {
    SearchConfigurationForTest configuration = SearchConfigurationForTest.noTestDefaults();
    configuration.addClasses(classes);
    SearchIntegratorBuilder builder = new SearchIntegratorBuilder();
    builder.configuration(configuration);
    return builder.buildSearchIntegrator();
  }

  public static void preindexEntities(SearchIntegrator si, AbstractEntity... entities) {
    println("Starting index creation...");
    Worker worker = si.getWorker();
    TransactionContextForTest tc = new TransactionContextForTest();
    boolean needsFlush = false;
    int i = 1;
    for (; i <= entities.length; i++) {
      AbstractEntity entity = entities[i - 1];
      Work work = new Work(entity, entity.getId(), WorkType.ADD, false);
      worker.performWork(work, tc);
      needsFlush = true;
      if (i % 1000 == 0) {
        //commit in batches of 1000:
        tc.end();
        needsFlush = false;
        tc = new TransactionContextForTest();
      }
    }
    if (needsFlush) {
      //commit remaining work
      tc.end();
    }
    println(" ... created an index of " + (i - 1) + " entities.");
  }
}