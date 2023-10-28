import http from "./http-common-producer";

class ProducerService {
//   findAll = async () => {
//     return await http.get('/service-type');
//   }

  markAsResolved = async (request) => {
    // alert(JSON.stringify(request));
    return await http.post('/api/kafka/tombstone', request);
  }

//   delete = async (request) => {
//     return await http.post('/service-type/delete', request);
//   }
}

export default new ProducerService();