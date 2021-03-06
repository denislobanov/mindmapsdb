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

import org.apache.tinkerpop.gremlin.process.traversal.P;

class NeqPredicate extends ComparatorPredicate {

    /**
     * @param value the value that this predicate is testing against
     */
    NeqPredicate(Object value) {
        super(value);
    }

    @Override
    public P<Object> getPredicate() {
        return P.neq(value);
    }

    @Override
    protected String getSymbol() {
        return "!=";
    }
}
