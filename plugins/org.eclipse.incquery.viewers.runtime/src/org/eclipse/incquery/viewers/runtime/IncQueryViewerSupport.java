/*******************************************************************************
 * Copyright (c) 2010-2013, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/
package org.eclipse.incquery.viewers.runtime;

import org.eclipse.incquery.viewers.runtime.model.FilteredViewerDataModel;
import org.eclipse.incquery.viewers.runtime.model.ViewerDataFilter;
import org.eclipse.incquery.viewers.runtime.model.ViewerDataModel;
import org.eclipse.incquery.viewers.runtime.sources.ListContentProvider;
import org.eclipse.incquery.viewers.runtime.sources.QueryLabelProvider;
import org.eclipse.incquery.viewers.runtime.sources.TreeContentProvider;
import org.eclipse.jface.viewers.AbstractListViewer;
import org.eclipse.jface.viewers.AbstractTreeViewer;

/**
 * @author Zoltan Ujhelyi
 *
 */
public class IncQueryViewerSupport {

    public static void bind(AbstractListViewer viewer, ViewerDataModel model) {
        if (!(viewer.getContentProvider() instanceof ListContentProvider)) { 
            viewer.setContentProvider(new ListContentProvider(false));
        }
        if (!(viewer.getLabelProvider() instanceof QueryLabelProvider)) {
            viewer.setLabelProvider(new QueryLabelProvider()); 
        }
        viewer.setInput(model);
        viewer.refresh();
    }

    public static void bind(AbstractListViewer viewer, ViewerDataModel model, ViewerDataFilter filter) {
        if (!(viewer.getContentProvider() instanceof ListContentProvider)) {
            viewer.setContentProvider(new ListContentProvider(false));
        }
        if (!(viewer.getLabelProvider() instanceof QueryLabelProvider)) {
            viewer.setLabelProvider(new QueryLabelProvider()); 
        }
        viewer.setInput(new FilteredViewerDataModel(model, filter));
        viewer.refresh();
    }

    public static void bind(AbstractTreeViewer viewer, ViewerDataModel model) {
        viewer.setContentProvider(new TreeContentProvider());
        if (!(viewer.getLabelProvider() instanceof QueryLabelProvider)) {
            viewer.setLabelProvider(new QueryLabelProvider()); 
        }
        viewer.setInput(model);
        viewer.refresh();
    }

    public static void bind(AbstractTreeViewer viewer, ViewerDataModel model, ViewerDataFilter filter) {
        if (!(viewer.getContentProvider() instanceof TreeContentProvider)) {
            viewer.setContentProvider(new TreeContentProvider());
        }
        if (!(viewer.getLabelProvider() instanceof QueryLabelProvider)) {
            viewer.setLabelProvider(new QueryLabelProvider()); 
        }
        viewer.setInput(new FilteredViewerDataModel(model, filter));
        viewer.refresh();
    }
}
