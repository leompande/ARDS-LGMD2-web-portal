package org.hisp.dhis.system.util;

/*
 * Copyright (c) 2004-2013, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.hisp.dhis.common.Weighted;

/**
 * @author Lars Helge Overland
 */
public class WeightedPaginatedList<T extends Weighted>
    extends ArrayList<T>
{
    private int pages = 0;
    private int totalWeight = 0;
    private int weightPageBreak = 0;
    private int startIndex = 0;

    public WeightedPaginatedList( Collection<T> collection, int pages )
    {
        super( collection );
        this.pages = pages;
        this.init();
    }
    
    private void init()
    {
        Iterator<T> iterator = super.iterator();
        
        while ( iterator.hasNext() )
        {
            T element = iterator.next();
            
            totalWeight += element != null ? element.getWeight() : 0;
        }
        
        weightPageBreak = (int) Math.ceil( (double) totalWeight / pages );
    }
    
    /**
     * Returns the next page in the list. Returns null if there are no more pages.
     */
    public List<T> nextPage()
    {
        int size = size();
        
        if ( startIndex >= size )
        {
            return null;
        }
        
        int currentWeight = 0;
        int currentIndex = startIndex;
        
        while ( currentWeight < weightPageBreak && currentIndex < size )
        {
            T element = get( currentIndex++ );
            
            currentWeight += element != null ? element.getWeight() : 0;
        }
        
        List<T> page = super.subList( startIndex, currentIndex );
        
        startIndex = currentIndex;
                
        return page;
    }
    
    /**
     * Returns a list of all pages.
     */
    public List<List<T>> getPages()
    {
        List<List<T>> pages = new ArrayList<List<T>>();
        
        List<T> page = new ArrayList<T>();
        
        while ( ( page = nextPage() ) != null )
        {
            pages.add( page );
        }
        
        return pages;
    }
}
