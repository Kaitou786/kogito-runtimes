package org.drools.runtime.rule.impl;

import java.util.Iterator;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import org.drools.rule.Declaration;
import org.drools.runtime.rule.QueryResults;
import org.drools.runtime.rule.QueryResultsRow;

@XmlAccessorType( XmlAccessType.FIELD )
public class NativeQueryResults
    implements
    QueryResults {
    
    private org.drools.QueryResults results;
    
    public NativeQueryResults() {
	}

    public NativeQueryResults(org.drools.QueryResults results) {
        this.results = results;
    }

	public org.drools.QueryResults getResults() {
		return results;
	}

	public String[] getIdentifiers() {
        return getDeclarations().keySet().toArray( new String[this.getResults().getDeclarations().size()] );
    }
    
    
    public Map<String, Declaration> getDeclarations() {
        return this.getResults().getDeclarations();
    }

    public int size() {
        return this.getResults().size();
    }
    
    public Iterator<QueryResultsRow> iterator() {
        return new QueryResultsIterator( this.getResults().iterator() );
    }

    private class QueryResultsIterator
        implements
        Iterator<QueryResultsRow> {
        private Iterator<org.drools.QueryResult> iterator;

        public QueryResultsIterator(final Iterator<org.drools.QueryResult> iterator) {
            this.iterator = iterator;
        }

        public boolean hasNext() {
            return this.iterator.hasNext();
        }

        public QueryResultsRow next() {
            return new NativeQueryResultRow(this.iterator.next());
        }

        public void remove() {
            this.iterator.remove();
        }

    }

}
