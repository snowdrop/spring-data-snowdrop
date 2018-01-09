/*
 * Copyright 2018 Red Hat, Inc, and individual contributors.
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

package me.snowdrop.data.core.crud;

import java.util.Optional;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public abstract class CrudTestBase {

    @Autowired
    SimpleCrudRepository repository;

    protected abstract CEntity create();

    protected Tx createTx() throws Exception {
        return new DummyTx();
    }

    protected <T> T execute(Action<T> action) throws Exception {
        Tx tx = createTx();
        T result = action.run();
        tx.commit();
        return result;
    }

    @Test
    public void testSmoke() throws Exception {
        CEntity entity = create();
        final CEntity inner1 = entity;
        entity = execute(() -> repository.save(inner1));

        Optional<CEntity> cOptional = repository.findById(entity.getId());
        Assert.assertTrue(cOptional.isPresent());
        Assert.assertTrue(repository.existsById(entity.getId()));

        CEntity byCtype = repository.findByCtype(entity.getCtype());
        Assert.assertEquals(entity, byCtype);

        final CEntity inner2 = entity;
        execute(() -> {repository.delete(inner2); return null;});
        Assert.assertFalse(repository.existsById(entity.getId()));

        entity = create();
        final CEntity inner3 = entity;
        entity = execute(() -> repository.save(inner3));
        execute(() -> {repository.deleteAll(); return null;});
        Assert.assertFalse(repository.existsById(entity.getId()));
    }

    protected interface Action<T> {
        T run();
    }

    protected interface Tx {
        void commit();
    }

    protected static class DummyTx implements Tx {
        public void commit() {
        }
    }
}
