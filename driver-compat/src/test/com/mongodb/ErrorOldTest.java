/*
 * Copyright (c) 2008 - 2013 10gen, Inc. <http://10gen.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mongodb;

import category.ReplicaSet;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.assertTrue;

public class ErrorOldTest extends DatabaseTestCase {

    @Test
    public void testLastError() {
        database.resetError();
        assertTrue(database.getLastError().get("err") == null);
        database.forceError();
        assertTrue(database.getLastError().get("err") != null);
        database.resetError();
        assertTrue(database.getLastError().get("err") == null);
    }

    @Test
    public void testLastErrorWithConcern() {
        database.resetError();
        final CommandResult cr = database.getLastError(WriteConcern.FSYNC_SAFE);
        assertTrue(cr.get("err") == null);
        assertTrue(cr.containsField("fsyncFiles") || cr.containsField("waited"));
    }

    @Test
    @Category(ReplicaSet.class)
    public void testLastErrorWithConcernAndW() {
        database.resetError();
        final CommandResult cr = database.getLastError(WriteConcern.REPLICAS_SAFE);
        assertTrue(cr.get("err") == null);
        assertTrue(cr.containsField("wtime"));
    }

    @Test
    public void testPrevError() {
        database.resetError();

        assertTrue(database.getLastError().get("err") == null);
        assertTrue(database.getPreviousError().get("err") == null);

        database.forceError();

        assertTrue(database.getLastError().get("err") != null);
        assertTrue(database.getPreviousError().get("err") != null);

        database.getCollection("misc").insert(new BasicDBObject("foo", 1));

        assertTrue(database.getLastError().get("err") == null);
        assertTrue(database.getPreviousError().get("err") != null);

        database.resetError();

        assertTrue(database.getLastError().get("err") == null);
        assertTrue(database.getPreviousError().get("err") == null);
    }
}
