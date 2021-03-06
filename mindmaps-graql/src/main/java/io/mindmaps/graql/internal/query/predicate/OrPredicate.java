/*
 * MindmapsDB - A Distributed Semantic Database
 * Copyright (C) 2016  Mindmaps Research Ltd
 *
 * MindmapsDB is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MindmapsDB is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MindmapsDB. If not, see <http://www.gnu.org/licenses/gpl.txt>.
 */

package io.mindmaps.graql.internal.query.predicate;

import com.google.common.collect.ImmutableSet;
import io.mindmaps.graql.admin.ValuePredicateAdmin;
import org.apache.tinkerpop.gremlin.process.traversal.P;

class OrPredicate extends AbstractValuePredicate {

    private final ValuePredicateAdmin predicate1;
    private final ValuePredicateAdmin predicate2;

    OrPredicate(ValuePredicateAdmin predicate1, ValuePredicateAdmin predicate2, ImmutableSet<Object> innerValues) {
        super(innerValues);
        this.predicate1 = predicate1;
        this.predicate2 = predicate2;
    }

    @Override
    public P<Object> getPredicate() {
        return predicate1.getPredicate().or(predicate2.getPredicate());
    }

    @Override
    public String toString() {
        return "(" + predicate1 + " or " + predicate2 + ")";
    }
}
