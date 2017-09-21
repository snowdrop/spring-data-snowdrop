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

package me.snowdrop.data.hibernatesearch.ops;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import me.snowdrop.data.hibernatesearch.TestsAction;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public abstract class OpsTestsActionBase implements TestsAction {
  public void setUp() {
    List<SimpleEntity> entities = new ArrayList<>();

    ContainedEntity frankDalton = new ContainedEntity( 1L, "Frank Dalton", 41);
    ContainedEntity emmettDalton = new ContainedEntity( 2L, "Emmett Dalton", 42);
    ContainedEntity oliverTwist = new ContainedEntity( 3L, "Oliver Twist", 43);

    SimpleEntity entity = new SimpleEntity(
            1L, "ann", "Does Ann like good red apples?", -20, true,
            "Superman", "red", new Location(10.0, 10.0),
            Arrays.asList(frankDalton, emmettDalton)
    );
    entities.add(entity);
    entity = new SimpleEntity(
            2L, "barb", "Why is Barb dancing twist?", -10, true,
            "Spiderman", "red", new Location(24.0, 32.0),
            Collections.emptyList()
    );
    entities.add(entity);
    entity = new SimpleEntity(
            3L, "carl", "Carl is good at running and jumping.", 0, true,
            "Flash", "red", new Location(20.0, 20.0),
            Collections.emptyList()
    );
    entities.add(entity);
    entity = new SimpleEntity(
            4L, "doug", "Doug likes to sleeps.", 10, false,
            "Batman", "black", new Location(-10.0, -10.0),
            Collections.emptyList()
    );
    entities.add(entity);
    entity = new SimpleEntity(
            5L, "eva", "Eva is running in circles.", 20, false,
            "Ironman", "gold", new Location(-20.0, 5.0),
            Arrays.asList(oliverTwist)
    );
    entities.add(entity);
    entity = new SimpleEntity(
            6L, "fanny", "Fanny is reading a good book.", 30, false,
            "Aquaman", "blue", new Location(5.0, -20.0),
            Collections.emptyList()
    );
    entities.add(entity);

    setUp(entities);
  }

  protected abstract void setUp(List<SimpleEntity> entities);
}
