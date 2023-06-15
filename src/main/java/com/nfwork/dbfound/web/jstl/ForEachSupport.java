package com.nfwork.dbfound.web.jstl;

import jakarta.el.ValueExpression;
import jakarta.servlet.jsp.JspTagException;
import jakarta.servlet.jsp.jstl.core.LoopTagSupport;

import java.util.*;

public abstract class ForEachSupport extends LoopTagSupport {
    protected ForEachSupport.ForEachIterator items;
    protected Object rawItems;

    public ForEachSupport() {
    }

    protected boolean hasNext() throws JspTagException {
        return this.items.hasNext();
    }

    protected Object next() throws JspTagException {
        return this.items.next();
    }

    protected void prepare() throws JspTagException {
        if (this.rawItems != null) {
            if (this.rawItems instanceof ValueExpression) {
                this.deferredExpression = (ValueExpression)this.rawItems;
                this.rawItems = this.deferredExpression.getValue(this.pageContext.getELContext());
            }

            this.items = this.supportedTypeForEachIterator(this.rawItems);
        } else {
            this.items = this.beginEndForEachIterator();
        }

    }

    public void release() {
        super.release();
        this.items = null;
        this.rawItems = null;
        this.deferredExpression = null;
    }

    protected ForEachSupport.ForEachIterator supportedTypeForEachIterator(Object o) throws JspTagException {
        ForEachSupport.ForEachIterator items;
        if (o instanceof Object[]) {
            items = this.toForEachIterator((Object[])((Object[])o));
        } else if (o instanceof boolean[]) {
            items = this.toForEachIterator((boolean[])((boolean[])o));
        } else if (o instanceof byte[]) {
            items = this.toForEachIterator((byte[])((byte[])o));
        } else if (o instanceof char[]) {
            items = this.toForEachIterator((char[])((char[])o));
        } else if (o instanceof short[]) {
            items = this.toForEachIterator((short[])((short[])o));
        } else if (o instanceof int[]) {
            items = this.toForEachIterator((int[])((int[])o));
        } else if (o instanceof long[]) {
            items = this.toForEachIterator((long[])((long[])o));
        } else if (o instanceof float[]) {
            items = this.toForEachIterator((float[])((float[])o));
        } else if (o instanceof double[]) {
            items = this.toForEachIterator((double[])((double[])o));
        } else if (o instanceof Collection) {
            items = this.toForEachIterator((Collection)o);
        } else if (o instanceof Iterator) {
            items = this.toForEachIterator((Iterator)o);
        } else if (o instanceof Enumeration) {
            items = this.toForEachIterator((Enumeration)o);
        } else if (o instanceof Map) {
            items = this.toForEachIterator((Map)o);
        } else if (o instanceof String) {
            items = this.toForEachIterator((String)o);
        } else {
            items = this.toForEachIterator(o);
        }

        return items;
    }

    private ForEachSupport.ForEachIterator beginEndForEachIterator() {
        Integer[] ia = new Integer[this.end + 1];

        for(int i = 0; i <= this.end; ++i) {
            ia[i] = new Integer(i);
        }

        return new ForEachSupport.SimpleForEachIterator(Arrays.asList(ia).iterator());
    }

    protected ForEachSupport.ForEachIterator toForEachIterator(Object o) throws JspTagException {
        throw new JspTagException("FOREACH_BAD_ITEMS");
    }

    protected ForEachSupport.ForEachIterator toForEachIterator(Object[] a) {
        return new ForEachSupport.SimpleForEachIterator(Arrays.asList(a).iterator());
    }

    protected ForEachSupport.ForEachIterator toForEachIterator(boolean[] a) {
        Boolean[] wrapped = new Boolean[a.length];

        for(int i = 0; i < a.length; ++i) {
            wrapped[i] = new Boolean(a[i]);
        }

        return new ForEachSupport.SimpleForEachIterator(Arrays.asList(wrapped).iterator());
    }

    protected ForEachSupport.ForEachIterator toForEachIterator(byte[] a) {
        Byte[] wrapped = new Byte[a.length];

        for(int i = 0; i < a.length; ++i) {
            wrapped[i] = new Byte(a[i]);
        }

        return new ForEachSupport.SimpleForEachIterator(Arrays.asList(wrapped).iterator());
    }

    protected ForEachSupport.ForEachIterator toForEachIterator(char[] a) {
        Character[] wrapped = new Character[a.length];

        for(int i = 0; i < a.length; ++i) {
            wrapped[i] = new Character(a[i]);
        }

        return new ForEachSupport.SimpleForEachIterator(Arrays.asList(wrapped).iterator());
    }

    protected ForEachSupport.ForEachIterator toForEachIterator(short[] a) {
        Short[] wrapped = new Short[a.length];

        for(int i = 0; i < a.length; ++i) {
            wrapped[i] = new Short(a[i]);
        }

        return new ForEachSupport.SimpleForEachIterator(Arrays.asList(wrapped).iterator());
    }

    protected ForEachSupport.ForEachIterator toForEachIterator(int[] a) {
        Integer[] wrapped = new Integer[a.length];

        for(int i = 0; i < a.length; ++i) {
            wrapped[i] = new Integer(a[i]);
        }

        return new ForEachSupport.SimpleForEachIterator(Arrays.asList(wrapped).iterator());
    }

    protected ForEachSupport.ForEachIterator toForEachIterator(long[] a) {
        Long[] wrapped = new Long[a.length];

        for(int i = 0; i < a.length; ++i) {
            wrapped[i] = new Long(a[i]);
        }

        return new ForEachSupport.SimpleForEachIterator(Arrays.asList(wrapped).iterator());
    }

    protected ForEachSupport.ForEachIterator toForEachIterator(float[] a) {
        Float[] wrapped = new Float[a.length];

        for(int i = 0; i < a.length; ++i) {
            wrapped[i] = new Float(a[i]);
        }

        return new ForEachSupport.SimpleForEachIterator(Arrays.asList(wrapped).iterator());
    }

    protected ForEachSupport.ForEachIterator toForEachIterator(double[] a) {
        Double[] wrapped = new Double[a.length];

        for(int i = 0; i < a.length; ++i) {
            wrapped[i] = new Double(a[i]);
        }

        return new ForEachSupport.SimpleForEachIterator(Arrays.asList(wrapped).iterator());
    }

    protected ForEachSupport.ForEachIterator toForEachIterator(Collection c) {
        return new ForEachSupport.SimpleForEachIterator(c.iterator());
    }

    protected ForEachSupport.ForEachIterator toForEachIterator(Iterator i) {
        return new ForEachSupport.SimpleForEachIterator(i);
    }

    protected ForEachSupport.ForEachIterator toForEachIterator(Enumeration e) {
        class EnumerationAdapter implements ForEachSupport.ForEachIterator {
            private Enumeration e;

            public EnumerationAdapter(Enumeration e) {
                this.e = e;
            }

            public boolean hasNext() {
                return this.e.hasMoreElements();
            }

            public Object next() {
                return this.e.nextElement();
            }
        }

        return new EnumerationAdapter(e);
    }

    protected ForEachSupport.ForEachIterator toForEachIterator(Map m) {
        return new ForEachSupport.SimpleForEachIterator(m.entrySet().iterator());
    }

    protected ForEachSupport.ForEachIterator toForEachIterator(String s) {
        StringTokenizer st = new StringTokenizer(s, ",");
        return this.toForEachIterator((Enumeration)st);
    }

    protected class SimpleForEachIterator implements ForEachSupport.ForEachIterator {
        private Iterator i;

        public SimpleForEachIterator(Iterator i) {
            this.i = i;
        }

        public boolean hasNext() {
            return this.i.hasNext();
        }

        public Object next() {
            return this.i.next();
        }
    }

    protected interface ForEachIterator {
        boolean hasNext() throws JspTagException;

        Object next() throws JspTagException;
    }
}
