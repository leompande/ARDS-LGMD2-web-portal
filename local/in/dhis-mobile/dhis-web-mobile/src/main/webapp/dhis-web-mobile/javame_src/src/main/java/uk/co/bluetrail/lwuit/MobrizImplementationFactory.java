package uk.co.bluetrail.lwuit;

import com.sun.lwuit.impl.ImplementationFactory;
import com.sun.lwuit.impl.LWUITImplementation;

public class MobrizImplementationFactory extends ImplementationFactory {

    /**
     * Factory method to create the implementation instance
     * 
     * @return a newly created implementation instance
     */
    public LWUITImplementation createImplementation() {
        return new MobrizMidpImplementation();
    }
}
