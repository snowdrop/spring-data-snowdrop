package org.jboss.data.hibernatesearch;

import java.util.ArrayList;
import java.util.List;

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

  public static <T> List<T> toList(final Iterable<T> iter) {
    List<T> list = new ArrayList<>();
    iter.forEach(list::add);
    return list;
  }

  public static SearchIntegrator createSearchIntegrator(Class<?>... classes) {
    SearchConfigurationForTest configuration = SearchConfigurationForTest.noTestDefaults();
    configuration.addClasses(classes);
    SearchIntegratorBuilder builder = new SearchIntegratorBuilder();
    builder.configuration(configuration);
    return builder.buildSearchIntegrator();
  }

  public static DatasourceMapperForTest createDatasourceMapper() {
    return new DatasourceMapperForTest();
  }

  public static void preindexEntities(SearchIntegrator si, DatasourceMapperForTest datasourceMapper, AbstractEntity... entities) {
    println("Starting index creation...");
    Worker worker = si.getWorker();
    TransactionContextForTest tc = new TransactionContextForTest();
    boolean needsFlush = false;
    int i = 1;
    for (; i <= entities.length; i++) {
      AbstractEntity entity = entities[i - 1];
      Work work = new Work(entity, entity.getId(), WorkType.ADD, false);
      worker.performWork(work, tc);
      datasourceMapper.put(entity);
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

  public static void purgeAll(SearchIntegrator si, DatasourceMapperForTest datasourceMapper, Class<?> entityClass) {
    println("Purging index - " + entityClass.getSimpleName() + " ...");
    Worker worker = si.getWorker();
    TransactionContextForTest tc = new TransactionContextForTest();
    Work work = new Work(entityClass, null, WorkType.PURGE_ALL);
    worker.performWork(work, tc);
    tc.end();
    datasourceMapper.clear();
  }
}