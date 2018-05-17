/*******************************************************************************
 * Copyright (c) 2004-2008 Gabor Bergmann and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Gabor Bergmann - initial API and implementation
 *******************************************************************************/

package org.eclipse.viatra.query.runtime.matchers.memories;

import java.util.Collection;
import java.util.Iterator;

import org.eclipse.viatra.query.runtime.matchers.tuple.ITuple;
import org.eclipse.viatra.query.runtime.matchers.tuple.Tuple;
import org.eclipse.viatra.query.runtime.matchers.tuple.TupleMask;
import org.eclipse.viatra.query.runtime.matchers.util.CollectionsFactory;
import org.eclipse.viatra.query.runtime.matchers.util.CollectionsFactory.MemoryType;
import org.eclipse.viatra.query.runtime.matchers.util.IMemoryView;
import org.eclipse.viatra.query.runtime.matchers.util.IMultiLookup;
import org.eclipse.viatra.query.runtime.matchers.util.IMultiLookup.ChangeGranularity;

/**
 * @author Gabor Bergmann
 * 
 * Default implementation that covers all cases.
 * 
 * @since 2.0
 */
public final class DefaultMaskedTupleMemory extends MaskedTupleMemory {
    /**
     * Maps a signature tuple to the bucket of tuples with the given signature.
     * @since 2.0
     */
    protected IMultiLookup<Tuple, Tuple> signatureToTuples;

    /**
     * @param mask
     *            The mask used to index the matchings
     * @param owner the object "owning" this memory
     * @param bucketType the kind of tuple collection maintained for each indexer bucket
     * @since 2.0
     */
    DefaultMaskedTupleMemory(TupleMask mask, MemoryType bucketType, Object owner) {
        super(mask, owner);
        signatureToTuples = CollectionsFactory.<Tuple, Tuple>createMultiLookup(
                Object.class, bucketType, Object.class);
    }

    @Override
    public boolean add(Tuple tuple) {
        Tuple signature = mask.transform(tuple);
        return add(tuple, signature);
    }

    @Override
    public boolean add(Tuple tuple, Tuple signature) {
        try {
            return signatureToTuples.addPair(signature, tuple) == ChangeGranularity.KEY;
        } catch (IllegalStateException ex) { // ignore worthless internal exception details
            throw raiseDuplicateInsertion(tuple);
        }
    
    }

    @Override
    public boolean remove(Tuple tuple) {
        Tuple signature = mask.transform(tuple);
        return remove(tuple, signature);
    }

    @Override
    public boolean remove(Tuple tuple, Tuple signature) {
        try {
            return signatureToTuples.removePair(signature, tuple) == ChangeGranularity.KEY;
        } catch (IllegalStateException ex) { // ignore worthless internal exception details
            throw raiseDuplicateDeletion(tuple);
        }
    }

    @Override
    public Collection<Tuple> get(ITuple signature) {
        IMemoryView<Tuple> bucket = signatureToTuples.lookupUnsafe(signature);
        return bucket == null ? null : bucket.distinctValues();
    }

    @Override
    public void clear() {
        signatureToTuples.clear();
    }

    @Override
    public Iterable<Tuple> getSignatures() {
        return signatureToTuples.distinctKeys();
    }

    @Override
    public Iterator<Tuple> iterator() {
        return signatureToTuples.distinctValues().iterator();
    }

    @Override
    public int getTotalSize() {
        int i = 0;
        for (Tuple key : signatureToTuples.distinctKeys()) {
            i += signatureToTuples.lookup(key).size();
        }
        return i;
    }
    
    @Override
    public int getKeysetSize() {
        return signatureToTuples.countKeys();
    }

    
}