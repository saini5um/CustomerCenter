/*
 * IntegerCustomEnumeratedFilterAttribute.java
 *
 * Created on June 14, 2005, 12:36 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package com.maxis.util;

import com.csgsystems.domain.framework.attribute.*;
import com.csgsystems.domain.framework.businessobject.Domain;

import java.util.Map;

/**
 *
 * @author Pankaj Saini
 */
public class IntegerCustomEnumeratedFilterAttribute extends IntegerCustomEnumeratedAttribute {
    
    protected CriteriaAttributeImpl criteria = null;

    public boolean isFilterCriteria() {
        return true;
    }
    
    /** Creates a new instance of IntegerCustomEnumeratedFilterAttribute */
    public IntegerCustomEnumeratedFilterAttribute(String name, Domain parent, Map dataTable, String defaultValueKey, boolean required) {
        super(name, parent, dataTable, defaultValueKey, required);
        criteria = new CriteriaAttributeImpl(this);
    }
    
    public ICriteriaAttribute getCriteriaInterface() {
        return criteria;
    }
    
    protected boolean setValue(Object data) {
        boolean wasModified = false;
        if (isNullOrEmpty(data)) {
            if (!empty) {
                value = 0;
                empty = true;
                wasModified = true;
            }
        } else {
            int newValue = 0;
            if (data instanceof String) {
                newValue = Integer.parseInt((String) data);
            } else if (data instanceof Number) {
                newValue = ((Number) data).intValue();
            }
            if (empty) {
                value = newValue;
                empty = false;
                wasModified = true;
            } else {
                if (newValue != value) {
                    value = newValue;
                    wasModified = true;
                }
            }
        }

        if (wasModified) {
            setSelectedIndex(-1);
            if (!empty) {
                selectedIndexDeferred = true;
            }
        }

        return wasModified;
    }
}
