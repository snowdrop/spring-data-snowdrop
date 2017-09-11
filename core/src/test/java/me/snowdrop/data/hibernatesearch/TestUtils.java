/*
 * Copyright 2017 Red Hat, Inc, and individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.snowdrop.data.hibernatesearch;

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

  public static <T> int size(Iterable<T> iter) {
    return toList(iter).size();
  }

  public static <T> List<T> toList(final Iterable<T> iter) {
    if (iter instanceof List) {
      return (List<T>) iter;
    } else {
      List<T> list = new ArrayList<>();
      iter.forEach(list::add);
      return list;
    }
  }

  public static SearchIntegrator createSearchIntegrator(Class<?>... classes) {
    SearchConfigurationForTest configuration = SearchConfigurationForTest.noTestDefaults();
    configuration.addClasses(classes);
    SearchIntegratorBuilder builder = new SearchIntegratorBuilder();
    builder.configuration(configuration);
    return builder.buildSearchIntegrator();
  }

  public static DatasourceMapperForTest createDatasourceMapper(Class<?> entityClass) {
    SearchIntegrator searchIntegrator = createSearchIntegrator(entityClass);
    return new DatasourceMapperForTest(searchIntegrator);
  }

  public static void preindexEntities(DatasourceMapperForTest datasourceMapper, AbstractEntity... entities) {
    println("Starting index creation...");
    SearchIntegrator searchIntegrator = datasourceMapper.getSearchIntegrator();
    Worker worker = searchIntegrator.getWorker();
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

  public static void purgeAll(DatasourceMapperForTest datasourceMapper, Class<?> entityClass) {
    println("Purging index - " + entityClass.getSimpleName() + " ...");
    SearchIntegrator searchIntegrator = datasourceMapper.getSearchIntegrator();
    Worker worker = searchIntegrator.getWorker();
    TransactionContextForTest tc = new TransactionContextForTest();
    Work work = new Work(entityClass, null, WorkType.PURGE_ALL);
    worker.performWork(work, tc);
    tc.end();
    datasourceMapper.clear();
  }
}