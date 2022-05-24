package awsUtils;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.InstanceProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

public class AWSUtils {

    static Logger logger = LogManager.getLogger(AWSUtils.class.getName());

    public static AmazonSQS getAmazonSQSClient(String accesskey, String secretkey, Regions regions) {
        AWSCredentials credentials = new BasicAWSCredentials(accesskey, secretkey);
        AmazonSQS amazonSQS = AmazonSQSClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials)).withRegion(regions).build();
        return amazonSQS;
    }
    
    public static AmazonSQS getEC2SpecificSQSClientForGivenRegion(Regions region) {
    	InstanceProfileCredentialsProvider instanceProfileCredentialsProvider = InstanceProfileCredentialsProvider.getInstance();
		AmazonSQS amazonSQS = AmazonSQSClientBuilder.standard().
				withCredentials(new AWSStaticCredentialsProvider(instanceProfileCredentialsProvider.getCredentials()))
				.withRegion(region).build();
        return amazonSQS;
    }

    public static SendMessageResult sendMessageToFifoQueue(AmazonSQS amazonSQS,
                                         Map<String, MessageAttributeValue> messageAttributes,
                                         String queueUrl,
                                         String messageGroupId,
                                         String messageBody,
                                         String unqiueExecutionId){
        SendMessageResult sendMessageResult = null;
        try{
            logger.info(unqiueExecutionId +": Sending message into sqs with message body: "+messageBody);
            SendMessageRequest sendMessageRequest = new SendMessageRequest()
                    .withQueueUrl(queueUrl)
                    .withMessageBody(messageBody)
                    .withMessageGroupId(messageGroupId)
                    .withMessageAttributes(messageAttributes);
            sendMessageResult = amazonSQS.sendMessage(sendMessageRequest);
            logger.info(unqiueExecutionId +": Successfully pushed browserstack related info into sqs with message body: "+messageBody);
        }catch (Exception e){
        	logger.error(unqiueExecutionId + " :Error Occured while sending message to sqs: ",e);
        }
        return sendMessageResult;
    }

    public static ReceiveMessageResult receiveMessageFromFifoQueue(AmazonSQS amazonSQS, String fifoQueueUrl){
        ReceiveMessageResult receiveMessageResult = null;
        try {
            ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(fifoQueueUrl)
                    .withWaitTimeSeconds(10);
            receiveMessageResult = amazonSQS.receiveMessage(receiveMessageRequest);
        }catch (Exception e){
            logger.error("Error Occured while receiving message from sqs: ",e);
        }
        return receiveMessageResult;
    }

    public static DeleteMessageResult deleteMessageFromFifoQueue(AmazonSQS amazonSQS, String fifoQueueUrl, String recieptHandle){
        DeleteMessageResult deleteMessageResult = null;
        try {
            DeleteMessageRequest deleteMessageRequest = new DeleteMessageRequest()
                    .withQueueUrl(fifoQueueUrl)
                    .withReceiptHandle(recieptHandle);
            deleteMessageResult = amazonSQS.deleteMessage(deleteMessageRequest);
        }catch (Exception e){
            logger.error("Error Occured while deleting message from sqs: ",e);
        }
        return deleteMessageResult;
    }
}
