package com.thyn.backend.entities;


import java.io.Serializable;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

/**
 * Abstract class from which every storable entities on the datastore need to inherit from.
 *
 * @author Angelo Agatino Nicolosi
 */
@Entity
public abstract class EntityObject implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id @Index
    Long id;

    public Long getId()
    {
        return id;
    }

    public abstract String getName();
    public abstract boolean dispose();
}
