package org.hisp.dhis.ws.client;

import org.hisp.dhis.ws.DataSMS;
import org.hisp.dhis.ws.SendDataRequest;
import org.hisp.dhis.ws.SendDataResponse;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;

/**
 * @author THAI
 *
 */
public class DataClientImpl extends WebServiceGatewaySupport implements DataClient{

	public DataSMS sendData(DataSMS data) {
		// TODO Auto-generated method stub
		SendDataRequest request = new SendDataRequest();
		request.setDataSMS(data);
		SendDataResponse response = (SendDataResponse) getWebServiceTemplate().marshalSendAndReceive(request);
		return response.getDataSMS();
	}

}
