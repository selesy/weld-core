/*
 * JBoss, Home of Professional Open Source
 * Copyright 2018, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.weld.bootstrap.enablement;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ListIterator;

import javax.enterprise.inject.spi.Extension;

import org.junit.Test;

/**
 *
 * @author <a href="mailto:manovotn@redhat.com">Matej Novotny</a>
 */
public class EnablementListViewTest {

    @Test
    public void testBasicOperations() {
        final List<Item> list = new ArrayList<>();
        EnablementListView view = new EnablementListView() {
            @Override
            protected ViewType getViewType() {
                return null;
            }

            @Override
            protected Extension getExtension() {
                return null;
            }

            @Override
            protected List<Item> getDelegate() {
                return list;
            }
        };

        list.add(new Item(Integer.class, 20));
        list.add(new Item(String.class, 1));
        list.add(new Item(Double.class, 300));

        // test contains(), index() and lastIndexOf(), note that list is *not* sorted
        assertTrue(view.contains(Integer.class));
        assertEquals(2, view.indexOf(Double.class));
        assertEquals(0, view.lastIndexOf(Integer.class));

        assertEquals(3, view.size());
        assertEquals(Integer.class, view.get(0));
        list.add(new Item(BigInteger.class, 301));
        assertEquals(4, view.size());

        view.add(BigDecimal.class);
        assertEquals(5, view.size());
        assertEquals(BigDecimal.class, view.get(view.size() - 1));

        // remove via List.remove(Object)
        view.remove(Double.class);
        assertEquals(4, list.size());
    }

    @Test
    public void testListIterator() {

        final List<Item> list = new ArrayList<>();
        EnablementListView view = new EnablementListView() {
            @Override
            protected ViewType getViewType() {
                return null;
            }

            @Override
            protected Extension getExtension() {
                return null;
            }

            @Override
            protected List<Item> getDelegate() {
                return list;
            }
        };
        list.add(new Item(Integer.class, 10));
        list.add(new Item(String.class, 20));
        list.add(new Item(Double.class, 30));

        ListIterator<Class<?>> iterator = view.listIterator();
        // -> Integer, String, Double
        assertEquals(Integer.class, iterator.next());
        // Integer, -> String, Double
        iterator.remove();
        // -> String, Double
        iterator.add(Float.class);
        assertEquals(Float.class, iterator.previous());
        // -> Float, String, Double
        assertFalse(iterator.hasPrevious());
        assertTrue(iterator.hasNext());
        assertEquals(Float.class, iterator.next());
        assertEquals(String.class, iterator.next());
        // Float, -> String, Double
        iterator.set(StringBuilder.class);
        assertEquals(3, list.size());
        assertEquals(StringBuilder.class, view.get(1));
    }

    @Test
    public void testMassListOperations() {
        final List<Item> list = new ArrayList<>();
        EnablementListView view = new EnablementListView() {
            @Override
            protected EnablementListView.ViewType getViewType() {
                return null;
            }

            @Override
            protected Extension getExtension() {
                return null;
            }

            @Override
            protected List<Item> getDelegate() {
                return list;
            }
        };
        list.add(new Item(Integer.class, 10));
        list.add(new Item(String.class, 20));
        list.add(new Item(Double.class, 30));
        list.add(new Item(Float.class, 50));

        // note that these operations indirectly test List.contains()
        // try removeAll()
        Collection<Object> testList = new ArrayList<Object>();
        testList.add(Integer.class);
        testList.add(Float.class);

        view.removeAll(testList);

        assertEquals(2, list.size());
        assertEquals(String.class, list.get(0).getJavaClass());
        assertEquals(Double.class, list.get(1).getJavaClass());

        // re-add missing items
        list.add(new Item(Integer.class, 10));
        list.add(new Item(Float.class, 50));

        // try retainAll()
        view.retainAll(testList);
        assertEquals(2, list.size());
        assertEquals(Integer.class, list.get(0).getJavaClass());
        assertEquals(Float.class, list.get(1).getJavaClass());

        // re-add missing items
        list.add(new Item(String.class, 20));
        list.add(new Item(Double.class, 30));

        // try containsAll()
        assertTrue(view.containsAll(testList));
    }
}
