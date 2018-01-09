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

package me.snowdrop.data.core.config.jpa.crud;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;

import me.snowdrop.data.core.crud.CEntity;
import me.snowdrop.data.core.crud.CrudTestBase;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.orm.jpa.EntityManagerFactoryUtils;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
@SpringBootTest(classes = JpaCrudConfiguration.class)
@RunWith(SpringRunner.class)
public class JpaCrudTest extends CrudTestBase {

    @Autowired
    EntityManagerFactory emf;

    private EntityManager em;

    @Before
    public void setUp() {
        TransactionSynchronizationManager.initSynchronization();
        em = EntityManagerFactoryUtils.getTransactionalEntityManager(emf);
    }

    @After
    public void tearDown() {
        try {
            em.close();
        } finally {
            TransactionSynchronizationManager.clear();
        }
    }

    @Override
    protected Tx createTx() {
        final EntityTransaction transaction = em.getTransaction();
        transaction.begin();
        return transaction::commit;
    }

    @Override
    protected CEntity create() {
        CEntity entity = new CEntity();
        entity.setCtype("foobar");
        return entity; // no id yet
    }
}
