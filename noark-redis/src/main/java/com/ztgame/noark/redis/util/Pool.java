package com.ztgame.noark.redis.util;

import org.apache.commons.pool.PoolableObjectFactory;
import org.apache.commons.pool.impl.GenericObjectPool;

import com.ztgame.noark.redis.exception.JedisConnectionException;
import com.ztgame.noark.redis.exception.JedisException;


public abstract class Pool<T> {
    protected GenericObjectPool internalPool;

    protected Pool() {
	this.internalPool = null;
    }
    
    public Pool(final GenericObjectPool.Config poolConfig,
            PoolableObjectFactory factory) {
        this.internalPool = new GenericObjectPool(factory, poolConfig);
    }

    @SuppressWarnings("unchecked")
    public T getResource() {
        try {
            return (T) internalPool.borrowObject();
        } catch (Exception e) {
            throw new JedisConnectionException(
                    "Could not get a resource from the pool", e);
        }
    }
        
    public void returnResourceObject(final Object resource) {
        try {
            internalPool.returnObject(resource);
        } catch (Exception e) {
            throw new JedisException(
                    "Could not return the resource to the pool", e);
        }
    }
    
    public void returnBrokenResource(final T resource) {
    	returnBrokenResourceObject(resource);
    }
    
    public void returnResource(final T resource) {
    	returnResourceObject(resource);
    }

    protected void returnBrokenResourceObject(final Object resource) {
        try {
            internalPool.invalidateObject(resource);
        } catch (Exception e) {
            throw new JedisException(
                    "Could not return the resource to the pool", e);
        }
    }

    public void destroy() {
        try {
            internalPool.close();
        } catch (Exception e) {
            throw new JedisException("Could not destroy the pool", e);
        }
    }
}