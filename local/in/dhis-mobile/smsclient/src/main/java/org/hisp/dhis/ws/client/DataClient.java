package org.hisp.dhis.ws.client;

import org.hisp.dhis.ws.DataSMS;

/**
 * @author THAI
 *
 */
public interface DataClient {
	public DataSMS sendData(DataSMS data);
}
