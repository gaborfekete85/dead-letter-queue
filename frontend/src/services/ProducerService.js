import http from "./http-common-producer";
import httpDlt from "./http-common-dlt";

class ProducerService {
//   findAll = async () => {
//     return await http.get('/service-type');
//   }

  markAsResolved = async (request) => {
    // alert(JSON.stringify(request));
    return await http.post('/api/kafka/tombstone', request);
  }

  resendEvent = async (request) => {
    // alert("The request in the service is: " + JSON.stringify(request));
    return await httpDlt.post('/api/kafka/retry', request);
  }

//   delete = async (request) => {
//     return await http.post('/service-type/delete', request);
//   }
}

export default new ProducerService();