package com.mss.checkin.util;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.simplesystemsmanagement.AWSSimpleSystemsManagementAsync;
import com.amazonaws.services.simplesystemsmanagement.AWSSimpleSystemsManagementAsyncClientBuilder;
import com.amazonaws.services.simplesystemsmanagement.model.GetParameterRequest;
import org.springframework.stereotype.Service;

@Service
public class AWSSSMClient {

    public String getSSMParameterValue(String ssmParameter) {
        String ssmValue="";
        AWSSimpleSystemsManagementAsync client = AWSSimpleSystemsManagementAsyncClientBuilder.defaultClient();
        ssmValue = client.getParameter(new GetParameterRequest().withName(ssmParameter)
                .withWithDecryption(true)).getParameter().getValue();

        System.out.println("ssmValue--"+ssmValue);

        return ssmValue;
    }



}
