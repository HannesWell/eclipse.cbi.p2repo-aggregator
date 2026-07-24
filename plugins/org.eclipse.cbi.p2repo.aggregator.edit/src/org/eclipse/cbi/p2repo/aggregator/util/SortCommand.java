/**
 * Copyright (c) 2006-2009, Cloudsmith Inc.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package org.eclipse.cbi.p2repo.aggregator.util;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.eclipse.emf.common.command.AbstractCommand;
import org.eclipse.emf.common.util.ECollections;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emf.edit.provider.IItemLabelProvider;

/**
 * @author Karel Brezina
 *
 */
public class SortCommand<T> extends AbstractCommand {

	class LabelComparator implements Comparator<T> {

		private final IItemLabelProvider labelProvider;

		public LabelComparator(IItemLabelProvider labelProvider) {
			this.labelProvider = labelProvider;
		}

		@Override
		public int compare(T o1, T o2) {
			if (o1 == null) {
				if (o2 == null)
					return 0;
				return -1;
			} else if (o2 == null)
				return 1;
			else {
				return labelProvider.getText(o1).compareTo(labelProvider.getText(o2));
			}
		}
	};

	private EList<T> containment;

	private List<T> originalList;

	private List<T> sortedList;

	private T itemTemplate;

	private IItemLabelProvider labelProvider;

	public SortCommand(EditingDomain editingDomain, EList<T> containment, T itemTemplate, String label) {
		super("Sort " + label);

		this.containment = containment;
		this.itemTemplate = itemTemplate;

		labelProvider = (IItemLabelProvider) ((AdapterFactoryEditingDomain) editingDomain).getAdapterFactory()
				.adapt(itemTemplate, IItemLabelProvider.class);

		if (labelProvider == null)
			throw new IllegalArgumentException(itemTemplate.getClass() + " does not provide label");
	}

	@Override
	public void execute() {
		originalList = new ArrayList<T>(containment);
		ECollections.setEList(containment, sortedList);
	}

	public Object getImage() {
		return labelProvider.getImage(itemTemplate);
	}

	@Override
	protected boolean prepare() {
		sortedList = new ArrayList<>(containment);
		sortedList.sort(new LabelComparator(labelProvider));
		return !sortedList.equals(containment);
	}

	@Override
	public void redo() {
		ECollections.setEList(containment, sortedList);
	}

	@Override
	public void undo() {
		ECollections.setEList(containment, originalList);
	}
}
