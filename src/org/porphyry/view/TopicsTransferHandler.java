/*
PORPHYRY - Digital space for building and confronting interpretations about
documents

SCIENTIFIC COMMITTEE
- Andrea Iacovella
- Aurelien Benel

OFFICIAL WEB SITE
http://www.porphyry.org/

Copyright (C) 2007 Aurelien Benel.

LEGAL ISSUES
This program is free software; you can redistribute it and/or modify it under
the terms of the GNU General Public License (version 2) as published by the
Free Software Foundation.
This program is distributed in the hope that it will be useful, but WITHOUT ANY
WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
PARTICULAR PURPOSE. See the GNU General Public License for more details:
http://www.gnu.org/licenses/gpl.html
*/

package org.porphyry.view;

import java.util.*;
import java.awt.datatransfer.*;
import java.io.IOException;
import javax.swing.*;

public class TopicsTransferHandler extends TransferHandler {//>>>>>>>>>>>>>>>>>

private static final TopicsTransferHandler singleton = 
	new TopicsTransferHandler();

public static final DataFlavor TOPICS_FLAVOR = new DataFlavor(
	Collection.class, "topics"
);

public static TopicsTransferHandler getSingleton() {
	return singleton;
}

protected static Collection<org.porphyry.presenter.Viewpoint.Topic> getSource(TransferSupport transfer) 
	throws UnsupportedFlavorException, IOException
{
	return (Collection<org.porphyry.presenter.Viewpoint.Topic>)
		transfer.getTransferable().getTransferData(TOPICS_FLAVOR);
}

protected static org.porphyry.presenter.Viewpoint.Topic getTarget(TransferSupport transfer) {
	return ((Viewpoint.ViewpointPane.Topic) transfer.getComponent()).presenter;
}

@Override
public int getSourceActions(JComponent source) {
	return COPY_OR_MOVE;
}

@Override
protected Transferable createTransferable(JComponent source) {
	Collection<org.porphyry.presenter.Viewpoint.Topic> c = null;
	if (source instanceof Viewpoint.ViewpointPane.Topic) {
		c = ((Viewpoint.ViewpointPane.Topic) source).getActiveTopics();	
	}	
	return new TopicSelection(c);
}

@Override
public boolean canImport(TransferSupport transfer) {
	try {			
		return transfer.isDataFlavorSupported(TOPICS_FLAVOR)
			&& !getSource(transfer).contains(getTarget(transfer)); //TODO must check that it does no cycle
	} catch (Exception e) {
		return false;
	}
}

@Override
public boolean importData(TransferSupport transfer) {
	boolean ok = true;
	try {
		if (this.canImport(transfer)) {
			Collection <org.porphyry.presenter.Viewpoint.Topic> source =
				getSource(transfer);
			org.porphyry.presenter.Viewpoint.Topic target =
				getTarget(transfer);
			target.linkTopics(source, "includes");
			if (transfer.getDropAction()==MOVE){
				for (org.porphyry.presenter.Viewpoint.Topic t: source){
					Collection<org.porphyry.presenter.Viewpoint.Topic> toDel =
						t.getTopics("includedIn");
					toDel.remove(target);
					t.unlinkTopics(toDel, "includedIn");
				}
			}
		}
	} catch (Exception e) {
		System.err.println(e);
		ok = false;
	}
	return ok;
}

public class TopicSelection implements Transferable {//>>>>>>>>>>>>>>>>>>>>>>>>

private Collection<org.porphyry.presenter.Viewpoint.Topic> data;

public TopicSelection(
	Collection<org.porphyry.presenter.Viewpoint.Topic> data
) {
	this.data = data;
}

public DataFlavor[] getTransferDataFlavors() {
	return new DataFlavor[] {TOPICS_FLAVOR};
}

public boolean isDataFlavorSupported(DataFlavor flavor) {
	return TOPICS_FLAVOR.equals(flavor);
}

public Object getTransferData(DataFlavor flavor) 
	throws UnsupportedFlavorException 
{
	if (TOPICS_FLAVOR.equals(flavor))
		return this.data;
	throw new UnsupportedFlavorException(flavor);
}

}//<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< class TopicSelection

}//<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< class TopicTransferHandler
