/**
 * Create Date:2011-8-24下午03:17:14
 */
package com.bitants.launcherdev.kitset.apache;

/**
 * <p>Provides the highest level of abstraction for Encoders.
 * This is the sister interface of {@link Decoder}.  Every implementation of
 * Encoder provides this common generic interface whic allows a user to pass a 
 * generic Object to any Encoder implementation in the codec package.</p>
 *
 * @version $Id: Encoder.java,v 1.10 2004/02/29 04:08:31 tobrien Exp $
 */
public interface Encoder {

    /**
     * Encodes an "Object" and returns the encoded content 
     * as an Object.  The Objects here may just be <code>byte[]</code>
     * or <code>String</code>s depending on the implementation used.
     *   
     * @param pObject An object ot encode
     * 
     * @return An "encoded" Object
     * 
     * @throws EncoderException an encoder exception is
     *  thrown if the encoder experiences a failure
     *  condition during the encoding process.
     */
    Object encode(Object pObject) throws EncoderException;
}
