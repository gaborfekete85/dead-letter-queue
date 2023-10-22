import {DLT_QUERIED} from "./actionTypes";

const initialState = {
    list: [    
      {
        "dltKey": "0e585884-b71c-4c04-aabd-a94497a9fc83",
        "eventKey": "61263540-12a0-4fee-9661-01a1f9dc19fc",
        "eventType": "com.feketegabor.streaming.avro.model.ServiceAgreementDataV2",
        "topic": "pcmproser_pcoevents_serviceagreement_v2",
        "partition": 0,
        "partitionOffset": 56,
        "dataAsJson": "{\"Context\": {\"businessRelationshipUid\": \"5cf8429a-61dc-4440-99ab-324b4cbe6cef\", \"partnerUid\": \"1c6fea45-371c-4f12-96c9-05c4a1fcbd70\"}, \"serviceAgreement\": {\"serviceAgreementId\": \"61263540-12a0-4fee-9661-01a1f9dc19fc\", \"serviceAgreementStatus\": \"ACTIVE\", \"serviceId\": \"7b734627-e4c6-4a14-919f-45079cc2f859\", \"serviceName\": \"ServiceName\", \"isFeeAuthentic\": false, \"isCollateralLoans\": true, \"isAuthPledge\": true, \"agreements\": [{\"agreementId\": \"d3a5cbfd-b033-40b6-aa1f-dc522d71ed1a\", \"documentType\": \"CIPPI2\"}, {\"agreementId\": \"f2c9d31c-a3e3-481c-8646-9f8e7712c3f1\", \"documentType\": \"CIPPI\"}]}, \"modifiedAt\": \"2023-10-22T11:31:30.350Z\", \"modifiedBy\": \"u57844\"}",
        "dataAsAvro": "Akg1Y2Y4NDI5YS02MWRjLTQ0NDAtOTlhYi0zMjRiNGNiZTZjZWYCSDFjNmZlYTQ1LTM3MWMtNGYxMi05NmM5LTA1YzRhMWZjYmQ3MAJINjEyNjM1NDAtMTJhMC00ZmVlLTk2NjEtMDFhMWY5ZGMxOWZjLEg3YjczNDYyNy1lNGM2LTRhMTQtOTE5Zi00NTA3OWNjMmY4NTkCFlNlcnZpY2VOYW1lAAEBAgRIZDNhNWNiZmQtYjAzMy00MGI2LWFhMWYtZGM1MjJkNzFlZDFhDENJUFBJMkhmMmM5ZDMxYy1hM2UzLTQ4MWMtODY0Ni05ZjhlNzcxMmMzZjEKQ0lQUEkAAtyevPLqYgIMdTU3ODQ0",
        "reason": "Key Starts with Digit: \njava.lang.RuntimeException: Key Starts with Digit\n\tat com.feketegabor.streaming.EventProcessor.listener.ServiceAgreementListener.serviceAgreementReceived(ServiceAgreementListener.java:79)\n\tat java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke0(Native Method)\n\tat java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)\n\tat java.base/jdk.internal.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)\n\tat java.base/java.lang.reflect.Method.invoke(Method.java:566)\n\tat org.springframework.messaging.handler.invocation.InvocableHandlerMethod.doInvoke(InvocableHandlerMethod.java:169)\n\tat org.springframework.messaging.handler.invocation.InvocableHandlerMethod.invoke(InvocableHandlerMethod.java:119)\n\tat org.springframework.kafka.listener.adapter.HandlerAdapter.invoke(HandlerAdapter.java:56)\n\tat org.springframework.kafka.listener.adapter.MessagingMessageListenerAdapter.invokeHandler(MessagingMessageListenerAdapter.java:347)\n\tat org.springframework.kafka.listener.adapter.RecordMessagingMessageListenerAdapter.onMessage(RecordMessagingMessageListenerAdapter.java:92)\n\tat org.springframework.kafka.listener.adapter.RecordMessagingMessageListenerAdapter.onMessage(RecordMessagingMessageListenerAdapter.java:53)\n\tat org.springframework.kafka.listener.KafkaMessageListenerContainer$ListenerConsumer.doInvokeOnMessage(KafkaMessageListenerContainer.java:2670)\n\tat org.springframework.kafka.listener.KafkaMessageListenerContainer$ListenerConsumer.invokeOnMessage(KafkaMessageListenerContainer.java:2650)\n\tat org.springframework.kafka.listener.KafkaMessageListenerContainer$ListenerConsumer.doInvokeRecordListener(KafkaMessageListenerContainer.java:2577)\n\tat org.springframework.kafka.listener.KafkaMessageListenerContainer$ListenerConsumer.doInvokeWithRecords(KafkaMessageListenerContainer.java:2457)\n\tat org.springframework.kafka.listener.KafkaMessageListenerContainer$ListenerConsumer.invokeRecordListener(KafkaMessageListenerContainer.java:2335)\n\tat org.springframework.kafka.listener.KafkaMessageListenerContainer$ListenerConsumer.invokeListener(KafkaMessageListenerContainer.java:2006)\n\tat org.springframework.kafka.listener.KafkaMessageListenerContainer$ListenerConsumer.invokeIfHaveRecords(KafkaMessageListenerContainer.java:1375)\n\tat org.springframework.kafka.listener.KafkaMessageListenerContainer$ListenerConsumer.pollAndInvoke(KafkaMessageListenerContainer.java:1366)\n\tat org.springframework.kafka.listener.KafkaMessageListenerContainer$ListenerConsumer.run(KafkaMessageListenerContainer.java:1257)\n\tat java.base/java.util.concurrent.Executors$RunnableAdapter.call(Executors.java:515)\n\tat java.base/java.util.concurrent.FutureTask.run(FutureTask.java:264)\n\tat java.base/java.lang.Thread.run(Thread.java:829)\n",
        "createdAt": "2023-10-22T13:31:30.370649Z"
      }
    ],
    taskTitle: "",
    taskDescription: ""
};

const taskReducer = (state = initialState, action) => {
    switch (action.type) {
        case DLT_QUERIED:
            return {
                ...state,
                list: [...state.list, action.payload],
            };
        default:
            return state;
    }
};

export default taskReducer;